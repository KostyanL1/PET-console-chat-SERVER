package org.legenkiy.api.service;

import org.legenkiy.protocol.ClientMessage;

public interface DispatcherService {

    void handle(ClientMessage clientMessage);

}
