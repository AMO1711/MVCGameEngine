package controller.mappers;

import model.weapons.ports.WeaponType;

public class WeaponTypeMapper {
    public static WeaponType fromWorldDef(WeaponType type) {
        // At the moment, the mapping is direct based on enum names
        return WeaponType.valueOf(type.name());
    }

}
