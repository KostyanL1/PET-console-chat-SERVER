package org.legenkiy.connection;


import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.enums.ClientState;
import org.legenkiy.exceptions.AlreadyConnectedException;
import org.legenkiy.exceptions.ObjectNotFoundException;
import org.legenkiy.models.ActiveConnection;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class ConnectionsManager {

    private List<ActiveConnection> activeConnectionList = new CopyOnWriteArrayList<>();
    private AtomicLong index = new AtomicLong(0);
    private final Logger LOGGER = LogManager.getLogger(ConnectionsManager.class);

    public ActiveConnection addNewConnection(ActiveConnection activeConnection) {
        if (!isAlreadyConnected(activeConnection)) {
            activeConnection.setId(index.incrementAndGet());
            activeConnection.setClientState(ClientState.NEW);
            this.activeConnectionList.add(activeConnection);
            LOGGER.info("NEW CONNECTION CREATED : {}", activeConnection);
            activeConnectionList.forEach(System.out::println);
            return activeConnection;
        }
        LOGGER.warn("CONNECTION ALREADY EXIST : {}", activeConnection);
        throw new AlreadyConnectedException("Connection already exist");
    }

    public ActiveConnection findConnectionByUsername(String username) {
        return this.activeConnectionList.stream().filter(
                activeConnection ->
                        activeConnection.getUsername().equals(username)
        ).findFirst().orElseThrow(() -> new RuntimeException("CONNECTION NOT FOUND"));
    }

    private synchronized boolean isAlreadyConnected(ActiveConnection activeConnection) {
        Optional<ActiveConnection> activeConnectionOptional = this.activeConnectionList.stream()
                .filter(connection ->
                        connection.getId().equals(activeConnection.getId())).findFirst();
        return activeConnectionOptional.isPresent();
    }

    private synchronized ActiveConnection findConnectionById(Long id) {
        return this.activeConnectionList.stream().filter(connection ->
                connection.getId().equals(id)).findFirst().orElseThrow(
                () -> {
                    LOGGER.warn("CONNECTION WITH ID {} NOT FOUND", id);
                    throw new ObjectNotFoundException("CONNECTION WITH ID " + id + " NOT FOUND");
                }
        );
    }

    public void authenticate(Socket socket, String username) {
        ActiveConnection connection = findConnectionBySocket(socket);
        connection.setUsername(username);
        connection.setClientState(ClientState.AUTHENTICATED);
        LOGGER.info("AUTHENTICATED {}", username);
    }

    public synchronized ActiveConnection findConnectionBySocket(Socket socket) {
            return this.activeConnectionList.stream().filter(
                    activeConnection -> {
                        String socketAsConnection = activeConnection.getSocket().getRemoteSocketAddress().toString();
                        String socketAsArgument = socket.getRemoteSocketAddress().toString();
                        LOGGER.info("socketAsConnection {}", socketAsConnection);
                        LOGGER.info("socketAsArgument {}", socketAsArgument);
                        return socketAsConnection.contentEquals(socketAsArgument);
                    }).findFirst().orElseThrow(
                    () -> {
                        LOGGER.warn("CONNECTION WITH SOCKET {} NOT FOUND", socket);
                        return new ObjectNotFoundException("CONNECTION WITH SOCKET " + socket + " NOT FOUND");
                    });
    }


    public ActiveConnection removeConnection(Socket socket) {
        ActiveConnection activeConnection = findConnectionBySocket(socket);
        this.activeConnectionList.remove(activeConnection);
        LOGGER.info("CONNECTION {} REMOVED", activeConnection);
        return activeConnection;
    }
}








