package uz.java.yoshlar_tashabusi_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "age_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer maxAge;

    private Integer minAge;
}
