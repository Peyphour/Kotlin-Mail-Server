package fr.bnancy.mail.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("smtp")
class SmtpServerConfig {
    var port: Int = 25
    var sessionTimeout: Int = 60000
}