package j2ee.ourteam.interfaces;

import java.util.List;
import java.util.Optional;

public interface GenericCrudService<E, // Entity (đại diện cho table trong DB)
    Req, // Request model (Create / Update DTO)
    Res, // Response model (DTO trả về)
    ID> { // Kiểu khóa chính (UUID, Long, ...)

  List<Res> findAll();

  Optional<Res> findById(ID id);

  Res create(Req dto);

  Res update(ID id, Req dto);

  void deleteById(ID id);
}
