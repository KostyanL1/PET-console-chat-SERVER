package org.legenkiy.dao;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.legenkiy.api.dao.ChatDao;
import org.legenkiy.models.Chat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatDaoImpl implements ChatDao {

    private static final Logger LOGGER = LogManager.getLogger(ChatDaoImpl.class);
    private final SessionFactory sessionFactory;

    @Override
    public List<Chat> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Chat").list();
        }
    }

    @Override
    public Optional<Chat> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(Chat.class, id));
        }
    }

    @Override
    public Long save(Chat newChat) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(newChat);
            transaction.commit();
            return newChat.getId();
        } catch (Exception e) {
            LOGGER.info("SAVE FAILED {}, {}", newChat, e);
            transaction.rollback();
            return null;
        }
    }

    @Override
    public Long update(Chat updatedChat) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            Chat chat = session.find(Chat.class, updatedChat.getId());
            chat.setMembers(updatedChat.getMembers());
            transaction.commit();
            return chat.getId();
        } catch (Exception e) {
            LOGGER.info("UPDATE FAILED {}, {}", updatedChat, e);
            transaction.rollback();
            return null;
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Chat chat = session.find(Chat.class, id);
            session.remove(chat);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.info("DELETE CHAT FAILED id {}, {}", id, e);
            transaction.rollback();
        }

    }


}
