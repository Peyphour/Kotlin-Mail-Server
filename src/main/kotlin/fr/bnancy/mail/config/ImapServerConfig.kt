package fr.bnancy.mail.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("imap")
class ImapServerConfig {
    var port: Int = 993
    var sessionTimeout = 60000
}