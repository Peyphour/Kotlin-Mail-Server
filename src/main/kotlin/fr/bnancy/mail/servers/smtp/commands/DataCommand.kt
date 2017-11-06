package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.commands.annotations.SmtpCommand
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.data.SmtpSessionState
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SessionListener

@SmtpCommand("DATA")
class DataCommand: SmtpAbstractCommand {
    override fun execute(data: String, smtpSession: SmtpSession, listener: SessionListener): SmtpResponseCode {
        if(!smtpSession.stateSmtp.contains(SmtpSessionState.RECIPIENT_ADDED))
            return SmtpResponseCode.BAD_SEQUENCE("Must issue RCPT first.")

        if(!smtpSession.receivingData) {
            smtpSession.receivingData = true
            return SmtpResponseCode.DATA("End data with <CR><LF>.<CR><LF>")
        } else {
            smtpSession.content += (data + "\r\n")
            if(smtpSession.content.endsWith("\r\n.\r\n")) {
                smtpSession.content = smtpSession.content.dropLast(5) // remove CRLF.CRLF
                smtpSession.receivingData = false
                smtpSession.stateSmtp.add(SmtpSessionState.DATA)
                return SmtpResponseCode.OK("OK will deliver.")
            }
        }
        return SmtpResponseCode.EMPTY() // return nothing, data reception is not finished yet
    }
}