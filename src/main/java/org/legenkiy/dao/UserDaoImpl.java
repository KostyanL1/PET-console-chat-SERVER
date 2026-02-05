package org.legenkiy.dao;


import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.legenkiy.models.User;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDaoImpl implements org.legenkiy.api.dao.UserDao {

    private static final Logger LOGGER = LogManager.getLogger();
    private final SessionFactory sessionFactory;

    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<User> userList = session.createQuery("From User").list();
            return userList;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.of(session.find(User.class, id));
        }
    }

    @Override
    public Long save(User newUser) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(newUser);
            transaction.commit();
            return newUser.getId();
        } catch (Exception e) {
            LOGGER.info(e);
            transaction.rollback();
            return null;
        }
    }

    @Override
    public Long update(User updatedUser) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, updatedUser.getId());
            user.setUsername(updatedUser.getUsername());
            if (updatedUser.getPassword() != null) {
                user.setPassword(updatedUser.getPassword());
            }
            if (updatedUser.getChats() != null) {
                user.setPassword(updatedUser.getPassword());
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.info("FAILED TO UPDATE USER WITH ID {}", updatedUser.getId());
            throw new RuntimeException("FAILED TO UPDATE USER");
        }
        return updatedUser.getId();
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            session.remove(user);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.info("DELETE USER FAILED id {}, {}", id, e);
            transaction.rollback();
        }
    }

}

