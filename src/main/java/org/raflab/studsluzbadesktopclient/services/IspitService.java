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
public class IspitService {

    private WebClient webClient;

    public List<IspitDTO> getAllIspiti() {
        return webClient
                .get()
                .uri("/api/ispit")
                .retrieve()
                .bodyToFlux(IspitDTO.class)
                .collectList()
                .block();
    }

    public Flux<IspitDTO> getAllIspitiAsync() {
        return webClient
                .get()
                .uri("/api/ispit")
                .retrieve()
                .bodyToFlux(IspitDTO.class);
    }

    public IspitDTO getById(Long id) {
        return webClient
                .get()
                .uri("/api/ispit/{id}", id)
                .retrieve()
                .bodyToMono(IspitDTO.class)
                .block();
    }

    public Mono<IspitDTO> getByIdAsync(Long id) {
        return webClient
                .get()
                .uri("/api/ispit/{id}", id)
                .retrieve()
                .bodyToMono(IspitDTO.class);
    }

    public IspitDTO create(IspitRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/ispit")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IspitDTO.class)
                .block();
    }

    public Mono<IspitDTO> createAsync(IspitRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/ispit")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IspitDTO.class);
    }

    public IspitDTO update(Long id, IspitRequestDTO dto) {
        return webClient
                .put()
                .uri("/api/ispit/{id}", id)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IspitDTO.class)
                .block();
    }

    public void delete(Long id) {
        webClient
                .delete()
                .uri("/api/ispit/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public ProsecnaOcenaDTO getProsecnaOcena(Long ispitId) {
        return webClient
                .get()
                .uri("/api/ispit/{ispitId}/prosecna-ocena", ispitId)
                .retrieve()
                .bodyToMono(ProsecnaOcenaDTO.class)
                .block();
    }

    public List<RezultatIspitaDTO> getRezultatiIspita(Long ispitId) {
        return webClient
                .get()
                .uri("/api/ispit/{ispitId}/rezultati", ispitId)
                .retrieve()
                .bodyToFlux(RezultatIspitaDTO.class)
                .collectList()
                .block();
    }

    public Flux<RezultatIspitaDTO> getRezultatiIspitaAsync(Long ispitId) {
        return webClient
                .get()
                .uri("/api/ispit/{ispitId}/rezultati", ispitId)
                .retrieve()
                .bodyToFlux(RezultatIspitaDTO.class);
    }

    public List<IspitDTO> getIspitiByIspitniRok(Long ispitniRokId) {
        return webClient
                .get()
                .uri("/api/ispit?ispitniRokId=" + ispitniRokId)
                .retrieve()
                .bodyToFlux(IspitDTO.class)
                .collectList()
                .block();
    }

    public Flux<IspitDTO> getIspitiByIspitniRokAsync(Long ispitniRokId) {
        return webClient
                .get()
                .uri("/api/ispit?ispitniRokId=" + ispitniRokId)
                .retrieve()
                .bodyToFlux(IspitDTO.class);
    }
}
