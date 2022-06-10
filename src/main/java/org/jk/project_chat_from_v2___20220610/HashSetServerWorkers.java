package org.jk.project_chat_from_v2___20220610;

import org.jk.project_chat_from_v2___20220610.commons.HistoryObject;
import org.jk.project_chat_from_v2___20220610.commons.TransferObject;
import org.jk.project_chat_from_v2___20220610.utils.IOUtils;

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
    public void broadcast(TransferObject transferObject) {
        workers.forEach(worker -> worker.send(transferObject));
    }

    @Override
    public void broadcastInRoom(Worker worker, TransferObject transferObject) {

        // ustawienie informacji o tym kto na jakim kanale wysłał message
        transferObject.setChatRoom(worker.getChatRoom());

        workers.stream()
                // nie wysyła do siebie
                .filter(w -> !worker.equals(w))
                // wysyła tylko w swoim pokoju
                .filter(w -> worker.getChatRoom().equals(w.getChatRoom()))
                .forEach(w -> w.send(transferObject));
    }

    @Override
    public void serverResponse(Worker worker, TransferObject transferObject) {
        worker.send(transferObject);
    }

    @Override
    public String getRoomInfo(TransferObject transferObject) {

        return transferObject.getMessage() + workers.stream()
                .map(Worker::getChatRoom)
                .sorted()
                .distinct()
                .collect(Collectors.joining(", "));
    }

    @Override
    public void getHistory(Worker worker, TransferObject transferObject) {

        // wysłanie nagłowka z informacja ze to historia czatu
        worker.send(transferObject);

        // odczyt historii
        List<HistoryObject> fullHistory = IOUtils.readHistory();

        List<String> userRooms = fullHistory.stream()
                .filter(history -> transferObject.getClientId().equals(history.getTransferObject().getClientId()))
                .map(HistoryObject::getChatroom)
                .sorted()
                .distinct()
                .toList();

        // wysylanie historii czatu
        fullHistory.stream()
                .filter(history -> userRooms.contains(history.getChatroom()))
                .map(HistoryObject::getTransferObject)
                .forEach(worker::send);

    }

}

