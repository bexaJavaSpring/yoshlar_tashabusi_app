package uz.java.yoshlar_tashabusi_app.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.java.yoshlar_tashabusi_app.dto.ImportResultDto;
import uz.java.yoshlar_tashabusi_app.dto.RegisterRequest;
import uz.java.yoshlar_tashabusi_app.dto.UserDto;
import uz.java.yoshlar_tashabusi_app.entity.*;
import uz.java.yoshlar_tashabusi_app.repository.AgeCategoryRepository;
import uz.java.yoshlar_tashabusi_app.repository.SportTypeRepository;
import uz.java.yoshlar_tashabusi_app.repository.UserRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final ExcelParserService excelParserService;
    private final UserRepository userRepository;
    private final AttachmentService attachmentService;
    private final SportTypeRepository sportTypeRepository;
    private final AgeCategoryRepository ageCategoryRepository;

    /*
              Buyerda biz faqat Userning Passport Seriya Raqamini va BirthDate sini saqlashiligimiz kerak.
              Qolgan barcha ma'lumotlar agentlik API sidan olib kelinishligi kerak
              */
    @Transactional
    @SneakyThrows
    public ImportResultDto importFromExcel(MultipartFile file) {
        validateFileFormat(file);
        List<UserDto> parsedUsers = excelParserService.parseExcel(file);
        log.info("Parse qilindi: {} ta yozuv", parsedUsers.size());
        List<String> errors = new ArrayList<>();
        int savedCount = 0;

        for (UserDto dto : parsedUsers) {
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

    private User toEntity(UserDto dto) {
        return User.builder()
                .documentSeriesNumber(dto.getDocumentSeriNumber())
                .phoneNumber(dto.getPhoneNumber())
                .dateOfBirth(dto.getBirthDate())
                .build();
    }

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
            if (user.getIsFullData() != null && user.getIsFullData())
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
                    user.setAttachment(attachmentService.saveAttachment(result.getJSONObject("photo")));
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

    @Transactional
    @SneakyThrows
    public Boolean insertData(List<RegisterRequest> request) {
        for (RegisterRequest r : request) {
            List<User> users = userRepository.findByDocumentSeriesNumberAndDateOfBirth(r.getDocumentSeries() + r.getDocumentNumber(), r.getBirthDate());
            for (User u : users) {
                sendRequest(u);
            }
        }
        return true;
    }

    public boolean sendRequest(User user) throws Exception {
//        randomda SportType dan 1tasini olamiz
        JSONObject body = new JSONObject();
        body.put("dateofbirth", formatDate(user.getDateOfBirth()));
        body.put("documentseries", user.getDocumentSeriesNumber().substring(0, 2));
        body.put("documentnumber", user.getDocumentSeriesNumber().substring(2));
        body.put("sporttypeids", new JSONArray(List.of()));
        URL url = new URL("https://api.5tashabbus.uz/Account/InsertRegistrationOfAthlete");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        conn.setRequestProperty("Accept", "application/json, text/plain, */*");
        conn.setRequestProperty("Origin", "https://5tashabbus.uz");
        conn.setRequestProperty("Referer", "https://5tashabbus.uz/");

        byte[] bodyBytes = body.toString().getBytes(StandardCharsets.UTF_8);
        conn.setFixedLengthStreamingMode(bodyBytes.length);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(bodyBytes);
        }

        int status = conn.getResponseCode();
        try (InputStream stream = (status >= 200 && status < 300)
                ? conn.getInputStream() : conn.getErrorStream()) {
            if (stream == null) return false;
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            JSONObject response = new JSONObject(sb.toString());
            boolean success = response.optBoolean("success", false);
            return success;
        }
    }

    public String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
