package org.jk.ex011_chat_v2;

import lombok.extern.java.Log;

import java.util.function.Consumer;;

@Log
class MessagesHistoryLogger implements Consumer<ServerEvent> {

    @Override
    public void accept(ServerEvent event) {
        if (event.getType().equals(ServerEventType.MESSAGE_RECEIVED)) {
            log.info("New message: " + event.getPayload());
        }
    }

}
