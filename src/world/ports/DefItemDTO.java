package world.ports;

public final class DefItemDTO implements DefItem {

    // region Fields
    public final String assetId;
    public final double posX;
    public final double posY;
    public final double size;
    public final double angle;
    public final double density;
    // endregion

    // *** CONSTRUCTOR ***

    public DefItemDTO(
            String assetId, double size, double angle,
            double posX, double posY, double density) {

        this.assetId = assetId;
        this.posX = posX;
        this.posY = posY;
        this.size = size;
        this.angle = angle;
        this.density = density;
    }
}
