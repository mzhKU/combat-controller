package ch.mzh.cc.components;

import ch.mzh.cc.Grid;
import ch.mzh.cc.Position2D;
import ch.mzh.cc.model.Entity;

public abstract class SupplyComponent implements Component {

    private final int refuelRange;

    public SupplyComponent(int refuelRange) {
        this.refuelRange = refuelRange;
    }

    public abstract boolean canRefuel(Entity supplier, Entity target);
    public abstract boolean refuelTarget(Entity supplier, Entity target);

    protected boolean isInRange(Position2D targetPosition, Position2D sourcePosition) {
        int distance = Grid.calculateManhattanDistance(targetPosition, sourcePosition);
        return distance <= refuelRange;
    }
}
