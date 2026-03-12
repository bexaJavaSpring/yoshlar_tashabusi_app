package uz.java.yoshlar_tashabusi_app.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import uz.java.yoshlar_tashabusi_app.entity.Attachment;
import uz.java.yoshlar_tashabusi_app.repository.AttachmentRepository;

@Service
@Slf4j
public class AttachmetService {
    AttachmentRepository attachmentRepository;


    public Attachment saveAttachment(JSONObject photo) {
        Attachment attachment = new Attachment();
        attachment.setPhotoId(photo.optInt("id", 0));
        attachment.setOwnerId(photo.optLong("ownerid", 0));
        attachment.setAttachmentFileId(photo.optString("attachmentfileid", ""));
        attachment.setAttachmentFileName(photo.optString("attachmentfilename", ""));
        attachment.setAttachmentFileType(photo.optString("attachmentfiletype", ".jpg"));
        attachment.setStatus(photo.optInt("Status", 0));
        return attachmentRepository.save(attachment);
    }
}
