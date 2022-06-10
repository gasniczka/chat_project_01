package org.jk.project_chat_from_v2___20220610;

import lombok.extern.java.Log;

import java.util.function.Consumer;


@Log
class MessagesHistoryLogger implements Consumer<ServerEvent> {

    @Override
    public void accept(ServerEvent event) {
        if (event.getType().equals(ServerEventType.MESSAGE_RECEIVED)) {
            log.info("New message: " + event.getPayload().getMessage());
        }
    }

}
