package org.legenkiy.services;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legenkiy.api.service.SenderService;
import org.legenkiy.mapper.MessageMapper;
import org.legenkiy.protocol.message.Envelope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

@Service
@RequiredArgsConstructor
public class SenderServiceImpl implements SenderService {

    private static Logger LOGGER = LogManager.getLogger(SenderServiceImpl.class);

    private final MessageMapper mapper;


    @Override
    public void send(Socket socket, Envelope envelope) {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println(
                    mapper.encode(envelope)
            );
            LOGGER.info("Message sent to : {}", socket.getRemoteSocketAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
