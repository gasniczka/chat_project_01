package org.jk.project_chat_from_v2___20220610.commons;


import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;


@Builder
@Getter
public class HistoryObject implements Serializable {

    private TransferObject transferObject;
    private String chatroom;

}
