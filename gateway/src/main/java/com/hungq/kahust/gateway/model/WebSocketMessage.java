package com.hungq.kahust.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessage {
    private Long userId;
    private String content;
    private Long roomId;
}
