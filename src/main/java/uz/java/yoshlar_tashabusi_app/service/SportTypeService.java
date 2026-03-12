package uz.java.yoshlar_tashabusi_app.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uz.java.yoshlar_tashabusi_app.entity.SportTypeCategory;
import uz.java.yoshlar_tashabusi_app.entity.SportyType;
import uz.java.yoshlar_tashabusi_app.entity.User;
import uz.java.yoshlar_tashabusi_app.repository.SportTypeRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SportTypeService {

    private final SportTypeRepository sportTypeRepository;
    private final SportTypeCategoryService sportTypeCategoryService;

    @SneakyThrows
    public void syncSportTypes(User user) {
        for (SportTypeCategory category : user.getSportTypeCategories()) {
            String urlString = "https://api.5tashabbus.uz/SportType/GetAll?" +
                    "lang=uz_latn" +
                    "&dateOfBirth=" + formatDate(user.getDateOfBirth()) +
                    "&genderId=" + user.getGenderId() +
                    "&agecategoryid=0" +
                    "&sporttypecategoryid=" + category.getId() +
                    "&isSeasonDoc=true" +
                    "&initiativtypeid=" + user.getInitiativTypeId() +
                    "&isonlineregistration=true" +
                    "&healthtypeid=" + user.getHealthTypeId();

            String response = sportTypeCategoryService.fetchGet(urlString);
            JSONArray results = new JSONArray(response);

            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                int id = item.getInt("id");

                if (!sportTypeRepository.existsById(id)) {
                    SportyType sportType = new SportyType();
                    sportType.setId(id);
                    sportType.setName(item.optString("name", ""));
                    sportType.setCommonType(item.optInt("commonType", 0));
                    sportType.setParticipantCount(item.optInt("participantCount", 0));
                    sportType.setSportTypeCategory(category);
                    sportTypeRepository.save(sportType);
                }
            }
            System.out.println("Category " + category.getName() + " uchun SportType lar saqlandi");
        }
    }

    public String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
