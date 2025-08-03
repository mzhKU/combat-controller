
Ongoing:

Tech dept:
- Integration tests
- Run the game without graphics



Refuel use cases:
4. Cannon moves inbetween base and supply truck  -> base refuels cannon
5. Supply truck moves inbetween cannon and base  -> base refuels truck, truck refules cannon
6. Cannon moves not next to truck or base        -> no refueling
7. Supply truck moves not next to cannon or base -> no refueling

Todo:
- What if a cannon moves inbetween a base and a supply truck?
- On start, initialize a map with distances between all positions (must have zeroes on diagonal) to remove
  distance calculation
- First version: enemy shots are indicated on the grid the user has to evade units from being hit
- Add map to entity manager to fetch entities by name or id
- Add a data header
- API for agent control
- Fuel level max limit -> cannot overfuel.
- Decouple MovementComponent from FuelComponent, maybe introduce a FuelMovementComponent
- Troops dont have fuel but they can move
- Integration test
- Clarify distinction between supply truck fuel and supply truck refuel supply
- Highlight hovered grid unit
- Render name of unit on unit
- Refuel supply truck at base
- Refuel cannon at base
- Drop fuel depots on grid
- Deploy solar panels if supply truck or cannon run out of fuel
- Show maximum travel range such that return to base is possible with three risk levels, safe, medium, risky
- Show current fuel on unit
- Fuel consumption depends on vehicle weight
- Vehicle weight depends on (frame weight, fuel in tank, shells loaded)
- External Vehicle configuration
- Playable without graphics
- Effect when trying to reposition but not enough fuel
- Highligh available positions
- Express fuel / distance capacity as ratios between vehicles
- Log the travel and the fuel cost
- Avoid duplicate calculation of fuel cost
- Handle case when cannon next to truck and base
- Supply truck refuels other supply truck?
- Cannon supplies grenades to other cannon?
- Vehicles have orientation, need to be correctly aligned for refueling
- Refuel also over diagonal

Player control
- Vehicle class which can move, consumes fuel
- Personel class which has no fuel
- Static entities which cannot move

Non player controlled:
- Rough terrain
- Blocked terrain

EntityManager:
- Get neighbours of position


Supply vehicle:
- Fuel transfer rate
- Supply vehicle can refuel itself
- Control if supply vehicle should refuel neighbour vehicle or not
- What happens if its next to multiple receiving vehicles?


Done:
- Observer pattern for change detection
- Position2D instead of X, Y
- Refuel should also happen when cannon moves next to supply truck
- 19. Jul. 2025:
    - Cannon moves next to supply truck
    - Supply truck moves next to cannon
    - Vehicle moves next to base
