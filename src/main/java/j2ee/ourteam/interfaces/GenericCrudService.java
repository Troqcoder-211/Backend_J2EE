package j2ee.ourteam.interfaces;

import java.util.List;
import java.util.Optional;

public interface GenericCrudService<T, ID> {
  List<T> findAll();

  Optional<T> findById(ID id);

  T save(T entity);

  void deleteById(ID id);
}
