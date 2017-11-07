package fr.bnancy.mail.servers.pop3.commands

import fr.bnancy.mail.servers.pop3.commands.annotations.Pop3Command
import fr.bnancy.mail.servers.pop3.data.Pop3ResponseCode
import fr.bnancy.mail.servers.pop3.data.Pop3Session
import fr.bnancy.mail.service.Pop3Service

@Pop3Command("USER")
class UserCommand: Pop3AbstractCommand {
    override fun execute(data: String, session: Pop3Session, pop3Service: Pop3Service): Pop3ResponseCode {
        val splitedCommand = data.split(' ')

        if(splitedCommand.size != 2)
            return Pop3ResponseCode.ERR("[AUTH] empty username")

        session.user = splitedCommand[1]

        return Pop3ResponseCode.OK
    }
}