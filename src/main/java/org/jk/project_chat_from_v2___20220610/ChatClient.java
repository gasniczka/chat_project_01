package org.jk.project_chat_from_v2___20220610;

import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.jk.project_chat_from_v2___20220610.commons.Sockets;
import org.jk.project_chat_from_v2___20220610.commons.TextReader;
import org.jk.project_chat_from_v2___20220610.commons.TransferObject;
import org.jk.project_chat_from_v2___20220610.commons.TransferObjectReader;
import org.jk.project_chat_from_v2___20220610.commons.TransferObjectWriter;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Random;


@Log
public class ChatClient {

    private static final String CLIENT_FILES_DIRECTORY = "/chatClientFiles/";

    private static final String COMMAND_SEPARATOR_SIGN = " ";


    private static final String HELP_MESSAGE = """
            USER HELP INFO\s
            \t //?             - view help info\s
            \t //vr            - view rooms\s
            \t //sr roomName   - change room to roomName\s
            \t //vf            - view files to download\s
            \t //sf fileName   - send file fileName\s
            \t //df fileName   - download file fileName\s
            \t //vh            - view chat history\s
            """;

    private static final int DEFAULT_PORT = 8888;

    private final Runnable readFromSocket;
    private final Runnable readFromConsole;


    public ChatClient(String host, int port, String name) throws IOException {

        var socket = new Socket(host, port);

        readFromSocket = () -> {

            // odczyt informacji z socketu
            new TransferObjectReader(
                    socket,
                    /*consumer*/
                    (transferObject) -> {
                        String messagetoShow = String.format("%s@%s: %s ", transferObject.getClientId(), transferObject.getChatRoom(), transferObject.getMessage());
                        System.out.println(messagetoShow);

                        if (transferObject.getFile() != null) {
                            // obsługa przyjecia pliku

                            File file = transferObject.getFile();
                            String fileName = CLIENT_FILES_DIRECTORY + file.getName();

                            try {
                                Files.write(new File(fileName).toPath(), transferObject.getFileContent());
                            } catch (IOException e) {
                                System.out.println("error: can not save file " + fileName + "error: " + e);
                            }

                        }
                    },
                    /*onClose*/
                    () -> Sockets.close(socket))
                    .read();

        };


        readFromConsole = () -> {
            // implementacja procesora przetwarzającego tekst wprowadzony
            //     //?              - wyświetli informacje o dostepnych opcjach
            //     // file: nazwa   - utworzy obiekt TransferObject z name, treść poda nazwę pliku, i załączy plik
            //     w pozostałych przypadkach utworzy TransferObject zawierający name i treść

            TransferObjectWriter objectWriter = new TransferObjectWriter(socket);

            new TextReader(
                    System.in,
                    /* consumer */
                    text -> executeInputCommand(objectWriter, name, text)
            ).read();

        };

    }


    private void start() {
        // wątek odczytujący z socketu, message reader oparty o socket, to co otrzyma będzie wypisywał w logu,
        // przejmuje rolę wątku głownego jako jedyny wątek niedemoniczny, bo wątek głowny sie skończył w main,
        // jak zostanie zakończone połaczenie, to ten wątek zostanie zabity i automatycznie wątek demoniczny odczytu z konsoli
        new Thread(readFromSocket).start();

        // wątek odczytujacy z konsoli, to co odczyta z konsoli jest wysyłane do socketu
        var consoleReader = new Thread(readFromConsole);
        consoleReader.setDaemon(true);
        consoleReader.start();
    }

    public static void main(String[] args) throws IOException {

        // domyslna konfiguracja
        String argHost = "localhost";
        String argPort = "";

        try {
            argHost = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            log.warning("setting default host");
        }

        try {
            argPort = args[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            log.warning("setting default port");
        }


        var port = Sockets.parsePort(argPort, DEFAULT_PORT);

        // creating client name
        String clientId = String.format("ID_%s", StringUtils.leftPad(String.valueOf(new Random().nextInt(100)), 5, "0"));

        new ChatClient(argHost, port, clientId).start();
    }

    private static void executeInputCommand(TransferObjectWriter objectWriter, String name, String text) {

        // procesowanie wejscia
        if ("//?".equals(text)) {
            System.out.println(HELP_MESSAGE);
            return;
        }

        // przygotowanie transfer object
        TransferObject transferObject;

        if (text.length() >= 4 && "//sf".equals(text.substring(0, 4))) {

            String[] commandArray = text.split(COMMAND_SEPARATOR_SIGN);

            if ((commandArray.length == 1) || (commandArray.length > 1 && StringUtils.isBlank(commandArray[1]))) {
                System.out.println("filename is not specified");
                return;
            }

            File file = new File(commandArray[1]);

            if (!file.exists() || !file.isFile()) {
                System.out.println("file does not exist: " + file.getAbsolutePath());
                return;
            }

            byte[] fileContent;

            try {
                fileContent = Files.readAllBytes(file.toPath());
                System.out.println("file has been sent: " + file.getName());

            } catch (IOException e) {
                throw new RuntimeException("could not read file", e);
            }

            transferObject = TransferObject.builder()
                    .clientId(name)
                    .file(file)
                    .fileContent(fileContent)
                    .build();

        } else {
            // System.out.println("message lub akcja do wykonania na serverze: //vr, //sr, //vf, //df");
            transferObject = TransferObject.builder().clientId(name).message(text).build();
        }

        // wysyłka obiektu
        objectWriter.write(transferObject);
    }

}
