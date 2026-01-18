package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import dto.request.IzlazakNaIspitRequestDTO;
import dto.response.BrojPolaganjaDTO;
import dto.response.IzlazakNaIspitDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class IzlazakNaIspitService {

    private WebClient webClient;

    public Mono<IzlazakNaIspitDTO> unosIzlaskaAsync(IzlazakNaIspitRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/izlazak")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IzlazakNaIspitDTO.class);
    }

    public Mono<BrojPolaganjaDTO> getBrojPolaganjaAsync(String brojIndeksa, String predmetSifra) {
        return webClient
                .get()
                .uri("/api/izlazak/broj-polaganja?brojIndeksa=" + brojIndeksa + "&predmetId=" + predmetSifra)
                .retrieve()
                .bodyToMono(BrojPolaganjaDTO.class);
    }
}
