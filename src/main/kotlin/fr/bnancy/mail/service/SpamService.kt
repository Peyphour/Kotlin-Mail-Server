package fr.bnancy.mail.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.bnancy.mail.config.SpamConfig
import fr.bnancy.mail.data.Mail
import fr.bnancy.mail.servers.smtp.data.Header
import hr.sdautovic.spamd.client.SpamdClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class SpamService {

    @Autowired
    lateinit var config: SpamConfig

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun inspectMail(mail: Mail) {

        if (!config.enabled)
            return

        val headers: Array<Header> = jacksonObjectMapper().readValue(mail.headers)
        var fullMail = ""

        headers.forEach { fullMail += it.key + ":" + it.value + "\r\n" }
        fullMail += mail.content

        val client = SpamdClient(config.spamAssassinUrl, config.spamAssassinPort,
                SpamdClient.ACTION.PROCESS, fullMail.toByteArray(StandardCharsets.UTF_8), true)

        logger.info("SpamAssassin response is {}", client.response.isSpam)

        mail.spam = client.response.isSpam
        mail.spamData = "spam=" + client.response.isSpam + ", response=" + client.response.spamdResponse()
                .lines()
                .filter { !fullMail.contains(it) } + ",score=" + client.response.score()
    }
}