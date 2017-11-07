package fr.bnancy.mail.servers.pop3.commands

import fr.bnancy.mail.servers.pop3.data.Pop3ResponseCode
import fr.bnancy.mail.servers.pop3.data.Pop3Session
import fr.bnancy.mail.service.Pop3Service

interface Pop3AbstractCommand {
    fun execute(data: String, session: Pop3Session, pop3Service: Pop3Service): Pop3ResponseCode
}