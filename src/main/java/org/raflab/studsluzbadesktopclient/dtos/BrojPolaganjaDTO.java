package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrojPolaganjaDTO {
    private String brojIndeksa;
    private String predmetNaziv;
    private Integer brojPolaganja;
    private List<DetaljiPolaganjaDTO> detaljiPolaganja;
}
