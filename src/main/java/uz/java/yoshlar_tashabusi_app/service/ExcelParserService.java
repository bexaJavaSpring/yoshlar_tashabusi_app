package uz.java.yoshlar_tashabusi_app.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.java.yoshlar_tashabusi_app.dto.UserDto;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ExcelParserService {
    private static final int HEADER_ROW_INDEX = 0;

    public List<UserDto> parseExcel(MultipartFile file) throws IOException {
        List<UserDto> users = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);  // Birinchi varaq
            int totalRows = sheet.getPhysicalNumberOfRows();

            log.info("Excel fayl o'qilmoqda: {} qator topildi", totalRows);

            for (int i = HEADER_ROW_INDEX + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                try {
                    UserDto dto = UserDto.builder()
                            .firstName(getCellString(row, 0))
                            .lastName(getCellString(row, 1))
                            .email(getCellString(row, 2))
                            .phoneNumber(getCellString(row, 3))
                            .birthDate(getCellDate(row, 4))
                            .role(getCellString(row, 5))
                            .rowNumber(i + 1)
                            .build();

                    users.add(dto);
                } catch (Exception e) {
                    log.warn("{}-qatorda xato: {}", i + 1, e.getMessage());
                    // Xatoli qatorni o'tkazib yuboramiz, service qayta tekshiradi
                    UserDto errorDto = new UserDto();
                    errorDto.setRowNumber(i + 1);
                    users.add(errorDto);
                }
            }
        }

        log.info("Excel parse tugadi: {} ta yozuv o'qildi", users.size());
        return users;
    }

    // ─── Yordamchi metodlar ───────────────────────────────────────────────────

    private String getCellString(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                // Raqam bor bo'lsa string sifatida qaytaramiz (telefon uchun)
                if (DateUtil.isCellDateFormatted(cell)) yield null;
                long num = (long) cell.getNumericCellValue();
                yield String.valueOf(num);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCachedFormulaResultType() == CellType.STRING
                    ? cell.getStringCellValue().trim()
                    : String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }

    private LocalDate getCellDate(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        if (cell.getCellType() == CellType.STRING) {
            String val = cell.getStringCellValue().trim();
            if (val.isEmpty()) return null;
            // dd.MM.yyyy formatini parse qilamiz
            String[] parts = val.split("[./\\-]");
            if (parts.length == 3) {
                int day   = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year  = Integer.parseInt(parts[2]);
                return LocalDate.of(year, month, day);
            }
        }

        return null;
    }

    private boolean isRowEmpty(Row row) {
        for (int c = 0; c < 6; c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
}
