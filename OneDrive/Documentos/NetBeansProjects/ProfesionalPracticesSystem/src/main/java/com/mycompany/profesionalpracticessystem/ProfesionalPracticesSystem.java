/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.profesionalpracticessystem;

import gui.controller.RegisterActivityController;
import gui.controller.RegisterEducationalExperienceController;
import gui.view.RegisterActivityView;
import gui.view.RegisterEducationalExperienceView;

import javafx.application.Application; 
import javafx.stage.Stage;


/**
 *
 * @author gamal
 */
public class ProfesionalPracticesSystem extends Application{

     @Override
    public void start(Stage primaryStage) {
        // Actividad
        RegisterActivityView actividadView = new RegisterActivityView();
        new RegisterActivityController(actividadView);
        actividadView.show();

        // Experiencia Educativa
        RegisterEducationalExperienceView educationalExperienceView = new RegisterEducationalExperienceView();
        new RegisterEducationalExperienceController(educationalExperienceView);
        educationalExperienceView.show();
    }
    
    public static void main(String[] args) {
        launch(args);  
    }
}
