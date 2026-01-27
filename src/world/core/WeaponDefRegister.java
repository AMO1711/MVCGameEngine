package world.core;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import world.ports.DefWeaponDTO;
import world.ports.DefWeaponType;

public final class WeaponDefRegister {

    // region Fields
    private final WorldAssetsRegister assets;
    private final EnumMap<DefWeaponType, ArrayList<DefWeaponDTO>> buckets = new EnumMap<>(DefWeaponType.class);
    // endregion

    // *** CONSTRUCTORS ***

    public WeaponDefRegister(WorldAssetsRegister assets) {
        if (assets == null)
            throw new IllegalArgumentException("assets cannot be null");
        this.assets = assets;
        for (DefWeaponType t : DefWeaponType.values()) {
            buckets.put(t, new ArrayList<>());
        }
    }


    // *** PUBLIC ***

    public void add(DefWeaponDTO weapon) {
        if (weapon == null)
            throw new IllegalArgumentException("weapon cannot be null");
        if (weapon.weaponType == null)
            throw new IllegalArgumentException("weapon.weaponType cannot be null");

        assets.registerAssetId(weapon.assetId);
        buckets.get(weapon.weaponType).add(weapon);
    }

    public ArrayList<DefWeaponDTO> bucket(DefWeaponType type) {
        if (type == null)
            throw new IllegalArgumentException("type cannot be null");

        return buckets.get(type);
    }

    public void clear() {
        for (List<DefWeaponDTO> list : buckets.values())
            list.clear();
    }

    public ArrayList<DefWeaponDTO> snapshot(DefWeaponType type) {
        return new ArrayList<>(bucket(type));
    }
}
