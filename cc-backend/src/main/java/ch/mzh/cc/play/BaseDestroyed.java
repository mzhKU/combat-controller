package ch.mzh.cc.play;

import ch.mzh.cc.EntityManager;
import ch.mzh.cc.model.Entity;
import ch.mzh.cc.model.EntityType;

import java.util.Optional;

public class BaseDestroyed implements GameRule {
  @Override
  public Optional<GameSystemCommand> apply(EntityManager entityManager, Entity destroyedEntity) {
    if (destroyedEntity.getType() == EntityType.BASE) {
      int winnerId = (destroyedEntity.getPlayerId() == 1) ? 2 : 1;
      return Optional.of(new SetWinnerCommand(winnerId));
    }
    return Optional.empty();
  }
}
