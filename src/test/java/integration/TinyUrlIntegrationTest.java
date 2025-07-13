package integration;

import org.junit.jupiter.api.Test;
import org.kuraterut.mytinyurlservice.MyTinyURLServiceApplication;
import org.kuraterut.mytinyurlservice.model.dto.request.CreateUrlRequest;
import org.kuraterut.mytinyurlservice.model.dto.response.UrlResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = MyTinyURLServiceApplication.class
)
@Testcontainers
public class TinyUrlIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.enabled", () -> true);
        registry.add("spring.liquibase.change-log", () -> "classpath:db/migration/changelog-master.xml");
        registry.add("application.base-url", () -> "http://localhost:8080/");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateAndRedirectShortUrl() {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setOriginalUrl("https://example.com");

        ResponseEntity<UrlResponse> createResponse = restTemplate.postForEntity(
                "/api/tinyurl", request, UrlResponse.class);


        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getShortUrl()).isNotEmpty();

        String shortCode = createResponse.getBody().getShortUrl().replace("http://localhost:8080/", "");
        ResponseEntity<UrlResponse> infoResponse = restTemplate.getForEntity(
                "/api/tinyurl/" + shortCode, UrlResponse.class);

        assertThat(infoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(infoResponse.getBody()).isNotNull();
        assertThat(infoResponse.getBody().getOriginalUrl()).isEqualTo("https://example.com");

        ResponseEntity<Void> redirectResponse = restTemplate.getForEntity(
                "/api/tinyurl/" + shortCode + "/redirect", Void.class);

        assertThat(redirectResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }
}