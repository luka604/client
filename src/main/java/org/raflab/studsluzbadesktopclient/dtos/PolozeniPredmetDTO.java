package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolozeniPredmetDTO {
    private PredmetDTO predmet;
    private Integer ocena;
    private LocalDate datumPolaganja;
    private boolean priznat;

    @Override
    public String toString() {
        return predmet.getNaziv() + " - Ocena: " + ocena;
    }
}
