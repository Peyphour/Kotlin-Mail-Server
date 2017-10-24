package fr.bnancy.mail.smtp_server.commands

import fr.bnancy.mail.smtp_server.commands.annotations.Command
import fr.bnancy.mail.smtp_server.data.Session
import fr.bnancy.mail.smtp_server.data.SmtpResponseCode
import fr.bnancy.mail.smtp_server.listeners.SessionListener

@Command("RSET")
class RsetCommand: AbstractCommand {
    override fun execute(data: String, session: Session, listener: SessionListener): SmtpResponseCode {

        session.state = ArrayList()
        session.loginState = ArrayList()
        session.to = ArrayList()
        session.from = ""
        session.content = ""
        session.receivingData = false

        return SmtpResponseCode.OK("OK")
    }
}