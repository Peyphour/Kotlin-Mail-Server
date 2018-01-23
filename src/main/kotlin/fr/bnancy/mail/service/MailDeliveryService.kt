package fr.bnancy.mail.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.bnancy.mail.repository.MailRepository
import fr.bnancy.mail.repository.UserRepository
import fr.bnancy.mail.servers.smtp.data.Header
import fr.bnancy.mail.servers.smtp.data.Mail
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import org.simplejavamail.email.Email
import org.simplejavamail.mailer.Mailer
import org.simplejavamail.mailer.config.ServerConfig
import org.simplejavamail.mailer.config.TransportStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.xbill.DNS.Lookup
import org.xbill.DNS.MXRecord
import org.xbill.DNS.Type
import java.util.*
import java.util.logging.Logger
import javax.mail.Message

@Service
class MailDeliveryService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var mailRepository: MailRepository

    private val externalDeliveryQueue: LinkedList<SmtpSession> = LinkedList()
    private val internalDeliveryQueue: LinkedList<SmtpSession> = LinkedList()
    private val tryLaterDeliveryQueue: LinkedList<Pair<SmtpSession, Long>> = LinkedList()

    private val FIVE_MINUTE_MILLIS = 1000 * 60 * 5

    private val logger = Logger.getLogger(javaClass.simpleName)

    fun queueDelivery(smtpSession: SmtpSession) {
        logger.info("queuing delivery $smtpSession")
        for (recipient in smtpSession.to) {
            if(userRepository.findByMail(recipient) == null) {
                externalDeliveryQueue.add(smtpSession.copy(to = arrayListOf(recipient)))
            } else {
                internalDeliveryQueue.add(smtpSession.copy(to = arrayListOf(recipient)))
            }
        }
    }

    @Scheduled(fixedDelay = 100, initialDelay = 1000)
    fun deliverInternalMail() {
        val session = internalDeliveryQueue.poll() ?: return
        logger.info("Delivering internal mail $session")
        mailRepository.save(Mail(session).toEntity())
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    fun deliverExternalMail() {
        val session = externalDeliveryQueue.poll() ?: return
        logger.info("Delivering external mail $session")
        sendMail(session).forEach { // Add each session in error to later delivery queue
            tryLaterDeliveryQueue.add(it to System.currentTimeMillis() + FIVE_MINUTE_MILLIS)
        }
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    fun tryDeliverAgain() {
        val found = mutableListOf<Pair<SmtpSession, Long>>()
        val sessionInError = mutableListOf<SmtpSession>()
        for((session, time) in tryLaterDeliveryQueue) {
            if(time <= System.currentTimeMillis()) {
                logger.info("New delivery for $session (time : $time)")
                sessionInError.addAll(sendMail(session))
                found.add(session to time)
            }
        }
        tryLaterDeliveryQueue.removeAll(found)
        sessionInError.forEach {
            tryLaterDeliveryQueue.add(it to System.currentTimeMillis() + FIVE_MINUTE_MILLIS)
        }
    }

    private fun doMxLookup(domain: String): String {
        val lookups = Lookup(domain, Type.MX).run()
        lookups.sortBy { it -> (it as MXRecord).priority }
        return (lookups[0] as MXRecord).target.toString(true)
    }
    /**
     * Send an email
     * @param session the session to send
     * @return a list of SmtpSession in error to attempt later delivery
     */
    private fun sendMail(session: SmtpSession): List<SmtpSession> {
        val mail = Mail(session).toEntity()
        val sessionInError = mutableListOf<SmtpSession>()
        for(recipient in mail.recipients) {
            val email = Email()
            email.setFromAddress(mail.sender, mail.sender)
            email.setReplyToAddress(mail.sender, mail.sender)
            email.addRecipient(recipient, recipient, Message.RecipientType.TO)
            val headers: Array<Header> = jacksonObjectMapper().readValue(mail.headers)

            headers.filter { !it.key.equals("From", true) }
                    .filter { !it.key.equals("Subject", true) }
                    .filter { !it.key.equals("To", true) }
                    .forEach { email.addHeader(it.key, it.value) }

            email.subject = headers.find { it.key.equals("Subject", true) }!!.value

            val contentTypeHeader: Header = headers.find { it.key.equals("Content-Type", true) }!!

            if(contentTypeHeader.value.contains("text/plain", true)) {
                email.text = mail.content
            } else {
                email.textHTML = mail.content
            }

            try {
                Mailer(
                        ServerConfig(doMxLookup(recipient.split("@")[1]), 25),
                        TransportStrategy.SMTP_TLS
                ).sendMail(email)
            } catch (e: Exception) {
                logger.info("got exception ${e.message} will sending to ${session.to}")
                sessionInError.add(session.copy(to = arrayListOf(recipient))) // Copy to the recipient in error
            }
        }
        return sessionInError
    }
}