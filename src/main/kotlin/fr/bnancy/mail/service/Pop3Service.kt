package fr.bnancy.mail.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.bnancy.mail.repository.MailRepository
import fr.bnancy.mail.servers.smtp.data.Header
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

    fun messageExistsForUser(user: String, id: Long): Boolean {
        val mail = mailRepository.findOne(id)

        return when(mail) {
            null -> false
            else -> mail.recipients.contains(user)
        }
    }

    fun getFullMailContent(id: Long): String {
        val mail = mailRepository.findOne(id)
        val headers: Array<Header> = jacksonObjectMapper().readValue(mail.headers)
        var content = ""
        headers.forEach { content += it.key + ":" + it.value + "\r\n"}
        content += mail.content

        return content
    }

    fun getMailBoxStatistics(user: String) : String {
        var totalMail = 0
        var totalSize = 0
        mailRepository.findBy()
                .filter { it.getRecipients().contains(user) }
                .also { totalMail = it.size }
                .forEach { totalSize += getFullMailContent(it.getId()).length }
        return "$totalMail $totalSize"
    }

    fun getAllStatistics(user: String): String {
        var response = ""
        mailRepository.findBy()
                .filter { it.getRecipients().contains(user) }
                .also { response += it.size.toString(10) + " messages:\r\n"}
                .map { mailRepository.findOne(it.getId()) }
                .forEach {response += it.id.toString(10) + " " + getFullMailContent(it.id).length + "\r\n"}
        response += "."
        return response
    }

    fun deleteMails(messageToDelete: MutableList<Long>) {
        messageToDelete.forEach({mailRepository.delete(it)})
    }

    fun getUidl(user: String): String {
        var response = ""
        mailRepository.findBy()
                .filter { it.getRecipients().contains(user) }
                .forEach { response += it.getId().toString(10) + " " + it.getId().toString(10) + "\r\n"}
        response += "."
        return response
    }
}