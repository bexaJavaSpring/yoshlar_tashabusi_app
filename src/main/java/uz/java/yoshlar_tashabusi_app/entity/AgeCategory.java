package uz.java.yoshlar_tashabusi_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "age_category")
@Data
public class AgeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer maxAge;

    private Integer minAge;
}
