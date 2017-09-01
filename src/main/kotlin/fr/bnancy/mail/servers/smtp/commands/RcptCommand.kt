package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.commands.annotations.Command
import fr.bnancy.mail.servers.smtp.data.Session
import fr.bnancy.mail.servers.smtp.data.SessionState
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SessionListener

@Command("RCPT")
class RcptCommand: AbstractCommand {
    override fun execute(data: String, session: Session, listener: SessionListener): SmtpResponseCode {
        if(!session.state.contains(SessionState.MAIL))
            return SmtpResponseCode.BAD_SEQUENCE("Must issue MAIL first")

        val mailRegex = Regex("<(.*)>")

        val address = mailRegex.find(data)!!.groupValues[1]

        if(!listener.acceptRecipient(address))
            return SmtpResponseCode.MAILBOX_UNAVAILABLE("<$address> does not exists here")

        session.to.add(address)

        session.state.add(SessionState.RECIPIENT_ADDED)

        return SmtpResponseCode.OK("OK")
    }

}