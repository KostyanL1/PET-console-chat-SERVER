package org.legenkiy;



import org.legenkiy.config.ApplicationConfig;
import org.legenkiy.net.TcpServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class Application {
    public static void main(String[] args) {

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        TcpServer tcpServer = applicationContext.getBean(TcpServer.class);
        Thread thread = new Thread(tcpServer);
        thread.start();


    }

}