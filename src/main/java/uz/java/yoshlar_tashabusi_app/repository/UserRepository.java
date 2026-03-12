package uz.java.yoshlar_tashabusi_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.java.yoshlar_tashabusi_app.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByDocumentSeriesNumber(String document);


    @Query("select t from User t inner join Address a on a.id=t.address.id" +
            " where a.mfyId=?1 ")
    List<User> findByMfyId(Integer mfyId);
}
