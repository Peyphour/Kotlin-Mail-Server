package fr.bnancy.mail

import fr.bnancy.mail.repository.MailRepository
import fr.bnancy.mail.smtp_server.data.Mail
import fr.bnancy.mail.smtp_server.data.Session
import fr.bnancy.mail.smtp_server.listeners.SessionListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MailEventListener: SessionListener {

    @Autowired
    private lateinit var mailRepository: MailRepository

    override fun acceptSender(address: String): Boolean {
        return true
    }

    override fun deliverMail(session: Session) {
        mailRepository.save(Mail(session))
    }

    override fun acceptRecipient(recipientAddress: String): Boolean {
        return true
    }

    override fun sessionOpened(session: Session) {
        println(session)
    }

    override fun sessionClosed(session: Session) {
        // println(Mail(session))
    }
}