package fr.bnancy.mail

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
class MailApplication
fun main(args: Array<String>) {
    SpringApplication.run(MailApplication::class.java, *args)
}

