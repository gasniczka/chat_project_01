package org.jk.project_chat_from_v2;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
class ServerEvent implements Serializable {

    private final ServerEventType type;
    private String payload;
    private Worker source;

}
