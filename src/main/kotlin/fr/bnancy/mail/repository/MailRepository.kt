package fr.bnancy.mail.repository

import fr.bnancy.mail.data.Mail
import fr.bnancy.mail.data.MailWithoutContent
import org.springframework.data.jpa.repository.Query

import org.springframework.data.repository.CrudRepository

interface MailRepository: CrudRepository<Mail, Long> {
    @Query("SELECT s.headers as headers, s.recipients as recipients , s.id as id from Mail s")
    fun findBy(): Iterable<MailWithoutContent>
}
