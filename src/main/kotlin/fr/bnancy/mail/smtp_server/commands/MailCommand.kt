package fr.bnancy.mail.smtp_server.commands

import fr.bnancy.mail.smtp_server.commands.annotations.Command
import fr.bnancy.mail.smtp_server.data.Session
import fr.bnancy.mail.smtp_server.data.SessionState
import fr.bnancy.mail.smtp_server.data.SmtpResponseCode
import fr.bnancy.mail.smtp_server.listeners.SessionListener

@Command("MAIL")
class MailCommand: AbstractCommand {
    override fun execute(data: String, session: Session, listener: SessionListener): SmtpResponseCode {

        if(!session.state.contains(SessionState.HELO))
            return SmtpResponseCode.BAD_SEQUENCE("Must issue HELO/EHLO first.")
        if(session.state.contains(SessionState.MAIL))
            return SmtpResponseCode.BAD_SEQUENCE("Transaction in progress already.")

        // Clean buffers as specified by RFC
        session.to = ArrayList()
        session.content = ""

        val mailRegex = Regex("<(.*)>")
        val address = mailRegex.find(data)!!.groupValues[1]

        session.from = address

        session.state.add(SessionState.MAIL)

        return SmtpResponseCode.OK("OK")
    }
}