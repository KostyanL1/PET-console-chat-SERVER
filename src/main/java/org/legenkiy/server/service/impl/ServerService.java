package org.legenkiy.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.model.ActiveConnection;
import org.legenkiy.server.connection.ConnectionsManager;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ServerService implements Runnable {


    private static final Logger LOGGER = LogManager.getLogger(ServerService.class);
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
            if (clientSocket.isConnected()) {
                ActiveConnection activeConnection = ActiveConnection.builder()
                        .connectedAt(LocalDateTime.now())
                        .socket(clientSocket.getInetAddress() + ":" + clientSocket.getPort()).build();
                connectionsManager.addNewConnection(activeConnection);
            }

            try (
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                printWriter.println("CONNECTED");
                while (true) {
                    String message;
                    if ((message = bufferedReader.readLine()) != null) {
                        switch (message) {
                            case "/exit": {
                                clientSocket.close();
                                connectionsManager.removeConnection(clientSocket);
                                return;
                            }
                            default: {
                                System.out.println(message);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                String message = "CONNECTION LOST WITH SOCKET " + clientSocket.getInetAddress() + ":" + clientSocket.getPort();
                LOGGER.warn(message);
                System.out.println(message);
            }
        }
        ).start();

    }


}
