package org.raflab.studsluzbadesktopclient.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.raflab.studsluzbadesktopclient.dtos.PageResponse;
import org.raflab.studsluzbadesktopclient.dtos.StudentPodaciDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class StudentPodaciService {

    private WebClient webClient;

    public StudentPodaciDTO getStudentByBrojIndeksa(String brojIndeksa) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/student/indeks")
                        .queryParam("brojIndeksa", brojIndeksa)
                        .build())
                .retrieve()
                .bodyToMono(StudentPodaciDTO.class)
                .block();
    }

    public Mono<StudentPodaciDTO> getStudentByBrojIndeksaAsync(String brojIndeksa) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/student/indeks")
                        .queryParam("brojIndeksa", brojIndeksa)
                        .build())
                .retrieve()
                .bodyToMono(StudentPodaciDTO.class);
    }

    public Mono<PageResponse<StudentPodaciDTO>> pretragaStudenataAsync(String ime, String prezime, int page, int size) {
        return webClient
                .get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/api/student/studenti")
                            .queryParam("page", page)
                            .queryParam("size", size);
                    if (ime != null && !ime.isEmpty()) {
                        builder.queryParam("ime", ime);
                    }
                    if (prezime != null && !prezime.isEmpty()) {
                        builder.queryParam("prezime", prezime);
                    }
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<StudentPodaciDTO>>() {});
    }

    public Flux<StudentPodaciDTO> getStudentiBySrednjaSkolaAsync(Long srednjaSkolaId) {
        return webClient
                .get()
                .uri("/api/student/srednja-skola/{srednjaSkolaId}", srednjaSkolaId)
                .retrieve()
                .bodyToFlux(StudentPodaciDTO.class);
    }
}
