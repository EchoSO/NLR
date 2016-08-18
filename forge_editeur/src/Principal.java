import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import struct.Projet;
import struct.Projet.Block;
import struct.Projet.Carte;
import struct.Projet.Case;
import forge_client.ImageLoader;
//TODO @BETA finir export projet SO
//TODO @BETA finir import projet SO
//TODO @BETA %Serveur[]%,%Bool[]% guilde, join, msg, afficher pseudo sous joueurs etc, %String%, %Grade%,%BloqueChevauchement%, systeme d'alignement
//TODO @BETA Effacer le perso de test
//TODO @BETA Commandes admin/joueurs, logs de tout, etc
//TODO @BETA2 Passer de zoom 1/1 à zoom 1/8 la première fois = cases trop larges
//TODO @BETA2 Changer de carte avec zoom != 1 = cases trop larges
//TODO @BETA autres types de cartes et de magies
//TODO @ALPHA2 un test.sav pour chaque projet

/**Classe qui gère l'onglet de la zone de monstres**/
class ZoneMonstre extends JPanel
{
	
	private static final long serialVersionUID = 1L;
	FenetreSimple parent;
	DefaultListModel Zonemodel; 
	public JList ZoneList;
	JScrollPane scrollpanezonlist;
	JButton Bt_AjouteZone,Bt_RetireZone;
	JTextField Ed_X1,Ed_Y1,Ed_X2,Ed_Y2;
	JTextField Ed_NbMonstreMax,Ed_VitesseSpawn;
	JTextField Ed_Variable,Ed_Resultat;
	boolean AllowSave;
	JPanel paneunezone,paneunmonstre;
	JComboBox CB_TypeMonstre;
	
	/** Constructeur. Crée l'onglet etc **/
	public ZoneMonstre(FenetreSimple p)
	{
		setLayout(null);
		parent=p;
		Zonemodel= new DefaultListModel();
		ZoneList=new JList(Zonemodel);
		ZoneList.addMouseListener(new MouseAdapter() 
		{
			public void mousePressed(MouseEvent e) 
			{
				if (ZoneList.getSelectedIndex()>=0)
				{/**Quand on clique un élément de la liste, affiche les boutons de choix de monstres et X Y de zone etc **/
					paneunezone.setVisible(true);
					AllowSave=false;
					CB_TypeMonstre.removeAllItems();
					CB_TypeMonstre.addItem("Spawn interdit");//Elements de droite, type, position , nombre, spawn etc
					for(int i=0;i<parent.general.getMonstres().size();i++)
						CB_TypeMonstre.addItem(parent.general.getMonstreByIndex(i).Name);
				    Ed_X1.setText(Integer.toString(parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).X1));
				    Ed_X2.setText(Integer.toString(parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).X2));
				    Ed_Y1.setText(Integer.toString(parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).Y1));
				    Ed_Y2.setText(Integer.toString(parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).Y2));
				    Ed_NbMonstreMax.setText(Integer.toString(parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).MonstreMax));
				    Ed_VitesseSpawn.setText(Integer.toString(parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).VitesseSpawn));
				    Ed_Variable.setText(parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).Variable);
				    Ed_Resultat.setText(parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).Resultat);
				    CB_TypeMonstre.setSelectedIndex(parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).ZoneTypeMonstre);
					AllowSave=true;//Active la sauvegarde si on l'édite
				}
			}
		});
		scrollpanezonlist = new JScrollPane(ZoneList);//On rend la liste scrollable
		scrollpanezonlist.setBounds(new Rectangle(10,10,290,470));
		add(scrollpanezonlist);
		Bt_AjouteZone=new JButton("Ajouter une zone");//Bouton ajout zone
		Bt_AjouteZone.setBounds(new Rectangle(305,10,180,20));
		add(Bt_AjouteZone);
		Bt_AjouteZone.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{/**Quand on clique ajouter une zone, ajoute un élément à la liste**/
				Zonemodel.add(ZoneList.getModel().getSize(),"New Zone");
				parent.monContenu.carteSel.zones.add(parent.general.new Zone());
			}
		});
		Bt_RetireZone=new JButton("Retirer une zone");
		Bt_RetireZone.setBounds(new Rectangle(500,10,180,20));//Bouton retirer une zone
	    Bt_RetireZone.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent e)
			{/**Quand on clique retirer une zone, demande une confirmation puis supprime et enlève de la liste **/
				if (ZoneList.getSelectedIndex()>=0)
				{
			    	if (JOptionPane.showConfirmDialog(null,
			                "Etes vous sûr de vouloir effacer cette zone?",
			                "Effacer",
			                JOptionPane.YES_NO_OPTION,
			                JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)//Confirmation
			    	{
			    		paneunezone.setVisible(false);
						parent.monContenu.carteSel.zones.remove(ZoneList.getSelectedIndex());
						Zonemodel.remove(ZoneList.getSelectedIndex());//Efface
			    	}
				}
			}
		});
		add(Bt_RetireZone);		
		paneunezone=new JPanel();
		paneunezone.setLayout(null);//Panel qui contient les label et zone texte de l'onglet zone de monstres
		paneunezone.setBounds(new Rectangle(305,35,480,550));
		paneunezone.setVisible(false);
		add(paneunezone);
		KeyListener keyListener = new KeyListener() 
		{
		      public void keyTyped(KeyEvent keyEvent) {
		      }
		      public void keyPressed(KeyEvent keyEvent) {
		      }
		      public void keyReleased(KeyEvent keyEvent) //Quand on fait une modif des valeurs, enregistre
		      {
		    	  SaveZone();
		      }
		};
		JLabel LblX=new JLabel("X :");
		LblX.setBounds(new Rectangle(18,10,20,20));
		paneunezone.add(LblX);
		Ed_X1=new JTextField("0");
		Ed_X1.setBounds(new Rectangle(60,10,40,20));//Les textfields...
		Ed_X1.addKeyListener(keyListener);
		paneunezone.add(Ed_X1);
		JLabel LblFleche=new JLabel("->");
		LblFleche.setBounds(new Rectangle(113,10,20,20));//Et les labels...
		paneunezone.add(LblFleche);
		Ed_X2=new JTextField("0");
		Ed_X2.setBounds(new Rectangle(138,10,40,20));
		Ed_X2.addKeyListener(keyListener);
		paneunezone.add(Ed_X2);
		JLabel LblY=new JLabel("Y :");
		LblY.setBounds(new Rectangle(18,35,20,20));
		paneunezone.add(LblY);
		Ed_Y1=new JTextField("0");
		Ed_Y1.setBounds(new Rectangle(60,35,40,20));
		Ed_Y1.addKeyListener(keyListener);
		paneunezone.add(Ed_Y1);
		LblFleche=new JLabel("->");
		LblFleche.setBounds(new Rectangle(113,35,20,20));
		paneunezone.add(LblFleche);
		Ed_Y2=new JTextField("0");
		Ed_Y2.setBounds(new Rectangle(138,35,40,20));
		Ed_Y2.addKeyListener(keyListener);
		paneunezone.add(Ed_Y2);
		JLabel TypeMonstre=new JLabel("Type :");
		TypeMonstre.setBounds(new Rectangle(18,60,40,20));
		paneunezone.add(TypeMonstre);
		CB_TypeMonstre=new JComboBox();
		CB_TypeMonstre.setBounds(new Rectangle(60,60,260,20));
		ActionListener ac=new ActionListener()
		{
			public void actionPerformed(ActionEvent e)//Quand on met pas de spawn, ne demande pas la vitesse etc
			{
				if (CB_TypeMonstre.getSelectedIndex()==0)
					paneunmonstre.setVisible(false);
				else
					paneunmonstre.setVisible(true);
				SaveZone();//Et sauvegarde
			}
	    };
	    CB_TypeMonstre.addActionListener(ac);
	    paneunezone.add(CB_TypeMonstre);
	    paneunmonstre=new JPanel();
	    paneunmonstre.setLayout(null);
	    paneunmonstre.setBounds(new Rectangle(0,85,480,465));
	    paneunezone.add(paneunmonstre);
		JLabel NbMonstreMax=new JLabel("Nb monstre max :");//Ajoutee nombre max et vitesse spawn si != spawn interdit
		NbMonstreMax.setBounds(new Rectangle(18,0,100,20));
		paneunmonstre.add(NbMonstreMax);
		Ed_NbMonstreMax=new JTextField("0");
		Ed_NbMonstreMax.setBounds(new Rectangle(138,0,40,20));
		Ed_NbMonstreMax.addKeyListener(keyListener);
		paneunmonstre.add(Ed_NbMonstreMax);
		JLabel VitesseSpawn=new JLabel("Vitesse de spawn :");
		VitesseSpawn.setBounds(new Rectangle(18,25,110,20));
		paneunmonstre.add(VitesseSpawn);
		Ed_VitesseSpawn=new JTextField("0");
		Ed_VitesseSpawn.setBounds(new Rectangle(138,25,40,20));
		Ed_VitesseSpawn.addKeyListener(keyListener);
		paneunmonstre.add(Ed_VitesseSpawn);
		VitesseSpawn=new JLabel("0 = très rapide");
		VitesseSpawn.setBounds(new Rectangle(200,25,110,20));
		paneunmonstre.add(VitesseSpawn);
		JLabel Condition=new JLabel("Condition de spawn (laissé vide si pas de condition)");
		Condition.setBounds(new Rectangle(18,50,400,20));//Conditions etc
		paneunmonstre.add(Condition);
		JLabel Variable=new JLabel("Variable[");
		Variable.setBounds(new Rectangle(18,75,110,20));
		paneunmonstre.add(Variable);		
		Ed_Variable=new JTextField("");
		Ed_Variable.setBounds(new Rectangle(75,75,100,20));
		Ed_Variable.addKeyListener(keyListener);
		paneunmonstre.add(Ed_Variable);
		JLabel Resultat=new JLabel("]=");
		Resultat.setBounds(new Rectangle(180,75,20,20));
		paneunmonstre.add(Resultat);		
		Ed_Resultat=new JTextField("");
		Ed_Resultat.setBounds(new Rectangle(200,75,100,20));
		Ed_Resultat.addKeyListener(keyListener);
		paneunmonstre.add(Ed_Resultat);
		ComponentAdapter listener = new ComponentAdapter() {
	  		public void componentResized(ComponentEvent evt) 
	  		{
	  	        Component c = (Component) evt.getSource();//Si on redimensionne, adapte la taille de la liste
	  	        Dimension newSize = c.getSize();
	  			scrollpanezonlist.setBounds(new Rectangle(10,10,290,newSize.height-105));
	          }
	  	};
    	parent.addComponentListener(listener);	  
		cache();
	}
	
	/** ### Enregistre la zone ### **/
	private void SaveZone()
	{		
		if (AllowSave)
		{
			try
			{
				parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).X1=Integer.parseInt(Ed_X1.getText());
				parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).X2=Integer.parseInt(Ed_X2.getText());
				parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).Y1=Integer.parseInt(Ed_Y1.getText());
				parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).Y2=Integer.parseInt(Ed_Y2.getText());
				parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).MonstreMax=Integer.parseInt(Ed_NbMonstreMax.getText());
				parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).VitesseSpawn=Integer.parseInt(Ed_VitesseSpawn.getText());
				parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).Variable=Ed_Variable.getText();
				parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).Resultat=Ed_Resultat.getText();
				parent.monContenu.carteSel.zones.get(ZoneList.getSelectedIndex()).ZoneTypeMonstre=CB_TypeMonstre.getSelectedIndex();
			}catch(NumberFormatException e4){}
		}
	}
	
/**### Charge la liste de zones de monstres et modifie le titre de l'onglet ###**/
	public void LoadZone()
	{
		if (parent.general!=null)
		{
			paneunezone.setVisible(false);
			Zonemodel.clear();
			if (parent.monContenu.carteSel.zones.size()>0)//S'il y a des monstres
				parent.tabbedPane.setTitleAt(2, "Zone de monstres ***");
			else
				parent.tabbedPane.setTitleAt(2, "Zone sans monstres");//S'il n'y en a pas (titre)
			
			for(int i=0;i<parent.monContenu.carteSel.zones.size();i++)
			{
				if (parent.monContenu.carteSel.zones.get(i).ZoneTypeMonstre>0)//S'il y a un monstre, on met le mnstre
					Zonemodel.add(i, parent.general.getMonstreByIndex(parent.monContenu.carteSel.zones.get(i).ZoneTypeMonstre-1).Name+" : "+Integer.toString(parent.monContenu.carteSel.zones.get(i).X1)+"/"
											  +Integer.toString(parent.monContenu.carteSel.zones.get(i).Y1)+" -> "+Integer.toString(parent.monContenu.carteSel.zones.get(i).X2)+"/"+Integer.toString(parent.monContenu.carteSel.zones.get(i).Y2)
											  +" Max "+Integer.toString(parent.monContenu.carteSel.zones.get(i).MonstreMax));
				else // Sinon on met spawn interdit
					Zonemodel.add(i, "Spawn interdit : "+Integer.toString(parent.monContenu.carteSel.zones.get(i).X1)+"/"
							  +Integer.toString(parent.monContenu.carteSel.zones.get(i).Y1)+" -> "+Integer.toString(parent.monContenu.carteSel.zones.get(i).X2)+"/"+Integer.toString(parent.monContenu.carteSel.zones.get(i).Y2));
			}
			montre();
		}
	}
	
	/**Cache l'onglet zone monstre**/
	public void cache()
	{
		ZoneList.setVisible(false);
		Bt_AjouteZone.setVisible(false);
		Bt_RetireZone.setVisible(false);
	}
	
/**### Activée quand on ouvre l'onglet monstre. Montre les boutons et la liste de zones de monstres ###**/
	public void montre()
	{
		ZoneList.setVisible(true);
		Bt_AjouteZone.setVisible(true);
		Bt_RetireZone.setVisible(true);
	}
}
/** ### Classe qui gère l'onglet Blocage ### **/
class Blocage extends JPanel
{
	private static final long serialVersionUID = 1L;
	FenetreSimple parent;
	private JTable PatternGrid;
	JTabbedPane Posit;
	JScrollPane scrollpanePG;
	private Image[][] Source;
	Block ChipBlock;

	
/**################################################################**/
	public Blocage(FenetreSimple p)
/**################################################################
 * Constructeur, crée l'onglet "blocage" dans l'éditeur 'p' et crée la grille etc**/
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		parent=p;
		Posit=new JTabbedPane();
		Posit.addTab("Position Basse", null);
		Posit.addTab("Position Haute", null);
		Posit.setPreferredSize(new Dimension(800, 20));
		Posit.addChangeListener(new ChangeListener() { 
			// This method is called whenever the selected tab changes 
			public void stateChanged(ChangeEvent evt) 
			{ 
				reinitBlocageGrid();
			} 
		}); 
		add(Posit);
	    sourceModel sm=new sourceModel();
		PatternGrid = new JTable(sm);
		PatternGrid.setRowSelectionAllowed( false );
		PatternGrid.setColumnSelectionAllowed(false);
		scrollpanePG = new JScrollPane(PatternGrid);
		PatternGrid.setTableHeader(null);
		PatternGrid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		PatternGrid.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		PatternGrid.setRowSelectionAllowed( true );
		PatternGrid.setColumnSelectionAllowed(true);
		PatternGrid.setRowHeight(32);
		for(int i=0;i<PatternGrid.getColumnCount();i++)//Initialise la taille à 32px etc
		{
			TableColumn col = PatternGrid.getColumnModel().getColumn(i); 
		    col.setPreferredWidth(32); 
		}
		
