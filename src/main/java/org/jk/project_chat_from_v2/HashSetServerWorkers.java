package org.jk.project_chat_from_v2;

import java.util.*;
import java.util.stream.Collectors;


// realizacja kontraktu
class HashSetServerWorkers implements ServerWorkers {

    private final Set<Worker> workers = new HashSet<>();

    @Override
    public void add(Worker worker) {
        workers.add(worker);
    }

    @Override
    public void remove(Worker worker) {
        workers.remove(worker);
    }

    @Override
    public void broadcast(String text) {
        workers.forEach(worker -> worker.send(text));
    }

    @Override
    public void broadcastInRoom(Worker worker, String text) {

        workers.stream()
                .filter(w -> worker.getChatRoom().equals(w.getChatRoom()))
                .forEach(w -> w.send(text));
    }

    @Override
    public void serverResponse(Worker worker, String text) {
        worker.send(text);
    }

    @Override
    public String getRoomInfo(String text) {

        return text + workers.stream()
                .map(w -> Optional.ofNullable(w.getChatRoom()).orElse(Worker.GENERAL_ROOM))
                .sorted()
                .distinct()
                .collect(Collectors.joining(", "));
    }

    @Override
    public void getHistory(Worker worker, String text) {

        List<ServerEvent> fullHistory = IOUtils.readHistory();

        List<String> userRooms = fullHistory.stream()
                .filter(serverEvent -> worker.equals(serverEvent.getSource()))
                .map(serverEvent -> serverEvent.getSource().getChatRoom())
                .sorted()
                .distinct()
                .toList();

        List<String> historyForUser = new LinkedList<>();

        // wysłanie nagłowka z informacja ze to historia czatu
        worker.send(text);

        // wysylanie historii czatu
        fullHistory.stream()
                .filter(serverEvent -> userRooms.contains(serverEvent.getSource().getChatRoom()))
                .map(ServerEvent::getPayload)
                .forEach(worker::send);

    }

}

