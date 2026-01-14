package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentPrijavaDTO {
    private String brojIndeksa;
    private String ime;
    private String prezime;
    private LocalDate datumPrijave;
}
