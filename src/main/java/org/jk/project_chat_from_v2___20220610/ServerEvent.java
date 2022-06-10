package org.jk.project_chat_from_v2___20220610;

import lombok.Builder;
import lombok.Getter;
import org.jk.project_chat_from_v2___20220610.commons.TransferObject;

import java.io.Serializable;

@Getter
@Builder
class ServerEvent implements Serializable {

    private final ServerEventType type;
    private TransferObject payload;
    private Worker source;

}
