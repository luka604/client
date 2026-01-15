package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetaljiPolaganjaDTO {
    private LocalDate datumIspita;
    private Integer osvojeniPoeni;
    private Boolean ponisteno;
}
