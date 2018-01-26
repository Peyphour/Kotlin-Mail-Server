package fr.bnancy.mail.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spam")
class SpamConfig {
    var enabled: Boolean = false
    var spamAssassinUrl: String = "127.0.0.1"
    var spamAssassinPort: Int = 783
}