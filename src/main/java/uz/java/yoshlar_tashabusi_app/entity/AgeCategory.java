package uz.java.yoshlar_tashabusi_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Integer id;
    private String name;

    private Integer minAge;
    private Integer maxAge;
}
