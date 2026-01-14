package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IspitDTO {
    private Long id;
    private LocalDate datumOdrzavanja;
    private LocalTime vremePocetka;
    private Boolean zakljucan;
    private String ispitniRokName;
    private String predmetNaziv;
    private Long ispitniRokId;
    private Long predmetId;

    @Override
    public String toString() {
        return predmetNaziv + " - " + datumOdrzavanja + " " + vremePocetka;
    }
}
