package uz.java.yoshlar_tashabusi_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sport_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SportyType {
    @Id
    private Integer id;

    private String name;

    private Integer commonType;

    private Integer participantCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sport_type_category_id")
    private SportTypeCategory sportTypeCategory;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sport_type_age_category", joinColumns = {@JoinColumn(name = "sport_type_id", referencedColumnName = "id")}
            , inverseJoinColumns = {@JoinColumn(name = "age_category_id", referencedColumnName = "id")})
    private Set<AgeCategory> ageCategories = new HashSet<>();

    public SportyType(int id, String name, Integer commonType, Integer participantCount) {
        this.id = id;
        this.name = name;
        this.commonType = commonType;
        this.participantCount = participantCount;
    }
}
