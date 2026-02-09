package org.legenkiy.services;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.ChatService;
import org.legenkiy.api.service.DispatcherService;
import org.legenkiy.protocol.ClientMessage;
import org.legenkiy.protocol.MessageType;
import org.springframework.stereotype.Service;


import java.io.PrintWriter;
import java.net.Socket;

@Service
@RequiredArgsConstructor
public class DispatcherServiceImpl implements DispatcherService {

    private final static Logger LOGGER = LogManager.getLogger(DispatcherServiceImpl.class);

    private final ChatService chatService;
    private final AuthService authService;

    @Override
    public void handle(ClientMessage clientMessage, Socket socket, PrintWriter printWriter) {
        LOGGER.info("Handling request from {}" , socket.getRemoteSocketAddress());
        MessageType messageType = clientMessage.getMessageType();
        switch (messageType) {
            case HELLO -> {

            }
            case OK -> {

            }
            case PM -> {
                chatService.processMessage(clientMessage, socket, printWriter);
                break;
            }
            case MSG -> {

            }
            case WHO -> {

            }
            case ERROR -> {

            }
            case LOGIN -> {
                authService.login(socket, clientMessage.getUsername());
                printWriter.println("Authenticated");
                break;
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
