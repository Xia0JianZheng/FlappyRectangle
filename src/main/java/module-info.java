module com.example.flappyrectangle {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.flappyrectangle to javafx.fxml;
    exports com.example.flappyrectangle;
}