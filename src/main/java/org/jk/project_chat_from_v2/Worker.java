package org.jk.project_chat_from_v2;

import lombok.Getter;
import lombok.extern.java.Log;
import org.jk.project_chat_from_v2.commons.TextReader;
import org.jk.project_chat_from_v2.commons.TextWriter;

import java.io.Serializable;
import java.net.Socket;

import static org.jk.project_chat_from_v2.ServerEventType.*;


@Log
class Worker implements Runnable, Serializable {

    private static final String INFO_MESSAGE = "dozwolone operacje: \n  // switch room : room_name \n  // show rooms \n  // send file \n  // show history ";

    static final String GENERAL_ROOM = "general";

    private final Socket socket;
    private final EventsBus eventsBus;
    private final TextWriter writer;

    @Getter
    private String chatRoom = null;


    Worker(Socket socket, EventsBus eventsBus) {
        this.socket = socket;
        this.eventsBus = eventsBus;
        writer = new TextWriter(socket);
    }


    // odczytywanie
    @Override
    public void run() {
        new TextReader(socket, this::onText, this::onInputClose).read();
    }

    private void onText(String text) {

        log.info("Worker.onText = " + text);
        // worker obsługuje przychodzące informacje -> tu parsowanie treści i obsługę komend

        String[] command = text.split(": ");
        if (command[1].startsWith("// ")) {
            switch (command[1]) {

                case "// ?? " -> sendInfo(eventsBus, INFO_MESSAGE, this);

                case "// switch room " -> {
                    String room = switchRoom(command);
                    sendInfo(eventsBus, "switched to room: " + room, this);
                }

                case "// show rooms " -> showRooms(eventsBus, "Available rooms: ", this);

                case "// show history " -> showHistory(eventsBus, "History of your chat: ", this);

                case "// send file " -> log.info(command[0] + " sending file");
                default -> log.info(command[0] + " no proper action specified");
            }

        } else {

            // tu nie ma komendy więc wysyłam text
            sendMessage(eventsBus, text, this);
        }

    }


    private void onInputClose() {
        eventsBus.publish(ServerEvent.builder()
                .type(CONNECTION_CLOSED)
                .source(this)
                .build());
    }

    void send(String text) {
        writer.write(text);
    }


    // to co mozna zrobic w ramach sterowania z konsoli

    private static void sendMessage(EventsBus eventsBus, String text, Worker worker) {
        // wysyłanie wiadomości
        eventsBus.publish(ServerEvent.builder()
                .type(MESSAGE_RECEIVED)
                .payload(text)
                .source(worker)
                .build());
    }

    private static void sendInfo(EventsBus eventsBus, String text, Worker worker) {
        // wysyłanie informacji zwrotnej z servera do uzytkownika wolajacego
        eventsBus.publish(ServerEvent.builder()
                .type(SYSTEM_INFO)
                .payload(text)
                .source(worker)
                .build());
    }


    private String switchRoom(String[] command) {
        String room = command[2];

        if (room != null) {
            room = room.trim();
        } else {
            room = GENERAL_ROOM;
        }

        if (room.equals("")) {
            room = GENERAL_ROOM;
        }

        log.info(command[0] + " switching room to " + room);

        if (GENERAL_ROOM.equals(room)) {
            chatRoom = null;
        } else {
            chatRoom = room;
        }

        log.info(command[0] + " switched room to " + room);
        return room;
    }


    private static void showRooms(EventsBus eventsBus, String text, Worker worker) {
        // wysyłanie informacji zwrotnej z servera do uzytkownika wolajacego o dostepnych pokojach
        eventsBus.publish(ServerEvent.builder()
                .type(ROOM_INFO)
                .payload(text)
                .source(worker)
                .build());
    }


    private static void showHistory(EventsBus eventsBus, String text, Worker worker) {
        // wysyłanie informacji zwrotnej z servera do uzytkownika wolajacego o historii chatu
        eventsBus.publish(ServerEvent.builder()
                .type(GET_HISTORY)
                .payload(text)
                .source(worker)
                .build());
    }


}
