package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IspitniRokDTO {
    private Long id;
    private String naziv;
    private LocalDate pocetak;
    private LocalDate kraj;
    private Long skolskaGodinaId;

    @Override
    public String toString() {
        return naziv + " (" + pocetak + " - " + kraj + ")";
    }
}
