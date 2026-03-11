package uz.java.yoshlar_tashabusi_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "sport_type_category")
@Data
public class SportTypeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
}
