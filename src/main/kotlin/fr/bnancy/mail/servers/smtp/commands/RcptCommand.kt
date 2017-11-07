package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.commands.annotations.SmtpCommand
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.data.SmtpSessionState
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SmtpSessionListener

@SmtpCommand("RCPT")
class RcptCommand: SmtpAbstractCommand {
    override fun execute(data: String, smtpSession: SmtpSession, smtpListener: SmtpSessionListener): SmtpResponseCode {
        if(!smtpSession.stateSmtp.contains(SmtpSessionState.MAIL))
            return SmtpResponseCode.BAD_SEQUENCE("Must issue MAIL first")

        val mailRegex = Regex("<(.*)>")

        val address = mailRegex.find(data)!!.groupValues[1]

        if(!smtpListener.acceptRecipient(address, smtpSession))
            return SmtpResponseCode.MAILBOX_UNAVAILABLE("<$address> does not exists here")

        smtpSession.to.add(address)

        smtpSession.stateSmtp.add(SmtpSessionState.RECIPIENT_ADDED)

        return SmtpResponseCode.OK("OK")
    }

}