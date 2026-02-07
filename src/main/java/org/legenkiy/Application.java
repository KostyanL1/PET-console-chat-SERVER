package org.legenkiy;


import org.legenkiy.configs.ApplicationConfig;
import org.legenkiy.net.TcpServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx =
                     new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            TcpServer server = ctx.getBean(TcpServer.class);
            Thread thread = new Thread(server);
            thread.start();
        }

    }

}