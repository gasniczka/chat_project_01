package org.jk.project_chat_from_v2___20220610.commons;

import lombok.extern.java.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@Log
public class TextWriter {

    private PrintWriter writer;

    public TextWriter(Socket socket) {
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException exception) {
           log.severe("Creating output stream failed: " + exception.getMessage());
        }
    }

    // otrzymany text zapisuje do socketu
    public void write(String text) {
        writer.println(text);
    }

}
