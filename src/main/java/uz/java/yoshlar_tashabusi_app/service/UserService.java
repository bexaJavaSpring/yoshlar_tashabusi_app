package uz.java.yoshlar_tashabusi_app.service;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.java.yoshlar_tashabusi_app.dto.ImportResultDto;
import uz.java.yoshlar_tashabusi_app.dto.UserDto;
import uz.java.yoshlar_tashabusi_app.entity.User;
import uz.java.yoshlar_tashabusi_app.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final ExcelParserService excelParserService;
    private final UserRepository userRepository;

    public UserService(ExcelParserService excelParserService, UserRepository userRepository) {
        this.excelParserService = excelParserService;
        this.userRepository = userRepository;
    }

    @Transactional
    @SneakyThrows
    public ImportResultDto importFromExcel(MultipartFile file) {

        // 1. Fayl formatini tekshirish
        validateFileFormat(file);

        // 2. Excel ni parse qilish
        List<UserDto> parsedUsers = excelParserService.parseExcel(file);
        log.info("Parse qilindi: {} ta yozuv", parsedUsers.size());

        // 3. Har bir yozuvni validatsiya va saqlash
        List<String> errors    = new ArrayList<>();
        int          savedCount = 0;

        for (UserDto dto : parsedUsers) {
            List<String> rowErrors = validate(dto);

            if (!rowErrors.isEmpty()) {
                rowErrors.forEach(e -> errors.add(dto.getRowNumber() + "-qator: " + e));
                continue;
            }

            // Email takrorlanishini tekshirish
            if (userRepository.existsByEmail(dto.getEmail())) {
                errors.add(dto.getRowNumber() + "-qator: " + dto.getEmail() + " — bu email allaqachon mavjud");
                continue;
            }

            // Saqlash
            User user = toEntity(dto);
            userRepository.save(user);
            savedCount++;
            log.debug("Saqlandi: {}", dto.getEmail());
        }

        log.info("Import tugadi — saqlandi: {}, xato: {}", savedCount, errors.size());
        return ImportResultDto.of(parsedUsers.size(), savedCount, errors);
    }

    // ─── Validatsiya ──────────────────────────────────────────────────────────

    private List<String> validate(UserDto dto) {
        List<String> errors = new ArrayList<>();

        if (isBlank(dto.getFirstName()))  errors.add("firstName bo'sh bo'lmasligi kerak");
        if (isBlank(dto.getLastName()))   errors.add("lastName bo'sh bo'lmasligi kerak");
        if (isBlank(dto.getEmail()))      errors.add("email bo'sh bo'lmasligi kerak");
        else if (!isValidEmail(dto.getEmail())) errors.add("email formati noto'g'ri: " + dto.getEmail());

        return errors;
    }

    private boolean isBlank(String val) {
        return val == null || val.isBlank();
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$");
    }

    // ─── Mapper ───────────────────────────────────────────────────────────────

    private User toEntity(UserDto dto) {
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail().toLowerCase())
                .phoneNumber(dto.getPhoneNumber())
                .dateOfBirth(dto.getBirthDate())
                .build();
    }

    // ─── Fayl tekshirish ──────────────────────────────────────────────────────

    private void validateFileFormat(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fayl bo'sh yuborildi");
        }

        String name = file.getOriginalFilename();
        if (name == null || (!name.endsWith(".xlsx") && !name.endsWith(".xls"))) {
            throw new IllegalArgumentException("Faqat .xlsx yoki .xls fayl qabul qilinadi");
        }
    }
}
