package uz.java.yoshlar_tashabusi_app.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.java.yoshlar_tashabusi_app.dto.ImportResultDto;
import uz.java.yoshlar_tashabusi_app.dto.UserDto;
import uz.java.yoshlar_tashabusi_app.entity.*;
import uz.java.yoshlar_tashabusi_app.repository.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final ExcelParserService excelParserService;
    private final UserRepository userRepository;
    private final AttachmetService attachmetService;
    private final SportTypeService sportTypeService;
    private final SportTypeCategoryService sportTypeCategoryService;
    private final SportTypeRepository sportTypeRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final SportTypeCategoryRepository sportTypeCategoryRepository;

    public UserService(ExcelParserService excelParserService, UserRepository userRepository, AttachmentRepository attachmentRepository, AttachmetService attachmetService, SportTypeService sportTypeService, SportTypeCategoryService sportTypeCategoryService, SportTypeRepository sportTypeRepository, AgeCategoryRepository ageCategoryRepository, SportTypeCategoryRepository sportTypeCategoryRepository) {
        this.excelParserService = excelParserService;
        this.userRepository = userRepository;
        this.attachmetService = attachmetService;
        this.sportTypeService = sportTypeService;
        this.sportTypeCategoryService = sportTypeCategoryService;
        this.sportTypeRepository = sportTypeRepository;
        this.ageCategoryRepository = ageCategoryRepository;
        this.sportTypeCategoryRepository = sportTypeCategoryRepository;
    }

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
                    user.setAttachment(attachmetService.saveAttachment(result.getJSONObject("photo")));
                }
                return user;
            } catch (Exception e) {
                System.out.println(e);
                return null;
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Integer> insertData(Integer mfyId) {
        List<User> list = userRepository.findByMfyId(mfyId);
        List<Integer> failedUserIds = new ArrayList<>();
        for (User user : list) {
            try {
                boolean result = sendUserToApi(user);
                if (!result) failedUserIds.add(user.getId());
            } catch (Exception e) {
                System.out.println("User " + user.getId() + " xatolik: " + e.getMessage());
                failedUserIds.add(user.getId());
            }
        }
        return failedUserIds;
    }

    private boolean sendUserToApi(User user) throws Exception {
        AgeCategory ageCategory = findAgeCategoryByBirthDate(user.getDateOfBirth());
        Address address = user.getAddress();
        List<SportyType> sportTypeList = sportTypeRepository.findAllByAgeCategory(ageCategory.getId());
        Integer successSportTypeId = null;
        for (SportyType sportType : sportTypeList) {
            Integer result = sendRequest(user, address, ageCategory, sportType);
            if (result != null) {
                successSportTypeId = result;
                System.out.println("User " + user.getId() + " uchun sportTypeId: " + successSportTypeId + " muvaffaqiyatli!");
                break;
            } else {
                System.out.println("User " + user.getId() + " — sportType " + sportType.getId() + " mos kelmadi, keyingisi...");
            }
        }

        if (successSportTypeId == null) {
            System.out.println("User " + user.getId() + " uchun hech qaysi sportType mos kelmadi!");
            return false;
        }

        user.getSportTypeIdList().add(successSportTypeId);
        userRepository.save(user);
        return true;
    }

    private Integer sendRequest(User user, Address address, AgeCategory ageCategory, SportyType sportType) throws Exception {
        SportTypeCategory category = sportType.getSportTypeCategory();
        JSONObject body = new JSONObject();
        body.put("id", 0);
        body.put("healthtypeid", user.getHealthTypeId());
        body.put("detail", user.getDetail() != null ? user.getDetail() : "");
        body.put("oblastid", address != null ? address.getOblastId() : JSONObject.NULL);
        body.put("oblastname", address != null ? address.getOblastName() : "");
        body.put("regionid", address != null ? address.getRegionId() : JSONObject.NULL);
        body.put("regionname", address != null ? address.getRegionName() : "");
        body.put("mfyid", address != null ? address.getMfyId() : JSONObject.NULL);
        body.put("regionsectorid", JSONObject.NULL);
        body.put("regionsectorname", "");
        body.put("youthleaderpersonid", user.getYouthLeaderPersonId() != null ? user.getYouthLeaderPersonId() : JSONObject.NULL);
        body.put("familyname", user.getFamilyName());
        body.put("firstname", user.getFirstName());
        body.put("lastname", user.getLastName());
        body.put("shortname", user.getShortName());
        body.put("fullname", user.getFullName());
        body.put("dateofbirth", sportTypeService.formatDate(user.getDateOfBirth()));
        body.put("pinfl", user.getPinfl());
        body.put("genderid", user.getGenderId());
        body.put("gendername", user.getGenderName());
        body.put("identitydocumentid", user.getIdentityDocumentId());
        body.put("identitydocumentname", user.getIdentityDocumentName() != null ? user.getIdentityDocumentName() : JSONObject.NULL);
        body.put("documentseries", user.getDocumentSeriesNumber().substring(0, 2));
        body.put("documentnumber", user.getDocumentSeriesNumber().substring(2));
        body.put("sporttypeids", new JSONArray(List.of(sportType.getId())));
        body.put("canSave", true);
        body.put("agecategoryid", ageCategory != null ? ageCategory.getId() : JSONObject.NULL);
        body.put("sporttypecategoryid", category != null ? category.getId() : JSONObject.NULL);
        body.put("sporttypecategoryname", category != null ? category.getName() : "");
        body.put("isimport", user.isImport());
        body.put("initiativtypeid", user.getInitiativTypeId());
        body.put("initiativtypename", user.getInitiativTypeName() != null ? user.getInitiativTypeName() : "");
        body.put("userId", 0);
        body.put("phonenumber", user.getPhoneNumber());

        Attachment att = user.getAttachment();
        if (att != null) {
            JSONObject photo = new JSONObject();
            photo.put("id", att.getPhotoId() != null ? att.getPhotoId() : 0);
            photo.put("ownerid", att.getOwnerId() != null ? att.getOwnerId() : 0);
            photo.put("attachmentfileid", att.getAttachmentFileId());
            photo.put("attachmentfilename", att.getAttachmentFileName());
            photo.put("attachmentfiletype", att.getAttachmentFileType());
            photo.put("Status", att.getStatus() != null ? att.getStatus() : 1);
            body.put("photo", photo);
        } else {
            body.put("photo", JSONObject.NULL);
        }

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
        System.out.println("SportType " + sportType.getId() + " → HTTP status: " + status);
        try (InputStream stream = (status >= 200 && status < 300)
                ? conn.getInputStream() : conn.getErrorStream()) {
            if (stream == null) return null;
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            System.out.println("SportType " + sportType.getId() + " response: " + sb);
            JSONObject response = new JSONObject(sb.toString());
            boolean success = response.optBoolean("success", false);
            return success ? sportType.getId() : null;
        }
    }

    private AgeCategory findAgeCategoryByBirthDate(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return null;
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        return ageCategoryRepository.findByAge(age).orElse(null);
    }
}