	    PatternGrid.addMouseListener(new MouseAdapter() 
	    {
		  public void mouseReleased(MouseEvent e) 
		  {/**Quand on relache  la souris, ça met le blocage/déblocage sur les cases selectionnées (un rectangle)**/
				for (int i=PatternGrid.getSelectedRow();i<=PatternGrid.getSelectionModel().getMaxSelectionIndex();i++)
					for (int j=PatternGrid.getSelectedColumn();j<=PatternGrid.getColumnModel().getSelectionModel().getMaxSelectionIndex();j++)
						ChipBlock.blocage[Posit.getSelectedIndex()][j][i]=!ChipBlock.blocage[Posit.getSelectedIndex()][j][i];
				PatternGrid.updateUI();//Inverse le blocage et update l'interface
		  }
	    });
		scrollpanePG.setPreferredSize(new Dimension(800, 580));
		scrollpanePG.setAlignmentX(CENTER_ALIGNMENT);
		add(scrollpanePG);
	}

/**################################################################**/
	public void reinitBlocageGrid()
/**################################################################
 * Crée une nouvelle grille de blocage quand on change de chipset/map**/
	{
		Source=parent.monContenu.Source;
		if(parent.monContenu.carteSel==null) return;
		ChipBlock=parent.general.getBlocageByName(parent.monContenu.carteSel.Chipset);
		if (ChipBlock==null)
		{
			//nouveau Chipset
			ChipBlock=parent.general.new Block(parent.monContenu.carteSel.Chipset,Source.length,Source[0].length);
			parent.general.getBlocages().add(ChipBlock);
		}
		else
		{
			// on vérifie que la taille du chipset n'a pas changé
			if ((ChipBlock.blocage[0].length!=Source.length)||(ChipBlock.blocage[0][0].length!=Source[0].length))
				ChipBlock=parent.general.resizeBlocage(ChipBlock, Source.length, Source[0].length);
		}
	    sourceModel sm=new sourceModel();
		PatternGrid.setModel(sm);	
		PatternGrid.setRowHeight(32);
		for(int i=0;i<PatternGrid.getColumnCount();i++)//On crée la grille, taille 32px
		{
			TableColumn col = PatternGrid.getColumnModel().getColumn(i); 
		    col.setPreferredWidth(32); 
		}
	}
	
	
/**################################################################**/
	  public class sourceModel extends AbstractTableModel 
/**################################################################
 * Sert à l'affichage du chipset dans l'onglet blocage**/	  
	  {
			private static final long serialVersionUID = 1L;
			//Nombre de colonnes
			public int getColumnCount() { if (Source!=null) return Source.length; else return 0; }
			//Nombre de lignes
		    public int getRowCount() { if (Source!=null) return Source[0].length; else return 0; }
		    
		    /**############### Rendu d'une case ###################**/
		    public Object getValueAt(int row, int col) {
		    	if (Source!=null)
		    	{/**Dessine un rond blanc sur les cases non-bloquantes**/
		    		boolean selected=ChipBlock.blocage[Posit.getSelectedIndex()][col][row];
			    	if (selected)
			    	{
			    		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();

			            BufferedImage dst = gc.createCompatibleImage(Source[col][row].getWidth(null),Source[col][row].getHeight(null));
			            // Setting transparency
			            Graphics2D g2d = dst.createGraphics();
			            g2d.setComposite(AlphaComposite.Src);
			            // Copy image
			            g2d.drawImage(Source[col][row],0,0,null);//Dessine l'image de la case
			            Stroke stroke=new BasicStroke(2);
			            g2d.setStroke(stroke);
			            g2d.drawArc(2, 2, dst.getWidth(null)-3, dst.getHeight(null)-3, 0, 360);//Dessine le rond dessus
			            g2d.dispose();
			    		return new ImageIcon(dst);//Renvoie l'image générée
			    	}
			    	else
			    		return new ImageIcon(Source[col][row]);//Si bloquante, renvoie juste la case
		    	}
		    	else
		    	  return null;
		    }
		    public Class<ImageIcon> getColumnClass(int column)  {return ImageIcon.class;}    
		  };
}

