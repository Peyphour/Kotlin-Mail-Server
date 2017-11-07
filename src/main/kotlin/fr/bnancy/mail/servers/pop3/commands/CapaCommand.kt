package fr.bnancy.mail.servers.pop3.commands

import fr.bnancy.mail.servers.pop3.commands.annotations.Pop3Command
import fr.bnancy.mail.servers.pop3.data.Pop3ResponseCode
import fr.bnancy.mail.servers.pop3.data.Pop3Session
import fr.bnancy.mail.service.Pop3Service

@Pop3Command("CAPA")
class CapaCommand: Pop3AbstractCommand {
    override fun execute(data: String, session: Pop3Session, pop3Service: Pop3Service): Pop3ResponseCode {
        return Pop3ResponseCode.OK("\r\n" +
                "CAPA\r\n" +
                "USER\r\n" +
                "UIDL\r\n" +
                ".")
    }
}