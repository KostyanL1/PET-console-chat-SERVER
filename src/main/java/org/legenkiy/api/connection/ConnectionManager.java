package org.legenkiy.api.connection;

import org.legenkiy.models.ActiveConnection;

import java.net.ConnectException;
import java.net.Socket;

public interface ConnectionManager {

    void addNewConnection(ActiveConnection activeConnection) throws ConnectException;

    ActiveConnection findConnectionByUsername(String username);

    boolean isAlreadyConnected(ActiveConnection activeConnection);

    ActiveConnection findConnectionById(Long id);

    void authenticate(Socket socket, String username);

    ActiveConnection findConnectionBySocket(Socket socket);

    ActiveConnection removeConnection(Socket socket);

}
