package fr.bnancy.mail

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class MailApplication
fun main(args: Array<String>) {
    SpringApplication.run(MailApplication::class.java, *args)
}

