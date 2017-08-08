package fr.bnancy.mail.smtp_server.commands

import fr.bnancy.mail.smtp_server.commands.annotations.Command
import fr.bnancy.mail.smtp_server.data.Session
import fr.bnancy.mail.smtp_server.data.SessionState
import fr.bnancy.mail.smtp_server.data.SmtpResponseCode
import fr.bnancy.mail.smtp_server.listeners.SessionListener

@Command("QUIT")
class QuitCommand: AbstractCommand {
    override fun execute(data: String, session: Session, listener: SessionListener): SmtpResponseCode {
        session.state.add(SessionState.QUIT)
        return SmtpResponseCode.QUIT("OK")
    }
}