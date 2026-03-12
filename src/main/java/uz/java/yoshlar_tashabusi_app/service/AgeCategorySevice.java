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


}
