package ch.mzh.cc.components;


import ch.mzh.cc.Position2D;
import ch.mzh.cc.model.Entity;

public class TroopMovementComponent implements Component {

    public TroopMovementComponent() {
    }

    public boolean move(Entity selectedEntity, Position2D targetPosition) {
        int distance = calculateDistance(selectedEntity.getPosition(), targetPosition);

        if (distance > 1) return false; // Troops can move maximum 1 unit

        selectedEntity.setGridPosition(targetPosition);
        return true;
    }

    private int calculateDistance(Position2D from, Position2D to) {
        return Math.abs(to.getX() - from.getX()) + Math.abs(to.getY() - from.getY());
    }
}
