package uz.java.yoshlar_tashabusi_app.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uz.java.yoshlar_tashabusi_app.entity.SportyType;
import uz.java.yoshlar_tashabusi_app.repository.AgeCategoryRepository;
import uz.java.yoshlar_tashabusi_app.repository.SportTypeCategoryRepository;
import uz.java.yoshlar_tashabusi_app.repository.SportTypeRepository;

@Service
@RequiredArgsConstructor
public class SportTypeService {

    private final SportTypeRepository sportTypeRepository;
    private final SportTypeCategoryRepository sportTypeCategoryRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final SportTypeCategoryService sportTypeCategoryService;

    @SneakyThrows
    public void syncSportTypes() {
        String urlString = "https://api.5tashabbus.uz/SportType/GetAll?lang=uz_latn";
        String response = sportTypeCategoryService.fetchGet(urlString);

        JSONObject object = new JSONObject(response);
        JSONArray results = object.getJSONArray("result");

        for (int i = 0; i < results.length(); i++) {
            JSONObject item = results.getJSONObject(i);
            int id = item.getInt("id");

            if (!sportTypeRepository.existsById(id)) {
                SportyType sportType = new SportyType();
                sportType.setId(id);
                sportType.setName(item.optString("name", ""));
                sportType.setCommonType(item.optInt("commonType", 0));
                sportType.setParticipantCount(item.optInt("participantCount", 0));

                // SportTypeCategory ni bog'lash
                if (!item.isNull("sportTypeCategoryId")) {
                    int categoryId = item.getInt("sportTypeCategoryId");
                    sportTypeCategoryRepository.findById(categoryId)
                            .ifPresent(sportType::setSportTypeCategory);
                }

                // AgeCategory ni bog'lash (agar kelsa)
                if (!item.isNull("ageCategoryId")) {
                    int ageCatId = item.getInt("ageCategoryId");
                    ageCategoryRepository.findById(ageCatId)
                            .ifPresent(sportType::setAgecategory);
                }

                sportTypeRepository.save(sportType);
            }
        }
        System.out.println("SportType lar saqlandi: " + results.length());
    }
}
