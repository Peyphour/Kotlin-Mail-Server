package fr.bnancy.mail

import java.net.InetAddress

fun getHostname(): String {
    return InetAddress.getLocalHost().hostName
}