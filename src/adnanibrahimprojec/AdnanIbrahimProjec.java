
package adnanibrahimprojec;

import java.io.File;
import java.io.FileInputStream;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.format.DateTimeFormatter;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AdnanIbrahimProjec extends Application {
       TableView<PropertyRecord> table = new TableView<>();

       String currentUsername = null;
      
       private Connection connectDatabase() throws SQLException {
           
    String url = "jdbc:mysql://localhost:3306/aboi"; // Change database URL
    String user = "root"; 
    String password = ""; 

    return DriverManager.getConnection(url, user, password);

    }
       public static class PaymentHistoryRecord {
    private final SimpleStringProperty propertyName;
    private final SimpleStringProperty tenantName;
    private final SimpleStringProperty paymentAmount;
    private final SimpleStringProperty paymentDate;

    public PaymentHistoryRecord(String propertyName, String tenantName, String paymentAmount, String paymentDate) {
        this.propertyName = new SimpleStringProperty(propertyName);
        this.tenantName = new SimpleStringProperty(tenantName);
        this.paymentAmount = new SimpleStringProperty(paymentAmount);
        this.paymentDate = new SimpleStringProperty(paymentDate);
    }

    public String getPropertyName() { return propertyName.get(); }
    public String getTenantName() { return tenantName.get(); }
    public String getPaymentAmount() { return paymentAmount.get(); }
    public String getPaymentDate() { return paymentDate.get(); }

    public void setPropertyName(String value) { propertyName.set(value); }
    public void setTenantName(String value) { tenantName.set(value); }
    public void setPaymentAmount(String value) { paymentAmount.set(value); }
    public void setPaymentDate(String value) { paymentDate.set(value); }
}
       public static class PropertyRecord {
    private final SimpleStringProperty propertyName;
    private final SimpleStringProperty tenantName;
    private final SimpleStringProperty location;
    private final SimpleStringProperty payment;
    private final SimpleStringProperty endingDate;
   private final SimpleStringProperty startingDate;
   private final SimpleStringProperty fines;

public PropertyRecord(String propertyName, String tenantName, String location, String payment, String endingDate, String startingDate, String fines) {
    this.propertyName = new SimpleStringProperty(propertyName);
    this.tenantName = new SimpleStringProperty(tenantName);
    this.location = new SimpleStringProperty(location);
    this.payment = new SimpleStringProperty(payment);
    this.endingDate = new SimpleStringProperty(endingDate);
    this.startingDate = new SimpleStringProperty(startingDate);
    this.fines = new SimpleStringProperty(fines);
}

public String getStartingDate() {
    return startingDate.get();
}

public String getFines() {
    return fines.get();
}


    public String getPropertyName() {
        return propertyName.get();
    }

    public String getTenantName() {
        return tenantName.get();
    }

    public String getLocation() {
        return location.get();
    }

    public String getPayment() {
        return payment.get();
    }

    public String getEndingDate() {
        return endingDate.get();
    }

    public void setPropertyName(String value) {
        propertyName.set(value);
    }

    public void setTenantName(String value) {
        tenantName.set(value);
    }

    public void setLocation(String value) {
        location.set(value);
    }

    public void setPayment(String value) {
        payment.set(value);
    }

    public void setEndingDate(String value) {
        endingDate.set(value);
    }
}
       public static class MaintenanceRecord {
    private final SimpleStringProperty propertyName;
    private final SimpleStringProperty tenantName;
    private final SimpleStringProperty maintenanceType;

    public MaintenanceRecord(String propertyName, String tenantName, String maintenanceType) {
        this.propertyName = new SimpleStringProperty(propertyName);
        this.tenantName = new SimpleStringProperty(tenantName);
        this.maintenanceType = new SimpleStringProperty(maintenanceType);
    }

    public String getPropertyName() {
        return propertyName.get();
    }

    public String getTenantName() {
        return tenantName.get();
    }

    public String getMaintenanceType() {
        return maintenanceType.get();
    }

    public void setPropertyName(String value) {
        propertyName.set(value);
    }

    public void setTenantName(String value) {
        tenantName.set(value);
    }

    public void setMaintenanceType(String value) {
        maintenanceType.set(value);
    }
}
       private ObservableList<MaintenanceRecord> getMaintenanceRecordsFromDB() {
    ObservableList<MaintenanceRecord> data = FXCollections.observableArrayList();

    if (currentUsername == null) {
        return data; // No user logged in
    }

    String query = "SELECT property_name, tenant_name, maintenance_type FROM maintenance WHERE username = ?";

    try (Connection conn = connectDatabase();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, currentUsername);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String propertyName = rs.getString("property_name");
            String tenantName = rs.getString("tenant_name");
            String maintenanceType = rs.getString("maintenance_type");

            data.add(new MaintenanceRecord(propertyName, tenantName, maintenanceType));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return data;
}
       private ObservableList<PaymentHistoryRecord> getPaymentHistoryRecordsFromDB() {
    ObservableList<PaymentHistoryRecord> data = FXCollections.observableArrayList();

    if (currentUsername == null) {
        return data;
    }

    String query = "SELECT property_name, tenant_name, payment_amount, payment_date FROM payment_history WHERE username = ? ORDER BY payment_date DESC";

    try (Connection conn = connectDatabase();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, currentUsername);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String propertyName = rs.getString("property_name");
            String tenantName = rs.getString("tenant_name");
            String paymentAmount = String.valueOf(rs.getDouble("payment_amount"));
            String paymentDate = rs.getString("payment_date");

            data.add(new PaymentHistoryRecord(propertyName, tenantName, paymentAmount, paymentDate));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return data;
}
       private ObservableList<PropertyRecord> getPropertyRecordsFromDB() {
    ObservableList<PropertyRecord> data = FXCollections.observableArrayList();

    if (currentUsername == null) {
        return data; // Return empty if no user is logged in
    }

    String query = "SELECT property_name, tenant_name, location, payment, ending_date, starting_date, fines FROM properties WHERE username = ?";

    try (Connection conn = connectDatabase();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, currentUsername);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String propertyName = rs.getString("property_name");
            String tenantName = rs.getString("tenant_name");
            String location = rs.getString("location");
            String payment = rs.getString("payment");
            String endingDate = rs.getDate("ending_date").toString();
            String startingDate = (rs.getDate("starting_date") != null) ? rs.getDate("starting_date").toString() : "";
            String fines = rs.getString("fines");  // fines is used for Rent per Month in your design

            data.add(new PropertyRecord(propertyName, tenantName, location, payment, endingDate, startingDate, fines));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return data;
}



    @Override     
    public void start(Stage primaryStage) {
        String backgroundColor = "#181a1b";     // Charcoal black – subtle and elegant
String buttonColor = "#2c2f33";         // Muted slate – clear but not loud
String WtextColor = "#dcdcdc"; 
      String fontSizeButtons = "16px";
       


      Image image1 = new Image("file:src/resources/logo.png");
      ImageView imageView2 = new ImageView(image1);
      imageView2.setFitWidth(400);  
      imageView2.setFitHeight(400); 
      imageView2.setPreserveRatio(true);
      Button LoginDash = new Button("Get Started");
      LoginDash.setMinSize(150, 45);
      VBox loginRoot = new VBox(20); // 20 px spacing
      loginRoot.setAlignment(Pos.CENTER); // Optional: center content
      loginRoot.getChildren().addAll(imageView2,LoginDash);
      Scene mainPage = new Scene(loginRoot, 750, 750);
      //Styling 
      loginRoot.setStyle("-fx-background-color: "+backgroundColor+";"); 
      LoginDash.setStyle("-fx-background-color: "+ buttonColor +" ; -fx-text-fill: "+ WtextColor +"; -fx-font-size: 20px"); 
      
      

        Image image2 = new Image("file:src/resources/logo.png");
        ImageView imageView1 = new ImageView(image2);
        imageView1.setFitWidth(120);
        imageView1.setFitHeight(120);
        imageView1.setPreserveRatio(true);
        Label loginLabel = new Label("Login");
        loginLabel.setStyle("-fx-text-fill : "+WtextColor+";");
        loginLabel.setFont(Font.font(32));
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-text-fill : "+WtextColor+";");
        TextField usernameTxt = new TextField();
        usernameTxt.setPromptText("Enter username");
        usernameTxt.setStyle("-fx-background-color: transparent; -fx-text-fill: "+WtextColor+"; -fx-border-color: "+WtextColor+"; -fx-border-width: 0 0 1 0;");
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-text-fill : "+WtextColor+";");
        PasswordField passwordTxt = new PasswordField();
        passwordTxt.setPromptText("Enter password");
        passwordTxt.setStyle("-fx-background-color: transparent; -fx-text-fill: "+WtextColor+"; -fx-border-color: "+WtextColor+"; -fx-border-width: 0 0 1 0;");
        Button loginBtn = new Button("Login");
        Button RegisterBtn = new Button("Register");
        Button backLoginBTN = new Button("Back");
        String buttonStyle = "-fx-background-color: " + buttonColor + ";" +
                             "-fx-text-fill: " + WtextColor + ";" +
                             "-fx-font-size: " + fontSizeButtons + ";" +
                             "-fx-pref-width: 100;";
        loginBtn.setStyle(buttonStyle);
        RegisterBtn.setStyle(buttonStyle);
        backLoginBTN.setStyle(buttonStyle);

        GridPane loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setHgap(10);
        loginGrid.setVgap(15);
        loginGrid.setPadding(new Insets(25));

        loginGrid.add(imageView1, 1, 0, 1, 1); // Centered image
        loginGrid.add(loginLabel, 1, 1);
        loginGrid.add(usernameLabel, 0, 2);
        loginGrid.add(usernameTxt, 1, 2);
        loginGrid.add(passwordLabel, 0, 3);
        loginGrid.add(passwordTxt, 1, 3);
        loginGrid.add(loginBtn, 0, 4);
        loginGrid.add(RegisterBtn, 1, 4);
        loginGrid.add(backLoginBTN, 2, 4);

        StackPane LoginRoot = new StackPane(loginGrid);
        LoginRoot.setStyle("-fx-background-color: " + backgroundColor + ";");
        Scene LoginPage = new Scene(LoginRoot, 750, 750);


    RegisterBtn.setOnAction(e -> {
    Stage popupStage4 = new Stage();
    popupStage4.setTitle("Register New User");
    GridPane grid4 = new GridPane();
    grid4.setAlignment(Pos.CENTER);
    grid4.setHgap(10);
    grid4.setVgap(10);
    grid4.setPadding(new Insets(20));
    
    
    TextField usernameNameField = new TextField();
    TextField passwordNameField = new TextField();
    TextField confirmpasswordNameField = new TextField();
    Label usernameLabel4 = new Label("Enter Username:");
    Label passwordLabel4 = new Label("Enter Password:");
    Label confirmLabel4 = new Label("Confirm Password:");
    Button RegisterButton = new Button("Register"); 
    
    
    RegisterButton.setStyle(
        "-fx-background-color: "+buttonColor+"; " +
        "-fx-text-fill: "+WtextColor+"; " +
        "-fx-font-size: "+fontSizeButtons+"; " +
        "-fx-background-radius: 5px;"
    );
    grid4.setStyle("-fx-background-color: "+backgroundColor+";");
    usernameLabel4.setStyle("-fx-text-fill: "+WtextColor+"; -fx-font-size: 14px;");
    passwordLabel4.setStyle("-fx-text-fill: "+WtextColor+"; -fx-font-size: 14px;");
    confirmLabel4.setStyle("-fx-text-fill: "+WtextColor+"; -fx-font-size: 14px;");
    usernameNameField.setStyle("-fx-background-color: "+buttonColor+"; -fx-text-fill: "+WtextColor+";");
    passwordNameField.setStyle("-fx-background-color: "+buttonColor+"; -fx-text-fill: "+WtextColor+";");
    confirmpasswordNameField.setStyle("-fx-background-color: "+buttonColor+"; -fx-text-fill: "+WtextColor+";");



    
    
    grid4.add(usernameLabel4, 0, 0);
    grid4.add(usernameNameField, 1, 0);
    grid4.add(passwordLabel4, 0, 1);
    grid4.add(passwordNameField, 1, 1);
    grid4.add(confirmLabel4, 0, 2);
    grid4.add(confirmpasswordNameField, 1, 2);


    grid4.add(RegisterButton, 1, 5);
    
    RegisterButton.setOnAction(ev -> {
    String username4 = usernameNameField.getText();
    String password4 = passwordNameField.getText();
    String confirmedPassword4 = confirmpasswordNameField.getText();
    if(!username4.isEmpty()&!password4.isEmpty()&!confirmedPassword4.isEmpty() & 
       !username4.equals(password4)& confirmedPassword4.equals(password4))
    {
    try (Connection conn = connectDatabase()) {
    String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
    
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setString(1, username4);
    stmt.setString(2, password4);
    stmt.executeUpdate();
    stmt.close();
    conn.close();
        
    table.setItems(getPropertyRecordsFromDB());
    popupStage4.close();

    } 
    catch (Exception ex) 
    {
        ex.printStackTrace();
        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error saving property: " + ex.getMessage(), ButtonType.OK);
        errorAlert.showAndWait();
    }
  }
    else
    {
        String errorString ="ERROR :";
        if(username4.isEmpty() || password4.isEmpty() || confirmedPassword4.isEmpty())
       {
        errorString += "Empty fields,  ";
        }
        if(!username4.isEmpty() && username4.equals(password4))
        {
        errorString += "Username must be different than password,  ";
        }
        if(!password4.isEmpty() && !confirmedPassword4.equals(password4))
        {
        errorString += "Password confimation is wrong,  ";
        }
        errorString += "Try Again.";
    Alert errorAlert = new Alert(Alert.AlertType.ERROR, errorString);
    errorAlert.showAndWait();
    errorString = "";
    }
});
    Scene popupScene = new Scene(grid4, 400, 300);
    popupStage4.setScene(popupScene);
    popupStage4.show();
});
      
       BorderPane dashboard = new BorderPane();
       // Left Dashboard button

        VBox leftDashboard = new VBox(10);
        leftDashboard.setPrefWidth(200); 
        leftDashboard.setPadding(new Insets(10));
        leftDashboard.setStyle("-fx-background-color: "+backgroundColor+";");

  
        
        // Button Properties button
        Button buttonProperties = new Button("Properties");
        buttonProperties.setMaxWidth(Double.MAX_VALUE);
        buttonProperties.setStyle("-fx-background-color: "+buttonColor+"; -fx-text-fill: "+ WtextColor +"; -fx-font-size: "+ fontSizeButtons +";");
        buttonProperties.setPrefWidth(100);
        buttonProperties.setPrefHeight(100);
        leftDashboard.getChildren().add(buttonProperties);

        // Maintenance History button
        Button buttonMaintenance = new Button("Maintenance History");
        buttonMaintenance.setMaxWidth(Double.MAX_VALUE);
        buttonMaintenance.setStyle("-fx-background-color: "+ buttonColor +"; -fx-text-fill: "+ WtextColor +"; -fx-font-size: "+ fontSizeButtons +";");
        buttonMaintenance.setPrefWidth(100);
        buttonMaintenance.setPrefHeight(100);
        leftDashboard.getChildren().add(buttonMaintenance);

        // Payments button
        Button buttonPayments = new Button("Payments History");
        buttonPayments.setMaxWidth(Double.MAX_VALUE);
        buttonPayments.setStyle("-fx-background-color: "+ buttonColor +"; -fx-text-fill: "+ WtextColor +"; -fx-font-size: "+ fontSizeButtons +";");
        buttonPayments.setPrefWidth(100);
        buttonPayments.setPrefHeight(100);
        leftDashboard.getChildren().add(buttonPayments);

        // Tenants / Contracts button
         Button buttonTenants = new Button("Tenants / Contracts");
         buttonTenants.setMaxWidth(Double.MAX_VALUE);
         buttonTenants.setStyle("-fx-background-color: "+ buttonColor +"; -fx-text-fill: "+ WtextColor +"; -fx-font-size: "+ fontSizeButtons +";");
         buttonTenants.setPrefWidth(100);
         buttonTenants.setPrefHeight(100);
         leftDashboard.getChildren().add(buttonTenants);

         // Service Requests / Work Orders button
         Button buttonOptions = new Button("Options");
         buttonOptions.setMaxWidth(Double.MAX_VALUE);
         buttonOptions.setStyle("-fx-background-color: "+ buttonColor +"; -fx-text-fill: "+ WtextColor +"; -fx-font-size: "+ fontSizeButtons +";");
         buttonOptions.setPrefWidth(100);
         buttonOptions.setPrefHeight(100);
         leftDashboard.getChildren().add(buttonOptions);


         buttonProperties.setOnMouseEntered(e -> buttonProperties.setCursor(Cursor.HAND));
         buttonProperties.setOnMouseExited(e -> buttonProperties.setCursor(Cursor.DEFAULT));
         buttonMaintenance.setOnMouseEntered(e -> buttonMaintenance.setCursor(Cursor.HAND));
         buttonMaintenance.setOnMouseExited(e -> buttonMaintenance.setCursor(Cursor.DEFAULT));
         buttonPayments.setOnMouseEntered(e -> buttonPayments.setCursor(Cursor.HAND));
         buttonPayments.setOnMouseExited(e -> buttonPayments.setCursor(Cursor.DEFAULT));
         buttonTenants.setOnMouseEntered(e -> buttonTenants.setCursor(Cursor.HAND));
         buttonTenants.setOnMouseExited(e -> buttonTenants.setCursor(Cursor.DEFAULT));
         buttonOptions.setOnMouseEntered(e -> buttonOptions.setCursor(Cursor.HAND));
         buttonOptions.setOnMouseExited(e -> buttonOptions.setCursor(Cursor.DEFAULT));



         
         

        HBox topDashboard = new HBox();
        topDashboard.setPadding(new Insets(10));
        topDashboard.setStyle("-fx-background-color: "+ buttonColor +";");
        topDashboard.setAlignment(Pos.CENTER_RIGHT);
        topDashboard.setPadding(new Insets(10));
       
        Button logOutButton = new Button("Log Out");
        Button addButton = new Button("Add Property");
        Button modifyButton = new Button("Modify Property");
        Button removeButton = new Button("Remove Property");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: "+ WtextColor +";");
        modifyButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: "+ WtextColor +";");
        removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: "+ WtextColor +";");
        logOutButton.setStyle("-fx-background-color: #cc0000; -fx-text-fill: "+ WtextColor +";");
        
        topDashboard.getChildren().add(logOutButton);
        buttonProperties.setOnAction(e -> 
        {
        topDashboard.getChildren().clear();
        topDashboard.getChildren().addAll(addButton, modifyButton, removeButton,logOutButton);
        dashboard.setCenter(table);
        });
        
// Maintenance Scene and page


TableView<MaintenanceRecord> maintenanceTable = new TableView<>();
TableColumn<MaintenanceRecord, String> maintenancePropertyCol = new TableColumn<>("Property Name");
maintenancePropertyCol.setCellValueFactory(new PropertyValueFactory<>("propertyName"));

TableColumn<MaintenanceRecord, String> maintenanceTenantCol = new TableColumn<>("Tenant Name");
maintenanceTenantCol.setCellValueFactory(new PropertyValueFactory<>("tenantName"));

TableColumn<MaintenanceRecord, String> maintenanceTypeCol = new TableColumn<>("Maintenance Type");
maintenanceTypeCol.setCellValueFactory(new PropertyValueFactory<>("maintenanceType"));

maintenanceTable.getColumns().addAll(maintenancePropertyCol, maintenanceTenantCol, maintenanceTypeCol);
maintenanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);



buttonMaintenance.setOnAction(e ->
{
topDashboard.getChildren().clear();
topDashboard.getChildren().addAll(addButton, modifyButton, removeButton,logOutButton);
maintenanceTable.setItems(getMaintenanceRecordsFromDB());
dashboard.setCenter(maintenanceTable); 
});



buttonMaintenance.setOnAction(e -> {
    topDashboard.getChildren().clear();

    Button addMaintenanceButton = new Button("Add Maintenance");
    Button deleteMaintenanceButton = new Button("Delete Maintenance");

    addMaintenanceButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: "+ WtextColor +";");
    deleteMaintenanceButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: "+ WtextColor +";");

    topDashboard.getChildren().addAll(addMaintenanceButton, deleteMaintenanceButton, logOutButton);

    maintenanceTable.setItems(getMaintenanceRecordsFromDB());
    dashboard.setCenter(maintenanceTable);

    // Add Maintenance button action
    addMaintenanceButton.setOnAction(ev -> {
        Stage popupStage = new Stage();
        popupStage.setTitle("Add Maintenance Record");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        
        Label propertyLabel = new Label("Property Name:");
        Label tenantLabel = new Label("Tenant Name:");
        Label typeLabel = new Label("Maintenance Type:");
        TextField propertyNameField = new TextField();
        TextField tenantNameField = new TextField();
        TextField maintenanceTypeField = new TextField();
        Button saveButton = new Button("Save");
        
        grid.setStyle("-fx-background-color: " + backgroundColor + ";");
        propertyNameField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
        tenantNameField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
        maintenanceTypeField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
        propertyLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
        tenantLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
        typeLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
        saveButton.setStyle(
        "-fx-background-color: " + buttonColor + "; " +
        "-fx-text-fill: " + WtextColor + "; " +
        "-fx-font-size: " + fontSizeButtons + "; " +
        "-fx-background-radius: 5px;"
        );
        
        grid.add(propertyLabel, 0, 0);
        grid.add(propertyNameField, 1, 0);
        grid.add(tenantLabel, 0, 1);
        grid.add(tenantNameField, 1, 1);
        grid.add(typeLabel, 0, 2);
        grid.add(maintenanceTypeField, 1, 2);
        grid.add(saveButton, 1, 3);

        saveButton.setOnAction(evSave -> {
            String propertyName = propertyNameField.getText();
            String tenantName = tenantNameField.getText();
            String maintenanceType = maintenanceTypeField.getText();

            if (!propertyName.isEmpty() && !tenantName.isEmpty() && !maintenanceType.isEmpty()) {
                try (Connection conn = connectDatabase()) {
                    String sql = "INSERT INTO maintenance (username, property_name, tenant_name, maintenance_type) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, currentUsername);
                    stmt.setString(2, propertyName);
                    stmt.setString(3, tenantName);
                    stmt.setString(4, maintenanceType);

                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();

                    // Refresh table
                    maintenanceTable.setItems(getMaintenanceRecordsFromDB());

                    // Close popup
                    popupStage.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error saving maintenance record: " + ex.getMessage(), ButtonType.OK);
                    errorAlert.showAndWait();
                }
            } else {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields.", ButtonType.OK);
                warningAlert.showAndWait();
            }
        });

        Scene popupScene = new Scene(grid, 400, 300);
        popupStage.setScene(popupScene);
        popupStage.show();
    });

    // Delete Maintenance button action
    deleteMaintenanceButton.setOnAction(ev -> {
        Stage popupStage = new Stage();
        popupStage.setTitle("Delete Maintenance Record");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        grid.setStyle("-fx-background-color: " + backgroundColor + ";");
        Label propertyLabel = new Label("Property Name:");
        Label tenantLabel = new Label("Tenant Name:");
        Label typeLabel = new Label("Maintenance Type:");
        TextField propertyNameField = new TextField();
        TextField tenantNameField = new TextField();
        TextField maintenanceTypeField = new TextField();
        Button deleteButton = new Button("Delete");
        
        propertyNameField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
        tenantNameField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
        maintenanceTypeField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
        propertyLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
        tenantLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
        typeLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
        deleteButton.setStyle(
        "-fx-background-color: " + buttonColor + "; " +
        "-fx-text-fill: " + WtextColor + "; " +
        "-fx-font-size: " + fontSizeButtons + "; " +
        "-fx-background-radius: 5px;"
        );
        
        grid.add(propertyLabel, 0, 0);
        grid.add(propertyNameField, 1, 0);
        grid.add(tenantLabel, 0, 1);
        grid.add(tenantNameField, 1, 1);
        grid.add(typeLabel, 0, 2);
        grid.add(maintenanceTypeField, 1, 2);
        grid.add(deleteButton, 1, 3);

        deleteButton.setOnAction(evDelete -> {
            String propertyName = propertyNameField.getText();
            String tenantName = tenantNameField.getText();
            String maintenanceType = maintenanceTypeField.getText();

            if (!propertyName.isEmpty() && !tenantName.isEmpty() && !maintenanceType.isEmpty()) {
                try (Connection conn = connectDatabase()) {
                    String sql = "DELETE FROM maintenance WHERE username = ? AND property_name = ? AND tenant_name = ? AND maintenance_type = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    stmt.setString(1, currentUsername);
                    stmt.setString(2, propertyName);
                    stmt.setString(3, tenantName);
                    stmt.setString(4, maintenanceType);

                    stmt.executeUpdate();
                    stmt.close();
                    conn.close();

                    // Refresh table
                    maintenanceTable.setItems(getMaintenanceRecordsFromDB());

                    // Close popup
                    popupStage.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error deleting maintenance record: " + ex.getMessage(), ButtonType.OK);
                    errorAlert.showAndWait();
                }
            } else {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields.", ButtonType.OK);
                warningAlert.showAndWait();
            }
        });

        Scene popupScene = new Scene(grid, 400, 300);
        popupStage.setScene(popupScene);
        popupStage.show();
    });
});


// PROPERTY PAGE

        TableColumn<PropertyRecord, String> propertyCol = new TableColumn<>("Property Name");
        propertyCol.setCellValueFactory(new PropertyValueFactory<>("propertyName"));

        TableColumn<PropertyRecord, String> tenantCol = new TableColumn<>("Tenant Name");
        tenantCol.setCellValueFactory(new PropertyValueFactory<>("tenantName"));

        TableColumn<PropertyRecord, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<PropertyRecord, String> paymentCol = new TableColumn<>("Payment");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("payment"));

        TableColumn<PropertyRecord, String> endingDateCol = new TableColumn<>("Ending Date");
        endingDateCol.setCellValueFactory(new PropertyValueFactory<>("endingDate"));

        table.getColumns().addAll(propertyCol, tenantCol, locationCol, paymentCol, endingDateCol);

        ObservableList<PropertyRecord> data = getPropertyRecordsFromDB();
        table.setItems(data);

