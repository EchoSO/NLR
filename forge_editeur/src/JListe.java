import java.awt.BorderLayout;
import java.awt.Component;
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


public class JListe extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	JTextField Ed_ChoixRapide;//Texte en haut pour écrire soi même le choix
	public JList ListBox;
	public int status;
	JButton Bt_Ok;
	
/**################################################################**/
	private int search()
/**################################################################
 * Cherche si le texte entré est dans les choix et valide. Sinon -1**/
	{
		String current;
		for(int i = 0; i < ListBox.getModel().getSize(); i++)
		{//Parcoure le contenu de la liste et si un élément correspond au choix,le renvoie
			current = ListBox.getModel().getElementAt(i).toString().toLowerCase();
			if (current.toLowerCase().startsWith(Ed_ChoixRapide.getText().toLowerCase())) return i;
		}
		return -1;
	}
	
/**################################################################**/
	public JListe(String[] Liste,Dialog owner, String titre , boolean modal)
/**################################################################
 * Constructeur de JListe. Crée une liste avec "Liste"**/
	{
	  super(owner,titre,modal);
	  Toolkit k = Toolkit.getDefaultToolkit();
	  Dimension tailleEcran = k.getScreenSize();
	  int largeurEcran = tailleEcran.width;
	  int hauteurEcran = tailleEcran.height;
	  setSize(230,330);//Définit la taille position etc
	  setLocation((largeurEcran/2)-115, (hauteurEcran/2)-165);
	  setLayout(new BorderLayout());
	  Ed_ChoixRapide=new JTextField();
	  Ed_ChoixRapide.addKeyListener(new KeyListener() 
	  {
		    public void keyTyped(KeyEvent keyEvent) {}
			public void keyPressed(KeyEvent keyEvent) {}
			public void keyReleased(KeyEvent e) 
			{/**Quand on modifie le texte de choix rapide, cherche le texte et sélectionne si présent**/
				 if ((e.getKeyChar()!=KeyEvent.CHAR_UNDEFINED)&&(e.getKeyCode()!=KeyEvent.VK_BACK_SPACE) && (e.getKeyCode()!=KeyEvent.VK_DELETE))
				 {//Quand on appuie sur n'impporte quelle touche sauf backspace et delete ou indéfini
					String current=Ed_ChoixRapide.getText();
					int pos=search();//Cherche s'il est dedans
					if (pos>=0)
					{
						ListBox.setSelectedIndex(pos);//S'il est dedans sélectionne
						Ed_ChoixRapide.setText(ListBox.getModel().getElementAt(pos).toString());
						Ed_ChoixRapide.setSelectionStart(current.length());
						Ed_ChoixRapide.setSelectionEnd(Ed_ChoixRapide.getText().length());
					}
				 }
			}
	  });
	  
	  add(Ed_ChoixRapide, BorderLayout.PAGE_START);
	  ListBox=new JList(Liste);
	  ListBox.addMouseListener(new MouseAdapter() 
	  {
		  public void mousePressed(MouseEvent e) 
		  {/**Quand on double-clique sur un élément de la liste, le valide**/
			  if (e.getClickCount() == 2)
				  Bt_Ok.doClick();
		  }
	  });
	  JScrollPane scrollpaneList = new JScrollPane(ListBox);
	  add(scrollpaneList);
	  JPanel pane=new JPanel();
	  pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
	  Bt_Ok=new JButton(" Ok ");
	  Bt_Ok.setAlignmentX(Component.CENTER_ALIGNMENT);//Positionne le bouton "ok"
	  Bt_Ok.addActionListener(this);
	  pane.add(Bt_Ok);
	  add(pane,BorderLayout.PAGE_END);
	  setVisible(true);		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{/**Quand on clique sur Ok, renvoie le status 1. Ferme la fenêtre.**/
		if (e.getSource()==this.Bt_Ok)
			status=1;			
		else
			status=0;
		setVisible(false);
	} 
}