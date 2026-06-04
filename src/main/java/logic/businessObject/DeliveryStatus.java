/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.businessObject;

/**
 *
 * @author akyer
 */
public enum DeliveryStatus {
    NO_ENTREGADO("No Entregado"),
    ENTREGADO("Entregado");

    private final String displayName;

    DeliveryStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    /** Convierte el valor almacenado en BD al enum correspondiente. */
    public static DeliveryStatus fromDb(String value) {
        if ("Entregado".equalsIgnoreCase(value)) return ENTREGADO;
        return NO_ENTREGADO;
    }

    /** Valor que se almacena en la BD. */
    public String toDb() { return displayName; }

    @Override
    public String toString() { return displayName; }

}
