package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.ChatService;
import org.legenkiy.api.service.SenderService;
import org.legenkiy.connection.ConnectionsManagerImpl;
import org.legenkiy.context.ChatsContext;
import org.legenkiy.context.RequestContext;
import org.legenkiy.models.ActiveConnection;
import org.legenkiy.models.Chat;
import org.legenkiy.protocol.dtos.*;
import org.legenkiy.protocol.enums.MessageType;
import org.legenkiy.protocol.message.Envelope;
import org.springframework.stereotype.Service;

import java.net.Socket;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final static Logger LOGGER = LogManager.getLogger(ChatServiceImpl.class);

    private final ConnectionsManagerImpl connectionsManagerImpl;
    private final AuthService authService;
    private final SenderService senderService;


    @Override
    public void handleChatRequest(Socket clientSocket, Envelope envelope) {
        try {
            ChatRequestPayload chatRequestPayload = (envelope.getPayload() instanceof ChatRequestPayload) ? (ChatRequestPayload) envelope.getPayload() : null;

            if (chatRequestPayload != null && authService.isAuthenticated(clientSocket)) {

                String senderUsername = connectionsManagerImpl.findConnectionBySocket(clientSocket).getUsername();
                String recipientUsername = chatRequestPayload.getTo();
                ActiveConnection recipientActiveConnection = connectionsManagerImpl.findConnectionByUsername(recipientUsername);

                if (authService.isAuthenticated(recipientActiveConnection.getSocket())) {
                    ChatIncomingPayload chatIncomingPayload = RequestContext.create(senderUsername);
                    Envelope envelopeForSend = new Envelope();
                    envelopeForSend.setType(MessageType.CHAT_REQUEST);
                    envelopeForSend.setPayload(chatIncomingPayload);
                    senderService.send(recipientActiveConnection.getSocket(), envelopeForSend);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void acceptChat(Socket clientSocketThatAccepted, Envelope envelope) {
        try {
            ChatAcceptPayload chatAcceptPayload = (envelope.getPayload() instanceof ChatAcceptPayload) ? (ChatAcceptPayload) envelope.getPayload() : null;

            if (chatAcceptPayload != null && RequestContext.isExist(chatAcceptPayload.getRequestId())) {


                String firstUser = RequestContext.findById(chatAcceptPayload.getRequestId()).getFrom();
                String secondUser = connectionsManagerImpl.findConnectionBySocket(clientSocketThatAccepted).getUsername();
                Socket clientSocketThatSentRequest = connectionsManagerImpl.findConnectionByUsername(firstUser).getSocket();

                RequestContext.removeById(chatAcceptPayload.getRequestId());

                if (authService.isAuthenticated(clientSocketThatSentRequest) && authService.isAuthenticated(clientSocketThatSentRequest)) {
                    ChatsContext.create(firstUser, secondUser);

                    ChatStartedPayload chatStartedPayload = new ChatStartedPayload();
                    chatStartedPayload.setA(firstUser);
                    chatStartedPayload.setB(secondUser);

                    Envelope envelopeForBothUsers = new Envelope();
                    envelopeForBothUsers.setType(MessageType.CHAT_STARTED);
                    envelopeForBothUsers.setPayload(chatStartedPayload);

                    senderService.send(clientSocketThatAccepted, envelopeForBothUsers);
                    senderService.send(clientSocketThatSentRequest, envelopeForBothUsers);

                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void rejectChat(Socket clientSocketThatAccepted, Envelope envelope) {
        try {
            ChatRejectPayload chatRejectPayload = (envelope.getPayload() instanceof ChatRejectPayload) ? (ChatRejectPayload) envelope.getPayload() : null;

            if (chatRejectPayload != null && RequestContext.isExist(chatRejectPayload.getRequestId())) {
                String firstUser = RequestContext.findById(chatRejectPayload.getRequestId()).getFrom();
                Socket clientSocketThatSentRequest = connectionsManagerImpl.findConnectionByUsername(firstUser).getSocket();

                Envelope envelopeForUserThatSentRequest = new Envelope();
                envelopeForUserThatSentRequest.setType(MessageType.CHAT_REJECT);
                envelopeForUserThatSentRequest.setPayload(chatRejectPayload);

                senderService.send(clientSocketThatSentRequest, envelopeForUserThatSentRequest);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void endChat(Socket clientThatSentRequestForEnd, Envelope envelope) {
        try {
            ChatEndPayload chatEndPayload = ((envelope.getPayload()) instanceof ChatEndPayload) ? (ChatEndPayload) envelope.getPayload() : null;
            if (chatEndPayload != null) {
                Long chatId = chatEndPayload.getId();
                if (ChatsContext.isExist(chatId)) {
                    Chat chat = ChatsContext.findById(chatId);
                    Socket firstUserSocket = connectionsManagerImpl.findConnectionByUsername(chat.getMembers().get(0).getUsername()).getSocket();
                    Socket secondUserSocket = connectionsManagerImpl.findConnectionByUsername(chat.getMembers().get(1).getUsername()).getSocket();
                    if (clientThatSentRequestForEnd.equals(firstUserSocket) || clientThatSentRequestForEnd.equals(secondUserSocket)) {
                        ChatsContext.removeById(chatEndPayload.getId());
                        Envelope envelopeForBothUsers = new Envelope();
                        envelopeForBothUsers.setType(MessageType.CHAT_END);

                        senderService.send(firstUserSocket, envelopeForBothUsers);
                        senderService.send(secondUserSocket, envelopeForBothUsers);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processMessage(Socket senderSocket, Envelope envelope) {
        try {
            String senderUsername = connectionsManagerImpl.findConnectionBySocket(senderSocket).getUsername();
            ChatMessagePayload chatMessagePayloadFromSender = ((envelope.getPayload() instanceof ChatMessagePayload)) ? (ChatMessagePayload) envelope.getPayload() : null;
            if (chatMessagePayloadFromSender != null) {
                Long chatId = chatMessagePayloadFromSender.getChatId();
                if (ChatsContext.isExist(chatId)) {
                    Chat chat = ChatsContext.findById(chatId);
                    String aUsername = chat.getMembers().get(0).getUsername();
                    String bUsername = chat.getMembers().get(1).getUsername();
                    Socket recipientSocket;
                    if (aUsername.equals(senderUsername)) {
                        recipientSocket = connectionsManagerImpl.findConnectionByUsername(bUsername).getSocket();
                    } else {
                        recipientSocket = connectionsManagerImpl.findConnectionByUsername(aUsername).getSocket();
                    }

                    ChatMessagePayload chatMessagePayloadForRecipient = new ChatMessagePayload();
                    chatMessagePayloadForRecipient.setChatId(chatId);
                    chatMessagePayloadForRecipient.setText(chatMessagePayloadFromSender.getText());

                    Envelope envelopeForRecipient = new Envelope();
                    envelopeForRecipient.setType(MessageType.CHAT_MSG);
                    envelopeForRecipient.setPayload(chatMessagePayloadForRecipient);

                    senderService.send(recipientSocket, envelope);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}

