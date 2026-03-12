package uz.java.yoshlar_tashabusi_app.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uz.java.yoshlar_tashabusi_app.entity.SportTypeCategory;
import uz.java.yoshlar_tashabusi_app.entity.User;
import uz.java.yoshlar_tashabusi_app.repository.SportTypeCategoryRepository;
import uz.java.yoshlar_tashabusi_app.repository.UserRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SportTypeCategoryService {
    private final SportTypeCategoryRepository sportTypeCategoryRepository;
    private final UserRepository userRepository;

    @SneakyThrows
    public void syncSportTypeCategories(User user) {
        Set<SportTypeCategory> sportTypeCategories = new HashSet<>();
        String urlString = "https://api.5tashabbus.uz/SportTypeCategory/GetAll?lang=uz_latn";
        String response = fetchGet(urlString);

        JSONObject object = new JSONObject(response);
        JSONArray results = object.getJSONArray("result");

        for (int i = 0; i < results.length(); i++) {
            JSONObject item = results.getJSONObject(i);
            int id = item.getInt("id");
            String name = item.optString("name", "");

            // Bazada bormi yo'qmi — har doim set ga qo'shish kerak
            SportTypeCategory category = sportTypeCategoryRepository.findById(id)
                    .orElseGet(() -> {
                        SportTypeCategory newCategory = new SportTypeCategory();
                        newCategory.setId(id);
                        newCategory.setName(name);
                        return sportTypeCategoryRepository.save(newCategory);
                    });

            sportTypeCategories.add(category); // har doim qo'shiladi
        }

        user.setSportTypeCategories(sportTypeCategories);
        userRepository.save(user);
        System.out.println("SportTypeCategory lar saqlandi: " + results.length());
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
