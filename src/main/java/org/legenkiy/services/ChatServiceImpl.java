package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.ChatService;
import org.legenkiy.connection.ConnectionsManager;
import org.legenkiy.mapper.MessageMapper;
import org.legenkiy.protocol.ClientMessage;
import org.legenkiy.protocol.ServerMessage;
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
                LOGGER.info("SOCKET AUTHENTICATE {}", clientSocket);
                String recipientUsername = clientMessage.getTo();
                ServerMessage serverMessage = ServerMessage.chat(clientMessage.getFrom(), clientMessage.getContent());
                Socket recipientSocket = connectionsManager.findConnectionByUsername(recipientUsername).getSocket();
                try (PrintWriter recipientPrintWriter = new PrintWriter(recipientSocket.getOutputStream(), true)){
                    recipientPrintWriter.println(mapper.encode(serverMessage));
                    LOGGER.info("MESSAGE ENCODED {}", serverMessage);
                }catch (Exception e){
                    clientPrintWriter.println("FAILED TO SENT : " + e);
                }
            }else {
                clientPrintWriter.println("NEED AUTHENTICATION");
            }

    }


        @Override
        public void processMessage (ServerMessage serverMessage){

        }

        @Override
        public void processMessage (ClientMessage clientMessage, ServerMessage serverMessage){

        }
}