table.setItems(data);
table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // columns auto resize


      dashboard.setCenter(null);
      dashboard.setLeft(leftDashboard);
      dashboard.setTop(topDashboard);
      Scene DashboardPage = new Scene(dashboard,1000,750);
    
      
      addButton.setOnAction(e -> {
    Stage popupStage = new Stage();
    popupStage.setTitle("Add New Property");

    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20));
    
    Label propertyLabel = new Label("Property Name:");
    Label tenantLabel = new Label("Tenant Name:");
    Label locationLabel = new Label("Location:");
    Label paymentLabel = new Label("Payment:");
    Label rentLabel = new Label("Rent per Month:");
    Label startDateLabel = new Label("Starting Date (YYYY-MM-DD):");
    Label endDateLabel = new Label("Ending Date (YYYY-MM-DD):");
    TextField propertyNameField = new TextField();
    TextField tenantNameField = new TextField();
    TextField locationField = new TextField();
    TextField paymentField = new TextField();
    DatePicker endingDateField = new DatePicker();
    Button saveButton = new Button("Save");
    TextField rentPerMonthField = new TextField();  // <-- fines
    DatePicker startingDateField = new DatePicker();  // <-- starting_date
        
    
    grid.setStyle("-fx-background-color: " + backgroundColor + ";");
    TextField[] fields = {propertyNameField, tenantNameField, locationField, paymentField, rentPerMonthField};
    for (TextField field : fields) {
        field.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
    }
    Label[] labels = {propertyLabel, tenantLabel, locationLabel, paymentLabel, rentLabel, startDateLabel, endDateLabel};
    for (Label label : labels) {
        label.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
    }
        saveButton.setStyle(
        "-fx-background-color: " + buttonColor + "; " +
        "-fx-text-fill: " + WtextColor + "; " +
        "-fx-font-size: " + fontSizeButtons + "; " +
        "-fx-background-radius: 5px;"
    );
    // Add Labels + Fields
    grid.add(propertyLabel, 0, 0);
    grid.add(propertyNameField, 1, 0);
    grid.add(tenantLabel, 0, 1);
    grid.add(tenantNameField, 1, 1);
    grid.add(locationLabel, 0, 2);
    grid.add(locationField, 1, 2);
    grid.add(paymentLabel, 0, 3);
    grid.add(paymentField, 1, 3);
    grid.add(rentLabel, 0, 4);
    grid.add(rentPerMonthField, 1, 4);
    grid.add(startDateLabel, 0, 5);
    grid.add(startingDateField, 1, 5);
    grid.add(endDateLabel, 0, 6);
    grid.add(endingDateField, 1, 6);

    // Save Button

    grid.add(saveButton, 1, 7);

    // Action for Save Button
    saveButton.setOnAction(ev -> {
        String propertyName = propertyNameField.getText();
        String tenantName = tenantNameField.getText();
        String location = locationField.getText();
        String payment = paymentField.getText();

        String endingDate = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localEndingDate = endingDateField.getValue();
        if (localEndingDate != null) {
            endingDate = localEndingDate.format(formatter);
        }

        String startingDate = "";
        LocalDate localStartingDate = startingDateField.getValue();
        if (localStartingDate != null) {
            startingDate = localStartingDate.format(formatter);
        }

        String rentPerMonth = rentPerMonthField.getText();

        // Check required fields
        if (!propertyName.isEmpty() && !tenantName.isEmpty() && !location.isEmpty() && !payment.isEmpty()
                && !endingDate.isEmpty() && !startingDate.isEmpty() && !rentPerMonth.isEmpty()) {

            try (Connection conn = connectDatabase()) {
                String sql = "INSERT INTO properties (username, property_name, tenant_name, location, payment, ending_date, starting_date, fines) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, currentUsername);
                stmt.setString(2, propertyName);
                stmt.setString(3, tenantName);
                stmt.setString(4, location);
                stmt.setString(5, payment);
                stmt.setString(6, endingDate);
                stmt.setString(7, startingDate);
                stmt.setString(8, rentPerMonth);  // Storing rent per month into fines

                stmt.executeUpdate();
                stmt.close();
                conn.close();

                // Refresh table
                table.setItems(getPropertyRecordsFromDB());

                // Close popup
                popupStage.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error saving property: " + ex.getMessage(), ButtonType.OK);
                errorAlert.showAndWait();
            }
        } else {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields.", ButtonType.OK);
            warningAlert.showAndWait();
        }
    });

    // Setup Scene & Show Popup
    Scene popupScene = new Scene(grid, 400, 450);  // Adjusted height for new fields
    popupStage.setScene(popupScene);
    popupStage.show();
});

          
        
