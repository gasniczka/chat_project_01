package org.jk.project_chat_from_v2___20220610;


import org.jk.project_chat_from_v2___20220610.commons.TransferObject;

// kontrakt, funkcjonalności realizowane
interface ServerWorkers {

    void add(Worker worker);

    void remove(Worker worker);

    void broadcast(TransferObject transferObject);


    // przesłanie informacji do osób w pokoju
    void broadcastInRoom(Worker worker, TransferObject transferObject);

    // odpowiedz servera na zapytanie konkretnego chatusera
    void serverResponse(Worker worker, TransferObject transferObject);

    // informacja o pokojach na chacie
    String getRoomInfo(TransferObject transferObject);

    void getHistory(Worker worker, TransferObject transferObject);

}
