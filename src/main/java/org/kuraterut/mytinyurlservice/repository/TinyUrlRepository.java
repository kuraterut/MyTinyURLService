package org.kuraterut.mytinyurlservice.repository;


import org.kuraterut.mytinyurlservice.model.entity.TinyUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface TinyUrlRepository extends JpaRepository<TinyUrl, Long> {
    Optional<TinyUrl> findByShortCode(String shortCode);
    Optional<TinyUrl> findByAlias(String alias);
    void deleteByExpiresAtBefore(OffsetDateTime now);
}