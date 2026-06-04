
package com.mycompany.profesionalpracticessystem;

import logic.util.PasswordHasher;

/**
 *
 * @author akyer
 */
public class hasheo {
   public static void main(String[] args) {
        String hash = PasswordHasher.hash("Estudiante°1");
        System.out.println("Hash: " + hash);
    }
}
