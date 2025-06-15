package org.kuraterut.mytinyurlservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kuraterut.mytinyurlservice.model.dto.request.CreateUrlRequest;
import org.kuraterut.mytinyurlservice.model.dto.response.UrlResponse;
import org.kuraterut.mytinyurlservice.service.TinyUrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tinyurl")
public class TinyUrlController {
    private final TinyUrlService tinyUrlService;

    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody CreateUrlRequest request) {
        return ResponseEntity.ok(tinyUrlService.createShortUrl(request));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlResponse> getUrlInfo(@PathVariable String shortCode) {
        return ResponseEntity.ok(tinyUrlService.getUrlInfo(shortCode));
    }

    @GetMapping("/{shortCode}/redirect")
    public void redirectToOriginalUrl(
            @PathVariable String shortCode,
            HttpServletResponse response
    ) throws IOException {
        String originalUrl = tinyUrlService.getOriginalUrl(shortCode);
        response.sendRedirect(originalUrl);
    }
}