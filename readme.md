
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


Given these game mechanics for a gameArtillery Strategy Game - Detailed Specifications
Core Game Concept
A turn-based artillery strategy game where players manage logistics, positioning, and resource allocation to support autonomous troop advancement while controlling a mobile cannon with limited fuel and ammunition.
Game Board & Environment
Map LayoutGrid System: 128x128 square grid
Territory Division: Map divided into friendly, neutral, and enemy territories
Home Base: Fixed position in friendly territory (ammunition and fuel depot)
Enemy Territory: Target area that troops must reach
Terrain Types:Open ground (normal movement/firing)
Rough terrain (increased fuel consumption)
Obstacles (impassable for cannon, may block line of sight)Visual RepresentationCannon: Rectangle sprite on grid
Troops: Small unit indicators moving autonomously
Base: Larger structure sprite with resource indicators
Supply Truck: Mobile rectangle when deployedCore Game Mechanics
Turn Structure
Each turn consists of these phases:Planning Phase: Player decides cannon actions
Movement Phase: Cannon moves based on fuel availability
Combat Phase: Cannon fires at targets
Troop Phase: Autonomous troop movement occurs
Supply Phase: Resupply operations if requested
End Phase: Resource consumption calculated, turn endsCannon Mechanics
Fuel SystemStarting Fuel: 100 units per mission
Fuel Consumption:1 unit per grid square on open ground
2 units per grid square on rough terrain
3 units per grid square when towing suppliesFuel Actions:Move to adjacent square (costs fuel based on terrain)
Return to base (automatic pathfinding, costs fuel)
Call supply truck (uses communication, no fuel cost)Ammunition SystemShell Capacity: 20 shells maximum
Shell Types:High Explosive: Standard damage, moderate range
Smoke: Creates concealment, extended range
Illumination: Reveals hidden enemies, longest rangeReloading: Only possible at home base or supply truckFiring MechanicsRange Calculation: Based on distance from cannon to target
Maximum Range: 15 grid squares
Line of Sight: Must have clear path to target
Accuracy: Decreases with distance and terrain interference
Firing Cost: 1 shell per shot, no fuel costTroop Mechanics
Autonomous BehaviorSpawn Rate: 3 troops per turn from home base
Movement: 2 grid squares per turn toward enemy territory
Pathfinding: Automatic route calculation avoiding obstacles
Survival: Troops eliminated when hit by enemy fireTroop SupportArtillery Support: Cannon can clear obstacles for troop advancement
Smoke Cover: Smoke shells provide protection for advancing troops
Supply Protection: Troops escort supply convoys when neededSupply System
Supply Truck MechanicsDeployment: Called when cannon fuel drops below 25%
Capacity: Carries 50 fuel units and 10 shells
Movement Speed: 1 grid square per turn
Vulnerability: Can be destroyed by enemy fire
Supply Time: 2 turns to fully resupply cannonResource ManagementBase Inventory: Unlimited ammunition and fuel at home base
Supply Line: Truck must travel from base to cannon position
Emergency Supplies: Limited emergency fuel drops (3 per mission)Strategic Elements
Risk vs. Reward DecisionsForward Positioning:Risk: Higher fuel consumption, harder to retreat
Reward: Better fire support for troops, extended rangeConservative Play:Risk: Troops advance without adequate fire support
Reward: Fuel efficiency, easy resupply accessResource AllocationFuel Management: Balance between aggressive positioning and supply security
Ammunition Selection: Choose appropriate shell types for tactical situations
Timing: Coordinate supply runs with troop movementsTactical ConsiderationsTerrain Exploitation: Use elevated positions for extended range
Concealment: Smoke screens to protect both cannon and troops
Supply Line Security: Protect supply trucks from enemy interdictionVictory Conditions
Primary ObjectivesTroop Success: Get 75% of deployed troops to enemy territory
Time Limit: Complete objective within 30 turns
Survival: Cannon must survive the entire missionSecondary Objectives (Bonus Points)Efficiency: Complete mission using less than 80% of available fuel
Accuracy: Achieve 70% hit rate with artillery fire
Protection: Lose fewer than 25% of deployed troopsFailure ConditionsCannon Destruction: Cannon eliminated by enemy fire
Fuel Depletion: Cannon stranded without fuel and no supply available
Time Expiration: Objective not met within turn limit
Troop Losses: More than 50% of troops eliminatedEnemy Mechanics (AI Controlled)
Enemy ArtilleryCounter-Battery: Attempts to locate and engage player cannon
Troop Harassment: Fires at advancing player troops
Supply Interdiction: Targets player supply trucksEnemy PositionsStatic Defenses: Fixed positions that must be neutralized
Patrol Units: Mobile threats that can reposition
Reinforcements: Additional units arrive periodicallyGame Flow Example
Turn 1Player spawns at base with full fuel/ammo
3 troops begin advancing toward enemy territory
Player moves cannon 5 squares forward (fuel: 95/100)
Player fires HE shell at enemy position (ammo: 19/20)
Enemy returns fire, misses cannonTurn 5Fuel level at 60/100, troops 40% to objective
Player calls supply truck for future resupply
Player fires smoke shell to cover troop advance
Supply truck begins 8-square journey to cannon
Enemy eliminates 1 advancing troopTurn 12Supply truck reaches cannon, begins resupply
Player repositions during resupply (fuel: 100/100)
Troops 80% to objective, mission nearly complete
Enemy launches concentrated attack on advancing troopsSuccess MetricsStrategic Depth: Multiple viable approaches to victory
Resource Tension: Meaningful trade-offs in every decision
Tactical Complexity: Positioning and timing matter significantly
Replayability: Different strategies viable across multiple gamesTechnical Requirements for ImplementationGrid-based movement system with pathfinding algorithms
Line-of-sight calculations for firing mechanics
Resource tracking system for fuel and ammunition
AI behavior trees for autonomous troop movement
Turn-based state management for game flow controlWhat would be an approach to program this? what would you do first, then second and so on?Edit3 / 3Looking at this comprehensive artillery strategy game, I'd break down the development into logical phases that build from core systems to full gameplay. Here's my recommended approach:
Phase 1: Foundation & Core Systems (Week 1-2)
First Priority: Grid System & Basic Rendering

