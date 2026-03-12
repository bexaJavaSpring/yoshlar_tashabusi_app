package uz.java.yoshlar_tashabusi_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
