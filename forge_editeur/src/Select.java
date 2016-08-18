import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

//TODO @ALPHA2 : lire+commenter
public class Select extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JTextField Ed_Choose;
	private JList liste;
	private JButton Bt_Ok=new JButton("Ok");
	
	public int status;
	public ContenuSimple panel;
	public String resultat;
	
	public Select(String[] list,Dialog owner, String titre , boolean modal)
	{
		super(owner,titre,modal);
		setTitle(titre);
		liste=new JList(list);
		setSize(327, 246);
		Toolkit k = Toolkit.getDefaultToolkit();
		Dimension tailleEcran = k.getScreenSize();
		int largeurEcran = tailleEcran.width;
		int hauteurEcran = tailleEcran.height;
		setLocation((largeurEcran/2)-(327/2), (hauteurEcran/2)-(246/2));
		Container leContenant = getContentPane();
		panel=new ContenuSimple();
		leContenant.add(panel);
		Bt_Ok.addActionListener(this);
		setVisible(true);
	}
	class ContenuSimple extends JPanel
	{
		private static final long serialVersionUID = 1L;
		public ContenuSimple()
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			Ed_Choose=new JTextField();
			Ed_Choose.setAlignmentX(CENTER_ALIGNMENT);
			Ed_Choose.setPreferredSize(new Dimension(327, 20));
			Ed_Choose.setMinimumSize(new Dimension(327, 20));
			Ed_Choose.addKeyListener(new KeyListener() {
			    public void keyTyped(KeyEvent keyEvent) {
			    }

				public void keyPressed(KeyEvent keyEvent) {
			    	String ch=Ed_Choose.getText()+keyEvent.getKeyChar();
					int index=getMatch(ch);
					if (index>=0)
						liste.setSelectedIndex(index);
				}

				public void keyReleased(KeyEvent e) {
				}
			});
			add(Ed_Choose);
			JScrollPane listScroller = new JScrollPane(liste);
			listScroller.setPreferredSize(new Dimension(327, 170));
			listScroller.setMinimumSize(new Dimension(327, 170));
			listScroller.setAlignmentX(CENTER_ALIGNMENT);
			liste.addMouseListener(new MouseAdapter() {
			      public void mouseClicked(MouseEvent me) {
			    	 if (me.getClickCount()==2)
			    		 Bt_Ok.doClick();
			      }
			});
			add(listScroller);
			Bt_Ok.setAlignmentX(CENTER_ALIGNMENT);
			add(Bt_Ok);
		}
	}
	private int getMatch(String s) {
	    for (int i = 0; i < liste.getModel().getSize(); i++) {
	      String s1 = (String) liste.getModel().getElementAt(i);
	      if (s1 != null) {
	        if (s1.toLowerCase().startsWith(s.toLowerCase()))
	          return i;
	      }
	    }
        return -1;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==this.Bt_Ok)
		{
			status=1;
			resultat=(String) liste.getSelectedValue();
		}
		else
		{
			status=0;
			resultat="";
		}
		setVisible(false);
	} 
}