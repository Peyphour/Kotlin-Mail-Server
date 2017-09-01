package fr.bnancy.mail

import fr.bnancy.mail.repository.MailRepository
import fr.bnancy.mail.repository.UserRepository
import fr.bnancy.mail.servers.smtp.data.Mail
import fr.bnancy.mail.servers.smtp.data.Session
import fr.bnancy.mail.servers.smtp.listeners.SessionListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MailEventListener: SessionListener {

    @Autowired
    private lateinit var mailRepository: MailRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun acceptSender(address: String): Boolean {
        return true
    }

    override fun deliverMail(session: Session) {
        mailRepository.save(Mail(session).toEntity())
    }

    override fun acceptRecipient(recipientAddress: String): Boolean {
        return userRepository.findByMail(recipientAddress) != null
    }

    override fun sessionOpened(session: Session) {
        println(session)
    }

    override fun sessionClosed(session: Session) {
        // println(Mail(session))
    }
}