package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import dto.response.SkolskaGodinaDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;




@Service
@AllArgsConstructor
public class SkolskaGodinaService {

    private WebClient webClient;


    public Flux<SkolskaGodinaDTO> getAllAsync() {
        return webClient
                .get()
                .uri("/api/skolska-godina")
                .retrieve()
                .bodyToFlux(SkolskaGodinaDTO.class);
    }


}
