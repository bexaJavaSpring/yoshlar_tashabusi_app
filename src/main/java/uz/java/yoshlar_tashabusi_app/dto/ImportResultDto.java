package uz.java.yoshlar_tashabusi_app.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportResultDto {
    private int totalRows;
    private int successCount;
    private int failedCount;
    private List<String> errors;

    public static ImportResultDto of(int total, int success, List<String> errors) {
        return ImportResultDto.builder()
                .totalRows(total)
                .successCount(success)
                .failedCount(errors.size())
                .errors(errors)
                .build();
    }
}
