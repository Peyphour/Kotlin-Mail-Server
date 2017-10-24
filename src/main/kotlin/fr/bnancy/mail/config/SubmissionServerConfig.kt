package fr.bnancy.mail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("submission")
class SubmissionServerConfig {
    var port: Int = 587
    var sessionTimeout: Int = 60000
}
