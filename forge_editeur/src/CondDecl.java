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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import struct.Projet;

public class CondDecl extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JButton Bt_Ok,Bt_Annuler;
	private JTextField Ed_Resultat,Ed_NomVariable;
	JPanel panevariable;
	JComboBox CB_ChoixTypeDecl,CB_VarTypeDecl,CB_Operateur;
	JComboBox CB_Objet;
	public int status;
	public String Commande;
	
/**################################################################**/
	class Contenu extends JPanel
/**################################################################
 * Initialise le contenu de la fenêtre des conditions de déclenchement**/
	{
		private static final long serialVersionUID = 1L;
		CondDecl parent;		
		Projet projet;
		public Contenu(Projet prj,CondDecl p)
		{		  
		  parent=p;
		  projet=prj;
		  setLayout(null);
		  Font font = new Font("MS Sans Serif", Font.PLAIN, 10);
		  
		  CB_ChoixTypeDecl=new JComboBox(new String[] {"Appuie sur bouton","En contact","Attaque","Automatique","Auto une seul fois","Variable..."});
		  CB_ChoixTypeDecl.setFont(font);//Possibles conditions de déclenchement
		  CB_ChoixTypeDecl.setBounds(new Rectangle(105,5,155,20));
		  CB_ChoixTypeDecl.addActionListener(
				new ActionListener()
				{/**Si on a choisi "Variable...", affiche les possibilités avec les variables**/
					public void actionPerformed(ActionEvent e)
					{
						if (CB_ChoixTypeDecl.getSelectedIndex()==5)
							panevariable.setVisible(true);
						else
							panevariable.setVisible(false);
					}
				});
		  add(CB_ChoixTypeDecl);
		  
		  panevariable=new JPanel();
		  panevariable.setBounds(new Rectangle(5,30,325,100));
		  panevariable.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		  panevariable.setLayout(null);
		  add(panevariable);
		  panevariable.setVisible(false);
		  int statsize=projet.getStatsBase().size();
		  String[] values=new String[31+statsize];//Liste les variables relatives aux joueurs
		  values[0]="%Name%"; values[1]="%UpperName%"; values[2]="%Classe%"; values[3]="%Skin%"; values[4]="%Vie%"; values[5]="%VieMax%";
		  values[6]="%CurrentMag%"; values[7]="%MagMax%"; values[8]="%Gold%"; values[9]="%Lvl%"; values[10]="%LvlPoint%"; values[11]="%CurrentXP%";
		  values[12]="%NextXP%"; values[13]="%Timer%"; values[14]="%Timer2%"; values[15]="%Timer3%"; values[16]="%Effect%"; values[17]="%CaseX%";
		  values[18]="%CaseY%"; values[19]="%EvCaseX%"; values[20]="%EvCaseY%"; values[21]="%BloqueChangeSkin%"; values[22]="%Direction%";
		  values[23]="%Inventaire%"; values[24]="%NbObjetInventaire%"; values[25]="%Arme%"; values[26]="%Bouclier%"; values[27]="%Casque%"; values[28]="%Armure%";  
		  for (int i=0;i<statsize;i++)//Y ajoute les stats de base
			 values[29+i]="%"+projet.getStatsBase().get(i)+"%";
		  values[29+statsize]="%rand(...)%";//Le random
		  values[30+statsize]="Variable...";//Et les numéros de variables
		  
		  CB_VarTypeDecl=new JComboBox(values);
		  CB_VarTypeDecl.setBounds(new Rectangle(5,5,120,20));
		  CB_VarTypeDecl.setFont(font);
		  CB_VarTypeDecl.addActionListener(
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{//Si Variable ou rand selectionné, demande nom variable
							if ((CB_VarTypeDecl.getSelectedIndex()==InCombo(CB_VarTypeDecl,"Variable..."))
							|| (CB_VarTypeDecl.getSelectedIndex()==InCombo(CB_VarTypeDecl,"%rand(...)%")))
								Ed_NomVariable.setVisible(true);
							else 
								Ed_NomVariable.setVisible(false);
							if (CB_VarTypeDecl.getSelectedIndex()==InCombo(CB_VarTypeDecl,"%Inventaire%"))
							{//Si on a choisi Inventaire, montre les objets
								Ed_Resultat.setVisible(false);
								CB_Objet.setVisible(true);
							}
							else
							{
								Ed_Resultat.setVisible(true);
								CB_Objet.setVisible(false);
							}
						}
					});
		  panevariable.add(CB_VarTypeDecl);

		  CB_Operateur=new JComboBox(new String[] {">","=","!=","<",">=","<="});
		  CB_Operateur.setBounds(new Rectangle(130,5,60,20));
		  CB_Operateur.setFont(font);//Les opérateurs de comparaison
		  CB_Operateur.setSelectedIndex(1);
		  panevariable.add(CB_Operateur);
		  
		  Ed_Resultat=new JTextField();
		  Ed_Resultat.setBounds(new Rectangle(195,5,120,20));
		  Ed_Resultat.setFont(font);
		  panevariable.add(Ed_Resultat);
		  
		  values=new String[projet.getObjets().size()];
		  for(int i=0;i<projet.getObjets().size();i++)//Liste les objets
			  values[i]=projet.getObjetByIndex(i).Name;
		  CB_Objet=new JComboBox(values);
		  CB_Objet.setBounds(new Rectangle(195,5,120,20));
		  CB_Objet.setFont(font);
		  CB_Objet.setVisible(false);
		  panevariable.add(CB_Objet);

		  Ed_NomVariable=new JTextField();
		  Ed_NomVariable.setBounds(new Rectangle(5,30,120,20));
		  Ed_NomVariable.setFont(font);
		  Ed_NomVariable.setVisible(false);
		  panevariable.add(Ed_NomVariable);

		  JLabel Lbl_Texte=new JLabel("Dans le cas de l'inventaire, choisissez");
		  Lbl_Texte.setBounds(new Rectangle(130,30,200,15));
		  Lbl_Texte.setFont(font);
		  panevariable.add(Lbl_Texte);
		  Lbl_Texte=new JLabel("toujours le signe '=' ou '!='");
		  Lbl_Texte.setBounds(new Rectangle(130,45,200,15));
		  Lbl_Texte.setFont(font);
		  panevariable.add(Lbl_Texte);

		  Bt_Ok=new JButton("Ok");
		  Bt_Ok.setBounds(new Rectangle(81,140,90,20));
		  add(Bt_Ok);

	      Bt_Annuler=new JButton("Annuler");
		  Bt_Annuler.setBounds(new Rectangle(185,140,90,20));
		  add(Bt_Annuler);
		}
		public void paintComponent(Graphics g)
		{
		  super.paintComponent(g);
	      Graphics2D g2d = (Graphics2D)g;
		  g2d.drawString("Déclenchement : ", 6, 20);
		}
	}

	
	public CondDecl(Projet projet,String commande,Dialog owner,  boolean modal)
	{
		//Constructeur, crée le JDialog
	  super(owner,"Condition de déclenchement",modal);
	  Toolkit k = Toolkit.getDefaultToolkit();
	  Dimension tailleEcran = k.getScreenSize();
	  int largeurEcran = tailleEcran.width;
	  int hauteurEcran = tailleEcran.height;
	  setSize(350,200);
	  setLocation((largeurEcran/2)-175, (hauteurEcran/2)-100);
	  setLayout(new BorderLayout());
	  
	  Contenu monContenu=new Contenu(projet,this);
	  add(monContenu);

	  Bt_Ok.addActionListener(this);
	  Bt_Annuler.addActionListener(this);
	  Commande=commande;
	  LoadCommande();
	  setVisible(true);		
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource()==this.Bt_Ok)
		{/**Enregistre quand on appuie sur Ok et ferme**/
			status=1;
			SaveCommande();
		}
		else
			status=0;
		setVisible(false);
	}
	
