package fr.bnancy.mail.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.bnancy.mail.repository.MailRepository
import fr.bnancy.mail.repository.UserRepository
import fr.bnancy.mail.servers.smtp.data.Header
import fr.bnancy.mail.servers.smtp.data.Mail
import fr.bnancy.mail.servers.smtp.data.Session
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
import javax.mail.Message

@Service
class MailDeliveryService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var mailRepository: MailRepository

    private val externalDeliveryQueue: LinkedList<Session> = LinkedList()

    fun queueDelivery(session: Session) {
        for (recipient in session.to) {
            if(userRepository.findByMail(recipient) == null) {
                externalDeliveryQueue.add(session)
            } else {
                mailRepository.save(Mail(session).toEntity())
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    fun deliverExternalMail() {
        val session = externalDeliveryQueue.poll() ?: return

        val mail = Mail(session).toEntity()

        val email = Email()
        email.setFromAddress(mail.sender, mail.sender)
        email.setReplyToAddress(mail.sender, mail.sender)
        for(recipient in mail.recipients) {
            email.addRecipient(recipient, recipient, Message.RecipientType.TO)
            val headers: Array<Header> = jacksonObjectMapper().readValue(mail.headers)

            email.subject = headers.find { it.key == "Subject" }!!.value
            email.textHTML = mail.content

            Mailer(
                    ServerConfig(doMxLookup(recipient.split("@")[1]), 25),
                    TransportStrategy.SMTP_TLS
            ).sendMail(email)
        }
    }

    fun doMxLookup(domain: String): String {
        val lookups = Lookup(domain, Type.MX).run()
        lookups.sortBy { it -> (it as MXRecord).priority }
        return (lookups[0] as MXRecord).target.toString(true)
    }
}