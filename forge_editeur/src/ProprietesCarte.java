import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import struct.Projet;

//TODO @ALPHA2 : lire+commenter
public class ProprietesCarte extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JButton Bt_Ok=new JButton("Ok");
	private JButton Bt_Annuler=new JButton("Annuler");
	public int status;
	private Projet.Carte carteSel;
	public ContenuSimple panel;
	private FenetreSimple parent;

	public ProprietesCarte(Projet.Carte Carte,Dialog owner, String titre , boolean modal,FenetreSimple p)
	{
		super(owner,titre,modal);
		parent=p;
		carteSel=Carte;
		setTitle("Propriétés de la carte");
		setSize(337, 420);
		Toolkit k = Toolkit.getDefaultToolkit();
		Dimension tailleEcran = k.getScreenSize();
		int largeurEcran = tailleEcran.width;
		int hauteurEcran = tailleEcran.height;
		setLocation((largeurEcran/2)-(337/2), (hauteurEcran/2)-(420/2));
		Container leContenant = getContentPane();
		panel=new ContenuSimple();
		leContenant.add(panel);
		Bt_Ok.addActionListener(
				 new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				// TODO @BETA Vérifier aussi que la carte n'existe pas déjà pour ajouter une carte
				if (panel.Ed_Nom.getText().compareTo("")==0)
					JOptionPane.showMessageDialog(null, "Il faut donner un nom a la carte.");
				else if (panel.Ed_Chipset.getText().compareTo("")==0)
					JOptionPane.showMessageDialog(null, "Il est obligatoire de choisir un chipset pour la carte");					
				else
				{
					Bt_Ok.removeActionListener(this);
					Bt_Ok.addActionListener(Bt_Annuler.getActionListeners()[0]);
					Bt_Ok.doClick();
				}
			}
		});

		Bt_Annuler.addActionListener(this);
		setVisible(true);		
	}
	class ContenuSimple extends JPanel
	{
		private static final long serialVersionUID = 1L;
		public JTextField Ed_Nom;
		public JComboBox Cb_Type;
		public JComboBox Cb_Effet;
		public JTextField Ed_Chipset;
		private JButton Bt_ChooseChipset;
		public JTextField Ed_Musique;
		private JButton Bt_ChooseMusique;
		public JTextField Ed_Fond;
		private JButton Bt_ChooseFond;
		public JCheckBox Check_DecRes;
		public JTextField Ed_X;
		public JTextField Ed_Y;

		public ContenuSimple()
		{ 
		   setLayout(null);
		   setResizable(false);
		   Ed_Nom = new JTextField();
		   Ed_Nom.setBounds(new Rectangle(64,8,225,21));
		   if (carteSel!=null) Ed_Nom.setText(carteSel.Name);
		   add(Ed_Nom);
		   Cb_Type = new JComboBox(new String[] { "Pas d'attaque", "Attaque possible"});
		   Cb_Type.setBounds(new Rectangle(64,40,225,21));
		   if (carteSel!=null) Cb_Type.setSelectedIndex(carteSel.TypeCarte);
		   add(Cb_Type);
		   Cb_Effet = new JComboBox(new String[] { "Aucun","Grotte sombre","Nuit","Chaleur","Pluie","Neige","Brouillard"});
		   Cb_Effet.setBounds(new Rectangle(64,72,225,21));
		   if (carteSel!=null) Cb_Effet.setSelectedIndex(carteSel.Effect);
		   add(Cb_Effet);
		   Ed_Chipset = new JTextField();
		   Ed_Chipset.setBounds(new Rectangle(64,104,207,21));
		   if (carteSel!=null) Ed_Chipset.setText(carteSel.Chipset);
		   add(Ed_Chipset);
		   Bt_ChooseChipset = new JButton("...");
		   Bt_ChooseChipset.setBounds(new Rectangle(271,104,18,20));
		   Bt_ChooseChipset.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent e)
						{
						     JFileChooser choix = new JFileChooser();
						     choix.addChoosableFileFilter(parent.new FilterImage()); 
						     choix.setCurrentDirectory(new java.io.File(parent.NomCarte+"/Chipset"));
						     
						     int retour = choix.showOpenDialog(null);
						     if(retour == JFileChooser.APPROVE_OPTION) 
						     {
						    	 if (!new File(parent.NomCarte+"/Chipset/"+choix.getSelectedFile().getName()).exists())
						    	   parent.copyfile(choix.getSelectedFile().getAbsolutePath(),parent.NomCarte+"/Chipset/"+choix.getSelectedFile().getName());
						    	 Ed_Chipset.setText("Chipset\\"+choix.getSelectedFile().getName());
						     }
						}
					});
		   add(Bt_ChooseChipset);
		   Ed_Musique = new JTextField();
		   Ed_Musique.setBounds(new Rectangle(64,136,207,21));
		   if (carteSel!=null) Ed_Musique.setText(carteSel.Music);
		   add(Ed_Musique);
		   Bt_ChooseMusique = new JButton("...");
		   Bt_ChooseMusique.setBounds(new Rectangle(271,136,18,20));
		   Bt_ChooseMusique.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent e)
						{
						     JFileChooser choix = new JFileChooser();
						     choix.addChoosableFileFilter(parent.new FilterMusic()); 
						     choix.setCurrentDirectory(new java.io.File(parent.NomCarte+"/Sound"));

						     int retour = choix.showOpenDialog(null);
						     if(retour == JFileChooser.APPROVE_OPTION) 
						     {
						    	 if (!new File(parent.NomCarte+"/Sound/"+choix.getSelectedFile().getName()).exists())
							    	   parent.copyfile(choix.getSelectedFile().getAbsolutePath(),parent.NomCarte+"/Sound/"+choix.getSelectedFile().getName());
						    	 Ed_Musique.setText("Sound\\"+choix.getSelectedFile().getName());
						     }
						}
					});
		   add(Bt_ChooseMusique);
		   Ed_Fond = new JTextField();
		   Ed_Fond.setBounds(new Rectangle(64,168,207,21));
		   if (carteSel!=null) Ed_Fond.setText(carteSel.Static);
		   add(Ed_Fond);
		   Bt_ChooseFond = new JButton("...");
		   Bt_ChooseFond.setBounds(new Rectangle(271,168,18,20));
		   Bt_ChooseFond.addActionListener(
					new ActionListener(){
						public void actionPerformed(ActionEvent e)
						{
						     JFileChooser choix = new JFileChooser();
						     choix.addChoosableFileFilter(parent.new FilterImage()); 
						     choix.setCurrentDirectory(new java.io.File(parent.NomCarte+"/Chipset"));

						     int retour = choix.showOpenDialog(null);
						     if(retour == JFileChooser.APPROVE_OPTION) 
						     {
						    	 if (!new File(parent.NomCarte+"/Chipset/"+choix.getSelectedFile().getName()).exists())
							    	   parent.copyfile(choix.getSelectedFile().getAbsolutePath(),parent.NomCarte+"/Chipset/"+choix.getSelectedFile().getName());
						    	 Ed_Fond.setText("Chipset\\"+choix.getSelectedFile().getName());
						     }
						}
					});
		   add(Bt_ChooseFond);
		   Check_DecRes=new JCheckBox();
		   Check_DecRes.setBounds(new Rectangle(10,205,20,20));
		   if (carteSel!=null) Check_DecRes.setSelected(carteSel.DecToResPoint);
		   add(Check_DecRes);
		   Ed_X = new JTextField("10");
		   Ed_X.setBounds(new Rectangle(64,330,40,21));
		   if (carteSel!=null) Ed_X.setText(String.valueOf(carteSel.TailleX));
		   add(Ed_X);
		   Ed_Y = new JTextField("10");
		   Ed_Y.setBounds(new Rectangle(220,330,40,21));
		   if (carteSel!=null) Ed_Y.setText(String.valueOf(carteSel.TailleY));
		   add(Ed_Y);
		   Bt_Ok.setBounds(new Rectangle(30,362,90,25));
		   add(Bt_Ok);
		   Bt_Annuler.setBounds(new Rectangle(200,362,90,25));
		   add(Bt_Annuler);
		}
		
		public void paintComponent(Graphics g)
		{
		  super.paintComponent(g);
		  g.drawString("Nom : ", 6, 22);
		  g.drawString("Type : ", 6, 54);
		  g.drawString("Effet : ", 6, 86);
		  g.drawString("Chipset : ", 6, 118);
		  g.drawString("Musique : ", 6, 150);
		  g.drawString("Fond : ", 6, 182);
		  g.drawString("La déconnexion sur cette carte renvoi le joueur", 40, 220);
		  g.drawString("a son point de résurrection", 40,242);
		  //Si variable[]>=XX
		  //Serveur[]=Champs
		  g.drawString("Taille de la carte", 6, 320);		  
		  g.drawString("X : ", 34, 344);		  
		  g.drawString("Y : ", 195, 344);		  
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