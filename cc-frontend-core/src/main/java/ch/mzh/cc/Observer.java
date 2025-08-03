package ch.mzh.cc;

import ch.mzh.cc.model.Entity;

public interface Observer {

    void onEntityMoved(Entity entity);

    void onEntitySelected(Entity entity);
    
    void onEntityDeselected();
}
