package org.jk.ex011_chat_v2;

interface ServerWorkers {

    void add(Worker worker);

    void remove(Worker worker);

    void broadcast(String text);

}
