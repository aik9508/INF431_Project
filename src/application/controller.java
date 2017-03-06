package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
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
	private MenuItem gamerestart;
	@FXML
	private MenuItem gameclose;
	@FXML
	private MenuItem gameclean;
	@FXML
	private MenuItem helprules;
	@FXML
	private MenuItem helpabout;
	@FXML
	private HBox namebox;
	@FXML
	private Button submitname, startandstop, reset;
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
	private Text showname = new Text("anonym");
	private int time_Left = 300;
	private final static int NEWGAME = -1, ONGAME = 1, STOPPED = 2;
	private byte status = NEWGAME;

	/*
	 * Analyzer and Display
	 */
	private Analyzer analyzer;
	private Display display;
	Thread trd_analyser;

	Thread trd_diaplay;

	@FXML
	public void initialize() {
		buf = new StringBuffer();
		analyzer = new Analyzer();
		display = new Display(score, synonym);
		analyzer.connectTo(display);
		typingArea.setEditable(false);
		incorrect.setEditable(false);
		myEvents();
	}

	public void reset() {
		status = NEWGAME;
		if (namebox.getChildren().contains(showname)) {
			namebox.getChildren().remove(showname);
			namebox.getChildren().add(inputname);
			inputname.requestFocus();
			namebox.getChildren().add(submitname);
		}
		score.setText("0");
		startandstop.setDisable(false);
		reset.setDisable(true);
		time_Left = 300;
		buf = new StringBuffer();
		analyzer = new Analyzer();
		display = new Display(score, synonym);
		analyzer.connectTo(display);
		timeLeft.setText("05:00");
		typingArea.setText("");
		incorrect.setText("");
		synonym.setText("");
		typingArea.setEditable(false);
		incorrect.setEditable(false);
		createThreads();
	}

	private void createThreads() {
		trd_analyser = new Thread(analyzer);

		trd_diaplay = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						String word = display.incorrectWordsList.take();
						if (word.equals("eric.goubault"))
							break;
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
		});
	}

	private void myEvents() {

		createThreads();

		showname.setFont(Font.font(24));

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

		reset.setOnAction(new EventHandler<ActionEvent>() {
			// We need to do: update the best and
			// register it in the txt file.
			// score
			@Override
			public void handle(ActionEvent event) {

				reset();
			}
		});

		gameclose.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});

		typingArea.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (status != ONGAME || event.getCharacter().equals("") || event.getCharacter().charAt(0) == 0x08) {
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

		submitname.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String playerName = inputname.getText();
				showname.setText(playerName);
				namebox.getChildren().remove(inputname);
				namebox.getChildren().remove(submitname);
				namebox.getChildren().add(showname);
				startandstop.requestFocus();
			}
		});

		startandstop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				startandstop.setText("Stop");
				typingArea.requestFocus();

				if (time_Left > 0 && status <= 0) {
					if (status == NEWGAME) {
						trd_analyser.start();
						trd_diaplay.start();
					}
					typingArea.setEditable(true);
					status = ONGAME;
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

				else if (status == ONGAME) {
					try {
						analyzer.kill();
						display.kill();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					typingArea.setEditable(false);
					status = STOPPED;
					timeCounter.stop();
					startandstop.setDisable(true);
					startandstop.setText("GO");
					reset.setDisable(false);
					reset.requestFocus();
					System.out.println(trd_analyser.isAlive());
				}
			}
		});

	}

}
