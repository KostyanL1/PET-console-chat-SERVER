package org.legenkiy.connection;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.connection.ConnectionManager;
import org.legenkiy.enums.ClientState;
import org.legenkiy.exceptions.AlreadyConnectedException;
import org.legenkiy.exceptions.ObjectNotFoundException;
import org.legenkiy.models.ActiveConnection;
import org.legenkiy.services.SenderServiceImpl;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Component
@RequiredArgsConstructor
public class ConnectionsManagerImpl implements ConnectionManager {

    private final Logger LOGGER = LogManager.getLogger(ConnectionsManagerImpl.class);
    private final ConcurrentHashMap<Socket, ActiveConnection> activeConnectionBySocketMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ActiveConnection> activeConnectionByUsernameMap = new ConcurrentHashMap<>();

    private final AtomicLong index = new AtomicLong(0);

    private final SenderServiceImpl senderServiceImpl;


    @Override
    public void create(Socket clientSocket, ActiveConnection activeConnection) throws AlreadyConnectedException {
        if (!isConnected(clientSocket)) {
            activeConnection.setId(index.incrementAndGet());
            activeConnection.setClientState(ClientState.NEW);
            this.activeConnectionBySocketMap.put(clientSocket, activeConnection);
            LOGGER.info("Connection created.");
        } else {
            String message = "Connection with socket: " + senderServiceImpl + " already connected!";
            LOGGER.info(message);
            throw new AlreadyConnectedException(message);
        }
    }

    @Override
    public boolean isConnected(Socket socket) {
        return this.activeConnectionBySocketMap
                .keySet()
                .stream()
                .anyMatch(connectedSocket -> connectedSocket.equals(socket) && !connectedSocket.isClosed());
    }

    @Override
    public ActiveConnection findConnectionByUsername(String username) {
        ActiveConnection connection = activeConnectionByUsernameMap.get(username);
        if (connection == null) {
            throw new ObjectNotFoundException("Connection with username: " + username + " not found!");
        }
        return connection;
    }


    @Override
    public synchronized ActiveConnection findConnectionBySocket(Socket socket) {
        ActiveConnection connection = activeConnectionBySocketMap.get(socket);
        if (connection == null) {
            throw new ObjectNotFoundException("Connection with socket: " + socket + " not found!");
        }
        return connection;
    }

    @Override
    public synchronized void authenticate(Socket socket, String username) {
        ActiveConnection connection = findConnectionBySocket(socket);
        connection.setUsername(username);
        connection.setClientState(ClientState.AUTHENTICATED);
        this.activeConnectionByUsernameMap.put(username, connection);
        LOGGER.info("Username {} is authenticated", username);
    }

    @Override
    public void removeConnectionBySocket(Socket socket) {
        ActiveConnection activeConnection = findConnectionBySocket(socket);
        this.activeConnectionBySocketMap.remove(socket);
        String username = activeConnection.getUsername();
        if (username != null) {
            this.activeConnectionByUsernameMap.remove(username);
        }
        LOGGER.info("Connection {} removed", socket.getRemoteSocketAddress());
    }


}








