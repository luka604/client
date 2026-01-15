package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObnovaGodineDTO {
    private Integer godinaKojuObnavlja;
    private LocalDate datumObnove;
    private String napomena;
    private List<PredmetDTO> predmeti;

    @Override
    public String toString() {
        return godinaKojuObnavlja + ". godina (obnova) - " + datumObnove;
    }
}
