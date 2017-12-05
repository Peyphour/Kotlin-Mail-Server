package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.commands.annotations.SmtpCommand
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.data.SmtpSessionState
import fr.bnancy.mail.servers.smtp.listeners.SmtpSessionListener

@SmtpCommand("RSET")
class RsetCommand: SmtpAbstractCommand {
    override fun execute(data: String, smtpSession: SmtpSession, smtpListener: SmtpSessionListener): SmtpResponseCode {

        smtpSession.stateSmtp = ArrayList()
        smtpSession.loginState = ArrayList()
        smtpSession.to = ArrayList()
        smtpSession.from = ""
        smtpSession.content = ""
        smtpSession.receivingData = false

        smtpSession.stateSmtp.add(SmtpSessionState.HELO);

        return SmtpResponseCode.OK("OK")
    }
}