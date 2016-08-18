import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**################################################################**/
public class JumpTo extends JDialog implements ActionListener
/**################################################################
 * Classe JumpTo: Fenêtre pour aller à une coordonnée sur la map**/
{
	private static final long serialVersionUID = 1L;
	public JTextField Ed_X,Ed_Y;
	public JLabel LblX,LblY;
	public int status;
	JButton Bt_Ok,Bt_Annuler;
	
	public JumpTo(Dialog owner, boolean modal)
	{/**Constructeur : Initialise la fenêtre JumpTo**/
	  super(owner,"Coordonnées",modal);
	  Toolkit k = Toolkit.getDefaultToolkit();
	  Dimension tailleEcran = k.getScreenSize();
	  int largeurEcran = tailleEcran.width;
	  int hauteurEcran = tailleEcran.height;
	  setSize(188,97);//Définit la taille et la position de la fenêtre
	  setResizable(false);
	  setLocation((largeurEcran/2)-94, (hauteurEcran/2)-48);	  
	  setLayout(null);
	  LblX=new JLabel("X :");
	  LblX.setBounds(new Rectangle(18,10,20,20));
	  add(LblX);
	  Ed_X=new JTextField("0");//Initialise les labels et TextField X
	  Ed_X.setBounds(new Rectangle(40,10,40,20));
	  Ed_X.addFocusListener(new FocusAdapter()
	  {
	      public void focusGained(final FocusEvent fe)
	      {SwingUtilities.invokeLater(new Runnable()
	        { public void run()/**Quand X obtient le focus, sélectionne tout le texte**/
	          {((JTextField)fe.getSource()).selectAll();}});}});
	  
	  Ed_X.addMouseListener(new MouseAdapter()
	  {
	      public void mouseReleased(final MouseEvent me)
	      {SwingUtilities.invokeLater(new Runnable()
	      	{public void run()/**Quand on clique dans X, sélectionne tout le texte**/
	            {((JTextField)me.getSource()).selectAll();}});}});
	  
	  Ed_X.addKeyListener(new KeyListener() 
	  {
		    public void keyTyped(KeyEvent keyEvent) {}
			public void keyPressed(KeyEvent e) 
			{/**Si on appuie sur "Enter" dans X, valide**/
				if (e.getKeyCode()==KeyEvent.VK_ENTER)
					Bt_Ok.doClick();
			}
			public void keyReleased(KeyEvent e) {}
	  });
	  add(Ed_X);
	  LblY=new JLabel("Y :");
	  LblY.setBounds(new Rectangle(88,10,20,20));
	  add(LblY);
	  Ed_Y=new JTextField("0");//Initialise les labels et TextField Y
	  Ed_Y.setBounds(new Rectangle(110,10,40,20));
	  Ed_Y.addFocusListener(new FocusAdapter()
	  {
	      public void focusGained(final FocusEvent fe)
	      {SwingUtilities.invokeLater(new Runnable()
	        {public void run()/**Quand Y obtient le focus, sélectionne tout le texte**/
	          {((JTextField)fe.getSource()).selectAll();}});}});
	  
	  Ed_Y.addMouseListener(new MouseAdapter()
	  {
	      public void mouseReleased(final MouseEvent me)
	      {SwingUtilities.invokeLater(new Runnable()
	        {public void run()/**Quand on clique dans Y, sélectionne tout le texte**/
	          {((JTextField)me.getSource()).selectAll();}});}});
	  
	  Ed_Y.addKeyListener(new KeyListener() 
	  {
		    public void keyTyped(KeyEvent keyEvent) { }
			public void keyPressed(KeyEvent e) 
			{/**Si on appuie sur "Enter" dans Y, valide**/
				if (e.getKeyCode()==KeyEvent.VK_ENTER)
					Bt_Ok.doClick();
			}
			public void keyReleased(KeyEvent e) {}
	  });
	  add(Ed_Y);
	  
	  Bt_Ok=new JButton("Ok");
	  Bt_Ok.setBounds(new Rectangle(5,40,80,25));
	  Bt_Ok.addActionListener(this);	  
	  add(Bt_Ok);
	  Bt_Annuler=new JButton("Annuler");//Met les boutons ok et annuler
	  Bt_Annuler.setBounds(new Rectangle(95,40,80,25));
	  Bt_Annuler.addActionListener(this);	  
	  add(Bt_Annuler);
	  setVisible(true);		
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{/**Si on clique sur ok, met le status à 1. Ferme.**/
		if (e.getSource()==this.Bt_Ok)
			status=1;			
		else
			status=0;
		setVisible(false);
	} 
}