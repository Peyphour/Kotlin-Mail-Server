package fr.bnancy.mail.servers.pop3.commands

import fr.bnancy.mail.servers.pop3.commands.annotations.Pop3Command
import fr.bnancy.mail.servers.pop3.data.Pop3ResponseCode
import fr.bnancy.mail.servers.pop3.data.Pop3Session
import fr.bnancy.mail.servers.pop3.data.Pop3SessionState
import fr.bnancy.mail.service.Pop3Service

@Pop3Command("UIDL")
class UidlCommand: Pop3AbstractCommand {
    override fun execute(data: String, session: Pop3Session, pop3Service: Pop3Service): Pop3ResponseCode {
        if(session.currentState != Pop3SessionState.TRANSACTION)
            return Pop3ResponseCode.ERR("Unknown command.")

        return Pop3ResponseCode.OK("\r\n" + pop3Service.getUidl(session.user))
    }
}