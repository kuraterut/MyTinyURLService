package org.kuraterut.mytinyurlservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.kuraterut.mytinyurlservice.exception.model.AliasAlreadyExistsException;
import org.kuraterut.mytinyurlservice.exception.model.TinyUrlNotFoundException;
import org.kuraterut.mytinyurlservice.mapper.DtoMapper;
import org.kuraterut.mytinyurlservice.model.dto.request.CreateUrlRequest;
import org.kuraterut.mytinyurlservice.model.dto.response.UrlResponse;
import org.kuraterut.mytinyurlservice.model.entity.TinyUrl;
import org.kuraterut.mytinyurlservice.repository.TinyUrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TinyUrlService {
    private final TinyUrlRepository tinyUrlRepository;
    private final DtoMapper dtoMapper;

    @Value("${application.tiny-url-length}")
    private int shortUrlLength;

    @Transactional
    public UrlResponse createShortUrl(CreateUrlRequest request) {
        cleanupExpiredUrls();
        if (request.getAlias() != null && !request.getAlias().isEmpty()) {
            if (tinyUrlRepository.findByAlias(request.getAlias()).isPresent()) {
                throw new AliasAlreadyExistsException("Alias already exists");
            }
        }
        String shortCode = request.getAlias() != null && !request.getAlias().isEmpty()
                ? request.getAlias()
                : generateUniqueShortCode();

        TinyUrl tinyUrl = dtoMapper.toEntity(request);
        tinyUrl.setShortCode(shortCode);

        tinyUrl = tinyUrlRepository.save(tinyUrl);
        return dtoMapper.toResponse(tinyUrl);
    }

    @Transactional
    public String getOriginalUrl(String shortCode) {
        TinyUrl tinyUrl = tinyUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new TinyUrlNotFoundException("Tiny URL not found"));

        if (tinyUrl.getExpiresAt() != null && tinyUrl.getExpiresAt().isBefore(LocalDateTime.now())) {
            tinyUrlRepository.delete(tinyUrl);
            throw new TinyUrlNotFoundException("Tiny URL has expired");
        }

        tinyUrlRepository.save(tinyUrl);

        return tinyUrl.getOriginalUrl();
    }

    @Transactional(readOnly = true)
    public UrlResponse getUrlInfo(String shortCode) {
        cleanupExpiredUrls();
        TinyUrl tinyUrl = tinyUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new TinyUrlNotFoundException("Tiny URL not found"));

        return dtoMapper.toResponse(tinyUrl);
    }

    @Transactional
    public void cleanupExpiredUrls() {
        tinyUrlRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = RandomStringUtils.randomAlphanumeric(shortUrlLength);
        } while (tinyUrlRepository.findByShortCode(shortCode).isPresent());

        return shortCode;
    }

}