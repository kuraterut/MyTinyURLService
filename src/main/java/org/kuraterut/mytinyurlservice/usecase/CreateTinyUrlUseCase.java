package org.kuraterut.mytinyurlservice.usecase;

import org.kuraterut.mytinyurlservice.model.dto.request.CreateUrlRequest;
import org.kuraterut.mytinyurlservice.model.dto.response.UrlResponse;

public interface CreateTinyUrlUseCase {
    UrlResponse createShortUrl(CreateUrlRequest request);
}
