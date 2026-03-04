package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.legenkiy.mapper.MessageMapper;
import org.legenkiy.protocol.message.Envelope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@Service
@RequiredArgsConstructor
public class SenderService {

    private final MessageMapper mapper;

    public void send(Socket socket, Envelope envelope){
        try {
            PrintWriter printWriter =  new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(
                    mapper.encode(envelope)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
