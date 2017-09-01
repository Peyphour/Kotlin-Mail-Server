package fr.bnancy.mail.servers.imap

import fr.bnancy.mail.config.ImapServerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.ServerSocket
import javax.net.ssl.SSLServerSocketFactory

@Component
class ImapServer {

    @Autowired
    lateinit var configImap: ImapServerConfig

    lateinit var socketServer: ServerSocket

    var running: Boolean = false
    val clients: ArrayList<ImapClientRunnable> = ArrayList()

    fun start() {
        this.running = true
        this.socketServer = SSLServerSocketFactory
                .getDefault()
                .createServerSocket(this.configImap.port)
        Thread({
            while(running) {
                val client = this.socketServer.accept()
            }
        }, "imap-server").start()
    }

    fun stop() {
        this.running = false
    }
}