/**### Classe pour gérer le contenu de la fenêtre éditeur ###**/
class Contenu extends JPanel
{
	private static final long serialVersionUID = 1L;
	public Image[][] Source;  
	private JTree Tree;
	public JTable CarteGrid,PatternGrid;
	public Case[][] CopyRectangle;
	public struct.Projet.Evenements[][] CopyEvents;
	private JStatusBar StatusBar;
	DefaultMutableTreeNode nodeSel;
	FenetreSimple parent;
	public Projet.Carte carteSel;
	Image oldim;
	String oldchipset;
	JCheckBox CB_EmptyColor;
	JRadioButton Bt_Selection;
    JRadioButton Bt_Crayon;
    JRadioButton Bt_Rectangle;
	JRadioButton Bt_CoucheBasse;
	JRadioButton Bt_CoucheHaute;//Déclaration des boutons etc en variables de classe
	JRadioButton Bt_CoucheEvent;
	JRadioButton Bt_Depart;
	JRadioButton Bt_Mort;
	JComboBox Cb_Affiche,Cb_Res;
	JRadioButton Bt_Zoom1,Bt_Zoom2,Bt_Zoom4,Bt_Zoom8;
	JCheckBox Bt_FullScreen,Bt_Musique;
	JScrollPane scrollpaneTree,scrollpanePG,scrollpaneCG;
	JButton Bt_Go;
	int previousrow=-1,previouscol=-1;
	ComponentListener listener;
	
	
/**################################################################**/
	public Contenu(FenetreSimple p)
/**################################################################
 * Constructeur de la classe Contenu. Initialise tout le contenu de l'éditeur 'p'**/
	{		  
	  parent=p;
	  Toolkit k = Toolkit.getDefaultToolkit();
      Dimension tailleEcran = k.getScreenSize();
	  int largeurEcran = tailleEcran.width;
	  setLayout(null);
	  
	  // barre d'état
	  StatusBar=new JStatusBar();
	  StatusBar.setLayout(null);
	  StatusBar.setBounds(new Rectangle(0,500,780,15));
	  StatusBar.setBorder(BorderFactory.createEtchedBorder());
	  
	  Bt_Go=new JButton("v ->");//Bouton "Go" (aller à une coordonnée)
	  Bt_Go.setBounds(new Rectangle(718,0,60,13));
  	  Bt_Go.addActionListener(
  			new ActionListener(){
  				public void actionPerformed(ActionEvent e)
  				{/**Quand on choisit une case dans "Go", la sélection se met dessus**/
					 JumpTo jump=new JumpTo(null,true);
					 if (jump.status==1)
					 {
						 try//Si ce sont des nombres valides, on va à la case
						 {
							 CarteGrid.changeSelection(Integer.parseInt(jump.Ed_Y.getText()), Integer.parseInt(jump.Ed_X.getText()), false, false);
						 }catch(NumberFormatException e3){}
					 }
					 jump.dispose();//Fermer la fenêtre
  				}
  			});
	  StatusBar.add(Bt_Go);
	  add(StatusBar);
	  
	  //barre d'outil
	  JToolBar toolbar=new JToolBar();
	  toolbar.setBounds(new Rectangle(0,0,largeurEcran-3,26));
	  toolbar.setLayout(null);
	  toolbar.setFloatable(false);//La rend indétachable de l'éditeur
	  
	  CB_EmptyColor=new JCheckBox("Empty color");//Checkbox "empty color"
	  CB_EmptyColor.setBounds(new Rectangle(10,0,110,22));//TODO @BETA2 parfois c'est empty color même s'il n'est  pas coché
	  CB_EmptyColor.setSelected(true);
	  
	  toolbar.add(CB_EmptyColor);
	  
	  ButtonGroup buttons = new ButtonGroup();
	  
	  Bt_Selection=new JRadioButton();
	  Bt_Selection.setToolTipText("Sélection");//Bouton sélection
	  Bt_Selection.setIcon(new ImageIcon(getClass().getResource("/resources/selection.png"), "Sélection"));
	  Bt_Selection.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/selectionsel.png")));
	  Bt_Selection.setSelected(true);
	  Bt_Selection.setBounds(new Rectangle(120,0,23,22));
	  Bt_Selection.setBorder(null);
  	  Bt_Selection.addActionListener(
    			new ActionListener()
    			{/**Bouton sélection: Sélectionner une seule case chipset et plusieurs cases map**/
    				public void actionPerformed(ActionEvent e)
    				{
   	   				  CarteGrid.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    			      CarteGrid.setRowSelectionAllowed( true );
    			      CarteGrid.setColumnSelectionAllowed(true);
      				  PatternGrid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    			      PatternGrid.setRowSelectionAllowed(false);
    			      PatternGrid.setColumnSelectionAllowed(false);
    				}
    			});
      toolbar.add(Bt_Selection);

      Bt_Crayon=new JRadioButton();
	  Bt_Crayon.setToolTipText("Crayon");//Bouton crayon
	  Bt_Crayon.setIcon(new ImageIcon(getClass().getResource("/resources/crayon.png"), "Crayon"));
	  Bt_Crayon.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/crayonsel.png")));
	  Bt_Crayon.setBounds(new Rectangle(144,0,23,22));
	  Bt_Crayon.setBorder(null);
  	  Bt_Crayon.addActionListener(
  			new ActionListener()
  			{/**Bouton crayon: Sélectionner plusieurs cases chipset vers une case map**/
  				public void actionPerformed(ActionEvent e)
  				{
   				  CarteGrid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  			      CarteGrid.setRowSelectionAllowed( false );
  			      CarteGrid.setColumnSelectionAllowed(false);
  				  PatternGrid.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
  			      PatternGrid.setRowSelectionAllowed(true);
  			      PatternGrid.setColumnSelectionAllowed(true);
  				}
  			});
      toolbar.add(Bt_Crayon);
	  
      Bt_Rectangle=new JRadioButton();
	  Bt_Rectangle.setToolTipText("Rectangle");//Bouton rectangle
	  Bt_Rectangle.setIcon(new ImageIcon(getClass().getResource("/resources/rectangle.png"), "Rectangle"));
	  Bt_Rectangle.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/rectanglesel.png")));
	  Bt_Rectangle.setBounds(new Rectangle(167,0,23,22));
	  Bt_Rectangle.setBorder(null);
  	  Bt_Rectangle.addActionListener(
    			new ActionListener()
    			{/**Bouton rectangle: Sélectionner une seule case chipset vers plusieurs cases map**/
    				public void actionPerformed(ActionEvent e)
    				{
   	   				  CarteGrid.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    			      CarteGrid.setRowSelectionAllowed( true );
    			      CarteGrid.setColumnSelectionAllowed(true);
      				  PatternGrid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      				  PatternGrid.setRowSelectionAllowed(false);
    			      PatternGrid.setColumnSelectionAllowed(false);
    				}
    			});
      toolbar.add(Bt_Rectangle);

      buttons.add(Bt_Selection);
      buttons.add(Bt_Crayon);//Ajoute selection/crayon/rectangle
      buttons.add(Bt_Rectangle);
      
	  buttons = new ButtonGroup();
	  
	  Bt_CoucheBasse=new JRadioButton();
	  Bt_CoucheBasse.setToolTipText("Zone basse");//Bouton couche basse
	  Bt_CoucheBasse.setIcon(new ImageIcon(getClass().getResource("/resources/couchebasse.png"), "Zone basse"));
	  Bt_CoucheBasse.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/couchebassesel.png")));
	  Bt_CoucheBasse.setBounds(new Rectangle(230,0,23,22));
	  Bt_CoucheBasse.setBorder(null);
  	  Bt_CoucheBasse.addActionListener(
  			new ActionListener()
  			{
  				public void actionPerformed(ActionEvent e)
  				{/**L'appui sur le bouton couche basse update l'affichage de la map(plus de carrés jaunes)**/
  					CarteGrid.updateUI();
  				}
  			});
      toolbar.add(Bt_CoucheBasse);

      Bt_CoucheHaute=new JRadioButton();
      Bt_CoucheHaute.setToolTipText("Zone haute");//Bouton couche haute
      Bt_CoucheHaute.setIcon(new ImageIcon(getClass().getResource("/resources/couchehaute.png"), "Zone haute"));
      Bt_CoucheHaute.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/couchehautesel.png")));
      Bt_CoucheHaute.setBounds(new Rectangle(256,0,23,22));
      Bt_CoucheHaute.setBorder(null);
  	  Bt_CoucheHaute.addActionListener(
    			new ActionListener()
    			{
    				public void actionPerformed(ActionEvent e)
    				{/**L'appui sur le bouton couche haute update l'affichage de la map(plus de carrés jaunes)**/
    					CarteGrid.updateUI();
    				}
    			});
      toolbar.add(Bt_CoucheHaute);

      Bt_CoucheEvent=new JRadioButton();
      Bt_CoucheEvent.setToolTipText("Zone événement");//Bouton event
      Bt_CoucheEvent.setIcon(new ImageIcon(getClass().getResource("/resources/coucheevenement.png"), "Zone événement"));
      Bt_CoucheEvent.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/coucheevenementsel.png")));
      Bt_CoucheEvent.setBounds(new Rectangle(279,0,23,22));
      Bt_CoucheEvent.setBorder(null);
	  Bt_CoucheEvent.setSelected(true);
  	  Bt_CoucheEvent.addActionListener(
    			new ActionListener()
    			{
    				public void actionPerformed(ActionEvent e)
    				{/**Le bouton event update l'interface de la map pour afficher les carrés jaunes**/
    					CarteGrid.updateUI();
    				}
    			});
      toolbar.add(Bt_CoucheEvent);

      Bt_Depart=new JRadioButton();
      Bt_Depart.setToolTipText("Point de départ");//Bouton Départ
      Bt_Depart.setIcon(new ImageIcon(getClass().getResource("/resources/depart.png"), "Point de départ"));
      Bt_Depart.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/departsel.png")));
      Bt_Depart.setBounds(new Rectangle(302,0,23,22));
      Bt_Depart.setBorder(null);
      toolbar.add(Bt_Depart);

      Bt_Mort=new JRadioButton();
      Bt_Mort.setToolTipText("Point de résurrection");//Bouton Résu
      Bt_Mort.setIcon(new ImageIcon(getClass().getResource("/resources/dead.png"), "Point de résurrection"));
      Bt_Mort.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/deadsel.png")));
      Bt_Mort.setBounds(new Rectangle(325,0,23,22));
      Bt_Mort.setBorder(null);
      toolbar.add(Bt_Mort);

      buttons.add(Bt_CoucheBasse);
      buttons.add(Bt_CoucheHaute);
      buttons.add(Bt_CoucheEvent);//Ajout les boutons
      buttons.add(Bt_Depart);
      buttons.add(Bt_Mort);
            
	  buttons = new ButtonGroup();
	  
	  Bt_Zoom1=new JRadioButton();
	  Bt_Zoom1.setIcon(new ImageIcon(getClass().getResource("/resources/zoom1.png")));
	  Bt_Zoom1.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/zoom1sel.png")));
	  Bt_Zoom1.setSelected(true);
	  Bt_Zoom1.setBounds(new Rectangle(355,0,23,22));
	  Bt_Zoom1.setBorder(null);
  	  Bt_Zoom1.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
	  			    CarteGrid.setRowHeight(32);
	  				for(int i=0;i<CarteGrid.getColumnCount();i++)
	  				{
	  				  TableColumn col = CarteGrid.getColumnModel().getColumn(i); 
	  				  col.setMinWidth(0);
	  				  col.setPreferredWidth(32);//Cases de 32px pour zoom normal (1/1)
	  				  col.setMaxWidth(32);
	  				}
					CarteGrid.updateUI();
				}
			});
      toolbar.add(Bt_Zoom1);

      Bt_Zoom2=new JRadioButton();
      Bt_Zoom2.setIcon(new ImageIcon(getClass().getResource("/resources/zoom2.png")));
      Bt_Zoom2.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/zoom2sel.png")));
      Bt_Zoom2.setBounds(new Rectangle(378,0,23,22));
      Bt_Zoom2.setBorder(null);
  	  Bt_Zoom2.addActionListener(
  			new ActionListener(){
  				public void actionPerformed(ActionEvent e)
  				{
	  			    CarteGrid.setRowHeight(16);
	  				for(int i=0;i<CarteGrid.getColumnCount();i++)
	  				{
	  				  TableColumn col = CarteGrid.getColumnModel().getColumn(i); 
	  				  col.setMinWidth(0);
	  				  col.setPreferredWidth(16);//Cases de 16px pour zoom 1/2
	  				  col.setMaxWidth(32);
	  				}
  					CarteGrid.updateUI();
  				}
  			});
      toolbar.add(Bt_Zoom2);
      
      Bt_Zoom4=new JRadioButton();
      Bt_Zoom4.setIcon(new ImageIcon(getClass().getResource("/resources/zoom4.png")));
      Bt_Zoom4.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/zoom4sel.png")));
      Bt_Zoom4.setBounds(new Rectangle(401,0,23,22));
      Bt_Zoom4.setBorder(null);
  	  Bt_Zoom4.addActionListener(
    			new ActionListener(){
    				public void actionPerformed(ActionEvent e)
    				{
  	  			    CarteGrid.setRowHeight(8);
  	  				for(int i=0;i<CarteGrid.getColumnCount();i++)
  	  				{
  	  				  TableColumn col = CarteGrid.getColumnModel().getColumn(i); 
  	  				  col.setMinWidth(0);
  	  				  col.setPreferredWidth(8);//Cases de 8px pour cases 1/4
  	  				  col.setMaxWidth(32);
  	  				}
    					CarteGrid.updateUI();
    				}
    			});
      toolbar.add(Bt_Zoom4);

      Bt_Zoom8=new JRadioButton();
      Bt_Zoom8.setIcon(new ImageIcon(getClass().getResource("/resources/zoom8.png")));
      Bt_Zoom8.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/zoom8sel.png")));
      Bt_Zoom8.setBounds(new Rectangle(424,0,23,22));
      Bt_Zoom8.setBorder(null);
  	  Bt_Zoom8.addActionListener(
    			new ActionListener()
    			{
    				public void actionPerformed(ActionEvent e)
    				{
  	  			    CarteGrid.setRowHeight(4);//Cases de 4px pour zoom 1/8
  	  				for(int i=0;i<CarteGrid.getColumnCount();i++)
  	  				{
  	  				  TableColumn col = CarteGrid.getColumnModel().getColumn(i); 
  	  				  col.setPreferredWidth(4); 
  	  				}
    					CarteGrid.updateUI();
    				}
    			});
      toolbar.add(Bt_Zoom8);

      buttons.add(Bt_Zoom1);
      buttons.add(Bt_Zoom2);
      buttons.add(Bt_Zoom4);//Boutons de zoom
      buttons.add(Bt_Zoom8);

	  Cb_Affiche = new JComboBox(new String[] { "Afficher tout","Position basse","Position haute"});
	  Cb_Affiche.setBounds(new Rectangle(450,0,110,22));
	  Cb_Affiche.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if (Cb_Affiche.getSelectedIndex()==1)
					Bt_CoucheBasse.setSelected(true);
				else if (Cb_Affiche.getSelectedIndex()==2)//Choix afficher couche haute/basse/2
					Bt_CoucheHaute.setSelected(true);
				else
					Bt_CoucheEvent.setSelected(true);
				CarteGrid.updateUI();
			}
	  });

	  toolbar.add(Cb_Affiche);

      JButton Bt_Launch=new JButton();
      Bt_Launch.setToolTipText("Lancer le test(F9)");//Bouton lancer le test
      Bt_Launch.setIcon(new ImageIcon(getClass().getResource("/resources/launch.png"),"Lancer le test(F9)"));
      Bt_Launch.setBounds(new Rectangle(570,0,23,23));
      Bt_Launch.setMnemonic(KeyEvent.VK_F9);//Alt+F9 (TODO @ALPHA2 : F9)
	  Bt_Launch.addActionListener(new ActionListener()
	  {
			public void actionPerformed(ActionEvent e)/**Quand on clique couton "Launch", lance le client avec param**/
			{
				double zoomClient=2;
				int valZoom=Cb_Res.getSelectedIndex();
				switch (valZoom)
				{
					case 0: zoomClient=0;break;
					case 1: zoomClient=1;break;
					case 2: zoomClient=2;break;
					case 3: zoomClient=2.5;break;
					case 4: zoomClient=3;break;
					case 5: zoomClient=3.2;break;
					case 6: zoomClient=4;break;
				}
				//new client_serveur.Game(parent.general,zoomClient,Bt_FullScreen.isSelected() ? 1 : 0,Bt_Musique.isSelected() ? 1 : 0);
				new forge_client.Game(parent.general,zoomClient,Bt_FullScreen.isSelected() ? 1 : 0,Bt_Musique.isSelected() ? 1 : 0);
			}
	  });
      toolbar.add(Bt_Launch);

      Bt_FullScreen=new JCheckBox();
      Bt_FullScreen.setIcon(new ImageIcon(getClass().getResource("/resources/fullscreen.png")));
      Bt_FullScreen.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/fullscreensel.png")));
      Bt_FullScreen.setBounds(new Rectangle(593,0,23,22));
      Bt_FullScreen.setBorder(null);
      toolbar.add(Bt_FullScreen);//Boutons fullscreen

      Bt_Musique=new JCheckBox();
      Bt_Musique.setIcon(new ImageIcon(getClass().getResource("/resources/notemusique.png")));
      Bt_Musique.setSelectedIcon(new ImageIcon(getClass().getResource("/resources/notemusiquesel.png")));
      Bt_Musique.setBounds(new Rectangle(616,0,23,22));//Bouton musique
      Bt_Musique.setBorder(null);
      toolbar.add(Bt_Musique);

	  Cb_Res = new JComboBox(new String[] { "Automatique","320*240","640*480","800*600","960*720","1024*768","1280*960"});
	  Cb_Res.setBounds(new Rectangle(645,0,90,22));//Bouton résolution client
	  Cb_Res.setSelectedIndex(1);
	  toolbar.add(Cb_Res);
	  
	  add(toolbar);
	  
	  //panneau du bas avec les maps
	  Tree=new JTree();
	  Tree.setAlignmentY(Component.BOTTOM_ALIGNMENT);
	  scrollpaneTree = new JScrollPane(Tree);
	  Tree.addMouseListener(new MouseAdapter() 
	  {
	      public void mouseClicked(MouseEvent me)/**Quand on clique à la souris sur l'arbre,selection map + chargement**/
	      {
	    	  TreePath tp = Tree.getPathForLocation(me.getX(), me.getY());//Récup la coord X et Y du clic
	  		  nodeSel = (DefaultMutableTreeNode) Tree.getLastSelectedPathComponent();//Récup l'élément selectionné
			  if (nodeSel==((DefaultTreeModel)Tree.getModel()).getRoot())
			  {
				  parent.monZoneMonstre.cache();//Actualise l'onglet zone de monstre
			  }
			  else
			  {
		    	  carteSel=(tp == null ? null : parent.general.getCarteByName(tp.getLastPathComponent().toString()));
		    	  if (carteSel!=null)//Si on a cliqué sur une carte 
		    	  {
		    		  ImageFilter cif2;
		    		  ImageProducer improd;
		    		  Image Image=null;
		    		  String _Image=System.getProperty("user.dir")+"/"+parent.NomCarte+"/"+carteSel.Chipset.replace("Chipset\\", "Chipset/");
		    		  if (new File(_Image).exists())//On récupère le chipset associé
		    		  {
		    			  ImageLoader im=new ImageLoader(null);
		    			  Image=im.loadImage(_Image);
		    		  }
		    		  Source= new Image[Image.getWidth(null)/16][Image.getHeight(null)/16];
		    		  improd=Image.getSource();//On crée le chipset avec
		    		  for (int i=0;i<Source.length;i++)
		    		  {
		    			for(int j=0;j<Source[i].length;j++)
		    			{
		    			  	cif2 =new CropImageFilter(i*16, j*16,16,16);
		    			   	Source[i][j] = createImage(new FilteredImageSource(improd, cif2));
		    			   	Source[i][j]=Source[i][j].getScaledInstance(32, 32, BufferedImage.SCALE_SMOOTH); 
		    			}//On remplit le chipset d'imag
		    		  }
		    		  sourceModel sm=new sourceModel();
		    		  PatternGrid.setModel(sm);
		    		  for(int i=0;i<PatternGrid.getColumnCount();i++)
		    		  {
		    			  TableColumn col = PatternGrid.getColumnModel().getColumn(i); 
		    			  col.setPreferredWidth(32); 
		    		  }//On crée la grille du chipset et on définit les colonnes
		    		  parent.monBlocage.reinitBlocageGrid();//Réinitialisation de la grille des blocages
		    	      targetModel tm=new targetModel();
		    	      tm.setColumnCount(carteSel.TailleX);
		    	      tm.setRowCount(carteSel.TailleY);//On crée la grille de la map
		    		  CarteGrid.setModel(tm);//et on définit sa taille etc
		    		  for(int i=0;i<CarteGrid.getColumnCount();i++)
		    		  {//On définit les colonnes
		    			  TableColumn col = CarteGrid.getColumnModel().getColumn(i); 
		    			  col.setPreferredWidth(32); 
		    		  }
		    		  parent.monZoneMonstre.LoadZone();//Charge les zones de monstres
		    	  }
		    	  /*else
					JOptionPane.showMessageDialog(null, "Carte introuvable.");*///Inutile.
			  }
		  }
	  });
	  addMyRightListener(Tree);
	  scrollpaneTree.setBounds(new Rectangle(0,300,226,190));
	  add(scrollpaneTree);// On fait un panel scrollable
	  //panneau de gauche
	  sourceModel sm=new sourceModel();
	  PatternGrid = new JTable(sm);
	  PatternGrid.setRowSelectionAllowed( false );
	  PatternGrid.setColumnSelectionAllowed(false);//Pour le chipset, on crée une grille dans un panel scrollable
	  scrollpanePG = new JScrollPane(PatternGrid);
	  PatternGrid.setTableHeader(null);
	  PatternGrid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	  PatternGrid.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	  PatternGrid.setRowHeight(32);
	  for(int i=0;i<PatternGrid.getColumnCount();i++)
	  {
		  TableColumn col = PatternGrid.getColumnModel().getColumn(i); 
		  col.setPreferredWidth(32); //On définit la largeur des colonnes
	  }
	  PatternGrid.addMouseListener(new MouseAdapter() 
	  {
		  public void mousePressed(MouseEvent e) 
		  {
			  CB_EmptyColor.setSelected(false);/**Quand on clique dans le chipset ça déselectionne "empty color"**/
		  }
	  });
	  scrollpanePG.setBounds(new Rectangle(0,26,226,269));
	  add(scrollpanePG);//On ajoute le chipset au container
	  //panneau de droite
      targetModel tm=new targetModel();
      tm.setRowCount(0);//On initialise la grille de la map
      tm.setColumnCount(0);
