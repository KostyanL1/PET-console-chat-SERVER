package org.legenkiy.net;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.connection.ConnectionsManagerImpl;
import org.legenkiy.factory.ClientHandlerFactory;
import org.legenkiy.models.ActiveConnection;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class TcpServer implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(TcpServer.class);


    private final ConnectionsManagerImpl connectionsManagerImpl;
    private final ClientHandlerFactory clientHandlerFactory;


    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(1010)) {
            LOGGER.info("Server started on port - {}", 1010);
            while (true) {
                Socket socket = serverSocket.accept();
                handleConnection(socket);
                LOGGER.info("Connection created {}", socket.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void handleConnection(Socket clientSocket) throws ConnectException {
            try {
                connect(clientSocket);
                Thread thread = new Thread(clientHandlerFactory.create(clientSocket));
                thread.start();
            } catch (IOException e) {
                LOGGER.info("Connection lost with socket {}", clientSocket.getRemoteSocketAddress());
                throw new ConnectException("Connection lost");
            }
    }


    private void connect(Socket clientSocket) throws ConnectException {
        if (clientSocket.isConnected()) {
            ActiveConnection activeConnection = ActiveConnection.builder()
                    .connectedAt(LocalDateTime.now())
                    .socket(clientSocket).build();
            connectionsManagerImpl.addNewConnection(activeConnection);
            LOGGER.info("Connected socket {}", clientSocket);
        } else {
            LOGGER.warn("Connection failed with socket {}", clientSocket);
            throw new ConnectException("Connection failed");
        }
    }


}
