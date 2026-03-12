package uz.java.yoshlar_tashabusi_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.java.yoshlar_tashabusi_app.entity.AgeCategory;

import java.util.Optional;

public interface AgeCategoryRepository extends JpaRepository<AgeCategory, Integer> {

    @Query("SELECT a FROM AgeCategory a WHERE a.minAge <= :age AND a.maxAge >= :age")
    Optional<AgeCategory> findByAge(@Param("age") int age);
}