//      tm.setRowCount((hauteurEcran/32)-2);
//      tm.setColumnCount(((largeurEcran-230)/32));
      CarteGrid = new JTable(tm);
      CarteGrid.setRowSelectionAllowed( true );
      CarteGrid.setColumnSelectionAllowed(true);
      CarteGrid.setTableHeader(null);
      CarteGrid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      CarteGrid.setRowHeight(32);

	  for(int i=0;i<CarteGrid.getColumnCount();i++)
	  {
		  TableColumn col = CarteGrid.getColumnModel().getColumn(i);
		  col.setMinWidth(0);
		  col.setPreferredWidth(32);//On initialise les tailles des colonnes
		  col.setMaxWidth(32);
	  }
	  scrollpaneCG = new JScrollPane(CarteGrid);
	  scrollpaneCG.setBounds(new Rectangle(230,26,550,470));
	  scrollpaneCG.setViewportView(CarteGrid);
	  CarteGrid.addKeyListener(new KeyListener() 
	  {
	      public void keyPressed(KeyEvent keyEvent) 
	      {
	    	  if (keyEvent.getKeyCode()==KeyEvent.VK_DELETE)/**Quand on fait "delete" sur une case, suppr case/event**/
	    	  {	    		  
				 for (int i=CarteGrid.getSelectedRow();i<=CarteGrid.getSelectionModel().getMaxSelectionIndex();i++)
					for (int j=CarteGrid.getSelectedColumn();j<=CarteGrid.getColumnModel().getSelectionModel().getMaxSelectionIndex();j++)
					{
					  if (Bt_CoucheBasse.isSelected())
					  {
						 carteSel.cases[j][i].X1=0;//Si la couche basse est sélectionnée, on vide la couche basse
						 carteSel.cases[j][i].Y1=0;
					  }
					  else if (Bt_CoucheHaute.isSelected())
					  {
						 carteSel.cases[j][i].X2=0;//Si la couche haute est sélectionnée, on vide la couche haute
						 carteSel.cases[j][i].Y2=0;					  
					  }
			    	  else if ((Bt_CoucheEvent.isSelected()) && (carteSel.evenements[j][i]!=null))
					  {
						 carteSel.evenements[j][i]=null;//Si la couche event est sélectionnée, on suppr l'event
					  }
					}
				  CarteGrid.updateUI();												
	    	  }
	    	  if ((keyEvent.isControlDown())&&(keyEvent.getKeyCode()==KeyEvent.VK_C))
	    		  parent.MenuCopier();/**Ctrl+C déclenche "Copier"**/
	    	  if ((keyEvent.isControlDown())&&(keyEvent.getKeyCode()==KeyEvent.VK_V))
	    		  parent.MenuColler();/**Ctrl+V déclenche "Coller"**/
	        }

	        public void keyReleased(KeyEvent keyEvent) {}

	        public void keyTyped(KeyEvent keyEvent) {}
	  });
	  CarteGrid.addMouseListener(new MouseAdapter() 
	  {
		  public void mouseReleased(MouseEvent e)/**Quand la souris est relachée sur la map, on écrit le rectangle**/
		  {
			  if (Bt_Rectangle.isSelected())//Si on a choisi le bouton Rectangle
			  {
					if (Bt_CoucheBasse.isSelected())//Si on a la couche basse
					{	
						for (int i=CarteGrid.getSelectedRow();i<=CarteGrid.getSelectionModel().getMaxSelectionIndex();i++)
							for (int j=CarteGrid.getSelectedColumn();j<=CarteGrid.getColumnModel().getSelectionModel().getMaxSelectionIndex();j++)
							{//Pour tout le carré selectionné
								if (CB_EmptyColor.isSelected())
								{//Si c'est empty color, on met 0 partout
									carteSel.cases[j][i].X1=0; 
									carteSel.cases[j][i].Y1=0;
								}
								else
								{//Si une case est choisie, on met la case choisie partout dans le rectangle
									carteSel.cases[j][i].X1=PatternGrid.getSelectedColumn()+1; 
									carteSel.cases[j][i].Y1=PatternGrid.getSelectedRow()+1;									
								}
								CarteGrid.updateUI();												
							}
					}
					if (Bt_CoucheHaute.isSelected())
					{//Si on a la couche haute
						for (int i=CarteGrid.getSelectedRow();i<=CarteGrid.getSelectionModel().getMaxSelectionIndex();i++)
							for (int j=CarteGrid.getSelectedColumn();j<=CarteGrid.getColumnModel().getSelectionModel().getMaxSelectionIndex();j++)
							{//Pour tout le rectangle
								if (CB_EmptyColor.isSelected())
								{//Empty color, on met 0 partout
									carteSel.cases[j][i].X2=0; 
									carteSel.cases[j][i].Y2=0;
								}
								else
								{//On met la case choisie dans tout le rectangle
									carteSel.cases[j][i].X2=PatternGrid.getSelectedColumn()+1; 
									carteSel.cases[j][i].Y2=PatternGrid.getSelectedRow()+1;									
								}
								CarteGrid.updateUI();												
							}						
					}
			  }
		  }
		  
		  public void mousePressed(MouseEvent e) /**Quand on clique sur la grille map, on remplit la case **/
		  {
			  if(carteSel==null) return;
			  JTable target = (JTable) e.getSource();
			  int row = target.rowAtPoint( e.getPoint() );
			  int column = target.columnAtPoint( e.getPoint() );
			  if ((e.getClickCount() == 2) && (Bt_CoucheEvent.isSelected())) 
			  {//Si double-clic et qu'on écrit sur la couche event
				  Evenements fenev=new Evenements(parent.general,carteSel,column,row);
				  fenev.setVisible(true);//On crée un évenement sur la case + fenêtre
			  }
			  if (Bt_Depart.isSelected())
			  {//Si le buton départ est choisi, on y met le départ
				  parent.general.getDepart().Carte=carteSel.Name;
				  parent.general.getDepart().X=column;
				  parent.general.getDepart().Y=row;
			  }
			  if (Bt_Mort.isSelected())
			  {//Si le buton résu est choisi, on y met le résu
				  parent.general.getMort().Carte=carteSel.Name;
				  parent.general.getMort().X=column;
				  parent.general.getMort().Y=row;
			  }
			  if (Bt_Crayon.isSelected())
			  {
				if (Bt_CoucheBasse.isSelected())
				{	//Si c'est le crayon en couche basse
					if (CB_EmptyColor.isSelected())
					{//On met 0 sur la case
						carteSel.cases[column][row].X1=0; 
						carteSel.cases[column][row].Y1=0;					
					}
					else
					{//Si une ou plusieurs cases sont selectionnées
						for (int i=PatternGrid.getSelectedRow();i<=PatternGrid.getSelectionModel().getMaxSelectionIndex();i++)
							for (int j=PatternGrid.getSelectedColumn();j<=PatternGrid.getColumnModel().getSelectionModel().getMaxSelectionIndex();j++)
							{//On dessine toutes les cases sur la map
								carteSel.cases[column+j-PatternGrid.getSelectedColumn()][row+i-PatternGrid.getSelectedRow()].X1=j+1; 
								carteSel.cases[column+j-PatternGrid.getSelectedColumn()][row+i-PatternGrid.getSelectedRow()].Y1=i+1; 
								CarteGrid.getModel().setValueAt(null, row+i-PatternGrid.getSelectedRow(), column+j-PatternGrid.getSelectedColumn());												
							}		    	                 
					}
				}
				if (Bt_CoucheHaute.isSelected())
				{	//Si crayon couche haute
					if (CB_EmptyColor.isSelected())
					{//Si empty color, on met 0 das la case
						carteSel.cases[column][row].X2=0; 
						carteSel.cases[column][row].Y2=0;					
					}
					else
					{//Si une ou plusieurs cases sont selectionnées
						for (int i=PatternGrid.getSelectedRow();i<=PatternGrid.getSelectionModel().getMaxSelectionIndex();i++)
							for (int j=PatternGrid.getSelectedColumn();j<=PatternGrid.getColumnModel().getSelectionModel().getMaxSelectionIndex();j++)
							{//On dessine toutes les cases sur la map
								carteSel.cases[column+j-PatternGrid.getSelectedColumn()][row+i-PatternGrid.getSelectedRow()].X2=j+1; 
								carteSel.cases[column+j-PatternGrid.getSelectedColumn()][row+i-PatternGrid.getSelectedRow()].Y2=i+1; 
								CarteGrid.getModel().setValueAt(null, row+i-PatternGrid.getSelectedRow(), column+j-PatternGrid.getSelectedColumn());												
							}		    	                 
					}
				}
//				CarteGrid.updateUI();
			  }
		  }
	  });
	  CarteGrid.addMouseMotionListener(new MouseAdapter() 
	  {
		  public void mouseDragged(MouseEvent e) /**Quand on déplace la souris cliquée, on écrit la case**/
		  {
			  JTable target = (JTable) e.getSource();
			  int row = target.rowAtPoint( e.getPoint() );
			  int column = target.columnAtPoint( e.getPoint() );//On récup la case pointée
			  try
			  {
				  if ((Bt_Crayon.isSelected())&&(SwingUtilities.isLeftMouseButton(e))&&((previouscol!=column)||(previousrow!=row)))
				  {
					if (Bt_CoucheBasse.isSelected())
					{	//Crayon couche basse
						if (CB_EmptyColor.isSelected())
						{//Si "empty color", on met 0 dans la case
							carteSel.cases[column][row].X1=0; 
							carteSel.cases[column][row].Y1=0;					
						}
						else
						{//Sinon, on met la case dans la map
							carteSel.cases[column][row].X1=PatternGrid.getSelectedColumn()+1; 
							carteSel.cases[column][row].Y1=PatternGrid.getSelectedRow()+1;
						}
					}
					if (Bt_CoucheHaute.isSelected())
					{	//Crayon couche haute
						if (CB_EmptyColor.isSelected())
						{//Si "empty color", on met 0 dans la case
							carteSel.cases[column][row].X2=0; 
							carteSel.cases[column][row].Y2=0;					
						}
						else
						{//Sinon, on met la case dans la map
							carteSel.cases[column][row].X2=PatternGrid.getSelectedColumn()+1; 
							carteSel.cases[column][row].Y2=PatternGrid.getSelectedRow()+1;
						}
					}
					CarteGrid.getModel().setValueAt(null, row, column);	//On met à jour l'image			
	//				CarteGrid.updateUI();
					previouscol=column;
					previousrow=row;
				  }
			  }catch(Exception e2){}
		  }
	  });
  	  listener = new ComponentAdapter() 
  	  {
		public void componentResized(ComponentEvent evt)/**Quand on redimensionne, redimensionne les composants**/ 
		{
	        Component c = (Component) evt.getSource();	
	        Dimension newSize = c.getSize();//On récup la nouvelle taille
	        //	  scrollpanePG.setBounds(new Rectangle(0,26,226,269));

	        scrollpaneTree.setBounds(new Rectangle(0,newSize.height-290,scrollpaneTree.getWidth(),scrollpaneTree.getHeight()));
	        scrollpanePG.setBounds(new Rectangle(scrollpanePG.getX(),scrollpanePG.getY(),scrollpanePG.getWidth(),scrollpaneTree.getY()-32));
	        scrollpanePG.updateUI();//On met à l'échelle le chipset
	        scrollpaneCG.setBounds(new Rectangle(scrollpaneCG.getX(),scrollpaneCG.getY(),newSize.width-scrollpaneCG.getX()-20,newSize.height-scrollpaneCG.getY()-100));
	        scrollpaneCG.updateUI();//On met à l'échelle la map
	        StatusBar.setBounds(new Rectangle(StatusBar.getX(),newSize.height-100,newSize.width-StatusBar.getX()-20,StatusBar.getHeight()));
  	  	    Bt_Go.setBounds(new Rectangle(newSize.width-StatusBar.getX()-81,0,60,13));//Et le reste
        }
	  };
	  parent.addComponentListener(listener);	  
	  add(scrollpaneCG);//On ajoute la carte à l'interface
	  oldchipset="";
	  oldim=null;
	  }
	
	  private void addMyRightListener(final JTree tree) 
	  {
		MouseListener ml = new MouseAdapter() 
		{
			public void mousePressed(MouseEvent e) /**Quand on fait clic droit sur l'arbre, affiche le menu**/
			{
				if (e.getButton() == MouseEvent.BUTTON3) 
				{//Si clic droit
					int selRow = tree.getRowForLocation(e.getX(), e.getY());
					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());//On récup la map cliquée
					if (selRow != -1) 
					{
						tree.clearSelection();
						tree.setSelectionPath(selPath);
						nodeSel = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
						// Affichage du popup adéquat
						JPopupMenu popup = new JPopupMenu();
						JMenuItem menuItem = new JMenuItem("Ajouter une carte");
						menuItem.addActionListener(new ActionListener()
						{/**Quand on clique ajouter carte, propriétés + création de la carte**/
							public void actionPerformed(ActionEvent e)
							{
								ProprietesCarte propcarte=new ProprietesCarte(null,null,"Propriétés de la carte",true,parent);
								if (propcarte.status==1)//Si cliqué "OK"
								{
								  DefaultMutableTreeNode child = new DefaultMutableTreeNode(propcarte.panel.Ed_Nom.getText());
								  int w=Integer.parseInt(propcarte.panel.Ed_X.getText());//Récup les valeur entrées
								  int h=Integer.parseInt(propcarte.panel.Ed_Y.getText());
								  nodeSel.add(child);
								  if (nodeSel==((DefaultTreeModel)Tree.getModel()).getRoot())
								  {
									  ((DefaultTreeModel)Tree.getModel()).setRoot(null);
									  ((DefaultTreeModel)Tree.getModel()).setRoot(nodeSel);
								  }
								  Tree.expandPath(new TreePath(nodeSel.getPath()));//On add la carte dans l'arbre
								  Projet.Carte c=parent.general.new Carte();//On l'add au projet
								  c.evenements=new Projet.Evenements[w][h];//On y crée un nouveau "Evenement" avec la taille
								  c.cases=new Projet.Case[w][h];
								  for (int i=0;i<w;i++)
									  for(int j=0;j<h;j++)
									  {
										  c.cases[i][j]=parent.general.new Case();
										  c.evenements[i][j]=null;
									  }
								  c.Name=propcarte.panel.Ed_Nom.getText();//On met le nom etc
								  c.TypeCarte=(byte) propcarte.panel.Cb_Type.getSelectedIndex();
								  c.Effect=(byte) propcarte.panel.Cb_Effet.getSelectedIndex();
								  c.Static=propcarte.panel.Ed_Fond.getText();
								  c.Chipset=propcarte.panel.Ed_Chipset.getText();
								  c.Music=propcarte.panel.Ed_Musique.getText();
								  c.DecToResPoint=propcarte.panel.Check_DecRes.isSelected();
								  c.TailleX=(short) w; c.TailleY=(short) h;//La taille...
								  if (nodeSel==((DefaultTreeModel)Tree.getModel()).getRoot())
								    c.Parent="";//Si pas de parent, on met la racine
								  else
								  {//Sinon on met sous la carte parent
							    	TreePath tp = Tree.getSelectionPath();
									c.Parent=tp.getLastPathComponent().toString();
								  }
								  parent.general.getCartes().add(c);//On ajoute avec les autres carte
								}
								propcarte.dispose();//On ferme la fenêtre propriétés
							}
						});
					    popup.add(menuItem);
						menuItem = new JMenuItem("Propriétés");
						menuItem.addActionListener(new ActionListener()
						{/**Quand on clique sur "Propriétés", on ouvre la fenêtre propriétés + on update la map**/
							public void actionPerformed(ActionEvent e)
							{
								if (nodeSel==((DefaultTreeModel)Tree.getModel()).getRoot())
								{
									//Si c'est la racine, on fait rien
								}
								else
								{//Sinon, c'est une carte, donc on ouvre la fenetre propriétés
									ProprietesCarte propcarte=new ProprietesCarte(carteSel,null,"Propriétés de la carte",true,parent);
									if (propcarte.status==1)//Si on a cliqué sur OK
									{
									  int w=Integer.parseInt(propcarte.panel.Ed_X.getText());
									  int h=Integer.parseInt(propcarte.panel.Ed_Y.getText());//On récup largeur hauteur
									  carteSel.resizeEvenement(w,h);
									  carteSel.resizeCase(w, h);//On applique le nouvelles dimensions
									  carteSel.Name=propcarte.panel.Ed_Nom.getText();
									  nodeSel.setUserObject(carteSel.Name);
									  ((DefaultTreeModel)Tree.getModel()).nodeChanged(nodeSel); 
									  carteSel.TypeCarte=(byte) propcarte.panel.Cb_Type.getSelectedIndex();
									  carteSel.Effect=(byte) propcarte.panel.Cb_Effet.getSelectedIndex();
									  carteSel.Static=propcarte.panel.Ed_Fond.getText();
									  carteSel.Chipset=propcarte.panel.Ed_Chipset.getText();
									  carteSel.Music=propcarte.panel.Ed_Musique.getText();//On applique les autre param
									  carteSel.DecToResPoint=propcarte.panel.Check_DecRes.isSelected();
									  carteSel.TailleX=(short) w; carteSel.TailleY=(short) h;
			
									  ImageFilter cif2;
						    		  ImageProducer improd;
						    		  Image Image=null;
						    		  String _Image=System.getProperty("user.dir")+"/"+parent.NomCarte+"/"+carteSel.Chipset.replace("Chipset\\", "Chipset/");
						    		  if (new File(_Image).exists())//On modifie le chipset
						    		  {
						    			  ImageLoader im=new ImageLoader(null);
						    			  Image=im.loadImage(_Image);		
						    		  }
						    		  Source= new Image[Image.getWidth(null)/16][Image.getHeight(null)/16];
						    		  improd=Image.getSource();
						    		  for (int i=0;i<Source.length;i++)
						    		  {//On recrée la  grille
						    			for(int j=0;j<Source[i].length;j++)
						    			{//On y met les cases
						    			  	cif2 =new CropImageFilter(i*16, j*16,16,16);
						    			   	Source[i][j] = createImage(new FilteredImageSource(improd, cif2));
						    			   	Source[i][j]=Source[i][j].getScaledInstance(32, 32, BufferedImage.SCALE_SMOOTH); 
						    			}
						    		  }
						    		  sourceModel sm=new sourceModel();
						    		  PatternGrid.setModel(sm);
						    		  for(int i=0;i<PatternGrid.getColumnCount();i++)
						    		  {//On définit la taille etc
						    			  TableColumn col = PatternGrid.getColumnModel().getColumn(i); 
						    			  col.setPreferredWidth(32); 
						    		  }
						    	      targetModel tm=new targetModel();
						    	      tm.setColumnCount(carteSel.TailleX);
						    	      tm.setRowCount(carteSel.TailleY);
						    		  CarteGrid.setModel(tm);//On met à jour la map
						    		  for(int i=0;i<CarteGrid.getColumnCount();i++)
						    		  {//On définit la taille des colonnes
						    			  TableColumn col = CarteGrid.getColumnModel().getColumn(i); 
						    			  col.setPreferredWidth(32); 
						    		  }
									}
									propcarte.dispose();//On ferme la fenêtre propriétés
								}
							}
						});
					    popup.add(menuItem);
						menuItem = new JMenuItem("Changer le parent");
						menuItem.addActionListener(new ActionListener()
						{/**Quand on clique changer le parent, ouvre une liste et change le parent **/
							@SuppressWarnings("unchecked")
							public void actionPerformed(ActionEvent e)
							{
							    if (nodeSel==((DefaultTreeModel)Tree.getModel()).getRoot())
							    {
							    	//Si c'est la racine, on fait rien
							    }
							    else
							    {//Si c'est une carte...
									String[] Cartes=new String[parent.general.getCartes().size()+1];
							        Enumeration<DefaultMutableTreeNode> en = ((DefaultMutableTreeNode) Tree.getModel().getRoot()).breadthFirstEnumeration();
							        
							        //iterate through the enumeration
							        int i=0;
							        while(en.hasMoreElements())//On liste les cartes
							        {
							            //get the node
							        	DefaultMutableTreeNode node = (DefaultMutableTreeNode)en.nextElement();
							            Cartes[i]=node.getUserObject().toString();
							            i++;
							        }
									Select chgparent=new Select(Cartes,null,"Changer le parent",true);//On choisit
									if (chgparent.status==1)//Si cliqué sur "OK"
									{
										if (chgparent.resultat.compareTo(carteSel.Name)==0)//Si elle-même
										{
											JOptionPane.showMessageDialog(null, "Le parent de cette carte ne peut être elle même!");									
										}
										else
										{//On va changer le parent
											DefaultMutableTreeNode prt;
											if (chgparent.resultat.compareTo(parent.general.getName())==0)//Si c'est la racine
												prt=(DefaultMutableTreeNode) ((DefaultTreeModel)Tree.getModel()).getRoot();
											else
											    prt=parent.searchNode(chgparent.resultat);//Si c'est une carte
											if (prt!=null)
											{
												((DefaultTreeModel)Tree.getModel()).removeNodeFromParent(nodeSel);
												prt.add(nodeSel);
											    if (prt==((DefaultTreeModel)Tree.getModel()).getRoot())
											    {//Si c'est la racine, on met la racine en parent
											    	carteSel.Parent="";
											    	((DefaultTreeModel)Tree.getModel()).setRoot(null);
											    	((DefaultTreeModel)Tree.getModel()).setRoot(prt);
											    }
											    else
											    	carteSel.Parent=chgparent.resultat;//Sinon on met la carte
											}
											else
												JOptionPane.showMessageDialog(null, "Parent introuvable!");
										}
									}
									chgparent.dispose();//On ferme la liste
							    }
							}
						});
					    popup.add(menuItem);
					    popup.addSeparator();
						menuItem = new JMenuItem("Supprimer carte");
						menuItem.addActionListener(new ActionListener()
						{/**Si on clique sur "Supprimer carte, on demande confirmation et on supprime**/
							  @SuppressWarnings("unchecked")
							public void actionPerformed(ActionEvent e)
							{
							    if (nodeSel==((DefaultTreeModel)Tree.getModel()).getRoot())
							    {
							    	//Si c'est la racine, on ne fait rien
							    }
							    else
							    {
							    	if (JOptionPane.showConfirmDialog(null,
							                "Etes vous sûr de vouloir effacer cette carte?",
							                "Effacer",
							                JOptionPane.YES_NO_OPTION,//On demande confirmation
							                JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)
							    	{
							    		ArrayList<Carte> ct=parent.general.getCartes();
								        Enumeration<DefaultMutableTreeNode> en = nodeSel.breadthFirstEnumeration();
								        
								        //iterate through the enumeration
								        while(en.hasMoreElements())
								        {//On cherche la carte et on la supprime
								            //get the node
								        	DefaultMutableTreeNode node = (DefaultMutableTreeNode)en.nextElement();
								            ct.remove(parent.general.getIndexCarteByName(node.getUserObject().toString()));
								        }
										((DefaultTreeModel)Tree.getModel()).removeNodeFromParent(nodeSel);
							    	}//Et on supprime de l'arbre
							    }
							}
						});
					    popup.add(menuItem);//On affiche le menu clic droit
					    popup.show(e.getComponent(),e.getX(), e.getY());
					}
				}
			}
		};
		tree.addMouseListener(ml);
	  } 
