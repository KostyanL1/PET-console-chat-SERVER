package org.legenkiy.net;


import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.net.Socket;
import java.net.SocketException;


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
        while (true) {
            try {
                while (true) {
                    BufferedReader bufferedReader = connectionsManagerImpl.findConnectionBySocket(socket).getBufferedReader();
                    String message;
                    while ((message = bufferedReader.readLine()) != null) {
                        System.out.println("waiting command for " + socket.getRemoteSocketAddress());
                        ClientMessage clientMessage = mapper.decode(message, ClientMessage.class);
                        dispatcherService.handle(clientMessage, socket, connectionsManagerImpl.findConnectionBySocket(socket).getPrintWriter());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
        public void closeResource () {
            try {
                socket.close();
                connectionsManagerImpl.removeConnection(socket);
                LOGGER.info("Socket closed {}", socket);
            } catch (IOException e) {
                LOGGER.info("Failed to close {}", socket);
            }
        }
    }
