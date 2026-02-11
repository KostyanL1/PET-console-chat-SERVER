package org.legenkiy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.legenkiy.models.Chat;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private String username;
    private String password;
    private List<Chat> chats;

}
