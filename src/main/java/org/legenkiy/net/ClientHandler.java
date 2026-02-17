package org.legenkiy.net;


import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.DispatcherService;
import org.legenkiy.connection.ConnectionsManagerImpl;
import org.legenkiy.mapper.MessageMapper;
import org.legenkiy.protocol.message.ClientMessage;
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
    private final ConnectionsManagerImpl connectionsManagerImpl;

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
                String message;
                if ((message = bufferedReader.readLine()) != null){
                    ClientMessage clientMessage = mapper.decode(message, ClientMessage.class);
                    dispatcherService.handle(clientMessage, socket, printWriter);
                }
            }
        } catch (IOException exception) {
            System.out.println(exception);
            try {
                socket.close();
                connectionsManagerImpl.removeConnection(socket);
                LOGGER.info("Socket closed {}", socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
