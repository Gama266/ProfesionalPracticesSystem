/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic.util;

/**
 *
 * @author akyer
 */
import logic.businessObject.Student;
import logic.businessObject.Teacher;
import logic.businessObject.User;

public class SessionContext {

    private static SessionContext instance;

    private User    currentUser;
    private Teacher currentTeacher;
    private Student currentStudent;

    private SessionContext() {}

    public static SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }
        return instance;
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }

    public Teacher getCurrentTeacher() { return currentTeacher; }
    public void setCurrentTeacher(Teacher currentTeacher) { this.currentTeacher = currentTeacher; }

    public Student getCurrentStudent() { return currentStudent; }
    public void setCurrentStudent(Student currentStudent) { this.currentStudent = currentStudent; }

    public void clear() {
        currentUser    = null;
        currentTeacher = null;
        currentStudent = null;
    }
}

