package org.jk.ex011_chat_v2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class ServerEvent {

    private final ServerEventType type;
    private String payload;
    private Worker source;

}
