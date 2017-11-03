package fr.bnancy.mail

import fr.bnancy.mail.repository.UserRepository
import fr.bnancy.mail.servers.smtp.data.Session
import fr.bnancy.mail.servers.smtp.data.SessionState
import fr.bnancy.mail.servers.smtp.listeners.SessionListener
import fr.bnancy.mail.service.IpBlacklistService
import fr.bnancy.mail.service.MailDeliveryService
import fr.bnancy.mail.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class MailEventListener: SessionListener {

    @Autowired
    private lateinit var mailDeliveryService: MailDeliveryService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var ipBlacklistService: IpBlacklistService

    @Autowired
    private lateinit var userService: UserService

    private val logger = Logger.getLogger(javaClass.simpleName)

    override fun acceptSender(address: String): Boolean {
        return true
    }

    override fun deliverMail(session: Session) {
        mailDeliveryService.queueDelivery(session)
    }

    override fun acceptRecipient(recipientAddress: String, session: Session): Boolean {
        if(!session.authenticated)
            return userRepository.findByMail(recipientAddress) != null
        return true
    }

    override fun sessionOpened(session: Session) {
        logger.info(session.toString())
        if(ipBlacklistService.blacklistedIp(session.netAddress))
            session.state.add(SessionState.QUIT)
    }

    override fun sessionClosed(session: Session) {
        // println(Mail(session))
    }

    override fun isValidUser(session: Session, password: String): Boolean {
        session.authenticated = userService.isValidUser(session.loginUsername, password)
        return session.authenticated
    }
}