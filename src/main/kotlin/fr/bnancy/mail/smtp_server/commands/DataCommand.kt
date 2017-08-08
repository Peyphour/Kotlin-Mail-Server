package fr.bnancy.mail.smtp_server.commands

import fr.bnancy.mail.smtp_server.commands.annotations.Command
import fr.bnancy.mail.smtp_server.data.Session
import fr.bnancy.mail.smtp_server.data.SessionState
import fr.bnancy.mail.smtp_server.data.SmtpResponseCode
import fr.bnancy.mail.smtp_server.listeners.SessionListener

@Command("DATA")
class DataCommand: AbstractCommand {
    override fun execute(data: String, session: Session, listener: SessionListener): SmtpResponseCode {
        if(!session.state.contains(SessionState.RECIPIENT_ADDED))
            return SmtpResponseCode.BAD_SEQUENCE("Must issue RCPT first.")

        if(!session.receivingData) {
            session.receivingData = true
            return SmtpResponseCode.DATA("End data with <CR><LF>.<CR><LF>")
        } else {
            session.content += data
            if(session.content.endsWith("\r\n.\r\n")) {
                session.content = session.content.dropLast(5) // remove CRLF.CRLF
                session.receivingData = false
                session.state.add(SessionState.DATA)
                return SmtpResponseCode.OK("OK will deliver.")
            }
        }
        return SmtpResponseCode.UNKNOWN() // return nothing, data reception is not finished yet
    }
}