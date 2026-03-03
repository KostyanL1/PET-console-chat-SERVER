package org.legenkiy.api.connection;

import org.legenkiy.models.ActiveConnection;

import java.net.ConnectException;
import java.net.Socket;

public interface ConnectionManager {

    void create(Socket clientSocket,  ActiveConnection activeConnection) throws ConnectException;

    boolean isConnected(Socket socket);

    ActiveConnection findConnectionByUsername(String username);

    ActiveConnection findConnectionById(Long id);

    void authenticate(Socket socket, String username);

    ActiveConnection findConnectionBySocket(Socket socket);

    ActiveConnection removeConnectionBySocket(Socket socket);

}
