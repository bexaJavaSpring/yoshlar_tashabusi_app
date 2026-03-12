package uz.java.yoshlar_tashabusi_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer photoId;
    private Long ownerId;
    private String attachmentFileId;
    private String attachmentFileName;
    private String attachmentFileType;
    private Integer status;
}
