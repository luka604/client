package org.raflab.studsluzbadesktopclient.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.BrojPolaganjaDTO;
import org.raflab.studsluzbadesktopclient.dtos.IzlazakNaIspitDTO;
import org.raflab.studsluzbadesktopclient.dtos.IzlazakNaIspitRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class IzlazakNaIspitService {

    private WebClient webClient;

    public IzlazakNaIspitDTO unosIzlaska(IzlazakNaIspitRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/izlazak")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IzlazakNaIspitDTO.class)
                .block();
    }

    public Mono<IzlazakNaIspitDTO> unosIzlaskaAsync(IzlazakNaIspitRequestDTO dto) {
        return webClient
                .post()
                .uri("/api/izlazak")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(IzlazakNaIspitDTO.class);
    }

    public BrojPolaganjaDTO getBrojPolaganja(String brojIndeksa, String predmetSifra) {
        return webClient
                .get()
                .uri("/api/izlazak/broj-polaganja?brojIndeksa=" + brojIndeksa + "&predmetId=" + predmetSifra)
                .retrieve()
                .bodyToMono(BrojPolaganjaDTO.class)
                .block();
    }

    public Mono<BrojPolaganjaDTO> getBrojPolaganjaAsync(String brojIndeksa, String predmetSifra) {
        return webClient
                .get()
                .uri("/api/izlazak/broj-polaganja?brojIndeksa=" + brojIndeksa + "&predmetId=" + predmetSifra)
                .retrieve()
                .bodyToMono(BrojPolaganjaDTO.class);
    }
}
