package fr.bnancy.mail.service

import fr.bnancy.mail.repository.MailRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Pop3Service {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var mailRepository: MailRepository

    fun authenticateUser(user: String, pass: String): Boolean {
        return userService.isValidUser(user, pass)
    }

    fun getAllStatistics(user: String): String {
        return ""
    }

    fun deleteMails(messageToDelete: MutableList<Int>) {
        messageToDelete.forEach({mailRepository.delete(it.toLong())})
    }
}