package org.legenkiy.api.service;


import org.legenkiy.protocol.message.Envelope;


import java.net.Socket;

public interface ChatService {

    void handleChatRequest(Socket clientSocket, Envelope envelope);

    void acceptChat(Socket clientSocketThatAccepted, Envelope envelope);

    void rejectChat(Socket clientSocketThatAccepted, Envelope envelope);

    void endChat(Socket clientThatSentRequestForEnd, Envelope envelope);

    void processMessage() ;


}
