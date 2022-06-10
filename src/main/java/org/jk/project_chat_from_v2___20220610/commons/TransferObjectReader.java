package org.jk.project_chat_from_v2___20220610.commons;

import lombok.extern.java.Log;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;


@Log
public class TransferObjectReader {

    private final Consumer<TransferObject> transferObjectConsumer;
    private ObjectInputStream reader;
    private Runnable onClose;

    public TransferObjectReader(InputStream inputStream, Consumer<TransferObject> transferObjectConsumer) {

        this.transferObjectConsumer = transferObjectConsumer;

        try {
            reader = new ObjectInputStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TransferObjectReader(Socket socket, Consumer<TransferObject> transferObjectConsumer, Runnable onClose) {

        this.transferObjectConsumer = transferObjectConsumer;
        this.onClose = onClose;

        try {
            // czytanie z socketu, strumien utworzony z socketu
            reader = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            log.severe("Creating input stream failed: " + e.getMessage());
        }

    }

    public void read() {
        TransferObject transferObject;
        try {
            while ((transferObject = (TransferObject) reader.readObject()) != null) {
                // publikowanie przez callback, przez consumenta
                transferObjectConsumer.accept(transferObject);
            }
        } catch (IOException | ClassNotFoundException exception) {
            log.severe("Read transfer object failed: " + exception.getMessage());
        } finally {
            if (onClose != null) {

                // wykonaj akcje po onClose po zakończeniu odczytu
                onClose.run();
            }
        }
    }

}
