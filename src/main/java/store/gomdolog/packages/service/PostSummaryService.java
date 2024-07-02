package store.gomdolog.packages.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import store.gomdolog.packages.domain.PostSummary;
import store.gomdolog.packages.dto.PostSummaryDTO;
import store.gomdolog.packages.error.NetworkError;
import store.gomdolog.packages.repository.PostSummaryRepository;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostSummaryService {

    private final PostSummaryRepository postSummaryRepository;

    @Value("${llm.secret}")
    private String secret;

    public PostSummary save(String content) {
        return postSummaryRepository.save(new PostSummary(content));
    }

    public PostSummary findById(Long id) {
        return postSummaryRepository.findById(id).orElse(null);
    }

    public Mono<PostSummaryDTO> getSummary(String content) {
        WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8000")
            .defaultHeader("Authorization", secret)
            .defaultHeader("Content-Type", "application/json")
            .build();

        return webClient.post()
            .uri("/api/llm")
            .bodyValue(new PostSummary(content))
            .exchangeToMono(res -> {
                if  (res.statusCode().is2xxSuccessful()) {
                    return res.bodyToMono(PostSummaryDTO.class);
                } else {
                    return Mono.error(new NetworkError());
                }
            });

    }
}

