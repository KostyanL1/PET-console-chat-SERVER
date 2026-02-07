package org.legenkiy.factory;


import lombok.RequiredArgsConstructor;
import org.legenkiy.net.ClientHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.net.Socket;

@Component
@RequiredArgsConstructor
public class ClientHandlerFactory {

    private final ObjectProvider<ClientHandler> clientHandlerObjectProvider;

    public ClientHandler create(Socket socket){
        ClientHandler clientHandler = clientHandlerObjectProvider.getObject();
        clientHandler.init(socket);
        return clientHandler;
    }

}
