package org.mrdarkimc.enchantsplus.enchants.interfaces;


import org.mrdarkimc.SatanicLib.Debugger;

import java.util.ArrayList;
import java.util.List;

public interface Reloadable {

    static void register(Reloadable r){
        if (ReloadableClasses.contains(r))
            return;
        ReloadableClasses.add(r);
    }
    static void reloadAll(){
        //ReloadableClasses.forEach(Reloadable::reload);
        ReloadableClasses.forEach(e -> {
            Debugger.chat("Reloaded clazz: " +e.toString(),2);
            e.reload();
        });
    }


    void reload();
    static List<Reloadable> ReloadableClasses = new ArrayList<>();
}
