package ch.mzh.cc;

import ch.mzh.cc.model.Base;
import ch.mzh.cc.model.Entity;
import ch.mzh.cc.model.EntityType;
import ch.mzh.cc.model.SupplyTruck;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GameRuleEngineTest {

  @Test
  void testFlatMapKeepsOnlyValuesOfNonEmptyOptionals() {
    Entity baseDestroyed = new Base("Test Base", EntityType.BASE, new Position2D(1, 1), 1);
    Entity supplyTruckDestroyed = new SupplyTruck("Test Truck", EntityType.SUPPLY_TRUCK, new Position2D(2, 2), 1);

    // Given
    Stream<Optional<Entity>> gameEntities = Stream.of(
            Optional.empty(),
            Optional.of(baseDestroyed),
            Optional.empty(),
            Optional.of(supplyTruckDestroyed),
            Optional.empty()
    );
    Stream<Entity> expected = Stream.of(baseDestroyed, supplyTruckDestroyed);

    // When
    Stream<Entity> result = gameEntities.flatMap(Optional::stream);

    // Then
    List<Entity> resultList = result.toList();
    List<Entity> expectedList = expected.toList();

    assertIterableEquals(resultList, expectedList);
  }


  @Test
  public void testStreamOfOptionalReturnsStreamOfObjectOnlyWhenOptionalIsNonEmpty() {
    // Given
    String value = "ABC";
    Optional<Object> filledOptional = Optional.of(value);
    Optional<Object> emptyOptional = Optional.empty();
    Stream<Object> expectedFilledStream = Stream.of(value);
    Stream<Object> expectedEmptyStream = Stream.empty();

    // When
    Stream<Object> resultFilledStream = filledOptional.stream();
    Stream<Object> resultEmptyStream = emptyOptional.stream();

    // Then
    assertEquals(expectedFilledStream.toList(), resultFilledStream.toList());
    assertEquals(expectedEmptyStream.toList(), resultEmptyStream.toList());
  }

}