package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.SkolskaGodinaDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class SkolskaGodinaService {

    private WebClient webClient;

    public List<SkolskaGodinaDTO> getAll() {
        return webClient
                .get()
                .uri("/api/skolska-godina")
                .retrieve()
                .bodyToFlux(SkolskaGodinaDTO.class)
                .collectList()
                .block();
    }

    public Flux<SkolskaGodinaDTO> getAllAsync() {
        return webClient
                .get()
                .uri("/api/skolska-godina")
                .retrieve()
                .bodyToFlux(SkolskaGodinaDTO.class);
    }

    public SkolskaGodinaDTO getAktivna() {
        return webClient
                .get()
                .uri("/api/skolska-godina/aktivna")
                .retrieve()
                .bodyToMono(SkolskaGodinaDTO.class)
                .block();
    }

    public Mono<SkolskaGodinaDTO> getAktivnaAsync() {
        return webClient
                .get()
                .uri("/api/skolska-godina/aktivna")
                .retrieve()
                .bodyToMono(SkolskaGodinaDTO.class);
    }
}
