package j2ee.ourteam.models.message;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import j2ee.ourteam.entities.Message;

public class MessageSpecification {

  public static Specification<Message> filter(MessageFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      // lọc theo conversation
      if (filter.getConversationId() != null) {
        predicates.add(cb.equal(root.get("conversation").get("id"), filter.getConversationId()));
      }

      // lọc theo người gửi
      if (filter.getSenderId() != null) {
        predicates.add(cb.equal(root.get("sender").get("id"), filter.getSenderId()));
      }

      // tìm theo nội dung (LIKE)
      if (filter.getKeyword() != null && !filter.getKeyword().isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("content")),
            "%" + filter.getKeyword().toLowerCase() + "%"));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
