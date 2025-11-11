package j2ee.ourteam.repositories;

import j2ee.ourteam.entities.Message;
import j2ee.ourteam.entities.MessageReaction;
import j2ee.ourteam.entities.MessageReactionId;
import j2ee.ourteam.entities.User;


import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReactionRepository extends JpaRepository<MessageReaction, MessageReactionId> {
  Optional<MessageReaction> findByMessageAndUser(Message message, User user);

  Page<MessageReaction> findByMessageId(UUID messageId, Pageable pageable);

  void deleteByMessageAndUserAndIdEmoji(Message message, User user, String emoji);
}
