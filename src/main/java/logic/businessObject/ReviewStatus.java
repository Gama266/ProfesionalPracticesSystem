package logic.businessObject;

/**
 *
 * @author akyer
 */
public enum ReviewStatus {
    PENDIENTE("Pendiente"),
    VALIDO("Válido"),
    NO_VALIDO("No Válido");

    private final String displayName;

    ReviewStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    public static ReviewStatus fromDb(String value) {
        if (value == null) return PENDIENTE;
        switch (value) {
            case "Válido":   return VALIDO;
            case "No Válido": return NO_VALIDO;
            default:          return PENDIENTE;
        }
    }

    public String toDb() { return displayName; }

    @Override
    public String toString() { return displayName; }
}