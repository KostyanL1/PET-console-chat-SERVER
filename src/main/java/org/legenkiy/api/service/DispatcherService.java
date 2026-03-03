package org.legenkiy.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;


import java.io.PrintWriter;
import java.net.Socket;

public interface DispatcherService {

    void handle(Socket socket, PrintWriter printWriter) throws JsonProcessingException;

}
