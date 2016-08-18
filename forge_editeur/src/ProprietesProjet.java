import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import struct.Projet;

//TODO @ALPHA2 : lire+commenter
public class ProprietesProjet extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JButton Bt_Ok=new JButton("Ok");
	private JButton Bt_Annuler=new JButton("Annuler");
	public int status;
	private Projet general;
	public ContenuSimple panel;
	private JRadioButton RB_Automatique,RB_Menu,RB_Manuel;
	private JCheckBox CB_MenuFreeze;

	public ProprietesProjet(Projet general_,Dialog owner, String titre , boolean modal)
	{
		super(owner,titre,modal);
		general=general_;
		setTitle("Propriétés du projet");
		setSize(337, 170);
		Toolkit k = Toolkit.getDefaultToolkit();
		Dimension tailleEcran = k.getScreenSize();
		int largeurEcran = tailleEcran.width;
		int hauteurEcran = tailleEcran.height;
		setLocation((largeurEcran/2)-(337/2), (hauteurEcran/2)-(150/2));
		Container leContenant = getContentPane();
		panel=new ContenuSimple();
		leContenant.add(panel);
		Bt_Ok.addActionListener(
				 new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if (RB_Automatique.isSelected()) general.setStyleProjet((short) 0);
				if (RB_Menu.isSelected()) general.setStyleProjet((short) 1);
				if (RB_Manuel.isSelected()) general.setStyleProjet((short) 2);
				general.setMenuFreeze(CB_MenuFreeze.isSelected());
				Bt_Ok.removeActionListener(this);
				Bt_Ok.addActionListener(Bt_Annuler.getActionListeners()[0]);
				Bt_Ok.doClick();
			}
		});

		Bt_Annuler.addActionListener(this);
		setVisible(true);		
	}
	class ContenuSimple extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public ContenuSimple()
		{ 
		   setLayout(null);
		   setResizable(false);
		   Font font = new Font("MS Sans Serif", Font.PLAIN, 10);
			JPanel panetypeprojet=new JPanel();
			TitledBorder title = BorderFactory.createTitledBorder("Type de projet");
			panetypeprojet.setBorder(title);
			panetypeprojet.setBounds(new Rectangle(3,30,325,40));
			panetypeprojet.setLayout(new GridLayout(1,0));
			add(panetypeprojet);
			ButtonGroup group=new ButtonGroup();
			RB_Automatique=new JRadioButton("Automatique");
			group.add(RB_Automatique);
			panetypeprojet.add(RB_Automatique);
			RB_Menu=new JRadioButton("Menu");
			group.add(RB_Menu);
			panetypeprojet.add(RB_Menu);
			RB_Manuel=new JRadioButton("Manuel");
			group.add(RB_Manuel);
			panetypeprojet.add(RB_Manuel);
			switch(general.getStyleProjet())
			{
				case 0 : RB_Automatique.setSelected(true); break;
				case 1 : RB_Menu.setSelected(true); break;
				case 2 : RB_Manuel.setSelected(true); break;
			}
			CB_MenuFreeze=new JCheckBox("Les monstres ne bougent plus lors de l'accès au menu");
			CB_MenuFreeze.setFont(font);
			CB_MenuFreeze.setBounds(new Rectangle(5,72,300,25));
			CB_MenuFreeze.setSelected(general.isMenuFreeze());
			add(CB_MenuFreeze);
		   Bt_Ok.setBounds(new Rectangle(30,102,90,25));
		   add(Bt_Ok);
		   Bt_Annuler.setBounds(new Rectangle(200,102,90,25));
		   add(Bt_Annuler);
		}
		
		public void paintComponent(Graphics g)
		{
		  super.paintComponent(g);
		  g.drawString("Nom : "+general.getName(), 6, 22);
		}
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