/**################################################################**/
	private int InCombo(JComboBox cb,String chaine)
/**################################################################
 * Si chaine est dans le  ComboBox, renvoie l'index de l'élément, sinon renvoie -1**/
	{
		for(int i=0;i<cb.getItemCount();i++)
			if (cb.getItemAt(i).toString().compareTo(chaine)==0)
				return i;
		return -1;
	}
	
/**################################################################**/	
	public void LoadCommande()
/**################################################################
 * Charge les commandes texte dans le Wizard**/
	{
		int posop;
		String variable,op;
		if (Commande.compareTo("")!=0)
		{
			posop=InCombo(CB_ChoixTypeDecl,Commande);
			if (posop>=0)
				CB_ChoixTypeDecl.setSelectedIndex(posop);
			else
			{
				CB_ChoixTypeDecl.setSelectedIndex(5);
				posop=Commande.indexOf("!="); op="!=";
				if (posop==-1) { posop=Commande.indexOf(">="); op =">="; }//Récupère la position de l'opérateur
				if (posop==-1) { posop=Commande.indexOf("<="); op ="<="; }
				if (posop==-1) { posop=Commande.indexOf(">"); op =">"; }
				if (posop==-1) { posop=Commande.indexOf("<"); op ="<"; }
				if (posop==-1) { posop=Commande.indexOf("="); op ="="; }
				if (posop!=-1)
				{//S'il y a un opérateur de comparaison dans la ligne
					variable=Commande.substring(0,posop);//Partie gauche
					CB_Operateur.setSelectedIndex(InCombo(CB_Operateur,op));//Sélectionne le bon opérateur
					Ed_Resultat.setText(Commande.substring(posop+op.length()));//Met ce qu'il y a sa droite
					posop=InCombo(CB_VarTypeDecl,variable);//dans case droite et récup la pos de gauche
					if (posop>=0)
					{//Si la partie gauche fait partie de la liste
						CB_VarTypeDecl.setSelectedIndex(posop);//On sélectionne dans la liste
						if (CB_VarTypeDecl.getItemAt(CB_VarTypeDecl.getSelectedIndex()).toString().compareTo("%Inventaire%")==0)
						{//Si c'est %Inventaire%, on sélectionne dans la liste d'objets
							posop=InCombo(CB_Objet,Ed_Resultat.getText());
							if (posop>=0)
								CB_Objet.setSelectedIndex(posop);
						}
					}
					else
					{
						if (variable.startsWith("%rand("))
						{//Si rand, on récup la valeur
							CB_VarTypeDecl.setSelectedIndex(InCombo(CB_VarTypeDecl,"%rand(...)%"));
							variable=variable.substring(variable.indexOf("(")+1);
							if (variable.indexOf(")%")>=0)
								variable=variable.substring(0,variable.lastIndexOf(")%"));
							Ed_NomVariable.setText(variable);
						}
						else if (variable.startsWith("Variable"))
						{//Si Variable, on récupère le numéro de la variable
							CB_VarTypeDecl.setSelectedIndex(InCombo(CB_VarTypeDecl,"Variable..."));
							variable=variable.substring(variable.indexOf("[")+1);
							if (variable.indexOf("]")>=0)
								variable=variable.substring(0,variable.lastIndexOf("]"));
							Ed_NomVariable.setText(variable);
						}
					}
				}
			}
		}
	}
	
