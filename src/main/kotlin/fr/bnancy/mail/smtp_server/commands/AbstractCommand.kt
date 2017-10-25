package fr.bnancy.mail.smtp_server.commands

import fr.bnancy.mail.smtp_server.data.Session
import fr.bnancy.mail.smtp_server.data.SmtpResponseCode
import fr.bnancy.mail.smtp_server.listeners.SessionListener


interface AbstractCommand {
    fun execute(data: String, session: Session, listener: SessionListener): SmtpResponseCode
}