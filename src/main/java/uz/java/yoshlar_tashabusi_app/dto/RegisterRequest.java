package uz.java.yoshlar_tashabusi_app.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RegisterRequest {
    private String documentNumber;
    private String documentSeries;
    private LocalDate birthDate;
}
