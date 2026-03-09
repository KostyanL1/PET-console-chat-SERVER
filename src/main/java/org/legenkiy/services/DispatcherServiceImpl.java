package org.legenkiy.services;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.ChatService;
import org.legenkiy.api.service.DispatcherService;
import org.legenkiy.api.service.SenderService;
import org.legenkiy.protocol.enums.MessageType;
import org.legenkiy.protocol.message.Envelope;
import org.springframework.stereotype.Service;


import java.io.PrintWriter;
import java.net.Socket;

@Service
@RequiredArgsConstructor
public class DispatcherServiceImpl implements DispatcherService {

    private final static Logger LOGGER = LogManager.getLogger(DispatcherServiceImpl.class);

    private final AuthService authService;
    private final SenderService senderService;
    private final ChatService chatService;

    @Override
    public void handle(Envelope envelope, Socket socket, PrintWriter printWriter) {
        try {
            switch (envelope.getType()) {
                case HELLO -> authService.handshake(socket, envelope);

                case AUTH_REGISTER -> authService.register(socket, envelope);

                case AUTH_LOGIN -> authService.login(socket, envelope);

                case CHAT_REQUEST -> chatService.handleChatRequest(socket, envelope);

                case CHAT_ACCEPT -> chatService.acceptChat(socket, envelope);

                case CHAT_REJECT -> chatService.rejectChat(socket, envelope);

                case CHAT_END -> chatService.endChat(socket, envelope);

                case CHAT_MSG -> chatService.processMessage(socket, envelope);

                default -> handleError(socket, new Exception("Unknown request"));

            }
        } catch (Exception e) {
            handleError(socket, e);
        }

    }


    public void handleError(Socket socket, Exception e) {
        Envelope env = new Envelope();
        env.setType(MessageType.ERROR);
        env.setPayload(e.getMessage());
        senderService.send(socket, env);
        LOGGER.error(e.getMessage());
    }

}
