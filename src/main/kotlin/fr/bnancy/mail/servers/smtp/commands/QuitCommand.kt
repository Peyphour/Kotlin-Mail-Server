package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.commands.annotations.SmtpCommand
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.data.SmtpSessionState
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SessionListener

@SmtpCommand("QUIT")
class QuitCommand: SmtpAbstractCommand {
    override fun execute(data: String, smtpSession: SmtpSession, listener: SessionListener): SmtpResponseCode {
        smtpSession.stateSmtp.add(SmtpSessionState.QUIT)
        return SmtpResponseCode.QUIT("OK")
    }
}