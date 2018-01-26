package fr.bnancy.mail.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(SubmissionServerConfig::class, SmtpServerConfig::class, Pop3ServerConfig::class, SpamConfig::class)
class Configuration