package org.legenkiy.connection;


import org.legenkiy.api.connection.ConnectionManager;
import org.legenkiy.enums.ClientState;
import org.legenkiy.exceptions.ObjectNotFoundException;
import org.legenkiy.models.ActiveConnection;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class ConnectionsManagerImpl implements ConnectionManager {

    private final List<ActiveConnection> activeConnectionList = new CopyOnWriteArrayList<>();
    private final AtomicLong index = new AtomicLong(0);

    @Override
    public void addNewConnection(ActiveConnection activeConnection) throws ConnectException {
        if (!isAlreadyConnected(activeConnection)) {
            activeConnection.setId(index.incrementAndGet());
            activeConnection.setClientState(ClientState.NEW);
            this.activeConnectionList.add(activeConnection);
        } else {
            throw new ConnectException("Connection exist");
        }
    }

    @Override
    public ActiveConnection findConnectionByUsername(String username) {
        String searchName = String.valueOf(username);
        return this.activeConnectionList.stream()
                .filter(active -> String.valueOf(active.getUsername()).equals(searchName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Connection not found for username: " + username));
    }

    @Override
    public synchronized boolean isAlreadyConnected(ActiveConnection activeConnection) {
        Optional<ActiveConnection> activeConnectionOptional = this.activeConnectionList.stream()
                .filter(connection ->
                        connection.getId().equals(activeConnection.getId())).findFirst();
        return activeConnectionOptional.isPresent();
    }

    @Override
    public synchronized ActiveConnection findConnectionById(Long id) {
        return this.activeConnectionList.stream().filter(connection ->
                connection.getId().equals(id)).findFirst().orElseThrow(
                () -> {
                    throw new ObjectNotFoundException("Connection not found");
                }
        );
    }

    @Override
    public synchronized void authenticate(Socket socket, String username) {
        ActiveConnection connection = findConnectionBySocket(socket);
        connection.setUsername(username);
        connection.setClientState(ClientState.AUTHENTICATED);
    }

    @Override
    public synchronized ActiveConnection findConnectionBySocket(Socket socket) {
        return this.activeConnectionList.stream().filter(
                activeConnection -> {
                    String socketAsConnection = activeConnection.getSocket().getRemoteSocketAddress().toString();
                    String socketAsArgument = socket.getRemoteSocketAddress().toString();
                    return socketAsConnection.contentEquals(socketAsArgument);
                }).findFirst().orElseThrow(
                () -> new ObjectNotFoundException("Connection not found"));
    }

    @Override
    public ActiveConnection removeConnection(Socket socket) {
        ActiveConnection activeConnection = findConnectionBySocket(socket);
        this.activeConnectionList.remove(activeConnection);
        return activeConnection;
    }
}








