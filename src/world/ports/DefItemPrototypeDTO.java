package world.ports;

public final class DefItemPrototypeDTO implements DefItem {

    // region Fields
    public final String assetId;
    public final double density;
    public final double minSize;
    public final double maxSize;
    public final double minAngle;
    public final double maxAngle;
    public final double posMinX;
    public final double posMaxX;
    public final double posMinY;
    public final double posMaxY;
    // endregion

    // *** CONSTRUCTOR ***

    public DefItemPrototypeDTO(
            String assetId,
            double density,
            double minAngle, double maxAngle,
            double minSize, double maxSize,
            double posMinX, double posMaxX,
            double posMinY, double posMaxY) {

        this.assetId = assetId;

        this.density = density;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
        this.posMinX = posMinX;
        this.posMaxX = posMaxX;
        this.posMinY = posMinY;
        this.posMaxY = posMaxY;
    }
}
