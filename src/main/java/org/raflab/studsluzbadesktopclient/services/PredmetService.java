package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import dto.request.PredmetRequestDTO;
import dto.response.PredmetDTO;
import dto.response.ProsecnaOcenaDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class PredmetService {

    private WebClient webClient;

    public List<PredmetDTO> getAllPredmeti() {
        return webClient
                .get()
                .uri("/api/predmet")
                .retrieve()
                .bodyToFlux(PredmetDTO.class)
                .collectList()
                .block();
    }

    public Flux<PredmetDTO> getAllPredmetiAsync() {
        return webClient
                .get()
                .uri("/api/predmet")
                .retrieve()
                .bodyToFlux(PredmetDTO.class);
    }

    public PredmetDTO getById(Long id) {
        return webClient
                .get()
                .uri("/api/predmet/{id}", id)
                .retrieve()
                .bodyToMono(PredmetDTO.class)
                .block();
    }

    public List<PredmetDTO> getPredmetiByStudijskiProgram(String oznaka) {
        return webClient
                .get()
                .uri("/api/predmet/studijski-programi/{oznaka}", oznaka)
                .retrieve()
                .bodyToFlux(PredmetDTO.class)
                .collectList()
                .block();
    }

    public Flux<PredmetDTO> getPredmetiByStudijskiProgramAsync(String oznaka) {
        return webClient
                .get()
                .uri("/api/predmet/studijski-programi/{oznaka}", oznaka)
                .retrieve()
                .bodyToFlux(PredmetDTO.class);
    }

    public PredmetDTO create(PredmetRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/predmet")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(PredmetDTO.class)
                .block();
    }

    public Mono<PredmetDTO> createAsync(PredmetRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/predmet")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(PredmetDTO.class);
    }

    public PredmetDTO addToStudijskiProgram(String oznaka, PredmetRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/predmet/studijski-programi/{oznaka}", oznaka)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(PredmetDTO.class)
                .block();
    }

    public Mono<PredmetDTO> addToStudijskiProgramAsync(String oznaka, PredmetRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/predmet/studijski-programi/{oznaka}", oznaka)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(PredmetDTO.class);
    }

    public PredmetDTO update(Long id, PredmetRequestDTO dto) {
        return webClient
                .put()
                .uri("/api/predmet/{id}", id)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(PredmetDTO.class)
                .block();
    }

    public void delete(Long id) {
        webClient
                .delete()
                .uri("/api/predmet/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public ProsecnaOcenaDTO getProsecnaOcena(String predmetSifra, Integer godinaOd, Integer godinaDo) {
        return webClient
                .get()
                .uri("/api/predmet/" + predmetSifra + "/prosecna-ocena?godinaOd=" + godinaOd + "&godinaDo=" + godinaDo)
                .retrieve()
                .bodyToMono(ProsecnaOcenaDTO.class)
                .block();
    }

    public Mono<ProsecnaOcenaDTO> getProsecnaOcenaAsync(String predmetSifra, Integer godinaOd, Integer godinaDo) {
        return webClient
                .get()
                .uri("/api/predmet/" + predmetSifra + "/prosecna-ocena?godinaOd=" + godinaOd + "&godinaDo=" + godinaDo)
                .retrieve()
                .bodyToMono(ProsecnaOcenaDTO.class);
    }
}
