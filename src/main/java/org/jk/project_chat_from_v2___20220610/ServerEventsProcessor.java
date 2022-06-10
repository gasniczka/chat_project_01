package org.jk.project_chat_from_v2___20220610;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;


@RequiredArgsConstructor
public class ServerEventsProcessor implements Consumer<ServerEvent> {

    private final ServerWorkers serverWorkers;

    @Override
    public void accept(ServerEvent event) {

        switch (event.getType()) {

            case MESSAGE_RECEIVED -> {

                try {
                    Worker worker = event.getSource();

                    if (worker.getChatRoom() != null) {
                        serverWorkers.broadcastInRoom(worker, event.getPayload());
                    } else {
                        serverWorkers.broadcast(event.getPayload());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            case SYSTEM_INFO -> serverWorkers.serverResponse(event.getSource(), event.getPayload());

            case ROOM_INFO -> {

                try {
                    Worker worker = event.getSource();
                    String availableRooms = serverWorkers.getRoomInfo(event.getPayload());

                    event.getPayload().setMessage(availableRooms);
                    serverWorkers.serverResponse(worker, event.getPayload()); // TransferObject.builder().clientId("server info").message(availableRooms).build() );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            case GET_HISTORY -> {

                try {
                    Worker worker = event.getSource();
                    serverWorkers.getHistory(worker, event.getPayload());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            case CONNECTION_CLOSED -> serverWorkers.remove(event.getSource());
        }

    }

}
