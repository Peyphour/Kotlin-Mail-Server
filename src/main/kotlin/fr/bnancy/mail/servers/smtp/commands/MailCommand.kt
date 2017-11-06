package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.commands.annotations.SmtpCommand
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.data.SmtpSessionState
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SessionListener

@SmtpCommand("MAIL")
class MailCommand: SmtpAbstractCommand {
    override fun execute(data: String, smtpSession: SmtpSession, listener: SessionListener): SmtpResponseCode {

        if(!smtpSession.stateSmtp.contains(SmtpSessionState.HELO))
            return SmtpResponseCode.BAD_SEQUENCE("Must issue HELO/EHLO first.")
        if(smtpSession.stateSmtp.contains(SmtpSessionState.MAIL))
            return SmtpResponseCode.BAD_SEQUENCE("Transaction in progress already.")

        // Clean buffers as specified by RFC
        smtpSession.to = ArrayList()
        smtpSession.content = ""

        val mailRegex = Regex("<(.*)>")
        val address = mailRegex.find(data)!!.groupValues[1]

        if(!listener.acceptSender(address))
            return SmtpResponseCode.MAILBOX_UNAVAILABLE("$address isn't authorized to send mail here")

        smtpSession.from = address

        smtpSession.stateSmtp.add(SmtpSessionState.MAIL)

        return SmtpResponseCode.OK("OK")
    }
}