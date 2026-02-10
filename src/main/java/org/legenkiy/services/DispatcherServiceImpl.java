package org.legenkiy.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.ChatService;
import org.legenkiy.api.service.DispatcherService;
import org.legenkiy.mapper.MessageMapper;
import org.legenkiy.protocol.dtos.AuthDto;
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
                String username = clientMessage.getUsername();
                System.out.println(username);
                authService.login(socket, username);
                AuthDto authDto = new AuthDto();
                authDto.setUsername(username);
                ServerMessage serverMessage = ServerMessage.ok(messageMapper.encode(authDto));
                printWriter.println(messageMapper.encode(serverMessage));
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
