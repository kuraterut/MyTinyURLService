package org.kuraterut.mytinyurlservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kuraterut.mytinyurlservice.model.dto.request.CreateUrlRequest;
import org.kuraterut.mytinyurlservice.model.dto.response.UrlResponse;
import org.kuraterut.mytinyurlservice.usecase.CreateTinyUrlUseCase;
import org.kuraterut.mytinyurlservice.usecase.GetInfoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tinyurl")
public class TinyUrlController {
    private final GetInfoUseCase getInfoUseCase;
    private final CreateTinyUrlUseCase createTinyUrlUseCase;

    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody CreateUrlRequest request) {
        return ResponseEntity.ok(createTinyUrlUseCase.createShortUrl(request));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlResponse> getUrlInfo(@PathVariable String shortCode) {
        return ResponseEntity.ok(getInfoUseCase.getUrlInfo(shortCode));
    }

    @GetMapping("/{shortCode}/redirect")
    public void redirectToOriginalUrl(
            @PathVariable String shortCode,
            HttpServletResponse response
    ) throws IOException {
        String originalUrl = getInfoUseCase.getOriginalUrl(shortCode);
        response.sendRedirect(originalUrl);
    }
}