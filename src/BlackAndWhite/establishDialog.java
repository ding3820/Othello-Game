package BlackAndWhite;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

class establishDialog extends JDialog implements ActionListener {
	private JPanel textP1, buttonP, noticeP;
	private JLabel nameL, noticeL;
	private JTextField nameT;
	private JButton yesB, noB;
	public String name;
	public boolean yes = false;

	public establishDialog(Frame owner) {
		super(owner, "Connect Game", true);

		nameL = new JLabel("PC name");
		nameT = new JTextField("server", 20);
		textP1 = new JPanel();
		textP1.add(nameL);
		textP1.add(nameT);

		yesB = new JButton("Yes");
		noB = new JButton("NO");
		buttonP = new JPanel();
		buttonP.add(yesB);
		buttonP.add(noB);
		yesB.addActionListener(this);
		noB.addActionListener(this);

		noticeL = new JLabel("PC Info");
		noticeP = new JPanel();
		noticeP.add(noticeL);

		getContentPane().setLayout(new GridLayout(3, 1));
		getContentPane().add(noticeP);
		getContentPane().add(textP1);
		getContentPane().add(buttonP);
		Point p;
		p = owner.getLocation();
		int x = (int) p.getX() + 200;
		int y = (int) p.getY() + 150;
		this.setLocation(x, y);
		this.setLocation(x, y);
		setResizable(false);
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == yesB) {
			name = nameT.getText();
			yes = true;
		}
		hide();
	}
}