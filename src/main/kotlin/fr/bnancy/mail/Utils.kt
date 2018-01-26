package fr.bnancy.mail

import java.io.*
import java.net.InetAddress
import java.nio.charset.StandardCharsets
import java.security.KeyPair
import java.security.KeyStore
import java.security.interfaces.RSAPrivateKey

fun getHostname(): String {
    return InetAddress.getLocalHost().canonicalHostName
}

fun getRSAKeyPair(): KeyPair? {

    val keyStorePath = System.getProperty("javax.net.ssl.keyStore") ?: return null
    val keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword").toCharArray()

    val keyStore = KeyStore.getInstance("JKS")
    val inputStream = FileInputStream(keyStorePath)
    keyStore.load(inputStream, keyStorePassword)

    val alias = keyStore.aliases().nextElement()

    val certificate = keyStore.getCertificate(alias)

    val privateKey = keyStore.getKey(alias, keyStorePassword) as RSAPrivateKey

    return KeyPair(certificate.publicKey, privateKey)
}

/***********************************************************************
 * Copyright (c) 2000-2006 The Apache Software Foundation.             *
 * All rights reserved.                                                *
 * ------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License"); you *
 * may not use this file except in compliance with the License. You    *
 * may obtain a copy of the License at:                                *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0                      *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS,   *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or     *
 * implied.  See the License for the specific language governing       *
 * permissions and limitations under the License.                      *
 */
/**
 * A Reader for use with SMTP or other protocols in which lines
 * must end with CRLF.  Extends Reader and overrides its
 * readLine() method.  The Reader readLine() method cannot
 * serve for SMTP because it ends lines with either CR or LF alone.

 * JSS: The readline() method of this class has been 'enchanced' from
 * the Apache JAMES version to throw an IOException if the line is
 * greater than or equal to MAX_LINE_LENGTH (998) which is defined
 * in [RFC 2822](http://rfc.net/rfc2822.html#s2.1.1.).
 */
class CRLFTerminatedReader(
        /**
         * Constructs this CRLFTerminatedReader.

         * @param in
         * *            an InputStream
         * *
         * @param charsetName
         * *            the String name of a supported charset. "ASCII" is common
         * *            here.
         * *
         * @throws UnsupportedEncodingException
         * *             if the named charset is not supported
         */
        internal var `in`: BufferedReader) : Reader() {

    inner class TerminationException : IOException {
        private var where: Int = 0

        constructor(where: Int) : super() {
            this.where = where
        }

        constructor(s: String, where: Int) : super(s) {
            this.where = where
        }

        fun position(): Int {
            return this.where
        }
    }

    inner class MaxLineLengthException : IOException {
        constructor() : super()

        constructor(s: String) : super(s)
    }

    @Throws(UnsupportedEncodingException::class)
    constructor(`in`: InputStream) : this(BufferedReader(InputStreamReader(`in`, StandardCharsets.UTF_8)))

    private val lineBuffer = StringBuffer()
    private val EOF = -1
    private val CR = 13
    private val LF = 10

    private var tainted = -1

    /**
     * Read a line of text which is terminated by CRLF.  The concluding
     * CRLF characters are not returned with the String, but if either CR
     * or LF appears in the text in any other sequence it is returned
     * in the String like any other character.  Some characters at the
     * end of the stream may be lost if they are in a "line" not
     * terminated by CRLF.

     * @return either a String containing the contents of a
     * * line which must end with CRLF, or null if the end of the
     * * stream has been reached, possibly discarding some characters
     * * in a line not terminated with CRLF.
     * *
     * @throws IOException if an I/O error occurs.
     */
    @Throws(IOException::class)
    fun readLine(): String? {
        //start with the StringBuffer empty
        this.lineBuffer.delete(0, this.lineBuffer.length)

        /* This boolean tells which state we are in,
		 * depending upon whether or not we got a CR
		 * in the preceding read().
		 */
        var cr_just_received = false

        while (true) {
            val inChar = this.read()

            if (!cr_just_received) {
                //the most common case, somewhere before the end of a line
                when (inChar) {
                    CR -> cr_just_received = true
                    EOF -> return null // premature EOF -- discards data(?)
                    LF //the normal ending of a line
                    -> {
                        if (this.tainted == -1)
                            this.tainted = this.lineBuffer.length
                        this.lineBuffer.append(inChar.toChar())
                    }
                // intentional fall-through
                    else -> this.lineBuffer.append(inChar.toChar())
                }
            } else {
                // CR has been received, we may be at end of line
                when (inChar) {
                    LF // LF without a preceding CR
                    -> {
                        if (this.tainted != -1) {
                            val pos = this.tainted
                            this.tainted = -1
                            throw TerminationException(
                                    "\"bare\" CR or LF in data stream", pos)
                        }
                        return this.lineBuffer.toString()
                    }
                    EOF -> return null // premature EOF -- discards data(?)
                    CR //we got two (or more) CRs in a row
                    -> {
                        if (this.tainted == -1)
                            this.tainted = this.lineBuffer.length
                        this.lineBuffer.append(this.CR.toChar())
                    }
                    else //we got some other character following a CR
                    -> {
                        if (this.tainted == -1)
                            this.tainted = this.lineBuffer.length
                        this.lineBuffer.append(this.CR.toChar())
                        this.lineBuffer.append(inChar.toChar())
                        cr_just_received = false
                    }
                }
            }
            if (this.lineBuffer.length >= MAX_LINE_LENGTH) {
                throw MaxLineLengthException("Input line length is too long!")
            }
        }
    }

    @Throws(IOException::class)
    override fun read(): Int {
        return this.`in`.read()
    }

    @Throws(IOException::class)
    override fun ready(): Boolean {
        return this.`in`.ready()
    }

    @Throws(IOException::class)
    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        val temp = ByteArray(len)
        val result = this.`in`.read(String(temp, StandardCharsets.UTF_8).toCharArray(), 0, len)
        for (i in 0 until result)
            cbuf[i] = temp[i].toChar()
        return result
    }

    @Throws(IOException::class)
    override fun close() {
        this.`in`.close()
    }

    companion object {
        internal var MAX_LINE_LENGTH = 998
    }
}