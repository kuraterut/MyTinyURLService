package unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kuraterut.mytinyurlservice.exception.model.AliasAlreadyExistsException;
import org.kuraterut.mytinyurlservice.exception.model.TinyUrlNotFoundException;
import org.kuraterut.mytinyurlservice.mapper.DtoMapper;
import org.kuraterut.mytinyurlservice.model.dto.request.CreateUrlRequest;
import org.kuraterut.mytinyurlservice.model.dto.response.UrlResponse;
import org.kuraterut.mytinyurlservice.model.entity.TinyUrl;
import org.kuraterut.mytinyurlservice.repository.TinyUrlRepository;
import org.kuraterut.mytinyurlservice.service.TinyUrlService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TinyUrlServiceTest {
    @Mock
    private TinyUrlRepository tinyUrlRepository;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private TinyUrlService tinyUrlService;


    @Test
    void createShortUrl_shouldGenerateShortCode() {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setOriginalUrl("https://example.com");

        TinyUrl tinyUrl = new TinyUrl();
        tinyUrl.setOriginalUrl(request.getOriginalUrl());
        tinyUrl.setShortCode("abc123");

        when(dtoMapper.toEntity(request)).thenReturn(tinyUrl);
        when(tinyUrlRepository.findByShortCode(any())).thenReturn(Optional.empty());
        when(tinyUrlRepository.save(any())).thenAnswer(invocation -> {
            TinyUrl saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(dtoMapper.toResponse(any())).thenReturn(new UrlResponse(
                tinyUrl.getOriginalUrl(),
                "http://localhost:8080/" + tinyUrl.getShortCode(),
                null,
                LocalDateTime.now(),
                null
        ));

        UrlResponse response = tinyUrlService.createShortUrl(request);

        assertThat(response).isNotNull();
        assertThat(response.getOriginalUrl()).isEqualTo("https://example.com");
        assertThat(response.getShortUrl()).startsWith("http://localhost:8080/");
        verify(tinyUrlRepository, times(1)).save(any());
    }

    @Test
    void createShortUrl_withAlias_shouldUseAlias() {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setOriginalUrl("https://example.com");
        request.setAlias("example");

        TinyUrl tinyUrl = new TinyUrl();
        tinyUrl.setOriginalUrl(request.getOriginalUrl());
        tinyUrl.setShortCode("example");

        when(dtoMapper.toEntity(request)).thenReturn(tinyUrl);
        when(tinyUrlRepository.findByAlias("example")).thenReturn(Optional.empty());
        when(tinyUrlRepository.save(any())).thenAnswer(invocation -> {
            TinyUrl saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(dtoMapper.toResponse(any())).thenReturn(new UrlResponse(
                tinyUrl.getOriginalUrl(),
                "http://localhost:8080/example",
                "example",
                LocalDateTime.now(),
                null
        ));

        UrlResponse response = tinyUrlService.createShortUrl(request);

        assertThat(response).isNotNull();
        assertThat(response.getShortUrl()).isEqualTo("http://localhost:8080/example");
        verify(tinyUrlRepository, times(1)).save(any());
    }

    @Test
    void createShortUrl_withExistingAlias_shouldThrowException() {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setOriginalUrl("https://example.com");
        request.setAlias("example");

        TinyUrl existingTinyUrl = new TinyUrl();
        existingTinyUrl.setAlias("example");

        when(tinyUrlRepository.findByAlias("example")).thenReturn(Optional.of(existingTinyUrl));

        assertThrows(AliasAlreadyExistsException.class, () -> tinyUrlService.createShortUrl(request));
        verify(tinyUrlRepository, never()).save(any());
    }

    @Test
    void getOriginalUrl_shouldReturnOriginalUrl() {
        TinyUrl tinyUrl = new TinyUrl();
        tinyUrl.setOriginalUrl("https://example.com");
        tinyUrl.setShortCode("abc123");

        when(tinyUrlRepository.findByShortCode("abc123")).thenReturn(Optional.of(tinyUrl));
        when(tinyUrlRepository.save(any())).thenReturn(tinyUrl);

        String originalUrl = tinyUrlService.getOriginalUrl("abc123");

        assertThat(originalUrl).isEqualTo("https://example.com");
        verify(tinyUrlRepository, times(1)).save(tinyUrl);
    }

    @Test
    void getOriginalUrl_withExpiredUrl_shouldThrowException() {
        TinyUrl tinyUrl = new TinyUrl();
        tinyUrl.setOriginalUrl("https://example.com");
        tinyUrl.setShortCode("abc123");
        tinyUrl.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(tinyUrlRepository.findByShortCode("abc123")).thenReturn(Optional.of(tinyUrl));
        doNothing().when(tinyUrlRepository).delete(tinyUrl);

        assertThrows(TinyUrlNotFoundException.class, () -> tinyUrlService.getOriginalUrl("abc123"));
        verify(tinyUrlRepository, times(1)).delete(tinyUrl);
    }
}