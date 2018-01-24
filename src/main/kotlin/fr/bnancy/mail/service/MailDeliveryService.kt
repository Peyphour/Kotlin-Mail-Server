package fr.bnancy.mail.service

import fr.bnancy.mail.repository.MailRepository
import fr.bnancy.mail.repository.UserRepository
import fr.bnancy.mail.sender.MailSender
import fr.bnancy.mail.servers.smtp.data.Mail
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import org.simplejavamail.mailer.config.ServerConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.xbill.DNS.Lookup
import org.xbill.DNS.MXRecord
import org.xbill.DNS.Type
import java.util.*

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

    private val logger = LoggerFactory.getLogger(javaClass.simpleName)

    fun queueDelivery(smtpSession: SmtpSession) {
        logger.debug("queuing delivery $smtpSession")
        for (recipient in smtpSession.to) {
            if (userRepository.findByMail(recipient) == null) {
                externalDeliveryQueue.add(smtpSession.copy(to = arrayListOf(recipient)))
            } else {
                internalDeliveryQueue.add(smtpSession.copy(to = arrayListOf(recipient)))
            }
        }
    }

    @Scheduled(fixedDelay = 100, initialDelay = 1000)
    fun deliverInternalMail() {
        val session = internalDeliveryQueue.poll() ?: return
        logger.debug("Delivering internal mail $session")
        mailRepository.save(Mail(session).toEntity())
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    fun deliverExternalMail() {
        val session = externalDeliveryQueue.poll() ?: return
        logger.debug("Delivering external mail $session")
        sendMail(session).forEach {
            // Add each session in error to later delivery queue
            tryLaterDeliveryQueue.add(it to System.currentTimeMillis() + FIVE_MINUTE_MILLIS)
        }
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    fun tryDeliverAgain() {
        val found = mutableListOf<Pair<SmtpSession, Long>>()
        val sessionInError = mutableListOf<SmtpSession>()
        for ((session, time) in tryLaterDeliveryQueue) {
            if (time <= System.currentTimeMillis()) {
                logger.debug("New delivery for $session (time : $time)")
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
        for (recipient in mail.recipients) {
            try {
                MailSender(
                        mail.copy(recipients = arrayListOf(recipient)),
                        ServerConfig(doMxLookup(recipient.split("@")[1]), 25)
                ).send()
            } catch (e: Exception) {
                logger.error("got exception ${e.message} will sending to ${session.to}")
                sessionInError.add(session.copy(to = arrayListOf(recipient))) // Copy to the recipient in error
            }
        }
        return sessionInError
    }
}