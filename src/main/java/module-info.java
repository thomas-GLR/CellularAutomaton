module org.example.cellularautomaton {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.cellularautomaton to javafx.fxml;
    exports org.example.cellularautomaton;
}