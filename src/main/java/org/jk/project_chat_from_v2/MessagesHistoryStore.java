package org.jk.project_chat_from_v2;

import lombok.extern.java.Log;

import java.util.function.Consumer;

;

@Log
class MessagesHistoryStore implements Consumer<ServerEvent> {

    @Override
    public void accept(ServerEvent event) {

        if (event.getType().equals(ServerEventType.MESSAGE_RECEIVED)) {
            IOUtils.saveHistory(event);
        }

    }

}
