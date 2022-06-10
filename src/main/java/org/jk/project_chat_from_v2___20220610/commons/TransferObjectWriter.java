package org.jk.project_chat_from_v2___20220610.commons;

import lombok.extern.java.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;


@Log
public class TransferObjectWriter {

    private ObjectOutputStream writer;


    public TransferObjectWriter(Socket socket) {

        try {
            writer = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException exception) {
            log.severe("Creating output stream failed: " + exception);
        }

    }


    // otrzymany obiekt zapisuje do socketu
    public void write(TransferObject transferObject) {

        try {
            writer.writeObject(transferObject);
            // writer.flush();
            // writer.reset();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
