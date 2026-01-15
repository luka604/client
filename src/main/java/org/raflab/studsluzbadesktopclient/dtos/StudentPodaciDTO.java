package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentPodaciDTO {
    private String ime;
    private String prezime;
    private String srednjeIme;
    private Character pol;
    private String brojTelefonaMobilni;
    private String email;
    private Long srednjaSkolaId;
    private Double uspehSrednjaSkola;
    private Double uspehPrijemni;

    @Override
    public String toString() {
        return ime + " " + prezime;
    }
}
