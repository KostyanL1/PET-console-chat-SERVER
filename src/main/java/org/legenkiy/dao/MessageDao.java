package org.legenkiy.dao;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.legenkiy.models.Chat;
import org.legenkiy.models.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageDao {

    private static final Logger LOGGER = LogManager.getLogger(MessageDao.class);
    private final SessionFactory sessionFactory;

    public List<Message> findAllByChat(Chat chat) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Message m WHERE m.chat=:chat", Message.class)
                    .setParameter("chat", chat)
                    .list();
        }
    }

    public Long save(Message newMessage) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            session.persist(newMessage);
            transaction.commit();
            return newMessage.getId();
        } catch (Exception e) {
            LOGGER.info("CHAT CREATION FAILED WITH id {}, {}", newMessage.getId(), e);
            transaction.rollback();
            return null;
        }
    }

    public Long update(Message updatedMessage) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            Message message = session.find(Message.class, updatedMessage.getId());
            message.setContent(updatedMessage.getContent());
            transaction.commit();
            return message.getId();
        } catch (Exception e) {
            LOGGER.info("MESSAGE UPDATE FAILED WITH id {}, {}", updatedMessage.getId(), e);
            transaction.rollback();
            return null;
        }
    }

    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            Message message = session.find(Message.class, id);
            session.remove(message);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.info("MESSAGE DELETE FAILED WITH id {}, {}", id, e);
            transaction.rollback();
        }
    }


}
