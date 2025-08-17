package ch.mzh.cc.play;

import ch.mzh.cc.EntityManager;
import ch.mzh.cc.model.Entity;

import java.util.Optional;

public interface GameRule {

  Optional<GameSystemCommand> apply(EntityManager entityManager, Entity entity);

}
