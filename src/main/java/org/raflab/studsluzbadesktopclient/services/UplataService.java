package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.PreostaliIznosDTO;
import org.raflab.studsluzbadesktopclient.dtos.UplataDTO;
import org.raflab.studsluzbadesktopclient.dtos.UplataRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UplataService {

    private WebClient webClient;

    public Mono<UplataDTO> dodajUplatuAsync(String brojIndeksa, UplataRequestDTO request) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/uplata")
                        .queryParam("brojIndeksa", brojIndeksa)
                        .build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UplataDTO.class);
    }

    public Mono<PreostaliIznosDTO> getPreostaliIznosAsync(String brojIndeksa) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/uplata/preostali-iznos")
                        .queryParam("brojIndeksa", brojIndeksa)
                        .build())
                .retrieve()
                .bodyToMono(PreostaliIznosDTO.class);
    }
}
