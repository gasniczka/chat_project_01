package org.jk.project_chat_from_v2;

import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.jk.project_chat_from_v2.commons.Sockets;
import org.jk.project_chat_from_v2.commons.TextReader;
import org.jk.project_chat_from_v2.commons.TextWriter;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

@Log
public class ChatClient {

    private static final int DEFAULT_PORT = 8888;

    private final Runnable readFromSocket;
    private final Runnable readFromConsole;

    public ChatClient(String host, int port, String name) throws IOException {
        var socket = new Socket(host, port);
        readFromSocket = () -> new TextReader(socket, log::info, () -> Sockets.close(socket)).read();
        readFromConsole = () -> new TextReader(System.in, text -> new TextWriter(socket).write(name + ": " + text)).read();
    }

    // TODO tu by sie chyba przydalo przesylac obiekt skladający sie z  name  i  text



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

}
