
package logic.businessObject;

/**
 *
 * @author akyer
 */
public enum UserType {
    
    ESTUDIANTE("estudiante"),
    COORDINADOR("coordinador"),
    PROFESOR("profesor"),
    ADMINISTRADOR("administrador");

    private final String dbValue;

    UserType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static UserType fromDbValue(String value) {
        for (UserType type : values()) {
            if (type.dbValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de usuario desconocido: " + value);
    }
}
