package org.legenkiy.net;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.connection.ConnectionsManager;
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


    private final ConnectionsManager connectionsManager;


    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(1010)) {
            LOGGER.info("SERVER WAS STARTED ON PORT 1010");
            while (true) {
                Socket socket = serverSocket.accept();
                handleConnection(socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void handleConnection(Socket clientSocket) {
        new Thread(() -> {
            try {
                connect(clientSocket);
                Thread thread = new Thread(new ClientHandler(clientSocket, this.connectionsManager));
                thread.start();

            } catch (IOException e) {
                String message = "CONNECTION LOST WITH SOCKET " + clientSocket.getInetAddress() + ":" + clientSocket.getPort();
                LOGGER.warn(message);
                System.out.println(message);
            }
        }
        ).start();
    }


    private void connect(Socket clientSocket) throws ConnectException {
        if (clientSocket.isConnected()) {
            ActiveConnection activeConnection = ActiveConnection.builder()
                    .connectedAt(LocalDateTime.now())
                    .socket(clientSocket.getInetAddress() + ":" + clientSocket.getPort()).build();
            connectionsManager.addNewConnection(activeConnection);
            LOGGER.info("CONNECTED {}", clientSocket);
        } else {
            LOGGER.warn("FAILED CONNECTION {}", clientSocket);
            throw new ConnectException("Failed connections");
        }
    }


}
