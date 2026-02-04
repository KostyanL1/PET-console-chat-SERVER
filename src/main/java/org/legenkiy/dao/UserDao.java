package org.legenkiy.dao;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.legenkiy.models.User;
import org.legenkiy.configs.HibernateConfig;



import java.util.List;
import java.util.Optional;


public class UserDao {

    private static final Logger LOGGER = LogManager.getLogger();


    public List<User> findAll() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            List<User> userList = session.createQuery("From User").list();
            return userList;
        }
    }

    public Optional<User> findById(Long id) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return Optional.of(session.find(User.class, id));
        }
    }

    public Long save(User newUser) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(newUser);
            transaction.commit();
            return newUser.getId();
        } catch (Exception e) {
            LOGGER.info(e);
            return null;
        }
    }

    public Long update(User updatedUser) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            User user = session.find(User.class, updatedUser.getId());
            user.setUsername(updatedUser.getUsername());
            if (updatedUser.getPassword() != null) {
                user.setPassword(updatedUser.getPassword());
            }
            transaction.commit();
        }catch (Exception e){
            LOGGER.info("FAILED TO UPDATE USER WITH ID {}", updatedUser.getId());
            throw new RuntimeException("FAILED TO UPDATE USER");
        }
        return updatedUser.getId();
    }

    public Long delete(Long id){
        try (Session session = HibernateConfig.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            session.remove(user);
            transaction.commit();
            return user.getId();
        }
    }

}

