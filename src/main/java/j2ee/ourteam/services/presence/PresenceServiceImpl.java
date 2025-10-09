package j2ee.ourteam.services.presence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Presence;
import j2ee.ourteam.repositories.PresenceRepository;

@Service
public class PresenceServiceImpl implements IPresenceService {

  private final PresenceRepository presenceRepository;

  public PresenceServiceImpl(PresenceRepository presenceRepository) {
    this.presenceRepository = presenceRepository;
  }

  @Override
  public List<Presence> findAll() {
    return presenceRepository.findAll();
  }

  @Override
  public Optional<Presence> findById(UUID id) {
    return presenceRepository.findById(id);
  }

  @Override
  public Presence save(Presence entity) {
    return presenceRepository.save(entity);
  }

  @Override
  public void deleteById(UUID id) {
    presenceRepository.deleteById(id);
  }

}
