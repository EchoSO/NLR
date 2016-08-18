import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import struct.Projet;

//TODO @ALPHA2 : lire+commenter
public class StatWizard extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JButton Bt_Ok,Bt_Annuler;
	public JTextField Ed_Commande;
	public int status;
	
	class Contenu extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private JList ListJoueur,ListMonstre;
		private JComboBox CB_ComboSpecial;
		public JTextField Ed_Commande;
		StatWizard parent;		
		Projet projet;
		String comment,Liste1,Liste2;
		CondDecl cd;
		JumpTo jump;

		public Contenu(Projet prj,StatWizard p,String liste1,String liste2,JTextField Ed_C,String[] values,String[] cible,String[] combospecial)
		{		  
		  parent=p;
		  projet=prj;
		  Ed_Commande=Ed_C;
		  Liste1=liste1;
		  Liste2=liste2;
		  setLayout(null);
	      ListJoueur=new JList(values);
	      JScrollPane lj = new JScrollPane(ListJoueur);
	      lj.setBounds(new Rectangle(6,22,255,280));
		  ListJoueur.addMouseListener(new MouseAdapter() {
			  public void mousePressed(MouseEvent e) {
				  if (e.getClickCount() == 2)
				  {
				    	Ed_Commande.setText(Ed_Commande.getText()+ListJoueur.getSelectedValue().toString());
				  }
			  }
		  });
	      add(lj);	      

	      ListMonstre=new JList(cible);
	      JScrollPane lm = new JScrollPane(ListMonstre);
	      lm.setBounds(new Rectangle(270,22,255,280));
		  ListMonstre.addMouseListener(new MouseAdapter() {
			  public void mousePressed(MouseEvent e) {
				  if (e.getClickCount() == 2)
				  {
				    	Ed_Commande.setText(Ed_Commande.getText()+ListMonstre.getSelectedValue().toString());
				  }
			  }
		  });
		  add(lm);
	      
		  if (combospecial!=null)
		  {
			  CB_ComboSpecial=new JComboBox(combospecial);
			  CB_ComboSpecial.setBounds(new Rectangle(6,305,255,20));
			  CB_ComboSpecial.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						Ed_Commande.setText(Ed_Commande.getText()+CB_ComboSpecial.getSelectedItem());
					}
			  });
			 add(CB_ComboSpecial);
		  }
		  
		  Ed_Commande.setBounds(new Rectangle(6,360,545,20));
		  Ed_Commande.addKeyListener(new KeyListener() {
			    public void keyTyped(KeyEvent keyEvent) {
			    }
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode()==KeyEvent.VK_ENTER)
						Bt_Ok.doClick();
				}
				public void keyReleased(KeyEvent e) {
				}
		  });
	      add(Ed_Commande);

	      Bt_Ok=new JButton("Ok");
		  Bt_Ok.setBounds(new Rectangle(6,385,90,20));
		  add(Bt_Ok);

	      Bt_Annuler=new JButton("Annuler");
		  Bt_Annuler.setBounds(new Rectangle(100,385,90,20));
		  add(Bt_Annuler);
		}
		public void paintComponent(Graphics g)
		{
		  super.paintComponent(g);
	      Graphics2D g2d = (Graphics2D)g;
	    
	      Font font = new Font("MS Sans Serif", Font.PLAIN, 10);
	    
	      g2d.setFont(font);
		  g2d.drawString(Liste1, 6, 20);
		  g2d.drawString(Liste2, 270, 20);
		  g2d.drawString(comment, 6, 335);
		  g2d.drawString("Commande : ", 6, 355);
		}
	}

	public StatWizard(Projet projet,String commande,String Liste1,String Liste2,String commentaire,String[] values,String[] cible,String[] combospecial,Dialog owner,  boolean modal)
	{
	  super(owner,"Stat Wizard",modal);
	  Toolkit k = Toolkit.getDefaultToolkit();
	  Dimension tailleEcran = k.getScreenSize();
	  int largeurEcran = tailleEcran.width;
	  int hauteurEcran = tailleEcran.height;
	  setSize(576,445);
	  setLocation((largeurEcran/2)-288, (hauteurEcran/2)-222);
	  setLayout(new BorderLayout());
	  
      Ed_Commande=new JTextField();
	  Contenu monContenu=new Contenu(projet,this,Liste1,Liste2,Ed_Commande,values,cible,combospecial);
	  monContenu.Ed_Commande.setText(commande);
	  monContenu.comment=commentaire;
	  add(monContenu);

	  Bt_Ok.addActionListener(this);
	  Bt_Annuler.addActionListener(this);
	  setVisible(true);		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==this.Bt_Ok)
			status=1;			
		else
			status=0;
		setVisible(false);
	} 
}