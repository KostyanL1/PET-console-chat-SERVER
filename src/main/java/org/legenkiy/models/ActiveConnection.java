package org.legenkiy.models;


import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ActiveConnection{

   private Long id;
   private LocalDateTime connectedAt;
   private String socket;


}