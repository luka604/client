package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlusaPredmetDTO {
    private Long id;
    private String brojIndeksa;
    private String predmetNaziv;
    private String predmetSifra;

    @Override
    public String toString() {
        return predmetSifra + " - " + predmetNaziv;
    }
}
