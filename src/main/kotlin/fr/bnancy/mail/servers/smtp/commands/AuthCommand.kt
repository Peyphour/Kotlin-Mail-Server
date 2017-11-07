package fr.bnancy.mail.servers.smtp.commands

import fr.bnancy.mail.servers.smtp.commands.annotations.SmtpCommand
import fr.bnancy.mail.servers.smtp.data.LoginState
import fr.bnancy.mail.servers.smtp.data.SmtpSession
import fr.bnancy.mail.servers.smtp.data.SmtpSessionState
import fr.bnancy.mail.servers.smtp.data.SmtpResponseCode
import fr.bnancy.mail.servers.smtp.listeners.SmtpSessionListener
import java.nio.charset.Charset
import java.util.*

@SmtpCommand("AUTH", arrayOf("submission"))
class AuthCommand : SmtpAbstractCommand {
    override fun execute(data: String, smtpSession: SmtpSession, smtpListener: SmtpSessionListener): SmtpResponseCode {
        if(!smtpSession.secured)
            return SmtpResponseCode.BAD_SEQUENCE("Must issue STARTTLS first")

        if(!smtpSession.stateSmtp.contains(SmtpSessionState.HELO))
            return SmtpResponseCode.BAD_SEQUENCE("Must issue HELO/EHLO first.")

        if(!smtpSession.loginState.contains(LoginState.LOGIN_IN_PROGRESS))
            smtpSession.loginState.add(LoginState.LOGIN_IN_PROGRESS)

        if(!smtpSession.loginState.contains(LoginState.USERNAME_TRANSMITTED)
                && !smtpSession.loginState.contains(LoginState.PASSWORD_TRANSMITTED)
                && smtpSession.loginUsername.isEmpty()) {
            smtpSession.loginState.add(LoginState.USERNAME_TRANSMITTED)
            return SmtpResponseCode.AUTH_GO_ON("VXNlcm5hbWU6")
        } else if(smtpSession.loginState.contains(LoginState.USERNAME_TRANSMITTED)
                && !smtpSession.loginState.contains(LoginState.PASSWORD_TRANSMITTED)
                && smtpSession.loginUsername.isEmpty()) {
            smtpSession.loginUsername = Base64.getDecoder().decode(data).toString(Charset.forName("UTF-8"))
            smtpSession.loginState.add(LoginState.PASSWORD_TRANSMITTED)
            return SmtpResponseCode.AUTH_GO_ON("UGFzc3dvcmQ6")
        } else if (smtpSession.loginState.contains(LoginState.USERNAME_TRANSMITTED) && smtpSession.loginState.contains(LoginState.PASSWORD_TRANSMITTED)) {
            val password = Base64.getDecoder().decode(data).toString(Charset.forName("UTF-8"))
            smtpSession.loginState.clear()
            return if(smtpListener.isValidUser(smtpSession, password))
                SmtpResponseCode.AUTH_OK("Welcome back ${smtpSession.loginUsername}")
            else
                SmtpResponseCode.INVALID("Invalid user/password")
        } else {
            return SmtpResponseCode.BAD_SEQUENCE
        }
    }
}