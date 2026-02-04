package org.legenkiy.dao;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.legenkiy.models.Chat;
import org.legenkiy.configs.HibernateConfig;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
public class ChatDao {

    private static final Logger LOGGER = LogManager.getLogger(ChatDao.class);


    public List<Chat> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            List<Chat> chatList = session.createQuery("From Chat").list();
            return chatList;
        }
    }

    public Optional<Chat> findById(Long id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return Optional.of(session.find(Chat.class, id));
        }
    }

    public Long save(Chat newChat){
        try (Session session = HibernateConfig.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();


        }

    }

}
