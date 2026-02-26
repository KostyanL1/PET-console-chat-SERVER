package org.legenkiy.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.ChatService;
import org.legenkiy.connection.ConnectionsManagerImpl;
import org.legenkiy.mapper.MessageMapper;
import org.legenkiy.protocol.message.ClientMessage;
import org.legenkiy.protocol.message.ServerMessage;
import org.springframework.stereotype.Service;

import java.net.Socket;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final static Logger LOGGER = LogManager.getLogger(ChatServiceImpl.class);

    private final ConnectionsManagerImpl connectionsManagerImpl;
    private final MessageMapper mapper;
    private final AuthService authService;


    @Override
    public void handleChatRequest(ClientMessage clientMessage, Socket clientSocket) {
        try {
            if (authService.isAuthenticate(clientSocket)) {
                String senderUsername = clientMessage.getFrom();
                connectionsManagerImpl.findConnectionByUsername(senderUsername).getPrintWriter().println(
                        mapper.encode(
                                ServerMessage.requestChat(senderUsername)
                        )
                );
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void processMessage(ClientMessage clientMessage, Socket clientSocket) throws JsonProcessingException {
        if (authService.isAuthenticate(clientSocket)) {
            connectionsManagerImpl.findConnectionByUsername(clientMessage.getTo()).getPrintWriter().println(
                    mapper.encode(
                            ServerMessage
                                    .chat(
                                            clientMessage.getFrom(),
                                            clientMessage.getContent()
                                    )
                    )
            );
        } else {
            LOGGER.info("Sending failed. Authentication needed for client {}", clientSocket.getRemoteSocketAddress());
            connectionsManagerImpl.findConnectionBySocket(
                            clientSocket)
                    .getPrintWriter().println(ServerMessage.error("Authentication needed"));
        }
    }


    @Override
    public void processMessage(ServerMessage serverMessage) {

    }

    @Override
    public void processMessage(ClientMessage clientMessage, ServerMessage serverMessage) {

    }
}

