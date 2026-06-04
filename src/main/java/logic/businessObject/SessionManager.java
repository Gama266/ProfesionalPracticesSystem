/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.businessObject;

/**
 *
 * @author gamal
 */

public class SessionManager {

    private static User currentUser;

    private static String currentEnrollment;

    private SessionManager() {
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static String getCurrentEnrollment() {
        return currentEnrollment;
    }

    public static void setCurrentEnrollment(String enrollment) {
        currentEnrollment = enrollment;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
        currentEnrollment = null;
    }
}

