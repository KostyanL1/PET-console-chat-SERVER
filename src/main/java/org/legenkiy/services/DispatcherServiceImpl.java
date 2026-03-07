package org.legenkiy.services;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.ChatService;
import org.legenkiy.api.service.DispatcherService;
import org.legenkiy.api.service.SenderService;
import org.legenkiy.mapper.MessageMapper;
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
        switch (envelope.getType()) {
            case HELLO -> {
                try {
                    authService.handshake(socket, envelope);
                } catch (Exception e) {
                    handleError(socket, e);
                }
            }
            case AUTH_REGISTER -> {
                try {
                    authService.register(socket, envelope);
                } catch (Exception e) {
                    handleError(socket, e);
                }
            }
            case AUTH_LOGIN -> {
                try {
                    authService.login(socket, envelope);
                } catch (Exception e) {
                    handleError(socket, e);
                }
            }
            case CHAT_REQUEST -> {

            }
            default -> {
                handleError(socket, new Exception("Unknown request"));
            }
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
