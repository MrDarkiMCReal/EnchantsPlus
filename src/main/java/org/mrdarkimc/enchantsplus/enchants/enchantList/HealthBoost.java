package org.mrdarkimc.enchantsplus.enchants.enchantList;

import org.mrdarkimc.enchantsplus.enchants.interfaces.Reloadable;

public class HealthBoost implements Reloadable {
    public HealthBoost() {
        Reloadable.register(this);
    }

    @Override
    public void reload() {

    }
}
