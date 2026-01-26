package world.core;

import world.ports.DefWeaponDTO;
import world.ports.DefWeaponType;

public final class WeaponDefFactory {

    // *** PUBLIC STATICS ***

    public static DefWeaponDTO create(
            String assetId,
            DefWeaponType weaponType,

            // Projectile params
            double projectileSize,
            double projectileSpeed,
            double projectileThrust,
            double projectileThrustDuration,
            double projectileMass,
            double projectileMaxLifetime,

            // Firing params
            int burstSize,
            int burstFireRate,
            int fireRate,
            int maxAmmo,
            double reloadTime) {

        if (weaponType == null) {
            throw new IllegalArgumentException("DefWeaponType cannot be null.");
        }

        // Fail-fast validations (keep them light but useful)
        if (projectileSize <= 0)
            throw new IllegalArgumentException("projectileSize must be > 0");
        if (projectileSpeed < 0)
            throw new IllegalArgumentException("projectileSpeed must be >= 0");
        if (projectileMass <= 0)
            throw new IllegalArgumentException("projectileMass must be > 0");
        if (projectileMaxLifetime <= 0)
            throw new IllegalArgumentException("projectileMaxLifetime must be > 0");
        if (burstSize <= 0)
            throw new IllegalArgumentException("burstSize must be >= 1");
        if (fireRate < 0)
            throw new IllegalArgumentException("fireRate must be >= 0");
        if (maxAmmo < 0)
            throw new IllegalArgumentException("maxAmmo must be >= 0");
        if (reloadTime < 0)
            throw new IllegalArgumentException("reloadTime must be >= 0");

        // If burstSize > 1, burstFireRate should be > 0 (otherwise burst is
        // meaningless).
        if (burstSize > 1 && burstFireRate <= 0) {
            throw new IllegalArgumentException("burstFireRate must be > 0 when burstSize > 1");
        }

        return new DefWeaponDTO(
                assetId,
                projectileSize,
                weaponType,
                projectileSpeed,
                projectileThrust,
                projectileThrustDuration,
                burstSize,
                burstFireRate,
                fireRate,
                maxAmmo,
                reloadTime,
                projectileMass,
                projectileMaxLifetime);
    }

    public static DefWeaponDTO createPresetedBulletBasic(String assetId) {
        return create(
                assetId,
                DefWeaponType.BULLET_WEAPON,

                8.0, // projectileSize
                650.0, // projectileSpeed
                0.0, // projectileThrust
                0.0, // projectileThrustDuration
                1.0, // projectileMass
                2.0, // projectileMaxLifetime

                1, // burstSize
                0, // burstFireRate
                8, // fireRate (shots/s)
                200, // maxAmmo
                1.5 // reloadTime
        );
    }

    public static DefWeaponDTO createPresetedBurst(String assetId) {
        return create(
                assetId,
                DefWeaponType.BURST_WEAPON,

                7.0,
                600.0,
                0.0,
                0.0,
                0.8,
                1.6,

                3, // burstSize
                12, // burstFireRate inside burst
                2, // fireRate (bursts/s)
                60,
                2.0);
    }

    public static DefWeaponDTO createPresetedMissileLauncher(String assetId) {
        return create(
                assetId,
                DefWeaponType.MISSILE_LAUNCHER,

                14.0,
                250.0,
                180.0, // projectileThrust
                1.8, // projectileThrustDuration
                6.0,
                5.0,

                1,
                0,
                1, // fireRate (missiles/s)
                12,
                3.5);
    }

    public static DefWeaponDTO createPresetedMineLauncher(String assetId) {
        return create(
                assetId,
                DefWeaponType.MINE_LAUNCHER,

                18.0,
                120.0,
                0.0,
                0.0,
                10.0,
                12.0,

                1,
                0,
                1, // mines/s
                6,
                5.0);
    }

}
