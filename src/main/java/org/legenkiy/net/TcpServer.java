package org.legenkiy.net;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.connection.ConnectionsManagerImpl;
import org.legenkiy.exceptions.ConnectionException;
import org.legenkiy.factory.ClientHandlerFactory;
import org.legenkiy.models.ActiveConnection;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
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
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                LOGGER.info("Accepted connection {}", socket.getRemoteSocketAddress());
                handleConnection(socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleConnection(Socket clientSocket) {
        try {
            ActiveConnection activeConnection = ActiveConnection.builder()
                    .connectedAt(LocalDateTime.now())
                    .socket(clientSocket)
                    .printWriter(new PrintWriter(clientSocket.getOutputStream(), true))
                    .build();

            connectionsManagerImpl.create(clientSocket, activeConnection);
            Thread thread = new Thread(clientHandlerFactory.create(clientSocket));
            thread.start();
        } catch (IOException e) {
            LOGGER.info("Connection lost with socket {}", clientSocket.getRemoteSocketAddress());
            close(clientSocket);
            throw new ConnectionException("Connection lost: " + e.getMessage());
        }
    }

    public void close(Socket socket) {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            LOGGER.error("Socket closing failed, {}", socket.getRemoteSocketAddress());
            throw new RuntimeException(e);
        }
    }

}