//       =--------------------------------------------------------------- 


    modifyButton.setOnAction(e -> {
    Stage popupStage3 = new Stage();
    popupStage3.setTitle("Modify Property");

    GridPane grid3 = new GridPane();
    grid3.setAlignment(Pos.CENTER);
    grid3.setHgap(10);
    grid3.setVgap(10);
    grid3.setPadding(new Insets(20));

    
    Label propertyLabel = new Label("Property Name (to modify):");
    Label tenantLabel = new Label("Tenant Name (to modify):");
    Label locationLabel = new Label("New Location:");
    Label paymentLabel = new Label("New Payment:");
    Label dateLabel = new Label("New Ending Date (YYYY-MM-DD):");
    TextField propertyNameField = new TextField();
    TextField tenantNameField = new TextField();
    TextField locationField = new TextField();
    TextField paymentField = new TextField();
    DatePicker endingDateField = new DatePicker();
    Button saveChangesButton = new Button("Save Changes");
    
    grid3.setStyle("-fx-background-color: " + backgroundColor + ";");
    propertyLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
    tenantLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
    locationLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
    paymentLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
    dateLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
    propertyNameField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
    tenantNameField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
    locationField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
    paymentField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
    endingDateField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
    saveChangesButton.setStyle(
        "-fx-background-color: " + buttonColor + "; " +
        "-fx-text-fill: " + WtextColor + "; " +
        "-fx-font-size: " + fontSizeButtons + "; " +
        "-fx-background-radius: 5px;"
    );
    
    grid3.add(propertyLabel, 0, 0);
    grid3.add(propertyNameField, 1, 0);
    grid3.add(tenantLabel, 0, 1);
    grid3.add(tenantNameField, 1, 1);
    grid3.add(locationLabel, 0, 2);
    grid3.add(locationField, 1, 2);
    grid3.add(paymentLabel, 0, 3);
    grid3.add(paymentField, 1, 3);
    grid3.add(dateLabel, 0, 4);
    grid3.add(endingDateField, 1, 4);
    grid3.add(saveChangesButton, 1, 5);

    saveChangesButton.setOnAction(ev -> {
        String propertyName = propertyNameField.getText();
        String tenantName = tenantNameField.getText();
        String newLocation = locationField.getText();
        String newPayment = paymentField.getText();
    String newendingDate = "";
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    LocalDate localDate1 = endingDateField.getValue();
    if (localDate1 != null) {
    newendingDate = localDate1.format(formatter2);
    }
    else
    {
    newendingDate = "";
    }
        if (!propertyName.isEmpty() && !tenantName.isEmpty() &&
            !newLocation.isEmpty() && !newPayment.isEmpty() && !newendingDate.isEmpty()) {
            try (Connection conn = connectDatabase()) {
                String sql = "UPDATE properties SET location = ?, payment = ?, ending_date = ? " +
                             "WHERE username = ? AND property_name = ? AND tenant_name = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);

                stmt.setString(1, newLocation);
                stmt.setString(2, newPayment);
                stmt.setString(3, newendingDate);
                stmt.setString(4, currentUsername);
                stmt.setString(5, propertyName);
                stmt.setString(6, tenantName);

                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) 
                {
                    System.out.println("Property updated successfully.");
                } 
                else 
                {
                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION, "No matching property found to update.", ButtonType.OK);
                    infoAlert.showAndWait();
                }
                stmt.close();
                conn.close();
                
                table.setItems(getPropertyRecordsFromDB());
                popupStage3.close();

            } 
            catch (Exception ex) 
            {
                ex.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error modifying property: " + ex.getMessage(), ButtonType.OK);
                errorAlert.showAndWait();
            }
        } 
        else 
        {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields.", ButtonType.OK);
            warningAlert.showAndWait();
        }
    }
    );

    
    
    Scene popupScene3 = new Scene(grid3, 450, 400);
    popupStage3.setScene(popupScene3);
    popupStage3.show();
});
    

        
    removeButton.setOnAction(e -> 
    {
    Stage popupStage2 = new Stage();
    popupStage2.setTitle("Remove New Property");


    GridPane grid2 = new GridPane();
    grid2.setAlignment(Pos.CENTER);
    grid2.setHgap(10);
    grid2.setVgap(10);
    grid2.setPadding(new Insets(20));
    
    Label propertyLabel = new Label("Property Name:");
    Label tenantLabel = new Label("Tenant Name:");
    TextField propertyNameField = new TextField();
    TextField tenantNameField = new TextField();
    Button deleteButton = new Button("Delete");
    
    grid2.setStyle("-fx-background-color: " + backgroundColor + ";");
    propertyLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
    tenantLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
    propertyNameField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
    tenantNameField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
    deleteButton.setStyle(
        "-fx-background-color: " + buttonColor + "; " +
        "-fx-text-fill: " + WtextColor + "; " +
        "-fx-font-size: " + fontSizeButtons + "; " +
        "-fx-background-radius: 5px;"
    );
    
    
    grid2.add(propertyLabel, 0, 0);
    grid2.add(propertyNameField, 1, 0);
    grid2.add(tenantLabel, 0, 1);
    grid2.add(tenantNameField, 1, 1);
    grid2.add(deleteButton, 1, 5);

    deleteButton.setOnAction(ev -> 
    {
        
    String propertyName = propertyNameField.getText();
    String tenantName = tenantNameField.getText();
    
    if(!propertyNameField.getText().isEmpty()&!tenantNameField.getText().isEmpty())
    {

    try (Connection conn = connectDatabase()) {
    String sql = "DELETE FROM properties WHERE username = ? AND property_name = ? AND tenant_name = ?";
   
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setString(1, currentUsername);
    stmt.setString(2, propertyName);
    stmt.setString(3, tenantName);
    stmt.executeUpdate();
    stmt.close();
    conn.close();

    table.setItems(getPropertyRecordsFromDB());
  
    popupStage2.close();

    } 
    catch (Exception ex) 
    {
        ex.printStackTrace();
        Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error Deleting property: " + ex.getMessage(), ButtonType.OK);
        errorAlert.showAndWait();
    }
}
    });

    Scene popupScene2 = new Scene(grid2, 400, 300);
    popupStage2.setScene(popupScene2);
    popupStage2.show();
});
    
    
    
    
        
    logOutButton.setOnAction(e -> 
    {
    currentUsername = null;
    usernameTxt.setText("");
    passwordTxt.setText("");
    table.getItems().clear();
    primaryStage.setScene(LoginPage);
    dashboard.setCenter(null);
    topDashboard.getChildren().clear();
    topDashboard.getChildren().add(logOutButton);
    dashboard.setTop(topDashboard);
    dashboard.setLeft(leftDashboard);
});
 
    
TableView<PaymentHistoryRecord> paymentsTable = new TableView<>();

