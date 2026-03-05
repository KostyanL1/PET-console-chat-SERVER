package org.legenkiy.api.service;

import org.legenkiy.protocol.message.Envelope;


import java.io.PrintWriter;
import java.net.Socket;

public interface DispatcherService {

    void handle(Envelope envelope, Socket socket, PrintWriter printWriter);

}
