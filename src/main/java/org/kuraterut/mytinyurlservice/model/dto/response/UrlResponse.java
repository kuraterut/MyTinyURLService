package org.kuraterut.mytinyurlservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlResponse {
    private String originalUrl;
    private String shortUrl;
    private String alias;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}