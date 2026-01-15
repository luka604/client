package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpisGodineRequestDTO {
    private Integer godinaKojuUpisuje;
    private String napomena;
    private List<Long> preneseniPredmetiIds;
}
