package org.mrdarkimc.enchantsplus.enchants.interfaces;


import java.util.ArrayList;
import java.util.List;

public interface Reloadable {

    static void register(Reloadable r){
        if (!ReloadableClasses.contains(r))
            return;
        ReloadableClasses.add(r);
    }
    static void reloadAll(){
        ReloadableClasses.forEach(Reloadable::reload);
    }


    void reload();
    static List<Reloadable> ReloadableClasses = new ArrayList<>();
}
