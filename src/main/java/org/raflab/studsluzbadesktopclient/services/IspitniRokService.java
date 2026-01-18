package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import dto.response.IspitniRokDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class IspitniRokService {

    private WebClient webClient;

    public List<IspitniRokDTO> getAllIspitniRokovi() {
        return webClient
                .get()
                .uri("/api/ispitni-rok")
                .retrieve()
                .bodyToFlux(IspitniRokDTO.class)
                .collectList()
                .block();
    }

    public Flux<IspitniRokDTO> getAllIspitniRokoviAsync() {
        return webClient
                .get()
                .uri("/api/ispitni-rok")
                .retrieve()
                .bodyToFlux(IspitniRokDTO.class);
    }

    public IspitniRokDTO getById(Long id) {
        return webClient
                .get()
                .uri("/api/ispitni-rok/{id}", id)
                .retrieve()
                .bodyToMono(IspitniRokDTO.class)
                .block();
    }

    public Mono<IspitniRokDTO> getByIdAsync(Long id) {
        return webClient
                .get()
                .uri("/api/ispitni-rok/{id}", id)
                .retrieve()
                .bodyToMono(IspitniRokDTO.class);
    }

    public IspitniRokDTO create(IspitniRokDTO dto) {
        return webClient
                .post()
                .uri("/api/ispitni-rok")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IspitniRokDTO.class)
                .block();
    }

    public Mono<IspitniRokDTO> createAsync(IspitniRokDTO dto) {
        return webClient
                .post()
                .uri("/api/ispitni-rok")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IspitniRokDTO.class);
    }

    public IspitniRokDTO update(Long id, IspitniRokDTO dto) {
        return webClient
                .put()
                .uri("/api/ispitni-rok/{id}", id)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IspitniRokDTO.class)
                .block();
    }

    public void delete(Long id) {
        webClient
                .delete()
                .uri("/api/ispitni-rok/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
