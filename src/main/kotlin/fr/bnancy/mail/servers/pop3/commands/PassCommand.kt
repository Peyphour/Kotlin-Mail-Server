package fr.bnancy.mail.servers.pop3.commands

import fr.bnancy.mail.servers.pop3.commands.annotations.Pop3Command
import fr.bnancy.mail.servers.pop3.data.Pop3ResponseCode
import fr.bnancy.mail.servers.pop3.data.Pop3Session
import fr.bnancy.mail.servers.pop3.data.Pop3SessionState
import fr.bnancy.mail.service.Pop3Service

@Pop3Command("PASS")
class PassCommand: Pop3AbstractCommand {
    override fun execute(data: String, session: Pop3Session, pop3Service: Pop3Service): Pop3ResponseCode {
        if(session.user == "")
            return Pop3ResponseCode.ERR("No username given")

        val splitedCommand = data.split(' ')
        if(splitedCommand.size != 2)
            return Pop3ResponseCode.ERR("No password given")

        return if(pop3Service.authenticateUser(session.user, splitedCommand[1])) {
            session.currentState = Pop3SessionState.TRANSACTION
            Pop3ResponseCode.OK("Logged in")
        } else {
            Pop3ResponseCode.ERR("[AUTH] Authentication failed.")
        }
    }

}