Implement the 128x128 grid data structure
Create basic rendering system to display the grid
Add coordinate system and grid-to-screen conversion
Basic camera/viewport system for navigating the large map

Second: Entity System

Create base Entity class/component system
Implement Cannon, Troop, Base, and SupplyTruck entities
Basic positioning and grid-based placement
Simple sprite rendering for each entity type

Phase 2: Movement & Pathfinding (Week 2-3)
Third: Basic Movement

Implement grid-based movement for cannon
Add terrain types (open, rough, obstacles) to grid
Basic collision detection (can't move into obstacles)

Fourth: Pathfinding

Implement A* pathfinding algorithm for autonomous units
Add pathfinding for troops moving toward enemy territory
Supply truck pathfinding from base to cannon

Phase 3: Core Mechanics (Week 3-4)
Fifth: Resource Systems

Fuel tracking and consumption based on terrain
Ammunition system with different shell types
Resource depletion calculations per turn

Sixth: Basic Combat

Line-of-sight calculations using raycasting
Range and accuracy calculations
Damage system and entity destruction

Phase 4: Turn Management (Week 4-5)
Seventh: Turn-Based System

Implement turn phases (Planning, Movement, Combat, etc.)
Turn state management
Action queuing and execution

Eighth: Autonomous Behaviors

Troop spawning system (3 per turn from base)
Autonomous troop movement toward objectives
Basic AI decision making for troop routing

Phase 5: Advanced Systems (Week 5-6)
Ninth: Supply System

Supply truck deployment and management
Resupply mechanics at base and via truck
Emergency supply system

Tenth: Enemy AI

Basic enemy positions and behavior
Counter-battery fire mechanics
Enemy targeting prioritization

Phase 6: Game Flow & Polish (Week 6-7)
Eleventh: Victory/Defeat Conditions

Objective tracking (75% troops to enemy territory)
Win/loss state detection
Turn limit enforcement

Twelfth: UI/UX

Resource displays (fuel, ammo counters)
Turn phase indicators
Action feedback and game state communication

Phase 7: Balance & Testing (Week 7-8)
Final Steps:

Gameplay balancing and tuning
Bug fixes and edge case handling
Performance optimization for large grid
Save/load system if needed

Key Technical Decisions:
Language/Framework: I'd recommend:

Unity/C# for rapid prototyping with built-in grid systems
JavaScript/HTML5 Canvas for web deployment
Python/Pygame for quick iteration and testing

Architecture Patterns:

Entity-Component-System for flexible entity management
State Machine for turn phases
Command Pattern for action queuing and undo functionality
Observer Pattern for UI updates when game state changes

Data Structures:

2D array for grid terrain data
Priority queues for pathfinding
Lists/arrays for entity collections
Hash maps for efficient entity lookups

This approach builds complexity gradually while ensuring each system works before moving to the next. The most critical early decision is getting the grid system and basic entity movement working, as everything else builds on that foundation.
Would you like me to elaborate on any specific phase or discuss implementation details for particular systems?