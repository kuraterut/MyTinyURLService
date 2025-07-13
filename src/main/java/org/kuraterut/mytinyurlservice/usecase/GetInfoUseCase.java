package org.kuraterut.mytinyurlservice.usecase;


import org.kuraterut.mytinyurlservice.model.dto.response.UrlResponse;

public interface GetInfoUseCase {
    String getOriginalUrl(String shortCode);
    UrlResponse getUrlInfo(String shortCode);
}
