package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.commands.annotations.SmtpCommand
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.data.SmtpSessionState
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SessionListener

@SmtpCommand("HELO")
class HeloCommand: SmtpAbstractCommand {
    override fun execute(data: String, smtpSession: SmtpSession, listener: SessionListener): SmtpResponseCode {

        val split = data.split(" ")

        if(split.size != 2)
            return SmtpResponseCode.ARGUMENT_ERROR("Missing hostname")

        smtpSession.senderHostname = split[1].takeWhile { it.isLetterOrDigit() }

        smtpSession.stateSmtp.add(SmtpSessionState.HELO)

        return SmtpResponseCode.OK("")
    }
}