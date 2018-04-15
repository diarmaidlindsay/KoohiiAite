package tech.diarmaid.koohiiaite.utils;

import java.io.BufferedReader;
import java.io.IOException;

public class CSVLineReader {
    private BufferedReader br;

    public CSVLineReader(BufferedReader br) {
        this.br = br;
        try {
            //skip first line (header row)
            br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLine() throws IOException {
        //read the first character
        int i = br.read();
        //if it's the end of the stream then return null
        if (i < 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        //add first character to newly created StringBuffer
        sb.append((char) i);
        //if the character which was added was not a newline character...
        if (i != '\r' && i != '\n') {
            //read and keep adding next character (and continue reading)
            while (0 <= (i = br.read())) {
                //Terminate line if "\r is encountered
                if (i == '\r' && sb.charAt(sb.length() - 1) == '"') {
                    sb.append("\r\n");
                    return sb.toString();
                } else {
                    sb.append((char) i);
                }
            }
        }

        return sb.toString();
    }
}
