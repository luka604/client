package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import dto.PageResponse;
import dto.response.PredmetDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class StudentPredmetService {

    private WebClient webClient;

    public Mono<PageResponse<PredmetDTO>> getNepolozeniIspitiAsync(String brojIndeksa, int page, int size) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/student-predmet/nepolozeni-ispiti")
                        .queryParam("brojIndeksa", brojIndeksa)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<PredmetDTO>>() {});
    }
}
