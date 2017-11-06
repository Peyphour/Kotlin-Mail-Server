package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SessionListener

interface SmtpAbstractCommand {
    fun execute(data: String, smtpSession: SmtpSession, listener: SessionListener): SmtpResponseCode
}