/**################################################################**/
	  public class sourceModel extends AbstractTableModel 
/**################################################################
 * Sert à l'affichage du chipset**/
	  {
		private static final long serialVersionUID = 1L;
		
		public int getColumnCount() { if (Source!=null) return Source.length; else return 0; }
		//Largeur
		
	    public int getRowCount() { if (Source!=null) return Source[0].length; else return 0; }
	    //Hauteur
	    
	    public Object getValueAt(int row, int col) 
	    {/**Définit le rendu d'une case du chipset**/
	    	if (Source!=null)
	    	{
	    		boolean selected=((PatternGrid.getSelectedRow()<=row) && (PatternGrid.getSelectionModel().getMaxSelectionIndex()>=row)
	    	                    && (PatternGrid.getSelectedColumn()<=col) && (PatternGrid.getColumnModel().getSelectionModel().getMaxSelectionIndex()>=col));
	    		//Si la case est selectionnée
		    	if (selected)
		    	{
			    	ImageFilter filter = new GetFilter();//On dessine l'image et on ajoute un filtre
			    	FilteredImageSource filteredSrc = new FilteredImageSource(Source[col][row].getSource(), filter);

			    	// Create the filtered image
			    	return new ImageIcon(Toolkit.getDefaultToolkit().createImage(filteredSrc));
		    	}
		    	else // Si elle n'estpas selectionnée, on dessine juste l'image
		    		return new ImageIcon(Source[col][row]);
	    	}
	    	else
	    	  return null;
	    }
	    public Class<ImageIcon> getColumnClass(int column)  {return ImageIcon.class;}    
	  };
	  
	  
/**################################################################**/
	  private class GetFilter extends RGBImageFilter 
/**################################################################
 * Filtre pour les images indexées autrement qu'en RGB, pour les convertir en RGB**/
	  {
	        public GetFilter() {
	            // When this is set to true, the filter will work with images
	            // whose pixels are indices into a color table (IndexColorModel).
	            // In such a case, the color values in the color table are filtered.
	            canFilterIndexColorModel = true;
	        }

	        // This method is called for every pixel in the image
	        public int filterRGB(int x, int y, int rgb) 
	        {
	    		Color c = new Color(rgb);
	    		int red=c.getRed()-30; if (red<0) red=0;
	    		int green=c.getGreen()-30; if (green<0) green=0;
	    		return new Color(red,green,c.getBlue()).getRGB();
	        }
	  }

/**################################################################**/	  
	  public class targetModel extends AbstractTableModel 
/**################################################################
 * Sert au rendu des cases de la map, avec les events/couches etc**/
	  {
			private static final long serialVersionUID = 1L;
			private int RowCount;
			private int ColCount;
			
			/**##################### Renvoie la largeur ################**/
			public int getColumnCount() { return this.ColCount;}
			
			/**##################### Renvoie la hauteur #######################**/
		    public int getRowCount() { return this.RowCount;}
		    
		    /**##################### Définit la hauteur #####################**/
		    public void setRowCount(int RowCount) {this.RowCount=RowCount;}
		    
		    /**################### Définit la largeur ################**/
		    public void setColumnCount(int ColCount) {this.ColCount=ColCount;}
		    
		    public Object getValueAt(int row, int col) 
		    {
		    	ImageIcon ReturnIM=null;
		    	boolean selected=((CarteGrid.getSelectedRow()<=row) && (CarteGrid.getSelectionModel().getMaxSelectionIndex()>=row)
			    	            && (CarteGrid.getSelectedColumn()<=col) && (CarteGrid.getColumnModel().getSelectionModel().getMaxSelectionIndex()>=col));
		    	//Définit si une case est selectionnée ou non
		    	
		    	int Zoom=1;
		    	if (Bt_Zoom1.isSelected()) Zoom=8;//Zoom normal
		    	if (Bt_Zoom2.isSelected()) Zoom=4;//1/2
		    	if (Bt_Zoom4.isSelected()) Zoom=2;//1/4, sinon 1/8
		    	
		    	//RGBImageFilter
			    if (Source!=null)
			    {
			    	try
			    	{
				    	if ((carteSel.Name.compareTo(parent.general.getDepart().Carte)==0) && (col==parent.general.getDepart().X) && (row==parent.general.getDepart().Y) && (Cb_Affiche.getSelectedIndex()==0))
				    	{//S'il y a la case "Départ" sur cette case de la map, renvoie l'image du spawn
				    		    ReturnIM=new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/resources/departori.png")).getScaledInstance(4*Zoom, 4*Zoom, Image.SCALE_FAST));
				    	}
				    	else if ((carteSel.Name.compareTo(parent.general.getMort().Carte)==0) && (col==parent.general.getMort().X) && (row==parent.general.getMort().Y) && (Cb_Affiche.getSelectedIndex()==0))
				    	{//S'il y a la case "résu" sur cette case de la map, renvoie l'image du résu
				    		    ReturnIM=new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/resources/deadori.png")).getScaledInstance(4*Zoom, 4*Zoom, Image.SCALE_FAST));
				    	}
				    	else if ((carteSel.cases[col][row].X1>0) && (carteSel.cases[col][row].Y1>0) && ((Cb_Affiche.getSelectedIndex()==0)||(Cb_Affiche.getSelectedIndex()==1)))
					    {//Si couche 1
					    	if ((carteSel.cases[col][row].X2>0)&&(carteSel.cases[col][row].Y2>0) && (Cb_Affiche.getSelectedIndex()==0))
					    	{//Si aussi couche2
					    		BufferedImage ico=new BufferedImage(4*Zoom, 4*Zoom, BufferedImage.TYPE_INT_RGB);
					    		if (Zoom==8)//Génère la case de la map
					    		{
					    			ico.getGraphics().drawImage(Source[carteSel.cases[col][row].X1-1][carteSel.cases[col][row].Y1-1], 0, 0, null);
					    			ico.getGraphics().drawImage(Source[carteSel.cases[col][row].X2-1][carteSel.cases[col][row].Y2-1], 0, 0, null);
					    		}
					    		else//Génère la case avec un zoom
					    		{
					    			ico.getGraphics().drawImage(Source[carteSel.cases[col][row].X1-1][carteSel.cases[col][row].Y1-1].getScaledInstance(4*Zoom, 4*Zoom, Image.SCALE_FAST), 0, 0, null);
					    			ico.getGraphics().drawImage(Source[carteSel.cases[col][row].X2-1][carteSel.cases[col][row].Y2-1].getScaledInstance(4*Zoom, 4*Zoom, Image.SCALE_FAST), 0, 0, null);
					    		}
					    		ReturnIM=new ImageIcon(ico);
					    	}
					    	else
					    	{//Si seulement couche 1
					    		if (Zoom==8)//Pareil mais seulement couche basse affichée
					    			ReturnIM= new ImageIcon(Source[carteSel.cases[col][row].X1-1][carteSel.cases[col][row].Y1-1]);
					    		else
					    			ReturnIM= new ImageIcon(Source[carteSel.cases[col][row].X1-1][carteSel.cases[col][row].Y1-1].getScaledInstance(4*Zoom, 4*Zoom, Image.SCALE_FAST));
					    	}
					    }
					    else if ((carteSel.cases[col][row].X2>0) && (carteSel.cases[col][row].Y2>0) && ((Cb_Affiche.getSelectedIndex()==0)||(Cb_Affiche.getSelectedIndex()==2)))
					    {//Si seulement couche 2
					    	if (Zoom==8)//Pareil mais seulement couche haute affichée
					    		ReturnIM= new ImageIcon(Source[carteSel.cases[col][row].X2-1][carteSel.cases[col][row].Y2-1]);
					    	else
					    		ReturnIM= new ImageIcon(Source[carteSel.cases[col][row].X2-1][carteSel.cases[col][row].Y2-1].getScaledInstance(4*Zoom, 4*Zoom, Image.SCALE_FAST));
					    }
				    	else
				    	    ReturnIM=null;
			    	}catch(NullPointerException e){ReturnIM=null;}
			    }
			    else
			    	return null;

			    if ((Bt_CoucheEvent.isSelected()) && carteSel!=null && (carteSel.evenements[col][row]!=null))
		    	{//Si il y a un événement sur la case, on le dessine
		    		BufferedImage ico=new BufferedImage(4*Zoom, 4*Zoom, BufferedImage.TYPE_INT_RGB);
		            Graphics2D g2d = ico.createGraphics();			        
		            g2d.setComposite(AlphaComposite.Src);
		            if ((carteSel.evenements[col][row].evenement.get(0).Chipset.compareTo("")!=0)&&(carteSel.evenements[col][row].evenement.get(0).Visible==true))
		            {//S'il y a une image d'évent
			    		Projet.Evenement ev=carteSel.evenements[col][row].evenement.get(0);
			    		ImageLoader im=new ImageLoader(null);
			    		if (oldchipset.compareTo(ev.Chipset)!=0)
			    		{
			    			oldim=im.loadImage(System.getProperty("user.dir")+"/"+parent.general.getName()+"/"+ev.Chipset.replace("Chipset\\", "Chipset/"));
			    			oldchipset=ev.Chipset;
			    		}
			    		ImageFilter cif2;
			    		if (carteSel.evenements[col][row].evenement.get(0).TypeAnim==3)
							 cif2=new CropImageFilter(ev.X,ev.Y,ev.W,ev.H);
			    		else
			    		if (carteSel.evenements[col][row].evenement.get(0).TypeAnim==4)
							 cif2=new CropImageFilter(ev.X+(ev.NumAnim*ev.W),
										ev.Y+(2*ev.H),
										ev.W,ev.H);
			    		else
							 cif2=new CropImageFilter(ev.X+(ev.NumAnim*ev.W),
										ev.Y+(ev.Direction*ev.H),
										ev.W,ev.H);
						Image imaffiche = createImage(new FilteredImageSource(oldim.getSource(), cif2));
			    		g2d.drawImage(imaffiche,0,0,4*Zoom,4*Zoom,null);
		            }
		            else
		            {//S'il n'y en a pas
		            	g2d.drawImage(ReturnIM.getImage(),0,0,4*Zoom,4*Zoom,null);
		            }
		            //On dessine la case jaune
		            Stroke stroke=new BasicStroke(3);
		            g2d.setColor(Color.yellow);
		            g2d.setStroke(stroke);
		            g2d.drawRect(3, 3, (4*Zoom)-4, (4*Zoom)-4);
		            g2d.dispose();
		    		ReturnIM=new ImageIcon(ico);
		    	}

			    if ((ReturnIM!=null)&&(selected))
			    {//Si la case est selectionnée, on ajoute un léger filtre dessus pour indiquée qu'elle est selectionnée
			    	ImageFilter filter = new GetFilter();
			    	FilteredImageSource filteredSrc = new FilteredImageSource(ReturnIM.getImage().getSource(), filter);

			    	// Create the filtered image
			    	ReturnIM = new ImageIcon(Toolkit.getDefaultToolkit().createImage(filteredSrc));
			    }
			    StatusBar.updateUI();
			    return ReturnIM;
	  		}
		    public Class<ImageIcon> getColumnClass(int column)  {return ImageIcon.class;}
	  };
	 
/**################################################################**/
/**Renvoie l'arbre de maps**/
	  public JTree getTree() { return this.Tree; }
	  
/**################################################################**/
	  private class JStatusBar extends JPanel
