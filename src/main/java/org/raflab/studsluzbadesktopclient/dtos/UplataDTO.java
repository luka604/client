package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UplataDTO {
    private LocalDate datumUplate;
    private BigDecimal iznosRSD;
    private BigDecimal srednjiKurs;
    private BigDecimal iznosEUR;

    @Override
    public String toString() {
        return datumUplate + " - " + iznosRSD + " RSD (" + iznosEUR + " EUR)";
    }
}
