package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreostaliIznosDTO {
    private BigDecimal preostaloEUR;
    private BigDecimal preostaloRSD;
    private BigDecimal ukupnoUplacenoEUR;
    private BigDecimal srednjiKurs;
}
