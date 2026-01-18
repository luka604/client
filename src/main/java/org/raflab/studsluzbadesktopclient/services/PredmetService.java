package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import dto.request.PredmetRequestDTO;
import dto.response.PredmetDTO;
import dto.response.ProsecnaOcenaDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Service
@AllArgsConstructor
public class PredmetService {

    private WebClient webClient;


    public Flux<PredmetDTO> getAllPredmetiAsync() {
        return webClient
                .get()
                .uri("/api/predmet")
                .retrieve()
                .bodyToFlux(PredmetDTO.class);
    }

    public Flux<PredmetDTO> getPredmetiByStudijskiProgramAsync(String oznaka) {
        return webClient
                .get()
                .uri("/api/predmet/studijski-programi/{oznaka}", oznaka)
                .retrieve()
                .bodyToFlux(PredmetDTO.class);
    }

    public Mono<PredmetDTO> addToStudijskiProgramAsync(String oznaka, PredmetRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/predmet/studijski-programi/{oznaka}", oznaka)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(PredmetDTO.class);
    }

    public Mono<ProsecnaOcenaDTO> getProsecnaOcenaAsync(String predmetSifra, Integer godinaOd, Integer godinaDo) {
        return webClient
                .get()
                .uri("/api/predmet/" + predmetSifra + "/prosecna-ocena?godinaOd=" + godinaOd + "&godinaDo=" + godinaDo)
                .retrieve()
                .bodyToMono(ProsecnaOcenaDTO.class);
    }
}
