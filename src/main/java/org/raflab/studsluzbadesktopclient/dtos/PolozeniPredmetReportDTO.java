package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Jasper Reports - Certificate of passed exams.
 * Contains calculated godinaStudija field for grouping.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolozeniPredmetReportDTO {
    private String predmetNaziv;
    private String predmetSifra;
    private Integer espb;
    private Integer ocena;
    private String datumPolaganja;
    private Integer godinaStudija;

    /**
     * Creates a report DTO from PolozeniPredmetDTO.
     * Calculates godinaStudija based on semester: 1-2 = Year 1, 3-4 = Year 2, etc.
     */
    public static PolozeniPredmetReportDTO fromPolozeniPredmetDTO(PolozeniPredmetDTO dto) {
        PolozeniPredmetReportDTO reportDTO = new PolozeniPredmetReportDTO();

        if (dto.getPredmet() != null) {
            reportDTO.setPredmetNaziv(dto.getPredmet().getNaziv());
            reportDTO.setPredmetSifra(dto.getPredmet().getSifra());
            reportDTO.setEspb(dto.getPredmet().getEspb());

            Integer semestar = dto.getPredmet().getSemestar();
            if (semestar != null) {
                reportDTO.setGodinaStudija((semestar + 1) / 2);
            } else {
                reportDTO.setGodinaStudija(1);
            }
        }

        reportDTO.setOcena(dto.getOcena());

        if (dto.getDatumPolaganja() != null) {
            reportDTO.setDatumPolaganja(dto.getDatumPolaganja().format(
                java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
        }

        return reportDTO;
    }
}
