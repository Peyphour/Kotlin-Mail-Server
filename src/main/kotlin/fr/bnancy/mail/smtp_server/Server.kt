package fr.bnancy.mail.smtp_server

import fr.bnancy.mail.config.ServerConfig
import fr.bnancy.mail.smtp_server.listeners.SessionListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.net.ServerSocket
import java.net.Socket
import javax.annotation.PostConstruct

@Component
class Server {

    @Autowired
    lateinit var config: ServerConfig

    @Autowired
    lateinit var listener: SessionListener

    lateinit var socketServer: ServerSocket
    var running: Boolean = false
    val clients: ArrayList<ClientRunnable> = ArrayList()

    @PostConstruct
    fun init() {
        this.socketServer = ServerSocket(this.config.port)
    }

    fun start() {
        this.running = true
        Thread({
            while(running) {
                val client: Socket = this.socketServer.accept()
                clients.add(ClientRunnable(client, listener, config.sessionTimeout))
                Thread(clients[clients.size - 1]).start()
            }
        }).start()

        println("Starting SMTP server on port ${config.port}")
    }

    fun stop() {
        running = false
        clients.forEach { it.stop() }
    }
}

