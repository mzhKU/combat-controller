package ch.mzh.cc.model;

import ch.mzh.cc.Position2D;

public class BaseRefuelPosition extends Position2D {

    public BaseRefuelPosition(Position2D p) {
        super(p.getX(), p.getY());
    }

}