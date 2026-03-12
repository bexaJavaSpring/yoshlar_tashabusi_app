package uz.java.yoshlar_tashabusi_app.service;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.java.yoshlar_tashabusi_app.dto.ImportResultDto;
import uz.java.yoshlar_tashabusi_app.dto.UserDto;
import uz.java.yoshlar_tashabusi_app.entity.User;
import uz.java.yoshlar_tashabusi_app.repository.AttachmentRepository;
import uz.java.yoshlar_tashabusi_app.repository.UserRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final ExcelParserService excelParserService;
    private final UserRepository userRepository;
    private final AttachmetService attachmetService;
    private final SportTypeService sportTypeService;
    private final SportTypeCategoryService sportTypeCategoryService;

    public UserService(ExcelParserService excelParserService, UserRepository userRepository, AttachmentRepository attachmentRepository, AttachmetService attachmetService, SportTypeService sportTypeService, SportTypeCategoryService sportTypeCategoryService) {
        this.excelParserService = excelParserService;
        this.userRepository = userRepository;
        this.attachmetService = attachmetService;
        this.sportTypeService = sportTypeService;
        this.sportTypeCategoryService = sportTypeCategoryService;
    }

    /*
              Buyerda biz faqat Userning Passport Seriya Raqamini va BirthDate sini saqlashiligimiz kerak.
              Qolgan barcha ma'lumotlar agentlik API sidan olib kelinishligi kerak
              */
    @Transactional
    @SneakyThrows
    public ImportResultDto importFromExcel(MultipartFile file) {

        // 1. Fayl formatini tekshirish
        validateFileFormat(file);

        // 2. Excel ni parse qilish
        List<UserDto> parsedUsers = excelParserService.parseExcel(file);
        log.info("Parse qilindi: {} ta yozuv", parsedUsers.size());

        // 3. Har bir yozuvni validatsiya va saqlash
        List<String> errors = new ArrayList<>();
        int savedCount = 0;

        for (UserDto dto : parsedUsers) {
//            List<String> rowErrors = validate(dto);

//            if (!rowErrors.isEmpty()) {
//                rowErrors.forEach(e -> errors.add(dto.getRowNumber() + "-qator: " + e));
//                continue;
//            }

            // Email takrorlanishini tekshirish
//            if (userRepository.existsByEmail(dto.getEmail())) {
//                errors.add(dto.getRowNumber() + "-qator: " + dto.getEmail() + " — bu email allaqachon mavjud");
//                continue;
//            }

            // Saqlash

            try {
                User user = toEntity(dto);
                if (!userRepository.existsByDocumentSeriesNumber(user.getDocumentSeriesNumber())) {
                    userRepository.save(user);
                    savedCount++;
                    log.debug("Saqlandi: {}", dto.getDocumentSeriNumber());
                } else
                    log.debug("Saqlanmadi!!!!!!: {}", dto.getDocumentSeriNumber());
            } catch (Exception e) {
                System.out.printf("ERROR", e);
            }
        }

        log.info("Import tugadi — saqlandi: {}, xato: {}", savedCount, errors.size());
        return ImportResultDto.of(parsedUsers.size(), savedCount, errors);
    }

    // ─── Validatsiya ──────────────────────────────────────────────────────────

    private List<String> validate(UserDto dto) {
        List<String> errors = new ArrayList<>();

        if (isBlank(dto.getFirstName())) errors.add("firstName bo'sh bo'lmasligi kerak");
        if (isBlank(dto.getLastName())) errors.add("lastName bo'sh bo'lmasligi kerak");
        if (isBlank(dto.getEmail())) errors.add("email bo'sh bo'lmasligi kerak");
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
//                .firstName(dto.getFirstName())
//                .lastName(dto.getLastName())
//                .email(dto.getEmail().toLowerCase())
                .documentSeriesNumber(dto.getDocumentSeriNumber())
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

    /*
     * Bizning local bazamizda bor userlarni tekshirib chiqadi.
     * Agar barcha kerakli ma'lumotlari to'ldirilgan bo'lmasa
     * Tashabbus API sidan to'ldirishlikni boshlaydi
     * */
    @Transactional
    public List<UserDto> changeUserFields() {
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            /*
             *               MUHAMMADOQDIR
             * Add age categor method
             * Add Region
             *
             * */


            /*
             *       BEXRUZ
             *   Add Sport TpyeCategory method
             *   Add Sport Tpye method
             * */
            User updatedUser = sportTypeCategoryService.syncSportTypeCategories(user);
            sportTypeService.syncSportTypes(updatedUser);
//
            if (user.getIsFullData())
                continue;
            if (!changeData(user))
                userDtos.add(new UserDto(user.getDocumentSeriesNumber(), user.getDateOfBirth()));
        }

        return userDtos;
    }

    public boolean changeData(User user) {
        User findUser = findUser(user);
        if (findUser == null) return false;
        findUser.setIsFullData(true);
        userRepository.save(findUser);
        return true;
    }

    /*
     * Tashabbus bazasidan userning passpot seriyraqam va birthdate orqali qidirib topadi
     * */

    public User findUser(User user) {
        try {
            String urlString = "https://api.5tashabbus.uz/Account/GetAthleteInfoForRegistration?" +
                    "DocumentSeries=" + user.getDocumentSeriesNumber().substring(0, 2) +
                    "&DocumentNumber=" + user.getDocumentSeriesNumber().substring(2) +
                    "&DateOfBirth=" + user.getDateOfBirth() +
                    "&identityDocumentId=2" +
                    "&lang=uz_latn" +
                    "&initiativTypeId=1" +
                    "&captchaText=";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 1. Set Method and Output
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(0); // Specifically tells the server the body is empty

            // 2. Essential Headers
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            conn.setRequestProperty("Accept", "application/json, text/plain, */*");
            conn.setRequestProperty("Origin", "https://5tashabbus.uz");
            conn.setRequestProperty("Referer", "https://5tashabbus.uz/");
            conn.setRequestProperty("Content-Type", "application/json");


            int status = conn.getResponseCode();
            System.out.println("status = " + status);

            try (InputStream stream = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream()) {
                if (stream == null) return null;

                BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                JSONObject object = new JSONObject(response.toString());
                if (object.isNull("result")) return null; // Safety check

                JSONObject result = object.getJSONObject("result");
                user.setFamilyName(result.optString("familyname", ""));
                user.setFirstName(result.optString("firstname", ""));
                user.setLastName(result.optString("lastname", ""));
                user.setShortName(result.optString("shortname", ""));
                user.setFullName(result.optString("fullname", "Noma'lum"));
                user.setGenderId(result.optInt("genderid", 0));
                user.setGenderName(result.optString("gendername", ""));
                user.setIdentityDocumentId(result.optInt("identitydocumentid", 0));
                user.setIdentityDocumentName(result.optString("identitydocumentname", null));
                user.setPinfl(result.optString("pinfl", ""));

                if (result.isNull("photo")) {
                    user.setAttachment(attachmetService.saveAttachment(result.getJSONObject("photo")));
                }
                return user;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }
    }

}
