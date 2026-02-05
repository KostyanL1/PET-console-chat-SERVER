package org.legenkiy.dao;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.legenkiy.api.dao.MessageDao;
import org.legenkiy.models.Chat;
import org.legenkiy.models.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageDaoImpl implements MessageDao {

    private static final Logger LOGGER = LogManager.getLogger(MessageDaoImpl.class);
    private final SessionFactory sessionFactory;

    @Override
    public List<Message> findAllByChat(Chat chat) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Message m WHERE m.chat=:chat", Message.class)
                    .setParameter("chat", chat)
                    .list();
        }
    }

    @Override
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

    @Override
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

    @Override
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
