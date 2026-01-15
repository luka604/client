package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpisGodineDTO {
    private Integer godinaKojuUpisuje;
    private LocalDate datumUpisa;
    private String napomena;
    private String brojIndeksa;
    private List<PredmetDTO> preneseniPredmeti;

    @Override
    public String toString() {
        return godinaKojuUpisuje + ". godina - " + datumUpisa;
    }
}
