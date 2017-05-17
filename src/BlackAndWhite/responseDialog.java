package BlackAndWhite;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class responseDialog extends JDialog implements ActionListener
{
	public static final int agree=0;
	public static final int disagree=1;
	private JPanel buttonP,labelP,mainP;
	private JButton yesB;
	private JLabel  l1,lu,ld;
	
	private String status[]={"The opponent agrees your undo request!","The opponent disagrees your undo request!"};

	public responseDialog(Frame owner,int s)
	{
		super(owner, "undo...", true);

		l1 = new JLabel("                       " + status[s] + "                       ");
		lu = new JLabel(" ");
		ld = new JLabel(" ");
		labelP = new JPanel(new GridLayout(3, 1));
		labelP.add(lu);
		labelP.add(l1);
		labelP.add(ld);
		labelP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		yesB = new JButton("Yes");
		buttonP = new JPanel();
		buttonP.add(yesB);
		buttonP.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		yesB.addActionListener(this);

		mainP = new JPanel(new BorderLayout());
		mainP.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		mainP.add(labelP, BorderLayout.NORTH);
		mainP.add(buttonP, BorderLayout.SOUTH);
		Point p;
		p = owner.getLocation();
		int x = (int) p.getX() + 200;
		int y = (int) p.getY() + 150;
		this.setLocation(x, y);
		setResizable(false);

		getContentPane().add(mainP);
		pack();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		hide();                            
	}
}