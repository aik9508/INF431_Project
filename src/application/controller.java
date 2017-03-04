package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import util.Analyzer;
import util.Display;

public class controller {
	@FXML
	private HBox namebox;
	@FXML
	private Button submitname;
	@FXML
	private TextField inputname;
	@FXML
	private TextArea incorrect;
	@FXML
	private Text timeLeft;
	@FXML
	private TextArea typingArea;
	@FXML
	private Text score;
	@FXML
	private Text synonym;

	private StringBuffer buf;

	private Timeline timeCounter;
	private int time_Left = 300;
	private final static int NEWGAME = -1, PAUSE = 0, ONGAME = 1, STOPPED = 2;
	private byte status = NEWGAME;

	/*
	 * Analyzer and Display
	 */
	private Analyzer analyzer;
	private Display display;

	@FXML
	public void initialize() {
		buf = new StringBuffer();
		analyzer = new Analyzer();
		display = new Display(score,synonym);
		analyzer.connectTo(display);
		typingArea.setEditable(false);
		incorrect.setEditable(false);
		myEvents();
	}

	private void myEvents() {
		typingArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.BACK_SPACE) {
					if (buf.length() > 0) {
						analyzer.deleteCharacter();
						buf.deleteCharAt(buf.length() - 1);
					} else {
						event.consume();
					}
				}
			}
		});

		typingArea.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (status != ONGAME || event.getCharacter().equals("")) {
					event.consume();
				} else {
					char c = event.getCharacter().charAt(0);
					try {
						analyzer.putCharacter(c);
						if (Character.isAlphabetic(c) || Character.isDigit(c)) {
							buf.append(c);
						} else {
							buf.setLength(0);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@FXML
	protected void hideGoButton(ActionEvent event) {
		String name = inputname.getText();
		Text showname = new Text(name);
		showname.setFont(Font.font(24));
		// Here I will set the style of #showname.
		namebox.getChildren().remove(inputname);
		namebox.getChildren().remove(submitname);
		namebox.getChildren().add(showname);
		// incorrect.setText(oldIncorrect + " YES");

		if (time_Left > 0 && status <= 0) {
			if (status == NEWGAME) {
				new Thread(analyzer).start();
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
							try {
								String word = display.incorrectWordsList.take();
								System.out.println(word);
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										incorrect.appendText("\n" + word);
									}
								});
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}).start();

			}
			typingArea.setEditable(true);
			status = ONGAME;
		}

		timeCounter = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (time_Left-- > 0) {
					int minute = time_Left / 60;
					int second = time_Left % 60;
					timeLeft.setText(String.format("%02d", minute) + " : " + String.format("%02d", second));
				} else {
					typingArea.setEditable(false);
					timeCounter.stop();
					status = STOPPED;
				}
			}

		}));
		timeCounter.setCycleCount(Timeline.INDEFINITE);
		timeCounter.play();
	}

}
