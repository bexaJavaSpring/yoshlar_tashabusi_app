package uz.java.yoshlar_tashabusi_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.java.yoshlar_tashabusi_app.entity.SportTypeCategory;
import uz.java.yoshlar_tashabusi_app.entity.SportyType;

import java.util.List;

public interface SportTypeRepository extends JpaRepository<SportyType, Integer> {

    List<SportyType> findBySportTypeCategory(SportTypeCategory category);}
