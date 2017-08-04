package fr.bnancy.mail

import fr.bnancy.mail.server.Server


fun main(args: Array<String>) {
    val server: Server = Server(25)
    server.run()

    println("Starting SMTP server on port " + 25)

    Runtime.getRuntime().addShutdownHook(Thread(server::stop))
}