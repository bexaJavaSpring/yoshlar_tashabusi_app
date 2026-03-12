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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    private Integer categoryId;
    private String name;

    private Integer minAge;
    private Integer maxAge;

//    public AgeCategory(int categoryId, String name) {
//        this.categoryId = categoryId;
//        this.name = name;
//    }
}
