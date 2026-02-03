package org.legenkiy.net;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.legenkiy.connection.ConnectionsManager;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ConnectionsManager connectionsManager;

    public ClientHandler(Socket socket, ConnectionsManager connectionsManager) {
        this.socket = socket;
        this.connectionsManager = connectionsManager;
    }



    @Override
    public void run() {
        try(
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true)
        ){
            String message;
            while (true){
                if ((message = bufferedReader.readLine()) != null){
                    switch (message){
                        case "/exit" : {
                            connectionsManager.removeConnection(socket);
                            return;
                        }
                        default: {
                            System.out.println(message);
                        }
                    }
                }
            }

        }catch (IOException exception){
            throw new RuntimeException(exception);
        }
    }
}
