package ch.mzh.cc.command;

import ch.mzh.cc.GameCore;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class CommandProcessor {

  /*
  Input Layer (InputHandler, AI Controller, Network, etc.)
           ↓ (sends commands)
  Control Layer (CommandProcessor)
           ↓ (executes operations)
  Game Logic Layer (GameCore)
           ↓ (triggers events)
  System Layer (SupplyRuleEngine, FuelSystem, etc.)
  */

  private final GameCore gameCore;
  private final Queue<Command> commandQueue = new ArrayDeque<>();
  private final List<Command> executedCommands = new ArrayList<>();
  private final List<Command> failedCommands = new ArrayList<>();

  public CommandProcessor(GameCore gameCore) {
    this.gameCore = gameCore;
  }

  public void queueCommand(Command command) {
    commandQueue.offer(command);
  }

  public void executeNextCommand() {
    Command command = commandQueue.poll();

    if (command == null) return;

    if (command.execute(gameCore)) {
      executedCommands.add(command);
    } else {
      failedCommands.add(command);
      System.out.println("Command failed: " + command.getFailureReason());
    }
  }

  public void executeAllCommands() {
    while (!commandQueue.isEmpty()) {
      executeNextCommand();
    }
  }

  public List<Command> getExecutedCommands() {
    return executedCommands;
  }

  public List<Command> getFailedCommands() {
    return new ArrayList<>(failedCommands);
  }
}
