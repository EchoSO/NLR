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
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import struct.Projet;
import struct.Projet.Carte;

public class CommandeEv extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JButton Bt_Ok,Bt_Annuler;
	public JTextField Ed_Commande;
	public int status;
	VerifieSyntaxe verifie;
	
/** ###########################**/	
	class Contenu extends JPanel
/** ###########################
 * Gère le contenu de la fenêtre de commandes event**/
	{
		private static final long serialVersionUID = 1L;
		private JList ListCommande,ListEvent,ListJoueur;
		public JTextField Ed_Commande;
		JComboBox ListObj,ListMag;
		CommandeEv parent;		
		Projet projet;
	    String[] values;
		JListe liste;
		CondDecl cd;
		JumpTo jump;

/**################################################################**/
		public Contenu(Projet prj,CommandeEv p,JTextField Ed_C)
/**################################################################
 * Définit le contenu de la fenêtre CommandeEv**/
		{		  
		  parent=p;
		  projet=prj;
		  Ed_Commande=Ed_C;//Liste des évenements possibles
		  setLayout(null);
		  //	      Font font = new Font("MS Sans Serif", Font.PLAIN, 10);
	      ListCommande=new JList(new String[] {"Message('Entrez votre message retour a la ligne automatique')","Condition('Appuie sur bouton')",
	    		  "AddObject(NomObjet)","DelObject(NomObjet)","Teleport(NomCarte,CaseX,CaseY)","ChangeResPoint(NomCarte,CaseX,CaseY)","SScroll(CaseX,CaseY)",
	    		  "ChangeClasse('Classe')","ChangeSkin('Chipset\\skin.png')","GenereMonstre(NomMonstre,CaseX,CaseY,NbMonstre,Respawn,DonneXP)","TueMonstre",
	    		  "InputQuery('Voulez vous dormir ici?','oui','non','5 choix possible','vide=inutilise')","OnResultQuery('oui')","QueryEnd","InputString('Entrez votre texte ici')",
	    		  "Magasin('Bonjour,que puis je faire pour vous?','Objet1','Objet2')","Attente(Temps)","PlayMusic('Sound\\nom.mid')","StopMusic",
	    		  "PlaySound('Sound\\sound.wav')","ChAttaqueSound('Sound\\sound.wav')","ChBlesseSound('Sound\\sound.wav')","AddMagie(NomMagie)","DelMagie(NomMagie)","Concat('Chaine')","// Commentaires",
	    		  "Chargement('nom')","Sauvegarde('nom')","Quitter()","Options()","ShowInterface","HideInterface","AddMenu(Menu)","DelMenu(Menu)"});
	      JScrollPane lc = new JScrollPane(ListCommande);
	      lc.setBounds(new Rectangle(6,22,205,280));//Rend scrollable
		  ListCommande.addMouseListener(new MouseAdapter() 
		  {
			  public void mousePressed(MouseEvent e) 
			  {
				  if (e.getClickCount() == 2)//Si double clic
				  {
					  String S,S2,S3,Temp;
					  int i;
					  String[] projstr;
					  switch(ListCommande.getSelectedIndex())
					  {//On teste les élements de la liste de comande
					    case 0 : //Message()
					    case 12 : //OnResultQuery()
							S = JOptionPane.showInputDialog(null, "Entrez le message", 
									"Message", 1);
					      if (S!=null)
					      {
					    	  S="'"+S;
					        if (ListCommande.getSelectedIndex()==0)
					        {//Si Message()
						    	Temp="";
						    	if (JOptionPane.showConfirmDialog(null,
						                "Voulez vous positionner le message?",
						                "Option",
						                JOptionPane.YES_NO_OPTION,
						                JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)
						    	{
									 jump=new JumpTo(null,true);
									 Temp=jump.Ed_X.getText()+","+jump.Ed_Y.getText();
									 jump.dispose();
									 jump=new JumpTo(null,false);
									 jump.setVisible(false);
									 jump.setModal(true);//Positionner le msg sur la map
									 jump.setTitle("Largeur/Hauteur");
									 jump.LblX.setText("W");
									 jump.LblY.setText("H");
									 jump.setVisible(true);
									 Temp+=","+jump.Ed_X.getText()+","+jump.Ed_Y.getText()+",";
									 jump.dispose();
									 S=Temp+S;
						    	}
					          Ed_Commande.setText("Message("+S+"')");
					        }
					        if (ListCommande.getSelectedIndex()==12)
					          Ed_Commande.setText("OnResultQuery('"+S+"')");
					      }
					      break;
					    case 1 : // Condition()
			    		  cd=new CondDecl(projet,"",null,true);
			    		  if (cd.status==1)//Si cliqué "Ok"
					          Ed_Commande.setText("Condition('"+cd.Commande+"')");
					      cd.dispose();
					      break;					    
					    case 2 ://AddObject()
					    case 3 ://DelObject()
						  values=new String[projet.getObjets().size()];
						  for(int j=0;j<projet.getObjets().size();j++)
							  values[j]=projet.getObjetByIndex(j).Name;
					      liste=new JListe(values,null,"Choisissez l'objet",true);
						  if (liste.status==1 && liste.ListBox.getSelectedValue() !=null)
						  {
					          S="1";
							  S = (String) JOptionPane.showInputDialog(null, "Entrez la quantité de l'objet", 
										"Quantité", 1,null,null,S);
							  if (S!=null)
							  {
								if (S.compareTo("")!=0)
								{//Choisi la quantité
									if (S.compareTo("1")==0)
									{
							            if (ListCommande.getSelectedIndex()==2)//Vérif si Add ou Del
							              Ed_Commande.setText("AddObject("+liste.ListBox.getSelectedValue().toString()+")");
							            else
								          Ed_Commande.setText("DelObject("+liste.ListBox.getSelectedValue().toString()+")");
									}//Si !=1, la spécifie explicitement
									else
									{
							            if (ListCommande.getSelectedIndex()==2)//Vérif si Add ou Del
							              Ed_Commande.setText("AddObject("+liste.ListBox.getSelectedValue().toString()+","+S+")");
							            else
								          Ed_Commande.setText("DelObject("+liste.ListBox.getSelectedValue().toString()+","+S+")");
									}
								}
							  }
						  }
						  liste.dispose();
					      break;
					    case 4 ://Teleport()
					    case 5 ://ChangeResPoint()
					    	ArrayList<Carte> carte=projet.getCartes();//Liste les cartes et leurs noms
					    	ArrayList<String> nomcarte=new ArrayList<String>();
					    	for(i=0;i<carte.size();i++)
					    		nomcarte.add(carte.get(i).Name);
							projstr=new String[nomcarte.size()];
							projstr=nomcarte.toArray(projstr);
					    	liste=new JListe(projstr,null,"Choisissez la carte",true);
							 if (liste.status==1)//Demande de choisir la carte
							 {
								 jump=new JumpTo(null,true);//Et la coordonnée
								 if (jump.status==1 && liste.ListBox.getSelectedValue() !=null)
								 {
							            if (ListCommande.getSelectedIndex()==4)//Si Teleport
							              Ed_Commande.setText("Teleport("+liste.ListBox.getSelectedValue().toString()
							                                +","+jump.Ed_X.getText()+","+jump.Ed_Y.getText()+")");
							            else //Si ChangeResPoint
							              Ed_Commande.setText("ChangeResPoint("+liste.ListBox.getSelectedValue().toString()
							            		  			+","+jump.Ed_X.getText()+","+jump.Ed_Y.getText()+")");
									 
								 }
								 jump.dispose();//Ferme les fenêtres
							 }
						    liste.dispose();
					      break;
					    case 6 ://SScroll (screen scroll)
							 jump=new JumpTo(null,true);//Demande la coordonnée
							 if (jump.status==1)
					            Ed_Commande.setText("SScroll("+jump.Ed_X.getText()+","+jump.Ed_Y.getText()+")");
							 jump.dispose();
					         break;
					    case 7 ://ChangeClasse
					    	ArrayList<String> nomclasses=new ArrayList<String>();
					    	for(i=0;i<projet.getClassesJoueur().size();i++)
					    		nomclasses.add(projet.getClassesJoueur().get(i).Name);
							projstr=new String[nomclasses.size()];//Liste les classes et leur nom
							projstr=nomclasses.toArray(projstr);
					    	liste=new JListe(projstr,null,"Choisissez la classe(vide=aucune)",true);
							 if (liste.status==1 && liste.ListBox.getSelectedValue()!=null)//Et demande de choisir dans la liste
							 {
								 Ed_Commande.setText("ChangeClasse('"+liste.ListBox.getSelectedValue().toString()+"')");
							 }
							 break;
					    case 8 ://ChangeSkin
					    case 17 ://PlayMusic
					    case 19 ://PlaySound
					    case 20 ://ChAttaqueSound
					    case 21 ://ChBlesseSound
							 JFileChooser choix = new JFileChooser();
						     if (ListCommande.getSelectedIndex()==7)
								choix.setCurrentDirectory(new java.io.File(projet.getName()+"/Chipset/"));
						      else
								choix.setCurrentDirectory(new java.io.File(projet.getName()+"/Sound/"));
						     int retour = choix.showOpenDialog(null);//Liste les chipsets ou sons
							 if(retour == JFileChooser.APPROVE_OPTION)//Demande un choix 
							 {
								 switch(ListCommande.getSelectedIndex())
								 {
								 	case 8 :  Ed_Commande.setText("ChangeSkin('Chipset\\"+choix.getSelectedFile().getName()+"')"); break;
								 	case 17 : Ed_Commande.setText("PlayMusic('Sound\\"+choix.getSelectedFile().getName()+"')"); break;
								 	case 29 : Ed_Commande.setText("PlaySound('Sound\\"+choix.getSelectedFile().getName()+"')"); break;
								 	case 20 : Ed_Commande.setText("ChAttaqueSound('Sound\\"+choix.getSelectedFile().getName()+"')"); break;
								 	case 21 : Ed_Commande.setText("ChBlesseSound('Sound\\"+choix.getSelectedFile().getName()+"')"); break;
								 }
							 }
					      break;
					    case 9 ://GenereMonstre
						  values=new String[projet.getMonstres().size()];
						  for(int j=0;j<projet.getMonstres().size();j++)
							  values[j]=projet.getMonstreByIndex(j).Name;
					      liste=new JListe(values,null,"Choisissez le monstre",true);
						  if (liste.status==1)//Liste les monstre et propose la liste
						  {
							 jump=new JumpTo(null,true);//Demande la coordonnée
							 if (jump.status==1 && liste.ListBox.getSelectedValue() !=null)
							 {
								S = JOptionPane.showInputDialog(null, "Entrez le nombre de Monstre", 
										"Monstre", 1);
								if (S!=null)
								{//Si nombre choisi
									S2 = JOptionPane.showInputDialog(null, "Vitesse de respawn?(0=ne respawn pas)", 
											"Monstre", 1);
									if (S2!=null)
									{//Si vitesse choisie
										S3 = JOptionPane.showInputDialog(null, "Monstres donnent de l'xp? (0=non, 1=oui)", 
												"Monstre", 1);
										if (S3!=null)
										{//Si (XP==true) choisi..
							                Ed_Commande.setText("GenereMonstre("+liste.ListBox.getSelectedValue().toString()
							                                 +","+jump.Ed_X.getText()+","+jump.Ed_Y.getText()+","+S+","+S2+","+S3+")");
										}
									}
								}
					         }
							 jump.dispose();//Ferme les fenêtres
						  }
						  liste.dispose();
						  break;
					    case 11 ://InputQuery()
					    	Temp="";
					    	if (JOptionPane.showConfirmDialog(null,
					                "Voulez vous positionner le query?",
					                "Option",
					                JOptionPane.YES_NO_OPTION,
					                JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)//Demande si positionner
					    	{
								 jump=new JumpTo(null,true);//Demande coordonnée
								 if (jump.status==1)
									 Temp="InputQuery("+jump.Ed_X.getText()+","+jump.Ed_Y.getText();
								 jump.dispose();
					    	}
					    	S = JOptionPane.showInputDialog(null, "Entrez la question", 
									"Message", 1);//Demande la question
					      if (S!=null)
					      {
					        i=0;
					        if (Temp.compareTo("")==0)					        	
					        	Temp="InputQuery('"+S+"'";
					        else
					        	Temp+=",'"+S+"'";
					        do
					        {
					          S="";//Demande la réponse
					          S = JOptionPane.showInputDialog(null, "Entrez la réponse "+(i+1), 
										"Message", 1);
					          if (S==null)
					            S="";
					          if (S!="")
					            Temp+=",'"+S+"'";
					          i++;
					        }
					        while(S!="");//Demande tant qu'on donne des réponses
					        Temp+=")";
					        Ed_Commande.setText(Temp);//Ajoute
					      }
					      break;
					    case 14 ://InputString()
					    	S = JOptionPane.showInputDialog(null, "Entrez la question", 
									"Message", 1);
					      if (S!=null)
					        Ed_Commande.setText(Ed_Commande.getText()+"InputString('"+S+"')");					      
					      break;
					    case 15 ://Magasin()
					    	S = JOptionPane.showInputDialog(null, "Entrez le message du magasin", 
									"Message", 1);
					      if (S!=null)
					      {
							values=new String[projet.getObjets().size()];
							for(int j=0;j<projet.getObjets().size();j++)
								values[j]=projet.getObjetByIndex(j).Name;
						    liste=new JListe(values,null,"Choisissez les objets",true);
						    if (liste.status==1)//Liste les objets et propose de choisir
						    {					            
						    	Ed_Commande.setText("Magasin('"+S+"'");
						    	Object[] obj=liste.ListBox.getSelectedValues();
					            for (int j=0;j<obj.length;j++)
					                Ed_Commande.setText(Ed_Commande.getText()+",'"+obj[j].toString()+"'");
					            Ed_Commande.setText(Ed_Commande.getText()+")");
						    }
						    liste.dispose();
					      }
					      break;
					    case 16 ://Attente()
					    	S = JOptionPane.showInputDialog(null, "Entrez le temps d'attente", 
									"Timer", 1);
					      if (S!=null)
					        Ed_Commande.setText("Attente("+S+")");
					      break;
					    case 22 ://AddMagie()
					    case 23 ://DelMagie()
							values=new String[projet.getMagies().size()];
							for(int j=0;j<projet.getMagies().size();j++)
								values[j]=projet.getMagieByIndex(j).Name;
						    liste=new JListe(values,null,"Choisissez la magie",true);
						    if (liste.status==1 && liste.ListBox.getSelectedValue() !=null)//Liste les magies et demande un choix
						    {					            
					            if (ListCommande.getSelectedIndex()==22)
					            	Ed_Commande.setText("AddMagie("+liste.ListBox.getSelectedValue().toString()+")");
					            else
					            	Ed_Commande.setText("DelMagie("+liste.ListBox.getSelectedValue().toString()+")");
						    }
						    liste.dispose();
						    break;
					    case 26 ://Chargement()
					    case 27 ://Sauvegarde()
					      S = JOptionPane.showInputDialog(null, "Entrez le nom de la sauvegarde(Vide = Choix du joueur)", 
									"Sauvegarde", 1);
					      if (S!=null)
					      {
				            if (ListCommande.getSelectedIndex()==27)
					    	  Ed_Commande.setText("Chargement('"+S+"')");
				            else
					    	  Ed_Commande.setText("Sauvegarde('"+S+"')");
					      }
					      break;					    	
					    case 32 ://AddMenu()
					    case 33 ://DelMenu()
							projstr=new String[verifie.getMenuPossibles().size()];
							projstr=verifie.getMenuPossibles().toArray(projstr);
					    	liste=new JListe(projstr,null,"Choisissez le menu",true);
							 if (liste.status==1 && liste.ListBox.getSelectedValue() !=null)
							 {
					            if (ListCommande.getSelectedIndex()==32)
					            	Ed_Commande.setText("AddMenu("+liste.ListBox.getSelectedValue().toString()+")");
					            else
					            	Ed_Commande.setText("DelMenu("+liste.ListBox.getSelectedValue().toString()+")");
							 }
							 break;
					     default :
					    	Ed_Commande.setText(Ed_Commande.getText()+ListCommande.getSelectedValue().toString());
					  }					  
				  }
			  }
		  });
	      add(lc);	      
//Variables relatives aux évenements
	      ListEvent=new JList(new String[] {"%NomEv.Name%","%NomEv.CaseX%","%NomEv.CaseY%","%NomEv.CaseNBX%","%NomEv.CaseNBY%",
	    		  "%NomEv.Chipset%","%NomEv.Bloquant%","%NomEv.Transparent%","%NomEv.Visible%","%NomEv.TypeAnim%","%NomEv.Direction%",
	    		  "%NomEv.X%","%NomEv.Y%","%NomEv.W%","%NomEv.H%","%NomEv.NumAnim%","%NomEv.Vitesse%","%NomEv.AnimAttaque%",
	    		  "%NomEv.AnimDefense%","%NomEv.AnimMagie%"});
	      JScrollPane le = new JScrollPane(ListEvent);
	      le.setBounds(new Rectangle(220,22,170,208));
		  ListEvent.addMouseListener(new MouseAdapter() {
			  public void mousePressed(MouseEvent e) 
			  {
				  if (e.getClickCount() == 2)
				  {
					  String S;/**Demande le nom de l'événement et remplace NomEv dans la commande**/
				    	S = JOptionPane.showInputDialog(null, "Entrez le nom de l'événement", 
								"Evénement", 1);
				      if (S!=null)
				    	  Ed_Commande.setText(ListEvent.getSelectedValue().toString().replaceAll("NomEv", S)+"=");
				  }
			  }
		  });
	      add(le);	      
		  values=new String[projet.getObjets().size()];
		  for(int j=0;j<projet.getObjets().size();j++)
			  values[j]=projet.getObjetByIndex(j).Name;
	      ListObj=new JComboBox(values);//La liste des objets
	      ListObj.setBounds(new Rectangle(220,248,170,20));		  
		  ListObj.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{/**crée la commande quand on choisit l'objet**/
					Ed_Commande.setText(Ed_Commande.getText()+projet.getObjetByIndex(ListObj.getSelectedIndex()).Name);
				}
		  });
	      add(ListObj);
		  values=new String[projet.getMagies().size()];
		  for(int j=0;j<projet.getMagies().size();j++)
			  values[j]=projet.getMagieByIndex(j).Name;
	      ListMag=new JComboBox(values);//La liste des magies
	      ListMag.setBounds(new Rectangle(220,283,170,20));
		  ListMag.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{/**crée la commande quand on choisit la magie**/
					Ed_Commande.setText(Ed_Commande.getText()+projet.getMagieByIndex(ListMag.getSelectedIndex()).Name);
				}
		  });
	      add(ListMag);//Variables relatives au joueur
	      values=new String[35+projet.getStatsBase().size()];
	      values[0]="%Name%"; values[1]="%UpperName%"; values[2]="%Classe%";
	      values[3]="%Skin%"; values[4]="%Vie%"; values[5]="%VieMax%";
	      values[6]="%CurrentMag%"; values[7]="%MagMax%"; values[8]="%Gold%";
	      values[9]="%Lvl%"; values[10]="%LvlPoint%"; values[11]="%CurrentXP%";
	      values[12]="%NextXP%"; values[13]="%Timer%"; values[14]="%Timer2%";
	      values[15]="%Timer3%"; values[16]="%Effect%"; values[17]="%upper(chaine)%";
	      values[18]="%rand(100)%"; values[19]="%max(valeur1,valeur2)%";
	      values[20]="%min(valeur1,valeur2)%"; values[21]="%Visible%";
	      values[22]="%Bloque%"; values[23]="%CaseX%"; values[24]="%CaseY%";
	      values[25]="%Position%"; values[26]="%CentreX%"; values[27]="%CentreY%";
	      values[28]="%BloqueChangeSkin%"; values[29]="%BloqueAttaque%";
	      values[30]="%BloqueDefense%"; values[31]="%BloqueMagie%";
	      values[32]="%NbObjetInventaire%"; values[33]="%Direction%";
	      for(int i=0;i<projet.getStatsBase().size();i++)
	    	  values[34+i]="%"+projet.getStatsBase().get(i)+"%";//Ajoute les stats de baase dans les var joueur
	      values[34+projet.getStatsBase().size()]="Variable[nomvar]";
	      ListJoueur=new JList(values);
	      JScrollPane lj = new JScrollPane(ListJoueur);
	      lj.setBounds(new Rectangle(400,22,150,280));
		  ListJoueur.addMouseListener(new MouseAdapter() {
			  public void mousePressed(MouseEvent e) {
				  if (e.getClickCount() == 2)
				  {/**Quand on double-clique, ajoute à la fin du texte**/
				    	Ed_Commande.setText(Ed_Commande.getText()+ListJoueur.getSelectedValue().toString());
				  }
			  }
		  });
	      add(lj);//Ajoute la liste à la fenêtre
	      
	      Ed_Commande.setBounds(new Rectangle(6,320,545,20));
		  Ed_Commande.addKeyListener(new KeyListener() 
		  {
			    public void keyTyped(KeyEvent keyEvent)  {}
				public void keyPressed(KeyEvent e) 
				{/**Enter= Bouton OK**/
					if (e.getKeyCode()==KeyEvent.VK_ENTER)
						Bt_Ok.doClick();
				}
				public void keyReleased(KeyEvent e) {
				}
		  });
	      add(Ed_Commande);

	      Bt_Ok=new JButton("Ok");
		  Bt_Ok.setBounds(new Rectangle(6,345,90,20));
		  add(Bt_Ok);

	      Bt_Annuler=new JButton("Annuler");
		  Bt_Annuler.setBounds(new Rectangle(100,345,90,20));//Boutons
		  add(Bt_Annuler);
		}
		
