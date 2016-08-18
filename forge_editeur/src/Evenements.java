import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import forge_client.ImageLoader;

import struct.Projet;


public class Evenements extends JFrame
{
	private static final long serialVersionUID = 1L;
	JTabbedPane Page;
	
	class Contenu extends JPanel
	{/**Gère le contenu de la fenêtre évenement**/
		private static final long serialVersionUID = 1L;
		Evenements parent;
		JTextField Ed_Nom,Ed_Vitesse;
		JCheckBox CB_EvSuisSprite,CB_Visible,CB_Bloque,CB_Transparent,CB_AvecCommande;
		JRadioButton RB_PosDyn,RB_PosHaut,RB_PosBas;
		JTextField Ed_Chipset,Ed_NumAnim,Ed_X,Ed_Y,Ed_W,Ed_H,Ed_SiDroite,Ed_SiBas,Ed_Arrete;
		JLabel paneimg;
		JRadioButton RB_TaFixe,RB_TaPFixe,RB_TaPMAl,RB_TaEffet,RB_TaSuiveur;
		JRadioButton RB_Bas,RB_Gauche,RB_Haut,RB_Droite;
		JPanel PEffetVisuel,PPNJSuiveur,PRegarderVers;
		JTextArea LB_CD;
		JCustomPane LB_CE;//Déclaration des boutons etc
		boolean LB_CEmodif;
		Projet projet;
		Projet.Evenements events;
		Projet.Carte carte;
		int oldpage,eventx,eventy;
		Image imchipset,imaffiche;
		VerifieSyntaxe verif;
		
/**################################################################**/
		private void LoadPage(int page)
/**################################################################
 * Charge les évenements dans les pages d'évenement**/
		{
			Ed_Nom.setText(events.evenement.get(page).Name);
			Ed_Chipset.setText(events.evenement.get(page).Chipset);
			Ed_X.setText(Integer.toString(events.evenement.get(page).X));
			Ed_Y.setText(Integer.toString(events.evenement.get(page).Y));
			Ed_W.setText(Integer.toString(events.evenement.get(page).W));//Remplit les champs textes
			Ed_H.setText(Integer.toString(events.evenement.get(page).H));
			Ed_NumAnim.setText(Integer.toString(events.evenement.get(page).NumAnim));
			Ed_SiBas.setText(Integer.toString(events.evenement.get(page).Direction));
			Ed_SiDroite.setText(Integer.toString(events.evenement.get(page).NumAnim));
			Ed_Arrete.setText(Integer.toString(events.evenement.get(page).Direction));
			CB_Visible.setSelected(events.evenement.get(page).Visible);
			CB_Bloque.setSelected(events.evenement.get(page).Bloquant);
			CB_Transparent.setSelected(events.evenement.get(page).Transparent);
			CB_EvSuisSprite.setSelected(events.evenement.get(page).EvSuisSprite);
			switch(events.evenement.get(page).TypeAnim)
			{//Sélectionne le bouton du type d'animation
				case 0 : RB_TaFixe.setSelected(true); break;
				case 1 : RB_TaPFixe.setSelected(true); break;
				case 2 : RB_TaPMAl.setSelected(true); break;
				case 3 : RB_TaEffet.setSelected(true); break;
				case 4 : RB_TaSuiveur.setSelected(true); break;
			}
			AffichePanelTypeAnim();
			switch(events.evenement.get(page).Direction)
			{//Sélectionne le bouton de la direction
				case 0 : RB_Haut.setSelected(true); break;
				case 1 : RB_Droite.setSelected(true); break;
				case 2 : RB_Bas.setSelected(true); break;
				case 3 : RB_Gauche.setSelected(true); break;
				default : RB_Haut.setSelected(true);
			}
			Ed_Vitesse.setText(Integer.toString(events.evenement.get(page).Vitesse));
			switch(events.evenement.get(page).Z)
			{//Sélectionne le bouton de la position (au dessous, dynamique, en dessous)
				case 0 : RB_PosDyn.setSelected(true); break;
				case 1 : RB_PosHaut.setSelected(true); break;
				case 2 : RB_PosBas.setSelected(true); break;
			}
			String texte="";
			for(int i=0;i<events.CondDecl.get(page).size();i++)
				texte+=events.CondDecl.get(page).get(i)+"\n";
			LB_CD.setText(texte);//Récupère les conditions de déclenchement et les met dans la case
			texte="";
			for(int i=0;i<events.CommandeEv.get(page).size();i++)
				texte+=events.CommandeEv.get(page).get(i)+"\n";
			LB_CE.setText(texte);//Récupère les commandes d'évenements et les met dans la case
			SyntaxHighlight();
		}
		
/**################################################################**/
		private void SavePage(int page)
/**################################################################
 * Enregistre les evenements depuis les champs de texte**/
		{
			events.evenement.get(page).Name=Ed_Nom.getText();
			events.evenement.get(page).Chipset=Ed_Chipset.getText();
			events.evenement.get(page).X=Integer.parseInt(Ed_X.getText());			
			events.evenement.get(page).Y=Integer.parseInt(Ed_Y.getText());//Récup les champs de texte
			events.evenement.get(page).W=(short) Integer.parseInt(Ed_W.getText());
			events.evenement.get(page).H=(short) Integer.parseInt(Ed_H.getText());
			events.evenement.get(page).Visible=CB_Visible.isSelected();
			events.evenement.get(page).Bloquant=CB_Bloque.isSelected();
			events.evenement.get(page).Transparent=CB_Transparent.isSelected();
			events.evenement.get(page).EvSuisSprite=CB_EvSuisSprite.isSelected();
			if (RB_TaFixe.isSelected()) events.evenement.get(page).TypeAnim=0;
			if (RB_TaPFixe.isSelected()) events.evenement.get(page).TypeAnim=1;
			if (RB_TaPMAl.isSelected()) events.evenement.get(page).TypeAnim=2;//Récup le type d'anim
			if (RB_TaEffet.isSelected()) events.evenement.get(page).TypeAnim=3;
			if (RB_TaSuiveur.isSelected()) events.evenement.get(page).TypeAnim=4;
			switch (events.evenement.get(page).TypeAnim)
			{
				case 0 :
				case 1 :
				case 2 ://Récup la direction et imge pour fixe, perso fixe et perso mv aléatoire
					if (RB_Haut.isSelected()) events.evenement.get(page).Direction=0;
					if (RB_Droite.isSelected()) events.evenement.get(page).Direction=1;
					if (RB_Bas.isSelected()) events.evenement.get(page).Direction=2;
					if (RB_Gauche.isSelected()) events.evenement.get(page).Direction=3;
					events.evenement.get(page).NumAnim=(short) Integer.parseInt(Ed_NumAnim.getText());
					break;
				case 3 ://Récup la direction sur le chipset pour les effets visuels
					events.evenement.get(page).Direction=(short) Integer.parseInt(Ed_SiBas.getText());
					events.evenement.get(page).NumAnim=(short) Integer.parseInt(Ed_SiDroite.getText());
					break;
				case 4 ://Récup pour "suiveur"
					events.evenement.get(page).Direction=(short) Integer.parseInt(Ed_Arrete.getText());
					events.evenement.get(page).NumAnim=(short) Integer.parseInt(Ed_NumAnim.getText());
			}

			events.evenement.get(page).Vitesse=(short) Integer.parseInt(Ed_Vitesse.getText());
			if (RB_PosDyn.isSelected()) events.evenement.get(page).Z=0;//Récup vitesse et position relative
			if (RB_PosHaut.isSelected()) events.evenement.get(page).Z=1;
			if (RB_PosBas.isSelected()) events.evenement.get(page).Z=2;
			
			String[] textlbcd=LB_CD.getText().split("\n");
			events.CondDecl.get(page).clear();
			for(int i=0;i<textlbcd.length;i++)//Récup les condition de déclenchement
				events.CondDecl.get(page).add(textlbcd[i]);

			String[] textlbce=LB_CE.getText().split("\n");
			events.CommandeEv.get(page).clear();//Récup les commandes évenement
			for(int i=0;i<textlbce.length;i++)
				events.CommandeEv.get(page).add(textlbce[i]);
		}
/**################################################################**/
		private void LoadImage()
/**################################################################
 * Affiche l'image à partir du texte**/
		{
			if (Ed_Chipset.getText().compareTo("")!=0)
			{//S'il y a une image
	    		ImageLoader im=new ImageLoader(null);
	    		imchipset=im.loadImage(System.getProperty("user.dir")+"/"+projet.getName()+"/"+Ed_Chipset.getText().replace("Chipset\\", "Chipset/"));
			}
			else
			{//S'il n'y a pas d'image
				imchipset=null;
				imaffiche=null;
			}
    		RefreshImage();
		}
		
/**################################################################**/
		private void RefreshImage()
/**################################################################
 * Dessine l'image d'event à partir des coordonnées, directions etc**/
		{
//			Graphics g=paneimg.getGraphics();
//			g.clearRect(0, 0, paneimg.getWidth(), paneimg.getHeight());
			if (imchipset!=null)
			{//S'il y a une image
				int Direction=0;
				if ((PEffetVisuel.isVisible()==false)&&(PPNJSuiveur.isVisible()==false))
				{
					if (RB_Haut.isSelected()) Direction=0;
					if (RB_Droite.isSelected()) Direction=1;
					if (RB_Bas.isSelected()) Direction=2;//récup la direction
					if (RB_Gauche.isSelected()) Direction=3;
				}
				if (PPNJSuiveur.isVisible()==true)
					Direction=2;
				ImageFilter cif2;
				if (PEffetVisuel.isVisible()==false)//Si effet visuel, calcul image
					cif2=new CropImageFilter(Integer.parseInt(Ed_X.getText())+(Integer.parseInt(Ed_NumAnim.getText())*Integer.parseInt(Ed_W.getText())),
										Integer.parseInt(Ed_Y.getText())+(Direction*Integer.parseInt(Ed_H.getText())),
										Integer.parseInt(Ed_W.getText()),Integer.parseInt(Ed_H.getText()));
				else //Sinon calcule l'image avec l'orientation etc 
					cif2=new CropImageFilter(Integer.parseInt(Ed_X.getText()),
							Integer.parseInt(Ed_Y.getText())+(Direction*Integer.parseInt(Ed_H.getText())),
							Integer.parseInt(Ed_W.getText()),Integer.parseInt(Ed_H.getText()));
		    	imaffiche = createImage(new FilteredImageSource(imchipset.getSource(), cif2));
		    	paneimg.setIcon(new ImageIcon(imaffiche.getScaledInstance(Integer.parseInt(Ed_W.getText())*2, Integer.parseInt(Ed_H.getText())*2, Image.SCALE_SMOOTH)));
//		    	g.drawImage(imaffiche,0,0,Integer.parseInt(Ed_W.getText())*2,Integer.parseInt(Ed_H.getText())*2,parent);
			}
		}
		
/**################################################################**/
		private void AffichePanelTypeAnim()
/**################################################################
 * Affiche le panel avec les différents champs à remplir en fonction du type d'animation**/
		{
	  		  if ((RB_TaFixe.isSelected())||(RB_TaPFixe.isSelected())||(RB_TaPMAl.isSelected()))
			  {//Si fixe ou perso fixe ou perso mv aléatoire
				  PEffetVisuel.setVisible(false);
				  PPNJSuiveur.setVisible(false);
				  PRegarderVers.setVisible(true);
				  Ed_NumAnim.setVisible(true);//Affiche direction et numéro anim
			  }
			  else if (RB_TaEffet.isSelected())
			  {//Si effet
				  PEffetVisuel.setVisible(true);//Options d'effets visu
				  PPNJSuiveur.setVisible(false);
				  PRegarderVers.setVisible(false);			    			  
				  Ed_NumAnim.setVisible(false);
			  }
			  else if (RB_TaSuiveur.isSelected())
			  {//Si suiveur
				  PEffetVisuel.setVisible(false);
				  PPNJSuiveur.setVisible(true);//Affiche suiveur + numéro anim
				  PRegarderVers.setVisible(false);			    			  			    			  
				  Ed_NumAnim.setVisible(true);
			  }
			  RefreshImage();//Refresh l'image
		}
		
/**################################################################**/
		private void SyntaxHighlight()
/**################################################################
 * Affiche en rouge les erreurs de syntaxe des commandes évenements et colore/indente les commandes**/
		{
	  		  String ligne;
			  String[] lignes;
			  LB_CE.removeStyle("Red");//Enlève les couleurs qu'il y avait avant
			  LB_CE.removeStyle("Red Underline");LB_CE.removeStyle("Green");
			  LB_CE.removeStyle("Blue");LB_CE.removeStyle("Orange");
			  LB_CE.removeStyle("Mauve");LB_CE.removeStyle("Indent");
			  LB_CE.removeStyle("Indent2");
			  
			  Style style = LB_CE.addStyle("Red", null);
			  StyleConstants.setForeground(style, Color.red);//Rouge pour erreurs
			  
			  Style commentaire = LB_CE.addStyle("Green", null);
			  StyleConstants.setForeground(commentaire, new Color(0,150,75));//Vert pour commentaires
			  
			  Style fonc = LB_CE.addStyle("Blue", null);
			  StyleConstants.setForeground(fonc, new Color(0,0,150));//Bleu fonctions
			  
			  Style vari = LB_CE.addStyle("Orange", null);
			  StyleConstants.setForeground(vari, new Color(150,75,0));//Orange variables
			  
			  Style vari2 = LB_CE.addStyle("Mauve", null);
			  StyleConstants.setForeground(vari2, new Color(150,0,150));//Mauve variables[]
			  StyleConstants.setLeftIndent(vari2, 20);
			  
			  Style Indent = LB_CE.addStyle("Indent", null);//Indenté Condition()
			  StyleConstants.setLeftIndent(Indent, 16);
			  StyleConstants.setBold(Indent, true);
			  StyleConstants.setFontSize(Indent, 11);
			  
			  Style Indent2 = LB_CE.addStyle("Indent2", null);//Indenté Query
			  StyleConstants.setLeftIndent(Indent2, 10);
			  StyleConstants.setItalic(Indent2, true);
			  StyleConstants.setFontSize(Indent2, 10);
			  
			  style = LB_CE.addStyle("Red Underline", style);//Crée le style rouge souligné
			  StyleConstants.setUnderline(style, true);
			  
			  StyledDocument doc = LB_CE.getStyledDocument();
			  
			  
			  ligne=LB_CE.getText();
			  if (ligne.split("\n").length>0 && ligne.split("\n")[0].contains("Condition('")) ligne="//Condition:\n"+ligne;
			  if (ligne.indexOf("\r\n")>=0)
				  lignes=ligne.split("\r\n");//Récup toutes les lignes
			  else
				  lignes=ligne.split("\n");
			  LB_CE.setText("");
			   
			  

			  for(int i=lignes.length-1;i>=0;i--)
			  {
				  if (!lignes[i].equals("") || lignes.length>=2)//Vérifie la syntaxe TODO @BETA2 Améliorer vérif ex: Message(')
				  {//Si une seule ligne vide,on ne vérif pas
					  verif.Highlight(lignes[i],doc,style,commentaire,fonc,vari,vari2);
				  }/**Colore**/
			  }
			  
			  try 
			  {
				  //int d1,d2,d3=0;
				  int actuPos=0;
				  String txt=doc.getText(0, doc.getLength());
				  while(txt.indexOf("Condition('", actuPos) >=0)
				  {/**Indentation+Gras des Conditions**/
					  actuPos=txt.indexOf("Condition('", actuPos)+1;
					  doc.setParagraphAttributes(actuPos-1, 0, Indent, true );
					  
					  /*d1=actuPos-1;
					  d2=txt.indexOf("\n", d1);
					  if(d2<0) d2=txt.length();
					  System.out.println(d1+" : "+txt.substring(d1,d2));*/
				  }
				  actuPos=0;
				  while(txt.indexOf("OnResultQuery('", actuPos) >=0)
				  {/**Indentation+Gras des Conditions**/
					  actuPos=txt.indexOf("OnResultQuery('", actuPos)+1;
					  doc.setParagraphAttributes(actuPos-1, 1, Indent2, true );
				  }
				  actuPos=0;
				  while(txt.indexOf("QueryEnd", actuPos) >=0)
				  {/**Indentation+Gras des Conditions**/
					  actuPos=txt.indexOf("QueryEnd", actuPos)+1;
					  doc.setParagraphAttributes(actuPos-1, 1, Indent2, true );
				  }
			  } catch (BadLocationException e) {e.printStackTrace();}
			  
		}
		
/**################################################################**/
		private int getLineFromCarret(String[] lines,int pos)
/**################################################################
 * Retourne la ligne correspondante au caractère à la position "pos" **/
		{
			int taille=0;
			for(int i=0;i<lines.length;i++)
			{
				taille+=lines[i].length()+1;
				if (taille>pos)//Si on dépasse la position, renvoie la ligne
					return i;
			}
			return lines.length;//Sinon, renvoie la dernière ligne
		}
		
/**################################################################**/
		public class JCustomPane extends JTextPane
/**###############################################################
 * Un JTextPane personnalisé. Largeur non fixe => Scroll**/
		{
			private static final long serialVersionUID = -6799672938319152749L;
			public boolean getScrollableTracksViewportWidth()
			{//Pour qu'il soit scrollable à l'horizontale plutot que d'affiche sur plusieurs lignes
		       return false;//TODO @BETA : Un bouton pour afficher sans dépasser (return this.view)
			}
			public JCustomPane()
	    	{
	    		super();
	    		
	    	}		
		}
		
/**################################################################**/
		class JPaneImage extends JPanel
/**################################################################
 * Classe pour le panel en bas à gauche avec image et coordonnées, type d'anim etc**/
		{
			private static final long serialVersionUID = 1L;
			
			public JPaneImage()
			{/**Constructeur**/
				TitledBorder title = BorderFactory.createTitledBorder("Image");
			    Font font = new Font("MS Sans Serif", Font.PLAIN, 10);
				title.setTitleFont(font);
				setBorder(title);
				setLayout(null);
				  
				  verif=new VerifieSyntaxe(projet);//Crée le vérifieur de syntaxe

				  Ed_Chipset=new JTextField();
				  Ed_Chipset.setFont(font);
				  Ed_Chipset.setBounds(new Rectangle(10,15,83,17));
				  Ed_Chipset.setEditable(false);
				  add(Ed_Chipset);//Nom du chipset
				  
				  JButton Bt_AddChipset=new JButton("+");
				  Bt_AddChipset.setFont(font);
				  Bt_AddChipset.setBounds(new Rectangle(10,31,42,17));
				  Bt_AddChipset.setToolTipText("Ajouter une image a l'événement");
				  Bt_AddChipset.addActionListener(new ActionListener()
				  {
			    	  public void actionPerformed(ActionEvent e)
			          {/**Quand on clique sur le bouton, demande pour choisir un fichier et l'ajoute+ met l'img**/			    		  
					     JFileChooser choix = new JFileChooser();
					     choix.addChoosableFileFilter(new FilterImage()); 
					     choix.setCurrentDirectory(new java.io.File(projet.getName()+"/Chipset"));
					     
					     int retour = choix.showOpenDialog(null);
					     if(retour == JFileChooser.APPROVE_OPTION) 
					     {//Si un fichier choisi, on le copie si nécessaire
					    	 if (!new File(projet.getName()+"/Chipset/"+choix.getSelectedFile().getName()).exists())
					    	   copyfile(choix.getSelectedFile().getAbsolutePath(),projet.getName()+"/Chipset/"+choix.getSelectedFile().getName());
					    	 Ed_Chipset.setText("Chipset\\"+choix.getSelectedFile().getName());
					    	 LoadImage();//On met le texte et on charge l'image
					     }
			          }
				  });
				  add(Bt_AddChipset);
				  
				  JButton Bt_RmChipset=new JButton("-");
				  Bt_RmChipset.setFont(font);
				  Bt_RmChipset.setBounds(new Rectangle(51,31,42,17));
				  Bt_RmChipset.setToolTipText("Enlever l'image de l'événement");
				  Bt_RmChipset.addActionListener(new ActionListener()
				  {
			    	  public void actionPerformed(ActionEvent e)
			          {/**Enlève le chipset et actualise l'image**/
			    		 Ed_Chipset.setText("");
			    		 LoadImage();
			          }
				  });
				  add(Bt_RmChipset);
				  
				  paneimg=new JLabel();
				  paneimg.setBorder(BorderFactory.createLoweredBevelBorder());
				  paneimg.setLayout(null);//Positionnement
				  paneimg.setBounds(new Rectangle(10,48,83,145));
				  paneimg.setVerticalAlignment(JLabel.TOP);
				  paneimg.setHorizontalAlignment(JLabel.LEFT);
				  add(paneimg);

				  title = BorderFactory.createTitledBorder("Type d'animation");
				  title.setTitleFont(font);
				  JPanel pane2=new JPanel();//Choisis le type d'anim
				  pane2.setBorder(title);
				  pane2.setLayout(null);
				  pane2.setBounds(new Rectangle(104,10,249,89));
				  
				  ActionListener actypeanim=new ActionListener()
				  {
			    	  public void actionPerformed(ActionEvent e)
			          {/**Affiche le panel du type d'anim**/
			    		  AffichePanelTypeAnim();
			          }
				  };

				  ButtonGroup group=new ButtonGroup();
				  RB_TaFixe=new JRadioButton("Fixe");
				  RB_TaFixe.setFont(font);
				  RB_TaFixe.setBounds(new Rectangle(10,12,80,17));
				  RB_TaFixe.addActionListener(actypeanim);
				  group.add(RB_TaFixe);//Ajoute "Fixe" au groupe de boutons
				  pane2.add(RB_TaFixe);

				  RB_TaPFixe=new JRadioButton("Personnage fixe");
				  RB_TaPFixe.setFont(font);
				  RB_TaPFixe.setBounds(new Rectangle(10,29,120,17));
				  RB_TaPFixe.addActionListener(actypeanim);
				  group.add(RB_TaPFixe);//Ajoute "Perso fixe" au groupe de boutons
				  pane2.add(RB_TaPFixe);

				  RB_TaPMAl=new JRadioButton("Personnage mv aléatoire");
				  RB_TaPMAl.setFont(font);
				  RB_TaPMAl.setBounds(new Rectangle(10,46,150,17));
				  RB_TaPMAl.addActionListener(actypeanim);
				  group.add(RB_TaPMAl);//Ajoute "Perso mv aléatoire" au groupe de boutons
				  pane2.add(RB_TaPMAl);

				  RB_TaEffet=new JRadioButton("Effet visuel");
				  RB_TaEffet.setFont(font);
				  RB_TaEffet.setBounds(new Rectangle(10,63,100,17));
				  RB_TaEffet.addActionListener(actypeanim);
				  group.add(RB_TaEffet);//Ajoute "Effet visuel" au groupe de boutons
				  pane2.add(RB_TaEffet);
				  
				  RB_TaSuiveur=new JRadioButton("Suiveur");
				  RB_TaSuiveur.setFont(font);
				  RB_TaSuiveur.setBounds(new Rectangle(120,63,100,17));
				  RB_TaSuiveur.addActionListener(actypeanim);
				  group.add(RB_TaSuiveur);//Ajoute "Suiveur" au groupe de boutons
				  pane2.add(RB_TaSuiveur);

				  add(pane2);
				  
				  Ed_NumAnim=new JTextField();
				  Ed_NumAnim.setFont(font);//Numéro d'anim
				  Ed_NumAnim.setBounds(new Rectangle(170,125,33,17));
				  Ed_NumAnim.addKeyListener(new KeyListener() 
				  {/**Quand on modifie, raffraichit l'image.**/
					    public void keyTyped(KeyEvent keyEvent) {}
						public void keyPressed(KeyEvent keyEvent) {}
						public void keyReleased(KeyEvent e) 
						{
							RefreshImage();
						}
				  });
				  add(Ed_NumAnim);
				  
				  Ed_X=new JTextField();
				  Ed_X.setFont(font);//Position X
				  Ed_X.setBounds(new Rectangle(140,147,33,17));
				  Ed_X.addKeyListener(new KeyListener() 
				  {/**Quand on modifie, raffraichit l'image.**/
					    public void keyTyped(KeyEvent keyEvent) { }
						public void keyPressed(KeyEvent keyEvent) {	}
						public void keyReleased(KeyEvent e) 
						{
							RefreshImage();
						}
				  });
				  add(Ed_X);

				  Ed_Y=new JTextField();
				  Ed_Y.setFont(font);//Position Y
				  Ed_Y.setBounds(new Rectangle(220,147,33,17));
				  Ed_Y.addKeyListener(new KeyListener() 
				  {/**Quand on modifie, raffraichit l'image.**/
					    public void keyTyped(KeyEvent keyEvent) {}
						public void keyPressed(KeyEvent keyEvent) {}
						public void keyReleased(KeyEvent e) 
						{
							RefreshImage();
						}
				  });
				  add(Ed_Y);

				  Ed_W=new JTextField();
				  Ed_W.setFont(font);//Width/Largeur
				  Ed_W.setBounds(new Rectangle(140,169,33,17));
				  Ed_W.addKeyListener(new KeyListener() 
				  {/**Quand on modifie, raffraichit l'image.**/
					    public void keyTyped(KeyEvent keyEvent) {}
						public void keyPressed(KeyEvent keyEvent) {}
						public void keyReleased(KeyEvent e) 
						{
							RefreshImage();
						}
				  });
				  add(Ed_W);

				  Ed_H=new JTextField();
				  Ed_H.setFont(font);//Height/Largeur
				  Ed_H.setBounds(new Rectangle(220,169,33,17));
				  Ed_H.addKeyListener(new KeyListener() 
				  {/**Quand on modifie, raffraichit l'image.**/
					    public void keyTyped(KeyEvent keyEvent) { }
						public void keyPressed(KeyEvent keyEvent) {}
						public void keyReleased(KeyEvent e) 
						{
							RefreshImage();
						}
				  });
				  add(Ed_H);

				  title = BorderFactory.createTitledBorder("Regarde vers");
				  title.setTitleFont(font);
				  PRegarderVers=new JPanel();
				  PRegarderVers.setBorder(title);
				  PRegarderVers.setLayout(null);//Direction 
				  PRegarderVers.setBounds(new Rectangle(263,115,89,73));
				  ActionListener acregardervers=new ActionListener()
				  {
			    	  public void actionPerformed(ActionEvent e)
			    	  {/**Quand on modifie, raffraichit l'image.**/
			    		  RefreshImage();
			    	  }
				  };			  
				  RB_Gauche = new JRadioButton("<");
				  RB_Gauche.setFont(font);
				  RB_Gauche.setBounds(new Rectangle(16,32,33,17));
				  RB_Gauche.addActionListener(acregardervers);//Gauche
				  PRegarderVers.add(RB_Gauche);
				  RB_Droite = new JRadioButton(">");
				  RB_Droite.setFont(font);
				  RB_Droite.setBounds(new Rectangle(48,32,33,17));
				  RB_Droite.addActionListener(acregardervers);//Droite
				  PRegarderVers.add(RB_Droite);
				  RB_Haut = new JRadioButton("^");
				  RB_Haut.setFont(font);
				  RB_Haut.setBounds(new Rectangle(32,16,33,17));
				  RB_Haut.addActionListener(acregardervers);//Haut
				  PRegarderVers.add(RB_Haut);
				  RB_Bas = new JRadioButton("v");
				  RB_Bas.setFont(font);
				  RB_Bas.setBounds(new Rectangle(32,48,33,17));//Bas
				  RB_Bas.addActionListener(acregardervers);
				  PRegarderVers.add(RB_Bas);
				  group = new ButtonGroup();
				  group.add(RB_Gauche);
				  group.add(RB_Droite);
				  group.add(RB_Haut);//Les ajoute
				  group.add(RB_Bas);
				  
				  add(PRegarderVers);
				  
				  title = BorderFactory.createTitledBorder("Effet visuel");
				  title.setTitleFont(font);
				  PEffetVisuel=new JPanel();
				  PEffetVisuel.setBorder(title);
				  PEffetVisuel.setLayout(null);//Panel pour les effets  visuels
				  PEffetVisuel.setBounds(new Rectangle(104,100,249,94));
				  PEffetVisuel.setVisible(false);
				  JLabel Lbl_X=new JLabel("X : ");
				  Lbl_X.setFont(font);
				  Lbl_X.setBounds(new Rectangle(17,47,20,20));
				  PEffetVisuel.add(Lbl_X);
				  JLabel Lbl_Y=new JLabel("Y : ");
				  Lbl_Y.setFont(font);
				  Lbl_Y.setBounds(new Rectangle(97,47,20,20));
				  PEffetVisuel.add(Lbl_Y);
				  JLabel Lbl_W=new JLabel("W : ");//Labels pour effets visuels
				  Lbl_W.setFont(font);
				  Lbl_W.setBounds(new Rectangle(17,67,20,20));
				  PEffetVisuel.add(Lbl_W);
				  JLabel Lbl_H=new JLabel("H : ");
				  Lbl_H.setFont(font);
				  Lbl_H.setBounds(new Rectangle(97,67,20,20));
				  PEffetVisuel.add(Lbl_H);
				  JLabel Lbl_SiDroite=new JLabel("Si -> :");
				  Lbl_SiDroite.setFont(font);
				  Lbl_SiDroite.setBounds(new Rectangle(157,27,40,20));
				  PEffetVisuel.add(Lbl_SiDroite);
				  Ed_SiDroite=new JTextField();
				  Ed_SiDroite.setFont(font);
				  Ed_SiDroite.setBounds(new Rectangle(157,47,33,17));
				  PEffetVisuel.add(Ed_SiDroite);
				  JLabel Lbl_SiBas=new JLabel("Si bas :");
				  Lbl_SiBas.setFont(font);
				  Lbl_SiBas.setBounds(new Rectangle(197,27,40,20));
				  PEffetVisuel.add(Lbl_SiBas);
				  Ed_SiBas=new JTextField();
				  Ed_SiBas.setFont(font);
				  Ed_SiBas.setBounds(new Rectangle(197,47,33,17));
				  PEffetVisuel.add(Ed_SiBas);
				  add(PEffetVisuel);				  
				  title = BorderFactory.createTitledBorder("Personnage suiveur");
				  title.setTitleFont(font);
				  PPNJSuiveur=new JPanel();
				  PPNJSuiveur.setBorder(title);
				  PPNJSuiveur.setLayout(null);
				  PPNJSuiveur.setBounds(new Rectangle(104,100,249,94));
				  PPNJSuiveur.setVisible(false);
				  Lbl_X=new JLabel("X : ");
				  Lbl_X.setFont(font);
				  Lbl_X.setBounds(new Rectangle(17,47,20,20));//Labels pour perso suiveur
				  PPNJSuiveur.add(Lbl_X);
				  Lbl_Y=new JLabel("Y : ");
				  Lbl_Y.setFont(font);
				  Lbl_Y.setBounds(new Rectangle(97,47,20,20));
				  PPNJSuiveur.add(Lbl_Y);
				  Lbl_W=new JLabel("W : ");
				  Lbl_W.setFont(font);
				  Lbl_W.setBounds(new Rectangle(17,67,20,20));
				  PPNJSuiveur.add(Lbl_W);
				  Lbl_H=new JLabel("H : ");
				  Lbl_H.setFont(font);
				  Lbl_H.setBounds(new Rectangle(97,67,20,20));
				  PPNJSuiveur.add(Lbl_H);
				  JLabel Lbl_NumAnim=new JLabel("N° Anim :");
				  Lbl_NumAnim.setFont(font);
				  Lbl_NumAnim.setBounds(new Rectangle(16,21,50,20));
				  PPNJSuiveur.add(Lbl_NumAnim);
				  JLabel Lbl_Arrete=new JLabel("S'arrête à");
				  Lbl_Arrete.setFont(font);
				  Lbl_Arrete.setBounds(new Rectangle(157,16,80,20));
				  PPNJSuiveur.add(Lbl_Arrete);
				  Lbl_Arrete=new JLabel("X Cases du joueur");
				  Lbl_Arrete.setFont(font);
				  Lbl_Arrete.setBounds(new Rectangle(157,29,100,20));
				  PPNJSuiveur.add(Lbl_Arrete);
				  Ed_Arrete=new JTextField();
				  Ed_Arrete.setFont(font);//Texte  "s'arrête" pour suiveur
				  Ed_Arrete.setBounds(new Rectangle(177,57,33,17));
				  PPNJSuiveur.add(Ed_Arrete);
				  add(PPNJSuiveur);
			}
			
/**################################################################**/
			public void paintComponent(Graphics g)
/**################################################################
 * Dessine les textes devant les textfields**/
			{
			  super.paintComponent(g);
		      Graphics2D g2d = (Graphics2D)g;
		    
		      Font font = new Font("MS Sans Serif", Font.PLAIN, 10);
		    
		      g2d.setFont(font);
			  g2d.drawString("N° Anim : ", 120, 135);//Texte pour les choix par défaut
			  g2d.drawString("X : ", 120, 160);
			  g2d.drawString("Y : ", 200, 160);
			  g2d.drawString("W : ", 120, 180);
			  g2d.drawString("H : ", 200, 180);
			}
			
/**################################################################**/
			class FilterImage extends javax.swing.filechooser.FileFilter 
/**################################################################
 * Filtre pour les images pour sélectionner les .png**/
			{ 
			  public boolean accept(File file) 
			  { 
				if (file.isDirectory())
				      return true;
				String filename = file.getName(); 
				return filename.endsWith(".png"); 
			  } 
			  public String getDescription() 
			  { 
				  return "*.png"; 
			  } 
			} 
			
/**################################################################**/
		    private void copyfile(String srFile, String dtFile)
/**################################################################
 * Copie srFile en dtFile**/ //TODO @BETA2 utiliser celui de la classe principale ou classe utilitaires
		    {
			    try{
			      File f1 = new File(srFile);
			      File f2 = new File(dtFile);
			      InputStream in = new FileInputStream(f1);
			      
			      //For Append the file.
//				      OutputStream out = new FileOutputStream(f2,true);

			      //For Overwrite the file.
			      OutputStream out = new FileOutputStream(f2);

			      byte[] buf = new byte[1024];
			      int len;
			      while ((len = in.read(buf)) > 0){
			        out.write(buf, 0, len);
			      }
			      in.close();
			      out.close();
			      System.out.println("File copied.");
			    }
			    catch(FileNotFoundException ex){
			      System.out.println(ex.getMessage() + " in the specified directory.");
			      System.exit(0);
			    }
			    catch(IOException e){
			      System.out.println(e.getMessage());      
			    }
			}
		    
		    
		}
		
/**################################################################**/
		public Contenu(Evenements p,Projet prj,Projet.Carte ct,Projet.Evenements ev,int x,int y)
/**################################################################
 * Constructeur. Gère et initialise le contenu**/
		{		  
		  eventx=x;
		  eventy=y;
		  events=ev;
		  projet=prj;
		  carte=ct;
		  parent=p;
		  setLayout(null);
	      Font font = new Font("MS Sans Serif", Font.PLAIN, 10);
		  imchipset=null;
		  imaffiche=null;
	      
		  Ed_Nom=new JTextField();
		  Ed_Nom.setBounds(new Rectangle(130,8,121,21));
		  Ed_Nom.setFont(font);//Nom de l'event
		  add(Ed_Nom);

		  Ed_Vitesse=new JTextField();
		  Ed_Vitesse.setBounds(new Rectangle(78,30,33,21));
		  Ed_Vitesse.setFont(font);//Vitesse de l'event
		  add(Ed_Vitesse);

		  CB_EvSuisSprite=new JCheckBox("Suis le sprite");
		  CB_EvSuisSprite.setBounds(new Rectangle(130,30,121,17));
		  CB_EvSuisSprite.setFont(font);//Case à cocher "Suis le sprite"
		  add(CB_EvSuisSprite);
		  
		  CB_Visible=new JCheckBox("Visible");
		  CB_Visible.setBounds(new Rectangle(260,2,100,17));
		  CB_Visible.setFont(font);//Si l'évenement est visible ou non
		  add(CB_Visible);
		  
		  CB_Bloque=new JCheckBox("Bloque le joueur");
		  CB_Bloque.setBounds(new Rectangle(260,18,110,17));
		  CB_Bloque.setFont(font);//Si l'évenement est bloquant ou non
		  add(CB_Bloque);

		  CB_Transparent=new JCheckBox("Transparent");
		  CB_Transparent.setBounds(new Rectangle(260,34,100,17));
		  CB_Transparent.setFont(font);//S'il est transparent ou non
		  add(CB_Transparent);
		  
		  JPanel pane = new JPanel();
		  TitledBorder title;
		  title = BorderFactory.createTitledBorder("Condition de déclenchement");
		  title.setTitleFont(font);//Titre Condition de déclenchement
		  pane.setBorder(title);
		  pane.setLayout(null);
		  pane.setBounds(new Rectangle(4,50,361,212));
		  
		  JButton Bt_WzdCD=new JButton("Wizard");//Bouton Wizard des conditions de déclenchement
		  Bt_WzdCD.setFont(font);
		  Bt_WzdCD.setBounds(new Rectangle(10,15,70,17));
		  Bt_WzdCD.addActionListener(new ActionListener()
		  {
	    	  public void actionPerformed(ActionEvent e)
	          {/**Quand on clique sur le Wizard CD**/
	    		  CondDecl cd;
	    		  String[] textlbcd=LB_CD.getText().split("\n");//Récup les lignes
	    		  String s="";
	  			  int line=getLineFromCarret(textlbcd,LB_CD.getCaretPosition());
	  			  if (line==textlbcd.length)
	  				 cd=new CondDecl(projet,"",null,true);//Si dernière ligne, crée une nouvelle+conddecl
	  			  else
	  				 cd=new CondDecl(projet,textlbcd[line],null,true);//Sinon prend le texte dedans +conddecl
	    		  if (cd.status==1)
	    		  {//Si  cliqué sur Ok
		  			  if (line==textlbcd.length)
		  				  LB_CD.setText(LB_CD.getText()+cd.Commande+"\n");//Si ajout nouvelle ligne
		  			  else
		  			  {
		  				  textlbcd[line]=cd.Commande;
		  				  for(int i=0;i<textlbcd.length;i++)
		  				  	s+=textlbcd[i]+"\n";//Si édite une ligne
		  				  LB_CD.setText(s);
		  			  }
	    		  }
	    		  cd.dispose();//Ferme la fenetre conddecl
			  }
		  });
		  pane.add(Bt_WzdCD);
		  
		  LB_CD = new JTextArea(5, 20);
		  LB_CD.setFont(font);
		  JScrollPane scrollPane = new JScrollPane(LB_CD);//Rend scrollable
		  scrollPane.setBounds(new Rectangle(10,32,343,173));
		  pane.add(scrollPane);
		  
		  add(pane);

		  title = BorderFactory.createTitledBorder("Position");
		  title.setTitleFont(font);
		  pane=new JPanel();//Panel de la position (dynamique, tjr dessus, tjr dessous)
		  pane.setBorder(title);
		  pane.setLayout(null);
		  pane.setBounds(new Rectangle(4,260,361,35));
		  
		  ButtonGroup group = new ButtonGroup();
		  
		  RB_PosDyn=new JRadioButton("Dynamique");
		  RB_PosDyn.setFont(font);
		  RB_PosDyn.setBounds(new Rectangle(5,15,90,17));//Bouton Dynamique
		  pane.add(RB_PosDyn);
		  group.add(RB_PosDyn);
		  
		  RB_PosHaut=new JRadioButton("Toujours au dessus");
		  RB_PosHaut.setFont(font);
		  RB_PosHaut.setBounds(new Rectangle(100,15,120,17));//Bouton Dessus
		  pane.add(RB_PosHaut);
		  group.add(RB_PosHaut);

		  RB_PosBas=new JRadioButton("Toujours en dessous");
		  RB_PosBas.setFont(font);
		  RB_PosBas.setBounds(new Rectangle(230,15,125,17));//Bouton Dessous
		  pane.add(RB_PosBas);
		  group.add(RB_PosBas);
		  
		  add(pane);

		  JPaneImage paneimg=new JPaneImage();
		  paneimg.setBounds(new Rectangle(4,293,361,201));
		  add(paneimg);
		  
		  JButton Bt_NouvellePage=new JButton("Nouvelle page");
		  Bt_NouvellePage.setFont(font);
		  Bt_NouvellePage.setBorder(new LineBorder(Color.GRAY, 1));
		  Bt_NouvellePage.setBounds(new Rectangle(370,10,110,25));//Bouton nouvelle page
		  Bt_NouvellePage.addActionListener(new ActionListener()
		  {
	    	  public void actionPerformed(ActionEvent e)
	          {/**Quand on clique, ajoute une page d'évenement**/
	    		  events.evenement.add(projet.new Evenement());
	    		  events.CommandeEv.add(new ArrayList<String>());
	    		  events.CondDecl.add(new ArrayList<String>());
	    		  parent.Page.addTab(Integer.toString(parent.Page.getTabCount()+1),null);
	          }
		  });
		  add(Bt_NouvellePage);
		  
		  JButton Bt_CopierPage=new JButton("Copier page");
		  Bt_CopierPage.setFont(font);
		  Bt_CopierPage.setBorder(new LineBorder(Color.GRAY, 1));
		  Bt_CopierPage.setBounds(new Rectangle(495,10,110,25));//Copier page
		  Bt_CopierPage.addActionListener(new ActionListener()
		  {
			public void actionPerformed(ActionEvent e)
	          {/**Quand on clique, copie page évenement et copie les évenements si coché**/
	    		  String s;
	    		  int i;
	    		  SavePage(parent.Page.getSelectedIndex());
	    		  events.evenement.add(events.evenement.get(parent.Page.getSelectedIndex()).clone());//Copie l'event
	    		  if (CB_AvecCommande.isSelected())
	    		  {//Si avec commande
		    		    events.CommandeEv.add(new ArrayList<String>());
		    			for(i=0;i<events.CommandeEv.get(parent.Page.getSelectedIndex()).size();i++)
		    			{//Copie les commandes d'une page à l'autre
		    				s=events.CommandeEv.get(parent.Page.getSelectedIndex()).get(i);
		    				events.CommandeEv.get(events.CommandeEv.size()-1).add(s);
		    			}
	    		  }
	    		  else//Sans les commandes
		    		  events.CommandeEv.add(new ArrayList<String>());
	    		  
	    		    events.CondDecl.add(new ArrayList<String>());
	    			for(i=0;i<events.CondDecl.get(parent.Page.getSelectedIndex()).size();i++)
	    			{//Copie les conditions de declenchement
	    				s=events.CondDecl.get(parent.Page.getSelectedIndex()).get(i);
	    				events.CondDecl.get(events.CondDecl.size()-1).add(s);
	    			}
	    		  parent.Page.addTab(Integer.toString(parent.Page.getTabCount()+1),null);
	          }
		  });
		  add(Bt_CopierPage);
		  
		  CB_AvecCommande=new JCheckBox("Avec commande");
		  CB_AvecCommande.setFont(font);
		  CB_AvecCommande.setBounds(new Rectangle(491,35,110,21));//Bouton copier page event avec commandes
		  add(CB_AvecCommande);

		  JButton Bt_SupprPage=new JButton("Supprimer page");
		  Bt_SupprPage.setFont(font);
		  Bt_SupprPage.setBorder(new LineBorder(Color.GRAY, 1));//Bouton supprimer page évenement
		  Bt_SupprPage.setBounds(new Rectangle(620,10,110,25));
		  Bt_SupprPage.addActionListener(new ActionListener(){
	    	  public void actionPerformed(ActionEvent e)
	          {/**Quand on clique dessus, demande confirmation et supprime la page**/
		    	  if (JOptionPane.showConfirmDialog(null,
		                "Etes vous sûr de vouloir effacer cette page?",
		                "Effacer",
		                JOptionPane.YES_NO_OPTION,
		                JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION)//Si on confirme
		    	  {
		    		  if (parent.Page.getTabCount()>1)
		    		  {//Si au moins deux page
			    		  events.evenement.remove(parent.Page.getSelectedIndex());//Enlève les events
			    		  events.CommandeEv.remove(parent.Page.getSelectedIndex());//Enleve CD
			    		  events.CondDecl.remove(parent.Page.getSelectedIndex());//Enleve CE
			    		  oldpage=0;
			    		  LoadPage(0);//charge page 0
			    		  LoadImage();
			    		  parent.Page.remove(parent.Page.getSelectedIndex());//Enlève la page
			    		  parent.Page.setSelectedIndex(0);//Set met page 0
			    		  for(int i=0;i<parent.Page.getTabCount();i++)
			    			  parent.Page.setTitleAt(i, Integer.toString(i+1));//Actualiseles noms de page décalées
		    		  }
		    		  else
		    			  JOptionPane.showMessageDialog(null, "Ce bouton n'efface que les pages d'événements. Pour supprimer définitivement l'événement, placez vous dessus dans la grille et appuyez sur la touche suppr de votre clavier.");
		    	  }
	          }
		  });
		  add(Bt_SupprPage);

		  JButton Bt_Gauche=new JButton("<");
		  Bt_Gauche.setFont(font);//Bouton "déplacer la page à gauche"
		  Bt_Gauche.setBorder(new LineBorder(Color.GRAY, 1));
		  Bt_Gauche.setBounds(new Rectangle(745,10,20,25));
		  Bt_Gauche.addActionListener(new ActionListener()
		  {
	    	  public void actionPerformed(ActionEvent e)
	          {/**Quand on clique, supprime la page précédente et la recrée après celle ci**/
	    		  if (parent.Page.getSelectedIndex()>0)//Si c'est pas la première page
	    		  {
	    			  parent.Page.setSelectedIndex(parent.Page.getSelectedIndex()-1);//Sélectione la page d'avant
	    			  Projet.Evenement ev=events.evenement.get(parent.Page.getSelectedIndex());//Récup les event
	    			  ArrayList<String> StCd=events.CondDecl.get(parent.Page.getSelectedIndex());//les CD
	    			  ArrayList<String> StCe=events.CommandeEv.get(parent.Page.getSelectedIndex());//les CE
	    			  events.evenement.remove(parent.Page.getSelectedIndex());//Supprime les event
	    			  events.CondDecl.remove(parent.Page.getSelectedIndex());//les CD
	    			  events.CommandeEv.remove(parent.Page.getSelectedIndex());//et CE
	    			  events.evenement.add(parent.Page.getSelectedIndex()+1,ev);
	    			  events.CondDecl.add(parent.Page.getSelectedIndex()+1,StCd);//Recrée event,CD,CE à coté
	    			  events.CommandeEv.add(parent.Page.getSelectedIndex()+1,StCe);
	    			  LoadPage(parent.Page.getSelectedIndex());
	    		  }
	          }
		  });
		  add(Bt_Gauche);

		  JButton Bt_Droite=new JButton(">");
		  Bt_Droite.setFont(font);
		  Bt_Droite.setBorder(new LineBorder(Color.GRAY, 1));
		  Bt_Droite.setBounds(new Rectangle(765,10,20,25));//Bouton "déplacer la page à droite"
		  Bt_Droite.addActionListener(new ActionListener()
		  {
	    	  public void actionPerformed(ActionEvent e)
	          {/**Quand on clique, supprime la page suivant et la recrée avant celle ci**/
	    		  if (parent.Page.getSelectedIndex()<parent.Page.getTabCount()-1)
	    		  {//Si c'est pas la dernière page
	    			  parent.Page.setSelectedIndex(parent.Page.getSelectedIndex()+1);
	    			  Projet.Evenement ev=events.evenement.get(parent.Page.getSelectedIndex());//Récup event
	    			  ArrayList<String> StCd=events.CondDecl.get(parent.Page.getSelectedIndex());//CD et CE
	    			  ArrayList<String> StCe=events.CommandeEv.get(parent.Page.getSelectedIndex());//De page+1
	    			  events.evenement.remove(parent.Page.getSelectedIndex());//Supprime la page+1
	    			  events.CondDecl.remove(parent.Page.getSelectedIndex());
	    			  events.CommandeEv.remove(parent.Page.getSelectedIndex());
	    			  events.evenement.add(parent.Page.getSelectedIndex()-1,ev);//La recrée à gauche
	    			  events.CondDecl.add(parent.Page.getSelectedIndex()-1,StCd);
	    			  events.CommandeEv.add(parent.Page.getSelectedIndex()-1,StCe);
	    			  LoadPage(parent.Page.getSelectedIndex());
	    		  }
	          }
		  });
		  add(Bt_Droite);

		  pane = new JPanel();
		  title = BorderFactory.createTitledBorder("Commande événement");
		  title.setTitleFont(font);
		  pane.setBorder(title);//Titre cadre droite Commande événement
		  pane.setLayout(null);
		  pane.setBounds(new Rectangle(370,50,416,444));
		  
		  JButton Bt_WzdCE=new JButton("Wizard");
		  Bt_WzdCE.setFont(font);
		  Bt_WzdCE.setBounds(new Rectangle(10,15,70,17));//Wizard CE
		  Bt_WzdCE.addActionListener(new ActionListener()
		  {
	    	  public void actionPerformed(ActionEvent e)
	          {
	    		  CommandeEv ce;
	    		  String[] textlbce=LB_CE.getText().split("\n");//Récup les lignes
	    		  String s="";
	  			  int line=getLineFromCarret(textlbce,LB_CE.getCaretPosition());
	  			  if (line==textlbce.length)
	  				 ce=new CommandeEv(projet,"",null,true);//Si ligne courante vide, CE
	  			  else
	  				 ce=new CommandeEv(projet,textlbce[line],null,true);//Si déjà une CE, l'édite
	    		  if (ce.status==1)
	    		  {//Si cliqué sur OK
		  			  if (line==textlbce.length)
		  				  LB_CE.setText(LB_CE.getText()+ce.Ed_Commande.getText()+"\n");//Nouvelle ligne
		  			  else
		  			  {//Si édite lignes
		  				  textlbce[line]=ce.Ed_Commande.getText();
		  				  for(int i=0;i<textlbce.length;i++)
		  				  	s+=textlbce[i]+"\n";
		  				  LB_CE.setText(s);
		  			  };
	  				  SyntaxHighlight();//Vérifie la syntaxe
	    		  }
	    		  ce.dispose();
			  }
		  });
		  pane.add(Bt_WzdCE);
		  
		  LB_CEmodif=false;
		  LB_CE = new JCustomPane();
		  LB_CE.setFont(font);
		  scrollPane = new JScrollPane(LB_CE);
		  scrollPane.setBounds(new Rectangle(10,32,398,405));
		  scrollPane.getViewport().setBackground(Color.white );
		  scrollPane.addMouseListener(new MouseAdapter() 
		  {
			  public void mouseReleased(MouseEvent e) 
			  {/**Quand on clique sur le texte des CE, met le focus**/
				  LB_CE.requestFocus();
			  }
		  });
		  LB_CE.addKeyListener(new KeyListener() 
		  {
		      	public void keyPressed(KeyEvent keyEvent) { }

		        public void keyReleased(KeyEvent keyEvent) 
		        {
			    	  if (keyEvent.getKeyChar()!=KeyEvent.CHAR_UNDEFINED)
			    		  LB_CEmodif=true;//Si on tape du texte dans CE
			    	  if (((keyEvent.getKeyCode()==KeyEvent.VK_UP) || (keyEvent.getKeyCode()==KeyEvent.VK_DOWN) || (keyEvent.getKeyCode()==KeyEvent.VK_ENTER)) && (LB_CEmodif==true)) 
			    	  {//Si on édite  le CE et qu'on valide une ligne
			    		  try
			    		  {
				    		  int Curspos=LB_CE.getCaretPosition();
				    		  SyntaxHighlight();
				    		  LB_CEmodif=false;//Vérifie la syntaxe, stop la modif et positionne la ligne actuelle
				    		  LB_CE.setCaretPosition(Curspos);
			    		  }catch(IllegalArgumentException e){}
			    	  }
		        }

		        public void keyTyped(KeyEvent keyEvent) {}
		  });

		  pane.add(scrollPane);
		  add(pane);
		  
		  JButton Bt_Ok=new JButton("Ok");
		  Bt_Ok.setBounds(new Rectangle(6,495,100,21));
		  Bt_Ok.addActionListener(new ActionListener()//Bouton Ok
		  {
	    	  public void actionPerformed(ActionEvent e)
	          {/**Quand on clique sur "Ok", vérifie les events, applique les events à la carte et ferme fenetre événement**/
	    		  SavePage(parent.Page.getSelectedIndex());
	    		  if (verif.Verifie(carte.Name, eventx, eventy,events.CommandeEv))
	    		  {
		    		  carte.evenements[eventx][eventy]=events;//Met les events sur la carte
		    		  parent.dispose();//Ferme la fenêtre évenements
	    		  }
	          }
		  });
		  add(Bt_Ok);

		  JButton Bt_Annuler=new JButton("Annuler");
		  Bt_Annuler.setBounds(new Rectangle(130,495,100,21));//Bouton annuler
		  Bt_Annuler.addActionListener(new ActionListener()
		  {
	    	  public void actionPerformed(ActionEvent e)
	          {/**Quand on clique sur annuler, ferme la fenêtre et ne fait rien**/
	    		  parent.dispose();	    		  
	          }
		  });
		  add(Bt_Annuler);
		  
		  oldpage=0;
		  parent.Page.addChangeListener(new ChangeListener() 
		  { 
				// This method is called whenever the selected tab changes 
				public void stateChanged(ChangeEvent evt) 
				{/**Quand on change de page, sauvegarde l'ancienne, et charge la nouvelle**/
					SavePage(oldpage);
					oldpage=parent.Page.getSelectedIndex();//Page courante devient ancienne
					LoadPage(parent.Page.getSelectedIndex());//Selectionne la nouvelle page
					LoadImage();
				} 
		  }); 

		  LoadPage(0);
		  LoadImage();
		}
		public void paintComponent(Graphics g)
		{
		  super.paintComponent(g);
	      Graphics2D g2d = (Graphics2D)g;
	    
	      Font font = new Font("MS Sans Serif", Font.PLAIN, 10);
	    
	      g2d.setFont(font);
		  g2d.drawString("Nom de l'événement : ", 6, 22);//Textes pour fenêtre événement
		  g2d.drawString("Vitesse : ", 23, 44);
		}
	}
	
/**################################################################**/
	public Evenements(Projet projet,Projet.Carte carte,int x,int y)
/**################################################################
 * Constructeur classe évenement. Crée une fenêtre Evenements en x,y sur carte dans projet**/
	{
		  setLayout(null);
		  setResizable(false);
		  Toolkit k = Toolkit.getDefaultToolkit();
		  Dimension tailleEcran = k.getScreenSize();
		  int largeurEcran = tailleEcran.width;
		  int hauteurEcran = tailleEcran.height;
		  
		  setTitle("Evénement");
		  setSize(800,600);//Titre, taille, positionne etc
		  setLocation((largeurEcran/2)-400, (hauteurEcran/2)-300);
		  
		  Page=new JTabbedPane();
		  Page.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		  Page.setBounds(new Rectangle(0,0,790, 20));
		  add(Page);
		  
		  Projet.Evenements events=carte.evenements[x][y];
		  
		  if (events==null)
		  {//S'il n'y a pas d'event sur cette case
			  events=projet.new Evenements();
    		  events.evenement.add(projet.new Evenement());//Le crée
	    	  events.CondDecl.add(new ArrayList<String>());
	    	  events.CommandeEv.add(new ArrayList<String>());
			  Page.addTab("1", null);
		  }
		  else
		  {
			  // on clône afin de permettre le annuler.
			  events=events.clone();
			  for(int i=0;i<events.evenement.size();i++)
				  Page.addTab(Integer.toString(i+1), null);
		  }
		  
		  Contenu monContenu=new Contenu(this,projet,carte,events,x,y);
		  monContenu.setBounds(new Rectangle(0,20,800,580));//Crée le contenu
		  add(monContenu);
	}
}