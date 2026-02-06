package org.legenkiy.api.dao;

import org.legenkiy.models.Chat;
import org.legenkiy.models.Message;

import java.util.List;

public interface MessageDao {

    List<Message> findAllByChat(Chat chat);

    Long save(Message newMessage);

    Long update(Message updatedMessage);

    void delete(Long id);
}