TableColumn<PaymentHistoryRecord, String> propertyColPayments = new TableColumn<>("Property Name");
propertyColPayments.setCellValueFactory(new PropertyValueFactory<>("propertyName"));

TableColumn<PaymentHistoryRecord, String> tenantColPayments = new TableColumn<>("Tenant Name");
tenantColPayments.setCellValueFactory(new PropertyValueFactory<>("tenantName"));

TableColumn<PaymentHistoryRecord, String> paymentAmountColPayments = new TableColumn<>("Payment Amount");
paymentAmountColPayments.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));

TableColumn<PaymentHistoryRecord, String> paymentDateColPayments = new TableColumn<>("Payment Date");
paymentDateColPayments.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

paymentsTable.getColumns().addAll(propertyColPayments, tenantColPayments, paymentAmountColPayments, paymentDateColPayments);
paymentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    
buttonPayments.setOnAction(e -> {
    topDashboard.getChildren().clear();

    Button addPaymentButton = new Button("Add Payment");
    addPaymentButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: "+ WtextColor +";");

    topDashboard.getChildren().addAll(addPaymentButton, logOutButton);

    paymentsTable.setItems(getPaymentHistoryRecordsFromDB());
    dashboard.setCenter(paymentsTable);

    addPaymentButton.setOnAction(ev -> {
        Stage popupStage = new Stage();
        popupStage.setTitle("Add Payment to Property");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: " + backgroundColor + ";");

        
        Label propertyLabel = new Label("Property Name:");
        Label tenantLabel = new Label("Tenant Name:");
        Label paymentLabel = new Label("Payment Amount to Add:");
        TextField propertyNameField = new TextField();
        TextField tenantNameField = new TextField();
        TextField paymentAmountField = new TextField();
        Button saveButton = new Button("Save Payment");
       
        
        propertyLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
        tenantLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
        paymentLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
        propertyNameField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
        tenantNameField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
        paymentAmountField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
        saveButton.setStyle(
        "-fx-background-color: " + buttonColor + "; " +
        "-fx-text-fill: " + WtextColor + "; " +
        "-fx-font-size: " + fontSizeButtons + "; " +
        "-fx-background-radius: 5px;"
        );
        
        grid.add(propertyLabel, 0, 0);
        grid.add(propertyNameField, 1, 0);
        grid.add(tenantLabel, 0, 1);
        grid.add(tenantNameField, 1, 1);
        grid.add(paymentLabel, 0, 2);
        grid.add(paymentAmountField, 1, 2);
        grid.add(saveButton, 1, 3);

          saveButton.setOnAction(saveEv -> 
          {
            String propertyName = propertyNameField.getText();
            String tenantName = tenantNameField.getText();
            String paymentToAddStr = paymentAmountField.getText();

            if (!propertyName.isEmpty() && !tenantName.isEmpty() && !paymentToAddStr.isEmpty()) 
            {
                try (Connection conn = connectDatabase()) 
                {
                    conn.setAutoCommit(false); 
                    String selectSql = "SELECT payment FROM properties WHERE username = ? AND property_name = ? AND tenant_name = ?";
                   
                    PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                    selectStmt.setString(1, currentUsername);
                    selectStmt.setString(2, propertyName);
                    selectStmt.setString(3, tenantName);

                    ResultSet rs = selectStmt.executeQuery();
                    if (rs.next()) {
                        String currentPaymentStr = rs.getString("payment");
                        double currentPayment = 0.0;
                        
                        try 
                        {
                            currentPayment = Double.parseDouble(currentPaymentStr);
                        } 
                        catch (NumberFormatException ex) 
                        {
                            currentPayment = 0.0;
                        }

                        double paymentToAdd = Double.parseDouble(paymentToAddStr);
                        double newPayment = currentPayment + paymentToAdd;

                        String updateSql = "UPDATE properties SET payment = ? WHERE username = ? AND property_name = ? AND tenant_name = ?";
                        
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setString(1, String.valueOf(newPayment));
                        updateStmt.setString(2, currentUsername);
                        updateStmt.setString(3, propertyName);
                        updateStmt.setString(4, tenantName);
                        updateStmt.executeUpdate();

                        String insertHistorySql = "INSERT INTO payment_history (username, property_name, tenant_name, payment_amount, payment_date) VALUES (?, ?, ?, ?, ?)";
                        
                        PreparedStatement insertHistoryStmt = conn.prepareStatement(insertHistorySql);
                        insertHistoryStmt.setString(1, currentUsername);
                        insertHistoryStmt.setString(2, propertyName);
                        insertHistoryStmt.setString(3, tenantName);
                        insertHistoryStmt.setDouble(4, paymentToAdd);

                        String paymentDate = java.time.LocalDateTime.now().toString();
                        insertHistoryStmt.setString(5, paymentDate);
                        insertHistoryStmt.executeUpdate();
                        insertHistoryStmt.close();
                        updateStmt.close();
                        selectStmt.close();

                        conn.commit(); 
                        conn.setAutoCommit(true);

                        paymentsTable.setItems(getPaymentHistoryRecordsFromDB()); 
                        table.setItems(getPropertyRecordsFromDB()); 
                        popupStage.close();
                    } 
                    else 
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "No matching property found!", ButtonType.OK);
                        alert.showAndWait();
                    }

                } 
                catch (Exception ex) 
                {
                    ex.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error saving payment: " + ex.getMessage(), ButtonType.OK);
                    errorAlert.showAndWait();
                }
            } 
            else 
            {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields.", ButtonType.OK);
                warningAlert.showAndWait();
            }
        });

        Scene popupScene = new Scene(grid, 400, 300);
        popupStage.setScene(popupScene);
        popupStage.show();
    });
});

