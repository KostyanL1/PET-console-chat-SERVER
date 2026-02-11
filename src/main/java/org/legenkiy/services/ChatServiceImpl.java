package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.ChatService;
import org.legenkiy.connection.ConnectionsManager;
import org.legenkiy.mapper.MessageMapper;
import org.legenkiy.protocol.message.ClientMessage;
import org.legenkiy.protocol.message.ServerMessage;
import org.springframework.stereotype.Service;


import java.io.PrintWriter;
import java.net.Socket;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final static Logger LOGGER = LogManager.getLogger(ChatServiceImpl.class);

    private final ConnectionsManager connectionsManager;
    private final MessageMapper mapper;
    private final AuthService authService;

    @Override
    public void processMessage(ClientMessage clientMessage, Socket clientSocket, PrintWriter clientPrintWriter) {
            if (authService.isAuthenticate(clientSocket)) {
                Socket recipientSocket = connectionsManager.findConnectionByUsername(clientMessage.getTo()).getSocket();
                try (PrintWriter recipientPrintWriter = new PrintWriter(recipientSocket.getOutputStream(), true)){
                    recipientPrintWriter.println(mapper.encode(
                            ServerMessage
                                    .chat(
                                            clientMessage.getFrom(),
                                            clientMessage.getContent()
                                    )
                    ));
                }catch (Exception e){
                    LOGGER.info("Sending failed. Exception {}", e.getMessage());
                    clientPrintWriter.println("Sending failed");
                }
            }else {
                LOGGER.info("Sending failed. Authentication needed for client {}", clientSocket.getRemoteSocketAddress());
                clientPrintWriter.println("Authentication needed");
            }

    }


        @Override
        public void processMessage (ServerMessage serverMessage){

        }

        @Override
        public void processMessage (ClientMessage clientMessage, ServerMessage serverMessage){

        }
}

