package uz.java.yoshlar_tashabusi_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "address")
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer oblastId;

    private String oblastName;

    private String regionName;

    private Integer regionId;

    @Column(unique = true)
    private Integer mfyId;

    private String regionSectorName;

    private Integer regionSectorId;
}
