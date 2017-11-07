package fr.bnancy.mail.servers.pop3.commands

import fr.bnancy.mail.servers.pop3.commands.annotations.Pop3Command
import fr.bnancy.mail.servers.pop3.data.Pop3ResponseCode
import fr.bnancy.mail.servers.pop3.data.Pop3Session
import fr.bnancy.mail.servers.pop3.data.Pop3SessionState
import fr.bnancy.mail.service.Pop3Service

@Pop3Command("RETR")
class RetrCommand: Pop3AbstractCommand {
    override fun execute(data: String, session: Pop3Session, pop3Service: Pop3Service): Pop3ResponseCode {
        if(session.currentState != Pop3SessionState.TRANSACTION)
            return Pop3ResponseCode.ERR("Unknown command")

        val splitedCommand = data.split(' ')
        if(splitedCommand.size != 2)
            return Pop3ResponseCode.ERR("Missing message ID")

        val messageId = splitedCommand[1].toLong()

        if(!pop3Service.messageExistsForUser(session.user, messageId))
            return Pop3ResponseCode.ERR("Message $messageId doesn't exists")

        return Pop3ResponseCode.OK("\r\n${pop3Service.getFullMailContent(messageId)}\r\n.")
    }

}