package starter.advancedStarter;

import javax.swing.*;

/**
 * the panel displayed at the starting splash screen when the game begins
 * @author Jack
 *
 */
public class PrimaryStartPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public PrimaryStartPanel()
	{
		JButton single = new JButton("Single Player");
		add(single);
		//setOpaque(false);
	}
}
