package org.legenkiy.services;


import lombok.RequiredArgsConstructor;
import org.legenkiy.api.service.DispatcherService;
import org.legenkiy.connection.ConnectionsManager;
import org.legenkiy.protocol.ClientMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DispatcherServiceImpl implements DispatcherService {

    private final ConnectionsManager connectionsManager;

    @Override
    public void handle(ClientMessage clientMessage) {


    }
}
