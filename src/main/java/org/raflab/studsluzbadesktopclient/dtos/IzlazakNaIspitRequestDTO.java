package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IzlazakNaIspitRequestDTO {
    private Long prijavaId;
    private Integer brojPoena;
    private String napomena;
}
