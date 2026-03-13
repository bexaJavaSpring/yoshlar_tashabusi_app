package uz.java.yoshlar_tashabusi_app.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthDate;
    private int rowNumber;
    private String documentSeriNumber;

    public UserDto(String documentSeriesNumber, LocalDate dateOfBirth) {
        this.documentSeriNumber = documentSeriesNumber;
        this.birthDate = dateOfBirth;
    }
}