/**################################################################
 * Barre d'état en bas qui affiche les coordonnées**/
	  {
		private static final long serialVersionUID = 1L;
		public void paintComponent(Graphics g)
		{
		  super.paintComponent(g);
	      Graphics2D g2d = (Graphics2D)g;
    	  FontMetrics fontMetrics = g2d.getFontMetrics();
	    
	      Font font = new Font("MS Sans Serif", Font.PLAIN, 10);
	    
	      g2d.setFont(font);
		  g2d.drawString("X : "+CarteGrid.getSelectedColumn()+" Y : "+CarteGrid.getSelectedRow(), 230, fontMetrics.getHeight()-5);
		}		  
	  }
	  
}

/**################################################################**/
class FenetreSimple extends JFrame
/**################################################################
 * Fenetre personnalisée pour l'éditeur**/
{
	private static final long serialVersionUID = 1L;
	private static final float version=0.80f;
	private FenetreSimple FrmSrc;
	public JTabbedPane tabbedPane;
	public Contenu monContenu;
	public Blocage monBlocage;
	public ZoneMonstre monZoneMonstre; //Les onglets et autres
	public OngletStats stats;
	public OngletClasses classes;
	public OngletObjets objets;
	public OngletMagies magies;
	public OngletMonstres monstres;
	public Projet general;
	public String NomCarte;
	public JMenuItem mnu_enregistrer,mnu_proprietes,mnu_importer,mnu_exporter_SO,mnu_importer_SO;
	
/**################################################################**/
	public void MenuCopier()
/**################################################################
 * Copie un event ou d'une/plusieurs cases**/
	{
		if ((monContenu!=null) && (general!=null))
		{
			if (monContenu.carteSel!=null)
			{
				monContenu.CopyRectangle=null;
				monContenu.CopyEvents=null;
				if ((monContenu.Bt_CoucheBasse.isSelected())||(monContenu.Bt_CoucheHaute.isSelected())) //Copie cases
				{
					boolean couchebasse=false;
					if (monContenu.Bt_CoucheBasse.isSelected())
						couchebasse=true;
					monContenu.CopyRectangle=new Case[monContenu.CarteGrid.getColumnModel().getSelectionModel().getMaxSelectionIndex()-monContenu.CarteGrid.getSelectedColumn()+1][monContenu.CarteGrid.getSelectionModel().getMaxSelectionIndex()-monContenu.CarteGrid.getSelectedRow()+1];
					for (int i=0;i<monContenu.CopyRectangle.length;i++)
						for (int j=0;j<monContenu.CopyRectangle[i].length;j++)
						{
							monContenu.CopyRectangle[i][j]=general.new Case();
							if (couchebasse)//Copie la couche basse
							{
								monContenu.CopyRectangle[i][j].X1=monContenu.carteSel.cases[i+monContenu.CarteGrid.getSelectedColumn()][j+monContenu.CarteGrid.getSelectedRow()].X1;
								monContenu.CopyRectangle[i][j].Y1=monContenu.carteSel.cases[i+monContenu.CarteGrid.getSelectedColumn()][j+monContenu.CarteGrid.getSelectedRow()].Y1;
							}
							else//Copie la couche haute
							{
								monContenu.CopyRectangle[i][j].X1=monContenu.carteSel.cases[i+monContenu.CarteGrid.getSelectedColumn()][j+monContenu.CarteGrid.getSelectedRow()].X2;
								monContenu.CopyRectangle[i][j].Y1=monContenu.carteSel.cases[i+monContenu.CarteGrid.getSelectedColumn()][j+monContenu.CarteGrid.getSelectedRow()].Y2;
							}
						}
				}
				else // Copie event
				{
					monContenu.CopyEvents=new Projet.Evenements[monContenu.CarteGrid.getColumnModel().getSelectionModel().getMaxSelectionIndex()-monContenu.CarteGrid.getSelectedColumn()+1][monContenu.CarteGrid.getSelectionModel().getMaxSelectionIndex()-monContenu.CarteGrid.getSelectedRow()+1];
					for (int i=0;i<monContenu.CopyEvents.length;i++)
						for (int j=0;j<monContenu.CopyEvents[i].length;j++)
						{
							if (monContenu.carteSel.evenements[i+monContenu.CarteGrid.getSelectedColumn()][j+monContenu.CarteGrid.getSelectedRow()]!=null)
								monContenu.CopyEvents[i][j]=monContenu.carteSel.evenements[i+monContenu.CarteGrid.getSelectedColumn()][j+monContenu.CarteGrid.getSelectedRow()].clone();
							else
								monContenu.CopyEvents[i][j]=null;
						}								
				}
			}
		}
	}
/**################################################################**/
	public void MenuColler()
/**################################################################
 * Colle un event ou des cases copiées**/
	{
		if ((monContenu!=null) && (general!=null))
		{
			if ((monContenu.carteSel!=null) && (monContenu.CopyRectangle!=null))// Colle des cases
			{
				boolean couchebasse=false;
				if (monContenu.Bt_CoucheBasse.isSelected())
					couchebasse=true;
				for (int i=0;i<monContenu.CopyRectangle.length;i++)
					for (int j=0;j<monContenu.CopyRectangle[i].length;j++)
					{
						if ((i+monContenu.CarteGrid.getSelectedColumn()<monContenu.carteSel.TailleX)&&(j+monContenu.CarteGrid.getSelectedRow()<monContenu.carteSel.TailleY))
						{
							if (couchebasse) // Colle couche basse
							{
								monContenu.carteSel.cases[i+monContenu.CarteGrid.getSelectedColumn()][j+monContenu.CarteGrid.getSelectedRow()].X1=monContenu.CopyRectangle[i][j].X1;
								monContenu.carteSel.cases[i+monContenu.CarteGrid.getSelectedColumn()][j+monContenu.CarteGrid.getSelectedRow()].Y1=monContenu.CopyRectangle[i][j].Y1;
							}
							else // Colle couche haute
							{
								monContenu.carteSel.cases[i+monContenu.CarteGrid.getSelectedColumn()][j+monContenu.CarteGrid.getSelectedRow()].X2=monContenu.CopyRectangle[i][j].X1;
								monContenu.carteSel.cases[i+monContenu.CarteGrid.getSelectedColumn()][j+monContenu.CarteGrid.getSelectedRow()].Y2=monContenu.CopyRectangle[i][j].Y1;
							}
						}
					}
				monContenu.CarteGrid.updateUI();// Update de l'affichage
			}
			else if ((monContenu.carteSel!=null) && (monContenu.CopyEvents!=null))// Colle un event.
			{
				for (int i=0;i<monContenu.CopyEvents.length;i++)
					for (int j=0;j<monContenu.CopyEvents[i].length;j++)
					{
						if ((i+monContenu.CarteGrid.getSelectedColumn()<monContenu.carteSel.TailleX)&&(j+monContenu.CarteGrid.getSelectedRow()<monContenu.carteSel.TailleY))
						{
							if (monContenu.CopyEvents[i][j]!=null)
								monContenu.carteSel.evenements[i+monContenu.CarteGrid.getSelectedColumn()][j+monContenu.CarteGrid.getSelectedRow()]=monContenu.CopyEvents[i][j].clone();
							else
								monContenu.carteSel.evenements[i+monContenu.CarteGrid.getSelectedColumn()][j+monContenu.CarteGrid.getSelectedRow()]=null;
						}
					}								
				
				monContenu.CarteGrid.updateUI();// Update de l'affichage
			}
		}					
	}
	
/**################################################################**/
	public class FilterImage extends javax.swing.filechooser.FileFilter 
/**################################################################
	 * Filtre pour la selection des chipsets (Tous les .png)**/
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
	public class FilterSound extends javax.swing.filechooser.FileFilter 
/**################################################################
 * Filtre pour la sélection des sons (Tous les .wav et .mp3)**/
	{ 
	  public boolean accept(File file) 
	  { 
		if (file.isDirectory())
		      return true;
		String filename = file.getName(); 
		return ((filename.endsWith(".wav") || (filename.endsWith(".mp3")))); 
	  } 
	  public String getDescription() 
	  { 
		  return "*.wav;*.mp3"; 
	  } 
	}
	
/**################################################################**/
	class FilterMusic extends javax.swing.filechooser.FileFilter 
/**################################################################
 * Filtre pour la séléction des musiques (.mid et .mp3)**/
	{ 
	  public boolean accept(File file) 
	  { 
		if (file.isDirectory())
		      return true;
		String filename = file.getName(); 
		return ((filename.endsWith(".mid") || (filename.endsWith(".mp3")))); 
	  } 
	  public String getDescription() 
	  { 
		  return "*.mid;*.mp3"; 
	  } 
	}
	
/**################################################################**/
    public void copyfile(String srFile, String dtFile)
/**################################################################
 * Copie un fichier srFile vers dtFile **/
    {
	    try{
	      File f1 = new File(srFile);
	      File f2 = new File(dtFile);
	      InputStream in = new FileInputStream(f1);
	      
	      //For Append the file.
//		      OutputStream out = new FileOutputStream(f2,true);

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

/**################################################################**/
    public void Sauvegarder()
/**################################################################
 * Sauvegarde le projet courant**/
    {
    	try 
    	{
			//creation du flux
			general.setName(NomCarte);
			ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(System.getProperty("user.dir")+"/"+NomCarte+"/"+NomCarte+".prj"));
			try
			{		
				//ecriture de l'objet
				out.write(Projet.PROJET_VERSION);
				out.writeObject(general);
				out.flush();
			}
			finally
			{
				out.close();
			}
			//lecture de l'objet
		} 
		catch (IOException e1) {
			e1.printStackTrace();
		}				
    }
    
/**################################################################**/
    public void ExportSO()
/**################################################################
 * Sauvegarde projet compatible SO**/
    {
    	File dossierSO=new File("prjSO");
    	if(!dossierSO.exists())
    	{
    		dossierSO.mkdir();//Si le dossier prjSO n'existe pas, le crée
    	}
    	if(dossierSO.isDirectory())
		{//Demande le nom du projet
			String nomProj=JOptionPane.showInputDialog("Nom du projet ?");
			if(nomProj!=null && !nomProj.equals(""))
			{//Crée les dossiers
				File dossierPrj=new File("prjSO"+File.separator+nomProj);
				if(!dossierPrj.isDirectory()) dossierPrj.mkdir();
				File dossierChip=new File("prjSO"+File.separator+nomProj+File.separator+"Chipset");
				if(!dossierChip.isDirectory()) dossierChip.mkdir();
				File dossierMaps=new File("prjSO"+File.separator+nomProj+File.separator+"Maps");
				if(!dossierMaps.isDirectory()) dossierMaps.mkdir();
				File dossierSound=new File("prjSO"+File.separator+nomProj+File.separator+"Sound");
				if(!dossierSound.isDirectory()) dossierSound.mkdir();
				sauvegardeSO(nomProj);//Et sauvegarde
			}
		}
    	else
    	{
    		JOptionPane.showMessageDialog(this, "Sauvegarde impossible, supprimez «prjSO»", "Erreur", JOptionPane.ERROR_MESSAGE);
    	}
    }
    
/**################################################################**/
	public void sauvegardeSO(String nomProj)
/**################################################################
* Sauvegarde le projet et la/les map(s)**/
	{
		ArrayList<Carte> carte=this.general.getCartes();
		for(int i=0;i<carte.size();i++)
		{//Sauvegarde toutes les maps, 
			saveMapSO(carte.get(i),nomProj);//d'abord les .map
			saveEv0SO(carte.get(i),nomProj);//Puis les .ev0
			
			//TODO @BETA .evn
			//TODO @BETA .zon
			//TODO @BETA .mon
			//TODO @BETA .obj
			//TODO @BETA .mag
			//TODO @ALPHA2 copier dossiers sound chipset
		}
		savePrjSO(nomProj);
		//Enfin, sauvegarde le .prj
	}
    
/**################################################################**/
	public void saveMapSO(Carte Carte0,String Projet)
/**################################################################
* Enregistre le .map passé en paramètre dans le dossier du Projet**/
	{
		FileWriter Writer = null;
		
		String Cart=Carte0.Name;//Récup cases et nom carte etc
		Case[][] cases=Carte0.cases;
		//Définition du filename
		String filename="prjSO"+File.separator+ Projet + File.separator +"Maps"+File.separator+ Cart+".map";
		System.out.print("Export: " +filename+"\n");
		try
		{ 
			Writer = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(Writer);//On ouvre le .map en écriture
			for(int i=0;i<Carte0.TailleX;i++)
			{//Pour toutes les cases
				for(int j=0;j<Carte0.TailleY;j++)
				{//On écrit X couche basse,X couche haute,Y couche basse, Y couche haute
					out.write(cases[i][j].X1 + "\r\n" + cases[i][j].X2 );
					out.write("\r\n" + cases[i][j].Y1 + "\r\n" + cases[i][j].Y2 + "\r\n");
				}	
			}
			out.close();//On ferme le fichier
		}catch(IOException e){e.printStackTrace();}
	}
	
/**################################################################**/
	public void saveEv0SO(Carte Carte0,String Projet)
/**################################################################
* Enregistre le .ev0 passé en paramètre dans le dossier du Projet**/
	{
		FileWriter Writer = null;
		String Cart=Carte0.Name;//Récup nom de la map et events
		struct.Projet.Evenements[][] Event = Carte0.evenements;
		
		String filename="prjSO"+File.separator+Projet + File.separator +"Maps"+File.separator+ Cart+".ev0";
		//Définition du filename
		System.out.print("Export: " +filename+"\n");
		try
		{ 
			Writer = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(Writer);//On ouvre le .ev0 en écriture
			for(int i=0;i<Carte0.TailleX;i++)
			{//Pour toutes les cases
				for(int j=0;j<Carte0.TailleY;j++)
				{
					
					if(Event[i][j] != null && !Event[i][j].equals(""))
					{
						for(int k=0;k<Event[i][j].CondDecl.size();k++)//Chaque page CD
							for(int l=0;l<Event[i][j].CondDecl.get(k).size();l++)//Chaque ligne CD
								out.write("CD"+i+","+j+","+k+","+l+"="+Event[i][j].CondDecl.get(k).get(l) + "\r\n");	
						for(int k=0;k<Event[i][j].CommandeEv.size();k++)//Chaque paage CE
							for(int l=0;l<Event[i][j].CommandeEv.get(k).size();l++)//Chaque ligne CE
								out.write("EV"+i+","+j+","+k+","+l+"="+Event[i][j].CommandeEv.get(k).get(l) + "\r\n");	
					}
				}	
			}
			out.close();//On ferme le fichier
		}catch(IOException e){e.printStackTrace();}
	}
	
/**################################################################**/
	public void savePrjSO(String Projet)//TODO @BETA Sound etc
/**################################################################
* Sauvegarde le .prj défini dans la variable de classe Projet**/
	{
		int pos=0;//Position dans le fichier
		FileOutputStream Writer = null;
		ArrayList<Carte> carte=this.general.getCartes();//Récup la liste de cartes
		
		String filename="prjSO"+File.separator+Projet + File.separator + Projet+ ".prj";//Définition du chemin vers le prj
		System.out.print("Export: " +filename + "\n");
		try
		{ 
			Writer = new FileOutputStream(filename);
			BufferedOutputStream out = new BufferedOutputStream(Writer);//On ouvre le prj en écriture
			for(int i=0;i<carte.size();i++,pos=0)
			{
				
				out.write(carte.get(i).Name.length());pos++;//On écrit la taille de la map
				out.write(carte.get(i).Name.getBytes());pos=pos+carte.get(i).Name.length();//Le nom
				while(pos<155){out.write(0);pos++;}//On met des 0x00 jusqu'en 155
				out.write(carte.get(i).Chipset.length());pos++;//On écrit la taille du Chipset
				out.write(carte.get(i).Chipset.replace(File.separatorChar,'\\').getBytes());pos=pos+carte.get(i).Chipset.length();//Son nom+chemin
				while(pos<206){out.write(0);pos++;}//On met des 0x00 jusqu'en 206
				out.write(carte.get(i).TailleX);pos++;out.write(0);pos++;//On écrit la largeur, un 0x00
				out.write(carte.get(i).TailleY);pos++;//Et la hauteur
				while(pos<=777){out.write(0);pos++;}//On termine le fichier avec des 0x00
			}
			
			
			out.close();//On ferme le fichier
		}catch(IOException e){e.printStackTrace();}
	}
    
/**################################################################**/
	public void chargPrjSO()
/**################################################################
* Charge un fichier Prj et la/les map(s) associée(s)**/		
	{
		File dir2 = new File(System.getProperty("user.dir")+File.separator+"prjSO");
		 FileFilter fileFilter2 = new FileFilter() 
		 { 
			 public boolean accept(File file) {
				 return file.isDirectory(); 
			 } 
		 }; 
		 File[] files2 = dir2.listFiles(fileFilter2);//On crée la liste des dossiers
		 ArrayList<String> Projects=new ArrayList<String>();
		 for(int i=0;i<files2.length;i++)
		 {
			 File subdir = new File(files2[i].getAbsolutePath());
			 File[] prj = subdir.listFiles();
			 for(int j=0;j<prj.length;j++)
			 {
				 String extension;
			     int dotPos = prj[j].getName().lastIndexOf(".");
			     if (dotPos>=0)
			     {
				     extension = prj[j].getName().substring(dotPos);//On garde ceux qui contiennent un .prj
				     if ((extension.compareTo(".prj")==0)&&(prj[j].getName().substring(0,dotPos).compareTo(files2[i].getName())==0))
				    	 Projects.add(files2[i].getName());
			     }
			 }						 
		 }
		 String[] projstr=new String[Projects.size()];
		 projstr=Projects.toArray(projstr);
		 JListe liste=new JListe(projstr,null,"Choisissez le projet (Dans dossier «prjSO»",true);//On crée la liste
		 /*JFileChooser choix = new JFileChooser();				     
	     int retour = choix.showOpenDialog(null);
	     if(retour == JFileChooser.APPROVE_OPTION) 
	     {*/
		 
		 if (liste.ListBox.getSelectedValue()==null || liste.status!=1)//Si un projet est selectionné
		 {
			 return;
		 }
//				    	NomCarte=choix.getSelectedFile().getName();						
//				    	NomCarte=NomCarte.substring(0, NomCarte.indexOf("."));
		String Projet=liste.ListBox.getSelectedValue().toString();
		
		
		
		int NombreMaps=0;
		if(Projet==null || Projet.equals(""))
		{
			return;
		}
		NomCarte=Projet;
		new File(NomCarte).mkdir();
		new File(NomCarte+"/Chipset").mkdir();//Les dossiers
		new File(NomCarte+"/Sound").mkdir();
	    File dir = new File(System.getProperty("user.dir")+"/Ressources/Chipset");
		 FileFilter fileFilter = new FileFilter() 
		 { 
			 public boolean accept(File file) 
			 {
				 return (!file.isDirectory()); //Vérif que c'est pas un dossier
			 } 
		 }; 
		File[] files = dir.listFiles(fileFilter);
		 for(int i=0;i<files.length;i++)
			 copyfile(System.getProperty("user.dir")+"/Ressources/Chipset/"+files[i].getName(), System.getProperty("user.dir")+"/"+NomCarte+"/Chipset/"+files[i].getName());
	    dir = new File(System.getProperty("user.dir")+"/Ressources/Sound");//Copie les chipsets et son de base dedans
		files = dir.listFiles(fileFilter);
		for(int i=0;i<files.length;i++)
			 copyfile(System.getProperty("user.dir")+"/Ressources/Sound/"+files[i].getName(), System.getProperty("user.dir")+"/"+NomCarte+"/Sound/"+files[i].getName());
		
		
		dir = new File(System.getProperty("user.dir")+"/prjSO/"+NomCarte+"/Chipset");//Copie les chipsets du proj dans le nouveau
		files = dir.listFiles(fileFilter);
		for(int i=0;i<files.length;i++)
			 copyfile(System.getProperty("user.dir")+"/prjSO/"+NomCarte+"/Chipset/"+files[i].getName(), System.getProperty("user.dir")+"/"+NomCarte+"/Chipset/"+files[i].getName());
		
		dir = new File(System.getProperty("user.dir")+"/prjSO/"+NomCarte+"/Sound");//Copie les sons du proj dans le nouveau
		files = dir.listFiles(fileFilter);
		for(int i=0;i<files.length;i++)
			 copyfile(System.getProperty("user.dir")+"/prjSO/"+NomCarte+"/Sound/"+files[i].getName(), System.getProperty("user.dir")+"/"+NomCarte+"/Sound/"+files[i].getName());
		
		general=new Projet();
		general.setName(NomCarte);//Création du proj
		JTree Tree=monContenu.getTree();//Ajout arbre de cartes
		DefaultMutableTreeNode racine = new DefaultMutableTreeNode(NomCarte,true);
		((DefaultTreeModel)Tree.getModel()).setRoot(racine);
		
		stats.Statmodel.clear();
		for(int i=0;i<general.getStatsBase().size();i++)
			stats.Statmodel.add(i,general.getStatsBase().get(i));
		stats.MetAjourListeCourbeXP();								
		//On met a jour l'écran des classes aussi
		classes.StatsBaseChange();
		//On met a jour l'écran des objets
		objets.StatsBaseChange();
		//On met a jour l'écran des magies
		magies.StatsBaseChange();
		//On met a jour l'écran des monstres
		monstres.StatsBaseChange();
		
		
		
		
		String mapprj=null;
		BufferedInputStream aFile=null;
		int pos=0;//Position
		String x="";String x2="";int y=0;
		int y1=0;int y3=0;int y4=0;char y2=0;//Diverses variables pour lecture et résultats
		
		mapprj = "prjSO"+File.separator+ Projet + File.separator + Projet + ".prj";
		//Chemin vers le prj
		try
		{
			File Prj= new File(mapprj);
			NombreMaps=(int)(Prj.length()/778);
			System.out.println("Chargement de '" + Projet + "' comportant "+NombreMaps+" maps.");
			aFile = new BufferedInputStream(new FileInputStream(mapprj));//Ouverture en écriture
		}catch(FileNotFoundException e){e.printStackTrace();}
		Carte Map=null;
		
				
		try{
			for(int j=0;j<NombreMaps;j++,pos=0,x="",x2="",y=0,y1=0,y3=0,y4=0,y2=0)
			{
				Map=general.new Carte();
				y=aFile.read(); pos++; 
				System.out.println("Longueur nom map: " + y);
				for(int i=0;i<y;i++)//Lis la taille du nom de la map puis le nom de la map 
				{
					y2=(char)aFile.read(); pos++;x=x+y2;
				}
				System.out.println("Nom map: " + x);
				Map.Name=x;
				
				while( pos<155){aFile.skip(1); pos++;}
				y1=aFile.read(); pos++;
				System.out.println("Longueur nom chipset: " + y1);
				for(int i=0;i<y1;i++)//Lis la taille du nom du chipset puis le nom du chipset
				{
					y2=(char)aFile.read(); pos++;x2=x2+y2;
				}
				System.out.println("Nom chipset: "+x2);
				Map.Chipset=x2.replace("\\",File.separator);//Remet Chipset\chipset.png sous Unix
				
				while( pos<206){aFile.skip(1); pos++;}
				y3=aFile.read(); pos++;//Lis et stocke la largeur de la map
				System.out.println("Largeur map : "+y3);aFile.read(); pos++;
				Map.TailleX=(short) y3;
				
				y4=aFile.read(); pos++;//Lis et stocke la hauteur de la map
				System.out.println("Hauteur map : "+y4 +"\n___________________________");
				Map.TailleY=(short) y4;
				while( pos<=777){aFile.skip(1); pos++;}//<=777
				
				Map.evenements=new Projet.Evenements[Map.TailleX][Map.TailleY];//On y crée un nouveau "Evenement" avec la taille
				Map.cases=new Projet.Case[Map.TailleX][Map.TailleY];
				  for (int i=0;i<Map.TailleX;i++)
					  for(int k=0;k<Map.TailleY;k++)
					  {
						  Map.cases[i][k]=general.new Case();
						  Map.evenements[i][k]=null;
					  }
				Map.TypeCarte=0;
				Map.Effect=0;
				Map.Static="";
				Map.Music="";
				Map.DecToResPoint=false;
				Map.Parent="";
				
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(Map.Name);
				racine.add(child);
				if (racine==((DefaultTreeModel)Tree.getModel()).getRoot())
				{
					((DefaultTreeModel)Tree.getModel()).setRoot(null);
					((DefaultTreeModel)Tree.getModel()).setRoot(racine);
				}
				Tree.expandPath(new TreePath(racine.getPath()));
				
				
				
				
				chargMapSO(Map,Projet);
				chargEv0SO(Map,Projet);
				
				general.getCartes().add(Map);
			}
			
		}catch(IOException ex){ex.printStackTrace();}
		
		tabbedPane.setVisible(true);
		mnu_enregistrer.setEnabled(true);//On active les menus
		Principal.save=true;
		mnu_proprietes.setEnabled(true);
		mnu_importer.setEnabled(true);
		mnu_exporter_SO.setEnabled(true);
		new ProprietesProjet(general,null,"Propriétés du projet",true);
	}
	
/**################################################################**/
	public void chargMapSO(Carte Carte0,String Projet)
/**################################################################
* Enregistre le .map passé en paramètre dans le dossier du Projet**/
	{					
		//Lecture map
		String Cart=Carte0.Name;
		String map= "prjSO"+File.separator+Projet+ File.separator +"Maps" + File.separator + Cart+".map";
		System.out.print("Chargement: " +map+"\n");
		
		BufferedReader bFile=null;
		String x3=null;int y5;
		
		
		try
		{
			
			bFile = new BufferedReader(new FileReader(map));//On ouvre en lecture
			for(int i=0;i<Carte0.TailleX;i++)
			{
				for(int j=0;j<Carte0.TailleY;j++)
				{
					x3=bFile.readLine();
					y5=Integer.parseInt(x3);//On lis un nombre qu'on met en X couche basse
					Carte0.cases[i][j].X1=y5;
					x3=bFile.readLine();
					y5=Integer.parseInt(x3);//On lis un nombre qu'on met en X couche haute
					Carte0.cases[i][j].X2=y5;
					x3=bFile.readLine();
					y5=Integer.parseInt(x3);//On lis un nombre qu'on met en Y couche basse
					Carte0.cases[i][j].Y1=y5;
					x3=bFile.readLine();
					y5=Integer.parseInt(x3);//On lis un nombre qu'on met en Y couche haute
					Carte0.cases[i][j].Y2=y5;
				}
			}
			bFile.close();//On ferme la map
		}catch(IOException e){e.printStackTrace();}
		
	}
	
/**################################################################**/
	public void chargEv0SO(Carte Carte0,String Projet)
/**################################################################
* Enregistre le .ev0 passé en paramètre dans le dossier du Projet**/
	{					
		//Lecture ev0
		String map= "prjSO"+File.separator+Projet+ File.separator +"Maps" + File.separator + Carte0.Name+".ev0";
		System.out.print("Chargement: " +map+"\n");
		
		BufferedReader bFile=null;
		String x3=null;
		int X,Y,P,L=0;
		String Type="";
		
		try
		{
			//On ouvre en lecture
			bFile= new BufferedReader(new InputStreamReader(new FileInputStream(map), "ISO-8859-1"));
			while(true)
			{
				x3=bFile.readLine();
				if(x3==null){break;}
				if(!x3.equals(""))
				{
					//Récup si CD ou EV
					Type=x3.substring(0,2);
					x3=x3.substring(2);
					//Récup coord X
					X=Integer.parseInt(x3.substring(0, x3.indexOf(",")));
					x3=x3.substring(x3.indexOf(",")+1);
					//Récup coord Y
					Y=Integer.parseInt(x3.substring(0, x3.indexOf(",")));
					x3=x3.substring(x3.indexOf(",")+1);
					//Récup page
					P=Integer.parseInt(x3.substring(0, x3.indexOf(",")));
					x3=x3.substring(x3.indexOf(",")+1);
					//Récup ligne
					L=Integer.parseInt(x3.substring(0, x3.indexOf("=")));
					x3=x3.substring(x3.indexOf("=")+1);
					
					
					if(Type.equals("CD"))
					{
						if(Carte0.evenements[X][Y]==null) Carte0.evenements[X][Y]=general.new Evenements();
						if(Carte0.evenements[X][Y].evenement.size() ==0) Carte0.evenements[X][Y].evenement.add(general.new Evenement());
						while(Carte0.evenements[X][Y].CondDecl.size()<=P) Carte0.evenements[X][Y].CondDecl.add(new ArrayList<String>());
						Carte0.evenements[X][Y].CondDecl.get(P).add(x3);
					}
					if(Type.equals("EV"))
					{
						if(Carte0.evenements[X][Y]==null) Carte0.evenements[X][Y]=general.new Evenements();
						if(Carte0.evenements[X][Y].evenement.size() ==0) Carte0.evenements[X][Y].evenement.add(general.new Evenement());
						while(Carte0.evenements[X][Y].CommandeEv.size()<=P) Carte0.evenements[X][Y].CommandeEv.add(new ArrayList<String>());
						Carte0.evenements[X][Y].CommandeEv.get(P).add(x3);
					}
				}
			}
			bFile.close();//On ferme le ev0
		}catch(IOException e){System.out.println("PAS DE FICHIER TROUVÉ POUR "+map);}
		
		
	}
	
/**################################################################**/
    public FenetreSimple()
/**################################################################
 * Constructeur de FenetreSimple.Crée la fenêtre et ajoute les menus**/
	  {
		  Toolkit k = Toolkit.getDefaultToolkit();
		  Dimension tailleEcran = k.getScreenSize();
		  int largeurEcran = tailleEcran.width;
		  int hauteurEcran = tailleEcran.height;
		  setTitle("SOForge -- Editeur");
		//  setSize(largeurEcran, hauteurEcran);
		  setSize(800,600);
		  setLocation((largeurEcran/2)-400, (hauteurEcran/2)-300);
		
		  FrmSrc=this;
		  JMenuBar menuBar = new JMenuBar();//Ajout de la barre du haut
		  JMenu menu = new JMenu("Cartes");
		  menu.setMnemonic(KeyEvent.VK_C);//Alt+C
		  
		  //Menu Cartes > Nouveau
		  JMenuItem submenu = new JMenuItem("Nouveau");
		  submenu.setMnemonic(KeyEvent.VK_N);//Alt+N
		  submenu.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						NomCarte = JOptionPane.showInputDialog(null, "Entrez le nom du projet", 
								"", 1);
						if (NomCarte!=null)
						{
							if (NomCarte.compareTo("")!=0)// Si le nom n'est pas vide, on crée
							{
								new File(NomCarte).mkdir();
								new File(NomCarte+"/Chipset").mkdir();//Les dossiers
								new File(NomCarte+"/Sound").mkdir();
							    File dir = new File(System.getProperty("user.dir")+"/Ressources/Chipset");
								 FileFilter fileFilter = new FileFilter() { 
									 public boolean accept(File file) {
										 return (!file.isDirectory()); //Vérif que c'est un dossier
									 } 
								 }; 
								File[] files = dir.listFiles(fileFilter);
								 for(int i=0;i<files.length;i++)
									 copyfile(System.getProperty("user.dir")+"/Ressources/Chipset/"+files[i].getName(), System.getProperty("user.dir")+"/"+NomCarte+"/Chipset/"+files[i].getName());
							    dir = new File(System.getProperty("user.dir")+"/Ressources/Sound");//Copie les chipsets et son de base dedans
								files = dir.listFiles(fileFilter);
								for(int i=0;i<files.length;i++)
									 copyfile(System.getProperty("user.dir")+"/Ressources/Sound/"+files[i].getName(), System.getProperty("user.dir")+"/"+NomCarte+"/Sound/"+files[i].getName());
								general=new Projet();
								general.setName(NomCarte);//Création du proj
								JTree Tree=monContenu.getTree();//Ajout arbre de cartes
								DefaultMutableTreeNode racine = new DefaultMutableTreeNode(NomCarte,true);
								((DefaultTreeModel)Tree.getModel()).setRoot(racine);
								tabbedPane.setVisible(true);
								mnu_enregistrer.setEnabled(true);//On active les menus
								Principal.save=true;
								mnu_proprietes.setEnabled(true);
								mnu_importer.setEnabled(true);
								mnu_exporter_SO.setEnabled(true);
								new ProprietesProjet(general,null,"Propriétés du projet",true);
							}
							else
								JOptionPane.showMessageDialog(null, "Votre projet doit avoir un nom.");
						}
					}
				});
		  menu.add(submenu);
		  
		  
		  //Menu Cartes> Ouvrir Cartes
		  submenu = new JMenuItem("Ouvrir Cartes...");
		  submenu.setMnemonic(KeyEvent.VK_O);//Alt+O
		  submenu.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						 File dir = new File(System.getProperty("user.dir"));
						 FileFilter fileFilter = new FileFilter() { 
							 public boolean accept(File file) {
								 return file.isDirectory(); 
							 } 
						 }; 
						 File[] files = dir.listFiles(fileFilter);//On crée la liste des dossiers
						 ArrayList<String> Projects=new ArrayList<String>();
						 for(int i=0;i<files.length;i++)
						 {
							 File subdir = new File(files[i].getAbsolutePath());
							 File[] prj = subdir.listFiles();
							 for(int j=0;j<prj.length;j++)
							 {
								 String extension;
							     int dotPos = prj[j].getName().lastIndexOf(".");
							     if (dotPos>=0)
							     {
								     extension = prj[j].getName().substring(dotPos);//On garde ceux qui contiennent un .prj
								     if ((extension.compareTo(".prj")==0)&&(prj[j].getName().substring(0,dotPos).compareTo(files[i].getName())==0))
								    	 Projects.add(files[i].getName());
							     }
							 }						 
						 }
						 String[] projstr=new String[Projects.size()];
						 projstr=Projects.toArray(projstr);
						 JListe liste=new JListe(projstr,null,"Choisissez le projet",true);//On crée la liste
						 /*JFileChooser choix = new JFileChooser();				     
					     int retour = choix.showOpenDialog(null);
					     if(retour == JFileChooser.APPROVE_OPTION) 
					     {*/
						 
						 if (liste.ListBox.getSelectedValue()!=null && liste.status==1)//Si un projet est selectionné
						 {
	//				    	NomCarte=choix.getSelectedFile().getName();						
	//				    	NomCarte=NomCarte.substring(0, NomCarte.indexOf("."));
							NomCarte=liste.ListBox.getSelectedValue().toString();//On récupère le nom du projet
							try {
								//lecture de l'objet
								ObjectInputStream in=new ObjectInputStream(new FileInputStream(System.getProperty("user.dir")+"/"+NomCarte+"/"+NomCarte+".prj"));
								try{
									in.read();
									general=(Projet)in.readObject();// Lis le projet
									general.setName(NomCarte);
									JTree Tree=monContenu.getTree();
									DefaultMutableTreeNode racine = new DefaultMutableTreeNode(NomCarte,true);
									((DefaultTreeModel)Tree.getModel()).setRoot(racine);//Met le nom du projet comme carte racine de l'arbre
									ArrayList<Carte> cartes=general.getCartes();//récupère les cartes
									Iterator<Carte> itr = cartes.iterator();
									ArrayList<Carte> LostList=new ArrayList<Carte>();
	
									while(itr.hasNext())//Parcourt les cartes
									{
										Carte c=itr.next();
										if (c.Parent.compareTo("")==0)//Si la carte n'a pas de parent
										{
											DefaultMutableTreeNode child = new DefaultMutableTreeNode(c.Name);
											racine.add(child);//On ajoute a la racine
										}
										else
										{
											DefaultMutableTreeNode parent=searchNode(c.Parent);
											if (parent!=null)//Sinon on cherche le parent et
											{
												DefaultMutableTreeNode child = new DefaultMutableTreeNode(c.Name);
												parent.add(child);//on l'ajoute sous la carte parent
											}
											else
												LostList.add(c);//Sinon, ajout à la liste perdue
										}
									}
									boolean ok=true;
									while (ok==true)
									{
										ok=false;
										int i=0; int compte=LostList.size();
										while(i<compte)
										{
											DefaultMutableTreeNode parent=searchNode(LostList.get(i).Parent);
											if (parent!=null)//On essaye de récupérer les parents perdus
											{
												DefaultMutableTreeNode child = new DefaultMutableTreeNode(LostList.get(i).Name);
												parent.add(child);
												ok=true;
												LostList.remove(i);
												compte--;
											}
											else
											    i++;
										}
									}
									// en désespoir de cause on ajoute tout a la racine
									for(int i=0;i<LostList.size();i++)
									{
										DefaultMutableTreeNode child = new DefaultMutableTreeNode(LostList.get(i).Name);
										racine.add(child);									
									}
									Tree.expandPath(new TreePath((DefaultMutableTreeNode)(Tree.getModel()).getRoot()));
									
									//On rajoute maintenant les stats de base
									stats.Statmodel.clear();
									for(int i=0;i<general.getStatsBase().size();i++)
										stats.Statmodel.add(i,general.getStatsBase().get(i));
									stats.MetAjourListeCourbeXP();								
									//On met a jour l'écran des classes aussi
									classes.StatsBaseChange();
									//On met a jour l'écran des objets
									objets.StatsBaseChange();
									//On met a jour l'écran des magies
									magies.StatsBaseChange();
									//On met a jour l'écran des monstres
									monstres.StatsBaseChange();
									
									tabbedPane.setVisible(true);
									mnu_enregistrer.setEnabled(true);
									Principal.save=true;
									mnu_proprietes.setEnabled(true);//On active les menus
									mnu_importer.setEnabled(true);
									mnu_exporter_SO.setEnabled(true);
								} 
								finally{
									//fermer le flux
									in.close();
								}
							} 
							catch (ClassNotFoundException el) {
								el.printStackTrace();
							}
							catch (IOException e1) {
								e1.printStackTrace();
							}
					     }
					}					
				});
		  menu.add(submenu);
		  
		  //Menu Cartes > Enregistrer
		  submenu = new JMenuItem("Enregistrer");
		  submenu.setMnemonic(KeyEvent.VK_E);//Alt+E
		  submenu.setEnabled(false);
		  submenu.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						Sauvegarder();
					}
				});
		  mnu_enregistrer=submenu;
		  menu.add(submenu);
		  menu.addSeparator();
	
		  //Menu Cartes > Propriétés
		  submenu = new JMenuItem("Propriétés");
		  submenu.setMnemonic(KeyEvent.VK_P);//Alt+P
		  submenu.setEnabled(false);
		  submenu.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						new ProprietesProjet(general,null,"Propriétés du projet",true);
					}
				});
		  mnu_proprietes=submenu;
		  menu.add(submenu);
		  //Menu Cartes > Importer
		  submenu = new JMenuItem("Importer...");
		  submenu.setMnemonic(KeyEvent.VK_I);//Alt+I
		  submenu.setEnabled(false);
		  submenu.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						new Importation(FrmSrc,null,"Importation",false);
					}
				});
		  mnu_importer=submenu;
		  menu.add(submenu);
		  menu.addSeparator();
		//Menu Cartes > Importer SO
		  submenu = new JMenuItem("Importer SO");
		  submenu.setMnemonic(KeyEvent.VK_M);//Alt+M
		  submenu.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						chargPrjSO();
					}
				});
		  mnu_importer_SO=submenu;
		  menu.add(submenu);
		  //Menu Cartes > Exporter
		  submenu = new JMenuItem("Exporter SO");
		  submenu.setMnemonic(KeyEvent.VK_P);//Alt+P
		  submenu.setEnabled(false);
		  submenu.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						ExportSO();
					}
				});
		  mnu_exporter_SO=submenu;
		  menu.add(submenu);
		  menu.addSeparator();
		  //Menu Cartes > Quitter
		  submenu = new JMenuItem("Quitter");
		  submenu.setMnemonic(KeyEvent.VK_Q);//Alt+Q
		  submenu.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					 dispose();
					 System.exit(0);
				}
			});
	  menu.add(submenu);
	  menuBar.add(menu);
	  //Menu Edition
	  menu = new JMenu("Edition");
	  menu.setMnemonic(KeyEvent.VK_E);
	  //Menu Edition > Copier
	  submenu = new JMenuItem("Copier Ctrl+C");
	  submenu.setMnemonic(KeyEvent.VK_C);
	  submenu.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						MenuCopier();
					}
				});
	  menu.add(submenu);
	  //Menu Edition > Coller
	  submenu = new JMenuItem("Coller Ctrl+V");
	  submenu.setMnemonic(KeyEvent.VK_O);
	  submenu.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						MenuColler();
					}
				});
	  menu.add(submenu);
	  menuBar.add(menu);
	  //Menu Version
	  submenu = new JMenuItem("Version");
	  submenu.setMnemonic(KeyEvent.VK_V);
	  submenu.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						JOptionPane.showMessageDialog(null, "SOForge V."+version);					
					}
				});
	  menuBar.add(submenu);
	  setJMenuBar(menuBar);//On place la MenuBar
	  tabbedPane = new JTabbedPane();  
	  tabbedPane.setVisible(false);
	  monContenu = new Contenu(this);//On place les onglets
	  tabbedPane.addTab("Général",monContenu);
	  monBlocage = new Blocage(this);
	  tabbedPane.addTab("Chipset", monBlocage);
	  monZoneMonstre = new ZoneMonstre(this);
	  tabbedPane.addTab("Zone de monstres", monZoneMonstre);
	  stats = new OngletStats(this);
	  tabbedPane.addTab("Stats", stats);
	  classes = new OngletClasses(this);
	  tabbedPane.addTab("Classes", classes);
	  objets = new OngletObjets(this);
	  tabbedPane.addTab("Objets", objets);
	  magies = new OngletMagies(this);
	  tabbedPane.addTab("Magies", magies);
	  monstres = new OngletMonstres(this);
	  tabbedPane.addTab("Monstres", monstres);
	  Container leContenant = getContentPane();
	  leContenant.add(tabbedPane);  //Et on ajoute tout au Container.
	}
    
    
  @SuppressWarnings("unchecked")
