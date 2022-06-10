package org.jk.project_chat_from_v2;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Collections.synchronizedSet;

class EventsBus {

    // bez implementacji, ale czy event bus nie powinien odpowiadać za zapis dancyh,
    // kolejki/bufory napychane a event bus zdejmuje z kolejki w osobnym wątku



    private final Set<Consumer<ServerEvent>> consumers = synchronizedSet(new HashSet<>());

    void addConsumer(Consumer<ServerEvent> consumer) {
        consumers.add(consumer);
    }

    void publish(ServerEvent event) {
        consumers.forEach(consumer -> consumer.accept(event));
    }

}
