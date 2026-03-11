package uz.java.yoshlar_tashabusi_app.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;

    private String lastName;

    private String familyName;

    private String shortName;

    private String fullName;

    private LocalDate dateOfBirth;

    private String email;

    @Column(unique = true)
    private String pinfl;

    private Integer genderId;

    private String genderName;

    private Integer identityDocumentId;

    private String identityDocumentName;

    private String documentSeries;

    @Column(unique = true)
    private String documentNumber;

    private String phoneNumber;

    private String detail;

    private Integer healthTypeId;

    private Integer youthLeaderPersonId;

    private boolean isImport;

    private Integer initiativTypeId;

    private String initiativTypeName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @ManyToMany
    @JoinTable(name = "user_sport_type_category", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
            , inverseJoinColumns = {@JoinColumn(name = "sport_type_category_id", referencedColumnName = "id")})
    private Set<SportTypeCategory> sportTypeCategories = new HashSet<>();
}
