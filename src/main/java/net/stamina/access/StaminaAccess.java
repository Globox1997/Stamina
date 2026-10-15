package net.stamina.access;

import net.stamina.api.StaminaCostSource;

public interface StaminaAccess {

    public void addStamina(int stamina);

    public void setStamina(int stamina);

    public int getStamina();

    public void consumeStamina(float cost, StaminaCostSource source);

    public int getExhaustionTime();

    public void requestStaminaSync();

    public void applyServerStamina(int stamina, int exhaustionTime, boolean force);

}
