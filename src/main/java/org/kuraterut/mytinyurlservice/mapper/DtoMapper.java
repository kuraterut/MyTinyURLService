package org.kuraterut.mytinyurlservice.mapper;

import org.kuraterut.mytinyurlservice.model.dto.request.CreateUrlRequest;
import org.kuraterut.mytinyurlservice.model.dto.response.UrlResponse;
import org.kuraterut.mytinyurlservice.model.entity.TinyUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {
    @Value("${application.base-url}")
    private String baseUrl;

    public UrlResponse toResponse(TinyUrl tinyUrl) {
        return new UrlResponse(
                tinyUrl.getOriginalUrl(),
                baseUrl + tinyUrl.getShortCode(),
                tinyUrl.getAlias(),
                tinyUrl.getCreatedAt(),
                tinyUrl.getExpiresAt()
        );
    }

    public TinyUrl toEntity(CreateUrlRequest request) {
        return TinyUrl.builder()
                .originalUrl(request.getOriginalUrl())
                .alias(request.getAlias())
                .expiresAt(request.getExpiresAt())
                .build();
    }
}
