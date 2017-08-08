package fr.bnancy.mail.smtp_server.commands

import fr.bnancy.mail.smtp_server.commands.annotations.Command
import fr.bnancy.mail.smtp_server.data.Session
import fr.bnancy.mail.smtp_server.data.SessionState
import fr.bnancy.mail.smtp_server.data.SmtpResponseCode
import fr.bnancy.mail.smtp_server.listeners.SessionListener

@Command("RCPT")
class RcptCommand: AbstractCommand {
    override fun execute(data: String, session: Session, listener: SessionListener): SmtpResponseCode {
        if(!session.state.contains(SessionState.MAIL))
            return SmtpResponseCode.BAD_SEQUENCE("Must issue MAIL first")

        val mailRegex = Regex("<(.*)>")

        val address = mailRegex.find(data)!!.groupValues[1]

        session.to.add(address)

        session.state.add(SessionState.RECIPIENT_ADDED)

        return SmtpResponseCode.OK("OK")
    }

}