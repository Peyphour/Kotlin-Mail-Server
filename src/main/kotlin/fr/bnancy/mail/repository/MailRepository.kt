package fr.bnancy.mail.repository

import fr.bnancy.mail.smtp_server.data.entities.Mail
import org.springframework.data.repository.CrudRepository

interface MailRepository: CrudRepository<Mail, Long>