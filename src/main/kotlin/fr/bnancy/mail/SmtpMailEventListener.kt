package fr.bnancy.mail

import fr.bnancy.mail.repository.UserRepository
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.listeners.SmtpSessionListener
import fr.bnancy.mail.service.MailDeliveryService
import fr.bnancy.mail.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class SmtpMailEventListener : SmtpSessionListener {

    @Autowired
    private lateinit var mailDeliveryService: MailDeliveryService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserService

    private val logger = Logger.getLogger(javaClass.simpleName)

    override fun acceptSender(address: String): Boolean {
        return true
    }

    override fun deliverMail(smtpSession: SmtpSession) {
        mailDeliveryService.queueDelivery(smtpSession)
    }

    override fun acceptRecipient(recipientAddress: String, smtpSession: SmtpSession): Boolean {
        if (!smtpSession.authenticated)
            return userRepository.findByMail(recipientAddress) != null
        return true
    }

    override fun sessionOpened(smtpSession: SmtpSession) {
        logger.info(smtpSession.toString())
    }

    override fun sessionClosed(smtpSession: SmtpSession) {
        // println(Mail(session))
    }

    override fun isValidUser(smtpSession: SmtpSession, password: String): Boolean {
        smtpSession.authenticated = userService.isValidUser(smtpSession.loginUsername, password)
        return smtpSession.authenticated
    }
}