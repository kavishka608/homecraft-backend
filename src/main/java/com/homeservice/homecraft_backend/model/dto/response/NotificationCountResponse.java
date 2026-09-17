package com.homeservice.homecraft_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCountResponse {
    private long total;
    private long unread;
}