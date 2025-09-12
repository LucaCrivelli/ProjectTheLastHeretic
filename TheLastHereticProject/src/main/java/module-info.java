module com.example.thelasthereticproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires com.almasb.fxgl.all;

    opens com.example.thelasthereticproject to javafx.fxml;
    exports com.example.thelasthereticproject;
}