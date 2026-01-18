package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import dto.response.StudijskiProgramDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class StudijskiProgramService {

    private WebClient webClient;

    public Flux<StudijskiProgramDTO> getAllStudijskiProgramiAsync() {
        return webClient
                .get()
                .uri("/api/studijski-program")
                .retrieve()
                .bodyToFlux(StudijskiProgramDTO.class);
    }

}
