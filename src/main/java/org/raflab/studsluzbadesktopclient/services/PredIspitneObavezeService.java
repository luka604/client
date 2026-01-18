package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import dto.response.PredispitniPoeniDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@AllArgsConstructor
public class PredIspitneObavezeService {

    private WebClient webClient;


    public Flux<PredispitniPoeniDTO> getPredispitniPoeniAsync(String brojIndeksa, Long predmetId, Long skolskaGodinaId) {
        return webClient
                .get()
                .uri("/api/obaveze?brojIndeksa=" + brojIndeksa + "&predmetId=" + predmetId + "&skolskaGodinaId=" + skolskaGodinaId)
                .retrieve()
                .bodyToFlux(PredispitniPoeniDTO.class);
    }
}
