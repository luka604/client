package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrijavaIspitaRequestDTO {
    private String brojIndeksa;
    private Long ispitId;
    private Long ispitniRokId;
}
