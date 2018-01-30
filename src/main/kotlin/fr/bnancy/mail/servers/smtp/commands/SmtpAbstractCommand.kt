package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.listeners.SmtpSessionListener

interface SmtpAbstractCommand {
    fun execute(data: String, smtpSession: SmtpSession, smtpListener: SmtpSessionListener): SmtpResponseCode
}