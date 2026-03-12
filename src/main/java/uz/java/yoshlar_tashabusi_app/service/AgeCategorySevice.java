package uz.java.yoshlar_tashabusi_app.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import uz.java.yoshlar_tashabusi_app.entity.AgeCategory;
import uz.java.yoshlar_tashabusi_app.entity.User;
import uz.java.yoshlar_tashabusi_app.repository.AgeCategoryRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class AgeCategorySevice {

    private AgeCategoryRepository ageCategoryRepository;


    public User addAgeCategory(User user) {

    }

    public List<AgeCategory> getAgeCategoryFromAPI(User user) {
        try {
            String urlString = "https://api.5tashabbus.uz/AgeCategory/GetAll?lang=uz_latn" +
                    "&genderid=" + user.getGenderId() +
                    "&dateofbirth=" + user.getDateOfBirth().toString() +
                    "&isSeasonDoc=true" +
                    "&initiativtypeid=" + user.getInitiativTypeId() +
                    "&isonlineregistration=true" +
                    "&sporttypecategoryid=undefined" +
                    "&sporttypeid=undefined";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 1. Set Method and Output
            conn.setRequestMethod("GET");
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
                ObjectMapper objectMapper = new ObjectMapper();
                List<AgeCategory> categories = null;
                try {
                    categories = objectMapper.readValue(
                            response.toString(),
                            new TypeReference<List<AgeCategory>>() {
                            }
                    );

                    for (AgeCategory category : categories) {
                        if (ageCategoryRepository.existsByName(category.getName())) {

                        }
                    }
                    // Endi categories ro'yxatidan foydalanishingiz mumkin
                    categories.forEach(c -> System.out.println(c.getName()));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                JSONObject object = new JSONObject(response.toString());

                if (object.isNull("result")) return null; // Safety check

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
