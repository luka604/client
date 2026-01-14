package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IspitRequestDTO {
    private LocalDate datumOdrzavanja;
    private LocalTime vremePocetka;
    private Boolean zakljucan;
    private Long ispitniRokId;
    private String predmetSifra;
}
