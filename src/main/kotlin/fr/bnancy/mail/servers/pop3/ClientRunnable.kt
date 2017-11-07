package fr.bnancy.mail.servers.pop3

import fr.bnancy.mail.CRLFTerminatedReader
import fr.bnancy.mail.servers.pop3.commands.Pop3AbstractCommand
import fr.bnancy.mail.servers.pop3.data.Pop3ResponseCode
import fr.bnancy.mail.servers.pop3.data.Pop3Session
import fr.bnancy.mail.servers.pop3.data.Pop3SessionState
import fr.bnancy.mail.service.Pop3Service
import java.io.PrintWriter
import javax.net.ssl.SSLSocket

class ClientRunnable(private val clientSocket: SSLSocket, private val sessionTimeout: Int, val commands: MutableMap<String,
        Pop3AbstractCommand>, private val pop3Service: Pop3Service) : Runnable {

    private var running: Boolean = true
    private val lineSeparator = "\r\n"
    private val session: Pop3Session = Pop3Session()

    override fun run() {

        val reader = CRLFTerminatedReader(this.clientSocket.inputStream)
        val out = PrintWriter(this.clientSocket.outputStream, true)
        var timeout: Long = System.currentTimeMillis()

        write(out, "+OK POP3 ready")

        while(running && (System.currentTimeMillis() - timeout < sessionTimeout)) {
            val line = reader.readLine() ?: // Received EOF
                    break

            timeout = System.currentTimeMillis()

            val response = handleCommand(line, session, pop3Service)

            write(out, response.code)

            if(session.currentState == Pop3SessionState.UPDATE) {
                pop3Service.deleteMails(session.messageToDelete)
                running = false
            }
        }

        clientSocket.close()
    }

    private fun handleCommand(data: String, session: Pop3Session, pop3Service: Pop3Service): Pop3ResponseCode {
        val commandString = data.takeWhile { it.isLetter() }.toUpperCase()
        val command: Pop3AbstractCommand? = commands[commandString]
        return when {
            command != null -> command.execute(data, session, pop3Service)
            else -> Pop3ResponseCode.ERR("Unknown command.")
        }
    }

    private fun write(out: PrintWriter, s: String) {
        out.write(s)
        out.write(lineSeparator)
        out.flush()
    }

    fun stop() {
        this.running = false
    }
}