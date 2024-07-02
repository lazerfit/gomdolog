package store.gomdolog.packages.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import store.gomdolog.packages.domain.PostSummary;
import store.gomdolog.packages.dto.PostSummaryDTO;
import store.gomdolog.packages.repository.PostSummaryRepository;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostSummaryService {

    private final PostSummaryRepository postSummaryRepository;

    @Value("${llm.api}")
    private String baseUrl;

    @Value("${llm.secret}")
    private String secret;

    public PostSummary save(String content) {
        return postSummaryRepository.save(new PostSummary(content));
    }

    public PostSummary findById(Long id) {
        return postSummaryRepository.findById(id).orElse(null);
    }

    public Mono<ResponseEntity<PostSummaryDTO>> getSummary(String content) {
        WebClient webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Authorization", secret)
            .defaultHeader("Content-Type", "application/json")
            .build();

        return webClient.post()
            .uri("/api/llm")
            .bodyValue(new PostSummary(content))
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, res -> Mono.error(
                new WebClientResponseException("Bad Request", res.statusCode().value(),
                    "Bad Request", null, null, null)))
            .onStatus(HttpStatusCode::is5xxServerError, res -> Mono.error(
                new WebClientResponseException("Server Error", res.statusCode().value(),
                    "Server Error", null, null, null)))
            .toEntity(PostSummaryDTO.class);
    }
}

