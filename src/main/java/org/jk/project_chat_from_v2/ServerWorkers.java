package org.jk.project_chat_from_v2;


// kontrakt, funkcjonalności realizowane
interface ServerWorkers {

    void add(Worker worker);

    void remove(Worker worker);

    void broadcast(String text);


    // przesłanie informacji do osób w pokoju
    void broadcastInRoom(Worker worker, String text);

    // odpowiedz servera na zapytanie konkretnego chatusera
    void serverResponse(Worker worker, String text);

    // informacja o pokojach na chacie
    String getRoomInfo(String text);

    void getHistory(Worker worker, String text);

}
