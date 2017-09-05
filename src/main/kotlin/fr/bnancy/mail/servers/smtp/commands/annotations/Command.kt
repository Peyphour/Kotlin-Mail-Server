package fr.bnancy.mail.servers.smtp.commands.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(val command: String)