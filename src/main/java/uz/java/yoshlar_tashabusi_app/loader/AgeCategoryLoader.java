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
//        addressRepository.saveAll(addressList());

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
}