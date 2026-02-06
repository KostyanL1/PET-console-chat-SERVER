package org.legenkiy.api.dao;

import org.legenkiy.models.Chat;


import java.util.List;
import java.util.Optional;

public interface ChatDao {

    List<Chat> findAll();

    Optional<Chat> findById(Long id);

    Long save(Chat newChat);

    Long update(Chat updatedChat);

    void delete(Long id);
}
