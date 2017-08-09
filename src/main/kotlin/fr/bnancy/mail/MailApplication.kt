package fr.bnancy.mail

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity


@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
class MailApplication
fun main(args: Array<String>) {
    SpringApplication.run(MailApplication::class.java, *args)
}

