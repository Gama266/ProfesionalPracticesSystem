/*
 * @(#)RegistroActividadView.java 1.0 22/04/2026
 * Copyright (c) 2026 JhonatanYerayLIS
 */

package gui.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
* Activity Log View.
* Builds the graphical interface of the activity registration form.
* Exposes its components to the controller through getter methods.
 *
 * @author Jhonatan Yeray Hernández Rivera
 * @version 1.0
 */
public class RegisterActivityView {

    private TextField   studentIdField;
    private DatePicker  activityDatePicker;
    private TextField   hoursField;
    private TextArea    descriptionArea;

    private Label studentIdError;
    private Label dateError;
    private Label hoursError;
    private Label descriptionError;

    private Button registerButton;
    private Button clearButton;

    private Label resultLabel;

    private final Stage stage;

    public RegisterActivityView() {
        stage = new Stage();
        stage.setTitle("Registro de Actividad");
        stage.setResizable(false);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F0F2F5;");
        root.setCenter(buildFormPanel());
        Scene scene = new Scene(root, 620, 520);
        stage.setScene(scene);
    }



    private ScrollPane buildFormPanel() {
        VBox card = new VBox(16);
        card.setPadding(new Insets(26, 28, 26, 28));
        
        card.getChildren().add(buildSectionHeader(
            "NUEVA ACTIVIDAD",
            "Registra las horas y descripción de una actividad de un estudiante."
        ));

        card.getChildren().add(buildSeparator());

        card.getChildren().add(buildField("Matrícula *",
                studentIdField   = buildTextField("Ej. S21013456"),
                studentIdError   = buildErrorLabel()));

        card.getChildren().add(buildField("Fecha de Actividad *",
                activityDatePicker = buildDatePicker(),
                dateError        = buildErrorLabel()));

        card.getChildren().add(buildField("Horas *",
                hoursField       = buildTextField("Ej. 3.5"),
                hoursError       = buildErrorLabel()));

        card.getChildren().add(buildTextAreaField("Descripción *",
                descriptionArea  = buildTextArea("Describe brevemente la actividad realizada..."),
                descriptionError = buildErrorLabel()));

        card.getChildren().add(buildSeparator());

        card.getChildren().add(buildButtonRow());

        resultLabel = new Label();
        resultLabel.setWrapText(true);
        resultLabel.setVisible(false);
        resultLabel.setManaged(false);
        resultLabel.setMaxWidth(Double.MAX_VALUE);
        card.getChildren().add(resultLabel);

        VBox wrapper = new VBox(card);
        wrapper.setPadding(new Insets(20, 24, 20, 24));
        VBox.setVgrow(card, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane(wrapper);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return scroll;
    }




    private VBox buildSectionHeader(String title, String description) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        titleLabel.setTextFill(Color.web("#2BBFAA"));

        Label descriptionLabel = new Label(description);
        descriptionLabel.setFont(Font.font("Segoe UI", 12));
        descriptionLabel.setTextFill(Color.web("#7A8FA6"));
        descriptionLabel.setWrapText(true);

        return new VBox(3, titleLabel, descriptionLabel);
    }

    private Region buildSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        VBox.setMargin(separator, new Insets(2, 0, 2, 0));
        return separator;
    }


    private VBox buildField(String labelText, Control control, Label errorLabel) {
        Label fieldLabel = new Label(labelText);
        fieldLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        fieldLabel.setTextFill(Color.web("#2C4A66"));

        control.setMaxWidth(Double.MAX_VALUE);

        VBox row = new VBox(5, fieldLabel, control, errorLabel);
        HBox.setHgrow(row, Priority.ALWAYS);
        return row;
    }

    private VBox buildTextAreaField(String labelText, TextArea textArea, Label errorLabel) {
        Label fieldLabel = new Label(labelText);
        fieldLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        fieldLabel.setTextFill(Color.web("#2C4A66"));

        textArea.setMaxWidth(Double.MAX_VALUE);

        VBox row = new VBox(5, fieldLabel, textArea, errorLabel);
        HBox.setHgrow(row, Priority.ALWAYS);
        return row;
    }

    private TextField buildTextField(String placeholder) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setPrefHeight(36);
        return textField;
    }

    private DatePicker buildDatePicker() {
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("dd/mm/aaaa");
        datePicker.setPrefHeight(36);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        return datePicker;
    }

    private TextArea buildTextArea(String placeholder) {
        TextArea textArea = new TextArea();
        textArea.setPromptText(placeholder);
        textArea.setPrefRowCount(4);
        textArea.setWrapText(true);
        return textArea;
    }

    private Label buildErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.setFont(Font.font("Segoe UI", 11));
        errorLabel.setTextFill(Color.web("#D32F2F"));
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        return errorLabel;
    }

    private HBox buildButtonRow() {
        clearButton = new Button("Limpiar campos");
        clearButton.setPrefHeight(36);

        registerButton = new Button(" Registrar Actividad");
        registerButton.setPrefHeight(36);

        HBox buttonRow = new HBox(10, clearButton, registerButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        return buttonRow;
    }

   



    public Stage getStage() { 
        return stage; 
    }

    public TextField   getStudentIdField()   { return studentIdField; }
    public DatePicker  getDatePicker()       { return activityDatePicker; }
    public TextField   getHoursField()       { return hoursField; }
    public TextArea    getDescriptionArea()  { return descriptionArea; }

    public Label getStudentIdError()  { return studentIdError; }
    public Label getDateError()       { return dateError; }
    public Label getHoursError()      { return hoursError; }
    public Label getDescriptionError() { return descriptionError; }

    public Button getRegisterButton() { return registerButton; }
    public Button getClearButton()    { return clearButton; }

    public Label getResultLabel()     { return resultLabel; }

    public void show() {
        stage.show();
    }
}
