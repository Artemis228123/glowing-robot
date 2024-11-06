package Model;

import java.util.*;

public class Attack {
    private final List<WeaponCard> weapons;
    private int value;

    public Attack() {
        this.weapons = new ArrayList<>();
        this.value = 0;
    }

    public boolean addWeapon(WeaponCard weapon) {
        if (weapons.stream()
                .anyMatch(w -> w.getType() == weapon.getType())) {
            return false; // No duplicate weapon types allowed
        }

        weapons.add(weapon);
        value += weapon.getValue();
        return true;
    }

    public int getValue() {
        return value;
    }

    public List<WeaponCard> getWeapons() {
        return Collections.unmodifiableList(weapons);
    }
}
