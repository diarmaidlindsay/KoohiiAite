package tech.diarmaid.koohiiaite.utils

import java.io.BufferedReader
import java.io.IOException

class CSVLineReader(private val br: BufferedReader) {

    init {
        try {
            //skip first line (header row)
            br.readLine()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    fun readLine(): String? {
        //read the first character
        var i = br.read()
        //if it's the end of the stream then return null
        if (i < 0) {
            return null
        }
        val sb = StringBuilder()
        //add first character to newly created StringBuffer
        sb.append(i.toChar())
        //if the character which was added was not a newline character...
        if (i != '\r'.toInt() && i != '\n'.toInt()) {
            //read and keep adding next character (and continue reading)
            while (true) {
                i = br.read()
                if(0 > i) break
                //Terminate line if "\r is encountered
                if (i == '\r'.toInt() && sb[sb.length - 1] == '"') {
                    sb.append("\r\n")
                    return sb.toString()
                } else {
                    sb.append(i.toChar())
                }
            }
        }

        return sb.toString()
    }
}
