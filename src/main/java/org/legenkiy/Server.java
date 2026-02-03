package org.legenkiy;



import org.legenkiy.server.service.impl.ServerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


@Component
@ComponentScan("org.legenkiy")
public class Server {
    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(Server.class);
        ServerService service = context.getBean(ServerService.class);
        Thread thread = new Thread(service);
        thread.start();


    }

}