/**################################################################**/
  public DefaultMutableTreeNode searchNode(String nodeStr)
/**################################################################
 * Sert a renvoyer une carte dans l'arbre des maps (bas-gauche)
 * Renvoie: La map dans l'arbre**/
  {
        DefaultMutableTreeNode node = null;
        
        //Get the enumeration
		JTree Tree=monContenu.getTree();
		node=(DefaultMutableTreeNode)(Tree.getModel()).getRoot();
        Enumeration en = node.breadthFirstEnumeration();
        
        //iterate through the enumeration
        while(en.hasMoreElements())
        {
            //get the node
            node = (DefaultMutableTreeNode)en.nextElement();
            
            if (node!=(DefaultMutableTreeNode)(Tree.getModel()).getRoot())
	            //match the string with the user-object of the node
	            if(nodeStr.equals(node.getUserObject().toString()))
	            {
	                //tree node with string found
	                return node;                         
	            }
        }
        
        //tree node with string node found return null
        return null;
    } 
}

//Classe lancée par le programme
public class Principal 
{
	public static boolean save=false;
	
/**################################################################**/
	public static void main(String[] args)
/**################################################################
 * Main. Crée une instance de l'éditeur**/
	{
		try
		{//Utilise l'apparence systeme de la fenêtre
			if(System.getProperty("os.name").contains("Linux")) UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			else UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//On met le lookandfeel system
		}
		catch(Exception e2)
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//On met le lookandfeel system
			}catch(Exception e3){System.out.println("Error setting system LookAndFeel");}
		}
		
			    
	    FenetreSimple fenetre = new FenetreSimple(); //On crée une fenêtre
	    fenetre.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
	    
	    fenetre.addWindowListener(new WindowListener() 
	    {/**Propose de sauvegarder si on ferme la fenêtre autrement qu'avec "Quitter" pour éviter les erreurs**/ @Override public void windowOpened(WindowEvent e) {}@Override public void windowIconified(WindowEvent e) {}@Override public void windowDeiconified(WindowEvent e) {}@Override public void windowDeactivated(WindowEvent e) {}@Override public void windowClosed(WindowEvent arg0){} @Override public void windowActivated(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) 
			{//TODO @BETA Ne proposer de sauvegarder que si on a modifié quelque chose
				int val=0;
		    	if (save && (val=JOptionPane.showConfirmDialog(null,
		                "Sauvegarder avant de quitter ?",
		                "Sauvegarder",
		                JOptionPane.YES_NO_CANCEL_OPTION,
		                JOptionPane.WARNING_MESSAGE))==JOptionPane.YES_OPTION)//Propose de sauvegarder
		    	{
		    		((FenetreSimple) arg0.getWindow()).Sauvegarder();
		    		arg0.getWindow().dispose();
			    	System.exit(0);
		    	}
		    	else if(!save || (save && val==JOptionPane.NO_OPTION))
		    	{
		    		arg0.getWindow().dispose();
			    	System.exit(0);
		    	}
		    	//Ne ferme pas si on a choisi "Annuler"
			}
		});
	    Image icon = Toolkit.getDefaultToolkit().getImage("Ressources"+File.separator+"Chipset"+File.separator+"icon.png");
	    fenetre.setIconImage(icon);//On définit l'icone de la fenêtre
	    
	    fenetre.setVisible(true);
	    
	    
	    
	    /**Java7 test**/
		/*switch("ll")
		{
			case "a": System.out.println("4");break;
			case "ll": System.out.println("5");break;
			default: System.out.println("45");break;
		}
		//
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {}
		//
		int binary = 0b1000; //2^3 = 8
		//
		double bigNumber=74_740_785_100_100_999D;
		//
		try 
		{
			throw new FileNotFoundException("FileNotFoundException");
		} catch (FileNotFoundException | NullPointerException e2) 
		{
			e2.printStackTrace();
		}
	    //
	    List<String> lines;
		try 
		{
			lines = Files.readAllLines(FileSystems.getDefault().getPath("src/Principal.java"), StandardCharsets.UTF_8);
			for (String line : lines) System.out.println(line);
		} catch (IOException e1) 
		{
			e1.printStackTrace();
		}  
	    */ 
	    		
	}
}
