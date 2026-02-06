package org.legenkiy.net;



import lombok.AllArgsConstructor;
import org.legenkiy.api.service.DispatcherService;
import org.legenkiy.mapper.MessageMapper;
import org.legenkiy.protocol.ClientMessage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


@Component
@Scope(scopeName = "prototype")
@AllArgsConstructor
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final MessageMapper mapper;
    private final DispatcherService dispatcherService;


    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()))) {
            String messageJson = bufferedReader.readLine();
            ClientMessage clientMessage = mapper.decode(messageJson, ClientMessage.class);
            dispatcherService.handle(clientMessage);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
