package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.commands.annotations.SmtpCommand
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SessionListener

@SmtpCommand("RSET")
class RsetCommand: SmtpAbstractCommand {
    override fun execute(data: String, smtpSession: SmtpSession, listener: SessionListener): SmtpResponseCode {

        smtpSession.stateSmtp = ArrayList()
        smtpSession.loginState = ArrayList()
        smtpSession.to = ArrayList()
        smtpSession.from = ""
        smtpSession.content = ""
        smtpSession.receivingData = false

        return SmtpResponseCode.OK("OK")
    }
}