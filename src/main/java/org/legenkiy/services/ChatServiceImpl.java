package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.connection.ConnectionManager;
import org.legenkiy.api.service.AuthService;
import org.legenkiy.api.service.ChatService;
import org.legenkiy.api.service.SenderService;

import org.legenkiy.context.ChatsContext;
import org.legenkiy.context.RequestContext;
import org.legenkiy.exceptions.ObjectNotFoundException;
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

    private final ConnectionManager connectionManager;
    private final AuthService authService;
    private final SenderService senderService;
    private final RequestContext requestContext;
    private final ChatsContext chatsContext;


    @Override
    public void handleChatRequest(Socket clientSocket, Envelope envelope) {
        try {
            LOGGER.info("Chat request handling from {}", clientSocket.getRemoteSocketAddress());
            ChatRequestPayload chatRequestPayload = (envelope.getPayload() instanceof ChatRequestPayload) ? (ChatRequestPayload) envelope.getPayload() : null;

            if (chatRequestPayload != null && authService.isAuthenticated(clientSocket)) {

                String senderUsername = connectionManager.findConnectionBySocket(clientSocket).getUsername();
                String recipientUsername = chatRequestPayload.getTo();
                ActiveConnection recipientActiveConnection = connectionManager.findConnectionByUsername(recipientUsername);

                if (authService.isAuthenticated(recipientActiveConnection.getSocket())) {
                    LOGGER.info("Recipient authenticated {}", recipientActiveConnection.getSocket().getRemoteSocketAddress());
                    ChatIncomingPayload chatIncomingPayload = requestContext.create(senderUsername);
                    Envelope envelopeForSend = new Envelope();
                    envelopeForSend.setType(MessageType.CHAT_INCOMING);
                    envelopeForSend.setPayload(chatIncomingPayload);
                    senderService.send(recipientActiveConnection.getSocket(), envelopeForSend);
                }else {
                    throw new RuntimeException("Recipient offline");
                }
            }else {
                throw new IllegalArgumentException("Incorrect payload");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void acceptChat(Socket clientSocketThatAccepted, Envelope envelope) {
        try {
            LOGGER.info("Chat request accepted from {}", clientSocketThatAccepted.getRemoteSocketAddress());
            ChatAcceptPayload chatAcceptPayload = (envelope.getPayload() instanceof ChatAcceptPayload) ? (ChatAcceptPayload) envelope.getPayload() : null;

            if (chatAcceptPayload != null && requestContext.isExist(chatAcceptPayload.getRequestId())) {


                String firstUser = requestContext.findById(chatAcceptPayload.getRequestId()).getFrom();
                String secondUser = connectionManager.findConnectionBySocket(clientSocketThatAccepted).getUsername();
                Socket clientSocketThatSentRequest = connectionManager.findConnectionByUsername(firstUser).getSocket();
                System.out.println(clientSocketThatSentRequest.getRemoteSocketAddress());
                System.out.println(clientSocketThatAccepted.getRemoteSocketAddress());

                requestContext.removeById(chatAcceptPayload.getRequestId());

                if (authService.isAuthenticated(clientSocketThatSentRequest)) {
                    Chat chat = chatsContext.create(firstUser, secondUser);

                    ChatStartedPayload chatStartedPayload = new ChatStartedPayload();
                    chatStartedPayload.setChatId(chat.getId());
                    chatStartedPayload.setA(firstUser);
                    chatStartedPayload.setB(secondUser);

                    Envelope envelopeForBothUsers = new Envelope();
                    envelopeForBothUsers.setType(MessageType.CHAT_STARTED);
                    envelopeForBothUsers.setPayload(chatStartedPayload);

                    senderService.send(clientSocketThatAccepted, envelopeForBothUsers);
                    senderService.send(clientSocketThatSentRequest, envelopeForBothUsers);

                }else {
                    throw new RuntimeException("Client that sent request offline");
                }
            }else {
                throw new IllegalArgumentException("Incorrect payload");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void rejectChat(Socket clientSocketThatAccepted, Envelope envelope) {
        try {
            LOGGER.info("Chat request rejected from {}", clientSocketThatAccepted.getRemoteSocketAddress());
            ChatRejectPayload chatRejectPayload = (envelope.getPayload() instanceof ChatRejectPayload) ? (ChatRejectPayload) envelope.getPayload() : null;

            if (chatRejectPayload != null && requestContext.isExist(chatRejectPayload.getRequestId())) {
                String firstUser = requestContext.findById(chatRejectPayload.getRequestId()).getFrom();
                Socket clientSocketThatSentRequest = connectionManager.findConnectionByUsername(firstUser).getSocket();

                Envelope envelopeForUserThatSentRequest = new Envelope();
                envelopeForUserThatSentRequest.setType(MessageType.CHAT_REJECT);
                envelopeForUserThatSentRequest.setPayload(chatRejectPayload);

                senderService.send(clientSocketThatSentRequest, envelopeForUserThatSentRequest);
            }else {
                throw new IllegalArgumentException("Incorrect payload");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void endChat(Socket clientThatSentRequestForEnd, Envelope envelope) {
        try {
            LOGGER.info("Chat ended from {}", clientThatSentRequestForEnd.getRemoteSocketAddress());
            ChatEndPayload chatEndPayload = ((envelope.getPayload()) instanceof ChatEndPayload) ? (ChatEndPayload) envelope.getPayload() : null;
            if (chatEndPayload != null) {
                Long chatId = chatEndPayload.getId();
                if (chatsContext.isExist(chatId)) {
                    Chat chat = chatsContext.findById(chatId);
                    Socket firstUserSocket = connectionManager.findConnectionByUsername(chat.getMembers().get(0).getUsername()).getSocket();
                    Socket secondUserSocket = connectionManager.findConnectionByUsername(chat.getMembers().get(1).getUsername()).getSocket();
                    if (clientThatSentRequestForEnd.equals(firstUserSocket) || clientThatSentRequestForEnd.equals(secondUserSocket)) {
                        chatsContext.removeById(chatEndPayload.getId());
                        Envelope envelopeForBothUsers = new Envelope();
                        envelopeForBothUsers.setType(MessageType.CHAT_END);

                        senderService.send(firstUserSocket, envelopeForBothUsers);
                        senderService.send(secondUserSocket, envelopeForBothUsers);
                    }else {
                        throw new RuntimeException("Error in finding recipient socket");
                    }
                }else {
                    throw new ObjectNotFoundException("Chat not found");
                }
            }else {
                throw new IllegalArgumentException("Incorrect payload");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processMessage(Socket senderSocket, Envelope envelope) {
        try {
            String senderUsername = connectionManager.findConnectionBySocket(senderSocket).getUsername();
            ChatMessagePayload chatMessagePayloadFromSender = ((envelope.getPayload() instanceof ChatMessagePayload)) ? (ChatMessagePayload) envelope.getPayload() : null;
            if (chatMessagePayloadFromSender != null) {
                Long chatId = chatMessagePayloadFromSender.getChatId();
                if (chatsContext.isExist(chatId)) {
                    Chat chat = chatsContext.findById(chatId);
                    String aUsername = chat.getMembers().get(0).getUsername();
                    String bUsername = chat.getMembers().get(1).getUsername();
                    Socket recipientSocket;
                    if (aUsername.equals(senderUsername)) {
                        recipientSocket = connectionManager.findConnectionByUsername(bUsername).getSocket();
                    } else {
                        recipientSocket = connectionManager.findConnectionByUsername(aUsername).getSocket();
                    }

                    ChatMessagePayload chatMessagePayloadForRecipient = new ChatMessagePayload();
                    chatMessagePayloadForRecipient.setChatId(chatId);
                    chatMessagePayloadForRecipient.setText(chatMessagePayloadFromSender.getText());

                    Envelope envelopeForRecipient = new Envelope();
                    envelopeForRecipient.setType(MessageType.CHAT_MSG);
                    envelopeForRecipient.setPayload(chatMessagePayloadForRecipient);

                    senderService.send(recipientSocket, envelopeForRecipient);
                }
            }else {
                throw new IllegalArgumentException("Incorrect payload");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

}

