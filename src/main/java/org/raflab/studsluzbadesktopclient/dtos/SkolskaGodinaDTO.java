package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkolskaGodinaDTO {
    private Long id;
    private String naziv;
    private LocalDate pocetak;
    private LocalDate kraj;
    private Boolean aktivna;

    @Override
    public String toString() {
        return naziv;
    }
}
