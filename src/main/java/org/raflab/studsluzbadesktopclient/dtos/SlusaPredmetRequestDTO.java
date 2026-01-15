package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlusaPredmetRequestDTO {
    private String brojIndeksa;
    private String sifraPredmeta;
    private Long skolskaGodinaId;
}
