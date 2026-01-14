package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RezultatIspitaDTO {
    private String brojIndeksa;
    private String ime;
    private String prezime;
    private Integer predispitniPoeni;
    private Integer poeniNaIspitu;
    private Integer ukupniPoeni;
    private Integer ocena;
    private Boolean polozen;
}
