package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.data.Session
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SessionListener

interface AbstractCommand {
    fun execute(data: String, session: Session, listener: SessionListener): SmtpResponseCode
}