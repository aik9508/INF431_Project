package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class controller {
	@FXML
	private HBox namebox;
	@FXML
	private Button submitname;
	@FXML
	private TextField inputname;
	@FXML
	private Text incorrect;

	@FXML
	protected void hideGoButton(ActionEvent event) {
		String name = inputname.getText();
		Text showname = new Text(name);
		showname.setFont(Font.font(24));
		// Here I will set the style of #showname.
		namebox.getChildren().remove(inputname);
		namebox.getChildren().remove(submitname);
		namebox.getChildren().add(showname);
		String oldIncorrect = incorrect.getText();
		// incorrect.setText(oldIncorrect + " YES");
	}

}
