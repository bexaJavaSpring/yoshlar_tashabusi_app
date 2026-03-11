package uz.java.yoshlar_tashabusi_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.java.yoshlar_tashabusi_app.entity.FileEntity;

public interface FileEntityRepository extends JpaRepository<FileEntity, Integer> {
}
