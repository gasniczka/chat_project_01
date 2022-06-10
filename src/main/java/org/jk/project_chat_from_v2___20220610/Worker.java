package org.jk.project_chat_from_v2___20220610;

import lombok.Getter;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.jk.project_chat_from_v2___20220610.commons.TransferObject;
import org.jk.project_chat_from_v2___20220610.commons.TransferObjectReader;
import org.jk.project_chat_from_v2___20220610.commons.TransferObjectWriter;

import java.io.Serializable;
import java.net.Socket;

import static org.jk.project_chat_from_v2___20220610.ServerEventType.GET_HISTORY;
import static org.jk.project_chat_from_v2___20220610.ServerEventType.MESSAGE_RECEIVED;
import static org.jk.project_chat_from_v2___20220610.ServerEventType.ROOM_INFO;
import static org.jk.project_chat_from_v2___20220610.ServerEventType.SYSTEM_INFO;


@Log
class Worker implements Runnable, Serializable {

    private static final String COMMAND_LEADING_SIGN = "//";
    private static final String COMMAND_SEPARATOR_SIGN = " ";
    static final String GENERAL_ROOM_NAME = "general";

    static final String SERVER_INFO_CLIENT = "info";
    static final String SERVER_INFO_ROOM = "server";

    private final Socket socket;
    private final EventsBus eventsBus;
    private final TransferObjectWriter writer;

    @Getter
    private String chatRoom = GENERAL_ROOM_NAME;


    Worker(Socket socket, EventsBus eventsBus) {
        this.socket = socket;
        this.eventsBus = eventsBus;
        writer = new TransferObjectWriter(socket);
    }


    // odczytywanie
    @Override
    public void run() {
        new TransferObjectReader(socket, this::onTransfer, this::onInputClose).read();
    }

    private void onTransfer(TransferObject transferObject) {

        if (transferObject == null) {
            log.warning(" -- onTransfer -> transferObject == null ");
            return;
        }

        String text = transferObject.getMessage();

        // worker obsługuje przychodzące informacje -> tu parsowanie treści i obsługę komend

        if (text.startsWith(COMMAND_LEADING_SIGN)) {

            String[] commandArray = text.split(COMMAND_SEPARATOR_SIGN);

            System.out.println(commandArray[0]);
            Command command = Command.getByOperation(commandArray[0]);

            System.out.println(command);

            switch (command) {

                case VIEW_ROOMS -> viewRooms(
                        eventsBus,
                        TransferObject.builder()
                                .clientId(SERVER_INFO_CLIENT)
                                .chatRoom(SERVER_INFO_ROOM)
                                .message("available rooms: ")
                                .build(),
                        this);

                case SWITCH_ROOM -> {
                    String room = switchRoom(transferObject.getClientId(), commandArray);
                    sendInfo(
                            eventsBus,
                            TransferObject.builder()
                                    .clientId(SERVER_INFO_CLIENT)
                                    .chatRoom(SERVER_INFO_ROOM)
                                    .message("switched to room: " + room)
                                    .build(),
                            this);
                }

                case VIEW_HISTORY -> viewHistory(
                        eventsBus,
                        transferObject,
                        this);

                case VIEW_FILES -> log.info(" view files");

                case SEND_FILE -> log.info(commandArray[1] + " send file");

                case DOWNLOAD_FILE -> log.info(commandArray[1] + " download file");

                default -> log.info(commandArray[0] + " no proper action specified");
            }

        } else {

            // tu nie ma komendy więc wysyłam text
            sendMessage(eventsBus, transferObject, this);
        }

    }


    private void onInputClose() {
        eventsBus.publish(ServerEvent.builder()
                .type(ServerEventType.CONNECTION_CLOSED)
                .source(this)
                .build());
    }

    void send(TransferObject transferObject) {
        writer.write(transferObject);
    }


    // to co mozna zrobic w ramach sterowania z konsoli

    private static void sendMessage(EventsBus eventsBus, TransferObject transferObject, Worker worker) {
        // wysyłanie wiadomości
        eventsBus.publish(ServerEvent.builder()
                .type(MESSAGE_RECEIVED)
                .payload(transferObject)
                .source(worker)
                .build());
    }

    private static void sendInfo(EventsBus eventsBus, TransferObject transferObject, Worker worker) {
        // wysyłanie informacji zwrotnej z servera do uzytkownika wolajacego
        eventsBus.publish(ServerEvent.builder()
                .type(SYSTEM_INFO)
                .payload(transferObject)
                .source(worker)
                .build());
    }


    private String switchRoom(String clientId, String[] command) {

        String room = GENERAL_ROOM_NAME;

        if (command.length > 1) {
            room = command[1].trim();
        }

        if (StringUtils.isBlank(room)) {
            room = GENERAL_ROOM_NAME;
        }

        log.info(clientId + " switching room to " + room);

        chatRoom = room;

        log.info(clientId + " switched room to " + room);
        return room;
    }


    private static void viewRooms(EventsBus eventsBus, TransferObject transferObject, Worker worker) {
        // wysyłanie informacji zwrotnej z servera do uzytkownika wolajacego o dostepnych pokojach
        eventsBus.publish(ServerEvent.builder()
                .type(ROOM_INFO)
                .payload(transferObject)
                .source(worker)
                .build());
    }


    private static void viewHistory(EventsBus eventsBus, TransferObject transferObject, Worker worker) {

        // wysyłanie nagłowka informacji o historii
        eventsBus.publish(ServerEvent.builder()
                .type(SYSTEM_INFO)
                .payload(TransferObject.builder()
                        .clientId(SERVER_INFO_CLIENT)
                        .chatRoom(SERVER_INFO_ROOM)
                        .message("chat history:")
                        .build())
                .source(worker)
                .build());

        // wysyłanie informacji zwrotnej z servera do uzytkownika wolajacego o historii chatu
        eventsBus.publish(ServerEvent.builder()
                .type(GET_HISTORY)
                .payload(transferObject)
                .source(worker)
                .build());

    }


}
