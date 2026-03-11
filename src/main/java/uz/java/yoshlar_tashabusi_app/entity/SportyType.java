package uz.java.yoshlar_tashabusi_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "sport_type")
@Data
public class SportyType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer commonType;

    private Integer participantCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "age_category_id")
    private AgeCategory agecategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sport_type_category_id")
    private SportTypeCategory sportTypeCategory;
}