/**################################################################**/
		public void paintComponent(Graphics g)
/**################################################################
 * Dessine les textes de la fenetre**/
		{
		  super.paintComponent(g);
	      Graphics2D g2d = (Graphics2D)g;
	    
	      Font font = new Font("MS Sans Serif", Font.PLAIN, 10);
	    
	      g2d.setFont(font);
		  g2d.drawString("Liste des commandes : ", 6, 20);
		  g2d.drawString("Variables relatives aux événements : ", 220, 20);
		  g2d.drawString("Variables relatives au joueur : ", 400, 20);
		  g2d.drawString("Liste des objets : ", 220, 243);//Textes de la fenêtre
		  g2d.drawString("Liste des magies : ", 220, 278);
		  g2d.drawString("Commande : ", 6, 315);
		}
	}

/**################################################################**/
	public CommandeEv(Projet projet,String commande,Dialog owner,  boolean modal)
/**################################################################
 * Constructeur, crée une fenêtre CommandEv**/
	{
	  super(owner,"Commande événement",modal);//Appelle le constructeur JDialog
	  Toolkit k = Toolkit.getDefaultToolkit();
	  Dimension tailleEcran = k.getScreenSize();
	  int largeurEcran = tailleEcran.width;
	  int hauteurEcran = tailleEcran.height;
	  setSize(576,405);
	  setLocation((largeurEcran/2)-288, (hauteurEcran/2)-199);
	  setLayout(new BorderLayout());
	  verifie=new VerifieSyntaxe(projet);//Définit la taille de la fenêtre etc, vérif la syntaxe
      Ed_Commande=new JTextField();
	  Contenu monContenu=new Contenu(projet,this,Ed_Commande);
	  monContenu.Ed_Commande.setText(commande);
	  add(monContenu);

	  Bt_Ok.addActionListener(this);
	  Bt_Annuler.addActionListener(this);
	  setVisible(true);		
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource()==this.Bt_Ok)//status de la fenetre pour voir si OK ou annuler
			status=1;			
		else
			status=0;
		setVisible(false);
	} 
}