package uz.java.yoshlar_tashabusi_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.java.yoshlar_tashabusi_app.entity.AgeCategory;

public interface AgeCategoryRepository extends JpaRepository<AgeCategory, Integer> {
    boolean existsByName(String name);
}