//Tenants Scene tab


     
buttonTenants.setOnAction(e -> {
    topDashboard.getChildren().clear();
    topDashboard.getChildren().add(logOutButton);

    TableView<PropertyRecord> tenantsTable = new TableView<>();
    
    TableColumn<PropertyRecord, String> tenantsPropertyCol = new TableColumn<>("Property Name");
    tenantsPropertyCol.setCellValueFactory(new PropertyValueFactory<>("propertyName"));
    TableColumn<PropertyRecord, String> tenantsTenantCol = new TableColumn<>("Tenant Name");
    tenantsTenantCol.setCellValueFactory(new PropertyValueFactory<>("tenantName"));
    TableColumn<PropertyRecord, String> tenantsStartingDateCol = new TableColumn<>("Starting Date");
    tenantsStartingDateCol.setCellValueFactory(new PropertyValueFactory<>("startingDate"));
    TableColumn<PropertyRecord, String> tenantsEndingDateCol = new TableColumn<>("Ending Date");
    tenantsEndingDateCol.setCellValueFactory(new PropertyValueFactory<>("endingDate"));
    TableColumn<PropertyRecord, String> tenantsFinesCol = new TableColumn<>("Rent Per-month");
    tenantsFinesCol.setCellValueFactory(new PropertyValueFactory<>("fines"));
    tenantsTable.getColumns().addAll(tenantsPropertyCol, tenantsTenantCol, tenantsStartingDateCol, tenantsEndingDateCol, tenantsFinesCol);
    tenantsTable.setItems(getPropertyRecordsFromDB());
    tenantsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    dashboard.setCenter(tenantsTable);

    tenantsTable.setRowFactory(tv -> 
    {
        TableRow<PropertyRecord> row = new TableRow<>();
        row.setOnMouseClicked(event -> 
        {
            if (!row.isEmpty() && event.getClickCount() == 2) 
            {
                PropertyRecord clickedRecord = row.getItem();
            }
        });
        return row;
    });
});
        Button dashboardButtonProperties = new Button("Properties") ;
        Button dashboardButtonMaintenance = new Button("Maintenance\n History") ;
        Button dashboardButtonPayments = new Button("Payments History") ;
        Button dashboardButtonTenants = new Button("Tenants / Contracts") ;
        Button dashboardButtonOptions = new Button("Options") ;

        dashboardButtonProperties.setOnAction(e -> 
        {
        primaryStage.setScene(DashboardPage);
        topDashboard.getChildren().clear();
        topDashboard.getChildren().addAll(addButton, modifyButton, removeButton,logOutButton);
        dashboard.setCenter(table);
        dashboard.setLeft(leftDashboard);
        dashboard.setTop(topDashboard);
        });
        dashboardButtonMaintenance.setOnAction(e -> 
        {
        primaryStage.setScene(DashboardPage);
        topDashboard.getChildren().clear();
        topDashboard.getChildren().addAll(addButton, modifyButton, removeButton,logOutButton);
        maintenanceTable.setItems(getMaintenanceRecordsFromDB());
        dashboard.setCenter(maintenanceTable); 
        dashboard.setLeft(leftDashboard);
        dashboard.setTop(topDashboard);
        });
        dashboardButtonPayments.setOnAction(e -> 
        {
          topDashboard.getChildren().clear();

    Button addPaymentButton = new Button("Add Payment");
    addPaymentButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: "+ WtextColor +";");

    topDashboard.getChildren().addAll(addPaymentButton, logOutButton);

    paymentsTable.setItems(getPaymentHistoryRecordsFromDB());
    dashboard.setCenter(paymentsTable);

    addPaymentButton.setOnAction(ev -> {
        Stage popupStage = new Stage();
        popupStage.setTitle("Add Payment to Property");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: " + backgroundColor + ";");

        
        Label propertyLabel = new Label("Property Name:");
        Label tenantLabel = new Label("Tenant Name:");
        Label paymentLabel = new Label("Payment Amount to Add:");
        TextField propertyNameField = new TextField();
        TextField tenantNameField = new TextField();
        TextField paymentAmountField = new TextField();
        Button saveButton = new Button("Save Payment");
       
        
        propertyLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
        tenantLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
        paymentLabel.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px;");
        propertyNameField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
        tenantNameField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
        paymentAmountField.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + ";");
        saveButton.setStyle(
        "-fx-background-color: " + buttonColor + "; " +
        "-fx-text-fill: " + WtextColor + "; " +
        "-fx-font-size: " + fontSizeButtons + "; " +
        "-fx-background-radius: 5px;"
        );
        
        grid.add(propertyLabel, 0, 0);
        grid.add(propertyNameField, 1, 0);
        grid.add(tenantLabel, 0, 1);
        grid.add(tenantNameField, 1, 1);
        grid.add(paymentLabel, 0, 2);
        grid.add(paymentAmountField, 1, 2);
        grid.add(saveButton, 1, 3);

          saveButton.setOnAction(saveEv -> 
          {
            String propertyName = propertyNameField.getText();
            String tenantName = tenantNameField.getText();
            String paymentToAddStr = paymentAmountField.getText();

            if (!propertyName.isEmpty() && !tenantName.isEmpty() && !paymentToAddStr.isEmpty()) 
            {
                try (Connection conn = connectDatabase()) 
                {
                    conn.setAutoCommit(false); 
                    String selectSql = "SELECT payment FROM properties WHERE username = ? AND property_name = ? AND tenant_name = ?";
                   
                    PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                    selectStmt.setString(1, currentUsername);
                    selectStmt.setString(2, propertyName);
                    selectStmt.setString(3, tenantName);

                    ResultSet rs = selectStmt.executeQuery();
                    if (rs.next()) {
                        String currentPaymentStr = rs.getString("payment");
                        double currentPayment = 0.0;
                        
                        try 
                        {
                            currentPayment = Double.parseDouble(currentPaymentStr);
                        } 
                        catch (NumberFormatException ex) 
                        {
                            currentPayment = 0.0;
                        }

                        double paymentToAdd = Double.parseDouble(paymentToAddStr);
                        double newPayment = currentPayment + paymentToAdd;

                        String updateSql = "UPDATE properties SET payment = ? WHERE username = ? AND property_name = ? AND tenant_name = ?";
                        
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setString(1, String.valueOf(newPayment));
                        updateStmt.setString(2, currentUsername);
                        updateStmt.setString(3, propertyName);
                        updateStmt.setString(4, tenantName);
                        updateStmt.executeUpdate();

                        String insertHistorySql = "INSERT INTO payment_history (username, property_name, tenant_name, payment_amount, payment_date) VALUES (?, ?, ?, ?, ?)";
                        
                        PreparedStatement insertHistoryStmt = conn.prepareStatement(insertHistorySql);
                        insertHistoryStmt.setString(1, currentUsername);
                        insertHistoryStmt.setString(2, propertyName);
                        insertHistoryStmt.setString(3, tenantName);
                        insertHistoryStmt.setDouble(4, paymentToAdd);

                        String paymentDate = java.time.LocalDateTime.now().toString();
                        insertHistoryStmt.setString(5, paymentDate);
                        insertHistoryStmt.executeUpdate();
                        insertHistoryStmt.close();
                        updateStmt.close();
                        selectStmt.close();

                        conn.commit(); 
                        conn.setAutoCommit(true);

                        paymentsTable.setItems(getPaymentHistoryRecordsFromDB()); 
                        table.setItems(getPropertyRecordsFromDB()); 
                        popupStage.close();
                    } 
                    else 
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "No matching property found!", ButtonType.OK);
                        alert.showAndWait();
                    }

                } 
                catch (Exception ex) 
                {
                    ex.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error saving payment: " + ex.getMessage(), ButtonType.OK);
                    errorAlert.showAndWait();
                }
            } 
            else 
            {
                Alert warningAlert = new Alert(Alert.AlertType.WARNING, "Please fill in all fields.", ButtonType.OK);
                warningAlert.showAndWait();
            }
        });

        Scene popupScene = new Scene(grid, 400, 300);
        popupStage.setScene(popupScene);
        popupStage.show();
    });   
        primaryStage.setScene(DashboardPage);
        topDashboard.getChildren().clear();
        topDashboard.getChildren().addAll(addPaymentButton, logOutButton);
        dashboard.setCenter(paymentsTable);
        dashboard.setLeft(leftDashboard);
        dashboard.setTop(topDashboard);
        });
    dashboardButtonTenants.setOnAction(e -> {
    topDashboard.getChildren().clear();
    topDashboard.getChildren().add(logOutButton);

    TableView<PropertyRecord> tenantsTable = new TableView<>();
    
    TableColumn<PropertyRecord, String> tenantsPropertyCol = new TableColumn<>("Property Name");
    tenantsPropertyCol.setCellValueFactory(new PropertyValueFactory<>("propertyName"));
    TableColumn<PropertyRecord, String> tenantsTenantCol = new TableColumn<>("Tenant Name");
    tenantsTenantCol.setCellValueFactory(new PropertyValueFactory<>("tenantName"));
    TableColumn<PropertyRecord, String> tenantsStartingDateCol = new TableColumn<>("Starting Date");
    tenantsStartingDateCol.setCellValueFactory(new PropertyValueFactory<>("startingDate"));
    TableColumn<PropertyRecord, String> tenantsEndingDateCol = new TableColumn<>("Ending Date");
    tenantsEndingDateCol.setCellValueFactory(new PropertyValueFactory<>("endingDate"));
    TableColumn<PropertyRecord, String> tenantsFinesCol = new TableColumn<>("Rent Per-month");
    tenantsFinesCol.setCellValueFactory(new PropertyValueFactory<>("fines"));
    tenantsTable.getColumns().addAll(tenantsPropertyCol, tenantsTenantCol, tenantsStartingDateCol, tenantsEndingDateCol, tenantsFinesCol);
    tenantsTable.setItems(getPropertyRecordsFromDB());
    tenantsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    dashboard.setCenter(tenantsTable);

    tenantsTable.setRowFactory(tv -> 
    {
        TableRow<PropertyRecord> row = new TableRow<>();
        row.setOnMouseClicked(event -> 
        {
            if (!row.isEmpty() && event.getClickCount() == 2) 
            {
                PropertyRecord clickedRecord = row.getItem();
            }
        });
        return row;
    });
         primaryStage.setScene(DashboardPage);
        topDashboard.getChildren().clear();
        topDashboard.getChildren().add(logOutButton);
        dashboard.setCenter(tenantsTable);
        dashboard.setLeft(leftDashboard);
        dashboard.setTop(topDashboard);
});
//        dashboardButtonTenants.setOnAction(e -> 
//        {
//   
//        });
        dashboardButtonOptions.setOnAction(e -> 
        {
        primaryStage.setScene(DashboardPage);
        topDashboard.getChildren().clear();
        topDashboard.getChildren().addAll(addButton, modifyButton, removeButton,logOutButton);
        maintenanceTable.setItems(getMaintenanceRecordsFromDB());
        dashboard.setCenter(maintenanceTable); 
        dashboard.setLeft(leftDashboard);
        dashboard.setTop(topDashboard);
        });
        Button btnArray[] =
        {
         dashboardButtonProperties,
         dashboardButtonMaintenance,
         dashboardButtonPayments,
         dashboardButtonTenants,
         dashboardButtonOptions
        };
        for(int i = 0 ; i <btnArray.length ; i++)
        {
            Button btn = btnArray[i];
        btn.setOnMouseEntered(e -> btn.setCursor(Cursor.HAND));
        btn.setOnMouseExited(e -> btn.setCursor(Cursor.DEFAULT));
        btn.setPrefSize(350, 350);
        btn.setStyle(
        "-fx-background-color:" + buttonColor + ";" +
        "-fx-background-radius: 15px;" +   // << round background
        "-fx-border-radius: 15px;" +       // << round border (optional if you have border)
        "-fx-border-color: transparent;" +
        "-fx-text-fill: " + WtextColor + ";" +
        "-fx-font-size: 28px; " +
        "-fx-font-weight: bold;" +
        "-fx-text-alignment: center;" 
        );
        }
        
        
       HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(20));
        topBar.setStyle("-fx-background-color:"+buttonColor+";");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Image logoImage = new Image("file:src/resources/logo.png");
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitHeight(80);
        logoView.setPreserveRatio(true);
        
        VBox appText = new VBox(-5);
        Label title = new Label("AI E-STATE");
        Label subtitle = new Label("E-State Management");
        subtitle.setStyle( "-fx-text-fill: "+ WtextColor + "; -fx-font-size: 28px;");
            title.setStyle(
                "-fx-background-color:" + buttonColor + ";"+
                "-fx-text-fill: "+ WtextColor + ";" +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold;"
            );   
            
        subtitle.setFont(Font.font("Arial", 14));
        Label rightDescription = new Label("Welcome "+currentUsername+"\n Manage your e-state easier with AI-STATE");
        rightDescription.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px; -fx-text-alignment : center;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        appText.getChildren().addAll(title, subtitle);
        topBar.getChildren().addAll(logoView, appText,spacer,rightDescription);

        VBox mainButtonsVBox = new VBox(20);
        mainButtonsVBox.setPadding(new Insets(40));
        mainButtonsVBox.setAlignment(Pos.CENTER);
        
        HBox firstRow = new HBox(20);
        firstRow.setAlignment(Pos.CENTER);
        firstRow.getChildren().addAll(dashboardButtonProperties, dashboardButtonMaintenance, dashboardButtonPayments);
        
        HBox secondRow = new HBox(20);
        secondRow.setAlignment(Pos.CENTER);
        secondRow.getChildren().addAll(dashboardButtonTenants, dashboardButtonOptions);

        mainButtonsVBox.getChildren().addAll(firstRow, secondRow);
 
        BorderPane allDashboardBorder = new BorderPane();
        allDashboardBorder.setTop(topBar);
        allDashboardBorder.setStyle("-fx-background-color: "+backgroundColor+";");
        allDashboardBorder.setCenter(mainButtonsVBox);
        Scene allDashboard = new Scene(allDashboardBorder, 979, 645);

        
        loginBtn.setOnAction(e -> {
        
    String username = usernameTxt.getText();
    String password = passwordTxt.getText();
    try (Connection conn = connectDatabase()) 
    {
        String sql = "SELECT username FROM users WHERE username = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            currentUsername = rs.getString("username");
            System.out.println("Login successful. Username: " + currentUsername);
            rightDescription.setText("Welcome "+currentUsername+"\n Manage your e-state easier with AI-STATE");

            table.setItems(getPropertyRecordsFromDB());

            primaryStage.setScene(allDashboard);
        } 
        else 
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid username or password!", ButtonType.OK);
            alert.showAndWait();
        }

    } 
    catch (SQLException ex) 
    {
        ex.printStackTrace();
    }
});


         HBox topBar3 = new HBox(10);
        topBar3.setPadding(new Insets(20));
        topBar3.setStyle("-fx-background-color:"+buttonColor+";");
        topBar3.setAlignment(Pos.CENTER_LEFT);

        Image logoImage3 = new Image("file:src/resources/logo.png");
        ImageView logoView3 = new ImageView(logoImage);
        logoView.setFitHeight(80);
        logoView.setPreserveRatio(true);
        
        VBox appText3 = new VBox(-5);
        Label title3 = new Label("AI E-STATE");
        Label subtitle3 = new Label("E-State Management");
        subtitle.setStyle( "-fx-text-fill: "+ WtextColor + "; -fx-font-size: 28px;");
            title.setStyle(
                "-fx-background-color:" + buttonColor + ";"+
                "-fx-text-fill: "+ WtextColor + ";" +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold;"
            );   
            
        subtitle.setFont(Font.font("Arial", 14));
        Label rightDescription3 = new Label("Welcome "+"ddadw"+"\n Manage your e-state easier with AI-STATE");
        rightDescription.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px; -fx-text-alignment : center;");
        Region spacer3 = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        appText3.getChildren().addAll(title, subtitle);
        topBar3.getChildren().addAll(logoView, appText,spacer,rightDescription);



        VBox themeCustomizer = new VBox(15);
        themeCustomizer.setPadding(new Insets(30));
        themeCustomizer.setAlignment(Pos.CENTER);

        Label themeLabel = new Label("Choose Theme:");
        themeLabel.setStyle("-fx-text-fill : "+WtextColor+";");
        themeLabel.setFont(Font.font(16));

        Button darkBtn = new Button("Dark");
        Button lightBtn = new Button("Light");

        darkBtn.setPrefWidth(200);
        lightBtn.setPrefWidth(200);

        darkBtn.setStyle("-fx-background-color: #4b4b4b; -fx-text-fill: "+WtextColor+";");
        lightBtn.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: "+backgroundColor+";");

        Label buttonColorLabel = new Label("Color 1:");
        buttonColorLabel.setStyle("-fx-text-fill : "+WtextColor+";");
        buttonColorLabel.setFont(Font.font(16));
        ColorPicker buttonColorPicker = new ColorPicker();

        Label bg1Label = new Label("Color 2:");
        bg1Label.setStyle("-fx-text-fill : "+WtextColor+";");
        bg1Label.setFont(Font.font(16));
        ColorPicker bg1Picker = new ColorPicker();

        Label bg2Label = new Label("Font Color:");
        bg2Label.setStyle("-fx-text-fill : "+WtextColor+";");
        bg2Label.setFont(Font.font(16));
        ColorPicker bg2Picker = new ColorPicker();

        Button saveMovesColors = new Button("Save Changes");
        saveMovesColors.setPrefWidth(100);
        saveMovesColors.setStyle("-fx-background-color: #555555; -fx-text-fill: "+WtextColor+";");
        Button backColors = new Button("Back");
        backColors.setPrefWidth(100);
        backColors.setStyle("-fx-background-color: #555555; -fx-text-fill: "+WtextColor+";");
        HBox backAndSave = new HBox(3);
        backAndSave.getChildren().addAll(saveMovesColors,backColors);
        backAndSave.setAlignment(Pos.CENTER);
        
        themeCustomizer.getChildren().addAll(
                themeLabel, darkBtn, lightBtn,
                buttonColorLabel, buttonColorPicker,
                bg1Label, bg1Picker,
                bg2Label, bg2Picker,
                backAndSave
        );

        darkBtn.setOnMouseEntered(e -> darkBtn.setCursor(Cursor.HAND));
        darkBtn.setOnMouseExited(e -> darkBtn.setCursor(Cursor.DEFAULT));
        lightBtn.setOnMouseEntered(e -> lightBtn.setCursor(Cursor.HAND));
        lightBtn.setOnMouseExited(e -> lightBtn.setCursor(Cursor.DEFAULT));
        saveMovesColors.setOnMouseEntered(e -> saveMovesColors.setCursor(Cursor.HAND));
        saveMovesColors.setOnMouseExited(e -> saveMovesColors.setCursor(Cursor.DEFAULT));
        backColors.setOnMouseEntered(e -> backColors.setCursor(Cursor.HAND));
        backColors.setOnMouseExited(e -> backColors.setCursor(Cursor.DEFAULT));
        
        BorderPane optionBorder = new BorderPane();
        optionBorder.setTop(topBar3);
        optionBorder.setStyle("-fx-background-color: "+backgroundColor+";");
        optionBorder.setCenter(themeCustomizer);
        Scene optionDashboard = new Scene(optionBorder, 979, 645);

        buttonOptions.setOnAction(e ->
        {
        topDashboard.getChildren().clear();
        topDashboard.getChildren().addAll(addButton, modifyButton, removeButton,logOutButton);
        primaryStage.setScene(optionDashboard);
        }
        );
        dashboardButtonOptions.setOnAction(e ->
        {
        topDashboard.getChildren().clear();
        topDashboard.getChildren().addAll(addButton, modifyButton, removeButton,logOutButton);
        primaryStage.setScene(optionDashboard);
        }
        );
         backColors.setOnAction(e ->
        {
        topDashboard.getChildren().clear();
        topDashboard.getChildren().addAll(addButton, modifyButton, removeButton,logOutButton);
        primaryStage.setScene(allDashboard);
        }
        );
        
        darkBtn.setOnAction(e ->
        {
loginRoot.setStyle("-fx-background-color: " + backgroundColor + ";");
LoginRoot.setStyle("-fx-background-color: " + backgroundColor + ";");
//grid4.setStyle("-fx-background-color: " + backgroundColor + ";");
leftDashboard.setStyle("-fx-background-color: " + backgroundColor + ";");
topDashboard.setStyle("-fx-background-color: " + buttonColor + ";");
//grid.setStyle("-fx-background-color: " + backgroundColor + ";");
//grid2.setStyle("-fx-background-color: " + backgroundColor + ";");
//grid3.setStyle("-fx-background-color: " + backgroundColor + ";");
allDashboardBorder.setStyle("-fx-background-color: " + backgroundColor + ";");
topBar.setStyle("-fx-background-color: " + buttonColor + ";");
topBar3.setStyle("-fx-background-color: " + buttonColor + ";");
optionBorder.setStyle("-fx-background-color: " + backgroundColor + ";");
LoginDash.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + "; -fx-font-size: 20px");
loginBtn.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + "; -fx-font-size: " + fontSizeButtons + "; -fx-pref-width: 100;");
RegisterBtn.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + "; -fx-font-size: " + fontSizeButtons + "; -fx-pref-width: 100;");
backLoginBTN.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + "; -fx-font-size: " + fontSizeButtons + "; -fx-pref-width: 100;");
addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: " + WtextColor + ";");
modifyButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: " + WtextColor + ";");
removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: " + WtextColor + ";");
logOutButton.setStyle("-fx-background-color: #cc0000; -fx-text-fill: " + WtextColor + ";");
//saveButton.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + "; -fx-font-size: " + fontSizeButtons + "; -fx-background-radius: 5px;");
//deleteButton.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + "; -fx-font-size: " + fontSizeButtons + "; -fx-background-radius: 5px;");
//saveChangesButton.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: " + WtextColor + "; -fx-font-size: " + fontSizeButtons + "; -fx-background-radius: 5px;");
//addPaymentButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: " + WtextColor + ";");
saveMovesColors.setStyle("-fx-background-color: #555555; -fx-text-fill: " + WtextColor + ";");
backColors.setStyle("-fx-background-color: #555555; -fx-text-fill: " + WtextColor + ";");
darkBtn.setStyle("-fx-background-color: #4b4b4b; -fx-text-fill: " + WtextColor + ";");
lightBtn.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: " + backgroundColor + ";");
rightDescription.setStyle("-fx-text-fill: " + WtextColor + "; -fx-font-size: 14px; -fx-text-alignment: center;");
                bg1Label.setStyle("-fx-text-fill : "+WtextColor+";");
