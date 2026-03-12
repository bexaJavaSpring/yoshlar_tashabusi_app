package uz.java.yoshlar_tashabusi_app.loader;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uz.java.yoshlar_tashabusi_app.entity.Address;
import uz.java.yoshlar_tashabusi_app.entity.AgeCategory;
import uz.java.yoshlar_tashabusi_app.repository.AddressRepository;
import uz.java.yoshlar_tashabusi_app.repository.AgeCategoryRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AgeCategoryLoader implements CommandLineRunner {

    private final AgeCategoryRepository repository;
    private final AddressRepository addressRepository;

    @Override
    public void run(String... args) throws Exception {
        // Ma'lumotlar ro'yxati


        repository.saveAll(ageCategoryList());


        System.out.println(">>> Yosh toifalari bazaga yuklandi!");
    }

    public List<AgeCategory> ageCategoryList() {
        List<AgeCategory> categories = List.of(
                new AgeCategory(2, "9-10 yosh toifasi"),
                new AgeCategory(4, "6-8 yosh toifasi"),
                new AgeCategory(10, "27-30 yosh toifasi"),
                new AgeCategory(27, "16-30 yosh toifasi"),
                new AgeCategory(33, "14-30 yosh toifasi"),
                new AgeCategory(37, "7-30 yosh toifasi"),
                new AgeCategory(38, "10-14 yosh toifasi"),
                new AgeCategory(39, "14-30 yosh toifasi"),
                new AgeCategory(40, "10-15 yosh toifasi"),
                new AgeCategory(42, "16-22 yosh toifasi"),
                new AgeCategory(44, "23-30 yosh toifasi"),
                new AgeCategory(58, "15-19 yosh toifasi"),
                new AgeCategory(61, "20-30 yosh toifasi"),
                new AgeCategory(74, "18-30 yosh toifasi"),
                new AgeCategory(87, "18-21 yosh toifasi"),
                new AgeCategory(122, "12-17 yosh toifasi"),
                new AgeCategory(123, "31-70+ yosh toifasi")
        );
        return categories;
    }


    public List<Address> addressList() {
        List<Address> addresses = List.of(

        );
    }
}