package uz.java.yoshlar_tashabusi_app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "file_entity")
@Data
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User user;

    private String attachmentFileId;

    private String attachmentFileName;

    private String attachmentFileType;

    private Integer status;
}
