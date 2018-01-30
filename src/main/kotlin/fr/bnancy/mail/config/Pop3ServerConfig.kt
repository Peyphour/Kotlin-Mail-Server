package fr.bnancy.mail.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("pop3")
class Pop3ServerConfig {
    var port: Int = 995
    var sessionTimeout: Int = 6000
}