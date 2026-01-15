package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredmetDTO {
    private Long id;
    private String sifra;
    private String naziv;
    private String opis;
    private Integer espb;
    private String studijskiProgramOznaka;
    private Boolean obavezan;
    private Integer semestar;
    private Integer fondPredavanja;
    private Integer fondVezbi;

    @Override
    public String toString() {
        return sifra + " - " + naziv + " (" + espb + " ESPB)";
    }
}
