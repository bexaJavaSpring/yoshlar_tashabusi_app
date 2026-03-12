package uz.java.yoshlar_tashabusi_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.java.yoshlar_tashabusi_app.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByDocumentSeriesNumber(String document);


}
