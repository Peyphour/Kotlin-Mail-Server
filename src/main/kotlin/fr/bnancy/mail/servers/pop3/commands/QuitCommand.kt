package fr.bnancy.mail.servers.pop3.commands

import fr.bnancy.mail.servers.pop3.commands.annotations.Pop3Command
import fr.bnancy.mail.servers.pop3.data.Pop3ResponseCode
import fr.bnancy.mail.servers.pop3.data.Pop3Session
import fr.bnancy.mail.servers.pop3.data.Pop3SessionState
import fr.bnancy.mail.service.Pop3Service

@Pop3Command("QUIT")
class QuitCommand: Pop3AbstractCommand {
    override fun execute(data: String, session: Pop3Session, pop3Service: Pop3Service): Pop3ResponseCode {
        session.currentState = Pop3SessionState.UPDATE

        return if(session.messageToDelete.size > 0) {
            Pop3ResponseCode.OK("Logging out. ${session.messageToDelete.size} messages deleted")
        } else {
            Pop3ResponseCode.OK("Logging out.")
        }
    }

}