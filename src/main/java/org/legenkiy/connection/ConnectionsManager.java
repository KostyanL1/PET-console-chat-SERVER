package org.legenkiy.connection;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.exception.AlreadyConnectedException;
import org.legenkiy.exception.ObjectNotFoundException;
import org.legenkiy.model.ActiveConnection;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Component
@Scope(scopeName = "singleton")
public class ConnectionsManager {

    private List<ActiveConnection> activeConnectionList = Collections.synchronizedList(new ArrayList<>());
    private AtomicLong index = new AtomicLong(0);
    private final Logger LOGGER = LogManager.getLogger(ConnectionsManager.class);

    public ActiveConnection addNewConnection(ActiveConnection activeConnection) {
        if (!isAlreadyConnected(activeConnection)) {
            activeConnection.setId(index.incrementAndGet());
            this.activeConnectionList.add(activeConnection);
            LOGGER.info("NEW CONNECTION CREATED : {}", activeConnection);
            return activeConnection;
        }
        LOGGER.warn("CONNECTION ALREADY EXIST : {}", activeConnection);
        throw new AlreadyConnectedException("Connection already exist");
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

    private synchronized ActiveConnection findConnectionBySocket(Socket socket){
        String clientSocket = socket.getInetAddress() + ":" + socket.getPort();
        return this.activeConnectionList.stream().filter(connection ->
                connection.getSocket().equals(clientSocket)).findFirst().orElseThrow(
                () -> {
                    LOGGER.warn("CONNECTION WITH SOCKET {} NOT FOUND", clientSocket);
                    throw new ObjectNotFoundException("CONNECTION WITH SOCKET " + clientSocket + " NOT FOUND");
                }
        );
    }

    public ActiveConnection removeConnection(Socket socket) {
        ActiveConnection activeConnection = findConnectionBySocket(socket);
        this.activeConnectionList.remove(activeConnection);
        LOGGER.info("CONNECTION {} REMOVED", activeConnection);
        return activeConnection;
    }


}





