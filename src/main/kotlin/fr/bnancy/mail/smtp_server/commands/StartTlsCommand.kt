package fr.bnancy.mail.smtp_server.commands

import fr.bnancy.mail.smtp_server.commands.annotations.Command
import fr.bnancy.mail.smtp_server.data.Session
import fr.bnancy.mail.smtp_server.data.SessionState
import fr.bnancy.mail.smtp_server.data.SmtpResponseCode
import fr.bnancy.mail.smtp_server.listeners.SessionListener

@Command("STARTTLS")
class StartTlsCommand : AbstractCommand {
    override fun execute(data: String, session: Session, listener: SessionListener): SmtpResponseCode {
        if(session.state.contains(SessionState.TLS_STARTED))
            return SmtpResponseCode.NOT_AVAILABLE("TLS already enabled")


        session.state.add(SessionState.TLS_STARTED)

        return SmtpResponseCode.HELO("Ready to start TLS")
    }

}