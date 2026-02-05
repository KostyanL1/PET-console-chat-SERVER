package org.legenkiy.dao;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.legenkiy.models.Chat;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
public class ChatDao {

    private static final Logger LOGGER = LogManager.getLogger(ChatDao.class);
    private final SessionFactory sessionFactory;

    public List<Chat> findAll() {
        try (Session session = sessionFactory.openSession()){
            return session.createQuery("FROM Chat").list();
        }
    }

    public Optional<Chat> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.of(session.find(Chat.class, id));
        }
    }

    public Long save(Chat newChat){
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()){
            transaction = session.beginTransaction();
            session.persist(newChat);
            transaction.commit();
            return newChat.getId();
        }catch (Exception e){
            LOGGER.info("SAVE FAILED {}, {}", newChat, e);
            transaction.rollback();
            return null;
        }
    }



}
