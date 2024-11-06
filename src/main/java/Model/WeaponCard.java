package Model;

public class WeaponCard extends AdventureCard {
    private final WeaponType type;

    public enum WeaponType {
        DAGGER('D', 5),
        HORSE('H', 10),
        SWORD('S', 10),
        BATTLE_AXE('B', 15),
        LANCE('L', 20),
        EXCALIBUR('E', 30);

        private final char code;
        private final int value;

        WeaponType(char code, int value) {
            this.code = code;
            this.value = value;
        }

        public char getCode() {
            return code;
        }

        public int getValue() {
            return value;
        }
    }

    public WeaponCard(WeaponType type) {
        super(type.getCode() + String.valueOf(type.getValue()), type.getValue());
        this.type = type;
    }

    public WeaponType getType() {
        return type;
    }
}
