package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import dto.request.SlusaPredmetRequestDTO;
import dto.response.SlusaPredmetDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class SlusaPredmetService {

    private WebClient webClient;

    public SlusaPredmetDTO upisiStudentaNaPredmet(SlusaPredmetRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/slusa-predmet")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(SlusaPredmetDTO.class)
                .block();
    }

    public Mono<SlusaPredmetDTO> upisiStudentaNaPredmetAsync(SlusaPredmetRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/slusa-predmet")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(SlusaPredmetDTO.class);
    }

    public List<SlusaPredmetDTO> getPredmetiZaStudenta(String brojIndeksa) {
        return webClient
                .get()
                .uri("/api/slusa-predmet/{brojIndeksa}", brojIndeksa)
                .retrieve()
                .bodyToFlux(SlusaPredmetDTO.class)
                .collectList()
                .block();
    }

    public Flux<SlusaPredmetDTO> getPredmetiZaStudentaAsync(String brojIndeksa) {
        return webClient
                .get()
                .uri("/api/slusa-predmet/{brojIndeksa}", brojIndeksa)
                .retrieve()
                .bodyToFlux(SlusaPredmetDTO.class);
    }
}
