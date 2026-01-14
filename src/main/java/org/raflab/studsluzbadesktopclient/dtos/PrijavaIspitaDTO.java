package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrijavaIspitaDTO {
    private Long id;
    private LocalDate datumPrijave;
    private String ispitNaziv;
}
