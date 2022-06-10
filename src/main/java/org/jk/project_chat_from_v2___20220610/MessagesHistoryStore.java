package org.jk.project_chat_from_v2___20220610;

import lombok.extern.java.Log;
import org.jk.project_chat_from_v2___20220610.commons.HistoryObject;
import org.jk.project_chat_from_v2___20220610.utils.IOUtils;

import java.util.function.Consumer;


@Log
class MessagesHistoryStore implements Consumer<ServerEvent> {

    @Override
    public void accept(ServerEvent event) {

        if (event.getType().equals(ServerEventType.MESSAGE_RECEIVED)) {

            IOUtils.saveHistory(
                    HistoryObject.builder()
                            .transferObject(event.getPayload())
                            .chatroom(event.getSource().getChatRoom())
                            .build()
            );
        }

    }

}
