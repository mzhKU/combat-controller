package ch.mzh.cc.components;

import ch.mzh.cc.components.Component;

public class FuelComponent implements Component {

    private int currentFuel;
    private int maxFuel;
    private int fuelConsumptionRate;
    private int lastFuelUsage = 0;

    public FuelComponent(int maxFuel, int fuelConsumptionRate) {
        this.maxFuel = maxFuel;
        this.currentFuel = maxFuel;
        this.fuelConsumptionRate = fuelConsumptionRate;
    }

    public int getCurrentFuel() {
        return this.currentFuel;
    }

    public int getMaxFuel() {
        return this.maxFuel;
    }

    public int getFuelConsumptionRate() {
        return fuelConsumptionRate;
    }

    public boolean isEmpty() {
        return currentFuel == 0;
    }

    public boolean isFull() {
        return currentFuel == maxFuel;
    }

    public boolean hasFuel(int amount) {
        return currentFuel >= amount;
    }

    public int consumeFuel(int amount) {
        if (hasFuel(amount)) {
            currentFuel -= amount;
            lastFuelUsage = amount;
        }
        return amount;
    }

    public void addFuel(int amount) {
        currentFuel = Math.min(maxFuel, currentFuel + amount);
    }

    public int calculateMovementCost(int distance) {
        return distance * fuelConsumptionRate;
    }

    public boolean canAffordMove(int distance) {
        return hasFuel(calculateMovementCost(distance));
    }

    public int getLastFuelUsage() { return lastFuelUsage; }

}
