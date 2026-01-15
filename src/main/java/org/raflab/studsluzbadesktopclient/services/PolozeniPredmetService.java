package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.PageResponse;
import org.raflab.studsluzbadesktopclient.dtos.PolozeniPredmetDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class PolozeniPredmetService {

    private WebClient webClient;

    public Mono<PageResponse<PolozeniPredmetDTO>> getPolozeniIspitiAsync(String brojIndeksa, int page, int size) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/polozeni")
                        .queryParam("brojIndeksa", brojIndeksa)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<PolozeniPredmetDTO>>() {});
    }
}
