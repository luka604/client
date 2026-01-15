package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.UpisGodineDTO;
import org.raflab.studsluzbadesktopclient.dtos.UpisGodineRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UpisGodineService {

    private WebClient webClient;

    public Flux<UpisGodineDTO> getUpisaneGodineAsync(String brojIndeksa) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/upis/upisane-godine")
                        .queryParam("brojIndeksa", brojIndeksa)
                        .build())
                .retrieve()
                .bodyToFlux(UpisGodineDTO.class);
    }

    public Mono<UpisGodineDTO> upisGodineAsync(String brojIndeksa, UpisGodineRequestDTO request) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/upis")
                        .queryParam("brojIndeksa", brojIndeksa)
                        .build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UpisGodineDTO.class);
    }
}
