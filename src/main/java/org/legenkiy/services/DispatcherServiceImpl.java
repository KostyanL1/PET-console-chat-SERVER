package org.legenkiy.services;


import lombok.RequiredArgsConstructor;
import org.legenkiy.api.service.ChatService;
import org.legenkiy.api.service.DispatcherService;
import org.legenkiy.connection.ConnectionsManager;
import org.legenkiy.protocol.ClientMessage;
import org.legenkiy.protocol.MessageType;
import org.legenkiy.protocol.ServerMessage;
import org.springframework.stereotype.Service;


import java.io.PrintWriter;
import java.net.Socket;

@Service
@RequiredArgsConstructor
public class DispatcherServiceImpl implements DispatcherService {

    private final ConnectionsManager connectionsManager;
    private final ChatService chatService;

    @Override
    public void handle(ClientMessage clientMessage, Socket socket, PrintWriter printWriter) {
        MessageType messageType = clientMessage.getMessageType();
        switch (messageType) {
            case HELLO -> {

            }
            case OK -> {

            }
            case PM -> {

            }
            case MSG -> {
                chatService.processMessage(clientMessage);
                break;
            }
            case WHO -> {

            }
            case ERROR -> {

            }
            case LOGIN -> {

            }
            case REGISTER -> {

            }
            default -> {
                printWriter.println("[UNKNOWN COMMAND]");
                break;
            }
        }
    }
}
