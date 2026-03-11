package org.legenkiy.net;



import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.connection.ConnectionManager;
import org.legenkiy.api.service.DispatcherService;
import org.legenkiy.mapper.MessageMapper;
import org.legenkiy.protocol.message.Envelope;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


@Component
@Scope(scopeName = "prototype")
@RequiredArgsConstructor
public class ClientHandler implements Runnable {

    private final static Logger LOGGER = LogManager.getLogger(ClientHandler.class);

    private final MessageMapper mapper;
    private final DispatcherService dispatcherService;
    private final ConnectionManager connectionManager;

    private Socket socket;

    public void init(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        if (this.socket == null) throw new IllegalArgumentException("Socket haven't been initialized");
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            String message;
            while ((message = bufferedReader.readLine()) != null){
                Envelope envelope;
                try {
                     envelope = mapper.decode(message, Envelope.class);
                }catch (Exception e){
                    LOGGER.error(e.getMessage());
                    continue;
                }
                dispatcherService.handle(envelope, socket, printWriter);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (!this.socket.isClosed()){
                try {
                    this.socket.close();
                    connectionManager.removeConnectionBySocket(this.socket);
                } catch (IOException ignored) {}
            }
        }
    }

}
