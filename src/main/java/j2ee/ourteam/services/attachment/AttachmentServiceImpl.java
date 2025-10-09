package j2ee.ourteam.services.attachment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Attachment;
import j2ee.ourteam.repositories.AttachmentRepository;

@Service
public class AttachmentServiceImpl implements IAttachmentService {

  private final AttachmentRepository attachmentRepository;

  public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
    this.attachmentRepository = attachmentRepository;
  }

  @Override
  public List<Attachment> findAll() {
    return attachmentRepository.findAll();
  }

  @Override
  public Optional<Attachment> findById(UUID id) {
    return attachmentRepository.findById(id);
  }

  @Override
  public Attachment save(Attachment entity) {
    return attachmentRepository.save(entity);
  }

  @Override
  public void deleteById(UUID id) {
    attachmentRepository.deleteById(id);
  }

}
