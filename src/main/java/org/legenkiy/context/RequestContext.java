package org.legenkiy.context;



import org.legenkiy.exceptions.ObjectNotFoundException;
import org.legenkiy.protocol.dtos.ChatIncomingPayload;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;


@Component
@Scope(value = "singleton")
public class RequestContext {

    private static volatile List<ChatIncomingPayload> chatIncomingPayloads = new CopyOnWriteArrayList<>();
    private static AtomicLong index = new AtomicLong(0);


    public static synchronized ChatIncomingPayload create(String from){
        ChatIncomingPayload chatIncomingPayload = new ChatIncomingPayload();
        chatIncomingPayload.setRequestId(index.incrementAndGet());
        chatIncomingPayload.setFrom(from);
        chatIncomingPayloads.add(chatIncomingPayload);
        return chatIncomingPayload;
    }

    public static boolean isExist(Long id){
        return chatIncomingPayloads.stream().anyMatch(
                payload -> Objects.equals(payload.getRequestId(), id)
        );
    }

    public static ChatIncomingPayload findById(Long id){
        return chatIncomingPayloads.stream().filter(
                payload -> Objects.equals(payload.getRequestId(), id)
        ).findFirst().orElseThrow(() -> new ObjectNotFoundException("Payload not found"));
    }

    public static synchronized void removeById(Long id){
        try {
            ChatIncomingPayload chatIncomingPayload = findById(id);
            chatIncomingPayloads.remove(chatIncomingPayload);
        }catch (Exception e){
            throw new RuntimeException("Failed to remove payload, " + e.getMessage());
        }
    }
}
