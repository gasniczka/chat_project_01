package org.jk.project_chat_from_v2___20220610.commons;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;


@Builder
@Getter
@Setter
public class TransferObject implements Serializable {

    // obiekt do przesyłania między klientem i serverem
    String clientId;
    String chatRoom;
    String message;
    File file;

}
