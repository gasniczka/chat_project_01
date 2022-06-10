package org.jk.project_chat_from_v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.jk.project_chat_from_v2.commons.Sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;


@Log
@RequiredArgsConstructor
public class ChatServer {

    private static final int DEFAULT_PORT = 8888;
    private static final int THREADS_COUNT = 1024;

    private final ServerWorkers serverWorkers;
    private final EventsBus eventsBus;
    private final ExecutorService executorService;

    private final List<ClientToken> connectionList = Collections.synchronizedList(new ArrayList<>());


    private void start(int port) throws IOException {

        eventsBus.addConsumer(new ServerEventsProcessor(serverWorkers));

        try (var serverSocket = new ServerSocket(port)) {
            eventsBus.publish(ServerEvent.builder().type(ServerEventType.SERVER_STARTED).build());

            while (true) {

                // zblokowany wątek głowny, nasłuchiwanie, oczekiwanie na nowe połączenie
                var socket = serverSocket.accept();

                // powiadomienie o nowym uzytkowniku
                eventsBus.publish(ServerEvent.builder().type(ServerEventType.CONNECTION_ACCEPTED).build());

                // utworzenie nowego workera, ktory implementuje runnable -> utworzenie nowego wątku
                createWorker(socket);
            }
        }
    }


    private void createWorker(Socket socket) {
        var worker = new Worker(socket, eventsBus);
        serverWorkers.add(worker);
        executorService.execute(worker);
    }


    public static void main(String[] args) throws IOException {

        // tworzenie konfiguracji do uruchomienia servera


        var port = configurePort(args);

        // tworzenie event busa
        var eventsBus = new EventsBus();

        // tworzenie listenerów/konsumerów bazujących na event busie
          // logowanie głownych operacji
        eventsBus.addConsumer(new ServerEventsLogger());
          // logowanie historii wiadomości
        eventsBus.addConsumer(new MessagesHistoryLogger());
          // TODO zapisywanie historii do pliku
        eventsBus.addConsumer(new MessagesHistoryStore());


        // workerzy z synchronizacją, wrapper obsługujący synchronizację na zwykłą kolekcję
        var serviceWorkers = new SynchronizedServiceWorkers(new HashSetServerWorkers());
        var server = new ChatServer(serviceWorkers, eventsBus, newFixedThreadPool(THREADS_COUNT));

        // uruchomienie servera
        server.start(port);
    }


    private static int configurePort(String[] args) {

        String argPort = "";

        try {
            argPort = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            log.warning("setting default port");
            log.warning(e.getMessage());
        }

        return Sockets.parsePort(argPort, DEFAULT_PORT);
    }

}
