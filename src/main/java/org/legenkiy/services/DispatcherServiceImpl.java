package org.legenkiy.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.ChatService;
import org.legenkiy.api.service.DispatcherService;
import org.legenkiy.mapper.MessageMapper;
import org.legenkiy.protocol.message.ClientMessage;
import org.legenkiy.protocol.enums.MessageType;
import org.legenkiy.protocol.message.ServerMessage;
import org.springframework.stereotype.Service;


import java.io.PrintWriter;
import java.net.Socket;

@Service
@RequiredArgsConstructor
public class DispatcherServiceImpl implements DispatcherService {

    private final static Logger LOGGER = LogManager.getLogger(DispatcherServiceImpl.class);

    private final ChatService chatService;
    private final AuthService authService;
    private final MessageMapper messageMapper;

    @Override
    public void handle(ClientMessage clientMessage, Socket socket, PrintWriter printWriter) throws JsonProcessingException {
        LOGGER.info("Handling request from {}", socket.getRemoteSocketAddress());
        MessageType messageType = clientMessage.getMessageType();
        switch (messageType) {
            case HELLO -> {

            }
            case OK -> {

            }
            case PM -> {
                System.out.println("process");
                chatService.processMessage(clientMessage, socket, printWriter);
            }
            case MSG -> {

            }
            case WHO -> {

            }
            case ERROR -> {

            }
            case LOGIN -> {
                authService.login(socket, clientMessage.getAuthDto());
                printWriter.println(messageMapper.encode(ServerMessage.ok("authenticated : " + clientMessage.getAuthDto().getUsername())));
            }
            case REGISTER -> {
                authService.register(socket, clientMessage.getAuthDto());
                printWriter.println(messageMapper.encode(ServerMessage.ok("registered : " + clientMessage.getAuthDto().getUsername())));
            }
            default -> {
                printWriter.println("[UNKNOWN COMMAND]");
            }
        }
    }
}
