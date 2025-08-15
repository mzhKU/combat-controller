package ch.mzh;

import ch.mzh.cc.Game;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Desktop {

  private final static int WIDTH = 400;
  private final static int HEIGHT = 800;

  public static void main(String[] arg) {
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setTitle("Combat Controller");
    config.setWindowedMode(WIDTH, HEIGHT);
    config.setResizable(true);
    new Lwjgl3Application(new Game(), config);
  }
}