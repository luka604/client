package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import dto.response.IspitniRokDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@AllArgsConstructor
public class IspitniRokService {

    private WebClient webClient;

    public Flux<IspitniRokDTO> getAllIspitniRokoviAsync() {
        return webClient
                .get()
                .uri("/api/ispitni-rok")
                .retrieve()
                .bodyToFlux(IspitniRokDTO.class);
    }

    public Mono<IspitniRokDTO> createAsync(IspitniRokDTO dto) {
        return webClient
                .post()
                .uri("/api/ispitni-rok")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IspitniRokDTO.class);
    }

}
