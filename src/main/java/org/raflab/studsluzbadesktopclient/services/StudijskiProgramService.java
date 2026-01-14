package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.StudijskiProgramDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class StudijskiProgramService {

    private WebClient webClient;

    public List<StudijskiProgramDTO> getAllStudijskiProgrami() {
        return webClient
                .get()
                .uri("/api/studijski-program")
                .retrieve()
                .bodyToFlux(StudijskiProgramDTO.class)
                .collectList()
                .block();
    }

    public Flux<StudijskiProgramDTO> getAllStudijskiProgramiAsync() {
        return webClient
                .get()
                .uri("/api/studijski-program")
                .retrieve()
                .bodyToFlux(StudijskiProgramDTO.class);
    }

    public StudijskiProgramDTO getByOznaka(String oznaka) {
        return webClient
                .get()
                .uri("/api/studijski-program/{oznaka}", oznaka)
                .retrieve()
                .bodyToMono(StudijskiProgramDTO.class)
                .block();
    }

    public Mono<StudijskiProgramDTO> getByOznakaAsync(String oznaka) {
        return webClient
                .get()
                .uri("/api/studijski-program/{oznaka}", oznaka)
                .retrieve()
                .bodyToMono(StudijskiProgramDTO.class);
    }

    public StudijskiProgramDTO create(StudijskiProgramDTO dto) {
        return webClient
                .post()
                .uri("/api/studijski-program")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(StudijskiProgramDTO.class)
                .block();
    }

    public Mono<StudijskiProgramDTO> createAsync(StudijskiProgramDTO dto) {
        return webClient
                .post()
                .uri("/api/studijski-program")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(StudijskiProgramDTO.class);
    }

    public StudijskiProgramDTO update(Long id, StudijskiProgramDTO dto) {
        return webClient
                .put()
                .uri("/api/studijski-program/{id}", id)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(StudijskiProgramDTO.class)
                .block();
    }

    public void delete(Long id) {
        webClient
                .delete()
                .uri("/api/studijski-program/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
