package BlackAndWhite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.net.*;
import java.io.*;

public class BlackAndWhite extends JFrame
		implements Observer, ActionListener {
	public ClockPanel clockP;
	public InfoPanel infoP;
	public ChessBoardPanel cbP;

	public Chessboard cb;

	public JButton startB;
	JButton redoB;
	private JButton surrenderB;
	private JPanel JButtonP;

	private JTextField messageT;
	public TextArea infoT;
	private JButton sendB;
	private JPanel textP;

	private JMenuBar MBA;
	private JMenu operaterM;
	private JMenu aboutM;
	private JMenu offoperaterM;
	private JMenu AIgameM;

	private JMenuItem connectMI;
	private JMenuItem establishMI;
	private JMenuItem disconnectMI;
	private JMenuItem exitMI;
	private JMenuItem aboutMI;
	private JMenuItem startMI;
	private JMenuItem savefileMI;
	private JMenuItem openfileMI;
	private JMenuItem setting1MI;
	private JMenuItem setting2MI;
	private JMenuItem AIgameMI;

	private JPanel leftP;
	private JPanel rightP;
	private JPanel mainP;
	static BlackAndWhite f;

	public static String timesetting;
	public static boolean timeset = false;

	public static String timesetting2;
	public static boolean timeset2 = false;

	public int currentP = Style.BLACK;

	public int blackredo = 0;
	public int whiteredo = 0;

	private serverskt server;
	private clientskt client;
	private boolean isServer = false;
	public static boolean isOffline = false;
	public boolean isAct;
	public boolean ready, remready;
	public boolean started;
	public boolean isRedoSender;
	public boolean isConnected = false;
	public boolean isundo = false;

	public static boolean AIgame = false;

	public boolean blackwin = false, whitewin = false;

	public boolean openfile = false;

	public String serverName = "Sever: ";
	public String clientName = "Client: ";

	Object lockready = new Object();

	private clockPThread clockPT = null;

	private musicPlay mP = new musicPlay();

	private TextArea textArea = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);

	public BlackAndWhite(String title) {
		super(title);
		mainPanel();
		restart();
	}

	public void update(Observable o, Object arg) {

		if (isOffline) {
			if (!started)
				return;
			if (cb.redo)
				return;
			if (cb.currentP == Style.EMPTY) {
				if (cb.countBlack > cb.countWhite) {
					blackwin = true;
					win();
				}
				else if (cb.countBlack < cb.countWhite) {
					whitewin = true;
					win();
				}
				else {
					drawgame();
				}
			}

		}
		else {
			if (!started)
				return;
			if (cb.redo) {
				// System.out.println("123");
				redoB.setEnabled(false);
				return;
			}
			if (currentP == cb.oldP) {
				redoB.setEnabled(true);
				if (isServer)
					server.dataout("playchess," + cb.currentX + "," + cb.currentY);
				else
					client.dataout("playchess," + cb.currentX + "," + cb.currentY);
			}
			else {
				redoB.setEnabled(false);
			}
			if (isServer && cb.currentP == Style.EMPTY) {
				if (currentP == Style.BLACK) {
					if (cb.countBlack > cb.countWhite) {
						server.dataout("lose,");
						win();
					}
					else if (cb.countBlack < cb.countWhite) {
						server.dataout("win,");
						lose();
					}
					else {
						server.dataout("draw,");
						drawgame();
					}
				}
				else {
					if (cb.countWhite > cb.countBlack) {
						server.dataout("lose,");
						win();
					}
					else if (cb.countWhite < cb.countBlack) {
						server.dataout("win,");
						lose();
					}
					else {
						server.dataout("draw,");
						drawgame();
					}
				}
			}
		}
	}

	public void restart() {
		connectMI.setEnabled(true);
		disconnectMI.setEnabled(false);
		establishMI.setEnabled(true);
		startB.setEnabled(false);
		redoB.setEnabled(false);
		surrenderB.setEnabled(false);
		sendB.setEnabled(false);
		isAct = false;
		ready = remready = false;
		started = false;
		cb.restart();
		cbP.restart();
		clockP.restart();
		infoP.restart();
	}

	public synchronized void reset() {
		connectMI.setEnabled(false);
		disconnectMI.setEnabled(true);
		establishMI.setEnabled(false);
		if (isAct)
			startB.setEnabled(true);
		else
			startB.setEnabled(false);
		redoB.setEnabled(false);
		surrenderB.setEnabled(false);
		sendB.setEnabled(true);
		ready = remready = false;
		started = false;
		cb.reset();
		cbP.reset();
		clockP.reset();
		infoP.reset();
	}

	public void start() {

		mP.clip1.start();

		cb.start(Style.BLACK);
		cbP.start(currentP);
		clockP.start(currentP);

		if (isOffline) {
			infoP.start("PLAYER 1", "PLAYER 2");
		}
		else {
			if (isServer) {
				if (currentP == Style.BLACK)
					infoP.start(serverName, clientName);
				else
					infoP.start(clientName, serverName);
			}
			else {
				if (currentP == Style.BLACK)
					infoP.start(clientName, serverName);
				else
					infoP.start(serverName, clientName);
			}
		}

		started = true;
		isRedoSender = false;
		connectMI.setEnabled(false);
		disconnectMI.setEnabled(true);
		establishMI.setEnabled(false);
		startB.setEnabled(false);
		redoB.setEnabled(true);
		surrenderB.setEnabled(true);
		sendB.setEnabled(true);
		disable(true);
	}

	public void win() {
		if (isOffline) {
			if (blackwin) {
				infoT.append(">" + "Black" + " Win!\n");
				clockP.reset();
				over(statusDialog.BLACKWIN);
			}
			if (whitewin) {
				infoT.append(">" + "White" + " Win!\n");
				clockP.reset();
				over(statusDialog.WHITEWIN);
			}

		}
		else {
			if (isServer)
				infoT.append(">" + serverName + " Win!\n" + ">" + clientName + " Lose!\n");
			else
				infoT.append(">" + clientName + " Win!\n" + ">" + serverName + " Lose!\n");
			clockP.reset();
			currentP = Style.WHITE;
			over(statusDialog.WIN);
		}
	}

	public void lose() {
		if (!isServer)
			infoT.append(">" + serverName + " Win!\n" + ">" + clientName + " Lose!\n");
		else
			infoT.append(">" + clientName + " Win!\n" + ">" + serverName + " Lose!\n");
		clockP.reset();
		currentP = Style.BLACK;
		over(statusDialog.LOSE);
	}

	public void drawgame() {
		infoT.append(">Draw!\n");
		over(statusDialog.DRAW);
	}

	public void timeOut() {
		if (isOffline) {
			if (cb.currentP == Style.BLACK) {
				infoT.append(">" + "Black " + "Timeout!  White turn.\n");
				cb.start(Style.WHITE);
				cbP.start(Style.WHITE);
				clockP.reset();
				clockP.start(Style.WHITE);
				// currentP = Style.WHITE;
				over(statusDialog.TIMEOUT);
			}
			else if (cb.currentP == Style.WHITE) {
				infoT.append(">" + "White " + "Timeout!  Black turn.\n");
				cb.start(Style.BLACK);
				cbP.start(Style.BLACK);
				clockP.reset();
				clockP.start(Style.BLACK);
				// currentP = Style.BLACK;
				over(statusDialog.TIMEOUT);
			}

		}
		else {
			if (isServer) {
				server.dataout("timeout,");
				infoT.append(">" + serverName + " Timeout!\n");
				currentP = Style.BLACK;
				cb.currentP = Style.WHITE;
				cb.checkboard();
				repaint();
				over(statusDialog.TIMEOUT);
				clockP.setTurned(false);
			}
			else {
				client.dataout("timeout,");
				cb.currentP = Style.BLACK;
				cb.checkboard();
				repaint();
				clockP.setTurned(false);
			}
		}

	}

	public void over(int s) {
		// reset();
		statusDialog sD = new statusDialog(f, s);
		sD.show();
	}

	public void redo() {

		if (!isOffline) {
			if (isRedoSender && (cb.currentP == currentP)) {
				cb.redo();
			}
			else if (!isRedoSender && (cb.currentP != currentP)) {
				cb.redo();
			}
		}
		cb.redo();
		
		//redoB.setEnabled(true);

	}

	public void disable(boolean b) {
		redoB.setEnabled(b);
		surrenderB.setEnabled(b);
		cbP.disable(b);
		clockPT.setIdle(b);
	}

	public static void main(String arg[]) {
		f = new BlackAndWhite("Othello Game");
		f.setResizable(false);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		f.pack();
		f.setVisible(true);
	}

	private void mainPanel() {

		cb = new Chessboard();
		cb.addObserver(this);
		clockP = new ClockPanel(cb);
		clockP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		infoP = new InfoPanel(cb);
		infoP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		cbP = new ChessBoardPanel(cb);
		cbP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		startB = new JButton("Start ");
		startB.setContentAreaFilled(false);
		startB.addActionListener(this);
		redoB = new JButton("Undo");
		redoB.setContentAreaFilled(false);
		redoB.addActionListener(this);
		surrenderB = new JButton("Surrender");
		surrenderB.setContentAreaFilled(false);
		surrenderB.addActionListener(this);
		JButtonP = new JPanel();
		JButtonP.add(startB);
		JButtonP.add(redoB);
		JButtonP.add(surrenderB);
		JButtonP.setSize(450, 60);
		JButtonP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		messageT = new JTextField(15);
		messageT.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!isAct)
						return;
					if (messageT.getText().length() == 0)
						return;
					if (isServer) {
						server.dataout("message," + messageT.getText());
						infoT.append(serverName + ":\n" + messageT.getText() + "\n");
					}
					else {
						client.dataout("message," + messageT.getText());
						infoT.append(clientName + ":\n" + messageT.getText() + "\n");
					}
					messageT.setText("");
				}
			}

			public void keyTyped(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}
		});
		infoT = new TextArea("", 10, 20, TextArea.SCROLLBARS_VERTICAL_ONLY);
		infoT.setEditable(false);
		sendB = new JButton("Send");
		sendB.addActionListener(this);
		textP = new JPanel();
		textP.setLayout(new BorderLayout());
		textP.add(infoT, BorderLayout.NORTH);
		textP.add(messageT, BorderLayout.CENTER);
		textP.add(sendB, BorderLayout.EAST);
		textP.setSize(200, 250);
		textP.setBorder(BorderFactory.createEmptyBorder());

		leftP = new JPanel();
		leftP.setLayout(new BorderLayout());
		leftP.add(cbP, BorderLayout.CENTER);
		leftP.add(JButtonP, BorderLayout.NORTH);
		leftP.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		rightP = new JPanel();
		rightP.setLayout(new BorderLayout());
		rightP.add(infoP, BorderLayout.CENTER);
		rightP.add(clockP, BorderLayout.NORTH);
		rightP.add(textP, BorderLayout.SOUTH);
		rightP.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		mainP = new JPanel();
		mainP.setLayout(new BorderLayout());
		mainP.add(leftP, BorderLayout.CENTER);
		mainP.add(rightP, BorderLayout.EAST);

		MBA = new JMenuBar();

		offoperaterM = new JMenu("Offline ");
		operaterM = new JMenu("Online ");
		AIgameM = new JMenu("PVE ");
		aboutM = new JMenu("About ");
		connectMI = new JMenuItem("Connect");
		connectMI.addActionListener(this);
		establishMI = new JMenuItem("Establish Server");
		establishMI.addActionListener(this);
		disconnectMI = new JMenuItem("Disconnect");
		disconnectMI.addActionListener(this);
		setting2MI = new JMenuItem("setting");
		setting2MI.addActionListener(this);
		exitMI = new JMenuItem("Exit");
		exitMI.addActionListener(this);
		aboutMI = new JMenuItem("About ");
		aboutMI.addActionListener(this);
		startMI = new JMenuItem("Start(2P game)");
		startMI.addActionListener(this);
		savefileMI = new JMenuItem("Save File");
		savefileMI.addActionListener(this);
		openfileMI = new JMenuItem("Open File");
		openfileMI.addActionListener(this);
		AIgameMI = new JMenuItem("Start");
		AIgameMI.addActionListener(this);

		setting1MI = new JMenuItem("Setting");
		setting1MI.addActionListener(this);
		operaterM.add(establishMI);
		operaterM.add(connectMI);
		operaterM.add(disconnectMI);
		operaterM.add(setting2MI);
		// operaterM.addSeparator();
		// operaterM.add(exitMI);
		aboutM.add(aboutMI);
		offoperaterM.add(startMI);
		offoperaterM.add(openfileMI);
		offoperaterM.add(savefileMI);
		// offoperaterM.add(AIgameMI);
		offoperaterM.add(setting1MI);
		offoperaterM.addSeparator();
		offoperaterM.add(exitMI);
		AIgameM.add(AIgameMI);
		MBA.add(offoperaterM);
		MBA.add(operaterM);
		MBA.add(AIgameM);
		MBA.add(aboutM);

		setJMenuBar(MBA);
		getContentPane().add(mainP);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		int x = (width - 600) / 2;
		int y = (height - 500) / 2;
		this.setLocation(x, y);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startB) {
			if (clockPT == null) {
				clockPT = new clockPThread(f);
				clockPT.start();
			}
			if (isServer) {
				synchronized (lockready) {
					if (currentP == Style.BLACK)
						server.dataout("start," + Style.WHITE);
					else
						server.dataout("start," + Style.BLACK);
					if (!remready)
						infoT.append(">Waiting for your opponent!\n");
					else {
						infoT.append(">Game starts!\n");
						start();
					}
					ready = true;
				}
			}
			else {
				synchronized (lockready) {
					client.dataout("start,");
					if (remready) {
						infoT.append(">Game starts!\n");
						start();
					}
					else
						infoT.append(">Waiting for your opponent!\n");
					ready = true;
				}
			}
			startB.setEnabled(false);
		}
		else if (e.getSource() == redoB) {
			isRedoSender = true;
			askRedoDialog arD = new askRedoDialog(f, askRedoDialog.request);
			arD.show();
			if (arD.yes == true) {
				disable(false);
				if (isOffline) {
					if (cb.currentP == Style.WHITE) {
						blackredo++;
						if (blackredo <= 2) {
							redo();
						}
						else {
							JOptionPane.showMessageDialog(null, "Undo limit exceeds!", "Alert", JOptionPane.ERROR_MESSAGE);
							infoT.append("undo limit exceeds\n");
						}

					}
					else if (cb.currentP == Style.BLACK) {
						whiteredo++;
						if (whiteredo <= 2) {
							redo();
						}
						else {
							JOptionPane.showMessageDialog(null, "Undo limit exceeds!", "Alert", JOptionPane.ERROR_MESSAGE);
							infoT.append("undo limit exceeds\n");
						}

					}
					disable(true);

				}
				else {
					if (isServer)
						server.dataout("redo,");
					else
						client.dataout("redo,");

				}
			}
		}
		else if (e.getSource() == surrenderB) {
			if (isOffline) {
				statusDialog sD = new statusDialog(f, statusDialog.SURRENDER2);
				sD.show();
				if (cb.currentP == Style.BLACK) {
					if (sD.yes) {
						infoT.append(">" + "Black " + " Surrender!\n");
						whitewin = true;
						win();
					}
					// over(statusDialog.SURRENDER2);

				}
				else if (cb.currentP == Style.WHITE) {
					if (sD.yes) {
						infoT.append(">" + "White " + " Surrender!\n");
						blackwin = true;
						win();
					}

					// over(statusDialog.SURRENDER2);

				}

			}
			else {
				if (isServer) {
					server.dataout("surrender,");
					infoT.append(">" + serverName + " Surrender!\n" + ">" + clientName + " Win!\n");
					currentP = Style.BLACK;
					over(statusDialog.SURRENDER);
				}
				else {
					client.dataout("surrender,");
				}
			}
		}
		else if (e.getSource() == sendB) {
			if (!isAct)
				return;
			if (messageT.getText().length() == 0)
				return;
			if (isServer) {
				server.dataout("message," + messageT.getText());
				infoT.append(serverName + ":\n" + messageT.getText() + "\n");
			}
			else {
				client.dataout("message," + messageT.getText());
				infoT.append(clientName + ":\n" + messageT.getText() + "\n");
			}
			messageT.setText("");
		}
		else if (e.getSource() == aboutMI) {
			aboutDialog aboutD = new aboutDialog(f);
			aboutD.show();
		}
		else if (e.getSource() == connectMI) {
			connectDialog conD = new connectDialog(f);
			conD.show();
			if (conD.yes) {
				reset();
				isServer = false;
				clientName = "Client:" + conD.name;
				client = new clientskt(conD.host, 421, f);
				client.start();
			}

		}
		else if (e.getSource() == establishMI) {
			establishDialog estD = new establishDialog(f);
			estD.show();
			if (estD.yes) {
				try {
					reset();
					isServer = true;
					serverName = "Server:" + estD.name;
					server = new serverskt(421, f);
					server.start();
				}
				catch (Exception er) {
					infoT.append("Fail to start a game!\n");
					restart();
				}
			}
		}
		else if (e.getSource() == disconnectMI) {
			disconnect();
		}
		else if (e.getSource() == exitMI) {
			System.exit(0);
		}
		else if (e.getSource() == startMI) {

			isOffline = true;
			if (clockPT == null) {
				clockPT = new clockPThread(f);
				clockPT.start();
			}
			clockP.reset();
			start();

		}
		else if (e.getSource() == openfileMI) {
			boolean correct = false;
			int temp_currentP = 0;
			openfile = true;
			JFileChooser open = new JFileChooser("save");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
			open.setFileFilter(filter);
			open.setAcceptAllFileFilterUsed(false);
			open.setMultiSelectionEnabled(false);
			disableButton(open, "FileChooser.homeFolderIcon");
			disableButton(open, "FileChooser.upFolderIcon");
			disableButton(open, "FileChooser.newFolderIcon");
			int option = open.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) {
				this.textArea.setText("");
				try {
					BufferedReader br = new BufferedReader(new FileReader(open.getSelectedFile().getPath()));
					int i = 0, j = 0;
					if (br.readLine().equals("mark")) {
						correct = true;
					}
					else {
						JOptionPane.showMessageDialog(null, "Wrong file!", "ERROR", JOptionPane.ERROR_MESSAGE);
					}

					while (br.ready()) {
						if (i == 8 && j == 0) {
							temp_currentP = Character.getNumericValue(br.read());
						}
						else {
							cb.board[i][j] = Character.getNumericValue(br.read());
						}
						j++;
						if (j == 8 && i != 8) {
							j = 0;
							i++;
						}
					}

				}
				catch (Exception ex) {
					System.out.println(ex);
				}
				/*
				 * for(int i=0; i<8; i++){ for(int j=0; j<8; j++){
				 * System.out.print(cb.board[i][j]); }System.out.println(); }
				 */
				if (correct) {
					isOffline = true;
					if (clockPT == null) {
						clockPT = new clockPThread(f);
						clockPT.start();
					}
					start();
					cb.start(temp_currentP);
					cb.getCount();
					repaint();
				}

			}

		}
		else if (e.getSource() == savefileMI) {

			if (started) {

				textArea.append("mark\r\n");

				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 8; j++) {
						if (cb.board[i][j] == Style.EMPTY)
							textArea.append("3");
						else if (cb.board[i][j] == Style.BLACK)
							textArea.append("1");
						else if (cb.board[i][j] == Style.WHITE)
							textArea.append("0");
					}
				}
				textArea.append(Integer.toString(cb.currentP));
				JFileChooser save = new JFileChooser("save");
				save.setSelectedFile(new File("fileToSave.txt"));

				FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
				save.setFileFilter(filter);
				save.setAcceptAllFileFilterUsed(false);
				save.setMultiSelectionEnabled(false);
				int option = save.showSaveDialog(this);
				if (option == JFileChooser.APPROVE_OPTION) {
					try {
						BufferedWriter out = new BufferedWriter(new FileWriter(save.getSelectedFile().getPath()));
						out.write(textArea.getText());
						out.close();
					}
					catch (Exception ex) {
						System.out.println(ex.getMessage());
					}
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "You haven't started a game!", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getSource() == setting1MI) {

			if (!timeset) {
				timesetting = JOptionPane.showInputDialog(null, "Please input time limit: ", "Input", JOptionPane.PLAIN_MESSAGE);
				if ((timesetting != null) && (timesetting.length() > 0)) {
					timeset = true;
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "You can't set time limit!", "Alert", JOptionPane.ERROR_MESSAGE);
			}

		}
		else if (e.getSource() == setting2MI) {
			if (!started) {
				if (isServer && isConnected) {
					timesetting2 = JOptionPane.showInputDialog(null, "Please input time limit: ", "Input", JOptionPane.PLAIN_MESSAGE);
					if ((timesetting2 != null) && (timesetting2.length() > 0)) {
						timeset2 = true;
						server.dataout("timeset " + timesetting2 + ",");
						infoT.append(">" + "  Time limit has been set!\n" + timesetting2 + "sec");
						clockP.reset();
					}
				}
				else {
					JOptionPane.showMessageDialog(null, "You can't set time limit!", "Alert", JOptionPane.ERROR_MESSAGE);
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "You can't set time limit!", "Alert", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (e.getSource() == AIgameMI) {
			AIgame = true;

			isOffline = true;
			if (clockPT == null) {
				clockPT = new clockPThread(f);
				clockPT.start();
			}
			clockP.reset();
			start();

		}

		//////////////////////////////////////////////
	}

	public static void disableButton(final Container c, final String iconString) {
		int len = c.getComponentCount();
		for (int i = 0; i < len; i++) {
			Component comp = c.getComponent(i);
			if (comp instanceof JButton) {
				JButton b = (JButton) comp;
				Icon icon = b.getIcon();
				if (icon != null
						&& icon == UIManager.getIcon(iconString)) {
					b.setEnabled(false);
				}
			}
			else if (comp instanceof Container) {
				disableButton((Container) comp, iconString);
			}
		}
	}

	public void disconnect() {
		try {
			if (isServer) {
				server.skt.close();
				server.Client.close();
				server.stopRequest();
				server = null;
			}
			else {
				client.skt.close();
				client.stopRequest();
				client = null;
			}
		}
		catch (Exception er) {
		}
		restart();
	}
}

class clientskt extends Thread {
	Socket skt;
	InetAddress host;
	int port;

	BufferedReader theInputStream;
	volatile PrintStream theOutputStream;
	String readin;

	private volatile boolean stopRequested = false;

	BlackAndWhite mainP;

	public clientskt(String ip, int p, BlackAndWhite m) {
		try {
			this.mainP = m;
			host = InetAddress.getByName(ip);
			port = p;
		}
		catch (IOException e) {
		}
	}

	public void run() {
		String com;
		try {
			mainP.infoT.append(">Trying to connect......\n");
			skt = new Socket(host, port);
			theInputStream = new BufferedReader(
					new InputStreamReader(skt.getInputStream()));
			theOutputStream = new PrintStream(skt.getOutputStream());
			dataout("connect," + mainP.clientName);
			mainP.isAct = true;
			mainP.startB.setEnabled(true);

			while (!stopRequested) {
				readin = theInputStream.readLine();
				com = readin.substring(0, readin.indexOf(","));

				if (com.compareTo("connect") == 0) {
					mainP.serverName = readin.substring(readin.indexOf(",") + 1);
					mainP.infoT.append(">Connected with " + mainP.serverName + "\n");
				}
				else if (com.compareTo("start") == 0) {
					int style;
					synchronized (mainP.lockready) {
						try {
							style = Integer.parseInt(
									readin.substring(6));
							mainP.currentP = style;
						}
						catch (Exception e) {
							mainP.infoT.append(">Internet connecting error. Please try to reconnect\n");
							dataout("other,>Internet connecting error. Please try to reconnect\n");
						}
						if (mainP.ready) {
							mainP.infoT.append(">Game start!\n");
							mainP.start();
						}
						else
							mainP.infoT.append(">Server is Waiting for you!\n");
						mainP.remready = true;
					}
				}
				else if (com.compareTo("playchess") == 0) {
					String sX, sY;
					int x, y, ls, le;
					ls = readin.indexOf(",");
					le = readin.lastIndexOf(",");
					sX = readin.substring(ls + 1, le);
					sY = readin.substring(le + 1);
					try {
						x = Integer.parseInt(sX);
						y = Integer.parseInt(sY);
						mainP.cb.playChess(x, y);
						// mainP.redoB.setEnabled(true);
					}
					catch (Exception e) {
						mainP.infoT.append(">Internet connecting error. Please try to reconnect!\n");
						dataout("other,>Internet connecting error. Please try to reconnect!\n");
					}
				}
				else if (com.compareTo("message") == 0) {
					mainP.infoT.append(mainP.serverName + ":\n");
					mainP.infoT.append(readin.substring(8) + "\n");
				}
				else if (com.compareTo("win") == 0) {
					mainP.win();
				}
				else if (com.compareTo("lose") == 0) {
					mainP.lose();
				}
				else if (com.compareTo("draw") == 0) {
					mainP.drawgame();
				}
				else if (com.compareTo("timeout") == 0) {
					mainP.infoT.append(">" + mainP.serverName + " Timeout!\n");
					mainP.over(statusDialog.OPTIMEOUT);
					mainP.cb.currentP = Style.WHITE;

					mainP.clockP.start(mainP.cb.currentP);
					mainP.cb.checkboard();
					mainP.repaint();
					// mainP.clockP.setTurned(true);
				}
				else if (com.compareTo("surrender") == 0) {
					mainP.infoT.append(">" + mainP.serverName + " ÈÏSurrender!\n" + ">" + mainP.clientName + " Win!\n");
					mainP.win();
					mainP.over(statusDialog.OPSUR);
				}
				else if (com.compareTo("rectimeout") == 0) {
					mainP.infoT.append(">" + mainP.clientName + " Timeout!\n");
					mainP.cb.checkboard();
					mainP.repaint();
					mainP.over(statusDialog.TIMEOUT);
				}
				else if (com.compareTo("recsurrender") == 0) {
					mainP.infoT.append(">" + mainP.clientName + " ÈÏSurrender!\n" + ">" + mainP.serverName + " Win!\n");
					mainP.lose();
					mainP.over(statusDialog.SURRENDER);
				}
				else if (com.compareTo("redo") == 0) {
					askRedoDialog arD = new askRedoDialog(mainP, askRedoDialog.response);
					arD.show();
					if (arD.yes == true) {
						dataout("agreeredo,");
						mainP.redo();
					}
					else
						dataout("disagree,");
				}
				else if (com.compareTo("agreeredo") == 0) {
					responseDialog rsD = new responseDialog(mainP, responseDialog.agree);
					rsD.show();
					mainP.redo();
					mainP.disable(true);
					mainP.redoB.setEnabled(false);
					mainP.isRedoSender = false;
				}
				else if (com.compareTo("disagree") == 0) {
					responseDialog rsD = new responseDialog(mainP, responseDialog.disagree);
					rsD.show();
					mainP.disable(true);
					mainP.isRedoSender = false;
				}
				else if (com.contains("timeset")) {
					BlackAndWhite.timeset2 = true;
					BlackAndWhite.timesetting2 = com.substring(8);
					System.out.println(BlackAndWhite.timesetting2);

					mainP.infoT.append(">" + "  Time limit has been set!\n" + BlackAndWhite.timesetting2 + "sec");
				}
				else {
					mainP.infoT.append(">" + readin + "\n");
				}
			}
		}
		catch (Exception e) {
			mainP.infoT.append("Disconnect\n");
			mainP.disconnect();
		}
	}

	public void dataout(String data) {
		theOutputStream.println(data);
	}

	public void stopRequest() {
		stopRequested = true;
	}
}

class serverskt extends Thread {
	ServerSocket skt;
	Socket Client;
	InetAddress host;

	BufferedReader theInputStream;
	volatile PrintStream theOutputStream;
	String readin;

	BlackAndWhite mainP;

	private volatile boolean stopRequested = false;

	public serverskt(int port, BlackAndWhite m) {
		try {
			this.mainP = m;
			skt = new ServerSocket(port);
		}
		catch (IOException e) {
		}
	}

	public void run() {
		String com;
		try {
			mainP.infoT.append(">Waiting for connection......\n");
			Client = skt.accept();
			theInputStream = new BufferedReader(
					new InputStreamReader(Client.getInputStream()));
			theOutputStream = new PrintStream(Client.getOutputStream());
			dataout("connect," + mainP.serverName);
			mainP.isAct = true;
			mainP.startB.setEnabled(true);

			while (!stopRequested) {
				readin = theInputStream.readLine();
				com = readin.substring(0, readin.indexOf(","));

				if (com.compareTo("connect") == 0) {
					mainP.clientName = readin.substring(readin.indexOf(",") + 1);
					mainP.infoT.append(">" + mainP.clientName + " is connected\n");
					mainP.isConnected = true;
				}
				else if (com.compareTo("start") == 0) {
					synchronized (mainP.lockready) {
						if (!mainP.ready)
							mainP.infoT.append(">Client is Waiting for you!\n");
						else {
							mainP.start();
							mainP.infoT.append(">Game start!\n");
						}
						mainP.remready = true;
					}
				}
				else if (com.compareTo("playchess") == 0) {
					String sX, sY;
					int x, y, ls, le;
					ls = readin.indexOf(",");
					le = readin.lastIndexOf(",");
					sX = readin.substring(ls + 1, le);
					sY = readin.substring(le + 1);
					try {
						x = Integer.parseInt(sX);
						y = Integer.parseInt(sY);
						mainP.cb.playChess(x, y);
						// mainP.redoB.setEnabled(true);
					}
					catch (Exception e) {
						mainP.infoT.append(">Internet connecting error. Please try to reconnect!\n");
						dataout("other,>Internet connecting error. Please try to reconnect!\n");
					}
				}
				else if (com.compareTo("message") == 0) {
					mainP.infoT.append(mainP.clientName + ":\n");
					mainP.infoT.append(readin.substring(8) + "\n");
				}
				else if (com.compareTo("timeout") == 0) {
					mainP.infoT.append(">" + mainP.clientName + " Timeout!\n");
					dataout("rectimeout,");
					mainP.currentP = Style.WHITE;
					mainP.over(statusDialog.OPTIMEOUT);
					mainP.cb.currentP = Style.BLACK;

					mainP.clockP.start(mainP.cb.currentP);
					mainP.cb.checkboard();
					mainP.repaint();
					// mainP.clockP.setTurned(true);
				}
				else if (com.compareTo("surrender") == 0) {
					mainP.infoT.append(">" + mainP.clientName + " Surrender!\n" + ">" + mainP.serverName + " Win!\n");
					dataout("recsurrender,");
					mainP.win();
					mainP.currentP = Style.WHITE;
					mainP.over(statusDialog.OPSUR);
				}
				else if (com.compareTo("redo") == 0) {
					askRedoDialog arD = new askRedoDialog(mainP, askRedoDialog.response);
					arD.show();
					if (arD.yes == true) {
						dataout("agreeredo,");
						mainP.redo();
					}
					else
						dataout("disagree,");
				}
				else if (com.compareTo("agreeredo") == 0) {
					responseDialog rsD = new responseDialog(mainP, responseDialog.agree);
					rsD.show();
					mainP.redo();
					mainP.disable(true);
					mainP.redoB.setEnabled(false);
					mainP.isRedoSender = false;
				}
				else if (com.compareTo("disagree") == 0) {
					responseDialog rsD = new responseDialog(mainP, responseDialog.disagree);
					rsD.show();
					mainP.disable(true);
					mainP.isRedoSender = false;
				}
				else {
					mainP.infoT.append(">" + readin.substring(5) + "\n");
				}
			}
		}
		catch (Exception e) {
			mainP.infoT.append("Disconnect\n");
			mainP.disconnect();
		}
	}

	public void dataout(String data) {
		theOutputStream.println(data);
	}

	public void stopRequest() {
		stopRequested = true;
	}
}

class clockPThread extends Thread {
	BlackAndWhite mainP;
	volatile boolean idle = false;

	public clockPThread(BlackAndWhite m) {
		mainP = m;
	}

	public void run() {
		while (true) {
			if (!idle) {
				mainP.clockP.repaint();
				// System.out.println("currentP :" + mainP.currentP);
				// System.out.println("cb.currentP :" + mainP.cb.currentP);
				if (BlackAndWhite.isOffline) {
					if (mainP.clockP.getLT() == 0)
						mainP.timeOut();
				}
				else {
					if (mainP.clockP.getLT() == 0 && mainP.currentP == mainP.cb.currentP)
						mainP.timeOut();
				}
			}
			try {
				Thread.sleep(200);
			}
			catch (Exception e) {
			}
		}
	}

	public void setIdle(boolean b) {
		idle = !b;
	}
}