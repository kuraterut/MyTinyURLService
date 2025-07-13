package org.kuraterut.mytinyurlservice.service;

import jakarta.validation.ConstraintViolationException;
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
import org.kuraterut.mytinyurlservice.usecase.CleanUseCase;
import org.kuraterut.mytinyurlservice.usecase.CreateTinyUrlUseCase;
import org.kuraterut.mytinyurlservice.usecase.GetInfoUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TinyUrlServiceImpl implements  CreateTinyUrlUseCase, GetInfoUseCase {
    private final TinyUrlRepository tinyUrlRepository;
    private final DtoMapper dtoMapper;

    @Value("${application.tiny-url-length}")
    private int shortUrlLength;

    @Override
    @Transactional
    public UrlResponse createShortUrl(CreateUrlRequest request) {
        try {
            String shortCode = request.getAlias() != null && !request.getAlias().isEmpty()
                    ? request.getAlias()
                    : generateUniqueShortCode();

            TinyUrl tinyUrl = dtoMapper.toEntity(request);
            tinyUrl.setShortCode(shortCode);

            tinyUrl = tinyUrlRepository.save(tinyUrl);
            return dtoMapper.toResponse(tinyUrl);

        } catch (DataIntegrityViolationException | ConstraintViolationException e) {
            if (request.getAlias() != null && !request.getAlias().isEmpty()) {
                throw new AliasAlreadyExistsException("Alias already exists");
            }

            return createShortUrl(request);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getOriginalUrl(String shortCode) {
        TinyUrl tinyUrl = tinyUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new TinyUrlNotFoundException("Tiny URL not found"));

        if (tinyUrl.getExpiresAt() != null && tinyUrl.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new TinyUrlNotFoundException("Tiny URL has expired");
        }

        return tinyUrl.getOriginalUrl();
    }

    @Override
    @Transactional(readOnly = true)
    public UrlResponse getUrlInfo(String shortCode) {
        TinyUrl tinyUrl = tinyUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new TinyUrlNotFoundException("Tiny URL not found"));

        return dtoMapper.toResponse(tinyUrl);
    }


    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = RandomStringUtils.randomAlphanumeric(shortUrlLength);
        } while (tinyUrlRepository.findByShortCode(shortCode).isPresent());

        return shortCode;
    }

}