package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudijskiProgramDTO {
    private Long id;
    private String oznaka;
    private String naziv;
    private Integer godinaAkreditacije;
    private String nazivZvanja;
    private Integer espBodovi;
    private Integer trajanjeUSemestrima;
    private String vrstaStudija;

    @Override
    public String toString() {
        return oznaka + " - " + naziv;
    }
}
