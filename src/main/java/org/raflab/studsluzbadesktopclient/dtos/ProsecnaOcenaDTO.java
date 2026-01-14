package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProsecnaOcenaDTO {
    private String predmetNaziv;
    private Double prosecnaOcena;
    private Integer brojStudenata;
    private Map<Integer, Long> distribucijaOcena;
}
