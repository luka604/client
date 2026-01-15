package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.ObnovaGodineDTO;
import org.raflab.studsluzbadesktopclient.dtos.ObnovaGodineRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ObnovaGodineService {

    private WebClient webClient;

    public Flux<ObnovaGodineDTO> getObnovljeneGodineAsync(String brojIndeksa) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/obnova/obnovljene-godine")
                        .queryParam("brojIndeksa", brojIndeksa)
                        .build())
                .retrieve()
                .bodyToFlux(ObnovaGodineDTO.class);
    }

    public Mono<ObnovaGodineDTO> obnovaGodineAsync(String brojIndeksa, ObnovaGodineRequestDTO request) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/obnova")
                        .queryParam("brojIndeksa", brojIndeksa)
                        .build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ObnovaGodineDTO.class);
    }
}
