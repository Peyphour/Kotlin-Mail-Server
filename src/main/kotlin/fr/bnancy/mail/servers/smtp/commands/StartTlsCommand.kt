package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.commands.annotations.SmtpCommand
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.data.SmtpSessionState
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SessionListener

@SmtpCommand("STARTTLS", arrayOf("smtp"))
class StartTlsCommand : SmtpAbstractCommand {
    override fun execute(data: String, smtpSession: SmtpSession, listener: SessionListener): SmtpResponseCode {
        if(smtpSession.stateSmtp.contains(SmtpSessionState.TLS_STARTED))
            return SmtpResponseCode.NOT_AVAILABLE("TLS already enabled")


        smtpSession.stateSmtp.add(SmtpSessionState.TLS_STARTED)

        return SmtpResponseCode.HELO("Ready to start TLS")
    }

}