package fr.bnancy.mail.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("smtp")
class ServerConfig {
    var port: Int = 0
    var sessionTimeout: Int = 0
}