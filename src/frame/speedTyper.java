package frame;
import java.awt.*;
import java.awt.event.*;

import javax.swing.Timer;

import util.Analyzer;
import util.Display;
import util.DisplayScore;
import util.DisplayWords;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

public class speedTyper extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel = new JPanel();
	private JPanel info = new JPanel();
	private JTextArea typingArea = new JTextArea();
	private JScrollPane typingArea_scroll = new JScrollPane(typingArea,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	private JLabel bestScore, name, score, timeLeft;
	private JLabel bestScoreText = new JLabel(" Best score is ");
	private JLabel nameText = new JLabel(" Your name ");
	private JLabel scoreText = new JLabel(" Your score is ");
	private JLabel timeLeftText = new JLabel(" Timeout in ");
	private JTextArea incorrectWords = new JTextArea();
	private JScrollPane incorrectWords_scroll = new JScrollPane(incorrectWords,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JPanel control = new JPanel();
	private JButton start = new JButton("Start");
	private JButton pause = new JButton("Pause");
	private JButton stop = new JButton("Stop");

	private Analyzer analyzer;
	private Display display;
	private StringBuffer buf;

	private int gameTime;
	private Timer timer;
	/*
	 * 0: pause 1: on game 2: stopped
	 */
	private final static int NEWGAME = -1, PAUSE = 0, ONGAME = 1, STOPPED = 2;
	private byte status = NEWGAME;

	public speedTyper() {
		super("SpeedTyper");
		init();
	}

	private void init() {
		setSize(600, 400);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		mainPanel.setLayout(new BorderLayout());

		typingArea.setLineWrap(true);
		typingArea.setWrapStyleWord(true);
		mainPanel.add(typingArea_scroll, BorderLayout.CENTER);

		info.setLayout(new GridLayout(2, 5));
		bestScore = new JLabel(" 0 ", SwingConstants.RIGHT);
		name = new JLabel(" David ", SwingConstants.RIGHT);
		score = new JLabel(" 0 ", SwingConstants.RIGHT);
		timeLeft = new JLabel(" 300s ", SwingConstants.RIGHT);
		info.add(bestScoreText);
		info.add(bestScore);
		info.add(new JLabel("|", SwingConstants.CENTER));
		info.add(nameText);
		info.add(name);
		info.add(scoreText);
		info.add(score);
		info.add(new JLabel("|", SwingConstants.CENTER));
		info.add(timeLeftText);
		info.add(timeLeft);
		mainPanel.add(info, BorderLayout.NORTH);

		incorrectWords.setColumns(10);
		incorrectWords.setText("Incorrect words.");
		incorrectWords.setEditable(false);
		mainPanel.add(incorrectWords_scroll, BorderLayout.EAST);

		control.setLayout(new GridLayout(1, 3, 20, 2));
		control.add(start);
		control.add(pause);
		control.add(stop);
		mainPanel.add(control, BorderLayout.SOUTH);

		add(mainPanel);

		gameTime = 300;

		analyzer = new Analyzer();
		display = new Display(incorrectWords, score);
		analyzer.connectTo(display);
		buf = new StringBuffer();

		myEvent();
		setVisible(true);
	}

	private void myEvent() {
		setTimer();
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (gameTime > 0 && status <= 0) {
					if (status == NEWGAME) {
						new Thread(analyzer).start();
						new Thread(new DisplayWords(display)).start();
						new Thread(new DisplayScore(display)).start();
					}
					timer.start();
					start.setText("Start");
					typingArea.setEditable(true);
					status = ONGAME;
				}
			}
		});
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (gameTime > 0 && status == ONGAME) {
					timer.stop();
					start.setText("Restart");
					typingArea.setEditable(false);
					status = PAUSE;
				}
			}
		});
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (gameTime > 0 && (status == ONGAME || status == PAUSE)) {
					timer.stop();
					typingArea.setEditable(false);
					start.setEnabled(false);
					pause.setEnabled(false);
					status = STOPPED;
				}
			}
		});
		typingArea.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (status != ONGAME || e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
					e.consume();
				} else {
					try {
						char c = e.getKeyChar();
						analyzer.putCharacter(c);
						if (Character.isAlphabetic(c) || Character.isDigit(c)) {
							buf.append(e.getKeyChar());
						} else {
							buf.setLength(0);
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		typingArea.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
					if (buf.length() > 0) {
						try {
							analyzer.putCharacter(e.getKeyChar());
							buf.deleteCharAt(buf.length() - 1);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}else{
						e.consume();
					}
				}
			}
		});
	}

	private void setTimer() {
		timer = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (gameTime > 0)
					timeLeft.setText(--gameTime + "s");
				else {
					timer.stop();
					typingArea.setEditable(false);
					start.setEnabled(false);
					pause.setEnabled(false);
					status = STOPPED;
				}
			}
		});
	}

	public static void main(String[] args) {
		new speedTyper();
	}
}
