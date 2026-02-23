package gameworld;

import engine.assets.core.AssetCatalog;
import engine.assets.ports.AssetIntensity;
import engine.assets.ports.AssetType;

public class ProjectMusic {
    public final AssetCatalog catalog;

    public ProjectMusic() {
        this.catalog = new AssetCatalog("src/resources/music");

        this.catalog.register("bocanada", "Bocanada.mp3", AssetType.MUSIC, AssetIntensity.HIGH);
        this.catalog.register("yro", "yro.WAV", AssetType.MUSIC, AssetIntensity.HIGH);
        this.catalog.register("inviernoFrenetico", "inviernoFrenetico.mp3", AssetType.MUSIC, AssetIntensity.HIGH);
    }
}
