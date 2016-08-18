import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

public class Importation extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JButton Bt_Ok=new JButton("Ok");
	private JButton Bt_Annuler=new JButton("Annuler");
	public int status;
	private FenetreSimple parent;
	public ContenuSimple panel;
	private JRadioButton RB_Tout,RB_Stats,RB_XP,RB_ClasseJoueur,RB_ClasseMonstre,RB_Objets,RB_Magies,RB_Monstres,RB_Cartes;
	private JRadioButton RB_Prj,RB_Csv;

	public Importation(FenetreSimple p,Dialog owner, String titre , boolean modal)
	{
		super(owner,titre,modal);
		parent=p;
		setTitle("Importation");
		setSize(337, 330);
		Toolkit k = Toolkit.getDefaultToolkit();
		Dimension tailleEcran = k.getScreenSize();
		int largeurEcran = tailleEcran.width;
		int hauteurEcran = tailleEcran.height;//Définit taille position etc
		setLocation((largeurEcran/2)-(337/2), (hauteurEcran/2)-(330/2));
		Container leContenant = getContentPane();
		panel=new ContenuSimple();
		leContenant.add(panel);
		Bt_Ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{//TODO @BETA2 Autres Importation
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
		   setLayout(new FlowLayout());
			JPanel panetypeimport=new JPanel();
			TitledBorder title = BorderFactory.createTitledBorder("Type d'importation");
			panetypeimport.setBorder(title);
			panetypeimport.setLayout(new GridLayout(0,1));
			add(panetypeimport);
			ButtonGroup group=new ButtonGroup();
			ActionListener ac=new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					if ((RB_Tout.isSelected())||(RB_Cartes.isSelected()))
						RB_Csv.setEnabled(false);
					else
						RB_Csv.setEnabled(true);
				}
		    };
			RB_Tout=new JRadioButton("Tout sauf les cartes");
			RB_Tout.addActionListener(ac);
			RB_Tout.setSelected(true);
			group.add(RB_Tout);
			panetypeimport.add(RB_Tout);
			RB_Stats=new JRadioButton("Les statistiques de base");
			RB_Stats.addActionListener(ac);
			group.add(RB_Stats);
			panetypeimport.add(RB_Stats);
			RB_XP=new JRadioButton("La courbe d'expérience");
			RB_XP.addActionListener(ac);
			group.add(RB_XP);
			panetypeimport.add(RB_XP);
			RB_ClasseJoueur=new JRadioButton("Les classes joueurs");
			RB_ClasseJoueur.addActionListener(ac);
			group.add(RB_ClasseJoueur);
			panetypeimport.add(RB_ClasseJoueur);
			RB_ClasseMonstre=new JRadioButton("Les classes monstres");
			RB_ClasseMonstre.addActionListener(ac);
			group.add(RB_ClasseMonstre);
			panetypeimport.add(RB_ClasseMonstre);
			RB_Objets=new JRadioButton("Les objets");
			RB_Objets.addActionListener(ac);
			group.add(RB_Objets);
			panetypeimport.add(RB_Objets);
			RB_Magies=new JRadioButton("Les magies");
			RB_Magies.addActionListener(ac);
			group.add(RB_Magies);
			panetypeimport.add(RB_Magies);
			RB_Monstres=new JRadioButton("Les monstres");
			RB_Monstres.addActionListener(ac);
			group.add(RB_Monstres);
			panetypeimport.add(RB_Monstres);
			RB_Cartes=new JRadioButton("Des cartes");
			RB_Cartes.addActionListener(ac);
			group.add(RB_Cartes);
			panetypeimport.add(RB_Cartes);
			JPanel paneformatimport=new JPanel();
			title = BorderFactory.createTitledBorder("Format");
			paneformatimport.setBorder(title);
			paneformatimport.setLayout(new GridLayout(0,1));
			add(paneformatimport);
			group=new ButtonGroup();
			RB_Prj=new JRadioButton("Importer d'un .prj");
			RB_Prj.setSelected(true);
			group.add(RB_Prj);
			paneformatimport.add(RB_Prj);
			RB_Csv=new JRadioButton("Importer d'un .csv");
			RB_Csv.setEnabled(false);
			group.add(RB_Csv);
			paneformatimport.add(RB_Csv);
			JPanel panelbas=new JPanel();
		   panelbas.setLayout(new FlowLayout( ));
		   panelbas.add(Bt_Ok);
		   panelbas.add(Bt_Annuler);
		   add(panelbas);
		}	
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource()==this.Bt_Ok)
			status=1;			
		else
			status=0;
		setVisible(false);
	} 
}