/**################################################################**/
	public void SaveCommande()
/**################################################################
 * Enregistre les commandes du Wizard vers texte**/
	{
		if (CB_ChoixTypeDecl.getItemAt(CB_ChoixTypeDecl.getSelectedIndex()).toString().compareTo("Variable...")==0)
		{
			if (CB_VarTypeDecl.getItemAt(CB_VarTypeDecl.getSelectedIndex()).toString().compareTo("%Inventaire%")==0)
				Commande="%Inventaire%"+CB_Operateur.getItemAt(CB_Operateur.getSelectedIndex()).toString()
						+CB_Objet.getItemAt(CB_Objet.getSelectedIndex()).toString();
			//Si Inventaire, met le texte de l'objet  etc
			else if (CB_VarTypeDecl.getItemAt(CB_VarTypeDecl.getSelectedIndex()).toString().compareTo("%rand(...)%")==0)
					Commande="%rand("+Ed_NomVariable.getText()+")%"
							+CB_Operateur.getItemAt(CB_Operateur.getSelectedIndex()).toString()
							+Ed_Resultat.getText();
			//Si rand, met le text du rand entre la balise etc
			else if (CB_VarTypeDecl.getItemAt(CB_VarTypeDecl.getSelectedIndex()).toString().compareTo("Variable...")==0)
				Commande="Variable["+Ed_NomVariable.getText()+"]"
						+CB_Operateur.getItemAt(CB_Operateur.getSelectedIndex()).toString()
						+Ed_Resultat.getText();
			//Si c'est une variable, met le num de la variable
			else
				Commande=CB_VarTypeDecl.getItemAt(CB_VarTypeDecl.getSelectedIndex()).toString()
				+CB_Operateur.getItemAt(CB_Operateur.getSelectedIndex()).toString()
				+Ed_Resultat.getText();
			//Sinon, met simplement le texte avec le texte et résultat
		}
		else
			Commande=CB_ChoixTypeDecl.getItemAt(CB_ChoixTypeDecl.getSelectedIndex()).toString();
		//Sinon, juste le texte
	}
}