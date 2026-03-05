package org.legenkiy.api.service;

import org.legenkiy.protocol.message.Envelope;

import java.net.Socket;

public interface SenderService {

    void send(Socket socket, Envelope envelope);
}
