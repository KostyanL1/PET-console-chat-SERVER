package org.legenkiy.models;


import lombok.*;
import org.legenkiy.enums.ClientState;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ActiveConnection {

    private Long id;
    private String username;
    private ClientState clientState;
    private LocalDateTime connectedAt;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private String connectedUsername;

}