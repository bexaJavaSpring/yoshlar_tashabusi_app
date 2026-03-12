package uz.java.yoshlar_tashabusi_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.java.yoshlar_tashabusi_app.entity.AgeCategory;
import uz.java.yoshlar_tashabusi_app.entity.SportTypeCategory;
import uz.java.yoshlar_tashabusi_app.entity.SportyType;

import java.util.List;

public interface SportTypeRepository extends JpaRepository<SportyType, Integer> {
    @Query("select t from SportyType t join t.ageCategories a where a.id=?1")
    List<SportyType> findAllByAgeCategory(Integer ageCategoryId);
}
