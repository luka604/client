package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class PrijavaIspitaService {

    private WebClient webClient;

    public PrijavaIspitaDTO prijavaIspita(PrijavaIspitaRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/prijava")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(PrijavaIspitaDTO.class)
                .block();
    }

    public Mono<PrijavaIspitaDTO> prijavaIspitaAsync(PrijavaIspitaRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/prijava")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(PrijavaIspitaDTO.class);
    }

    public List<StudentPrijavaDTO> getPrijavljeniStudenti(Long ispitId) {
        return webClient
                .get()
                .uri("/api/prijava/{ispitId}/prijavljeni-studenti", ispitId)
                .retrieve()
                .bodyToFlux(StudentPrijavaDTO.class)
                .collectList()
                .block();
    }

    public Flux<StudentPrijavaDTO> getPrijavljeniStudentiAsync(Long ispitId) {
        return webClient
                .get()
                .uri("/api/prijava/{ispitId}/prijavljeni-studenti", ispitId)
                .retrieve()
                .bodyToFlux(StudentPrijavaDTO.class);
    }
}