buttonColorLabel.setStyle("-fx-text-fill : "+WtextColor+";");
        bg2Label.setStyle("-fx-text-fill : "+WtextColor+";");
        }
        );
        lightBtn.setOnAction(e ->
        {loginRoot.setStyle("-fx-background-color: #e1e4e8;");
LoginRoot.setStyle("-fx-background-color: #e1e4e8;");
leftDashboard.setStyle("-fx-background-color: #e1e4e8;");
topDashboard.setStyle("-fx-background-color: #c0c4c9;");
allDashboardBorder.setStyle("-fx-background-color: #e1e4e8;");
topBar.setStyle("-fx-background-color: #c0c4c9;");
topBar3.setStyle("-fx-background-color: #c0c4c9;");
optionBorder.setStyle("-fx-background-color: #e1e4e8;");
LoginDash.setStyle("-fx-background-color: #c0c4c9; -fx-text-fill: #2c2f33; -fx-font-size: 20px;");
loginBtn.setStyle("-fx-background-color: #c0c4c9; -fx-text-fill: #2c2f33; -fx-font-size: 16px; -fx-pref-width: 100;");
RegisterBtn.setStyle("-fx-background-color: #c0c4c9; -fx-text-fill: #2c2f33; -fx-font-size: 16px; -fx-pref-width: 100;");
backLoginBTN.setStyle("-fx-background-color: #c0c4c9; -fx-text-fill: #2c2f33; -fx-font-size: 16px; -fx-pref-width: 100;");
addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: #2c2f33;");
modifyButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: #2c2f33;");
removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: #2c2f33;");
logOutButton.setStyle("-fx-background-color: #cc0000; -fx-text-fill: #2c2f33;");
saveMovesColors.setStyle("-fx-background-color: #555555; -fx-text-fill: #2c2f33;");
backColors.setStyle("-fx-background-color: #555555; -fx-text-fill: #2c2f33;");
darkBtn.setStyle("-fx-background-color: #4b4b4b; -fx-text-fill: #2c2f33;");
lightBtn.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: #e1e4e8;");
rightDescription.setStyle("-fx-text-fill: #2c2f33; -fx-font-size: 14px; -fx-text-alignment: center;");
bg1Label.setStyle("-fx-text-fill : #2c2f33;");
buttonColorLabel.setStyle("-fx-text-fill : #2c2f33;");
bg2Label.setStyle("-fx-text-fill : #2c2f33;");       
        }
        );
        
        saveMovesColors.setOnAction(e -> {
    // Get the selected colors from the ColorPickers
    Color buttonColorSelected = buttonColorPicker.getValue();
    Color bg1ColorSelected = bg1Picker.getValue();
    Color fontColorSelected = bg2Picker.getValue();  // Get font color from bg2Picker
    String buttonColorHex = "#" + buttonColorSelected.toString().substring(2, 8);
    String bg1ColorHex = "#" + bg1ColorSelected.toString().substring(2, 8);
    String fontColorHex = "#" + fontColorSelected.toString().substring(2, 8);  // Convert font color to hex
    loginRoot.setStyle("-fx-background-color: " + bg1ColorHex + ";");
    LoginRoot.setStyle("-fx-background-color: " + bg1ColorHex + ";");
    leftDashboard.setStyle("-fx-background-color: " + bg1ColorHex + ";");
    topDashboard.setStyle("-fx-background-color: " + buttonColorHex + ";");
    allDashboardBorder.setStyle("-fx-background-color: " + bg1ColorHex + ";");
    topBar.setStyle("-fx-background-color: " + buttonColorHex + ";");
    topBar3.setStyle("-fx-background-color: " + buttonColorHex + ";");
    optionBorder.setStyle("-fx-background-color: " + bg1ColorHex + ";");
    LoginDash.setStyle("-fx-background-color: " + buttonColorHex + "; -fx-text-fill: " + WtextColor + "; -fx-font-size: 20px;");
    loginBtn.setStyle("-fx-background-color: " + buttonColorHex + "; -fx-text-fill: " + WtextColor + "; -fx-font-size: " + fontSizeButtons + "; -fx-pref-width: 100;");
    RegisterBtn.setStyle("-fx-background-color: " + buttonColorHex + "; -fx-text-fill: " + WtextColor + "; -fx-font-size: " + fontSizeButtons + "; -fx-pref-width: 100;");
    backLoginBTN.setStyle("-fx-background-color: " + buttonColorHex + "; -fx-text-fill: " + WtextColor + "; -fx-font-size: " + fontSizeButtons + "; -fx-pref-width: 100;");
    addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: " + WtextColor + ";");
    modifyButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: " + WtextColor + ";");
    removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: " + WtextColor + ";");
    logOutButton.setStyle("-fx-background-color: #cc0000; -fx-text-fill: " + WtextColor + ";");
    saveMovesColors.setStyle("-fx-background-color: #555555; -fx-text-fill: " + WtextColor + ";");
    backColors.setStyle("-fx-background-color: #555555; -fx-text-fill: " + WtextColor + ";");
    darkBtn.setStyle("-fx-background-color: #4b4b4b; -fx-text-fill: " + WtextColor + ";");
    lightBtn.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: " + bg1ColorHex + ";");
    rightDescription.setStyle("-fx-text-fill: " + fontColorHex + "; -fx-font-size: 14px; -fx-text-alignment: center;");
    bg1Label.setStyle("-fx-text-fill : " + fontColorHex + ";");
    buttonColorLabel.setStyle("-fx-text-fill : " + fontColorHex + ";");

        });
      primaryStage.setTitle("AOBA E-State");
      primaryStage.setScene(mainPage);
      primaryStage.setWidth(979.0);
      primaryStage.setHeight(645.0);
      primaryStage.show();
    

    
      LoginDash.setOnAction(e ->
      {
      primaryStage.setScene(LoginPage);
      });
      backLoginBTN.setOnAction(e ->
      {
      primaryStage.setScene(mainPage);
      });
    }
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
