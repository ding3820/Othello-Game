package BlackAndWhite;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

class connectDialog extends JDialog implements ActionListener {
	private JPanel textP1, textP2, buttonP, noticeP;
	private JLabel hostL, nameL, noticeL;
	private JTextField hostT, nameT;
	private JButton yesB, noB;
	public String host, name;
	public boolean yes = false;

	public connectDialog(Frame owner) {
		super(owner, "Connect game", true);

		hostL = new JLabel("Server ip");
		hostT = new JTextField("127.0.0.1", 20);
		nameL = new JLabel("PC name");
		nameT = new JTextField("client", 20);
		textP1 = new JPanel();
		textP2 = new JPanel();
		textP1.add(hostL);
		textP1.add(hostT);
		textP2.add(nameL);
		textP2.add(nameT);

		yesB = new JButton("Yes");
		noB = new JButton("No");
		buttonP = new JPanel();
		buttonP.add(yesB);
		buttonP.add(noB);
		yesB.addActionListener(this);
		noB.addActionListener(this);

		noticeL = new JLabel("Connect");
		noticeP = new JPanel();
		noticeP.add(noticeL);

		getContentPane().setLayout(new GridLayout(4, 1));
		getContentPane().add(noticeP);
		getContentPane().add(textP1);
		getContentPane().add(textP2);
		getContentPane().add(buttonP);
		Point p;
		p = owner.getLocation();
		int x = (int) p.getX() + 200;
		int y = (int) p.getY() + 150;
		this.setLocation(x, y);
		setResizable(false);
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == yesB) {
			host = hostT.getText();
			name = nameT.getText();
			yes = true;
		}
		hide();
	}
}