package org.legenkiy.net;


import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.DispatcherService;
import org.legenkiy.connection.ConnectionsManager;
import org.legenkiy.mapper.MessageMapper;
import org.legenkiy.protocol.ClientMessage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



@Component
@Scope(scopeName = "prototype")
@RequiredArgsConstructor
public class ClientHandler implements Runnable {

    private final static Logger LOGGER = LogManager.getLogger(ClientHandler.class);

    private final MessageMapper mapper;
    private final DispatcherService dispatcherService;
    private final ConnectionsManager connectionsManager;

    private Socket socket;

    public void init(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true)
                )
        {
            while (true) {
                System.out.println("waiting command for " + socket.getRemoteSocketAddress());
                String messageJson = bufferedReader.readLine();
                ClientMessage clientMessage = mapper.decode(messageJson, ClientMessage.class);
                dispatcherService.handle(clientMessage, socket, printWriter);

            }
        } catch (IOException exception) {
            try {
                socket.close();
                connectionsManager.removeConnection(socket);
                LOGGER.info("Socket closed {}", socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
