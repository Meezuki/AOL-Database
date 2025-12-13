module AOL_Database {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	
	opens application to javafx.graphics, javafx.fxml;
	
	opens model to javafx.base;
}
