package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredispitniPoeniDTO {
    private String vrstaObaveze;
    private Integer maxPoena;
    private Integer osvojeniPoeni;
}
