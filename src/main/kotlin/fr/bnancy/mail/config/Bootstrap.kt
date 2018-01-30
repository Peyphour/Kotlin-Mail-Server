package fr.bnancy.mail.config

import fr.bnancy.mail.getHostIp
import fr.bnancy.mail.getHostname
import fr.bnancy.mail.getLocalIp
import fr.bnancy.mail.reverseDns
import fr.bnancy.mail.servers.pop3.Pop3Server
import fr.bnancy.mail.servers.smtp.SmtpServer
import fr.bnancy.mail.servers.smtp.SubmissionServer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class Bootstrap {

    @Value("\${servers.autostart: false}")
    private var autoStartServers: Boolean = false

    @Autowired
    private lateinit var smtpServer: SmtpServer

    @Autowired
    private lateinit var pop3Server: Pop3Server

    @Autowired
    private lateinit var submissionServer: SubmissionServer

    private final val logger = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    fun bootstrap() {
        val keystorePath = System.getProperty("javax.net.ssl.keyStore") != null
        if (autoStartServers && keystorePath) {
            submissionServer.start()
            pop3Server.start()
            smtpServer.start()
        }

        // Check keystore
        if (!keystorePath) {
            logger.error("No Java keyStore specified, POP3 and Submission servers will not work!")
        }

        val ip = getLocalIp()
        val hostname = getHostname()
        val hostnameIp = getHostIp(hostname)
        val reverseHostname = reverseDns(ip).removeSuffix(".")

        if (reverseHostname != hostname) {
            logger.error("Reverse DNS lookup for {} does not resolve to {} but to {}", ip, hostname, reverseHostname)
        } else {
            logger.info("Reverse DNS lookup for {} resolve to {}, all good !", ip, hostname)
        }

        if (hostnameIp != ip) {
            logger.error("DNS lookup for domain {} returns {} instead of detected IP {}", hostname, hostnameIp, ip)
        } else {
            logger.info("DNS lookup for domain {} returns previously detected IP {}, all good !", hostname, hostnameIp)
        }
    }
}