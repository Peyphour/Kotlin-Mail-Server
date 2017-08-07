package fr.bnancy.mail.repository

import fr.bnancy.mail.smtp_server.data.Mail
import org.springframework.data.repository.CrudRepository

interface MailRepository: CrudRepository<Mail, Long>