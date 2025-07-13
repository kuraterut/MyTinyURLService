package org.kuraterut.mytinyurlservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kuraterut.mytinyurlservice.repository.TinyUrlRepository;
import org.kuraterut.mytinyurlservice.usecase.CleanUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanupScheduledServiceImpl implements CleanUseCase {
    private final TinyUrlRepository tinyUrlRepository;

    @Override
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredUrls() {
        tinyUrlRepository.deleteByExpiresAtBefore(OffsetDateTime.now());
    }
}
