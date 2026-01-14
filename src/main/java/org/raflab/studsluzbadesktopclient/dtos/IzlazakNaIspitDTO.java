package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IzlazakNaIspitDTO {
    private Integer brojPoena;
    private String napomena;
    private Boolean ponisteno;
    private Long prijavaId;
}
