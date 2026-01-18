package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import dto.request.IspitRequestDTO;
import dto.response.IspitDTO;
import dto.response.ProsecnaOcenaDTO;
import dto.response.RezultatIspitaDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class IspitService {

    private WebClient webClient;


    public Mono<IspitDTO> createAsync(IspitRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/ispit")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IspitDTO.class);
    }

    public ProsecnaOcenaDTO getProsecnaOcena(Long ispitId) {
        return webClient
                .get()
                .uri("/api/ispit/{ispitId}/prosecna-ocena", ispitId)
                .retrieve()
                .bodyToMono(ProsecnaOcenaDTO.class)
                .block();
    }

    public Flux<RezultatIspitaDTO> getRezultatiIspitaAsync(Long ispitId) {
        return webClient
                .get()
                .uri("/api/ispit/{ispitId}/rezultati", ispitId)
                .retrieve()
                .bodyToFlux(RezultatIspitaDTO.class);
    }


    public Flux<IspitDTO> getIspitiByIspitniRokAsync(Long ispitniRokId) {
        return webClient
                .get()
                .uri("/api/ispit?ispitniRokId=" + ispitniRokId)
                .retrieve()
                .bodyToFlux(IspitDTO.class);
    }
}
