package mapEditor.mapEditorMenuBar;

import java.awt.Component;

import javax.swing.*;

/**
 * a simple dialog to display text
 * @author Jack
 *
 */
public class TextDialog extends JDialog
{
	public TextDialog(Component c, String text)
	{
		add(new JLabel(text));
		pack();
		setLocationRelativeTo(c);
		setVisible(true);
	}
}
