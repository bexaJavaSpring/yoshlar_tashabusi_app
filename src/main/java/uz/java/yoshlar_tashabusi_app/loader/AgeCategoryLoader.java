package uz.java.yoshlar_tashabusi_app.loader;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uz.java.yoshlar_tashabusi_app.entity.Address;
import uz.java.yoshlar_tashabusi_app.entity.AgeCategory;
import uz.java.yoshlar_tashabusi_app.entity.SportTypeCategory;
import uz.java.yoshlar_tashabusi_app.entity.SportyType;
import uz.java.yoshlar_tashabusi_app.repository.AddressRepository;
import uz.java.yoshlar_tashabusi_app.repository.AgeCategoryRepository;
import uz.java.yoshlar_tashabusi_app.repository.SportTypeCategoryRepository;
import uz.java.yoshlar_tashabusi_app.repository.SportTypeRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AgeCategoryLoader implements CommandLineRunner {

    private final AgeCategoryRepository repository;
    private final AddressRepository addressRepository;
    private final SportTypeCategoryRepository sportTypeCategoryRepository;
    private final SportTypeRepository sportTypeRepository;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Override
    public void run(String... args) throws Exception {
        // Ma'lumotlar ro'yxati

        if (!"create".equals(ddlAuto)) {
            return;
        }
        List<SportTypeCategory> sportTypeCategories = sportTypeCategorysList();
        List<AgeCategory> ageCategories = ageCategoryList();
        List<Address> addresses = addressList();

        sportTypeCategoryRepository.saveAll(sportTypeCategories);
        repository.saveAll(ageCategories);
        addressRepository.saveAll(addresses);

        for (AgeCategory ageCategory : ageCategories) {
            String url = "https://api.5tashabbus.uz/SportType/GetAll?lang=uz_latn&agecategoryid=" + ageCategory.getId();
            String response = fetchGet(url);

            if (response == null || response.isBlank() || !response.trim().startsWith("[")) {
                System.out.println("AgeCategory " + ageCategory.getName() + " — response xato: " + response);
                continue;
            }

            JSONArray results = new JSONArray(response);
            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                int id = item.getInt("id");

                SportyType sportType = sportTypeRepository.findById(id).orElse(null);
                if (sportType == null) {
                    sportType = new SportyType();
                    sportType.setId(id);
                    sportType.setName(item.optString("name", ""));
                    sportType.setCommonType(item.isNull("commandtype") ? null : item.optInt("commandtype"));
                    sportType.setParticipantCount(item.optInt("participantcount", 0));
                }
                sportType.getAgeCategories().add(ageCategory);
                sportTypeRepository.save(sportType);
            }

            System.out.println("AgeCategory " + ageCategory.getName() + " uchun " + results.length() + " ta SportType saqlandi");
        }
        for (SportTypeCategory category : sportTypeCategories) {
            String urlString = "https://api.5tashabbus.uz/SportType/GetAll?lang=uz_latn&sporttypecategoryid=" + category.getId();
            String response;
            try {
                response = fetchGet(urlString);
            } catch (Exception e) {
                System.out.println("Category " + category.getName() + " — fetch xato: " + e.getMessage());
                continue;
            }

            if (response == null || response.isBlank() || !response.trim().startsWith("[")) {
                System.out.println("Category " + category.getName() + " — response xato: " + response);
                continue;
            }

            JSONArray results = new JSONArray(response);
            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                int id = item.getInt("id");

                SportyType sportType = sportTypeRepository.findById(id).orElse(new SportyType());
                if (sportType.getId() == null) {
                    sportType.setId(id);
                    sportType.setName(item.optString("name", ""));
                    sportType.setCommonType(item.isNull("commandtype") ? null : item.optInt("commandtype"));
                    sportType.setParticipantCount(item.optInt("participantcount", 0));
                }
                sportType.setSportTypeCategory(category);
                sportTypeRepository.save(sportType);
            }

            System.out.println("Category " + category.getName() + " uchun " + results.length() + " ta SportType saqlandi");
        }

        System.out.println(">>> Barcha ma'lumotlar bazaga yuklandi!");
    }

    private List<SportTypeCategory> sportTypeCategorysList() {
        return List.of(
                new SportTypeCategory(884, "30 yoshdan yuqori aholi uchun sport musobaqalari"),
                new SportTypeCategory(155, "Sport yo'nalishi"),
                new SportTypeCategory(157, "Madaniyat va sa'nat yo'nalishi"),
                new SportTypeCategory(158, "Kibersport musobaqalari"),
                new SportTypeCategory(159, "Intellektual o'yinlar yo'nalishi"),
                new SportTypeCategory(160, "Madaniy san'at yo'nalishi"),
                new SportTypeCategory(161, "Kitobxonlik yo'nalishi"),
                new SportTypeCategory(538, "Ibrat chet tillari"),
                new SportTypeCategory(571, "Adaptiv"),
                new SportTypeCategory(156, "Zamonaviy kasblar")
        );
    }

    public List<AgeCategory> ageCategoryList() {
        List<AgeCategory> categories = List.of(
                new AgeCategory(2, "9-10 yosh toifasi", 9, 10),
                new AgeCategory(4, "6-8 yosh toifasi", 6, 8),
                new AgeCategory(10, "27-30 yosh toifasi", 27, 30),
                new AgeCategory(27, "16-30 yosh toifasi", 16, 30),
                new AgeCategory(33, "14-30 yosh toifasi", 14, 30),
                new AgeCategory(37, "7-30 yosh toifasi", 7, 30),
                new AgeCategory(38, "10-14 yosh toifasi", 10, 14),
                new AgeCategory(39, "14-30 yosh toifasi", 14, 30),
                new AgeCategory(40, "10-15 yosh toifasi", 10, 15),
                new AgeCategory(42, "16-22 yosh toifasi", 16, 22),
                new AgeCategory(44, "23-30 yosh toifasi", 23, 30),
                new AgeCategory(58, "15-19 yosh toifasi", 15, 19),
                new AgeCategory(61, "20-30 yosh toifasi", 20, 30),
                new AgeCategory(74, "18-30 yosh toifasi", 18, 30),
                new AgeCategory(87, "18-21 yosh toifasi", 18, 21),
                new AgeCategory(122, "12-17 yosh toifasi", 12, 17),
                new AgeCategory(123, "31-70+ yosh toifasi", 31, 200)
        );
        return categories;
    }


    public List<Address> addressList() {
        List<Address> addresses = List.of(
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 13998, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 13999, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14000, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14001, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14002, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14003, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14004, "2-sektor (Prokuror sektori)", 5),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14005, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14006, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14007, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14008, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14009, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14010, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14011, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14012, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14013, "2-sektor (Prokuror sektori)", 5),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14014, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14015, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14016, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14017, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14018, "2-sektor (Prokuror sektori)", 5),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14019, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14020, "2-sektor (Prokuror sektori)", 5),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14021, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14022, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14023, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14024, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14025, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14026, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14027, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14028, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14029, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14030, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14031, "2-sektor (Prokuror sektori)", 5),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14032, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14033, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14034, "2-sektor (Prokuror sektori)", 5),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14035, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14036, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14037, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14038, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14039, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14040, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14041, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14042, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14043, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14044, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14045, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14046, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14047, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14048, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14049, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14050, "2-sektor (Prokuror sektori)", 5),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14051, "2-sektor (Prokuror sektori)", 5),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14052, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14053, "2-sektor (Prokuror sektori)", 5),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14054, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14055, "2-sektor (Prokuror sektori)", 5),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14056, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14057, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14058, "3-sektor (IIB sektori)", 6),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14059, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14060, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14061, "2-sektor (Prokuror sektori)", 5),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14062, "1-sektor (Xokim sektori)", 4),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14063, "4-sektor (Soliq sektori)", 7),
                new Address(null, 3, "ANDIJON", "Paxtaobod", 45, 14064, "1-sektor (Xokim sektori)", 4)
        );

        return addresses;
    }


    private List<SportyType> sportyTypeList() {
        return List.of(

                new SportyType(54, "Intellektual olimpiada", null, 11),
                new SportyType(24, "Qandolatchilik", null, 1),
                new SportyType(25, "Hunarmandchilik", null, 1),
                new SportyType(26, "Yoshlar raqs festivali", null, 0),
                new SportyType(28, "Milliy  raqs jamoaviy", null, 3),
                new SportyType(30, "Zamonaviy raqs jamoaviy", null, 3),
                new SportyType(39, "Milliy cholg'ular: Rubob", null, 1),
                new SportyType(38, "\"Milliy va zamonaviy cholg'u ijrochiligi\" tanlovi", null, 0),
                new SportyType(103, "Arg'imchoqda sakrash", null, 1),
                new SportyType(109, "Uzunlikka sakrash", null, 1),
                new SportyType(29, "Zamonaviy raqs yakkaxon", null, 1),
                new SportyType(60, "Yosh quruvchi", null, 1),
                new SportyType(61, "Yosh musavvir", null, 1),
                new SportyType(33, "DOTA 2 (PC)", null, 6),
                new SportyType(70, "“To‘dako‘l open” Respublika turniri ( Shaxmat-professional)", null, 1),
                new SportyType(31, "Armrestling", null, 1),
                new SportyType(42, "Mohir fortepianochi", null, 1),
                new SportyType(32, "CS2 (PC)", null, 6),
                new SportyType(45, "Yosh kitobxon tanlovi ", null, 1),
                new SportyType(55, "Yosh kitobxon oila", null, 2),
                new SportyType(62, "Mushoira", null, 1),
                new SportyType(46, "Yengil atletika (100 metrga yugurish)", 1, 1),
                new SportyType(56, "Para badminton", 1, 1),
                new SportyType(69, "Para stol tennisi (Aravachada)", 1, 1),
                new SportyType(104, "Mas-restling", null, 1),
                new SportyType(34, "Valorant (PC)", null, 1),
                new SportyType(35, " eFootball 2024 (PS)", null, 1),
                new SportyType(82, "Yoshlar ovozi", null, 1),
                new SportyType(65, "Dizaynerlik (Liboslar bo‘yicha)", null, 1),
                new SportyType(83, "“To‘dako‘l open” Respublika turniri -Shaxmat(havaskor)", null, 1),
                new SportyType(81, "Avto mexanik", null, 0),
                new SportyType(36, "Zakovat intellektual o'yini ", null, 7),
                new SportyType(78, "Zukko kitobxon", null, 1),
                new SportyType(15, "Para yengil atletika (100 ga yugurish)", 1, 1),
                new SportyType(11, "Arqon tortish", 2, 10),
                new SportyType(1, "Mini futbol", 2, 10),
                new SportyType(2, "Milliy kurash", 1, 1),
                new SportyType(3, "Voleybol", 2, 9),
                new SportyType(4, "Yengil atletika", 1, 4),
                new SportyType(5, "Shaxmat", 1, 1),
                new SportyType(6, "Shashka", 1, 1),
                new SportyType(7, "Stol tennisi", 1, 1),
                new SportyType(8, "Stritbol", 2, 4),
                new SportyType(9, "Workaut", 1, 5),
                new SportyType(10, "Quvnoq startlar", 2, 10),
                new SportyType(13, "Futbol", 2, 16),
                new SportyType(14, "Gimnastrada", 2, 12),
                new SportyType(17, "Para stol tennisi", 1, 1),
                new SportyType(18, "Para armrestling", 1, 1),
                new SportyType(19, "Para shashka", 1, 1),
                new SportyType(20, "Para Shaxmat", 1, 1),
                new SportyType(22, "Erkaklar sartaroshi", null, 1),
                new SportyType(57, "Armrestling ", 1, 1),
                new SportyType(64, "Stol tennis", 1, 1),
                new SportyType(68, "Badminton", 1, 1),
                new SportyType(74, "Yengil atletika (uzunlikka sakrash)", 1, 1),
                new SportyType(76, "Gandbol", 2, 10),
                new SportyType(77, "Para yengil atletika (uzunlikka sakrash)", 1, 1),
                new SportyType(79, "Shaxmat", 1, 1),
                new SportyType(84, "Mini futbol", 2, 6),
                new SportyType(85, "Voleybol", 2, 8),
                new SportyType(86, "Stol tennis", 1, 1),
                new SportyType(87, "Stritbol", 2, 4),
                new SportyType(88, "Workaut", 1, 1),
                new SportyType(89, "Shashka", 1, 1),
                new SportyType(90, "Shaxmat", 1, 1),
                new SportyType(91, "60 metrga yugurish", 1, 1),
                new SportyType(92, "Shashka", 1, 1),
                new SportyType(93, "Mobilograf", null, 1),
                new SportyType(94, "Dizayner", null, 1),
                new SportyType(95, "Eng yaxshi dasturchi", null, 1),
                new SportyType(102, "Tosh ko'tarish", null, 1),
                new SportyType(105, "Yugurish-60 m", 1, 1),
                new SportyType(106, "Yugurish-100 m", 1, 1),
                new SportyType(107, "Yugurish-200 m", 1, 1),
                new SportyType(108, "Yugurish-400 m", 1, 1)

        );
    }

    public String fetchGet(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        conn.setRequestProperty("Accept", "application/json, text/plain, */*");
        conn.setRequestProperty("Origin", "https://5tashabbus.uz");
        conn.setRequestProperty("Referer", "https://5tashabbus.uz/");

        int status = conn.getResponseCode();
        System.out.println("GET " + urlString + " → status: " + status);

        try (InputStream stream = (status >= 200 && status < 300)
                ? conn.getInputStream() : conn.getErrorStream()) {
            if (stream == null) throw new RuntimeException("Stream null keldi");
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
}