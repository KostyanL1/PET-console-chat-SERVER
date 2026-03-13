package org.legenkiy.context;


import lombok.RequiredArgsConstructor;
import org.legenkiy.api.dao.UserDao;
import org.legenkiy.api.service.UserService;
import org.legenkiy.exceptions.ObjectNotFoundException;
import org.legenkiy.models.Chat;
import org.legenkiy.models.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Scope(value = "singleton")
@RequiredArgsConstructor
public class ChatsContext {

    private List<Chat> chats = new  CopyOnWriteArrayList<>();
    private AtomicLong index = new AtomicLong(0);
    private final UserService userService;



    public synchronized Chat create(String firstUserUsername, String secondUserUsername){
        Chat chat = new Chat();
        chat.setId(index.incrementAndGet());
        User firstUser = userService.findByUsername(firstUserUsername);
        User sercondUser = userService.findByUsername(secondUserUsername);
        chat.setMembers(List.of(firstUser, sercondUser));
        chats.add(chat);
        return chat;
    }

    public boolean isExist(Long id){
        return chats.stream().anyMatch(
                payload -> Objects.equals(payload.getId(), id)
        );
    }

    public Chat findById(Long id){
        return chats.stream().filter(
                payload -> Objects.equals(payload.getId(), id)
        ).findFirst().orElseThrow(() -> new ObjectNotFoundException("Payload not found"));
    }

    public synchronized void removeById(Long id){
        try {
            Chat chat = findById(id);
            chats.remove(chat);
        }catch (Exception e){
            throw new RuntimeException("Failed to remove payload, " + e.getMessage());
        }
    }


}
