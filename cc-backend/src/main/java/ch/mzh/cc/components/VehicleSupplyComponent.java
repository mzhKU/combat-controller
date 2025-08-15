package ch.mzh.cc.components;


import ch.mzh.cc.model.Entity;

public class VehicleSupplyComponent extends SupplyComponent {

    public VehicleSupplyComponent(int refuelRange) {
        super(refuelRange);
    }

    @Override
    public boolean canRefuel(Entity supplier, Entity target) {

        // Only refuel entities of the same player
        if (!supplier.isSamePlayer(target)) {
            return false;
        }

        FuelComponent supply = supplier.getComponent(FuelComponent.class);
        FuelComponent targetFuel = target.getComponent(FuelComponent.class);

        if (supply.isEmpty() || targetFuel.isFull()) {
            return false;
        }
        return isInRange(target.getPosition(), supplier.getPosition());
    }

    @Override
    public boolean refuelTarget(Entity supplier, Entity target) {
        FuelComponent targetFuel = target.getComponent(FuelComponent.class);

        int targetFuelNeed = targetFuel.getMaxFuel() - targetFuel.getCurrentFuel();

        if (targetFuelNeed <= 0) {
            return false; // Target is already full
        }

        int transferAmount;

        FuelComponent supplyFuel = supplier.getComponent(FuelComponent.class);
        transferAmount = Math.min(supplyFuel.getCurrentFuel(), targetFuelNeed);

        // TODO: This condition should emerge by itself from
        // 1: the FuelComponent logic where the currently available fuel
        //    is calculated, it should never drop below zero.
        // 2: The invariant that current fuel cannot be larger than maximal
        //    available fuel.
        if (transferAmount > 0) {
            targetFuel.addFuel(transferAmount);
            supplyFuel.consumeFuel(transferAmount);
            return true;
        }
        return false;
    }
}
