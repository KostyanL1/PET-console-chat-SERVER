package org.legenkiy.configs;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.legenkiy.models.Chat;
import org.legenkiy.models.Message;
import org.legenkiy.models.User;
import org.springframework.context.annotation.Bean;


@AllArgsConstructor
@NoArgsConstructor
@org.springframework.context.annotation.Configuration
public class HibernateConfig {

    private static final Logger LOGGER = LogManager.getLogger(HibernateConfig.class);

    @Bean
    public SessionFactory getSessionFactory() {
        SessionFactory sessionFactory;
        try {
            Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(User.class);
            configuration.addAnnotatedClass(Chat.class);
            configuration.addAnnotatedClass(Message.class);
            StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(standardServiceRegistryBuilder.build());
            LOGGER.info("SESSION FACTORY SUCCESSFULLY BUILT");
        } catch (Exception e) {
            LOGGER.info("SESSION FACTORY SUCCESSFULLY BUILDING FAILED");
            throw new RuntimeException(e);
        }
        return sessionFactory;
    }
}






}
