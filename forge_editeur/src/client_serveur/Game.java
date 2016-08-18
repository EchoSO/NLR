package client_serveur;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.sound.midi.Sequence;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.LayerUI;

import struct.Projet;
import struct.Projet.Carte;
import struct.Projet.Depart;
import struct.Projet.Evenement;
import struct.Projet.Magie;
import struct.Projet.Monstre;
import struct.Projet.Objet;
import struct.Projet.Zone;
import struct.Sauvegarde;

import client_serveur.KeyboardInput.KeyState;
//TODO @BETA Rajouter les menus supplémentaires, sauvegarde etc automatiquement
//TODO @ALPHA2 Supprimer objet supprime tout le stack
//TODO @ALPHA2 Les monstres peuvent marcher partout quand mvmt aléatoire
//TODO @BETA Raccourcis objets
//TODO @ALPHA2 fin du message = lastIndexOf(")") plutot que 2 carac avant la fin de ligne (pour commentaires)
//TODO @ALPHA2 points distribués disparaissent à la déco parfois
//TODO @BETA Monstres perdent aggro quand on meurt
//TODO @ALPHA2 Parfois le nombre de monstres dépasse la limite, et mauvais type de monstre remplace les autres
//TODO Monstres "attaquer toujours" restent immobiles
//TODO @ALPHA2 Bug au niveau de addMagie (la magie ne s'ajoute pas)
//TODO @ALPHA2 (a vérif) Bug dans AddObject pour donner beaucoup d'exemplaire de l'objet genre donné 30 potions d'un coup)
//TODO @BETA2 le jeu est légèrement décalé en haut à gauche, surtout en 320*240
//TODO @BETA Possibilité d'avoir plusieurs stacks d'un objet
//TODO @BETA Choix graphismes Fast/Smooth pour joueurs, monstres etc
//TODO @BETA2 Logs etc
//TODO @BETA2 : Fixer objectifs suivants
//TODO @ALPHA : Lire et commenter Client
//TODO @ALPHA : Réseau Client (Etapes à fixer)
//TODO @ALPHA : Créer Serveur (Etapes à fixer)
//TODO @ALPHA création de compte, afficher pseudo en bas
//TODO @BETA Cryptage protocole et mise en oeuvre pratique client/launcher/serveur, DL maps
//TODO @BETA Jeu par navigateur
//TODO @ALPHA Séparer Game.java en plusieurs classes bien ordonnées
//TODO @BETA Sécurisation
//TODO @ALPHA2 Or non affiché dans les stats
//TODO @ALPHA2 Effet chaleur:bide (nullpointerexception)
//TODO @BETA new inventaire, carrés blancs, animation horizontale
//TODO @BETA Gang/Conquête de territoires

//import net.java.games.input.Controller;
//import net.java.games.input.ControllerEnvironment;
public class Game extends JFrame implements KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final int MDetect = 20;
	protected static final int MNDetect = -20;
	protected static final int MaxTexteDelay = 300;

	Projet general;
	Game Self;
	private SpriteEngine SpriteE;
	struct.Sauvegarde PlayerInfo;
	private struct.Sauvegarde PSave; 
	private Player player;
	private TMChar PlayerMChar;
	Events Evenements[][];
	private ArrayList<Zone> zones;
	Image Background[][];
	public FiltresVisuels Filt;
	public Formules Formule;
	int CentreX = 20;
	int CentreY = 14;
	int Detect = 26;
	int DetectS = 28;
	int NDetect = -26;
	int NDetectS = -28;
	
	ArrayList<Integer> CurrentSpawn,CurrentMonstre;
	ArrayList<MonstreGame> ListeMonstre;
	ArrayList<MagieGame>ListeMagie;
	MonstreGame lastmonsteratt,LastSelectedCharM;
	int SVersX,SVersY,SScrollX,SScrollY,ScX,ScY,ScrollDirection;
	int ScreenX,ScreenY,prealx , prealy ,MagieUsed,MagicObject, oldx, oldy,brouillardx, brouillardy,Effect,PEffect,DecoX,DecoY;
	int MagicDommage,MagicAttaque,MagicDefense,MagicEsquive;
	long Timer,Timer2,Timer3,WaitTimer,WaitTimer2,WaitTimer3,LastMenu;
	boolean Dead,Freeze,Bloque,HasMusic,HasSound,HasFullScreen;
	boolean IsMenuActive,IsInEvent,ImportantThing,IsInSelectMChar,IsInvEquipActive;
	boolean IsInInputString,HasChangedMap,IsMagasinActive;
    boolean IsInvActive,IsStatActive,IsInConfirm,IsConfirmStatus;
	boolean HasToSendFinEv,AutoEvent,PossibleBloqueMonstre;
	String LastMusic,CondDeclenche,InputString,ResultQueryEv;
	
	int AttenteEvenement,BoucleEvent,AttenteDead;
	int EventAttX,EventAttY,EventEv,CurrentLigneEv;
	ArrayList<String> ListeEv;
    private boolean Quit;
//	private Controller joy;
	private KeyboardInput keyboard;
	private KeyboardInput.KeyState[] keys;
	private int KS,KD,KLEFT,KRIGHT,KUP,KDOWN,KRETURN,KSPACE,KESCAPE;
	private int K0,K1,K2,K3,K4,K5,K6,K7,K8,K9;
	private long time_passed,next_time;
	private long TICK_INTERVAL = 1000/30;//30FPS
	Carte CurrentMap;
	private boolean[][][] block;
	public double Zoom,Zoomopt;
	int PlayerDirection;
	ArrayList<String> EvCommande , EvCondition, EvVarCondition, EvEventCondition,MenuPossibles;
	Image Tile,SystemSurf,CaverneSurf,BrouillardSurf,PluieSurf,NeigeSurf,StaticSurf;
	Timer timerCreateMonster,timerGereMonster,timerGereMagie;
	Sprite SpriteEv;
	calcul Calcule;
	MusicThread MusicTh;
	SoundThread SoundTh;
	MidiPlayer midiplayer;
	Sequence midiseq;
	javazoom.jl.player.Player mp3player;
	FioleVie fiolevie;
	FioleMana fiolemana;
	BarreXP barrexp;
	BarreIcone barreicone;
	String DEFAULT_SOUND = "Sound\\Cursor1.wav";
	String DEFAULT_LVLUP_SOUND ="Sound\\Holy2.wav";//Valeur défaut des sons
	String SOUND_VALIDE = "Sound\\Item1.wav";
	JFrame FrmSrc;
	Point startDrag;
	Image imgWM;
	PositionJ posJ;
	ReceptionJ recJ;
	static String tmpR="";
	
	public static HashMap<String, PosXY> ListeJMap=new HashMap<>();
	public static HashMap<String,PosXY> psdUsed=new HashMap<>();
	public static HashMap<String,PosXY> psdUsedOld=new HashMap<>();
	public static HashMap<String,OtherPlayer> ListePlayer = new HashMap<>();

/**################################################################**/
	public Game(String key, String pseudo, String mdp, boolean full,String ip,int res) 
/**################################################################
 * Lance le jeu**/	
	{
		connexThread con=new connexThread(key, pseudo, mdp, full, ip, res);
		Thread conTh=new Thread(con);
		conTh.start();
		addKeyListener(this);
	}
	
/**################################################################**/
	class connexThread implements Runnable
/**################################################################
 * Se connecte au serveur etc**/
	{
		public connexThread(String key, String pseudo, String mdp, boolean full,String ip,int res)
		{
			Global.key=key.substring(5);
			Global.pseudo=pseudo;
			Global.mdp=mdp;//Enregistre les variables
			Global.full=full;
			Global.ip=ip;
			Global.res=res;
		}

		@Override
		public void run() 
		{
			Global.connect();//Envoie la connexion
			Global.envoi("\\connect|"+Global.pseudo+"|"+Global.mdp+"|"+Global.key);
			Global.rec0();
			tmpR=Global.recu.substring(0, 15);
			switch(tmpR)
			{//Traite la réponse
			case "statmsg:Connect": //Si connexion OK
				//Global.envoi("\\prj");
				Object tmpO= Global.recOb();
				play((Projet) tmpO, 2d, 0, 0);
				break;
			case "statmsg:No play":
				Global.message("An anormal error occured. You have been logged. [~]Please use Launcher");
				System.exit(1);
				break;
			case "statmsg:Bad key":
				Global.message("An anormal error occured. You have been logged. [~]Please use Launcher");
				System.exit(1);
				break;
			case "statmsg:No Laun":
				Global.message("An anormal error occured. You have been logged. [~]Please use Launcher");
				System.exit(1);
				break;
			case "statmsg:Bad pas":
				Global.message("An anormal error occured. You have been logged. [~]Please use Launcher");
				System.exit(1);
				break;
			default:
				Global.message("A fatal error occured. Sorry.");
				System.exit(1);
				break;
			}
			
		}
	}
	
	
	
	public void play(Projet gen,double _Zoom,int _FullScreen,int _Musique) 
	{
		class RenderingThread extends Thread 
		{	
		     public void run()
		     {
		 		  gameLoop();
		 		  //TODO Sauvegarde sur le serveur
	        	  if (mp3player!=null)
	        		  mp3player.close();
	        	  if (midiplayer!=null)
	        		  midiplayer.close();
	        	  if (HasSound)
	        		  AudioPlayer.shutdown();
	        	  timerCreateMonster.stop();
	        	  timerCreateMonster=null;
	        	  timerGereMonster.stop();
	        	  timerGereMonster=null;
	        	  timerGereMagie.stop();
	        	  timerGereMagie=null;
		     }
		}
		int _Sound;
		
		general=gen;
		_Sound=_Musique;
		LoadParams();
		
		Self=this;
		HasFullScreen=(_FullScreen>0);
		HasMusic=(_Musique>0);
		HasSound=(_Sound>0);
		Zoom=_Zoom;
		Zoomopt=_Zoom;
		Calcule=new calcul();
		LastMusic="";
        SScrollX=-1;
        SScrollY=-1;
        ScX=0;
        ScY=0;
        SVersX=-1;
        SVersY=-1;
		prealx=0;
		prealy=0;
	    ScreenX=0;
	    ScreenY=0;
		Dead=false;
		Freeze=false;
		Bloque=false;
		IsMenuActive=false;
		IsInEvent=false;
		ImportantThing=false;
		IsInSelectMChar=false;
		IsInvEquipActive=false;
		IsInInputString=false;
		IsMagasinActive=false;
		InputString="";
		LastSelectedCharM=null;
		HasChangedMap=false;
		Timer=0; Timer2=0; Timer3=0;
		WaitTimer=0; WaitTimer2=0; WaitTimer3=0;
		BoucleEvent=0; ResultQueryEv="";
		zones=new ArrayList<Zone>();
		CurrentSpawn=new ArrayList<Integer>();
		CurrentMonstre=new ArrayList<Integer>();		
		EvCommande=new ArrayList<String>();
		EvCondition=new ArrayList<String>();
		EvVarCondition=new ArrayList<String>();
		EvEventCondition=new ArrayList<String>();
		MenuPossibles=new ArrayList<String>();
		EvCondition.add("Appuie sur bouton"); EvCondition.add("Attaque"); EvCondition.add("En contact"); EvCondition.add("Automatique"); EvCondition.add("Auto une seul fois");
		EvVarCondition.add("%Name%"); EvVarCondition.add("%UpperName%"); EvVarCondition.add("%Classe%"); EvVarCondition.add("%Force%");
		EvVarCondition.add("%Dext%"); EvVarCondition.add("%Constit%"); EvVarCondition.add("%Magie%"); EvVarCondition.add("%Attaque%");
		EvVarCondition.add("%Defense%"); EvVarCondition.add("%Vie%");EvVarCondition.add("%VieMax%"); EvVarCondition.add("%CurrentMag%"); EvVarCondition.add("%MagMax%");
		EvVarCondition.add("%Alignement%"); EvVarCondition.add("%Reputation%"); EvVarCondition.add("%Gold%");
		EvVarCondition.add("%Lvl%"); EvVarCondition.add("%LvlPoint%"); EvVarCondition.add("%CurrentXP%");
		EvVarCondition.add("%NextXP%"); EvVarCondition.add("%Inventaire%"); EvVarCondition.add("%Timer%"); EvVarCondition.add("%Visible%"); EvVarCondition.add("%Bloque%");
		EvVarCondition.add("%CaseX%"); EvVarCondition.add("%CaseY%"); EvVarCondition.add("%EvCaseX%"); EvVarCondition.add("%EvCaseY%");
		EvVarCondition.add("%Direction%"); EvVarCondition.add("%CentreX%"); EvVarCondition.add("%CentreY%"); EvVarCondition.add("%BloqueChangeSkin%"); 
		EvVarCondition.add("%BloqueAttaque%"); EvVarCondition.add("%BloqueMagie%"); EvVarCondition.add("%BloqueDialogue%"); EvVarCondition.add("%Effect%");
		EvVarCondition.add("%BloqueDefense%"); EvVarCondition.add("%Position%");
		EvVarCondition.add("%NbObjetInventaire%"); EvVarCondition.add("%Arme%"); EvVarCondition.add("%Bouclier%");
		EvVarCondition.add("%Casque%"); EvVarCondition.add("%Armure%"); EvVarCondition.add("%Timer2%"); EvVarCondition.add("%Timer3%");
		EvEventCondition.add(".Name%"); EvEventCondition.add(".CaseX%"); EvEventCondition.add(".CaseY%"); EvEventCondition.add(".Chipset%");
		EvEventCondition.add(".Bloquant%"); EvEventCondition.add(".Transparent%"); EvEventCondition.add(".Visible%");
		EvEventCondition.add(".TypeAnim%"); EvEventCondition.add(".Direction%"); EvEventCondition.add(".X%"); EvEventCondition.add(".Y%"); EvEventCondition.add(".W%");
		EvEventCondition.add(".H%"); EvEventCondition.add(".NumAnim%"); EvEventCondition.add(".Vitesse%");
		EvCommande.add("Message("); EvCommande.add("Conditio"); EvCommande.add("AddObjec"); EvCommande.add("DelObjec"); EvCommande.add("Teleport");
		EvCommande.add("ChangeRe"); EvCommande.add("ChangeSk");  EvCommande.add("InputQue"); EvCommande.add("OnResult");
		EvCommande.add("QueryEnd"); EvCommande.add("Magasin(");  EvCommande.add("Coffre("); EvCommande.add("PlayMusi");
		EvCommande.add("StopMusi"); EvCommande.add("PlaySoun");
		EvCommande.add("ChAttaqu"); EvCommande.add("ChBlesse"); EvCommande.add("AddMagie"); EvCommande.add("DelMagie"); EvCommande.add("GenereMo");
		EvCommande.add("TueMonst"); EvCommande.add("SScroll("); EvCommande.add("Attente(");
		EvCommande.add("Sauvegar"); EvCommande.add("Chargeme"); EvCommande.add("Quitter("); EvCommande.add("Options(");
		EvCommande.add("ChangeCl"); EvCommande.add("ShowInte"); EvCommande.add("HideInte");
		EvCommande.add("AddMenu("); EvCommande.add("DelMenu(");
	    MenuPossibles.add("Inventaire"); MenuPossibles.add("Magie"); MenuPossibles.add("Statistique");
	    MenuPossibles.add("Charger"); MenuPossibles.add("Sauver"); 
	    MenuPossibles.add("Options"); MenuPossibles.add("Quitter");
	    this.setBackground(new Color(0));
	    if (Zoom==0)
	    {
	    	Toolkit k = Toolkit.getDefaultToolkit();
	    	Dimension tailleEcran = k.getScreenSize();
	    	int largeurEcran = tailleEcran.width;
	    	int hauteurEcran = tailleEcran.height;
	    	this.setSize(largeurEcran,hauteurEcran);
	    }
	    else
	    	this.setSize((int)(320*Zoom),(int)(240*Zoom));
		//this.setResizable(false);
		SetScreen(_FullScreen,true);
		setTitle(general.getName());
		keyboard = new KeyboardInput(); // Keyboard polling
		addKeyListener( keyboard );

		LoadSurface();
		
		PlayerInfo=(Sauvegarde) Global.recOb();
		
		//InitialisePlayerInfo(gen);
		SpriteE=new SpriteEngine(null);
		ReadMap();
//		player=new Player(this,Zoom);
//		SpriteE.AddSprite(player);
		Quit=false;
//		joy=null;
		addWindowListener(new java.awt.event.WindowAdapter() 
		{
		    public void windowClosing(WindowEvent winEvt) 
		    {
		    	Quit=true;
		    }
		});
		
		timerCreateMonster=TimerCreateMonster();
		timerCreateMonster.start();
		timerGereMonster=TimerGereMonster();
		timerGereMonster.start();
		timerGereMagie=TimerGereMagie();
		timerGereMagie.start();
		
		Filt=new FiltresVisuels();
		Formule=new Formules(Calcule, PlayerInfo, general, this);
		imgWM = LoadImage("Ressources"+File.separator+"Chipset"+File.separator+"txt"+Zoom+".jpg",false);
		
	    GereEvenement(true);
		RenderingThread renderingThread = new RenderingThread();
		renderingThread.start();
		Global.lock=false;
		posJ = new PositionJ();
		recJ = new ReceptionJ();
		Global.connected=true;
		Thread RecJ= new Thread(recJ);
		RecJ.start();
		Thread PosJ= new Thread(posJ);
		PosJ.start();

//		gameLoop();
	}
	
	
	
	public static void main(String[] args) 
	{/**Lance le client**/
/*		ControllerEnvironment ce =
			ControllerEnvironment.getDefaultEnvironment();

		// retrieve the available controllers
		Controller[] controllers = ce.getControllers();*/
		//new Game(null,1,0,0);
		
	}
	
/**################################################################**/
	public class MusicThread extends Thread
/**################################################################
 * Thread qui joue la musique du client**/
	{
		  public void run() 
		  {
	          try {
	        		  mp3player.play();//Joue la musique
	        		  if ((Quit==false)&& !LastMusic.equals(""))
	        			  PlayMusic(LastMusic,true);//Si on ne quitte pas, lance la musique d'après
	          }catch (Exception e) {e.printStackTrace();}
		  }	
	}

/**################################################################**/
	public class SoundThread extends Thread
/**################################################################
 * Thread qui joue les sons du client**/
	{
		  private String SoundFic;
		  public void setSoundFic(String fic) { SoundFic=fic; }
		  public void run() 
		  {
	          try 
	          {
			  	  javazoom.jl.player.Player mp3p;	        	  	
		          mp3p=new javazoom.jl.player.Player(new FileInputStream(general.getName()+"/"+SoundFic.replace("Sound\\", "Sound/")));
				  mp3p.play();//Joue le son SoundFic
	          }catch (Exception e) {e.printStackTrace();}
		  }	
	}
	


	
/**################################################################**/
	class Events 
/**################################################################
 * Classe type des events.**/
	{
		public ArrayList<Evenement> evenement;
    	public ArrayList<ArrayList<String>> CondDecl,CommandeEv;
		int Ev;
		long WaitingTimer,WaitingTimer2,WaitingTimer3;
		boolean Done[];
		boolean HasToCheckPage;
		int moveevenement;
		Sprite Sprite;
	}
	
/**################################################################**/
	class MonstreGame
/**################################################################
 * Crée les monstres IG. + variables**/
	{
	    public int attentemove,totalattente;
	    public int Direction, MagicDommage,MagicAttaque,MagicDefense,MagicEsquive;
	    public short tempsincantation,TypeMonstre;
	    boolean Bloque;
	    public TMChar sprite;
	    public String Name;
	    Monstre monstre;
	    public int mapx,mapy,zone,vie;
	    public int XPMin , XPMax , GoldMin, GoldMax;
		
		public MonstreGame(Monstre monstre_,int zone)
		{/**Constructeur. Crée un monstre**/
			monstre=monstre_;
			Direction=2;
			vie=monstre.Vie;
			TypeMonstre=monstre.TypeMonstre;
			 Name=monstre.Name+"\\"+Util.random(999999);
			 XPMin=monstre.XPMin;
			 XPMax=monstre.XPMax;
			 GoldMin=monstre.GoldMin;
			 GoldMax=monstre.GoldMax;
		}
	}

/**################################################################**/
	  class MagieGame 
/**################################################################
 * Classe des magies**/
	  {
	    public boolean Wizard,Cible;
	    public int attente, attentetotal;
	    public MonstreGame MWizard,Monstre;
	    public Magie magie;
	    public int TypeMagie;
	    public int duree,taillezone,oldduree;
	    public int X,Y;
	    public String FormuleTouche, FormuleEffet;
	    public ArrayList<MonstreGame>MAffected;
	    public boolean FirstTime;
	    public short OnMonster;
	    
	    public MagieGame(Magie magie_)
	    {/**Constructeur appelé quand on lance un sort**/
	    	magie=magie_;
	    	MAffected=new ArrayList<MonstreGame>();
	    }
	  }


/**################################################################**/
	  private Image LoadImage(String fichier,boolean Effets)
/**################################################################
 * Charge l'image "fichier" avec un effet si nécessaire**/
	  {
		FilteredImageSource filteredSrc;
		Image image;
		int leffet;
		ImageLoader im=new ImageLoader(null);
		image=im.loadImage(System.getProperty("user.dir")+"/"+fichier);
		if (Effets) leffet=PEffect;
		else leffet=0;//S'il y a un effet:
		switch(leffet)
		{//Applique l'effet appropré
			case 1 :
		    	filteredSrc = new FilteredImageSource(image.getSource(), Filt.Grotte);
				return Toolkit.getDefaultToolkit().createImage(filteredSrc);
			case 2 :
		    	filteredSrc = new FilteredImageSource(image.getSource(), Filt.Nuit);
				return Toolkit.getDefaultToolkit().createImage(filteredSrc);
			case 3 :
		    	filteredSrc = new FilteredImageSource(image.getSource(), Filt.Chaleur);
				return Toolkit.getDefaultToolkit().createImage(filteredSrc);
		}//Renvoie l'image modifiée si nécessaire
		return image;
	}

/**################################################################**/
	private void PopulateMonsterMap()
/**################################################################
 * Crée les monstres sur la map (currentmonstre)**/
	{
		boolean ok=false;
		synchronized(ListeMonstre)//TODO @ALPHA Utiliser des blocs synchronisés si nécessaire
		{
			while(ok==false)
			{
				CreateMonster(false,-1);//Crée un monstre
				ok=true;
		    	for (int i=0;i<zones.size();i++)//TODO @BETA bug génération des monstres ici ?(mauvais type)
		    	{//Parcoure les zones et voit les monstres à créer
					if (CurrentMonstre.get(i) < zones.get(i).MonstreMax)//Continue s'il reste des monstres à créer
						ok=false;
		    	}			
			}
		}
	}
	
/**################################################################**/
	private boolean HasMagie(MonstreGame cible,Magie magie)
/**################################################################
 * Vérifie si la magie est déjà en cours d'execution**/
	{
		int i=0;
		boolean trouve=false;
	    while ((i<ListeMagie.size()) && !trouve)//Parcoure la liste des magies
	    {
	    	//Si la magie vise le joueur et pas de cible OU si magie vise monstre et cible défini (Valide)
	    	if (( ListeMagie.get(i).Cible && cible==null || ListeMagie.get(i).Monstre==cible && cible!=null)
	    			&& (ListeMagie.get(i).magie==magie))//TODO @ALPHA2 Fonctionnel (==) ?
	    		trouve=true;//ET que la magie est dans la liste, renvoie true
	    	i++;
	    }
		return trouve;
	}

/**################################################################**/
	private void CreateMagie(MonstreGame MWizard,MonstreGame cible,Magie magie)
/**################################################################
 * MWizard lance magie sur cible**/
	{
		boolean trouve;
		trouve=PlayerInfo.BloqueMagie;//Si %BloqueMagie%=1, trouve=true
		MagieGame NewMagieEnCours;
		if (trouve==false);//True si cible monstre ou cible autour du joueur et carte pas d'attaque => rien
			trouve=(magie.OnMonster==0 || magie.OnMonster==2) && CurrentMap.TypeCarte==0;
        if (trouve==false)
        {
          if (magie.MagieType==6 && MWizard==null)
          {//Si attaque sournoise et MWizard null(Magie lancée par le joueur):
            if (PlayerInfo.Lvl >= magie.LvlMin)
              if (PlayerInfo.CurrentMag >= magie.MPNeeded)//Si il a lelvl et assez de magie, lance l'attaque
                GereAttaqueClient();
            if (lastmonsteratt!=null)
            	cible=lastmonsteratt;//Réattaque le dernier monstre attaqué s'il existe
            else
            	return;//S'il n'existe pas, rien
          }
          trouve=HasMagie(cible,magie);//S'il y a une magie sur cette cible,ne fait plus rien
        }
        if (trouve==false)
        {
            // on fait les differents calculs
            if (PlayerInfo.Lvl >= magie.LvlMin && PlayerInfo.CurrentMag >= magie.MPNeeded)
            {//Si le joueur a le lvl et les MP nécessaires, crée une magie
            	NewMagieEnCours=new MagieGame(magie);
            	if (MWizard==null)
            		NewMagieEnCours.Wizard=true;//Wizard = true si le joueur est le Wizard
                NewMagieEnCours.MWizard=MWizard;
                if (cible==null)
                	NewMagieEnCours.Cible=true;//Cible = true si le joueur est la cible                    
                NewMagieEnCours.Monstre=cible;
                NewMagieEnCours.TypeMagie=magie.MagieType;
                NewMagieEnCours.duree=1;
                NewMagieEnCours.taillezone=0;
                if (magie.FormuleZone.compareTo("")!=0)
                	NewMagieEnCours.taillezone=(int) Calcule.Calcule(Formule.ReplaceStringMagie(magie.FormuleZone,MWizard,cible));
                if (magie.FormuleDuree.compareTo("")!=0)
                	NewMagieEnCours.duree=(int) Calcule.Calcule(Formule.ReplaceStringMagie(magie.FormuleDuree,MWizard,cible));
                NewMagieEnCours.FormuleTouche=magie.FormuleTouche;
                NewMagieEnCours.FormuleEffet=magie.FormuleEffet;
                NewMagieEnCours.attente=0;
               	NewMagieEnCours.attentetotal=(magie.TempsIncantation / 60)-1;
                NewMagieEnCours.FirstTime=true;
                NewMagieEnCours.OnMonster=magie.OnMonster;
                if (cible==null)
                {
                  NewMagieEnCours.X=PlayerInfo.pmapx;
                  NewMagieEnCours.Y=PlayerInfo.pmapy;
                }
                else
                {
                  NewMagieEnCours.X=cible.mapx;
                  NewMagieEnCours.Y=cible.mapy;
                }
                synchronized(ListeMagie) { ListeMagie.add(NewMagieEnCours); }
            }
        }
	}
	
	private void CreateMonster(boolean fromTimer,int fromZone)
	{
    	int i ,j,compte,max;
    	String Temp;
    	MonstreGame NewMonster;
    	boolean access;
    	if (fromZone>=0)
    	{
    		i=fromZone;
    		max=fromZone+1;
    	}
    	else
    	{
    		i=0;
    		max=zones.size();
    	}
    	while(i<max)
    	{
    		access=true;
            if ((zones.get(i).X1>=0)&&(zones.get(i).Variable!=null))
    		if (zones.get(i).Variable.compareTo("")!=0)
    		{
    			Temp=PlayerInfo.Variable.getProperty(zones.get(i).Variable, "0");
    			access=(Temp.compareTo(zones.get(i).Resultat)==0) ? true : false;
    		}
    		if (zones.get(i).ZoneTypeMonstre==-1)
    			access=false;
    		if (access)
    		{
    			if (fromTimer==false)
    				CurrentSpawn.set(i,zones.get(i).VitesseSpawn);
    			if (CurrentSpawn.get(i) < zones.get(i).VitesseSpawn)
    				CurrentSpawn.set(i,CurrentSpawn.get(i)+1);
    			else
    			{
    				CurrentSpawn.set(i,0);
    				if (CurrentMonstre.get(i) < zones.get(i).MonstreMax)
    				{
    	                if (zones.get(i).X1<0)
    	                {
	    					NewMonster=new MonstreGame(general.getMonstreByIndex(0-(zones.get(i).X1+1)), i);
	    					if (zones.get(i).VitesseSpawn==-1)
	    						NewMonster.zone=-1;
	    					if (zones.get(i).X2==0)
	    					{
	    	                    NewMonster.XPMin=0;
	    	                    NewMonster.XPMax=0;
	    	                    NewMonster.GoldMin=0;
	    	                    NewMonster.GoldMax=0;
	    					}
    	                }
    	                else
	    					NewMonster=new MonstreGame(general.getMonstreByIndex(zones.get(i).ZoneTypeMonstre-1), i);
    					compte=0;
    					do
    					{	        						
        	                if (zones.get(i).X1<0)
        	                {
        	                	NewMonster.mapx=(int) Calcule.Calcule(Formule.ReplaceStringVariable(zones.get(i).Variable));
        	                	NewMonster.mapy=(int) Calcule.Calcule(Formule.ReplaceStringVariable(zones.get(i).Resultat));
        	                }
        	                else
        	                {
	    	                    NewMonster.mapx=Util.random(zones.get(i).X2-zones.get(i).X1)+zones.get(i).X1;
	    	                    NewMonster.mapy=Util.random(zones.get(i).Y2-zones.get(i).Y1)+zones.get(i).Y1;
        	                }
    	                    access=true;
        	                try
        	                {
        	    			    access=((NewMonster.mapx>=0) && (NewMonster.mapx<CurrentMap.TailleX) && (NewMonster.mapy>=0) && (NewMonster.mapy<CurrentMap.TailleY));
        	    			    if (access==true)
        	    			    {
	        	                	if ((CurrentMap.cases[NewMonster.mapx][NewMonster.mapy].X1 > 0) && (access==true))
	        	    			        access=block[0][CurrentMap.cases[NewMonster.mapx][NewMonster.mapy].X1-1][CurrentMap.cases[NewMonster.mapx][NewMonster.mapy].Y1-1];
	        	    			    if ((CurrentMap.cases[NewMonster.mapx][NewMonster.mapy].X2 > 0) && (access==true))
	        	    			        access=block[0][CurrentMap.cases[NewMonster.mapx][NewMonster.mapy].X2-1][CurrentMap.cases[NewMonster.mapx][NewMonster.mapy].Y2-1];
        	    			    }
        	    			    if (access==true)
        	    			    {
        	    			    	j=0;
        	    		        	while ((j<zones.size()) && (access==true))
        	    		        	{
        	    		        		if (zones.get(i).ZoneTypeMonstre==-1)
        	    		        		{
		        	                        access=!( (NewMonster.mapx>=zones.get(j).X1) && (NewMonster.mapx<=zones.get(j).X2)
		        	                                && (NewMonster.mapy>=zones.get(j).Y1) && (NewMonster.mapy<=zones.get(j).Y2));
        	    		        		}
        	    		        		j++;
        	    		        	}
        	    			    }
        	                } catch (Exception e) { System.out.println("Plantage Block!!"); access=false; e.printStackTrace();}
        	                NewMonster.mapx=NewMonster.mapx*2;
        	                NewMonster.mapy=NewMonster.mapy*2;
        	                compte++;
    					}
    					while ((access==false) && (compte<200));
    	                if (compte>=200)
    	                {//TODO @BETA Pas de boucle infinie si création monstre impo
    	                  System.out.println("!!=> Création monstre impossible : "+i+"/"+max);
    	                }
    	                else
    	                {
    	                	if (VerifieSpriteVisible(NewMonster.mapx,NewMonster.mapy))
    	                	{
    	                		NewMonster.sprite=new TMChar(Self,NewMonster.Name,NewMonster.monstre.Chipset,NewMonster.monstre.SoundAttaque,NewMonster.monstre.SoundWound,NewMonster.monstre.SoundConcentration,0,0,NewMonster.monstre.W,NewMonster.monstre.H,NewMonster.mapx,NewMonster.mapy,0,0,NewMonster.Direction,0,0,Sprite.idSprite.idMonstre,0,1,NewMonster.monstre.Bloquant);
    	                		SpriteE.AddSprite(NewMonster.sprite);
    	                	}
    	                	CurrentMonstre.set(i,CurrentMonstre.get(i)+1);
    	                	ListeMonstre.add(NewMonster);
    	                }
    				}
    			}
    		}
    		i++;
    	}		
	}
	
	
	private Timer TimerCreateMonster()
	{
		ActionListener action = new ActionListener ()
	    {
	        public void actionPerformed (ActionEvent event)
	        {
	        	synchronized(ListeMonstre)
	        	{
	        		if (((IsMenuActive==true)||(IsInEvent==true))&&(general.isMenuFreeze()==true))
	        			return;
	        		CreateMonster(true,-1);
	        	}
	        }
	    };
	      
	    return new Timer (2000, action);
	}  

	private boolean IsMonsterOnCase(int CaseX,int CaseY)
	{
		for(int i=0;i<ListeMonstre.size();i++)
		{
			if ((ListeMonstre.get(i).mapx==CaseX *2) && (ListeMonstre.get(i).mapy==CaseY *2))
				return true;
		}
		return false;
	}
	
	private Timer TimerGereMagie()
	{
		ActionListener action = new ActionListener ()
	    {
			private boolean[] ActionSort(MagieGame magie)
			{
				boolean MonsterDead,Touched;
				int  j,resultat,degat;
				boolean  trouve,touche;
				String formule;
				String  temp,variable;
				// on verifie d'abord si on touche
				MonsterDead=false; Touched=true;
				if (magie.FormuleTouche.compareTo("")!=0)
				{
					resultat=(int) Calcule.Calcule(Formule.ReplaceStringMagie(magie.FormuleTouche, magie.MWizard, magie.Monstre));
					touche=(resultat > 0);
				}
				else
				  touche=true;
				if (touche==true)
				{
				    if (magie.Monstre!=null)
				    	magie.Monstre.TypeMonstre=1;
				    formule =magie.FormuleEffet;
				    while(formule.indexOf(";")>=0)
				    {
				      temp="";
				      if (formule.indexOf(":")>=0)
				    	  temp=formule.substring(0,formule.indexOf(":")+1);
				      if (temp.trim().compareTo("spell:")==0)	
				      {
				        temp=formule.substring(formule.indexOf(":")+1);
				        temp=temp.substring(0,temp.indexOf(";")).trim();
				        touche=false;
				        j=0;
				        while((j<general.getMagies().size()) && (touche==false))
				        {
				          if (general.getMagieByIndex(j).Name.compareTo(temp)==0) touche=true;
				          j++;
				        }
				        if (touche==true)
				        {
				          j--;
				          trouve=HasMagie(magie.Monstre, general.getMagieByIndex(j));
				          if (trouve==false)
				          {
				        	  CreateMagie(magie.MWizard,magie.Monstre,general.getMagieByIndex(j));
				          }
				        }
				      }
				      else
				      {
				    	  temp=formule.substring(0,formule.indexOf(";"));
				    	  if (temp.indexOf("=")>0)
				    	  {
				    		  variable=temp.substring(0,temp.indexOf("=")).trim();
				    		  temp=temp.substring(temp.indexOf("=")+1).trim();
				    		  resultat=(int) Calcule.Calcule(Formule.ReplaceStringMagie(temp, magie.MWizard, magie.Monstre));
				    		  if (variable.startsWith("Variable["))
				    		  {
				    			  if (magie.Wizard==true)
				    			  {
				    			      AffectationVarPlayer(variable , Integer.toString(resultat));
				    			  }
				    		  }
				    		  else
				    		  if (variable.compareTo("%Wizard.Vie%")==0)
				    		  {
				    			  if (magie.Wizard==true)
				    			  {
				    				  if (PlayerInfo.Vie <= resultat)
				    				  {
			    				          degat=resultat-PlayerInfo.Vie;
				    					  SpriteE.AddSprite(new Degat(FrmSrc, Integer.toString(degat), player, Color.GREEN));
						                  PlayerInfo.Vie=resultat;
						                  if (PlayerInfo.Vie > PlayerInfo.VieMax)
						                    PlayerInfo.Vie=PlayerInfo.VieMax;
						                  if (fiolevie!=null)
						                	  fiolevie.Redraw();
				    				  }
				    				  else
				    				  {
				    					  if (resultat<=0)
				    					  {
        	            		    		  if (IsInEvent)
        	            		    			  PlayerInfo=PSave;
        	            		    		  if (player!=null)
        	            		    			  player.Action=0;
        	            		    		  Dead=true;
        	            		    		  PlayerInfo.Vie=0;
        	            		    		  KillPlayer();
        	            		    		  PlayerInfo.CurrentXP=(int) Math.round(PlayerInfo.CurrentXP
        	                                                            -(PlayerInfo.CurrentXP*0.01));
        	            		    		  if (PlayerInfo.CurrentXP<PlayerInfo.PrevXP)
        	            		    			  PlayerInfo.CurrentXP=PlayerInfo.PrevXP;
        	            		    		  PlayerInfo.Gold=(int) Math.round(PlayerInfo.Gold
        	                                                          -(PlayerInfo.Gold*0.01));
        	            		    		  PlayerInfo.Vie=0;
    						                  if (fiolevie!=null)
    						                	  fiolevie.Redraw();
				    					  }
				    					  else
				    					  {
			    							  if (PlayerInfo.SoundWound.compareTo("")!=0)
			    								  PlaySound(PlayerInfo.SoundWound,"",false);
				    						  degat=PlayerInfo.Vie-resultat;
				    				          SpriteE.AddSprite(new Degat(FrmSrc, Integer.toString(resultat), player, Color.WHITE));
				    						  PlayerInfo.Vie=resultat;
							                  if (fiolevie!=null)
							                	  fiolevie.Redraw();
				    					  }
				    				  }
				    			  }
				    			  else
				    			  {
				    				  if (magie.MWizard.vie <= resultat)
				    				  {
				    					  degat=resultat-magie.MWizard.vie;
			    				          SpriteE.AddSprite(new Degat(FrmSrc, Integer.toString(degat), magie.MWizard.sprite, Color.GREEN));
							              magie.MWizard.vie=resultat;
							              if (magie.MWizard.vie>magie.MWizard.monstre.Vie)
							                magie.MWizard.vie=magie.MWizard.monstre.Vie;
				    				  }
				    				  else
				    				  {
		    							  if (magie.MWizard.monstre.SoundWound.compareTo("")!=0)
		    								  PlaySound(magie.MWizard.monstre.SoundWound,"",false);
							              if (resultat<=0)
							              {
								              	// Monstre se tue lui meme
								                magie.MWizard.vie=0;
								        	    GereMonsterKill(magie.MWizard);
								                MonsterDead=true;
							              }
							              else
							              {
							            	    degat=magie.MWizard.vie-resultat;
								                magie.MWizard.vie=resultat;
				    				            SpriteE.AddSprite(new Degat(FrmSrc, Integer.toString(degat), magie.MWizard.sprite, Color.WHITE));
							              }
				    				  }
				    			  }
				    		  }
				    		  else
				    		  if (variable.compareTo("%Wizard.CurrentMag%")==0)
				    		  {
				    			  if (magie.Wizard==true)
				    			  {
				    				  if (PlayerInfo.CurrentMag <= resultat)
				    				  {
			    				          degat=resultat-PlayerInfo.CurrentMag;
				    					  SpriteE.AddSprite(new Degat(FrmSrc, Integer.toString(degat), player, Color.BLUE));
				    				  }
				    				  else
				    				  {
			    				          degat=PlayerInfo.CurrentMag-resultat;
			    				          degat=-degat;
				    					  SpriteE.AddSprite(new Degat(FrmSrc, Integer.toString(degat), player, Color.BLUE));
				    				  }
				    				  PlayerInfo.CurrentMag=resultat;
				    				  if (PlayerInfo.CurrentMag<0)
				    					  PlayerInfo.CurrentMag=0;
				    				  if (PlayerInfo.CurrentMag > PlayerInfo.MagMax)
				    					  PlayerInfo.CurrentMag=PlayerInfo.MagMax;
				    				  if (fiolemana!=null)
				    					  fiolemana.Redraw();
				    			  }
				    		  }
				    		  else
				    		  if (variable.compareTo("%Wizard.Gold%")==0)
				    		  {
				    			  if (magie.Wizard==true)
				    			  {
				    				  PlayerInfo.Gold=resultat;
				    				  SpriteE.AddSprite(new Degat(FrmSrc, "+"+Integer.toString(resultat)+"PO", player, Color.WHITE));
				    			  }
				    		  }
				    		  else
				    		  if (variable.compareTo("%[Wizard].CurrentXP%")==0)
				    		  {
				    			  if (magie.Wizard==true)
				    			  {
				    				  PlayerInfo.CurrentXP=resultat;
				    				  if (PlayerInfo.CurrentXP >=PlayerInfo.NextXP)
				    				  {
				    					  if ((PlayerInfo.Classe.LvlMax==-1) || ((PlayerInfo.Lvl<PlayerInfo.Classe.LvlMax) && (PlayerInfo.Lvl<general.getCourbeXP().size()+2)))
				    					  {
				    						  PlayerInfo.Lvl++;
				    						  PlayerInfo.PrevXP=general.getCourbeXP().get(PlayerInfo.Lvl);
				    						  PlayerInfo.NextXP=general.getCourbeXP().get(PlayerInfo.Lvl-1);
				    						  PlayerInfo.LvlPoint+=PlayerInfo.Classe.LvlupPoint;
						    				  SpriteE.AddSprite(new Degat(FrmSrc, "LEVEL UP!", player, Color.RED));
				    					  }				    					  
				    				  }
				    			  }
				    		  }
				        // 	les cibles maintenant
			    			  if (magie.Cible==true)
			    			  {
			    				  if (variable.compareTo("%Cible.Dommage%")==0)
			    				  {
			    					  if (MagicDommage==0)
			    						  MagicDommage=resultat;
			    					  else
			    						  Touched=false;
			    				  }
			    				  else
			    				  if (variable.compareTo("%Cible.Attaque%")==0)
			    			      { 
			    					  if (MagicAttaque==0)
			    						  MagicAttaque=resultat;
			    					  else
			    						  Touched=false;
			    			      }
			    				  else
			    				  if (variable.compareTo("%Cible.Esquive%")==0)
			    			      { 
			    					  if (MagicEsquive==0)
			    						  MagicEsquive=resultat;
			    					  else
			    						  Touched=false;
			    			      }
			    				  else
			    				  if (variable.compareTo("%Cible.Defense%")==0)
			    				  {
			    					  if (MagicDefense==0)
			    					  	 MagicDefense=resultat;
			    					  else
			    						  Touched=false;
			    				  }
			    				  else
			    				  if (variable.compareTo("%Cible.Bloque%")==0)
			    				  {
			    					  if (Freeze==false)
			    						  Freeze=(resultat > 0);
			    					  else
			    						  Touched=false;
			    				  }
			    				  else
			    				  if (variable.compareTo("%Cible.Vie%")==0)
			    				  {
			    					  if (PlayerInfo.Vie <= resultat)
			    					  {
			    						  degat=resultat-PlayerInfo.Vie;
			    				          SpriteE.AddSprite(new Degat(FrmSrc, Integer.toString(degat), player, Color.GREEN));
			    						  PlayerInfo.Vie=resultat;
				    					  if (PlayerInfo.Vie > PlayerInfo.VieMax)
				    						  PlayerInfo.Vie=PlayerInfo.VieMax;
			    					  }				            
			    					  else
			    					  if (resultat<=0)
			    					  {
		    							  if (PlayerInfo.SoundWound.compareTo("")!=0)
		    								  PlaySound(PlayerInfo.SoundWound,"",false);
    	            		    		  if (IsInEvent)
    	            		    			  PlayerInfo=PSave;
    	            		    		  if (player!=null)
    	            		    			  player.Action=0;
    	            		    		  Dead=true;
    	            		    		  PlayerInfo.Vie=0;
    	            		    		  KillPlayer();
    	            		    		  PlayerInfo.CurrentXP=(int) Math.round(PlayerInfo.CurrentXP
    	                                                            -(PlayerInfo.CurrentXP*0.01));
    	            		    		  if (PlayerInfo.CurrentXP<PlayerInfo.PrevXP)
    	            		    			  PlayerInfo.CurrentXP=PlayerInfo.PrevXP;
    	            		    		  PlayerInfo.Gold=(int) Math.round(PlayerInfo.Gold
    	                                                          -(PlayerInfo.Gold*0.01));
    	            		    		  PlayerInfo.Vie=0;
			    					  }
			    					  else
			    					  {
			    						  degat=PlayerInfo.Vie-resultat;
			    				          SpriteE.AddSprite(new Degat(FrmSrc, Integer.toString(degat), player, Color.WHITE));
			    						  PlayerInfo.Vie=resultat;
			    					  }
			    					  if (fiolevie!=null)
			    						  fiolevie.Redraw();
			    				  }
			    			  }
			    			  else
			    			  {
			    				  if (magie.Monstre!=null)
			    				  {
			    					  if (variable.compareTo("%Cible.Dommage%")==0)
			    					  {
			    						  if (magie.Monstre.MagicDommage==0)
			    							  magie.Monstre.MagicDommage=resultat;
			    						  else
			    							  Touched=false;
			    					  }
			    					  else
			    					  if (variable.compareTo("%Cible.Attaque%")==0)
			    				      {
			    						  if (magie.Monstre.MagicAttaque==0)
			    							  magie.Monstre.MagicAttaque=resultat;
			    						  else
			    							  Touched=false;
			    				      }
			    					  else
			    					  if (variable.compareTo("%Cible.Esquive%")==0)
			    				      {
			    						  if (magie.Monstre.MagicEsquive==0)
			    							  magie.Monstre.MagicEsquive=resultat;
			    						  else
			    							  Touched=false;
			    				      }
			    					  else
			    					  if (variable.compareTo("%Cible.Defense%")==0)
			    					  {
			    						  if (magie.Monstre.MagicDefense==0)
			    							  magie.Monstre.MagicDefense=resultat;
			    						  else
			    							  Touched=false;
			    					  }
			    					  else
			    					  if (variable.compareTo("%Cible.Bloque%")==0)
			    					  {
			    						  if (magie.Monstre.Bloque==false)
			    							  magie.Monstre.Bloque=(resultat > 0);
			    						  else
			    							  Touched=false;
			    					  }
			    					  else
			    					  if (variable.compareTo("%Cible.Vie%")==0)
			    					  {
			    						  if (resultat >= magie.Monstre.vie)
			    						  {
				    				          degat=resultat-magie.Monstre.vie; 
			    							  SpriteE.AddSprite(new Degat(FrmSrc, Integer.toString(degat), magie.Monstre.sprite, Color.GREEN));
			    							  magie.Monstre.vie=resultat;
			    							  if (magie.Monstre.vie > magie.Monstre.monstre.Vie)
			    								  magie.Monstre.vie=magie.Monstre.monstre.Vie;
			    						  }
			    						  else
			    						  {
				    				          degat=magie.Monstre.vie-resultat; 
			    							  magie.Monstre.vie=resultat;
			    							  magie.Monstre.TypeMonstre=1;
			    							  magie.Monstre.totalattente=1;
			    							  if (magie.Monstre.monstre.SoundWound.compareTo("")!=0)
			    								  PlaySound(magie.Monstre.monstre.SoundWound,"",false);
			    							  if (magie.Monstre.vie<=0)
			    							  {
			    							  		MonsterDead=true;
			    							  		GereMonsterKill(magie.Monstre);
			    							  }
			    							  else
			    							  {
					    				          SpriteE.AddSprite(new Degat(FrmSrc, Integer.toString(degat), magie.Monstre.sprite, Color.WHITE));
			    							  }
			    						  }
			    					  }
			    				  }
			    			  }
				    	  }
				      }
				      if (formule.contains(";"))
				    	  formule=formule.substring(formule.indexOf(";")+1);
				      else
				    	  formule="";
				    }
				}
				else
				{
				    if (magie.Cible==true)
				    {
				    	SpriteE.AddSprite(new Degat(FrmSrc, "Miss", player, Color.WHITE));
				    }
				    else
			        if (magie.Monstre!=null)
			        {
				        magie.Monstre.TypeMonstre=1;
				    	SpriteE.AddSprite(new Degat(FrmSrc, "Miss", magie.Monstre.sprite, Color.WHITE));
			        }
				}
				boolean[] bresult=new boolean[2];
				bresult[0]=MonsterDead;
				bresult[1]=Touched;
				return bresult;
			}
			
			public void actionPerformed (ActionEvent event)
	        {
	        	int i,compte2;
	        	int mapx,mapy,resultat;
	        	boolean touche=false,MagieDead;
	        	boolean MonsterDead,Touched;
        		if (((IsMenuActive==true)||(IsInEvent==true))&&(general.isMenuFreeze()==true))
        			return;
	        	synchronized(ListeMagie) {
		        	i=0; compte2=ListeMagie.size();
	                while (i<compte2)
	                {
	                	if ((ListeMagie.get(i).MWizard!=null) || ((ListeMagie.get(i).Wizard==true)&&(Dead==false)))
	                	{
	                		if (ListeMagie.get(i).attente >= ListeMagie.get(i).attentetotal)
	                		{
	                			ListeMagie.get(i).duree--;
	                			mapx=ListeMagie.get(i).X;
	                			mapy=ListeMagie.get(i).Y;
	                			if (ListeMagie.get(i).duree>=0)
	                			{
	                				if (ListeMagie.get(i).magie.SoundMagie.compareTo("")!=0)
	                					PlaySound(ListeMagie.get(i).magie.SoundMagie, "", false);
	                				switch(ListeMagie.get(i).TypeMagie)
	                				{
	                					case 0 :
	                					case 5 :
	                					case 6 :
	                						if (ListeMagie.get(i).Cible==true)
	                						{
	                							if (Dead==true)
	                								ListeMagie.get(i).duree=-1;
	                						}
	                						if (ListeMagie.get(i).Monstre!=null)
	                						{
	                							if (ListeMagie.get(i).Monstre.vie<=0)
	                								ListeMagie.get(i).duree=-1;
	                						}
	                						if (ListeMagie.get(i).duree>=0)
	                						{
	                							touche=false;
	                							if (ListeMagie.get(i).Wizard==true)
	                							{
	                								touche=true;
	                							}
	                						}
	                						if (ListeMagie.get(i).MWizard!=null)
	                						{
		      	                                  if ((ListeMagie.get(i).FirstTime==true) && (ListeMagie.get(i).TypeMagie==5))
		      	                                	ListeMagie.get(i).duree++;
		      	                                  if ((ListeMagie.get(i).FirstTime==false) && (ListeMagie.get(i).TypeMagie==5))
		      	                                	  touche=true;
		      	                                  if (ListeMagie.get(i).TypeMagie==0)
		      	                                	  touche=true;
		      	                                ListeMagie.get(i).FirstTime=false;                							
	                						}
	    	                                if (touche)
	    	                                {
	    	                                  if (ListeMagie.get(i).Cible==true)
	    	                                  {
	    	                                	  mapx=PlayerInfo.pmapx;
	    	                                	  mapy=PlayerInfo.pmapy;
	    	                                  }
	    	                                  else
	    	                                  {
	    	                                    if (ListeMagie.get(i).Monstre!=null)
	    	                                    {
	    	                                      mapx=ListeMagie.get(i).Monstre.mapx;
	    	                                      mapy=ListeMagie.get(i).Monstre.mapy;
	    	                                    }
	    	                                  }
	    	                                  if (ListeMagie.get(i).FormuleTouche.compareTo("oldfireball")==0)
	    	                                  {
	    	                                  	 ListeMagie.get(i).FormuleTouche="";
	    	                                     touche=false;
	    	                                  }
	    	                                  if (touche==true)
	    	                                  {
	    	                                	  if (ListeMagie.get(i).TypeMagie==5)
	    	                                	  {
	    	                                		  if ((ListeMagie.get(i).Monstre!=null) && (ListeMagie.get(i).Wizard==true))
	    	                                		  {
		    	                                		  if (player!=null)    	                                			  
		    	                                			  player.Kill();
		    	                                          player=null;
		    	                                          int dir=0;
		    	                                          if (PlayerInfo.pmapy!=ListeMagie.get(i).Monstre.mapy)
		    	                                          {
		    	                                        	  if (PlayerInfo.pmapy>ListeMagie.get(i).Monstre.mapy)
		    	                                        		  dir=0;
		    	                                        	  else
		    	                                        		  dir=2;
		    	                                          }
		    	                                          else
		    	                                          {
		    	                                        	  if (PlayerInfo.pmapx>ListeMagie.get(i).Monstre.mapx)
		    	                                        		  dir=3;
		    	                                        	  else
		    	                                        		  dir=1;
		    	                                          }
		    	                                          TMChar NewMChar=new TMChar(FrmSrc,PlayerInfo.Name,PlayerInfo.Chipset,PlayerInfo.SoundAttaque,PlayerInfo.SoundWound,PlayerInfo.SoundConcentration,0,0,24,32,PlayerInfo.pmapx,PlayerInfo.pmapy,0,0,dir,(Dead==true) ? 1 : 0,0,Sprite.idSprite.idMChar,2,3,false);
		    	                                          NewMChar.mrealx=prealx; NewMChar.mrealy=prealy;
		    	                                          NewMChar.versx=ListeMagie.get(i).Monstre.mapx;
		    	                                          NewMChar.versy=ListeMagie.get(i).Monstre.mapy;
		    	                                          NewMChar.Transparency=ListeMagie.get(i).magie.Tran / 255;
		    	                                          NewMChar.Action=1;
		    	                                          SpriteE.AddSprite(NewMChar);
		    	                                          if (ListeMagie.get(i).magie.Chipset.compareTo("")!=0)
		    	                                        	  SpriteE.AddSprite(new Animation(FrmSrc, NewMChar, ListeMagie.get(i).magie.Chipset,PlayerInfo.pmapx,PlayerInfo.pmapy,ListeMagie.get(i).magie.X, ListeMagie.get(i).magie.Y
				  	    			                        			, ListeMagie.get(i).magie.W, ListeMagie.get(i).magie.H, ListeMagie.get(i).magie.Z, ListeMagie.get(i).magie.DureeAnim, ListeMagie.get(i).magie.Tran));
	    	                                		  }
	    	                                		  else
	    	                                		  if (ListeMagie.get(i).MWizard!=null)
	    	                                		  {
		    	                                          if (ListeMagie.get(i).MWizard.sprite!=null)
		    	                                          {
			    	                                          int dir;
		    	                                        	  TMChar NewMChar=new TMChar(FrmSrc,ListeMagie.get(i).MWizard.Name,ListeMagie.get(i).MWizard.monstre.Chipset,ListeMagie.get(i).MWizard.monstre.SoundAttaque,ListeMagie.get(i).MWizard.monstre.SoundWound,
			    	                                        		  ListeMagie.get(i).MWizard.monstre.SoundConcentration,0,0,ListeMagie.get(i).MWizard.monstre.W,ListeMagie.get(i).MWizard.monstre.H,ListeMagie.get(i).MWizard.mapx,ListeMagie.get(i).MWizard.mapy,0,0,ListeMagie.get(i).MWizard.sprite.Direction,0,0,Sprite.idSprite.idMChar,1,3,false);
			    	                                          NewMChar.mrealx=ListeMagie.get(i).MWizard.sprite.mrealx; NewMChar.mrealy=ListeMagie.get(i).MWizard.sprite.mrealy;
		    	                                        	  if (ListeMagie.get(i).Cible==true)
		    	                                        	  {
		    	                                        		  NewMChar.versx=PlayerInfo.pmapx;
		    	                                        		  NewMChar.versy=PlayerInfo.pmapy;
		    	                                        		  ListeMagie.get(i).MWizard.mapx=PlayerInfo.pmapx;
		    	                                        		  ListeMagie.get(i).MWizard.mapy=PlayerInfo.pmapy;
				    	                                          if (NewMChar.y!=PlayerInfo.pmapy)
				    	                                          {
				    	                                        	  if (NewMChar.y>PlayerInfo.pmapy)
				    	                                        		  dir=0;
				    	                                        	  else
				    	                                        		  dir=2;
				    	                                          }
				    	                                          else
				    	                                          {
				    	                                        	  if (NewMChar.x>PlayerInfo.pmapy)
				    	                                        		  dir=3;
				    	                                        	  else
				    	                                        		  dir=1;
				    	                                          }
				    	                                          NewMChar.Direction=dir;
		    	                                        	  }
		    	                                        	  else
		    	                                        	  if (ListeMagie.get(i).Monstre!=null)
		    	                                        	  {
		    	    	                                          NewMChar.versx=ListeMagie.get(i).Monstre.mapx;
		    	    	                                          NewMChar.versy=ListeMagie.get(i).Monstre.mapy;
		    	                                        		  ListeMagie.get(i).MWizard.mapx=ListeMagie.get(i).Monstre.mapx;
		    	                                        		  ListeMagie.get(i).MWizard.mapy=ListeMagie.get(i).Monstre.mapy;
				    	                                          if (NewMChar.y!=ListeMagie.get(i).Monstre.mapy)
				    	                                          {
				    	                                        	  if (NewMChar.y>ListeMagie.get(i).Monstre.mapy)
				    	                                        		  dir=0;
				    	                                        	  else
				    	                                        		  dir=2;
				    	                                          }
				    	                                          else
				    	                                          {
				    	                                        	  if (NewMChar.x>ListeMagie.get(i).Monstre.mapx)
				    	                                        		  dir=3;
				    	                                        	  else
				    	                                        		  dir=1;
				    	                                          }
				    	                                          NewMChar.Direction=dir;
		    	                                        	  }
			    	                                          NewMChar.Transparency=ListeMagie.get(i).magie.Tran / 255;
		    	                                        	  NewMChar.Action=1;
			    	                                          ListeMagie.get(i).MWizard.sprite.Kill();
			    	                                          ListeMagie.get(i).MWizard.sprite=NewMChar;
			    	                                          SpriteE.AddSprite(NewMChar);
			    	                                          if (ListeMagie.get(i).magie.Chipset.compareTo("")!=0)
			    	                                        	  SpriteE.AddSprite(new Animation(FrmSrc, NewMChar, ListeMagie.get(i).magie.Chipset,NewMChar.x,NewMChar.y,ListeMagie.get(i).magie.X, ListeMagie.get(i).magie.Y
				  	    			                        			, ListeMagie.get(i).magie.W, ListeMagie.get(i).magie.H, ListeMagie.get(i).magie.Z, ListeMagie.get(i).magie.DureeAnim, ListeMagie.get(i).magie.Tran));
		    	                                          }
	    	                                		  }
	    	                                	  }
	    	                                	  else
	    	                                	  {
	    	                                		  if (ListeMagie.get(i).magie.Chipset.compareTo("")!=0)
	    	                                		  {
	    	                                			  Sprite sprite=null;
	    	                                			  if (ListeMagie.get(i).Cible==true)
	    	                                				  sprite=player;
	    	                                			  if (ListeMagie.get(i).Monstre!=null)
	    	                                				  sprite=ListeMagie.get(i).Monstre.sprite;
	    	                                			  if (sprite!=null)
	    		  	    			                        	SpriteE.AddSprite(new Animation(FrmSrc, sprite, ListeMagie.get(i).magie.Chipset,sprite.x,sprite.y,ListeMagie.get(i).magie.X, ListeMagie.get(i).magie.Y
	    		  	    			                        			, ListeMagie.get(i).magie.W, ListeMagie.get(i).magie.H, ListeMagie.get(i).magie.Z, ListeMagie.get(i).magie.DureeAnim, ListeMagie.get(i).magie.Tran));
	    	                                		  }
	    	                                          if ((ListeMagie.get(i).TypeMagie==6)&&(ListeMagie.get(i).MWizard!=null))
	    	                                          {
	    	                                        	  TMChar NewMChar=ListeMagie.get(i).MWizard.sprite;
	    	                                        	  if (NewMChar!=null)
	    	                                        	  {
		    	                                              NewMChar.Action=4;
		    	                                              NewMChar.AttenteTotal=ListeMagie.get(i).magie.TempsIncantation;
		    	                                              NewMChar.AttenteAttaque=0;
	    	                                        	  }
	    	                                          }
	    	                                	  }
	    	                                  }
		                                	  boolean[] res=ActionSort(ListeMagie.get(i));
		                                	  MonsterDead=res[0]; Touched=res[1];
		                                	  if (MonsterDead)
		                                    	ListeMagie.get(i).duree=0;
	    	                                }
	    	                                break;
	                					case 1 :
		      	                              if (ListeMagie.get(i).FirstTime)
		      	                              {
		      	                            	ListeMagie.get(i).oldduree=ListeMagie.get(i).duree;
		      	                            	ListeMagie.get(i).duree=0;
		    	                                if (ListeMagie.get(i).Cible==true)
		    	                                {
		    	                                  mapx=PlayerInfo.pmapx;
		    	                                  mapy=PlayerInfo.pmapy;
		    	                                }
		    	                                else
		    	                                {
		    	                                  if (ListeMagie.get(i).Monstre!=null)
		    	                                  {
		    	                                    mapx=ListeMagie.get(i).Monstre.mapx;
		    	                                    mapy=ListeMagie.get(i).Monstre.mapy;
		    	                                  }
		    	                                  else
		    	                                    mapx=-1;
		    	                                }
		    	                                if (mapx!=-1)
		    	                                {
		    	                                	ListeMagie.get(i).FirstTime=false;
		    	                                	if (ListeMagie.get(i).magie.Chipset.compareTo("")!=0)
		    	                                	{
		    	                                		  TMChar NewMChar=null;
		    	                                		  if (ListeMagie.get(i).Wizard==true)
		    	                                		  {
			    	                                          NewMChar=new TMChar(FrmSrc,"",ListeMagie.get(i).magie.Chipset,"","","",ListeMagie.get(i).magie.X,ListeMagie.get(i).magie.Y,ListeMagie.get(i).magie.W,ListeMagie.get(i).magie.H,PlayerInfo.pmapx,PlayerInfo.pmapy,0,0,PlayerDirection,0,ListeMagie.get(i).magie.Z,Sprite.idSprite.idAnimation,1,3,false);
			    	                                          NewMChar.mrealx=prealx; NewMChar.mrealy=prealy;
		    	                                		  }
		    	                                		  else
		    	                                		  if (ListeMagie.get(i).MWizard!=null)
		    	                                		  {
			    	                                          NewMChar=new TMChar(FrmSrc,"",ListeMagie.get(i).magie.Chipset,"","","",ListeMagie.get(i).magie.X,ListeMagie.get(i).magie.Y,ListeMagie.get(i).magie.W,ListeMagie.get(i).magie.H,ListeMagie.get(i).MWizard.mapx,ListeMagie.get(i).MWizard.mapy,0,0,PlayerDirection,0,ListeMagie.get(i).magie.Z,Sprite.idSprite.idAnimation,1,3,false);
		    	                                		  }
		    	                                		  if (NewMChar!=null)
		    	                                		  {
			    	                                		  if (ListeMagie.get(i).Cible==true)
			    	                                		  {
				    	                                          NewMChar.versx=PlayerInfo.pmapx;
				    	                                          NewMChar.versy=PlayerInfo.pmapy;
			    	                                		  }
			    	                                		  else
			    	                                		  if (ListeMagie.get(i).Monstre!=null)
			    	                                		  {
				    	                                          NewMChar.versx=ListeMagie.get(i).Monstre.mapx;
				    	                                          NewMChar.versy=ListeMagie.get(i).Monstre.mapy;	    	                                			  
			    	                                		  }
			    	                                          NewMChar.Transparency=ListeMagie.get(i).magie.Tran / 255;
			    	                                          SpriteE.AddSprite(NewMChar);
		    	                                		  }
		    	                                	}
		    	                                }
		      	                              }
		    	                              break;
		    	                          case 2 :
		    	                        	    // c'est la taille defaut de la magie
		    	                        	    int taillex,tailley,decx,decy;
		    	                        	    Rectangle Rect=new Rectangle();
		    			                        taillex=ListeMagie.get(i).magie.W / 16;
		    			                        tailley=ListeMagie.get(i).magie.H / 16;
		    			                        decx=ListeMagie.get(i).magie.X / 16;
		    			                        decy=ListeMagie.get(i).magie.Y / 16;
		    			                        Rect.x=ListeMagie.get(i).X-(((ListeMagie.get(i).taillezone+1)*taillex)+decx);
		    			                        Rect.width=ListeMagie.get(i).X+(((ListeMagie.get(i).taillezone+1)*taillex)+decx);
		    			                        Rect.y=ListeMagie.get(i).Y-(((ListeMagie.get(i).taillezone+1)*tailley)+decy);
		    			                        Rect.height=ListeMagie.get(i).Y+(((ListeMagie.get(i).taillezone+1)*tailley)+decy);
		    			                        mapx=ListeMagie.get(i).X;
		    			                        mapy=ListeMagie.get(i).Y;
		    			                        if  (((ListeMagie.get(i).magie.OnMonster==1) && (ListeMagie.get(i).Wizard==true)) ||
		    			                           (((ListeMagie.get(i).magie.OnMonster==0) || (ListeMagie.get(i).magie.OnMonster==2)) && (ListeMagie.get(i).MWizard!=null)))
	   			                                  if ((PlayerInfo.pmapx>=Rect.x) && (PlayerInfo.pmapx<=Rect.width) && (PlayerInfo.pmapy>=Rect.y) && (PlayerInfo.pmapy<=Rect.height) && (Dead==false))
	   			                                  {
	   			                                	  touche=true;
	   			                                	  ListeMagie.get(i).Cible=true;
	   			                                	  ListeMagie.get(i).Monstre=null;   			                                	  
	   	    	                                	  boolean[] res=ActionSort(ListeMagie.get(i));
	   	    	                                	  MonsterDead=res[0]; Touched=res[1];
	   			                                  }
		    			                        if  (((ListeMagie.get(i).magie.OnMonster==1) && (ListeMagie.get(i).MWizard!=null)) ||
		 	    			                           (((ListeMagie.get(i).magie.OnMonster==0) || (ListeMagie.get(i).magie.OnMonster==2)) && (ListeMagie.get(i).Wizard==true)))
		    			                        {
		    			                        	int j=0; int compte=ListeMonstre.size();
		    			                        	while (j<compte)
		    			                        	{
		    			                        		if ((ListeMonstre.get(j).mapx>=Rect.x) && (ListeMonstre.get(j).mapx<=Rect.width) && (ListeMonstre.get(j).mapy>=Rect.y) && (ListeMonstre.get(j).mapy<=Rect.height))
		    			                        		{
		    			                        			  ListeMagie.get(i).Monstre=ListeMonstre.get(j);
		    			                        			  ListeMagie.get(i).Cible=false;
		    	    	                                	  boolean[] res=ActionSort(ListeMagie.get(i));
		    	    	                                	  MonsterDead=res[0]; Touched=res[1];
		    			                                      if (MonsterDead)
		    			                                      {
		    			                                        compte--;
		    			                                        j--;
		    			                                      }
		    			                                      else
		    			                                      if (Touched)
		    			                                    	  ListeMagie.get(i).MAffected.add(ListeMonstre.get(j));	    			                        			
		    			                        		}
		    			                        		j++;
		    			                        	}
		    			                        }
		    			                        int hei,wid,j,k;
		    			                        wid=ListeMagie.get(i).taillezone;
	    			                            hei=wid;
		    			                        wid=wid*(ListeMagie.get(i).magie.W / 16);
		    			                        hei=hei*(ListeMagie.get(i).magie.H / 16);
		    			                        // maintenant on resize par rapport a la taille reel du graph
		    			                        j=ListeMagie.get(i).X-wid;
		    			                        while(j<=ListeMagie.get(i).X+wid)
		    			                        {
		    			                          k=ListeMagie.get(i).Y-hei;
		    			                          while(k<=ListeMagie.get(i).Y+hei)
		    			                          {
		    			                            // si nous et sort centree
		  	    			                        if ((ListeMagie.get(i).Wizard==true) && (ListeMagie.get(i).OnMonster==2))
		  	    			                        	SpriteE.AddSprite(new Animation(FrmSrc, null, ListeMagie.get(i).magie.Chipset,j,k,ListeMagie.get(i).magie.X+prealx, ListeMagie.get(i).magie.Y+prealy
		  	    			                        			, ListeMagie.get(i).magie.W, ListeMagie.get(i).magie.H, ListeMagie.get(i).magie.Z, ListeMagie.get(i).magie.DureeAnim, ListeMagie.get(i).magie.Tran));
		    			                            else
		  	    			                        	SpriteE.AddSprite(new Animation(FrmSrc, null, ListeMagie.get(i).magie.Chipset,j,k,ListeMagie.get(i).magie.X, ListeMagie.get(i).magie.Y
		  	    			                        			, ListeMagie.get(i).magie.W, ListeMagie.get(i).magie.H, ListeMagie.get(i).magie.Z, ListeMagie.get(i).magie.DureeAnim, ListeMagie.get(i).magie.Tran));
		    			                            k=k+(ListeMagie.get(i).magie.H / 16)+1;
		    			                          }
		    			                          j=j+(ListeMagie.get(i).magie.W / 16)+1;
		    			                        }
		    			                        break;
		    	                          case 4 :
		    	                        	  //Currently not implemented
		    	                        	  break;
	                				}
			                        if (ListeMagie.get(i).duree<=0)
			                        {
			                        	MagieDead=true;
		    			                if (ListeMagie.get(i).TypeMagie==1)
		    			                {
		    			                	if (ListeMagie.get(i).FormuleTouche.compareTo("")!=0)
		    			                	{
		    			    					resultat=(int) Calcule.Calcule(Formule.ReplaceStringMagie(ListeMagie.get(i).FormuleTouche, ListeMagie.get(i).MWizard, ListeMagie.get(i).Monstre));
		    			                        touche=(resultat > 0);
		    			                	}
		    			                    else
		    			                        touche=true;
		    			                    if (touche==true)
		    			                    {
		    			                    	if (ListeMagie.get(i).Cible==true)
		    			                    	{
		    			                            mapx=PlayerInfo.pmapx;
		    			                            mapy=PlayerInfo.pmapy;
		    			                    	}
		    			                        else
		    			                        {
		    			                            if (ListeMagie.get(i).Monstre!=null)
		    			                            {
		    			                                  mapx=ListeMagie.get(i).Monstre.mapx;
		    			                                  mapy=ListeMagie.get(i).Monstre.mapy;
		    			                            }
		    			                            else
		    			                               mapx=-1;
		    			                        }
		    			                        if (mapx!=-1)
		    			                        {	    			                        	
	    			                                int j=0;
		    			                        	ListeMagie.get(i).X=mapx;
		    			                        	ListeMagie.get(i).Y=mapy;
		    			                        	if (ListeMagie.get(i).FormuleEffet.startsWith("spell:")==true)
		    			                        	{
		    			                        		String temp=ListeMagie.get(i).FormuleEffet;
		    			                        		temp=temp.substring(6);
		    			                        		if (temp.indexOf(";")>=0)
		    			                        			temp=temp.substring(0,temp.indexOf(";")-1);
		    			                                touche=false;
		    			                                while((j<general.getMagies().size()) && (touche==false))
		    			                                {
		    			                                    if (general.getMagieByIndex(j).Name.compareTo(temp)==0) touche=true;
		    			                                    j++;
		    			                                }
		    			                        	}
		    			                        	else
		    			                        		touche=false;
	    			                                if (touche==true)
	    			                                {
	    			                                    j--;
	    			                                    ListeMagie.get(i).FormuleTouche=general.getMagieByIndex(j).FormuleTouche;
	    			                                    ListeMagie.get(i).FormuleTouche=general.getMagieByIndex(j).FormuleEffet;
	    			                                    ListeMagie.get(i).duree=ListeMagie.get(i).oldduree;
	    			                                    ListeMagie.get(i).magie=general.getMagieByIndex(j);
	    			                                    ListeMagie.get(i).TypeMagie=general.getMagieByIndex(j).MagieType;
	    			                                }
	    			                                else
	    			                                {
	    			                                	ListeMagie.get(i).TypeMagie=0;
	    			                                	ListeMagie.get(i).duree=ListeMagie.get(i).oldduree;
	    			                                	ListeMagie.get(i).FormuleTouche="oldfireball";
	    			                                }
	    			                                if (ListeMagie.get(i).duree==0) ListeMagie.get(i).duree=1;
	    			                                MagieDead=false;
		    			                        }
		    			                        else
		    			                        {
		    			                            if (ListeMagie.get(i).Cible==true)
		    			                            {
		    	                                		  if (ListeMagie.get(i).magie.Chipset.compareTo("")!=0)
		    	                                		  {
		    	                                			  Sprite sprite=null;
	    	                                				  sprite=player;
		    	                                			  if (sprite!=null)
		    		  	    			                        	SpriteE.AddSprite(new Animation(FrmSrc, sprite, ListeMagie.get(i).magie.Chipset,sprite.x,sprite.y,ListeMagie.get(i).magie.X, ListeMagie.get(i).magie.Y
		    		  	    			                        			, ListeMagie.get(i).magie.W, ListeMagie.get(i).magie.H, ListeMagie.get(i).magie.Z, ListeMagie.get(i).magie.DureeAnim, ListeMagie.get(i).magie.Tran));
		    	                                		  }
		    	                  				    	  SpriteE.AddSprite(new Degat(FrmSrc, "Miss", player, Color.WHITE));
		    			                            }
	    			                                else
	    			                                {
		    			                                if (ListeMagie.get(i).Monstre!=null)
		    			                                {
		    			                                	ListeMagie.get(i).Monstre.TypeMonstre=1;
		      	                                		  if (ListeMagie.get(i).magie.Chipset.compareTo("")!=0)
		    	                                		  {
		    	                                			  Sprite sprite=null;
		    	                                			  if (ListeMagie.get(i).Monstre!=null)
		    	                                				  sprite=ListeMagie.get(i).Monstre.sprite;
		    	                                			  if (sprite!=null)
		    		  	    			                        	SpriteE.AddSprite(new Animation(FrmSrc, sprite, ListeMagie.get(i).magie.Chipset,sprite.x,sprite.y,ListeMagie.get(i).magie.X, ListeMagie.get(i).magie.Y
		    		  	    			                        			, ListeMagie.get(i).magie.W, ListeMagie.get(i).magie.H, ListeMagie.get(i).magie.Z, ListeMagie.get(i).magie.DureeAnim, ListeMagie.get(i).magie.Tran));
		    	                                		  }
		      	                				    	  SpriteE.AddSprite(new Degat(FrmSrc, "Miss", ListeMagie.get(i).Monstre.sprite, Color.WHITE));
		    			                                }
	    			                                }
		    			                        }
		    			                    }
		    			                    else
		                				    	  SpriteE.AddSprite(new Degat(FrmSrc, "Miss", ListeMagie.get(i).Monstre.sprite, Color.WHITE));
		    			                }
		    			                if (MagieDead==true)
		    			                {
	   			                            if (ListeMagie.get(i).Cible==true)
	   			                            {
		    			                       MagicDommage=0;
		    			                       MagicAttaque=0;
		    			                       MagicDefense=0;
		    			                       MagicEsquive=0;
	    			                    	   Freeze=false;
	   			                            }
	   			                            else
	   			                            {
		    			                       if (ListeMagie.get(i).Monstre!=null)
		    			                       {
		    			                    	   ListeMagie.get(i).Monstre.MagicDommage=0;
		    			                    	   ListeMagie.get(i).Monstre.MagicAttaque=0;
		    			                    	   ListeMagie.get(i).Monstre.MagicDefense=0;
		    			                    	   ListeMagie.get(i).Monstre.MagicEsquive=0;
		    			                    	   ListeMagie.get(i).Monstre.Bloque=false;
		    			                       }
	   			                            }
	   			                            int j=0;
		    			                    while (j<ListeMagie.get(i).MAffected.size())
		    			                    {
		    			                    	ListeMagie.get(i).MAffected.get(j).MagicDommage=0;
		    			                    	ListeMagie.get(i).MAffected.get(j).MagicAttaque=0;
		    			                    	ListeMagie.get(i).MAffected.get(j).MagicDefense=0;
		    			                    	ListeMagie.get(i).MAffected.get(j).MagicEsquive=0;
		    			                    	ListeMagie.get(i).MAffected.get(j).Bloque=false;
		    			                    	j++;
		    			                    }
		    			                    ListeMagie.get(i).MAffected.clear();
		    			                    ListeMagie.remove(i);
		    			                    compte2=ListeMagie.size();
		    			                    i--;
		    			                }
			                        }
	                			}
	                		}
			                else
			                {
			                	ListeMagie.get(i).attente++;
			                }
	                	}
	            		else
	            		{
		                    ListeMagie.get(i).MAffected.clear();
		                    ListeMagie.remove(i);
		                    compte2=ListeMagie.size();
		                    i--;
	            		}
	            		i++;
	                }
		        }
	        }
	    };
	    return new Timer (800, action);
	}
	
	private Timer TimerGereMonster()
	{
		ActionListener action = new ActionListener ()
	    {
	        public void actionPerformed (ActionEvent event)
	        {	        	
	        	int i,k,CaseX, CaseY,Dext,Dext2,Degat,compte;
	        	float pmapx,pmapy,mmapx,mmapy;
	        	MagieGame NewMagieEnCours;;
	        	boolean trouve,access,magiespecial;
        		if (((IsMenuActive==true)||(IsInEvent==true))&&(general.isMenuFreeze()==true))
        			return;
	        	synchronized(ListeMonstre)
	        	{
		        	i=0; compte=ListeMonstre.size();
		        	while (i<compte)
		        	{
	       	            // gere les actions des monstres
		        		ListeMonstre.get(i).attentemove++;
		        	    if (ListeMonstre.get(i).attentemove > ListeMonstre.get(i).totalattente)
		        	    {
		        	    	ListeMonstre.get(i).attentemove=0;
		        	    	switch(ListeMonstre.get(i).TypeMonstre)
		        	    	{
		        	    		case 0 :
		        	    			// inoffensif
		        	                if (ListeMonstre.get(i).Bloque==false)
		        	                {
		        	                	ListeMonstre.get(i).totalattente=Util.random(8);
		        	                    CaseX=ListeMonstre.get(i).mapx / 2; CaseY=ListeMonstre.get(i).mapy / 2;
		        	                    switch(Util.random(4))
		        	                    {
		        	                    	case 0 : CaseY--; break;
		        	                    	case 1 : CaseX++; break;
		        	                    	case 2 : CaseY++; break;
		        	                    	case 3 : CaseX--; break;
		        	                    }
		        	                    if ((CaseX >= 0) && (CaseX < CurrentMap.TailleX)
		        	                      && (CaseY >= 0) && (CaseY < CurrentMap.TailleY))
		        	                    {
		        	                      access=true;
			        	    			  if (CurrentMap.cases[CaseX][CaseY].X1 > 0)
			        	    			    access=block[0][CurrentMap.cases[CaseX][CaseY].X1-1][CurrentMap.cases[CaseX][CaseY].Y1-1];
			        	    			  if (CurrentMap.cases[CaseX][CaseY].X2 > 0)
			        	    			    access=block[0][CurrentMap.cases[CaseX][CaseY].X2-1][CurrentMap.cases[CaseX][CaseY].Y2-1];
			        	    			  if (ListeMonstre.get(i).zone>=0)
			        	    			  {
			        	    				  if (zones.get(ListeMonstre.get(i).zone).X1>=0)
			        	    				  {
			        	                          if (access=true) access=(CaseX>=zones.get(ListeMonstre.get(i).zone).X1);
			        	                          if (access=true) access=(CaseY>=zones.get(ListeMonstre.get(i).zone).Y1);
			        	                          if (access=true) access=(CaseX<=zones.get(ListeMonstre.get(i).zone).X2);
			        	                          if (access=true) access=(CaseY<=zones.get(ListeMonstre.get(i).zone).Y2);		        	    					  
			        	    				  }
			        	    			  }
		        	                      if (access=true) access=!IsMonsterOnCase(CaseX,CaseY);
		        	                      if (access)
		        	                      {
		        	                    	  ListeMonstre.get(i).mapx=CaseX*2; ListeMonstre.get(i).mapy=CaseY*2;
		        	                      }
		        	                    }
		        	                }
		        	                break;
		        	    		case 1 :
		        	    		case 2 :
		        	                // choisi un adversaire
		        	    			ListeMonstre.get(i).attentemove=0;
		        	    			ListeMonstre.get(i).totalattente=1;
		        	    			trouve=false;
		        	                CaseX=ListeMonstre.get(i).mapx+MDetect+1;
		        	                CaseY=ListeMonstre.get(i).mapy+MDetect+1;
		    	                    if((Math.abs(ListeMonstre.get(i).mapx - PlayerInfo.pmapx)<MDetect) &&
		    	                       (Math.abs(ListeMonstre.get(i).mapy - PlayerInfo.pmapy)<MDetect))
		    	                    {
		    	                        CaseX=Math.abs(PlayerInfo.pmapx-ListeMonstre.get(i).mapx);
		    	                        CaseY=Math.abs(PlayerInfo.pmapy-ListeMonstre.get(i).mapy);
		    	                        trouve=(!Dead);
		    	                    }
		        	                // on a choisi l'adversaire maintenant on choisi l'action si proche on attaque sinon on bouge
		        	                NewMagieEnCours=null; k=0;
		        	                if (trouve==true)
		        	                {
		        	                    trouve=false; magiespecial=false;
		        	                    if (ListeMonstre.get(i).monstre.Spell.size()>0)
		        	                    {
			        	                      Dext=Util.random(100); k=0; Dext2=0;
		        	                    	  while ((k<ListeMonstre.get(i).monstre.Spell.size()) && (trouve==false))
		        	                    	  {
		        	                    		  if (ListeMonstre.get(i).monstre.Spell.get(k)>0)
		        	                    		  {
		        	                    			  if (Dext<=ListeMonstre.get(i).monstre.LuckSpell.get(k)+Dext2)
		        	                    				  trouve=true;
		        	                    			  else
		        	                    				  Dext2=Dext2+ListeMonstre.get(i).monstre.LuckSpell.get(k);
		        	                    			  if (trouve==true)
		        	                    				  // on verifie que le joueur n'est pas sous l'influence du sort choisi
		        	                    				  trouve=!HasMagie(null, null);
		        	                    		  }
		        	                    		  k++;
		        	                    	  }
		        	                    }
		        	                    magiespecial=false;
		        	                    // on est dans les stat pour lancer un sort!!=>!
		        	                    if (trouve)
		        	                    {
		        	                        k--;
		        	                        NewMagieEnCours=new MagieGame(general.getMagieByIndex(ListeMonstre.get(i).monstre.Spell.get(k)-1));
		        	                        NewMagieEnCours.TypeMagie=NewMagieEnCours.magie.MagieType;
		        	                        if (NewMagieEnCours.TypeMagie!=6)
		        	                        {
		        	                        	//animation
		        	                        	if (ListeMonstre.get(i).sprite!=null)
		        	                        	{
		        	                        		  ListeMonstre.get(i).sprite.Action=2;
			        	    				          PlaySound(ListeMonstre.get(i).monstre.SoundConcentration,"",false);
			        	    				          ListeMonstre.get(i).sprite.AttenteTotal=NewMagieEnCours.magie.TempsIncantation;
			        	    				          ListeMonstre.get(i).sprite.AttenteAttaque=0;
		        	                        	}
		        	                        }
		        	                        else
		        	                        {
		        	                          magiespecial=true;
		        	                          NewMagieEnCours.TypeMagie=0;
		        	                        }
		        	                        ListeMonstre.get(i).attentemove=0;
		        	                        NewMagieEnCours.Wizard=false;
		        	                        NewMagieEnCours.MWizard= ListeMonstre.get(i);
		        	                        NewMagieEnCours.Cible=false;
		        	                        NewMagieEnCours.Monstre=null;
		        	                        NewMagieEnCours.attente=0;
		        	                        if (magiespecial==false)
		        	                        	ListeMonstre.get(i).tempsincantation=NewMagieEnCours.magie.TempsIncantation;
		        	                        NewMagieEnCours.attentetotal=(ListeMonstre.get(i).tempsincantation / 60)-1;
		        	                        if (NewMagieEnCours.magie.OnMonster==0)
		        	                          NewMagieEnCours.Cible=true;
		        	                        else
		        	                        if (NewMagieEnCours.magie.OnMonster==2)
		        	                          NewMagieEnCours.Monstre=ListeMonstre.get(i);
		        	                        else
		        	                        {
		        	                          // on cherche la cible pour les sorts defensifs
		        	                          Degat=10000;
		        	                          for (k=0;k<ListeMonstre.size();k++)
		        	                          {
		        	                            if((Math.abs(ListeMonstre.get(i).mapx - ListeMonstre.get(k).mapx)<MDetect) &&
		        	                               (Math.abs(ListeMonstre.get(i).mapy - ListeMonstre.get(k).mapy)<MDetect))
		        	                            {
		        	                              if (Math.round((ListeMonstre.get(k).vie / ListeMonstre.get(k).monstre.Vie)*1000) <Degat)
		        	                              {
		        	                                NewMagieEnCours.Monstre=ListeMonstre.get(k);
		        	                                Degat=Math.round((ListeMonstre.get(k).vie / ListeMonstre.get(k).monstre.Vie)*1000);
		        	                              }
		        	                            }
		        	                          }
		        	                        }
		        	                        if (NewMagieEnCours.TypeMagie==5)
		        	                          ListeMonstre.get(i).totalattente=(NewMagieEnCours.magie.TempsIncantation / 15);
		        	                        else
		        	                          ListeMonstre.get(i).totalattente=(NewMagieEnCours.magie.TempsIncantation / 30);
		        	                        NewMagieEnCours.duree=1;
		        	                        NewMagieEnCours.taillezone=0;
		        	                        if (NewMagieEnCours.magie.FormuleZone.compareTo("")!=0)
		        	                        	NewMagieEnCours.taillezone=(int) Calcule.Calcule(Formule.ReplaceStringMagie(NewMagieEnCours.magie.FormuleZone,NewMagieEnCours.MWizard,NewMagieEnCours.Monstre));
		        	                        if (NewMagieEnCours.magie.FormuleDuree.compareTo("")!=0)
		        	                        	NewMagieEnCours.duree=(int) Calcule.Calcule(Formule.ReplaceStringMagie(NewMagieEnCours.magie.FormuleDuree,NewMagieEnCours.MWizard,NewMagieEnCours.Monstre));
		        	                        NewMagieEnCours.FormuleTouche=NewMagieEnCours.magie.FormuleTouche;
		        	                        NewMagieEnCours.FormuleEffet=NewMagieEnCours.magie.FormuleEffet;
		        	                        NewMagieEnCours.attente=0;
		        	                       	NewMagieEnCours.attentetotal=(NewMagieEnCours.magie.TempsIncantation / 60)-1;
		        	                        NewMagieEnCours.FirstTime=true;
		        	                        NewMagieEnCours.OnMonster=NewMagieEnCours.magie.OnMonster;
		        	                        if (NewMagieEnCours.Cible==true)
		        	                        {
		        	                          NewMagieEnCours.X=PlayerInfo.pmapx;
		        	                          NewMagieEnCours.Y=PlayerInfo.pmapy;
		        	                        }
		        	                        else
		        	                        {
		        	                          if (NewMagieEnCours.Monstre!=null)
		        	                          {
		        	                        	NewMagieEnCours.X=NewMagieEnCours.Monstre.mapx;
		        	                        	NewMagieEnCours.Y=NewMagieEnCours.Monstre.mapy;
		        	                          }
		        	                        }
		        	                        if (magiespecial==false)
		        	                        	synchronized(ListeMagie) { ListeMagie.add(NewMagieEnCours); }
		        	                    }
		        	                    if ((magiespecial==true) || (trouve==false))
		        	                    {
		        	                      // sinon on attaque
		        	                      pmapx=(PlayerInfo.pmapx) / 2;
		        	                      pmapy=(PlayerInfo.pmapy) / 2;
		        	                      mmapx=ListeMonstre.get(i).mapx / 2;
		        	                      mmapy=ListeMonstre.get(i).mapy / 2;
		        	                      if ((Math.abs(pmapx-mmapx) <=1.8)
		        	                      && (Math.abs(pmapy-mmapy) <=1.8))
		        	                      {
		        	                    	  if (ListeMonstre.get(i).sprite!=null)
		        	                    		  ListeMonstre.get(i).sprite.Action=1;
		        	            	    	  Dext=(int) Calcule.Calcule(Formule.ReplaceStatVariable(general.getClassesMonstre().get(ListeMonstre.get(i).monstre.ClasseMonstre).FormuleAttaque,ListeMonstre.get(i)))+ListeMonstre.get(i).MagicAttaque;
		        	            	    	  Dext2=(int) Calcule.Calcule(Formule.ReplaceStatVariable(PlayerInfo.Classe.FormuleEsquive,ListeMonstre.get(i)))+MagicEsquive;
		        	            	    	  trouve=false;
		        	            	    	  if (player!=null)
		        	            	    	  {
		        	            	    		  // pare
			        	            	    	  if (player.Action==5)
			        	            	    	  {
				        	                          if ((ListeMonstre.get(i).Direction==0) && (PlayerDirection==2)) trouve=true;
				        	                          if ((ListeMonstre.get(i).Direction==1) && (PlayerDirection==3)) trouve=true;
				        	                          if ((ListeMonstre.get(i).Direction==2) && (PlayerDirection==0)) trouve=true;
				        	                          if ((ListeMonstre.get(i).Direction==3) && (PlayerDirection==1)) trouve=true;
				        	                          if (trouve==false) Dext2=Dext2+(Dext2*(20 / 100));
			        	            	    	  }
		        	            	    	  }
		        	            		      if ((Dext > Dext2) && (trouve==false))
		        	            		      {
		        	            		         // touché!
			        	                          if (magiespecial==true)
			        	                          {
			        	                        	synchronized(ListeMagie) { ListeMagie.add(NewMagieEnCours); }
			        	                             magiespecial=false;
			        	                          }
		        	            		    	  Degat=(int) Calcule.Calcule(Formule.ReplaceStatVariable(general.getClassesMonstre().get(ListeMonstre.get(i).monstre.ClasseMonstre).FormuleDegat,ListeMonstre.get(i)))+ListeMonstre.get(i).MagicDommage;
		        	            		    	  Degat-=(int) Calcule.Calcule(Formule.ReplaceStatVariable(PlayerInfo.Classe.FormuleDefense,ListeMonstre.get(i)))+MagicDefense;
		        	            		    	  if (Degat<0) Degat=0;
		        	            		    	  PlayerInfo.Vie=PlayerInfo.Vie-Degat;
		        	            		    	  if (PlayerInfo.Vie<=0)
		        	            		    	  {
		        	            		    		  if (IsInEvent)
		        	            		    			  PlayerInfo=PSave;
		        	            		    		  if (player!=null)
		        	            		    			  player.Action=0;
		        	            		    		  Dead=true;
		        	            		    		  PlayerInfo.Vie=0;
		        	            		    		  KillPlayer();
		        	            		    		  PlayerInfo.CurrentXP=(int) Math.round(PlayerInfo.CurrentXP
		        	                                                            -(PlayerInfo.CurrentXP*0.01));
		        	            		    		  if (PlayerInfo.CurrentXP<PlayerInfo.PrevXP)
		        	            		    			  PlayerInfo.CurrentXP=PlayerInfo.PrevXP;
		        	            		    		  PlayerInfo.Gold=(int) Math.round(PlayerInfo.Gold
		        	                                                          -(PlayerInfo.Gold*0.01));
		        	            		    		  PlayerInfo.Vie=0;
		        	            		    	  }
		        	            		    	  else
		        	            		    	  {
		        	            			          SpriteE.AddSprite(new Degat(Self,Integer.toString(Degat),player,Color.ORANGE));
		        	            			          if (fiolevie!=null)
		        	            			        	  fiolevie.Redraw();
		        	            		    	  }
		        	                             
		        	            		      }
		        	            		      else
		        	            		      {
	        	            			          SpriteE.AddSprite(new Degat(Self,"Miss",player,Color.ORANGE));
		        	            		      }
		        	                      }
		        	                      else
		        	                      {
		        	                        if ((ListeMonstre.get(i).TypeMonstre==1) && (ListeMonstre.get(i).Bloque==false))
		        	                        {
		        	                          k=ListeMonstre.get(i).monstre.Vitesse; trouve=false;
		        	                          while ((k>0) && (trouve==false))
		        	                          {
		        	                            CaseX=ListeMonstre.get(i).mapx / 2; CaseY=ListeMonstre.get(i).mapy / 2;
		        	                            if (Math.abs(CaseX-(PlayerInfo.pmapx / 2)) >= Math.abs(CaseY-(PlayerInfo.pmapy / 2)))
		        	                            {
		        	                              if (CaseX > (PlayerInfo.pmapx / 2)) CaseX--;
		        	                              else CaseX++;
		        	                            }
		        	                            else
		        	                            {
		        	                              if (CaseY > (PlayerInfo.pmapy / 2)) CaseY--;
		        	                              else CaseY++;
		        	                            }
		        	                            if ((CaseX >= 0) && (CaseX < CurrentMap.TailleX)
		        	                              && (CaseY >= 0) && (CaseY < CurrentMap.TailleY))
		        	                            {
		        	                              access=true;
		    		        	    			  if (CurrentMap.cases[CaseX][CaseY].X1 > 0)
	    			        	    			    access=block[0][CurrentMap.cases[CaseX][CaseY].X1-1][CurrentMap.cases[CaseX][CaseY].Y1-1];
	    			        	    			  if ((CurrentMap.cases[CaseX][CaseY].X2 > 0) && (access==true))
	    			        	    			    access=block[0][CurrentMap.cases[CaseX][CaseY].X2-1][CurrentMap.cases[CaseX][CaseY].Y2-1];
		        	                              if (ListeMonstre.get(i).zone>=0)
		        	                              {
		    		        	    				  if (zones.get(ListeMonstre.get(i).zone).X1>=0)
		    		        	    				  {
		    		        	                          if (access==true) access=(CaseX>=zones.get(ListeMonstre.get(i).zone).X1);
		    		        	                          if (access==true) access=(CaseY>=zones.get(ListeMonstre.get(i).zone).Y1);
		    		        	                          if (access==true) access=(CaseX<=zones.get(ListeMonstre.get(i).zone).X2);
		    		        	                          if (access==true) access=(CaseY<=zones.get(ListeMonstre.get(i).zone).Y2);		        	    					  
		    		        	    				  }
		        	                              }
		        	                              if (access==true) access=!(IsMonsterOnCase(CaseX,CaseY));
		        	                              // si on est bloque on choisi une alternative
		        	                              if (access==false)
		        	                              {
		        	                                CaseX=ListeMonstre.get(i).mapx / 2; CaseY=ListeMonstre.get(i).mapy / 2;
		        	                                if (Math.abs(CaseX-(PlayerInfo.pmapx / 2)) >= Math.abs(CaseY-(PlayerInfo.pmapy / 2)))
		        	                                {
		        	                                  if (CaseY > (PlayerInfo.pmapy / 2)) CaseY--;
		        	                                  else CaseY++;
		        	                                }
		        	                                else
		        	                                {
		        	                                  if (CaseX > (PlayerInfo.pmapx / 2)) CaseX--;
		        	                                  else CaseX++;
		        	                                }
		        	                                if ((CaseX >= 0) && (CaseX < CurrentMap.TailleX)
		        	                                  && (CaseY >= 0) && (CaseY < CurrentMap.TailleY))
		        	                                {
		        	                                  access=true;
			    		        	    			  if (CurrentMap.cases[CaseX][CaseY].X1 > 0)
			    			        	    			    access=block[0][CurrentMap.cases[CaseX][CaseY].X1-1][CurrentMap.cases[CaseX][CaseY].Y1-1];
		    			        	    			  if ((CurrentMap.cases[CaseX][CaseY].X2 > 0) && (access==true))
		    			        	    				   access=block[0][CurrentMap.cases[CaseX][CaseY].X2-1][CurrentMap.cases[CaseX][CaseY].Y2-1];
			        	                              if (ListeMonstre.get(i).zone>=0)
			        	                              {
			    		        	    				  if (zones.get(ListeMonstre.get(i).zone).X1>=0)
			    		        	    				  {
			    		        	                          if (access==true) access=(CaseX>=zones.get(ListeMonstre.get(i).zone).X1);
			    		        	                          if (access==true) access=(CaseY>=zones.get(ListeMonstre.get(i).zone).Y1);
			    		        	                          if (access==true) access=(CaseX<=zones.get(ListeMonstre.get(i).zone).X2);
			    		        	                          if (access==true) access=(CaseY<=zones.get(ListeMonstre.get(i).zone).Y2);		        	    					  
			    		        	    				  }
			        	                              }
		        	                                  if (access==true) access=!(IsMonsterOnCase(CaseX,CaseY));
		        	                                }
		        	                              }
		        	                              if (access==true)
		        	                              {
		        	                                if (CaseX*2 > ListeMonstre.get(i).mapx) ListeMonstre.get(i).Direction=1;
		        	                                if (CaseX*2 < ListeMonstre.get(i).mapx) ListeMonstre.get(i).Direction=3;
		        	                                if (CaseY*2 > ListeMonstre.get(i).mapy) ListeMonstre.get(i).Direction=2;
		        	                                if (CaseY*2 < ListeMonstre.get(i).mapy) ListeMonstre.get(i).Direction=0;
		        	                                ListeMonstre.get(i).mapx=CaseX*2; ListeMonstre.get(i).mapy=CaseY*2;
		        	                              }
		        	                            }
		        	                            k--;
		        	                            if (k>0)
		        	                            {
		        	                              pmapx=PlayerInfo.pmapx / 2;
		        	                              pmapy=PlayerInfo.pmapy / 2;
		        	                              mmapx=ListeMonstre.get(i).mapx / 2;
		        	                              mmapy=ListeMonstre.get(i).mapy / 2;
		        	                              if ((Math.abs(pmapx-mmapx) <=1.8)
		        	                              && (Math.abs(pmapy-mmapy) <=1.8))
		        	                                trouve=true;
		        	                            }
		        	                          }
		        	                        }
		        	                      }
		        	                    }
		        	                  }
		        	                  else
		        	                  {
		        	                    if (ListeMonstre.get(i).TypeMonstre==1)
		        	                    {
		        	                    	access=false;
		        	                        // le monstre n'a pas trouve d'adversaire. Il bouge.
		        	                    	ListeMonstre.get(i).totalattente=Util.random(8);
		        	                    	CaseX=ListeMonstre.get(i).mapx / 2; CaseY=ListeMonstre.get(i).mapy / 2;
		        	                      	switch(Util.random(4))
		        	                      	{
		        	                      		case 0 : CaseY--; break;
		        	                      		case 1 : CaseX++; break;
		        	                      		case 2 : CaseY++; break;
		        	                      		case 3 : CaseX--; break;
		        	                      	}
	    	                                if ((CaseX >= 0) && (CaseX < CurrentMap.TailleX)
	   	                                    && (CaseY >= 0) && (CaseY < CurrentMap.TailleY))
	    	                                {
	    	                                  access=true;
	    		        	    			  if (CurrentMap.cases[CaseX][CaseY].X1 > 0)
	    			        	    			    access=block[0][CurrentMap.cases[CaseX][CaseY].X1-1][CurrentMap.cases[CaseX][CaseY].Y1-1];
				        	    			  if ((CurrentMap.cases[CaseX][CaseY].X2 > 0) && (access==true))
				        	    				   access=block[0][CurrentMap.cases[CaseX][CaseY].X2-1][CurrentMap.cases[CaseX][CaseY].Y2-1];
	        	                              if (ListeMonstre.get(i).zone>=0)
	        	                              {
	    		        	    				  if (zones.get(ListeMonstre.get(i).zone).X1>=0)
	    		        	    				  {
	    		        	                          if (access==true) access=(CaseX>=zones.get(ListeMonstre.get(i).zone).X1);
	    		        	                          if (access==true) access=(CaseY>=zones.get(ListeMonstre.get(i).zone).Y1);
	    		        	                          if (access==true) access=(CaseX<=zones.get(ListeMonstre.get(i).zone).X2);
	    		        	                          if (access==true) access=(CaseY<=zones.get(ListeMonstre.get(i).zone).Y2);		        	    					  
	    		        	    				  }
	        	                              }
	    	                                  if (access==true) access=!(IsMonsterOnCase(CaseX,CaseY));
	    	                                }
		        	                        if (access==true)
		        	                        {
	        	                                if (CaseX*2 > ListeMonstre.get(i).mapx) ListeMonstre.get(i).Direction=1;
	        	                                if (CaseX*2 < ListeMonstre.get(i).mapx) ListeMonstre.get(i).Direction=3;
	        	                                if (CaseY*2 > ListeMonstre.get(i).mapy) ListeMonstre.get(i).Direction=2;
	        	                                if (CaseY*2 < ListeMonstre.get(i).mapy) ListeMonstre.get(i).Direction=0;
	        	                                ListeMonstre.get(i).mapx=CaseX*2; ListeMonstre.get(i).mapy=CaseY*2;
		        	                        }
		        	                    }
		        	                  }
		        	                }
				        	    	if ((ListeMonstre.get(i).sprite!=null) && (ListeMonstre.get(i).sprite.isDead))
				        	    		ListeMonstre.get(i).sprite=null;
				        	    	if (ListeMonstre.get(i).sprite!=null)
				                    {
				                    	ListeMonstre.get(i).sprite.Direction=ListeMonstre.get(i).Direction;
				                    	ListeMonstre.get(i).sprite.versx=ListeMonstre.get(i).mapx;
				                    	ListeMonstre.get(i).sprite.versy=ListeMonstre.get(i).mapy;
				                    }
				        	    	else
				        	    	{
					                	if (VerifieSpriteVisible(ListeMonstre.get(i).mapx,ListeMonstre.get(i).mapy))
					                	{
					                		ListeMonstre.get(i).sprite=new TMChar(Self,ListeMonstre.get(i).monstre.Name,ListeMonstre.get(i).monstre.Chipset,ListeMonstre.get(i).monstre.SoundAttaque,ListeMonstre.get(i).monstre.SoundWound,ListeMonstre.get(i).monstre.SoundConcentration,0,0,ListeMonstre.get(i).monstre.W,ListeMonstre.get(i).monstre.H,ListeMonstre.get(i).mapx,ListeMonstre.get(i).mapy,0,0,ListeMonstre.get(i).Direction,0,0,Sprite.idSprite.idMonstre,0,1,ListeMonstre.get(i).monstre.Bloquant);
					                		SpriteE.AddSprite(ListeMonstre.get(i).sprite);
					                	}
				        	    	}
		        	    	}
		        	    	i++;
		        	    }
		        	}
	        }
	    };
	    return new Timer (400, action);
	}
	private boolean VerifieCondition(int i,int j,String ligne)
	{
	  int m;
	  int Variablei , Resultati;
	  int posop,op,oplen;
	  String Variable , Resultat;
	  boolean Good ,trouve;
	  boolean ToInteger;
	  Good=true;
	  posop=ligne.indexOf("!="); op=0; oplen=2;
	  if (posop==-1) { posop=ligne.indexOf(">="); op=1; oplen=2; }
	  if (posop==-1) { posop=ligne.indexOf("<="); op =2; oplen=2; }
	  if (posop==-1) { posop=ligne.indexOf(">"); op =3; oplen=1; }
	  if (posop==-1) { posop=ligne.indexOf("<"); op =4; oplen=1; }
	  if (posop==-1) { posop=ligne.indexOf("="); op =5; oplen=1; }
	  Variable=ligne.substring(0,posop);
	  Resultat=ligne.substring(posop+oplen,ligne.length());
	  if ((Variable.compareTo("%Direction%")==0) || (Variable.compareTo("%EvCaseX%")==0) || (Variable.compareTo("%EvCaseY")==0))
	    Evenements[i][j].HasToCheckPage=true;
	  try{
	    Variable = Formule.ReplaceStringVariable(Variable);
	    Resultat=Formule.ReplaceStringVariable(Resultat);
	    Integer.parseInt(Variable);
	    Integer.parseInt(Resultat);
	    ToInteger=true;
	  } catch(NumberFormatException e){
	    ToInteger=false;
	  }
	  Resultat=Formule.ReplaceStringVariable(Resultat);
	  if (Variable.compareTo("%Timer%")==0)
	  {
	    Resultati=(int) Math.floor(Calcule.Calcule(Resultat));
	    if ((Resultati>Evenements[i][j].WaitingTimer) || (Evenements[i][j].WaitingTimer==0))
	    {
	      Evenements[i][j].WaitingTimer=Resultati;
	    }
	    Resultat=Integer.toString(Resultati);
	  }
	  if (Variable.compareTo("%Timer2%")==0)
	  {
	    Resultati=(int) Math.floor(Calcule.Calcule(Resultat));
	    if ((Resultati>Evenements[i][j].WaitingTimer2) || (Evenements[i][j].WaitingTimer2==0))
	    {
	      Evenements[i][j].WaitingTimer2=Resultati;
	    }
	    Resultat=Integer.toString(Resultati);
	  }
	  if (Variable.compareTo("%Timer3%")==0)
	  {
	    Resultati=(int) Math.floor(Calcule.Calcule(Resultat));
	    if ((Resultati>Evenements[i][j].WaitingTimer3) || (Evenements[i][j].WaitingTimer3==0))
	    {
	      Evenements[i][j].WaitingTimer3=Resultati;
	    }
	    Resultat=Integer.toString(Resultati);
	  }
	  if (Variable.compareTo("%EvCaseX%")==0)
	  {
	    Variable=Integer.toString(i);
	    ToInteger=true;
	  }
	  else
	  if (Variable.compareTo("%EvCaseY%")==0)
	  {
	    Variable=Integer.toString(j);
	    ToInteger=true;
	  }
	  if (Variable.compareTo("%Inventaire%")==0)
	  {
	    m=0; trouve=false;
	    while ((m<100) && (trouve=false))
	    {
	      if (PlayerInfo.Inventaire[m][0] > 0)
	      {
	        if (general.getObjetByIndex(PlayerInfo.Inventaire[m][0]-1).Name == Resultat) trouve = true;
	      }
	      else
	        if (Resultat.compareTo("")==0)
	          trouve=true;
	      m++;
	    }
	    if ((trouve=false) && (op==5)) Good=false;
	    if ((trouve=true) && (op==0)) Good=false;
	  }
	  else
	  if (Variable.compareTo("%Arme%")==0)
	  {
		trouve=false;
	    if (PlayerInfo.Arme > 0)
	    {
	      if (general.getObjetByIndex(PlayerInfo.Arme-1).Name == Resultat) trouve = true;
	    }
	    else
	      if (Resultat.compareTo("")==0)
	        trouve=true;
	    if ((trouve==false) && (op==5)) Good=false;
	    if ((trouve==true) && (op==0)) Good=false;
	  }
	  else
	  if (Variable.compareTo("%Bouclier%")==0)
	  {
		trouve=false;
	    if (PlayerInfo.Bouclier > 0)
	    {
	      if (general.getObjetByIndex(PlayerInfo.Bouclier-1).Name == Resultat) trouve = true;
	    }
	    else
	      if (Resultat.compareTo("")==0)
	        trouve=true;
	    if ((trouve==false) && (op==5)) Good=false;
	    if ((trouve==true) && (op==0)) Good=false;
	  }
	  else
	  if (Variable.compareTo("%Casque%")==0)
	  {
		trouve=false;
	    if (PlayerInfo.Casque > 0)
	    {
	      if (general.getObjetByIndex(PlayerInfo.Casque-1).Name == Resultat) trouve = true;
	    }
	    else
	      if (Resultat.compareTo("")==0)
	        trouve=true;
	    if ((trouve==false) && (op==5)) Good=false;
	    if ((trouve==true) && (op==0)) Good=false;
	  }
	  else
	  if (Variable.compareTo("%Armure%")==0)
	  {
		trouve=false;
	    if (PlayerInfo.Armure > 0)
	    {
	      if (general.getObjetByIndex(PlayerInfo.Armure-1).Name == Resultat) trouve = true;
	    }
	    else
	      if (Resultat.compareTo("")==0)
	        trouve=true;
	    if ((trouve==false) && (op==5)) Good=false;
	    if ((trouve==true) && (op==0)) Good=false;
	  }
	  else
	  {
	    if (ToInteger)
	    {
	      Variablei=0;
	      Resultati=0;
	      try {
	        Variablei=Integer.parseInt(Variable);
		    Resultati=(int) Math.floor(Calcule.Calcule(Resultat));
		  } catch(NumberFormatException e){
			  System.out.println(e.getMessage());
		  }
	      switch(op)
	      {
	        case 0: Good=Variablei!=Resultati; break;
	        case 1: Good=Variablei>=Resultati; break;
	        case 2: Good=Variablei<=Resultati; break;
	        case 3: Good=Variablei>Resultati; break;
	        case 4: Good=Variablei<Resultati; break;
	        case 5: Good=Variablei==Resultati; break;
	      }
	    }
	    else
	    {
	      if (Good==true)
	      {
	        switch(op)
	        {
	          case 0: Good=Variable.toUpperCase().compareTo(Resultat.toUpperCase())!=0; break;
	          case 1: Good=Variable.toUpperCase().compareTo(Resultat.toUpperCase())>=0; break;
	          case 2: Good=Variable.toUpperCase().compareTo(Resultat.toUpperCase())<=0; break;
	          case 3: Good=Variable.toUpperCase().compareTo(Resultat.toUpperCase())>0; break;
	          case 4: Good=Variable.toUpperCase().compareTo(Resultat.toUpperCase())<0; break;
	          case 5: Good=Variable.toUpperCase().compareTo(Resultat.toUpperCase())==0; break;
	        }
	      }
	    }
	  }
	  return Good;
	}

	private void DrawImage(Graphics2D g,Image img,int x,int y,int w,int h,ImageObserver obs)
	{
		g.drawImage(img, x+DecoX,y+DecoY,w,h,null);		
	}
	
	private BufferedImage CreateWindow(int w,int h,boolean CadreOnly)
	{
	  Rectangle Src=new Rectangle();
	  Rectangle Dest=new Rectangle();
	  Image TileW;
	  BufferedImage Surface;
	  ImageProducer improd;
	  ImageFilter cif2;
	  int i;
	  GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
	  if (CadreOnly)
			Surface=gc.createCompatibleImage(w,h,java.awt.Transparency.TRANSLUCENT);
	  else
		  Surface=gc.createCompatibleImage(w,h);
      Graphics2D g2d = Surface.createGraphics();
	  Src.x=0; Src.y=0; Src.width=(int) (32*Zoom); Src.height=(int) (32*Zoom);
	  Dest.x = 0; Dest.y = 0;
	  improd=SystemSurf.getSource();
	  if (CadreOnly==false)
	  {
		  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
		  TileW = createImage(new FilteredImageSource(improd, cif2));	  
		  TileW = TileW.getScaledInstance(w, h, Image.SCALE_FAST);
		  g2d.drawImage(TileW,Dest.x,Dest.y,w,h, null);
	  }
	  // maintenant le cadre
	  if(Zoom==3.2 || Zoom==2.5)
	  {
		  Src.x=(int) (35*Zoom); Src.y=0; Src.width=(int) (2+(1*(Zoom-1))); Src.height=(int) (3*Zoom); 
	  }
	  else 
	  {
		  Src.x=(int) (35*Zoom); Src.y=0; Src.width=(int) (1+(1*(Zoom-1))); Src.height=(int) (3*Zoom);
	  }
	  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
	  TileW = createImage(new FilteredImageSource(improd, cif2));	  
	  for (i=3;i<=w-3;i++)
	  {
		Dest.x=(int) (i*Zoom); Dest.y = 0;
		g2d.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
	    Dest.x=(int) (i*Zoom); Dest.y = (int) (h-(2*Zoom));
		g2d.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
	  }
	  if(Zoom==3.2 || Zoom==2.5)
	  {
		  Src.x=(int) (32*Zoom); Src.y=(int) (3*Zoom); Src.width=(int) (3*Zoom); Src.height=(int) (2+(1*(Zoom-1)));
	  }
	  else
	  {
		  Src.x=(int) (32*Zoom); Src.y=(int) (3*Zoom); Src.width=(int) (3*Zoom); Src.height=(int) (1+(1*(Zoom-1)));
	  }
	  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
	  TileW = createImage(new FilteredImageSource(improd, cif2));	  
	  for (i=3;i<=h-3;i++)
	  {
		Dest.x=0; Dest.y = (int) (i*Zoom);
		g2d.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
	    Dest.x=(int) (w-(2*Zoom)); Dest.y = (int) (i*Zoom);
		g2d.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
	  }
	  //les coins
	  Src.x=(int) (32*Zoom); Src.y=0; Src.width=(int) (3*Zoom); Src.height=(int) (3*Zoom);
	  Dest.x=0; Dest.y = 0;
	  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
	  TileW = createImage(new FilteredImageSource(improd, cif2));	  
 	  g2d.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
	  Src.x=(int) (60*Zoom); Src.y=0; Src.width=(int) (3*Zoom); Src.height=(int) (3*Zoom);
	  Dest.x=(int) (w-(3*Zoom)); Dest.y=0;
	  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
	  TileW = createImage(new FilteredImageSource(improd, cif2));	  
 	  g2d.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
	  Src.x=(int) (32*Zoom); Src.y=(int) (28*Zoom); Src.width=(int) (3*Zoom); Src.height=(int) (3*Zoom);
	  Dest.x=0; Dest.y=(int) (h-(3*Zoom));
	  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
	  TileW = createImage(new FilteredImageSource(improd, cif2));	  
 	  g2d.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
	  Src.x=(int) (60*Zoom); Src.y=(int) (28*Zoom); Src.width=(int) (3*Zoom); Src.height=(int) (3*Zoom);
	  Dest.x=(int) (w-(3*Zoom)); Dest.y=(int) (h-(3*Zoom));
	  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
	  TileW = createImage(new FilteredImageSource(improd, cif2));	  
 	  g2d.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
	  g2d.dispose();
	  return Surface;
	}

	private void AffectationVarEv(String Variable,String Resultat)
	{
	  int i , j, k,CaseX,CaseY,Ev;
	  boolean trouve;
	  String nom;	
	  TMChar MChar;
	  TFChar FChar;
	  
	  trouve = false;
      nom=Variable.substring(1,Variable.indexOf("."));
      Variable=Variable.substring(Variable.indexOf(".")+1);
      Variable=Variable.substring(0,Variable.length()-1);
	  i=0; j=0;
	  while ((!trouve) && (i < CurrentMap.TailleX))
	  {
	    j=0;
	    while ((!trouve) && (j < CurrentMap.TailleY))
	    {
	      if (Evenements[i][j].evenement!=null)
	      {
	        if (Evenements[i][j].Ev==-1) CheckPageEvenement(i,j);
	        if (Evenements[i][j].evenement.get(Evenements[i][j].Ev).Name.compareTo(nom)==0) trouve=true;
	      }
	      j++;
	    }
	    i++;
	  }
	  if (trouve)
	  {
	    Ev=Evenements[i-1][j-1].Ev;
		if (Variable.compareTo("Name")==0)
	    	Evenements[i-1][j-1].evenement.get(Evenements[i-1][j-1].Ev).Name=Resultat;
	    else
	    if ((Variable.compareTo("CaseX")==0) || (Variable.compareTo("CaseNBX")==0))
	    {
	      CaseX=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat));
	      if ((Evenements[i-1][j-1].Sprite ==null) && (Evenements[i-1][j-1].evenement.get(Evenements[i-1][j-1].Ev).Chipset.compareTo("")!=0)
	      && (Evenements[i-1][j-1].evenement.get(Evenements[i-1][j-1].Ev).Visible==true))
	      {
              switch(Evenements[i-1][j-1].evenement.get(Ev).TypeAnim)
              {
                case 0 : Evenements[i-1][j-1].Sprite=new TFChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
                										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,
                										   Evenements[i-1][j-1].evenement.get(Ev).X+Evenements[i-1][j-1].evenement.get(Ev).NumAnim*Evenements[i-1][j-1].evenement.get(Ev).W,
                										   Evenements[i-1][j-1].evenement.get(Ev).Y+Evenements[i-1][j-1].evenement.get(Ev).Direction*Evenements[i-1][j-1].evenement.get(Ev).H,
                										   Evenements[i-1][j-1].evenement.get(Ev).W,
                										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,Evenements[i-1][j-1].evenement.get(Ev).Z,Evenements[i-1][j-1].evenement.get(Ev).Transparent);
                		 break;
                case 1 :
                case 2 : Evenements[i-1][j-1].Sprite=new TMChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
                										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,"","","",
                										   Evenements[i-1][j-1].evenement.get(Ev).X,
                										   Evenements[i-1][j-1].evenement.get(Ev).Y,
                										   Evenements[i-1][j-1].evenement.get(Ev).W,
                										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,i-1,j-1,
                										   Evenements[i-1][j-1].evenement.get(Ev).Direction,0,Evenements[i-1][j-1].evenement.get(Ev).Z,
                                                           Sprite.idSprite.idEvenement,0,Evenements[i-1][j-1].evenement.get(Ev).Vitesse,false);
                         break;
                case 3 : Evenements[i-1][j-1].Sprite=new TFChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
                										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,
                										   Evenements[i-1][j-1].evenement.get(Ev).X,
                										   Evenements[i-1][j-1].evenement.get(Ev).Y,
                										   Evenements[i-1][j-1].evenement.get(Ev).W,
                										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,Evenements[i-1][j-1].evenement.get(Ev).Z,Evenements[i-1][j-1].evenement.get(Ev).Transparent);
                         break;
                case 4 : Evenements[i-1][j-1].Sprite=new TMChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
                										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,"","","",
                										   Evenements[i-1][j-1].evenement.get(Ev).X,
                										   Evenements[i-1][j-1].evenement.get(Ev).Y,
                										   Evenements[i-1][j-1].evenement.get(Ev).W,
                										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,i-1,j-1,
                                                           2,0,Evenements[i-1][j-1].evenement.get(Ev).Z,
                                                           Sprite.idSprite.idEvenement,0,Evenements[i-1][j-1].evenement.get(Ev).Vitesse,false);
                         break;
              }
              SpriteE.AddSprite(Evenements[i-1][j-1].Sprite);
              if (Evenements[i-1][j-1].evenement.get(Ev).Transparent==true)
            	  Evenements[i-1][j-1].Sprite.Transparency=0.5f;
	      }
	      if (Evenements[i-1][j-1].Sprite!=null)
	      {
	        if (Evenements[i-1][j-1].Sprite.ID==Sprite.idSprite.idEvenement)
	        {
	          MChar=(TMChar) Evenements[i-1][j-1].Sprite;
	          MChar.versx=CaseX*2;
	          if (Variable.compareTo("CaseX")==0)
	          {
	            AttenteEvenement=1;
	            SpriteEv=Evenements[i-1][j-1].Sprite;
	          }
	        }
	        else
	        {
	          if (Evenements[i-1][j-1].evenement.get(Ev).EvSuisSprite==true)
	          {
	            CaseY=j-1; i=i-1; j=j-1;
	            if (Evenements[CaseX][CaseY].evenement==null)
	            {
	              Evenements[CaseX][CaseY].evenement=Evenements[i][j].evenement; Evenements[i][j].evenement=null;
	              Evenements[CaseX][CaseY].CondDecl=Evenements[i][j].CondDecl; Evenements[i][j].CondDecl=null;
	              Evenements[CaseX][CaseY].CommandeEv=Evenements[i][j].CommandeEv; Evenements[i][j].CommandeEv=null;
	              Evenements[CaseX][CaseY].Ev=Evenements[i][j].Ev; Evenements[i][j].Ev=0;
	              Evenements[CaseX][CaseY].Sprite=Evenements[i][j].Sprite; Evenements[i][j].Sprite =null;
	              Evenements[CaseX][CaseY].Done=Evenements[i][j].Done; Evenements[i][j].Done=null;
	              Evenements[CaseX][CaseY].HasToCheckPage=Evenements[i][j].HasToCheckPage; Evenements[i][j].HasToCheckPage=false;
	              FChar=(TFChar) Evenements[CaseX][CaseY].Sprite;
	              FChar.mmapx=CaseX*2;
	            }
	          }
	          else
	          {
                FChar=(TFChar) Evenements[i-1][j-1].Sprite;
	            FChar.mmapx=CaseX*2;
	          }
	        }
	      }
	      else
	      if (Evenements[i-1][j-1].evenement.get(Ev).EvSuisSprite==true)
	      {
	        CaseY=j-1; i=i-1; j=j-1;
	        if (Evenements[CaseX][CaseY].evenement==null)
	        {
	          Evenements[CaseX][CaseY].evenement=Evenements[i][j].evenement; Evenements[i][j].evenement=null;
	          Evenements[CaseX][CaseY].CondDecl=Evenements[i][j].CondDecl; Evenements[i][j].CondDecl=null;
	          Evenements[CaseX][CaseY].CommandeEv=Evenements[i][j].CommandeEv; Evenements[i][j].CommandeEv=null;
	          Evenements[CaseX][CaseY].Ev=Evenements[i][j].Ev; Evenements[i][j].Ev=0;
	          Evenements[CaseX][CaseY].Sprite=Evenements[i][j].Sprite; Evenements[i][j].Sprite =null;
	          Evenements[CaseX][CaseY].Done=Evenements[i][j].Done; Evenements[i][j].Done=null;
	          Evenements[CaseX][CaseY].HasToCheckPage=Evenements[i][j].HasToCheckPage; Evenements[i][j].HasToCheckPage=false;
	        }
	      }
	    }
	    else
	    if ((Variable.compareTo("CaseY")==0) || (Variable.compareTo("CaseNBY")==0))
	    {
	      CaseY=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat));
	      if ((Evenements[i-1][j-1].Sprite ==null) && (Evenements[i-1][j-1].evenement.get(Evenements[i-1][j-1].Ev).Chipset.compareTo("")!=0)
	      && (Evenements[i-1][j-1].evenement.get(Evenements[i-1][j-1].Ev).Visible==true))
	      {
              switch(Evenements[i-1][j-1].evenement.get(Ev).TypeAnim)
              {
                case 0 : Evenements[i-1][j-1].Sprite=new TFChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
                										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,
                										   Evenements[i-1][j-1].evenement.get(Ev).X+Evenements[i-1][j-1].evenement.get(Ev).NumAnim*Evenements[i-1][j-1].evenement.get(Ev).W,
                										   Evenements[i-1][j-1].evenement.get(Ev).Y+Evenements[i-1][j-1].evenement.get(Ev).Direction*Evenements[i-1][j-1].evenement.get(Ev).H,
                										   Evenements[i-1][j-1].evenement.get(Ev).W,
                										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,Evenements[i-1][j-1].evenement.get(Ev).Z,Evenements[i-1][j-1].evenement.get(Ev).Transparent);
                		 break;
                case 1 :
                case 2 : Evenements[i-1][j-1].Sprite=new TMChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
                										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,"","","",
                										   Evenements[i-1][j-1].evenement.get(Ev).X,
                										   Evenements[i-1][j-1].evenement.get(Ev).Y,
                										   Evenements[i-1][j-1].evenement.get(Ev).W,
                										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,i-1,j-1,
                										   Evenements[i-1][j-1].evenement.get(Ev).Direction,0,Evenements[i-1][j-1].evenement.get(Ev).Z,
                                                           Sprite.idSprite.idEvenement,0,Evenements[i-1][j-1].evenement.get(Ev).Vitesse,false);
                         break;
                case 3 : Evenements[i-1][j-1].Sprite=new TFChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
                										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,
                										   Evenements[i-1][j-1].evenement.get(Ev).X,
                										   Evenements[i-1][j-1].evenement.get(Ev).Y,
                										   Evenements[i-1][j-1].evenement.get(Ev).W,
                										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,Evenements[i-1][j-1].evenement.get(Ev).Z,Evenements[i-1][j-1].evenement.get(Ev).Transparent);
                         break;
                case 4 : Evenements[i-1][j-1].Sprite=new TMChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
                										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,"","","",
                										   Evenements[i-1][j-1].evenement.get(Ev).X,
                										   Evenements[i-1][j-1].evenement.get(Ev).Y,
                										   Evenements[i-1][j-1].evenement.get(Ev).W,
                										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,i-1,j-1,
                                                           2,0,Evenements[i-1][j-1].evenement.get(Ev).Z,
                                                           Sprite.idSprite.idEvenement,0,Evenements[i-1][j-1].evenement.get(Ev).Vitesse,false);
                         break;
              }
              SpriteE.AddSprite(Evenements[i-1][j-1].Sprite);
              if (Evenements[i-1][j-1].evenement.get(Ev).Transparent==true)
            	  Evenements[i-1][j-1].Sprite.Transparency=0.5f;
		  }
	      if (Evenements[i-1][j-1].Sprite!=null)
	      {
	        if (Evenements[i-1][j-1].Sprite.ID==Sprite.idSprite.idEvenement)
	        {
	          MChar=(TMChar) Evenements[i-1][j-1].Sprite;
	          MChar.versy=CaseY*2;
	          if (Variable.compareTo("CaseY")==0)
	          {
	            AttenteEvenement =1;
	            SpriteEv=Evenements[i-1][j-1].Sprite;
	          }
	        }
	        else
	        {
	          if (Evenements[i-1][j-1].evenement.get(Ev).EvSuisSprite==true)
	          {
	            CaseX=i-1; i=i-1; j=j-1;
	            if (Evenements[CaseX][CaseY].evenement==null)
	            {
	              Evenements[CaseX][CaseY].evenement=Evenements[i][j].evenement; Evenements[i][j].evenement=null;
	              Evenements[CaseX][CaseY].CondDecl=Evenements[i][j].CondDecl; Evenements[i][j].CondDecl=null;
	              Evenements[CaseX][CaseY].CommandeEv=Evenements[i][j].CommandeEv; Evenements[i][j].CommandeEv=null;
	              Evenements[CaseX][CaseY].Ev=Evenements[i][j].Ev; Evenements[i][j].Ev=0;
	              Evenements[CaseX][CaseY].Sprite=Evenements[i][j].Sprite; Evenements[i][j].Sprite =null;
	              Evenements[CaseX][CaseY].Done=Evenements[i][j].Done; Evenements[i][j].Done=null;
	              Evenements[CaseX][CaseY].HasToCheckPage=Evenements[i][j].HasToCheckPage; Evenements[i][j].HasToCheckPage=false;
	              FChar=(TFChar) Evenements[CaseX][CaseY].Sprite;
	              FChar.mmapy=CaseY*2;
	            }
	          }
	          else
	          {
        	    FChar=(TFChar) Evenements[i-1][j-1].Sprite;
	            FChar.mmapy=CaseY*2;
	          }
	        }
	      }
	      else
	      {
	        if (Evenements[i-1][j-1].evenement.get(Ev).EvSuisSprite==true)
	        {
	          CaseX=i-1; i=i-1; j=j-1;
	          if (Evenements[CaseX][CaseY].evenement==null)
	          {
	            Evenements[CaseX][CaseY].evenement=Evenements[i][j].evenement; Evenements[i][j].evenement=null;
	            Evenements[CaseX][CaseY].CondDecl=Evenements[i][j].CondDecl; Evenements[i][j].CondDecl=null;
	            Evenements[CaseX][CaseY].CommandeEv=Evenements[i][j].CommandeEv; Evenements[i][j].CommandeEv=null;
	            Evenements[CaseX][CaseY].Ev=Evenements[i][j].Ev; Evenements[i][j].Ev=0;
	            Evenements[CaseX][CaseY].Sprite=Evenements[i][j].Sprite; Evenements[i][j].Sprite =null;
	            Evenements[CaseX][CaseY].Done=Evenements[i][j].Done; Evenements[i][j].Done=null;
	            Evenements[CaseX][CaseY].HasToCheckPage=Evenements[i][j].HasToCheckPage; Evenements[i][j].HasToCheckPage=false;
	          }
	        }
	      }
	    }
	    else
	    if (Variable.compareTo("Direction")==0)
	    {
	      Evenements[i-1][j-1].evenement.get(Ev).Direction=(short) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat));
	      if (Evenements[i-1][j-1].Sprite!=null)
	      {
	        if (Evenements[i-1][j-1].Sprite.ID==Sprite.idSprite.idEvenement)
	        {
	        	MChar=(TMChar) Evenements[i-1][j-1].Sprite;
	        	MChar.Direction=Evenements[i-1][j-1].evenement.get(Ev).Direction;
	        }
	      }
	    }
	    else
	    if (Variable.compareTo("Chipset")==0)
	    {
	      Evenements[i-1][j-1].evenement.get(Ev).Chipset=Formule.ReplaceStringVariable(Resultat);
	      if (Evenements[i-1][j-1].evenement.get(Ev).Visible==true)
	      {
	        if (Evenements[i-1][j-1].Sprite.isDead==false)
	          Evenements[i-1][j-1].Sprite.Kill();
	        Evenements[i-1][j-1].Sprite=null;
            switch(Evenements[i-1][j-1].evenement.get(Ev).TypeAnim)
            {
              case 0 : Evenements[i-1][j-1].Sprite=new TFChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
              										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,
              										   Evenements[i-1][j-1].evenement.get(Ev).X+Evenements[i-1][j-1].evenement.get(Ev).NumAnim*Evenements[i-1][j-1].evenement.get(Ev).W,
              										   Evenements[i-1][j-1].evenement.get(Ev).Y+Evenements[i-1][j-1].evenement.get(Ev).Direction*Evenements[i-1][j-1].evenement.get(Ev).H,
              										   Evenements[i-1][j-1].evenement.get(Ev).W,
              										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,Evenements[i-1][j-1].evenement.get(Ev).Z,Evenements[i-1][j-1].evenement.get(Ev).Transparent);
              		 break;
              case 1 :
              case 2 : Evenements[i-1][j-1].Sprite=new TMChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
              										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,"","","",
              										   Evenements[i-1][j-1].evenement.get(Ev).X,
              										   Evenements[i-1][j-1].evenement.get(Ev).Y,
              										   Evenements[i-1][j-1].evenement.get(Ev).W,
              										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,i-1,j-1,
              										   Evenements[i-1][j-1].evenement.get(Ev).Direction,0,Evenements[i-1][j-1].evenement.get(Ev).Z,
                                                         Sprite.idSprite.idEvenement,0,Evenements[i-1][j-1].evenement.get(Ev).Vitesse,false);
                       break;
              case 3 : Evenements[i-1][j-1].Sprite=new TFChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
              										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,
              										   Evenements[i-1][j-1].evenement.get(Ev).X,
              										   Evenements[i-1][j-1].evenement.get(Ev).Y,
              										   Evenements[i-1][j-1].evenement.get(Ev).W,
              										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,Evenements[i-1][j-1].evenement.get(Ev).Z,Evenements[i-1][j-1].evenement.get(Ev).Transparent);
                       break;
              case 4 : Evenements[i-1][j-1].Sprite=new TMChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
              										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,"","","",
              										   Evenements[i-1][j-1].evenement.get(Ev).X,
              										   Evenements[i-1][j-1].evenement.get(Ev).Y,
              										   Evenements[i-1][j-1].evenement.get(Ev).W,
              										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,i-1,j-1,
                                                         2,0,Evenements[i-1][j-1].evenement.get(Ev).Z,
                                                         Sprite.idSprite.idEvenement,0,Evenements[i-1][j-1].evenement.get(Ev).Vitesse,false);
                       break;
            }
            SpriteE.AddSprite(Evenements[i-1][j-1].Sprite);
            if (Evenements[i-1][j-1].evenement.get(Ev).Transparent==true)
          	  Evenements[i-1][j-1].Sprite.Transparency=0.5f;
	      }
	    }
	    else
	    if (Variable.compareTo("Visible")==0)
	    {
	      Evenements[i-1][j-1].evenement.get(Ev).Visible=((int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)) > 0) ? true : false;
	      if (Evenements[i-1][j-1].evenement.get(Ev).Visible==true)
	      {
	        for (k=0;k<Evenements[i-1][j-1].evenement.size();k++)
	        {
	          if (Evenements[i-1][j-1].evenement.get(k).Name.compareTo(Evenements[i-1][j-1].evenement.get(Ev).Name)==0)
	            Evenements[i-1][j-1].evenement.get(k).Visible=true;
	        }
	        if ((Evenements[i-1][j-1].Sprite==null) && (Evenements[i-1][j-1].evenement.get(Ev).Chipset.compareTo("")!=0))
	        {
	            switch(Evenements[i-1][j-1].evenement.get(Ev).TypeAnim)
	            {
	              case 0 : Evenements[i-1][j-1].Sprite=new TFChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
	              										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,
	              										   Evenements[i-1][j-1].evenement.get(Ev).X+Evenements[i-1][j-1].evenement.get(Ev).NumAnim*Evenements[i-1][j-1].evenement.get(Ev).W,
	              										   Evenements[i-1][j-1].evenement.get(Ev).Y+Evenements[i-1][j-1].evenement.get(Ev).Direction*Evenements[i-1][j-1].evenement.get(Ev).H,
	              										   Evenements[i-1][j-1].evenement.get(Ev).W,
	              										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,Evenements[i-1][j-1].evenement.get(Ev).Z,Evenements[i-1][j-1].evenement.get(Ev).Transparent);
	              		 break;
	              case 1 :
	              case 2 : Evenements[i-1][j-1].Sprite=new TMChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
	              										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,"","","",
	              										   Evenements[i-1][j-1].evenement.get(Ev).X,
	              										   Evenements[i-1][j-1].evenement.get(Ev).Y,
	              										   Evenements[i-1][j-1].evenement.get(Ev).W,
	              										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,i-1,j-1,
	              										   Evenements[i-1][j-1].evenement.get(Ev).Direction,0,Evenements[i-1][j-1].evenement.get(Ev).Z,
	                                                         Sprite.idSprite.idEvenement,0,Evenements[i-1][j-1].evenement.get(Ev).Vitesse,false);
	                       break;
	              case 3 : Evenements[i-1][j-1].Sprite=new TFChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
	              										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,
	              										   Evenements[i-1][j-1].evenement.get(Ev).X,
	              										   Evenements[i-1][j-1].evenement.get(Ev).Y,
	              										   Evenements[i-1][j-1].evenement.get(Ev).W,
	              										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,Evenements[i-1][j-1].evenement.get(Ev).Z,Evenements[i-1][j-1].evenement.get(Ev).Transparent);
	                       break;
	              case 4 : Evenements[i-1][j-1].Sprite=new TMChar(this,Evenements[i-1][j-1].evenement.get(Ev).Name,
	              										   Evenements[i-1][j-1].evenement.get(Ev).Chipset,"","","",
	              										   Evenements[i-1][j-1].evenement.get(Ev).X,
	              										   Evenements[i-1][j-1].evenement.get(Ev).Y,
	              										   Evenements[i-1][j-1].evenement.get(Ev).W,
	              										   Evenements[i-1][j-1].evenement.get(Ev).H,(i-1)*2,(j-1)*2,i-1,j-1,
	                                                         2,0,Evenements[i-1][j-1].evenement.get(Ev).Z,
	                                                         Sprite.idSprite.idEvenement,0,Evenements[i-1][j-1].evenement.get(Ev).Vitesse,false);
	                       break;
	            }
	            SpriteE.AddSprite(Evenements[i-1][j-1].Sprite);
	            if (Evenements[i-1][j-1].evenement.get(Ev).Transparent==true)
	          	  Evenements[i-1][j-1].Sprite.Transparency=0.5f;
	        }
	      }
	      else
	      if (Evenements[i-1][j-1].Sprite!=null)
	      {
	        if (Evenements[i-1][j-1].Sprite.isDead==false)
	          Evenements[i-1][j-1].Sprite.Kill();
	        Evenements[i-1][j-1].Sprite=null;
	      }
	    }
	    else
	    if (Variable.compareTo("AnimAttaque")==0)
	    {
	      if (Evenements[i-1][j-1].Sprite!=null)
	      {
	        if (Evenements[i-1][j-1].Sprite.ID==Sprite.idSprite.idEvenement)
	        {
	            MChar=(TMChar) Evenements[i-1][j-1].Sprite;
	        	MChar.Action=1;
	        	if (Bloque)
	        	{
	        		AttenteEvenement =2;
	        		SpriteEv=Evenements[i-1][j-1].Sprite;
	        	}
	        }
	      }
	    }
	    else
	    if (Variable.compareTo("AnimDefense")==0)
	    {
	      if (Evenements[i-1][j-1].Sprite!=null)
	      {
	        if (Evenements[i-1][j-1].Sprite.ID==Sprite.idSprite.idEvenement)
	        {
	            MChar=(TMChar) Evenements[i-1][j-1].Sprite;
	        	MChar.Action=5;
	        	if (Bloque)
	        	{
	        		AttenteEvenement =2;
	        		SpriteEv=Evenements[i-1][j-1].Sprite;
	        	}
	        }
	      }
	    }
	    else
	    if (Variable.compareTo("AnimMagie")==0)
	    {
	      if (Evenements[i-1][j-1].Sprite!=null)
	      {
	        if (Evenements[i-1][j-1].Sprite.ID==Sprite.idSprite.idEvenement)
	        {
	            MChar=(TMChar) Evenements[i-1][j-1].Sprite;
	        	MChar.Action=2;
	        	MChar.AttenteTotal=42;
	        	if (Bloque)
	        	{
	        		AttenteEvenement =2;
	        		SpriteEv=Evenements[i-1][j-1].Sprite;
	        	}
	        }
	      }
	    }
	    else if (Variable.compareTo("X")==0) Evenements[i-1][j-1].evenement.get(Ev).X=(int) Calcule.Calcule(Resultat);
	    else if (Variable.compareTo("Y")==0) Evenements[i-1][j-1].evenement.get(Ev).Y=(int) Calcule.Calcule(Resultat);
	    else if (Variable.compareTo("W")==0) Evenements[i-1][j-1].evenement.get(Ev).W=(short) Calcule.Calcule(Resultat);
	    else if (Variable.compareTo("H")==0) Evenements[i-1][j-1].evenement.get(Ev).H=(short) Calcule.Calcule(Resultat);
	    else if (Variable.compareTo("TypeAnim")==0) Evenements[i-1][j-1].evenement.get(Ev).TypeAnim=(short) Calcule.Calcule(Resultat);
	    else if (Variable.compareTo("NumAnim")==0) Evenements[i-1][j-1].evenement.get(Ev).NumAnim=(short) Calcule.Calcule(Resultat);
	    else
	    if (Variable.compareTo("Vitesse")==0)
	    {
	      for (k=0;k<Evenements[i-1][j-1].evenement.size();k++)
	      {
	        if (Evenements[i-1][j-1].evenement.get(k).Name.compareTo(Evenements[i-1][j-1].evenement.get(Ev).Name)==0)
	          Evenements[i-1][j-1].evenement.get(k).Vitesse=(short) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat));
	      }
	      if (Evenements[i-1][j-1].Sprite!=null)
	      {
	        if (Evenements[i-1][j-1].Sprite.ID==Sprite.idSprite.idEvenement)
	        {
	          MChar=(TMChar) Evenements[i-1][j-1].Sprite; 	
	          MChar.vitesse=Evenements[i-1][j-1].evenement.get(Ev).Vitesse;
	          MChar.vit=Math.abs(MChar.vitesse);
	        }
	      }
	    }
	    else
	    if (Variable.compareTo("Bloquant")==0)
	    {
	      for (k=0;k<Evenements[i-1][j-1].evenement.size();k++)
	      {
	        if (Evenements[i-1][j-1].evenement.get(k).Name.compareTo(Evenements[i-1][j-1].evenement.get(Ev).Name)==0)
	        {
	          Evenements[i-1][j-1].evenement.get(k).Bloquant=((int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)) > 0) ? true : false;
	        }
	      }
	    }
	    else
	    if (Variable.compareTo("Transparent")==0)
	    {
	      for (k=0;k<Evenements[i-1][j-1].evenement.size();k++)
	      {
	        if (Evenements[i-1][j-1].evenement.get(k).Name.compareTo(Evenements[i-1][j-1].evenement.get(Ev).Name)==0)
	          Evenements[i-1][j-1].evenement.get(k).Transparent=((int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)) > 0) ? true : false;
            if (Evenements[i-1][j-1].evenement.get(Ev).Transparent==true)
	          	  Evenements[i-1][j-1].Sprite.Transparency=0.5f;
	        else
	          	  Evenements[i-1][j-1].Sprite.Transparency=1;
	      }
	    }
	  }
	}

	private void AffectationVarPlayer(String Variable,String Resultat)
	{
	  int maxx,maxy,numvari,gauche,droit;
	  int i ,j,mapx , mapy;
	  String temp;

	 if (Variable.indexOf("Variable[")>=0)
	 {
	   Resultat=Formule.ReplaceStringVariable(Resultat);
       temp=Variable.substring(Variable.indexOf("[")+1);
       temp=temp.substring(0,temp.length()-1);
       temp=Formule.ReplaceStringVariable(temp);
       if (temp.indexOf("..")>=0)
       {
	     gauche=Integer.parseInt(Formule.ReplaceStringVariable(temp.substring(0,temp.indexOf("..")-1)));
	     droit=Integer.parseInt(Formule.ReplaceStringVariable(temp.substring(temp.indexOf("..")+2)));
	     for (i=gauche;i<=droit;i++)
	     {
	    	 numvari=(int) Calcule.Calcule(Resultat);
	    	 if (Calcule.Err>0)
	    		 PlayerInfo.Variable.setProperty(Integer.toString(i), Resultat);
	    	 else
	    		 PlayerInfo.Variable.setProperty(Integer.toString(i), Integer.toString(numvari));
	     }
       }
	   else
	   {
		     numvari=(int) Calcule.Calcule(temp);
		     if (Calcule.Err==0)
		    	 temp=Integer.toString(numvari);	    	 
		     numvari=(int) Calcule.Calcule(Resultat);
	    	 if (Calcule.Err>0)
	    		 PlayerInfo.Variable.setProperty(temp, Resultat);
	    	 else
	    		 PlayerInfo.Variable.setProperty(temp, Integer.toString(numvari));		   
	   }
	 }
	 else
	 {
	   if (Variable.compareTo("%Name%")==0)
	   {
	     PlayerInfo.Name=Resultat;
	   }
	   else
	   if (Variable.compareTo("%Effect%")==0)
	   {
	      mapx=Effect;
	      Effect=(int) Calcule.Calcule(Resultat);
	      if (mapx!=Effect)
	      {
	        // les effets Aucun, Pluie et Neige utilise les memes couleurs, on ne redessine pas.
	        if ((Effect>=0) && (Effect<=3))
	          PEffect=Effect;
	        if ((Effect==4) && (mapx!=4)) PlaySound("/Sound/pluie.wav","pluie",true);
	        else if ((mapx==4) && (Effect!=4)) AudioPlayer.stop("pluie");
	        if (PEffect==Effect)
	        {
	          for (i=0;i<SpriteE.Sprites.size();i++)
	            if ((SpriteE.Sprites.get(i).ID==Sprite.idSprite.idMChar) || (SpriteE.Sprites.get(i).ID==Sprite.idSprite.idFChar) || (SpriteE.Sprites.get(i).ID==Sprite.idSprite.idEvenement)
	            || (SpriteE.Sprites.get(i).ID==Sprite.idSprite.idAnimation) || (SpriteE.Sprites.get(i).ID==Sprite.idSprite.idMonstre))
	              SpriteE.Sprites.get(i).Kill();
	          maxx=CurrentMap.TailleX;
	          maxy=CurrentMap.TailleY;
	          for (i=((PlayerInfo.pmapx-PlayerInfo.CentreX+NDetect-(ScX / 8)) / 2);i<=((PlayerInfo.pmapx-PlayerInfo.CentreX+Detect-(ScX / 8)) / 2);i++)
	            for (j=((PlayerInfo.pmapy-PlayerInfo.CentreY+NDetect-(ScY / 8)) / 2);j<=((PlayerInfo.pmapy-PlayerInfo.CentreY+Detect-(ScY / 8)) / 2);j++)
	              if ((i>=0) && (i<maxx) && (j>=0) && (j<maxy))
	                if (Evenements[i][j].evenement!=null)
	                {
	                  if (Evenements[i][j].Sprite!=null) Evenements[i][j].Sprite=null;
	                }
		  		Tile=null;
				String _Image=general.getName()+"/"+CurrentMap.Chipset.replace("Chipset\\", "Chipset/");
				if (new File(_Image).exists())
				{
				  ImageProducer improd;
				  ImageFilter cif2;
				  Tile=LoadImage(_Image,true);
				  Background= new Image[Tile.getWidth(null)/16][Tile.getHeight(null)/16];
				  improd=Tile.getSource();
				  for (i=0;i<Background.length;i++)
				  {
					for(j=0;j<Background[i].length;j++)
					{
					  	cif2 =new CropImageFilter(i*16, j*16,16,16);
					   	Background[i][j] = createImage(new FilteredImageSource(improd, cif2));
					   	Background[i][j]=Background[i][j].getScaledInstance((int)(16*Zoom), (int)(16*Zoom), Image.SCALE_FAST); 
					}
				  }
				}
	          LoadSurface();
	          if (player!=null)
	          {
	            player.Kill();
	            player=new Player(this,Zoom);
	            SpriteE.AddSprite(player);
	          }
	        }
	      }
	   }
	   else
	   if (Variable.compareTo("%Visible%")==0)
	   {
	     if ((Resultat.compareTo("0")==0) && (player!=null))
	     {
	       player.Kill();
	       player=null;
	     }
	     else
	     if ((Resultat.compareTo("0")==0) && (player==null) && (PlayerMChar!=null))
	     {
	       PlayerMChar.Visible=false;
	     }
	     else
	     if ((Resultat.compareTo("1")==0) && (player==null) && (PlayerMChar==null))
	     {
	       player=new Player(this,Zoom);
	       SpriteE.AddSprite(player);
	     }
	     else
	     if ((Resultat.compareTo("1")==0) && (PlayerMChar!=null))
	     {
	       PlayerMChar.Visible=true;
	     }
	   }
	   else
	   if (Variable.compareTo("%Bloque%")==0)
	   {
		   Bloque=(Resultat.compareTo("1")==0);
	   }
	   else
	   if (Variable.compareTo("%CaseX%")==0)
	   {
	     if (PlayerMChar==null)
	     {
	       if (player!=null)
	       {
	         player.Kill();
	         player=null;
	       }
	       PlayerMChar=new TMChar(this,PlayerInfo.Name,PlayerInfo.Chipset,PlayerInfo.SoundAttaque,PlayerInfo.SoundWound,PlayerInfo.SoundConcentration,0,0,24,32,PlayerInfo.pmapx,PlayerInfo.pmapy,0,0,PlayerDirection,(Dead==true) ? 1 : 0,0,Sprite.idSprite.idMChar,3,1,false);
	       PlayerMChar.mrealx=prealx; PlayerMChar.mrealy=prealy;
	       SpriteE.AddSprite(PlayerMChar);
	     }
	     PlayerMChar.versx=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat))*2;
	     AttenteEvenement=1;
	     SpriteEv=PlayerMChar;
	   }
	   else
	   if (Variable.compareTo("%CaseY%")==0)
	   {
	     if (PlayerMChar==null)
	     {
	       if (player!=null)
	       {
	         player.Kill();
	         player=null;
	       }
	       PlayerMChar=new TMChar(this,PlayerInfo.Name,PlayerInfo.Chipset,PlayerInfo.SoundAttaque,PlayerInfo.SoundWound,PlayerInfo.SoundConcentration,0,0,24,32,PlayerInfo.pmapx,PlayerInfo.pmapy,0,0,PlayerDirection,(Dead==true) ? 1 : 0,0,Sprite.idSprite.idMChar,3,1,false);
	       PlayerMChar.mrealx=prealx; PlayerMChar.mrealy=prealy;
	       SpriteE.AddSprite(PlayerMChar);
	     }
	     PlayerMChar.versy=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat))*2;
	     AttenteEvenement=1;
	     SpriteEv=PlayerMChar;
	   }
	   else
	   if (Variable.compareTo("%Direction%")==0)
	   {
	     PlayerDirection = Integer.parseInt(Resultat);
	     if (PlayerMChar!=null)
	       PlayerMChar.Direction=PlayerDirection;
	   }
	   else
	   if (Variable.compareTo("%Position%")==0)
	   {
	     mapx=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat));
	     if (PlayerInfo.Position!=mapx)
	     {
	       PlayerInfo.Position=(short) mapx;
	       if (player!=null)
	       {
	         player.z=player.y+(50*PlayerInfo.Position);
	         SpriteE.SortSprites();
	       }
	     }
	   }
	   else
	   if (Variable.compareTo("%CentreX%")==0)
	   {
	     mapx=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat))*2;
	     if (PlayerInfo.CentreX!=mapx)
	     {
	       PlayerInfo.CentreX=(short) mapx;
	       if (player!=null)
	       {
	         player.x = (int) (8 * Zoom * (CentreX+PlayerInfo.CentreX));
	         player.y = (int) (8 * Zoom * (CentreY+PlayerInfo.CentreY));
	       }
	     }
	   }
	   else
	   if (Variable.compareTo("%CentreY%")==0)
	   {
	     mapy=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat))*2;
	     if (PlayerInfo.CentreY!=mapy)
	     {
	       PlayerInfo.CentreY=(short) mapy;
	       if (player!=null)
	       {
	         player.x = (int) (8 * Zoom * (CentreX+PlayerInfo.CentreX));
	         player.y = (int) (8 * Zoom * (CentreY+PlayerInfo.CentreY));
	       }
	     }
	   }
	   else		 
	  // if (variable='%Classe%') inaffectable pour le momentl
	   if (Variable.compareTo("%Vie%")==0) { PlayerInfo.Vie=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); if (PlayerInfo.Vie > PlayerInfo.VieMax) PlayerInfo.Vie=PlayerInfo.VieMax; if (PlayerInfo.Vie<0) { PlayerInfo.Vie=0; Dead=true; KillPlayer(); } }  else
	   if (Variable.compareTo("%VieMax%")==0) { PlayerInfo.VieMax=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%CurrentMag%")==0) { PlayerInfo.CurrentMag=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%MagMax%")==0) { PlayerInfo.MagMax=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%Gold%")==0) { PlayerInfo.Gold=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%Lvl%")==0) { PlayerInfo.Lvl=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%LvlPoint%")==0) { PlayerInfo.LvlPoint=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%CurrentXP%")==0) { PlayerInfo.CurrentXP=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); if (PlayerInfo.CurrentXP > PlayerInfo.NextXP) LevelUp(); if (PlayerInfo.CurrentXP<0) PlayerInfo.CurrentXP=0; }  else
	   if (Variable.compareTo("%Arme%")==0) { PlayerInfo.Arme=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%Bouclier%")==0) { PlayerInfo.Bouclier=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%Casque%")==0) { PlayerInfo.Casque=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%Armure%")==0) { PlayerInfo.Armure=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%Timer%")==0) { Timer=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%Timer2%")==0) { Timer2=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%Timer3%")==0) { Timer3=(int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)); }  else
	   if (Variable.compareTo("%BloqueChangeSkin%")==0) { PlayerInfo.BloqueChangeSkin=((int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)) > 0) ? true : false; }  else
	   if (Variable.compareTo("%BloqueAttaque%")==0) { PlayerInfo.BloqueAttaque=((int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)) > 0) ? true : false; }  else
	   if (Variable.compareTo("%BloqueDefense%")==0) { PlayerInfo.BloqueDefense=((int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)) > 0) ? true : false; }  else
	   if (Variable.compareTo("%BloqueMagie%")==0) { PlayerInfo.BloqueMagie=((int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)) > 0) ? true : false; }
	   else
	   {
		 for(i=0;i<general.getStatsBase().size();i++)
		 {
			 if (Variable.compareTo("%"+general.getStatsBase().get(i)+"%")==0) { PlayerInfo.Stats.setProperty(general.getStatsBase().get(i), Integer.toString((int) Calcule.Calcule(Formule.ReplaceStringVariable(Resultat)))); }
		 }
	   }
	   RecalculCarac();
	 }
	 if (fiolevie!=null)
		 fiolevie.Redraw();
	 if (fiolemana!=null)
		 fiolemana.Redraw();
	 if (barrexp!=null)
		 barrexp.Redraw();
	}

	private void RecalculCarac()
	{
		if (PlayerInfo.Classe!=null)
		{
			  PlayerInfo.VieMax=(int) Calcule.Calcule(Formule.ReplaceStatVariable(PlayerInfo.Classe.FormuleVieMax,null));
			  PlayerInfo.MagMax=(int) Calcule.Calcule(Formule.ReplaceStatVariable(PlayerInfo.Classe.FormuleMagMax,null));
			  if (PlayerInfo.Vie > PlayerInfo.VieMax) PlayerInfo.Vie=PlayerInfo.VieMax;
			  if (PlayerInfo.CurrentMag > PlayerInfo.MagMax) PlayerInfo.CurrentMag=PlayerInfo.MagMax;
		}
	}
	
	private void DoEvenements(int PosX,int PosY,int Ev,ArrayList<String> Liste,int CurrentLigne)
	{
		String variable,resultat,ligne;
		boolean ok,Done;
		ArrayList<String> StringList;
		int i ,j, position,numvari,gauche,droit;
		TMChar MChar;
		Done=false;
	    StringList=Liste;
	    if (CurrentLigne < StringList.size())
	    {
	      if (CurrentLigne==0)
	    	  PSave=PlayerInfo.clone();
	      ligne=StringList.get(CurrentLigne).trim();
	      ListeEv=Liste;
	      EventAttX=PosX;
	      EventAttY=PosY;
	      EventEv=Ev;
	      if ((PosX>=0)&&(PosY>=0))
	      {
		      if ((Evenements[PosX][PosY].Sprite!=null) && (CurrentLigne==0))
		      {
		        if (Evenements[PosX][PosY].Sprite.ID==Sprite.idSprite.idEvenement)
		        {
		          MChar=(TMChar) Evenements[PosX][PosY].Sprite;
		          if ((MChar.mmapx!=MChar.versx) || (MChar.mmapy!=MChar.versy))
		          {
		            MChar.evversx=MChar.versx;
		            MChar.evversy=MChar.versy;
		            MChar.versx=MChar.mmapx;
		            MChar.versy=MChar.mmapy;
		          }
		        }
		      }
	      }
	      CurrentLigneEv=CurrentLigne;
	      IsInEvent=true;
	      if (ligne.compareTo("")==0)
	      {
		        DoEvenements(PosX,PosY,Ev,StringList,CurrentLigne+1);
		        return;
	      }
	      else
	      if (ligne.substring(0,2).compareTo("//")==0)
	      {
	        DoEvenements(PosX,PosY,Ev,StringList,CurrentLigne+1);
	        return;
	      }
	      else
	      if (ligne.indexOf("InputString(")>=0)
	      {
		      	// TODO @BETA2: Ajouter un log de inputstring?
		    	variable=ligne.substring(ligne.indexOf("InputString("));
		    	variable=variable.substring(0,variable.indexOf("')")+2);
		        if (InputString.compareTo("")!=0)
		        {
		        	ligne=ligne.replace(variable, InputString);
		        	InputString="";
		        }
		        else
		        {
		          AttenteEvenement=4;
		          variable=variable.substring(variable.indexOf("InputString(")+13);
		          variable=variable.substring(0,variable.indexOf("')"));
		          variable=Formule.ReplaceStringVariable(variable);
		          IsInInputString=true;
		          SpriteE.AddSprite(new EvInputString(this,variable));
		          return;
		        }
		      }
		      // TODO @BETA2 Ajouter log
	      	  if (ligne.length()>7)
	      		  variable=ligne.substring(0,8);
	      	  else
	      		  variable=ligne;
		      if (EvCommande.indexOf(variable) >= 0)
		      {
		        // commande
		        if (variable.compareTo("Message(")==0)
		        {
		          variable=ligne;
		          variable=variable.substring(variable.indexOf("(")+1);
		          variable=variable.substring(0,variable.length()-2);
		          variable=Formule.ReplaceStringVariable(variable);
		          SpriteEv=new EvMessage(this,variable);
		          SpriteE.AddSprite(SpriteEv);
		          AttenteEvenement=2;
		          Done=true;
		        }
			    else
			    if ((variable.compareTo("Conditio")==0) && (!Done))
			    {
		          variable=ligne;
		          variable=variable.substring(variable.indexOf("'")+1);
		          variable=variable.substring(0,variable.length()-2);
		          if (EvCondition.indexOf(variable)>=0)
		            resultat=CondDeclenche;
		          else
		          {
		            ok=VerifieCondition(PosX,PosY,variable);
		            if (ok) resultat=variable;
		            else resultat="";
		          }
		          if (variable.compareTo(resultat)!=0)
		          {
		            CurrentLigne++;
		            while ((CurrentLigne < StringList.size()) && (variable.compareTo("Conditio")!=0))
		            {
		            	variable=StringList.get(CurrentLigne).substring(0,8);
			            CurrentLigne++;
		            }
		            if (variable.compareTo("Conditio")==0) CurrentLigne=CurrentLigne-2;
		          }
		          Done=true;
			    }
			    else
			    if ((variable.compareTo("AddObjec")==0) && (!Done))
			    {
		            variable=ligne;
			    	variable=variable.substring(variable.indexOf("(")+1);
			    	variable=variable.substring(0,variable.length()-1);
			    	if (variable.indexOf(",")>=0)
			    	{
			    		resultat=variable.substring(variable.indexOf(",")+1);
			    		variable=variable.substring(0,variable.indexOf(","));
			    	}
			    	else
			    		resultat="1";
				   ok=false;
				   i=0;
				   while ((i<general.getObjets().size())&&(ok==false))
				   {
					   if (general.getObjetByIndex(i).Name.compareTo(variable)==0)
						   ok=true;
					   i++;
				   }
				   if (ok)
				   {
					   numvari=i;
					   droit=(int) Calcule.Calcule(Formule.ReplaceStringVariable(resultat));
					    i=0; ok=false;
					    while ((i<100)&&(ok==false))
					    {
						   if (PlayerInfo.Inventaire[i][0]==numvari)
						   {
							   ok=true;
							   PlayerInfo.Inventaire[i][1]+=droit;
							   if (PlayerInfo.Inventaire[i][1]>100)
								   PlayerInfo.Inventaire[i][1]=100;
						   }
						   i++;
					   }
					   // Dans le cas ou on a pas déjà l'objet dans l'inventaire
					   if (ok==false)
					   {
						    i=0; ok=false;
						    while ((i<100)&&(ok==false))
						    {
							   if (PlayerInfo.Inventaire[i][0]==0)
							   {
								   ok=true;
								   PlayerInfo.Inventaire[i][0]=(short) numvari;
								   PlayerInfo.Inventaire[i][1]=(short) droit;
								   if (PlayerInfo.Inventaire[i][1]>100)
									   PlayerInfo.Inventaire[i][1]=100;
							   }
							   i++;
						   }					   
					   }
					   if (ok==false)
					   {
						   if (general.getObjetByIndex(numvari-1).ObjType==6)
						   {
							    while ((i<100)&&(ok==false))
							    {
								    i=0; ok=false;
								   if (PlayerInfo.Inventaire[i][0]!=6)
								   {
									   ok=true;
									   PlayerInfo.Inventaire[i][0]=(short) numvari;
									   PlayerInfo.Inventaire[i][1]=(short) droit;
									   if (PlayerInfo.Inventaire[i][1]>100)
										   PlayerInfo.Inventaire[i][1]=100;
								   }
								   i++;
							   }					   							   
						   }
						   else
		   				        SpriteE.AddSprite(new Degat(this, "Inventaire plein...", player, Color.WHITE));							   
					   }
				   }
		          Done=true;
			    }
			    else
			    if ((variable.compareTo("DelObjec")==0) && (!Done))
			    {
		            variable=ligne;
			    	variable=variable.substring(variable.indexOf("(")+1);
			    	variable=variable.substring(0,variable.length()-1);
			    	if (variable.indexOf(",")>=0)
			    	{
			    		resultat=variable.substring(variable.indexOf(",")+1);
			    		variable=variable.substring(0,variable.indexOf(","));
			    	}
			    	else
			    		resultat="1";
				   ok=false;
				   i=0;
				   while ((i<general.getObjets().size())&&(ok==false))
				   {
					   if (general.getObjetByIndex(i).Name.compareTo(variable)==0)
						   ok=true;
					   i++;
				   }
				   if (ok)
				   {
					   numvari=i;
					   droit=(int) Calcule.Calcule(Formule.ReplaceStringVariable(resultat));
					    i=0; ok=false;
					    while ((i<100)&&(ok==false))
					    {
						   if (PlayerInfo.Inventaire[i][0]==numvari)
						   {
							   ok=true;
							   PlayerInfo.Inventaire[i][1]-=droit;
							   if (PlayerInfo.Inventaire[i][1]<0)
							   {
								   PlayerInfo.Inventaire[i][0]=0;
								   PlayerInfo.Inventaire[i][1]=0;
									for (j=0;j<10;j++)
										if (PlayerInfo.Raccourcis[j]==i+1)
											PlayerInfo.Raccourcis[j]=0;
									if (barreicone!=null)
										barreicone.Redraw();
							   }
						   }
						   i++;
					   }
				   }
		          Done=true;
			    }
		        else
			    if ((variable.compareTo("AddMagie")==0) && (!Done))
			    {		    	
		            variable=ligne;
			    	variable=variable.substring(variable.indexOf("(")+1);
			    	variable=variable.substring(0,variable.length()-1);
				    ok=false;
				    i=0;
				    while ((i < general.getMagies().size()) && (ok==false))
				    {
				    	if (general.getMagieByIndex(i).Name.compareTo(variable)==0)
				    		ok=true;
				    	i++;			    	
				    }
				    if (ok)
				    {
					      numvari=i; i=0; ok=false;
					      while ((i<100) && (ok==false))
					      {
					        if (PlayerInfo.OwnSpell[i]==numvari) 
					        	ok=true;
					        i++;			        
					      }
					      if (ok==false)
					      {
						      while ((i<100) && (ok==false))
						      {
						        if (PlayerInfo.OwnSpell[i]==0)
						        {
						        	PlayerInfo.OwnSpell[i]=(short) numvari;
						        	ok=true;
						        }
						        i++;			        
						      }
					      }
				    }
		           Done=true;
			    }
		        else
			    if ((variable.compareTo("DelMagie")==0) && (!Done))
			    {		    	
			        variable=ligne;
			    	variable=variable.substring(variable.indexOf("(")+1);
			    	variable=variable.substring(0,variable.length()-1);
				    ok=false;
				    i=0;
				    while ((i < general.getMagies().size()) && (ok==false))
				    {
				    	if (general.getMagieByIndex(i).Name.compareTo(variable)==0)
				    		ok=true;
				    	i++;			    	
				    }
				    if (ok)
				    {
					      numvari=i; i=0; ok=false;
					      while ((i<100) && (ok==false))
					      {
					        if (PlayerInfo.OwnSpell[i]==numvari)
					        {
								for (j=0;j<10;j++)
									if (PlayerInfo.Raccourcis[j]==-(i+1))
										PlayerInfo.Raccourcis[j]=0;
								if (barreicone!=null)
									barreicone.Redraw();
					        	PlayerInfo.OwnSpell[i]=0;
					        	ok=true;
					        }
					        i++;			        
					      }
				    }
		           Done=true;
			    }
		        else
		        if ((variable.compareTo("Teleport")==0) && (!Done))
		        {
		          variable=ligne;
		          position=0;
		          variable=variable.substring(variable.indexOf("(")+1);
		          resultat=variable.substring(0,variable.indexOf(","));
		          variable=variable.substring(variable.indexOf(",")+1);
		          i=(int) Calcule.Calcule(Formule.ReplaceStringVariable(variable.substring(0,variable.indexOf(","))))*2;
		          variable=variable.substring(variable.indexOf(",")+1);
		          j=(int) Calcule.Calcule(Formule.ReplaceStringVariable(variable.substring(0,variable.length()-1)))*2;
		          if (PlayerMChar!=null)
		          {
		            PlayerMChar.mmapx=i;
		            PlayerMChar.mmapy=j;
		            PlayerMChar.versx=i;
		            PlayerMChar.versy=j;
		          }
		          PlayerInfo.CurrentMap=resultat;
		          PlayerInfo.pmapx=(short) i;
		          PlayerInfo.pmapy=(short) j;
		          HasChangedMap=true;
		          Done=true;
		        }
		        else
		        if ((variable.compareTo("ChangeRe")==0) && (!Done))
		        {
		          variable=ligne;
		          position=0;
		          variable=variable.substring(variable.indexOf("(")+1);
		          resultat=variable.substring(0,variable.indexOf(","));
		          variable=variable.substring(variable.indexOf(",")+1);
		          i=(int) Calcule.Calcule(Formule.ReplaceStringVariable(variable.substring(0,variable.indexOf(","))))*2;
		          variable=variable.substring(variable.indexOf(",")+1);
		          j=(int) Calcule.Calcule(Formule.ReplaceStringVariable(variable.substring(0,variable.length()-1)))*2;
		          PlayerInfo.ResCarte=resultat;
		          PlayerInfo.ResX=(short) i;
		          PlayerInfo.ResY=(short) j;
		          Done=true;
		        }
		        else
		        if ((variable.compareTo("SScroll(")==0) && (!Done))
		        {
		          variable=ligne;
		          variable=variable.substring(variable.indexOf("(")+1);
		          SScrollX=(int) Calcule.Calcule(Formule.ReplaceStringVariable(variable.substring(0,variable.indexOf(","))))*16;
		          variable=variable.substring(variable.indexOf(",")+1);	          
		          SScrollY=(int) Calcule.Calcule(Formule.ReplaceStringVariable(variable.substring(0,variable.length()-1)))*16;
		          if (SVersX==-1)
		            SVersX=(PlayerInfo.pmapx*8)+prealx;
		          if (SVersY==-1)
		            SVersY=(PlayerInfo.pmapy*8)+prealy;
		          if (PlayerMChar==null)
		          {
		            if(player!=null)
		            {
		              player.Kill();
		              player=null;
		            }
			 	    PlayerMChar=new TMChar(this,PlayerInfo.Name,PlayerInfo.Chipset,PlayerInfo.SoundAttaque,PlayerInfo.SoundWound,PlayerInfo.SoundConcentration,0,0,24,32,PlayerInfo.pmapx,PlayerInfo.pmapy,0,0,PlayerDirection,(Dead==true) ? 1 : 0,0,Sprite.idSprite.idMChar,3,1,false);
				    PlayerMChar.mrealx=prealx; PlayerMChar.mrealy=prealy;
				    SpriteE.AddSprite(PlayerMChar);
		          }
		          AttenteEvenement=7;
		          Done=true;
		      	}
		        else
			    if ((variable.compareTo("ChangeSk")==0) && (!Done))
			    {
		            variable=ligne;
			    	variable=variable.substring(variable.indexOf("(")+2);
			    	variable=variable.substring(0,variable.length()-2);
			    	variable=Formule.ReplaceStringVariable(variable);
				    if (variable.compareTo(PlayerInfo.Chipset)!=0)
				    {
				      if (variable.compareTo("")!=0)
				      {
				        PlayerInfo.Chipset=variable;
				        player.Kill();
						player=new Player(this,Zoom);
						SpriteE.AddSprite(player);
				      }
				    }
		           Done=true;
			    }
		        else
			    if ((variable.compareTo("ChangeCl")==0) && (!Done))
			    {
		            variable=ligne;
			    	variable=variable.substring(variable.indexOf("(")+2);
			    	variable=variable.substring(0,variable.length()-2);
				    if (variable.compareTo("")!=0)
				    {
				       variable=Formule.ReplaceStringVariable(variable);
					   ok=false;
					   i=0;
					   while (i<general.getClassesJoueur().size())
					   {
						   if (general.getClassesJoueur().get(i).Name.compareTo(variable)==0)
							   ok=true;
						   i++;
					   }
					   if (ok)
					   {
						   PlayerInfo.Classe=general.getClassesJoueur().get(i-1);
						   if (PlayerInfo.Menu.indexOf(MenuPossibles.get(2))==-1)
								PlayerInfo.Menu.add(MenuPossibles.get(2));
						   if (PlayerInfo.Menu.indexOf(MenuPossibles.get(3))==-1)
								PlayerInfo.Menu.add(MenuPossibles.get(3));
							PlayerInfo.SoundAttaque=PlayerInfo.Classe.SoundAttaque;
							PlayerInfo.SoundWound=PlayerInfo.Classe.SoundWound;
							PlayerInfo.SoundConcentration=PlayerInfo.Classe.SoundConcentration;
							PlayerInfo.Stats.clear();
							for(i=0;i<general.getStatsBase().size();i++)
							{
								PlayerInfo.Stats.setProperty(general.getStatsBase().get(i), Integer.toString(PlayerInfo.Classe.StatsMin.get(i)));
							}
							RecalculCarac();
							PlayerInfo.Vie=PlayerInfo.VieMax;
							PlayerInfo.CurrentMag=PlayerInfo.MagMax;
							if (fiolevie==null)
							{
								fiolevie=new FioleVie(this);
								SpriteE.AddSprite(fiolevie);
							}
							if (fiolemana==null)
							{
								fiolemana=new FioleMana(this);
								SpriteE.AddSprite(fiolemana);
							}
							if (barrexp==null)
							{
								barrexp=new BarreXP(this);
								SpriteE.AddSprite(barrexp);
							}
							fiolevie.Redraw();
							fiolemana.Redraw();
							barrexp.Redraw();
					   }
				    }
				    else
				    {
						   PlayerInfo.Classe=null;
						   if (PlayerInfo.Menu.indexOf(MenuPossibles.get(2))>=0)
								PlayerInfo.Menu.remove(PlayerInfo.Menu.indexOf(MenuPossibles.get(2)));
						   if (PlayerInfo.Menu.indexOf(MenuPossibles.get(3))>=0)
								PlayerInfo.Menu.remove(PlayerInfo.Menu.indexOf(MenuPossibles.get(3)));
						   if (fiolevie!=null)
						   {
							   fiolevie.Kill();
							   fiolevie=null;
						   }
						   if (fiolemana!=null)
						   {
							   fiolemana.Kill();
							   fiolemana=null;
						   }
						   if (barrexp!=null)
						   {
							   barrexp.Kill();
							   barrexp=null;
						   }
				    }
		           Done=true;
			    }
		        else
		        if ((variable.compareTo("GenereMo")==0) && (!Done))
		        {
			          String Resultatx,Resultaty;
			          variable=ligne;
			          variable=variable.substring(variable.indexOf("(")+1);
			          resultat=variable.substring(0,variable.indexOf(","));
			          variable=variable.substring(variable.indexOf(",")+1);
			          Resultatx=variable.substring(0,variable.indexOf(","));
			          variable=variable.substring(variable.indexOf(",")+1);
			          Resultaty=variable.substring(0,variable.indexOf(","));
			          variable=variable.substring(variable.indexOf(",")+1);
			          // Nombre de monstre
			          numvari=(int) Calcule.Calcule(Formule.ReplaceStringVariable(variable.substring(0,variable.indexOf(","))));
			          variable=variable.substring(variable.indexOf(",")+1);
			          // Vitesse de respawn
			          position=(int) Calcule.Calcule(Formule.ReplaceStringVariable(variable.substring(0,variable.indexOf(","))));
			          variable=variable.substring(variable.indexOf(",")+1);
			          // Si les monstres distribuent de l'or ou non
			          gauche=(int) Calcule.Calcule(Formule.ReplaceStringVariable(variable.substring(0,variable.length()-1)));
			          i=0; ok=false;
			          while((i<general.getMonstres().size())&&(ok==false))
			          {
			        	  if (general.getMonstreByIndex(i).Name.compareTo(resultat)==0)
			        		  ok=true;
			        	  i++;
			          }
			          if (ok==true)
			          {	                    
		        		  Zone z=general.new Zone();
		        		  z.MonstreMax=numvari;
		        		  z.ZoneTypeMonstre=0;
		        		  z.VitesseSpawn=position-1;
		        		  z.X1=0-i;
		        		  z.X2=gauche;
		        		  z.Variable=Resultatx;
		        		  z.Resultat=Resultaty;
		        		  
	  					  synchronized(ListeMonstre)
	  					  {
			        		  CurrentMonstre.add(0);
			        		  CurrentSpawn.add(position);
			        		  zones.add(z);
			  				  while (CurrentMonstre.get(CurrentMonstre.size()-1) < zones.get(CurrentMonstre.size()-1).MonstreMax)
			  					  CreateMonster(false, CurrentMonstre.size()-1);
			  				  if (position==0)
			  				  {
			  					  CurrentMonstre.remove(zones.size()-1);
			  					  CurrentSpawn.remove(zones.size()-1);
			  					  zones.remove(zones.size()-1);
			  				  }
			          	  }
			          }
			          Done=true;
		        }
		        else
		        if ((variable.compareTo("TueMonst")==0) && (!Done))
		        {
					  synchronized(ListeMonstre)
  					  {
						  while(ListeMonstre.size()>0)
						  {
							  ListeMonstre.get(0).vie=0;
							  if (ListeMonstre.get(0).sprite!=null)
							  {
				   	    		  SpriteE.AddSprite(new MDead(this, ListeMonstre.get(0).sprite));
								  ListeMonstre.get(0).sprite.Kill();
								  ListeMonstre.get(0).sprite=null;
							  }
							  ListeMonstre.remove(0);
						  }
						  i=0;
						  while(i<zones.size())
						  {
							  if (zones.get(i).X1<0)
							  {
								  CurrentSpawn.remove(i);
								  CurrentMonstre.remove(i);
								  zones.remove(i);
							  }
							  else
								  i++;
						  }
  					  }
			          Done=true;
		        }
		        else
		        if ((variable.compareTo("InputQue")==0) && (!Done))
		        {
		          variable=ligne;
		          variable=variable.substring(variable.indexOf("("));
		          variable=variable.substring(1,variable.length()-1);
		          variable=Formule.ReplaceStringVariable(variable);
		          SpriteEv=new InputQuery(this,variable);
		          SpriteE.AddSprite(SpriteEv);
		          AttenteEvenement=2;
		          Done=true;
		        }
		        else
		        if ((variable.compareTo("OnResult")==0) && (!Done))
		        {
		          variable=ligne;
	        	  variable=variable.substring(variable.indexOf("('")+2);
	              variable=variable.substring(0,variable.length()-2);
		          variable=Formule.ReplaceStringVariable(variable);
		          if (variable.compareTo(ResultQueryEv)!=0)
		          {
		            CurrentLigne++;
		            while ((CurrentLigne < StringList.size()) && (variable.compareTo("OnResult")!=0) && (variable.compareTo("QueryEnd")!=0))
		            {
		              variable=StringList.get(CurrentLigne).substring(0,8);
		              CurrentLigne++;
		            }
		            if ((variable.compareTo("OnResult")==0) || (variable.compareTo("QueryEnd")==0)) CurrentLigne=CurrentLigne-2;
		          }
		          Done=true;
		        }
		        else
		        if ((variable.compareTo("QueryEnd")==0) && (!Done))
		        {
		          ResultQueryEv="";
		          Done=true;
		        }
		        else
		        if ((variable.compareTo("Magasin(")==0) && (!Done))
		        {
		          variable=ligne;
		          variable=variable.substring(variable.indexOf("('"));
		          variable=variable.substring(1,variable.length()-1);
		          variable=Formule.ReplaceStringVariable(variable);
		          SpriteEv=new MagasinEv(this,variable);
		          IsMagasinActive=true;
		          SpriteE.AddSprite(SpriteEv);
		          AttenteEvenement=3;
		          Done=true;
		        }
		        else
		        if ((variable.compareTo("Attente(")==0) && (!Done))
		        {
			          variable=ligne;
			          variable=variable.substring(variable.indexOf("(")+1);
			          variable=variable.substring(0,variable.indexOf(")"));
			          Timer3=0;
			          WaitTimer3=(int) Calcule.Calcule(Formule.ReplaceStringVariable(variable))+2;
			          AttenteEvenement=6;
			          Done=true;
		        }
		        else
		        if ((variable.compareTo("PlayMusi")==0) && (HasMusic) && (!Done))
		        {
		          variable=ligne;
			      variable=variable.substring(variable.indexOf("(")+2);
			      variable=variable.substring(0,variable.length()-2);
		          variable=Formule.ReplaceStringVariable(variable);
		          PlayMusic(variable,false);
		          Done=true;
		        }
		        else
		        if ((variable.compareTo("StopMusi")==0) && (HasMusic) && (!Done))
		        {
		          PlayMusic("",false);
		          Done=true;
		        }
		        else
		        if ((variable.compareTo("PlaySoun")==0) && (HasSound) && (!Done))
		        {
		          variable=ligne;
			      variable=variable.substring(variable.indexOf("(")+2);
			      variable=variable.substring(0,variable.length()-2);
		          variable=Formule.ReplaceStringVariable(variable);
		          PlaySound(variable,"",false);
		          Done=true;
		        }
		        else
		        if ((variable.compareTo("ChAttaqu")==0) && (!Done))
		        {
		          variable=ligne;
			      variable=variable.substring(variable.indexOf("(")+2);
			      variable=variable.substring(0,variable.length()-2);
		          variable=Formule.ReplaceStringVariable(variable);
		          PlayerInfo.SoundAttaque=variable;
		          Done=true;
		        }
		        else
		        if ((variable.compareTo("ChBlesse")==0) && (!Done))
		        {
		          variable=ligne;
			      variable=variable.substring(variable.indexOf("(")+2);
			      variable=variable.substring(0,variable.length()-2);
		          variable=Formule.ReplaceStringVariable(variable);
		          PlayerInfo.SoundWound=variable;
		          Done=true;
		        }
		        else
		        if (variable.compareTo("Sauvegar")==0)
		        {
		          variable=ligne;
		          variable=variable.substring(variable.indexOf("'")+1);
		          variable=variable.substring(0,variable.length()-2);
		          if (variable.compareTo("")!=0)
		        	  variable=Formule.ReplaceStringVariable(variable);
		          if (variable.compareTo("")==0)
		          {
			          SpriteEv=new EvSave(this,true);
			          SpriteE.AddSprite(SpriteEv);
			          AttenteEvenement=2;
		          }
		          else
		        	  Sauvegarder(System.getProperty("user.dir")+"/"+general.getName()+"_"+variable+".sav");
		          Done=true;
		        }
		        else
		        if (variable.compareTo("Chargeme")==0)
		        {
		          variable=ligne;
		          variable=variable.substring(variable.indexOf("'")+1);
		          variable=variable.substring(0,variable.length()-2);
	        	  if (general.getStyleProjet()==0)
		        	  Chargement(System.getProperty("user.dir")+"/"+general.getName()+".sav");
	        	  else
	        	  {
			          if (variable.compareTo("")!=0)
			        	  variable=Formule.ReplaceStringVariable(variable);
			          if (variable.compareTo("")==0)
			          {
				          SpriteEv=new EvSave(this,false);
				          SpriteE.AddSprite(SpriteEv);
				          AttenteEvenement=2;
			          }
			          else
			          {
		        		  Chargement(System.getProperty("user.dir")+"/"+general.getName()+"_"+variable+".sav");
			    	      while(StringList.size()>CurrentLigne+1)
			    	    	  StringList.remove(CurrentLigne+1);
			    	      StringList.set(CurrentLigne,"Teleport("+PlayerInfo.CurrentMap+","+(PlayerInfo.pmapx / 2)+","+(PlayerInfo.pmapy / 2)+")");
				          DoEvenements(PosX,PosY,Ev,StringList,CurrentLigne);
			    	      return;
			          }
	        	  }
		          Done=true;
		        }
			    else
		        if (variable.compareTo("Quitter(")==0)
		        {
		          Quit=true;
		          Done=true;
		        }
			    else
		        if (variable.compareTo("Options(")==0)
		        {
		        	  IsMenuActive=true;
	    	    	  IsStatActive=true;
	    	    	  SpriteEv=new Options(this);
		        	  SpriteE.AddSprite(SpriteEv);
		        	  AttenteEvenement=8;
		        }
			    else
		        if (variable.compareTo("ShowInte")==0)
		        {
					if (fiolevie==null)
					{
						fiolevie=new FioleVie(this);
						SpriteE.AddSprite(fiolevie);
					}
					if (fiolemana==null)
					{
						fiolemana=new FioleMana(this);
						SpriteE.AddSprite(fiolemana);
					}
					if (barrexp==null)
					{
						barrexp=new BarreXP(this);
						SpriteE.AddSprite(barrexp);
					}
					if (barreicone==null)
					{
						barreicone=new BarreIcone(this);
						SpriteE.AddSprite(barreicone);
					}
					fiolevie.Redraw();
					fiolemana.Redraw();
					barrexp.Redraw();
		        }
			    else
		        if (variable.compareTo("HideInte")==0)
		        {
				   if (fiolevie!=null)
				   {
					   fiolevie.Kill();
					   fiolevie=null;
				   }
				   if (fiolemana!=null)
				   {
					   fiolemana.Kill();
					   fiolemana=null;
				   }
				   if (barrexp!=null)
				   {
					   barrexp.Kill();
					   barrexp=null;
				   }
				   if (barreicone!=null)
				   {
					   barreicone.Kill();
					   barreicone=null;
				   }
		        }
			    else
		        if (variable.compareTo("AddMenu(")==0)
		        {		        	
		          variable=ligne;
		          variable=variable.substring(variable.indexOf("(")+1);
		          variable=variable.substring(0,variable.indexOf(")"));
		          if (MenuPossibles.indexOf(variable)>=0)
		          {
		        	  if (PlayerInfo.Menu.indexOf(variable)==-1)
		        	  	PlayerInfo.Menu.add(variable);
		          }
		        }
			    else
		        if (variable.compareTo("DelMenu(")==0)
		        {		        	
		          variable=ligne;
		          variable=variable.substring(variable.indexOf("(")+1);
		          variable=variable.substring(0,variable.indexOf(")"));
		          if (MenuPossibles.indexOf(variable)>=0)
		          {
		        	  if (PlayerInfo.Menu.indexOf(variable)>=0)
		        	  	PlayerInfo.Menu.remove(PlayerInfo.Menu.indexOf(variable));
		        	  if (MenuPossibles.indexOf(variable)==0)
		        	  {
		        		  for(i=0;i<10;i++)
		        			  if (PlayerInfo.Raccourcis[i]>0)
		        				  PlayerInfo.Raccourcis[i]=0;
		        	  }
		        	  if (MenuPossibles.indexOf(variable)==1)
		        	  {
		        		  for(i=0;i<10;i++)
		        			  if (PlayerInfo.Raccourcis[i]<0)
		        				  PlayerInfo.Raccourcis[i]=0;
		        	  }
		        	  if (barreicone!=null)
		        		  barreicone.Redraw();
		          }
		        }
		        // PAS DE ELSE ICI!!
		        if ((AttenteEvenement==0) && (Quit==false))
		          DoEvenements(PosX,PosY,Ev,StringList,CurrentLigne+1);
	      }
	      else
	      {
	        // affectation
	    	variable=ligne.substring(0,ligne.indexOf("="));
	    	resultat=ligne.substring(ligne.indexOf("=")+1);
	    	ok=false;
	    	if (variable.length()>9)
	    		if (variable.substring(0, 9).compareTo("Variable[")==0)
	    			ok=true;
	        if ((EvVarCondition.indexOf(variable) >= 0) || (ok==true))
	   		{
		      AffectationVarPlayer(variable , resultat);
	          if (AttenteEvenement==0)
	            DoEvenements(PosX,PosY,Ev,StringList,CurrentLigne+1);
	   		}
	        else
	        {
	          AffectationVarEv(variable,resultat);
	          if ((AttenteEvenement==0) && (Quit==false))
	            DoEvenements(PosX,PosY,Ev,StringList,CurrentLigne+1);
	        }
	      }
	    }
	    else
	    {
	      if ((StringList.size() > 0) && (HasChangedMap==false))
	      {
	        if ((PosX>=0)&&(PosY>=0))
	        {
		        if (Evenements[PosX][PosY].Sprite!=null)
		        {
		          if (Evenements[PosX][PosY].Sprite.ID==Sprite.idSprite.idEvenement)
		          {
		        	MChar=(TMChar) Evenements[PosX][PosY].Sprite;  
		            if ((MChar.evversx>0) && (MChar.evversy>0))
		            {
		              MChar.versx=MChar.evversx;
		              MChar.versy=MChar.evversy;
		              MChar.evversx=-1;
		              MChar.evversy=-1;
		            }
		          }
		        }
	        }
	        GerePageEvenement();
	      }
	      if (player==null)
	      {
	        if (PlayerMChar!=null)
	        if (PlayerMChar.typechar==3)
	        {
	          if ((PlayerInfo.pmapx!=PlayerMChar.mmapx) || (PlayerInfo.pmapy!=PlayerMChar.mmapy))
	          {
	            prealx=0; prealy=0;
	            PlayerInfo.pmapx=(short) PlayerMChar.mmapx;
	            PlayerInfo.pmapy=(short) PlayerMChar.mmapy;
	            PlayerDirection=PlayerMChar.Direction;
	          }
	          PlayerMChar.Kill();
	          PlayerMChar=null;
	        }
	        player=new Player(this,Zoom);
	        SpriteE.AddSprite(player);
	        ScrollDirection=0;
	      }
	      SVersX=-1; SScrollX=-1; SVersY=-1; SScrollY=-1; ScX=0; ScY=0;
	      ScrollDirection=0;
	      if (general.getStyleProjet()==0)
	      	Sauvegarder(System.getProperty("user.dir")+"/"+general.getName()+".sav");
	      AutoEvent=false;
	      IsInEvent=false;
	    }
	}

	private void CheckPageEvenement(int i,int j)
	{
		int k,l;
		String ligne;
		boolean StopBoucle,Good;
        if (Evenements[i][j].CondDecl.size()==1)
        {
           Evenements[i][j].Ev=0;
           return;
        }
        // on commence par regarder les conditions de declenchement
        Evenements[i][j].Ev=Evenements[i][j].evenement.size()-1; // si y'a pas de bonne possibilites on prendra 0 par defaut
        Evenements[i][j].HasToCheckPage=false;
        StopBoucle=false; k=0;
        while ((k<Evenements[i][j].CondDecl.size()) && (!StopBoucle))
        {
            Good=true;
            l=0;
            while ((l<Evenements[i][j].CondDecl.get(k).size()) && (Good==true))
            {
              // ne prend en compte que les elements variable
              if (!EvCondition.contains(Evenements[i][j].CondDecl.get(k).get(l)))
              {
                ligne =Evenements[i][j].CondDecl.get(k).get(l);
                Good=VerifieCondition(i,j,ligne);
              }
              l++;
            }
            if (Good)
            {
              Evenements[i][j].Ev=k;
              StopBoucle=true;
            }
            else
            {
              Evenements[i][j].WaitingTimer=0;
              Evenements[i][j].WaitingTimer2=0;
              Evenements[i][j].WaitingTimer3=0;
            }
            k++;
        }
        if ((Evenements[i][j].WaitingTimer>0) && (((Evenements[i][j].WaitingTimer<WaitTimer) || (WaitTimer==0)) && (Evenements[i][j].WaitingTimer>Timer)))
          WaitTimer=Evenements[i][j].WaitingTimer;
        if ((Evenements[i][j].WaitingTimer2>0) && (((Evenements[i][j].WaitingTimer2<WaitTimer2) || (WaitTimer2==0)) && (Evenements[i][j].WaitingTimer2>Timer2)))
          WaitTimer2=Evenements[i][j].WaitingTimer2;
        if ((Evenements[i][j].WaitingTimer3>0) && (((Evenements[i][j].WaitingTimer3<WaitTimer3) || (WaitTimer3==0)) && (Evenements[i][j].WaitingTimer3>Timer3)))
          WaitTimer3=Evenements[i][j].WaitingTimer3;
	}
	private boolean CheckEvenement(String StringEv)
	{
		boolean Appuie;
		int Ev, i;
		int CaseX , CaseY, prmapx , prmapy,compx,compy;
		TMChar MChar;
		if (HasChangedMap==true) return false;
		CondDeclenche=StringEv;
		compx=0; compy=4;
/*		if (Keys[KRIGHT] == KeyState.PRESSED) compx=8;
		if (Keys[KLEFT] == KeyState.PRESSED) compx=-8;
		if (Keys[KDOWN] == KeyState.PRESSED) compy=8;
		if (Keys[KUP] == KeyState.PRESSED) compy=-8;*/
        prmapx=Math.round(((PlayerInfo.pmapx * 16f) + prealx+ compx) / 32f);
        prmapy=Math.round(((PlayerInfo.pmapy * 16f) + prealy+compy) / 32f);
		if ((StringEv.compareTo("Appuie sur bouton")==0) || (StringEv.compareTo("Attaque")==0))
		{
		    Appuie=(StringEv.compareTo("Appuie sur bouton")==0);
		    CaseX=-1; CaseY=-1;
		    switch(PlayerDirection)
		    {
		      case 0 :
		        CaseX = prmapx;
		        if (Appuie)
		        {
		          if (prmapy-2 >=0)
		           if (Evenements[prmapx][prmapy-2].evenement!=null)
		           {
		             if (Evenements[prmapx][prmapy-2].HasToCheckPage==true)
		               CheckPageEvenement(prmapx,prmapy-2);
		             if (Evenements[prmapx][prmapy-2].evenement.get(Evenements[prmapx][prmapy-2].Ev).Visible==true)
		               if (Evenements[prmapx][prmapy-2].CondDecl.get(Evenements[prmapx][prmapy-2].Ev).contains(StringEv))
		                 CaseY=prmapy-2;
		           }
		        }
		        if (prmapy-1 >=0)
		         if (Evenements[prmapx][prmapy-1].evenement!=null)
		         {
		           if (Evenements[prmapx][prmapy-1].HasToCheckPage==true)
		             CheckPageEvenement(prmapx,prmapy-1);
		           if (Evenements[prmapx][prmapy-1].evenement.get(Evenements[prmapx][prmapy-1].Ev).Visible==true)
		              if (Evenements[prmapx][prmapy-1].CondDecl.get(Evenements[prmapx][prmapy-1].Ev).contains(StringEv))
		                 CaseY=prmapy-1;
		         }
		        if (prmapy >=0)
		         if (Evenements[prmapx][prmapy].evenement!=null)
		         {
		           if (Evenements[prmapx][prmapy].HasToCheckPage==true)
		             CheckPageEvenement(prmapx,prmapy);
		           if (Evenements[prmapx][prmapy].evenement.get(Evenements[prmapx][prmapy].Ev).Visible==true)
		              if (Evenements[prmapx][prmapy].CondDecl.get(Evenements[prmapx][prmapy].Ev).contains(StringEv))
		                CaseY=prmapy;
		         }
		        break;
		      case 1 :
		        CaseY = prmapy;
		        if (Appuie)
		        {
		          if (prmapx+2 <CurrentMap.TailleX)
		           if (Evenements[prmapx+2][prmapy].evenement!=null)
		           {
		             if (Evenements[prmapx+2][prmapy].HasToCheckPage==true)
		               CheckPageEvenement(prmapx+2,prmapy);
		             if (Evenements[prmapx+2][prmapy].evenement.get(Evenements[prmapx+2][prmapy].Ev).Visible==true)
		               if (Evenements[prmapx+2][prmapy].CondDecl.get(Evenements[prmapx+2][prmapy].Ev).contains(StringEv))
		                 CaseX=prmapx+2;
		           }
		        }
		        if (prmapx+1 <CurrentMap.TailleX)
		         if (Evenements[prmapx+1][prmapy].evenement!=null)
		         {
		           if (Evenements[prmapx+1][prmapy].HasToCheckPage==true)
		             CheckPageEvenement(prmapx+1,prmapy);
		           if (Evenements[prmapx+1][prmapy].evenement.get(Evenements[prmapx+1][prmapy].Ev).Visible==true)
		              if (Evenements[prmapx+1][prmapy].CondDecl.get(Evenements[prmapx+1][prmapy].Ev).contains(StringEv))
		                CaseX=prmapx+1;
		         }
		        if (prmapx <CurrentMap.TailleX)
		         if (Evenements[prmapx][prmapy].evenement!=null)
		         {
		           if (Evenements[prmapx][prmapy].HasToCheckPage==true)
		             CheckPageEvenement(prmapx,prmapy);
		           if (Evenements[prmapx][prmapy].evenement.get(Evenements[prmapx][prmapy].Ev).Visible==true)
		              if (Evenements[prmapx][prmapy].CondDecl.get(Evenements[prmapx][prmapy].Ev).contains(StringEv))
		                CaseX=prmapx;
		         }
		         break;
		      case 2 :
		        CaseX = prmapx;
		        if (Appuie)
		        {
		          if (prmapy+2 <CurrentMap.TailleY)
		           if (Evenements[prmapx][prmapy+2].evenement!=null)
		           {
		             if (Evenements[prmapx][prmapy+2].HasToCheckPage==true)
		               CheckPageEvenement(prmapx,prmapy+2);
		             if (Evenements[prmapx][prmapy+2].evenement.get(Evenements[prmapx][prmapy+2].Ev).Visible==true)
			           if (Evenements[prmapx][prmapy+2].CondDecl.get(Evenements[prmapx][prmapy+2].Ev).contains(StringEv))
		                  CaseY=prmapy+2;
		           }
		        }
		        if (prmapy+1 < CurrentMap.TailleY)
		         if (Evenements[prmapx][prmapy+1].evenement!=null)
		         {
		           if (Evenements[prmapx][prmapy+1].HasToCheckPage==true)
		             CheckPageEvenement(prmapx,prmapy+1);
		           if (Evenements[prmapx][prmapy+1].evenement.get(Evenements[prmapx][prmapy+1].Ev).Visible==true)
		              if (Evenements[prmapx][prmapy+1].CondDecl.get(Evenements[prmapx][prmapy+1].Ev).contains(StringEv))
		                CaseY=prmapy+1;
		         }
		        if (prmapy < CurrentMap.TailleY)
		         if (Evenements[prmapx][prmapy].evenement!=null)
		         {
		           if (Evenements[prmapx][prmapy].HasToCheckPage==true)
		             CheckPageEvenement(prmapx,prmapy);
		           if (Evenements[prmapx][prmapy].evenement.get(Evenements[prmapx][prmapy].Ev).Visible==true)
		              if (Evenements[prmapx][prmapy].CondDecl.get(Evenements[prmapx][prmapy].Ev).contains(StringEv))
		                CaseY=prmapy;
		         }
		        break;
		      case 3 :
		        CaseY = prmapy;
		        if (Appuie) 
		        {
		          if (prmapx-2 >=0)
		           if (Evenements[prmapx-2][prmapy].evenement!=null)
		           {
		             if (Evenements[prmapx-2][prmapy].HasToCheckPage==true)
		               CheckPageEvenement(prmapx-2,prmapy);
		             if (Evenements[prmapx-2][prmapy].evenement.get(Evenements[prmapx-2][prmapy].Ev).Visible==true)
			           if (Evenements[prmapx-2][prmapy].CondDecl.get(Evenements[prmapx-2][prmapy].Ev).contains(StringEv))
		                 CaseX=prmapx-2;
		           }
		        }
		        if (prmapx-1 >=0)
		         if (Evenements[prmapx-1][prmapy].evenement!=null)
		         {
		           if (Evenements[prmapx-1][prmapy].HasToCheckPage==true)
		             CheckPageEvenement(prmapx-1,prmapy);
		           if (Evenements[prmapx-1][prmapy].evenement.get(Evenements[prmapx-1][prmapy].Ev).Visible==true)
		             if (Evenements[prmapx-1][prmapy].CondDecl.get(Evenements[prmapx-1][prmapy].Ev).contains(StringEv))
		               CaseX=prmapx-1;
		         }
		        if (prmapx >=0)
		         if (Evenements[prmapx][prmapy].evenement!=null)
		         {
		           if (Evenements[prmapx][prmapy].HasToCheckPage==true)
		             CheckPageEvenement(prmapx,prmapy);
		           if (Evenements[prmapx][prmapy].evenement.get(Evenements[prmapx][prmapy].Ev).Visible==true)
		             if (Evenements[prmapx][prmapy].CondDecl.get(Evenements[prmapx][prmapy].Ev).contains(StringEv))
		               CaseX=prmapx;
		         }
		        break;
		    }
		    if ((CaseX != -1) && (CaseY != -1))
		    {
		      Ev = Evenements[CaseX][CaseY].Ev;
		      // trouve un evenement valide; maintenant on check ces conditions...
		      for (i=0;i<Evenements[CaseX][CaseY].CondDecl.get(Ev).size();i++)
		      {
		        // ne prend en compte que les elements variable
		          if (Evenements[CaseX][CaseY].CondDecl.get(Ev).get(i).compareTo(StringEv)==0)
		          {
		            if (Evenements[CaseX][CaseY].Sprite !=null)
		            if (Evenements[CaseX][CaseY].Sprite.ID==Sprite.idSprite.idEvenement)
		            {
		              MChar=(TMChar) Evenements[CaseX][CaseY].Sprite;
		              switch(PlayerDirection)
		              {
		                case 0 : MChar.Direction=2; break;
		                case 1 : MChar.Direction=3; break;
		                case 2 : MChar.Direction=0; break;
		                case 3 : MChar.Direction=1; break;
		              }
		            }
		            DoEvenements(CaseX,CaseY,Ev,Evenements[CaseX][CaseY].CommandeEv.get(Ev),0);
		            return true;
		          }
		      }
		    }
		}
		if (StringEv.compareTo("En contact")==0)
		{
		    if (Evenements[prmapx][prmapy].evenement!=null)
		    {
		      if (Evenements[prmapx][prmapy].HasToCheckPage==true)
		        CheckPageEvenement(prmapx,prmapy);
		      Ev = Evenements[prmapx][prmapy].Ev;
		      if (Ev!=-1)
		      {
			      for (i=0;i<Evenements[prmapx][prmapy].CondDecl.get(Ev).size();i++)
			    	if (EvCondition.contains(Evenements[prmapx][prmapy].CondDecl.get(Ev).get(i)))
			          if (Evenements[prmapx][prmapy].CondDecl.get(Ev).get(i).compareTo(StringEv)==0)
			          {
			            DoEvenements(prmapx,prmapy,Evenements[prmapx][prmapy].Ev,Evenements[prmapx][prmapy].CommandeEv.get(Evenements[prmapx][prmapy].Ev),0);
			          }
			   }
		    }		    
		}
		return false;
	}
	
	private void AttendreEvenement()
	{
		  TMChar MChar;
		  switch(AttenteEvenement)
		  {
		    case 1 :
		      MChar=(TMChar) SpriteEv;
		      if (((MChar.versx==MChar.mmapx) && (MChar.versy==MChar.mmapy)) ||
		         (MChar.isDead==true))
		      {
		        AttenteEvenement=0;
		        DoEvenements(EventAttX,EventAttY,EventEv,ListeEv,CurrentLigneEv+1);
		      }
		      break;
		    case 2 :		    
		      if (SpriteEv==null)
		      {
		        AttenteEvenement=0;
		        DoEvenements(EventAttX,EventAttY,EventEv,ListeEv,CurrentLigneEv+1);
		      }
		      break;
		    case 3 :
		      if (IsMagasinActive==false)
		      {
		        AttenteEvenement=0;
		        DoEvenements(EventAttX,EventAttY,EventEv,ListeEv,CurrentLigneEv+1);
		      }
		      break;
		    case 4 :
		      if (IsInInputString==false)
		      {
		        AttenteEvenement=0;
		        DoEvenements(EventAttX,EventAttY,EventEv,ListeEv,CurrentLigneEv);
		      }
		      break;
		    case 5 :
		      if (IsInInputString==false)
		      {
		        AttenteEvenement=0;
        	    Sauvegarder(System.getProperty("user.dir")+"/"+general.getName()+"_"+InputString+".sav");
        	    InputString="";
		        DoEvenements(EventAttX,EventAttY,EventEv,ListeEv,CurrentLigneEv+1);
		      }
		      break;
		    case 6 :
		      if (Timer3>WaitTimer3-2)
		      {
		        AttenteEvenement=0;
		        DoEvenements(EventAttX,EventAttY,EventEv,ListeEv,CurrentLigneEv+1);
		      }
		      break;
		    case 7 :
		      if ((SVersX==SScrollX) && (SVersY==SScrollY))
		      {
		        AttenteEvenement=0;
		        DoEvenements(EventAttX,EventAttY,EventEv,ListeEv,CurrentLigneEv+1);
		      }
		      break;
		    case 8 :		    
		      if (SpriteEv.isDead)
		      {
		    	  SpriteEv=null;
		          IsMenuActive=false;
		    	  AttenteEvenement=0;
		          DoEvenements(EventAttX,EventAttY,EventEv,ListeEv,CurrentLigneEv+1);
		      }
		      break;
		  }
	}
	
	private void GerePageEvenement()
	{
		int i,j, maxx , maxy,oldpage,Ev;
		TFChar FChar;
		if (HasChangedMap==false)
		{
		    maxx=CurrentMap.TailleX-1;
		    maxy=CurrentMap.TailleY-1;
		    for (i=0;i<=maxx;i++)
		      for (j=0;j<=maxy;j++)
		        if ((i>=0) && (i<=maxx) && (j>=0) && (j<=maxy))
		          if (Evenements[i][j].evenement!=null)
		          {
		            if  (((i>=(PlayerInfo.pmapx-PlayerInfo.CentreX-(ScX / 8)+NDetect) / 2) && (i<=(PlayerInfo.pmapx-PlayerInfo.CentreX-(ScX / 8)+Detect) / 2)
		            &&  (j>=(PlayerInfo.pmapy-PlayerInfo.CentreY-(ScY / 8)+NDetect) / 2) && (j<=(PlayerInfo.pmapy-PlayerInfo.CentreY-(ScY / 8)+Detect) / 2))
		            ||  ((i>=(PlayerInfo.pmapx+NDetect) / 2) && (i<=(PlayerInfo.pmapx+Detect) / 2)
		            &&  (j>=(PlayerInfo.pmapy+NDetect) / 2) && (j<=(PlayerInfo.pmapy+Detect) / 2)))
		            {
		              oldpage=Evenements[i][j].Ev;
		              if (Evenements[i][j].CondDecl.size()>1)
		                CheckPageEvenement(i,j);
		              else
		                Evenements[i][j].Ev=0;
		              if ((oldpage!=Evenements[i][j].Ev) && (Evenements[i][j].Sprite!=null))
		              {
		                //optimisation du bignou
		                Ev=Evenements[i][j].Ev;
		                if ((Evenements[i][j].evenement.get(Ev).Chipset.compareTo(Evenements[i][j].evenement.get(oldpage).Chipset)==0) &&
		                   (Evenements[i][j].evenement.get(Ev).W==Evenements[i][j].evenement.get(oldpage).W) &&
		                   (Evenements[i][j].evenement.get(Ev).H==Evenements[i][j].evenement.get(oldpage).H) &&
		                   (Evenements[i][j].evenement.get(Ev).TypeAnim==Evenements[i][j].evenement.get(oldpage).TypeAnim))
		                {
		                   if ((Evenements[i][j].evenement.get(Ev).Chipset.compareTo("")==0) && (Evenements[i][j].evenement.get(Ev).Visible))
		                   {
	                     	 FChar=(TFChar) Evenements[i][j].Sprite;
		                     switch (Evenements[i][j].evenement.get(Ev).TypeAnim)
		                     {
		                     	case 0 :
		                     		FChar.Redraw(Evenements[i][j].evenement.get(Ev).X+(Evenements[i][j].evenement.get(Ev).NumAnim*Evenements[i][j].evenement.get(Ev).W),
		                     				Evenements[i][j].evenement.get(Ev).Y+(Evenements[i][j].evenement.get(Ev).Direction*Evenements[i][j].evenement.get(Ev).H));		                     		
		                     		break;
		                     	case 3 :
		                     		FChar.Redraw(Evenements[i][j].evenement.get(Ev).X,Evenements[i][j].evenement.get(Ev).Y);
		                     		break;
		                     }
		                   }
		                   else
		                   {
		                     // on ne recree le sprite que si il est different
		                     if (Evenements[i][j].Sprite.isDead==false)
		                       Evenements[i][j].Sprite.Kill();
		                     Evenements[i][j].Sprite=null;
		                   }
		                }
		                else
		                {
		                  // on ne recree le sprite que si il est different
		                  if (Evenements[i][j].Sprite.isDead==false)
		                    Evenements[i][j].Sprite.Kill();
		                  Evenements[i][j].Sprite=null;
		                  if ((Evenements[i][j].evenement.get(Ev).Visible) && (Evenements[i][j].evenement.get(Ev).Chipset.compareTo("")==0))
		                  {
		                    // sprite visible
		                    if (Evenements[i][j].Sprite==null)
		                    {
		      	              switch(Evenements[i][j].evenement.get(Ev).TypeAnim)
		    	              {
		    	                case 0 : Evenements[i][j].Sprite=new TFChar(this,Evenements[i][j].evenement.get(Ev).Name,
		    	                										   Evenements[i][j].evenement.get(Ev).Chipset,
		    	                										   Evenements[i][j].evenement.get(Ev).X+Evenements[i][j].evenement.get(Ev).NumAnim*Evenements[i][j].evenement.get(Ev).W,
		    	                										   Evenements[i][j].evenement.get(Ev).Y+Evenements[i][j].evenement.get(Ev).Direction*Evenements[i][j].evenement.get(Ev).H,
		    	                										   Evenements[i][j].evenement.get(Ev).W,
		    	                										   Evenements[i][j].evenement.get(Ev).H,i*2,j*2,Evenements[i][j].evenement.get(Ev).Z,Evenements[i][j].evenement.get(Ev).Transparent);
		    	                		 break;
		    	                case 1 :
		    	                case 2 : Evenements[i][j].Sprite=new TMChar(this,Evenements[i][j].evenement.get(Ev).Name,
		    	                										   Evenements[i][j].evenement.get(Ev).Chipset,"","","",
		    	                										   Evenements[i][j].evenement.get(Ev).X,
		    	                										   Evenements[i][j].evenement.get(Ev).Y,
		    	                										   Evenements[i][j].evenement.get(Ev).W,
		    	                										   Evenements[i][j].evenement.get(Ev).H,i*2,j*2,i,j,
		    	                										   Evenements[i][j].evenement.get(Ev).Direction,0,Evenements[i][j].evenement.get(Ev).Z,
		    	                                                           Sprite.idSprite.idEvenement,0,Evenements[i][j].evenement.get(Ev).Vitesse,false);
		    	                         break;
		    	                case 3 : Evenements[i][j].Sprite=new TFChar(this,Evenements[i][j].evenement.get(Ev).Name,
		    	                										   Evenements[i][j].evenement.get(Ev).Chipset,
		    	                										   Evenements[i][j].evenement.get(Ev).X,
		    	                										   Evenements[i][j].evenement.get(Ev).Y,
		    	                										   Evenements[i][j].evenement.get(Ev).W,
		    	                										   Evenements[i][j].evenement.get(Ev).H,i*2,j*2,Evenements[i][j].evenement.get(Ev).Z,Evenements[i][j].evenement.get(Ev).Transparent);
		    	                         break;
		    	                case 4 : Evenements[i][j].Sprite=new TMChar(this,Evenements[i][j].evenement.get(Ev).Name,
		    	                										   Evenements[i][j].evenement.get(Ev).Chipset,"","","",
		    	                										   Evenements[i][j].evenement.get(Ev).X,
		    	                										   Evenements[i][j].evenement.get(Ev).Y,
		    	                										   Evenements[i][j].evenement.get(Ev).W,
		    	                										   Evenements[i][j].evenement.get(Ev).H,i*2,j*2,i,j,
		    	                                                           2,0,Evenements[i][j].evenement.get(Ev).Z,
		    	                                                           Sprite.idSprite.idEvenement,0,Evenements[i][j].evenement.get(Ev).Vitesse,false);
		    	                         break;
		    	              }
		                      SpriteE.AddSprite(Evenements[i][j].Sprite);
		                      if (Evenements[i][j].evenement.get(Ev).Transparent==true)
		                    	  Evenements[i][j].Sprite.Transparency=0.5f;
		                    }
		                  }
		                }
		              }
		            }
		            else
		              Evenements[i][j].Ev=-1;
		          }
		}
	}

	private void GereEvenement(boolean Animate)
	{
	  int CaseX , CaseY,i,j,l,maxx , maxy,prmapx,prmapy,compx,compy;
	  int Ev,NbADepasser;
      boolean StopBoucle,trouve;
      TMChar MChar;
	  if ((Animate==false) && (Dead))
	    return;
	  AttendreEvenement();
	  maxx=CurrentMap.TailleX-1;
	  maxy=CurrentMap.TailleY-1;
	  for (i=((PlayerInfo.pmapx-PlayerInfo.CentreX+(ScX / 8)+NDetect) / 2);i<=((PlayerInfo.pmapx-PlayerInfo.CentreX+(ScX / 8)+Detect) / 2);i++)
	    for (j=((PlayerInfo.pmapy-PlayerInfo.CentreY+(ScY / 8)+NDetect) / 2);j<=((PlayerInfo.pmapy-PlayerInfo.CentreY+(ScY / 8)+Detect) / 2);j++)
	      if ((i>=0) && (i<=maxx) && (j>=0) && (j<=maxy) && (HasChangedMap==false))
	        if (Evenements[i][j].evenement!=null)
	        {
	          if (Evenements[i][j].Ev==-1) CheckPageEvenement(i,j);
	          if (!Animate)
	          {
	            if ((Evenements[i][j].WaitingTimer > 0) && (Timer>Evenements[i][j].WaitingTimer))
	            {
	              WaitTimer=0;
	              Evenements[i][j].WaitingTimer=0;
	              GerePageEvenement();
	            }
	            if ((Evenements[i][j].WaitingTimer2 > 0) && (Timer2>Evenements[i][j].WaitingTimer2))
	            {
	              WaitTimer2=0;
	              Evenements[i][j].WaitingTimer2=0;
	              GerePageEvenement();
	            }
	            if ((Evenements[i][j].WaitingTimer3 > 0) && (Timer3>Evenements[i][j].WaitingTimer3))
	            {
	              WaitTimer3=0;
	              Evenements[i][j].WaitingTimer3=0;
	              GerePageEvenement();
	            }
	          }
	          CaseX=i; CaseY=j;
	          Ev=Evenements[i][j].Ev;
	          if ((Evenements[i][j].evenement.get(Ev).Visible) && (Evenements[i][j].evenement.get(Ev).Chipset.compareTo("")!=0)
	          &&  ((((i*2) - (PlayerInfo.pmapx-PlayerInfo.CentreX+(ScX / 8)))>NDetect) &&
	               (((i*2) - (PlayerInfo.pmapx-PlayerInfo.CentreX+(ScX / 8)))<Detect) &&
	               (((j*2) - (PlayerInfo.pmapy-PlayerInfo.CentreY+(ScY / 8)))>NDetect) &&
	               (((j*2) - (PlayerInfo.pmapy-PlayerInfo.CentreY+(ScY / 8)))<Detect)))
	          {
	            // sprite visible
	            CaseX=i; CaseY=j;
	            if (Evenements[i][j].Sprite==null)
	            {
	              switch(Evenements[i][j].evenement.get(Ev).TypeAnim)
	              {
	                case 0 : Evenements[i][j].Sprite=new TFChar(this,Evenements[i][j].evenement.get(Ev).Name,
	                										   Evenements[i][j].evenement.get(Ev).Chipset,
	                										   Evenements[i][j].evenement.get(Ev).X+Evenements[i][j].evenement.get(Ev).NumAnim*Evenements[i][j].evenement.get(Ev).W,
	                										   Evenements[i][j].evenement.get(Ev).Y+Evenements[i][j].evenement.get(Ev).Direction*Evenements[i][j].evenement.get(Ev).H,
	                										   Evenements[i][j].evenement.get(Ev).W,
	                										   Evenements[i][j].evenement.get(Ev).H,i*2,j*2,Evenements[i][j].evenement.get(Ev).Z,Evenements[i][j].evenement.get(Ev).Transparent);
	                		 break;
	                case 1 :
	                case 2 : Evenements[i][j].Sprite=new TMChar(this,Evenements[i][j].evenement.get(Ev).Name,
	                										   Evenements[i][j].evenement.get(Ev).Chipset,"","","",
	                										   Evenements[i][j].evenement.get(Ev).X,
	                										   Evenements[i][j].evenement.get(Ev).Y,
	                										   Evenements[i][j].evenement.get(Ev).W,
	                										   Evenements[i][j].evenement.get(Ev).H,i*2,j*2,i,j,
	                										   Evenements[i][j].evenement.get(Ev).Direction,0,Evenements[i][j].evenement.get(Ev).Z,
	                                                           Sprite.idSprite.idEvenement,0,Evenements[i][j].evenement.get(Ev).Vitesse,false);
	                         break;
	                case 3 : Evenements[i][j].Sprite=new TFChar(this,Evenements[i][j].evenement.get(Ev).Name,
	                										   Evenements[i][j].evenement.get(Ev).Chipset,
	                										   Evenements[i][j].evenement.get(Ev).X,
	                										   Evenements[i][j].evenement.get(Ev).Y,
	                										   Evenements[i][j].evenement.get(Ev).W,
	                										   Evenements[i][j].evenement.get(Ev).H,i*2,j*2,Evenements[i][j].evenement.get(Ev).Z,Evenements[i][j].evenement.get(Ev).Transparent);
	                         break;
	                case 4 : Evenements[i][j].Sprite=new TMChar(this,Evenements[i][j].evenement.get(Ev).Name,
	                										   Evenements[i][j].evenement.get(Ev).Chipset,"","","",
	                										   Evenements[i][j].evenement.get(Ev).X,
	                										   Evenements[i][j].evenement.get(Ev).Y,
	                										   Evenements[i][j].evenement.get(Ev).W,
	                										   Evenements[i][j].evenement.get(Ev).H,i*2,j*2,i,j,
	                                                           2,0,Evenements[i][j].evenement.get(Ev).Z,
	                                                           Sprite.idSprite.idEvenement,0,Evenements[i][j].evenement.get(Ev).Vitesse,false);
	                         break;
	              }
	              SpriteE.AddSprite(Evenements[i][j].Sprite);
	              if (Evenements[i][j].evenement.get(Ev).Transparent==true)
	            	  Evenements[i][j].Sprite.Transparency=0.5f;
	            }
	            if (((IsInEvent==false) || (Evenements[i][j].evenement.get(Ev).TypeAnim==3)) && (IsInInputString==false) && (Animate))
	            {
	              // Movement evenement
	              switch(Evenements[i][j].evenement.get(Ev).TypeAnim)
	              {
	                case 4 :	                
	                  Evenements[i][j].moveevenement++;
	                  if ((Evenements[i][j].evenement.get(Ev).Vitesse==0) ||
	                  (Evenements[i][j].moveevenement > (8- Evenements[i][j].evenement.get(Ev).Vitesse)))
	                  {
	                    compx=0; compy=4;
	                    prmapx=Math.round(((PlayerInfo.pmapx * 16f) + prealx+ compx) / 32f);
	                    prmapy=Math.round(((PlayerInfo.pmapy * 16f) + prealy+compy) / 32f);
	                    CaseX=i; CaseY=j;
	                    trouve=((CaseX!=prmapx) || (CaseY!=prmapy));
	                    if (trouve==false)
	                      CheckEvenement("En contact");
	                    else
	                      trouve=(Math.abs(CaseX-prmapx) + Math.abs(CaseY-prmapy) > Evenements[i][j].evenement.get(Ev).Direction);
	                    if (trouve)
	                    {
	                      if (Math.abs(CaseX-prmapx) >= Math.abs(CaseY-prmapy))
	                      {
	                        if (CaseX > prmapx) CaseX--;
	                        else CaseX++;
	                      }
	                      else
	                      {
	                        if (CaseY > prmapy) CaseY--;
	                        else CaseY++;
	                      }
	                      if ((CaseX >= 0) && (CaseX <= maxx)
	                        && (CaseY >= 0) && (CaseY <= maxy))
	                      {
	                        trouve=true;
	                        if (CurrentMap.cases[CaseX][CaseY].X1 > 0)
	                          trouve=block[0][CurrentMap.cases[CaseX][CaseY].X1-1][CurrentMap.cases[CaseX][CaseY].Y1-1];
	                        if ((CurrentMap.cases[CaseX][CaseY].X2 > 0) && (trouve=true))
	                          trouve=block[1][CurrentMap.cases[CaseX][CaseY].X2-1][CurrentMap.cases[CaseX][CaseY].Y2-1];
	                        if (trouve==true)
	                          trouve=(Evenements[CaseX][CaseY].evenement==null);
	                        if (trouve==false)
	                        {
	                          // si on est bloque on choisi une alternative
	                          CaseX=i; CaseY=j;
	                          if (Math.abs(CaseX-prmapx) >= Math.abs(CaseY-prmapy))
	                          {
	                            if (CaseY > prmapy) CaseY--;
	                            else CaseY++;
	                          }
	                          else
	                          {
	                            if (CaseX > prmapx) CaseX--;
	                            else CaseX++;
	                          }
	                          trouve=true;
	                          if (CurrentMap.cases[CaseX][CaseY].X1 > 0)
	                            trouve=block[0][CurrentMap.cases[CaseX][CaseY].X1-1][CurrentMap.cases[CaseX][CaseY].Y1-1];
	                          if ((CurrentMap.cases[CaseX][CaseY].X2 > 0) && (trouve==true))
	                            trouve=block[1][CurrentMap.cases[CaseX][CaseY].X2-1][CurrentMap.cases[CaseX][CaseY].Y2-1];
	                        }
	                        if ((Evenements[CaseX][CaseY].evenement==null) && (trouve))
	                        {
	                          Evenements[CaseX][CaseY].evenement=Evenements[i][j].evenement; Evenements[i][j].evenement=null;
	                          Evenements[CaseX][CaseY].CondDecl=Evenements[i][j].CondDecl; Evenements[i][j].CondDecl=null;
	                          Evenements[CaseX][CaseY].CommandeEv=Evenements[i][j].CommandeEv; Evenements[i][j].CommandeEv=null;
	                          Evenements[CaseX][CaseY].Ev=Evenements[i][j].Ev; Evenements[i][j].Ev=0;
	                          Evenements[CaseX][CaseY].Sprite=Evenements[i][j].Sprite; Evenements[i][j].Sprite =null;
	                          Evenements[CaseX][CaseY].Done=Evenements[i][j].Done; Evenements[i][j].Done=null;
	                          Evenements[CaseX][CaseY].HasToCheckPage=Evenements[i][j].HasToCheckPage; Evenements[i][j].HasToCheckPage=false;
	                          MChar=(TMChar) Evenements[CaseX][CaseY].Sprite;
	                          MChar.versx=CaseX*2;
	                          MChar.versy=CaseY*2;
	                          MChar.EventX=CaseX;
	                          MChar.EventY=CaseY;
	                          if(Evenements[CaseX][CaseY].HasToCheckPage)
	                            CheckPageEvenement(CaseX,CaseY);
	                        }
	                        else
	                        {
	                          CaseX=i; CaseY=j;
	                        }
	                      }
	                      else
	                      {
	                        CaseX=i; CaseY=j;
	                      }
	                    }
	                    Evenements[i][j].moveevenement=0;
	                  }
	                  break;
	                case 3 :
	                  Evenements[i][j].moveevenement++;
	                  NbADepasser=0;
	                  if (Evenements[i][j].evenement.get(Ev).NumAnim > 0) NbADepasser=Evenements[i][j].evenement.get(Ev).NumAnim;
	                  if (Evenements[i][j].evenement.get(Ev).Direction > 0) NbADepasser=Evenements[i][j].evenement.get(Ev).Direction;
	                  if (Evenements[i][j].moveevenement >= NbADepasser)
	                    Evenements[i][j].moveevenement =0;
	                  break;
	                case 2 :
	                  Evenements[i][j].moveevenement++;
	                  if ((Evenements[i][j].evenement.get(Ev).Vitesse==0) ||
	                  (Evenements[i][j].moveevenement > Util.random(20 / Math.abs(Evenements[i][j].evenement.get(Ev).Vitesse))))
	                  {
	                    if (Evenements[i][j].evenement.get(Ev).Vitesse>=0)
	                    {
	                      switch(Util.random(4))
	                      {
	                        case 0 : CaseY--; break;
	                        case 1 : CaseX++; break;
	                        case 2 : CaseY++; break;
	                        case 3 : CaseX--; break;
	                      }
	                    }
	                    else
	                    {
	                      CaseX=Util.random(maxx);
	                      CaseY=Util.random(maxy);
	                    }
	                    if ((CaseX >= 0) && (CaseX <= maxx)
	                      && (CaseY >= 0) && (CaseY <= maxy))
	                    {
	                      trouve=true;
	                      if (Evenements[i][j].evenement.get(Ev).Vitesse>=0)
	                      {
	                        if (CurrentMap.cases[CaseX][CaseY].X1 > 0)
	                          trouve=block[0][CurrentMap.cases[CaseX][CaseY].X1-1][CurrentMap.cases[CaseX][CaseY].Y1-1];
	                        if ((CurrentMap.cases[CaseX][CaseY].X2 > 0) && (trouve==true))
	                          trouve=block[1][CurrentMap.cases[CaseX][CaseY].X2-1][CurrentMap.cases[CaseX][CaseY].Y2-1];
	                      }
	                      if ((Evenements[CaseX][CaseY].evenement==null) && (trouve))
	                      {
	                        Evenements[CaseX][CaseY].evenement=Evenements[i][j].evenement; Evenements[i][j].evenement=null;
	                        Evenements[CaseX][CaseY].CondDecl=Evenements[i][j].CondDecl; Evenements[i][j].CondDecl=null;
	                        Evenements[CaseX][CaseY].CommandeEv=Evenements[i][j].CommandeEv; Evenements[i][j].CommandeEv=null;
	                        Evenements[CaseX][CaseY].Ev=Evenements[i][j].Ev; Evenements[i][j].Ev=0;
	                        Evenements[CaseX][CaseY].Sprite=Evenements[i][j].Sprite; Evenements[i][j].Sprite =null;
	                        Evenements[CaseX][CaseY].Done=Evenements[i][j].Done; Evenements[i][j].Done=null;
	                        Evenements[CaseX][CaseY].HasToCheckPage=Evenements[i][j].HasToCheckPage; Evenements[i][j].HasToCheckPage=false;
	                        MChar=(TMChar) Evenements[CaseX][CaseY].Sprite;
	                        MChar.versx=CaseX*2;
	                        MChar.versy=CaseY*2;
	                        MChar.EventX=CaseX;
	                        MChar.EventY=CaseY;
	                        if (Evenements[CaseX][CaseY].HasToCheckPage)
	                          CheckPageEvenement(CaseX,CaseY);
	                      }
	                      else
	                      {
	                        CaseX=i; CaseY=j;
	                      }
	                    }
	                    else
	                    {
	                      CaseX=i; CaseY=j;
	                    }
	                    Evenements[i][j].moveevenement=0;
	                  }
	              }
	            }
	          }
	          else
	          {
	            if (Evenements[i][j].Sprite != null)
	            {
	              if (Evenements[i][j].Sprite.isDead==false)
	                Evenements[i][j].Sprite.Kill();
	              Evenements[i][j].Sprite=null;
	            }
	          }
	          if ((!Dead) && (!HasChangedMap) && (!IsInEvent) && (!IsInInputString))
	          {
	            StopBoucle=false;
	            for (l=0;l<Evenements[CaseX][CaseY].CondDecl.get(Ev).size();l++)
	            {
	              // ne prend en compte que les elements variable
	                 if ((AutoEvent==false) &&
	                    ((Evenements[CaseX][CaseY].CondDecl.get(Ev).get(l).compareTo("Automatique")==0) ||
	                     (((Evenements[CaseX][CaseY].CondDecl.get(Ev).get(l).compareTo("Auto une seul fois")==0) && (Evenements[CaseX][CaseY].Done[Ev]==false)))))
	                 {
	                   AutoEvent=true;
	                   StopBoucle=true;
	                   CondDeclenche=Evenements[CaseX][CaseY].CondDecl.get(Ev).get(l);
	                   Evenements[CaseX][CaseY].Done[Ev]=true;
	                 }
	            }
	            if (StopBoucle==true)
	            {
	               DoEvenements(CaseX,CaseY,Ev,Evenements[CaseX][CaseY].CommandeEv.get(Ev),0);
	            }
	          }
	        }
	}
	private void ChangeCaseEvent(int i,int j,int CaseX,int CaseY)
	{
	  if (Evenements[CaseX][CaseY].evenement==null)
	  {
	    if ((AttenteEvenement > 0) && (EventAttX==i) && (EventAttY==j))
	    {
	      EventAttX=CaseX;
	      EventAttY=CaseY;
	    }
	    Evenements[CaseX][CaseY].evenement=Evenements[i][j].evenement; Evenements[i][j].evenement=null;
	    Evenements[CaseX][CaseY].CondDecl=Evenements[i][j].CondDecl; Evenements[i][j].CondDecl=null;
	    Evenements[CaseX][CaseY].CommandeEv=Evenements[i][j].CommandeEv; Evenements[i][j].CommandeEv=null;
	    Evenements[CaseX][CaseY].Ev=Evenements[i][j].Ev; Evenements[i][j].Ev=0;
	    Evenements[CaseX][CaseY].Sprite=Evenements[i][j].Sprite; Evenements[i][j].Sprite=null;
	    Evenements[CaseX][CaseY].Done=Evenements[i][j].Done; Evenements[i][j].Done=null;
	    Evenements[CaseX][CaseY].HasToCheckPage=Evenements[i][j].HasToCheckPage; Evenements[i][j].HasToCheckPage=false;
	    if (Evenements[CaseX][CaseY].Sprite!=null)
	    {
	    	TMChar MChar=(TMChar) Evenements[CaseX][CaseY].Sprite;
	        MChar.EventX=CaseX;
	        MChar.EventY=CaseY;
	    }
	    if ((AttenteEvenement==0) && (Evenements[CaseX][CaseY].HasToCheckPage))
	        CheckPageEvenement(CaseX,CaseY);
	  }
	}

	private MonstreGame FindMonstreBySprite(Sprite sprite)
	{
	  boolean trouve=false;
	  int i=0;
	  MonstreGame Result = null;
	  while ((i < ListeMonstre.size()) && (trouve==false))
	  {
	    if (ListeMonstre.get(i).sprite==sprite)
	    {
	    		Result=ListeMonstre.get(i);
	    		trouve = true;
	    }
	    i++;
	  }
	  return Result;
	}

	private class SelectMChar extends Sprite {
		Rectangle DestRect;
		int Clignote,Sens,Action,attente;
		Sprite.idSprite IdToScan;
		TMChar Parent;
		public SelectMChar(JFrame _Source,int action,Sprite.idSprite idtoscan)
		{
			super("",(int)(24*Zoom),(int)(32*Zoom),_Source);
			Image=CreateWindow((int)(24*Zoom), (int)(32*Zoom), true);
			Action=action;
			IdToScan=idtoscan;
			DestRect=new Rectangle();
		    z=(int) ((240*Zoom)+(150*Zoom));
			IsInSelectMChar=true;
			Sens=1;
			Clignote=0;
			attente=0;
		}
		private int ChercheSpell()
		{
			int i;
			boolean trouve;
		    int Result=-1;
			i=0; trouve=false;
			while ((i<100) && (trouve==false))
			{
				if (PlayerInfo.OwnSpell[i]==MagieUsed+1)
				{
					Result=i;
					trouve=true;
				}
				i++;
			}
			return Result;
		}
		private boolean CheckPath(int X2,int Y2)
		{
		  int X1,Y1;
		  boolean access;
		  int compx,compy;
		  compx=-8; compy=4;
		  X1=(int) Math.round((((PlayerInfo.pmapx * 16f)) + prealx+ compx) / 32f);
		  Y1=(int) Math.round((((PlayerInfo.pmapy * 16f)) + prealy+compy) / 32f);
		  X2=X2 / 2;
		  Y2=Y2 / 2;
		  access=true;
		  while ((X1!=X2) || (Y1!=Y2))
		  {
		    if (Math.abs(X1-X2)>=Math.abs(Y1-Y2))
		    {
		      if (X1>X2) X1--;
		      else X1++;
		    }
		    else
		    {
		      if (Y1>Y2) Y1--;
		      else Y1++;
		    }
		    if (CurrentMap.cases[X1][Y1].X1 > 0)
		        access=block[0][CurrentMap.cases[X1][Y1].X1-1][CurrentMap.cases[X1][Y1].Y1-1];
		    if ((CurrentMap.cases[X1][Y1].X2 > 0) && (access==true))
		        access=block[1][CurrentMap.cases[X1][Y1].X2-1][CurrentMap.cases[X1][Y1].Y2-1];
		    if ((Evenements[X1][Y1].evenement != null) && (access==true))
		    	if (Evenements[X1][Y1].Ev>=0)
		    		if (Evenements[X1][Y1].evenement.get(Evenements[X1][Y1].Ev).Z!=2)
		    			access=!Evenements[X1][Y1].evenement.get(Evenements[X1][Y1].Ev).Bloquant;
		    if (access==false)
		    {
		      return false;
		    }
		  }
		  return true;
		}
		
		private TMChar ChooseSprite(TMChar CurrentSprite,Sprite.idSprite IdToScan,int Sens,boolean CheckDead,boolean CheckRoot)
		{
		  int i , CaseX , CaseX2, CaseY, CaseY2;
		  TMChar TempSprite,MChar;
		  boolean Ok;
		  CaseX= PlayerInfo.pmapx+Detect+1; CaseY=PlayerInfo.pmapy+Detect+1; Ok=false;
		  TempSprite=null; CaseX2=0; CaseY2=0;
		  if (CurrentSprite!=null)
		  {
		    if ((CurrentSprite.ID==IdToScan)
		    &&(((CurrentSprite.mmapx - PlayerInfo.pmapx)>NDetect+8) &&
		       ((CurrentSprite.mmapx - PlayerInfo.pmapx)<Detect-8) &&
		       ((CurrentSprite.mmapy - PlayerInfo.pmapy)>NDetect+8) &&
		       ((CurrentSprite.mmapy - PlayerInfo.pmapy)<Detect-8) &&
		       (CurrentSprite.Dead==CheckDead)))
		    {
		        if ((CheckRoot) && ((IdToScan==Sprite.idSprite.idMChar) || (IdToScan==Sprite.idSprite.idMonstre)))
		        {
		          if (CheckPath(CurrentSprite.mmapx,CurrentSprite.mmapy))
		            Ok=true;
		          else
		          {
		            Ok=false;
		            CurrentSprite=null;
		          }
		        }
		        else
		          Ok=true;
		    }
		  }
		  else
		  {
		    Sens=0;
		  }

		  if ((Sens!=0) && (CurrentSprite!=null))
		  {
		    TempSprite=CurrentSprite;
		    CaseX =CurrentSprite.mmapx;
		    CaseY =CurrentSprite.mmapy;
		    CaseX2 = CurrentSprite.mmapx;
		    CaseY2 = CurrentSprite.mmapy;
		    if (Sens==-1) CaseX2=0;
		    if (Sens==1)  CaseX2=CurrentSprite.mmapx+Detect+1;
		    if (Sens==-2) CaseY2=0;
		    if (Sens==2) CaseY2=CurrentSprite.mmapy+Detect+1;
		    CurrentSprite=null;
		  }

		  if ((Ok==false) || (Sens!=0))
		  {
		    for (i=0;i<SpriteE.Sprites.size();i++)
		    {
		      if (SpriteE.Sprites.get(i).ID==IdToScan)
		      {
		        if (MagieUsed>=0)
		        {
		         	  Ok=true;
			          if ((CheckRoot) && ((IdToScan==Sprite.idSprite.idMChar) || (IdToScan==Sprite.idSprite.idMonstre)))
			          {
			        	  MChar=(TMChar) SpriteE.Sprites.get(i);
			        	  if (CheckPath(MChar.mmapx,MChar.mmapy))
			        		  Ok=true;
			              else
			            	Ok=false;
			          }
		        }
		        if (Ok)
		        {
		        	MChar=(TMChar) SpriteE.Sprites.get(i);
 		            if (((MChar.mmapx - PlayerInfo.pmapx)>NDetect+8) &&
		             ((MChar.mmapx - PlayerInfo.pmapx)<Detect-8) &&
		             ((MChar.mmapy - PlayerInfo.pmapy)>NDetect+8) &&
		             ((MChar.mmapy - PlayerInfo.pmapy)<Detect-8) &&
		             (MChar.Dead==CheckDead)
		             )
		          {
		             switch(Sens)
		             {
		             	case 0 :
			                if ((Math.abs(PlayerInfo.pmapx-MChar.mmapx)<CaseX)
			                &&(Math.abs(PlayerInfo.pmapy-MChar.mmapy)<CaseY))
			                {
			                  CurrentSprite=MChar;
			                  CaseX=Math.abs(PlayerInfo.pmapx-MChar.mmapx);
			                  CaseY=Math.abs(PlayerInfo.pmapy-MChar.mmapy);
			                }
			                break;
		             	case -2 :
			                if ((MChar!=TempSprite)
			                && (MChar.mmapy < CaseY)
			                && (MChar.mmapy > CaseY2))
			                {
			                  CurrentSprite=MChar;
			                  CaseY2=MChar.mmapy;
			                }
			                break;
		             	case -1 :
			                if ((MChar!=TempSprite)
			                && (MChar.mmapx < CaseX)
			                && (MChar.mmapx > CaseX2))
			                {
			                  CurrentSprite=MChar;
			                  CaseX2=MChar.mmapx;
			                }
			                break;
		             	case 1 :
			                if ((MChar!=TempSprite)
			                && (MChar.mmapx > CaseX)
			                && (MChar.mmapx < CaseX2))
			                {
			                  CurrentSprite=MChar;
			                  CaseX2=MChar.mmapx;
			                }
			                break;
		             	case 2 :
			                if ((MChar!=TempSprite)
			                && (MChar.mmapy > CaseY)
			                && (MChar.mmapy < CaseY2))
		          			{
			                  CurrentSprite=MChar;
			                  CaseY2=MChar.mmapy;
		          			}
		             }
		          }
		        }
		      }
		    }
		  }
		  return CurrentSprite;
		}
		
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
	    	int SpellUsed;
	    	Sprite sprite;
    		if ((IsInSelectMChar==false)||(keys[KESCAPE]==KeyState.PRESSED))
    		{
    			IsInSelectMChar=false;
    			Kill();
    			return;
    		}
    		switch(Action)
    		{
    			// Magie
    			case 0 :    				
    				  if (PlayerInfo.BloqueMagie==true)
    				  {
   				        SpriteE.AddSprite(new Degat(Source, "impossible de lancer ce sort ici...", player, Color.WHITE));
    				    IsInSelectMChar=false;
    				    Kill();
    				    return;
    				  }
    				  if ((MagicObject>=0) && (PlayerInfo.Inventaire[MagicObject][0]==0))
    				  {
    				    IsInSelectMChar=false;
    				    MagicObject=-1;
    				    Kill();
    				    return;
    				  }
    				  if (general.getMagieByIndex(MagieUsed).MagieType==6)
    				  {
    				    IsInSelectMChar=false;
    				    Kill();
    				    if (CurrentMap.TypeCarte > 0)
    				    {
    				      if (PlayerInfo.CurrentMag >= general.getMagieByIndex(MagieUsed).MPNeeded)
    				      {
    				        if (MagicObject>=0)
    				        {
    				           CreateMagie(null,null,general.getMagieByIndex(MagieUsed));
    				           if (lastmonsteratt!=null)
    				        	   PlayerInfo.CurrentMag=PlayerInfo.CurrentMag-general.getMagieByIndex(MagieUsed).MPNeeded;
    				          player.Action=4;
    				          player.AttenteTotal=general.getMagieByIndex(MagieUsed).TempsIncantation;
    				          player.AttenteAttaque=0;
    				          PlayerInfo.Inventaire[MagicObject][1]--;
    				          if (PlayerInfo.Inventaire[MagicObject][1]==0)
    				          {
    				        	  PlayerInfo.Inventaire[MagicObject][0]=0;
    				        	  if (barreicone!=null)
    				        		  barreicone.Redraw();
    				          }
    				          if (fiolevie!=null)
    				        	  fiolevie.Redraw();
    				          if (fiolemana!=null)
    				        	  fiolemana.Redraw();
    				          MagicObject=-1;
    				          GerePageEvenement();
    				        }
    				        else
    				        {
    				          SpellUsed=ChercheSpell();
    				          if (SpellUsed>=0)
    				          {
       				            CreateMagie(null,null,general.getMagieByIndex(MagieUsed));
       				            if (lastmonsteratt!=null)
     				        	   PlayerInfo.CurrentMag=PlayerInfo.CurrentMag-general.getMagieByIndex(MagieUsed).MPNeeded;
    				            player.Action=4;
	      				        player.AttenteTotal=general.getMagieByIndex(MagieUsed).TempsIncantation;
	    				        player.AttenteAttaque=0;
	    				          if (fiolevie!=null)
	    				        	  fiolevie.Redraw();
	    				          if (fiolemana!=null)
	    				        	  fiolemana.Redraw();
    				          }
    				          else
   		   				        SpriteE.AddSprite(new Degat(Source, "Vous ne possédez pas ce sort...", player, Color.WHITE));
    				        }
    				      }
    				      else
   	   				        SpriteE.AddSprite(new Degat(Source, "Vous ne possédez pas assez de mana...", player, Color.WHITE));
    				    }
    				    else
     				      SpriteE.AddSprite(new Degat(Source, "impossible de lancer ce sort ici...", player, Color.WHITE));
    				    return;
    				 }
    				 boolean CheckRoot=((general.getMagieByIndex(MagieUsed).MagieType==1)||(general.getMagieByIndex(MagieUsed).MagieType==5));
    				 int Vers=0;
    				boolean CheckDead = (general.getMagieByIndex(MagieUsed).MagieType==4);
    				if ((LastSelectedCharM!=null) && (IdToScan!=Sprite.idSprite.idPLAYER))
    				{
    				    sprite=LastSelectedCharM.sprite;
    				    if ((sprite!=null) && (sprite.isDead==false))
    				    {
    				      Parent=(TMChar) sprite;
    				      IdToScan=Parent.ID;
    				    }
    				    LastSelectedCharM=null;
    				}
    				if (CheckDead) IdToScan=Sprite.idSprite.idMChar;
    				if ((keys[KUP] == KeyState.PRESSED) && (attente >= 10))
					{
    				    Vers=-2;
    				    attente= 0;
					}
    				if ((keys[KDOWN] == KeyState.PRESSED) && (attente >= 10))
    				{
    				    Vers=2;
    				    attente = 0;
    				}
    				if ((keys[KLEFT] == KeyState.PRESSED) && (attente >= 10))
    				{
    				    Vers=-1;
    				    attente = 0;
    				}
    				if ((keys[KRIGHT] == KeyState.PRESSED) && (attente >= 10))
    				{
    				    Vers=1;
    				    attente = 0;
    				}
    				if (IdToScan!=Sprite.idSprite.idPLAYER)
    				    Parent=ChooseSprite(Parent,IdToScan,Vers,CheckDead,CheckRoot);
    				  // pas trouve on essaye tout les types
    				if ((Parent!=null) || (IdToScan==Sprite.idSprite.idPLAYER))
				    {
    				    if (IdToScan==Sprite.idSprite.idPLAYER)
    				    {
    				    	x=(int) (player.x+((player.w-(24*Zoom)) / 2)-(1+(Zoom-1)));
    				    	y=(int) (player.y+((player.h-(32*Zoom)) / 2)-(4*Zoom));
    				    }
    				    else
    				    {
    				    	x=(int) (Parent.x+((Parent.w-(24*Zoom)) / 2));
    				    	y=(int) (Parent.y+((Parent.h-(32*Zoom)) / 2));
    				    }
    				    if ((IdToScan!=Sprite.idSprite.idPLAYER) && (CheckDead==false))
    				    		if ((Parent.isDead==true)||(Parent.Dead==true)) Parent=null;
				    }
    				else
    				{
   				        SpriteE.AddSprite(new Degat(Source, "Pas de cible pour le sort...", player, Color.WHITE));
    				    IsInSelectMChar=false;
    				}
    				if ((((keys[KSPACE] == KeyState.PRESSED) || (keys[KRETURN] == KeyState.PRESSED)) && (attente >= 10))
    				     || ((general.getMagieByIndex(MagieUsed).OnMonster==1) || (general.getMagieByIndex(MagieUsed).OnMonster==2)))
    			    {
    				    if (((general.getMagieByIndex(MagieUsed).OnMonster==0) && (CurrentMap.TypeCarte>0))
    				    || (general.getMagieByIndex(MagieUsed).OnMonster>0))
    				    {
    				      if (PlayerInfo.CurrentMag >= general.getMagieByIndex(MagieUsed).MPNeeded)
    				      {
    				        if ((Freeze==false) || ((Freeze==true) && (general.getMagieByIndex(MagieUsed).MagieType!=5)))
    				        {
    				          if (IdToScan==Sprite.idSprite.idPLAYER)
    				          {
    				            LastSelectedCharM=null;
    				            if (MagicObject>=0)
    				            {
   	    				              CreateMagie(null,null,general.getMagieByIndex(general.getObjetByIndex(PlayerInfo.Inventaire[MagicObject][0]).MagieAssoc-1));
    	    				          PlayerInfo.Inventaire[MagicObject][1]--;
    	    				          if (PlayerInfo.Inventaire[MagicObject][1]==0)
    	    				          {
    	    				        	  PlayerInfo.Inventaire[MagicObject][0]=0;
    	    				        	  if (barreicone!=null)
    	    				        		  barreicone.Redraw();
    	    				          }
    	    				          if (fiolevie!=null)
    	    				        	  fiolevie.Redraw();
    	    				          if (fiolemana!=null)
    	    				        	  fiolemana.Redraw();
    	    				          MagicObject=-1;
    	    				          GerePageEvenement();
    				            }
    				            else
    				            {
    				              SpellUsed=ChercheSpell();
    				              if (SpellUsed>=0)
    	       				           CreateMagie(null,null,general.getMagieByIndex(MagieUsed));
    				              else
    			   				        SpriteE.AddSprite(new Degat(Source, "Vous ne possédez pas ce sort...", player, Color.WHITE));
    				            }
    				          }
    				          else
    				          if (Parent!=null)
    				          {
    				            LastSelectedCharM=FindMonstreBySprite(Parent);
    				            if (MagicObject>=0)
    				            {
    				              CreateMagie(null,LastSelectedCharM,general.getMagieByIndex(general.getObjetByIndex(PlayerInfo.Inventaire[MagicObject][0]).MagieAssoc-1));
	    				          PlayerInfo.Inventaire[MagicObject][1]--;
	    				          if (PlayerInfo.Inventaire[MagicObject][1]==0)
	    				          {
	    				        	  PlayerInfo.Inventaire[MagicObject][0]=0;
	    				        	  if (barreicone!=null)
	    				        		  barreicone.Redraw();
	    				          }
	    				          if (fiolevie!=null)
	    				        	  fiolevie.Redraw();
	    				          if (fiolemana!=null)
	    				        	  fiolemana.Redraw();
	    				          MagicObject=-1;
	    				          GerePageEvenement();
    				            }
    				            else
    				            {
    				              SpellUsed=ChercheSpell();
    				              if (SpellUsed>=0)
    				              {
    	       				            CreateMagie(null,LastSelectedCharM,general.getMagieByIndex(MagieUsed));
    				              }
    				              else
  			   				        SpriteE.AddSprite(new Degat(Source, "Vous ne possédez pas ce sort...", player, Color.WHITE));
    				            }
    				          }
    				          PlayerInfo.CurrentMag=PlayerInfo.CurrentMag-general.getMagieByIndex(MagieUsed).MPNeeded;
    				          if (fiolemana!=null)
    				        	  fiolemana.Redraw();
    				          IsInSelectMChar=false;
    				          player.Action=2;
    				          PlaySound(PlayerInfo.SoundConcentration,"",false);
    				          player.AttenteTotal=general.getMagieByIndex(MagieUsed).TempsIncantation;
    				          player.AttenteAttaque=0;
    				        }
    				        else
		   				        SpriteE.AddSprite(new Degat(Source, "Action impossible...", player, Color.WHITE));
    				      }
    				      else
		   				        SpriteE.AddSprite(new Degat(Source, "Vous ne possédez pas assez de mana...", player, Color.WHITE));
    				    }
    				    else
	   				        SpriteE.AddSprite(new Degat(Source, "impossible de lancer ce sort ici...", player, Color.WHITE));
    				    if ((general.getMagieByIndex(MagieUsed).OnMonster==1) || (general.getMagieByIndex(MagieUsed).OnMonster==2))
    				      IsInSelectMChar=false;
    				    attente=-1;
    			    }
    				if (attente < 10) attente++;
    				break;
    		}
	    }
		public void Draw(Graphics2D g)
		{
		  DestRect.x=(int) (x - ((w-(16*Zoom)) / 2) +ScreenX);
		  DestRect.y=(int) (y - h + (8*Zoom*(h / (16*Zoom))) + ScreenY);
		  if (Sens==1)
		  {
		    Clignote++;
		    if (Transparency!=1)
		    	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		    if(Clignote>30)
		      Sens=-1;
		  }
		  else
		  {
		    Clignote--;
		    if(Clignote==0)
		      Sens=1;
		  }			
		}
	}
	
	private class Curseur extends Sprite {
		BufferedImage Image1,Image2;
		Rectangle Src,Dest;
		int Clignote,Sens;
		public int PositionX,PositionY;		
		public Curseur(JFrame _Source,double d ,int Z_)
		{
			super("",(int)(d*Zoom),(int)(13*Zoom),_Source);
			ID=Sprite.idSprite.idCurseur;
			z=Z_;
			Src=new Rectangle();
			Dest=new Rectangle();
			ImageProducer improd;
			ImageFilter cif2;
			Image TileW;
			int i;
			GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
			improd=SystemSurf.getSource();
			Image1=gc.createCompatibleImage(w,h,java.awt.Transparency.TRANSLUCENT);
			Image2=gc.createCompatibleImage(w,h,java.awt.Transparency.TRANSLUCENT);
		    Graphics2D g2d1 = Image1.createGraphics();
		    Graphics2D g2d2 = Image2.createGraphics();
		    if(Zoom==3.2 || Zoom==2.5)
			{
		    	Src.x=(int) (66*Zoom); Src.y=0; Src.width=(int) (2+(1*(Zoom-1))); Src.height=(int) (2*Zoom);
			}
		    else
		    {
		    	Src.x=(int) (66*Zoom); Src.y=0; Src.width=(int) (1+(1*(Zoom-1))); Src.height=(int) (2*Zoom);
		    }
			cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
			TileW = createImage(new FilteredImageSource(improd, cif2));	  
			for (i=2;i<Image1.getWidth()-1;i++)
			{
			  Dest.x=(int) (i*Zoom); Dest.y= 0;
			  g2d1.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
			  Dest.x=(int) (i*Zoom); Dest.y= (int)(h-(2*Zoom));
			  g2d1.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
			}
			if(Zoom==3.2 || Zoom==2.5)
			{
				Src.x=(int) (98*Zoom); Src.y=0; Src.width=(int) (2+(1*(Zoom-1))); Src.height=(int) (2*Zoom);
			}
			else
			{
				Src.x=(int) (98*Zoom); Src.y=0; Src.width=(int) (1+(1*(Zoom-1))); Src.height=(int) (2*Zoom);
			}
			cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
			TileW = createImage(new FilteredImageSource(improd, cif2));	  
			for (i=2;i<Image2.getWidth()-1;i++)
			{
			    Dest.x=(int) (i*Zoom); Dest.y= 0;
				g2d2.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
			    Dest.x=(int) (i*Zoom); Dest.y= (int) (h-(2*Zoom));
				g2d2.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
			}
			if(Zoom==3.2 || Zoom==2.5)
			{
				Src.x=(int) (64*Zoom); Src.y=(int) (2*Zoom); Src.width=(int) (2*Zoom); Src.height=(int) (2+(1*(Zoom-1)));
			}
			else
			{
				Src.x=(int) (64*Zoom); Src.y=(int) (2*Zoom); Src.width=(int) (2*Zoom); Src.height=(int) (1+(1*(Zoom-1)));
			}
			cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
			TileW = createImage(new FilteredImageSource(improd, cif2));	  
			for (i=2;i<Image1.getHeight()-1;i++)
			{
			   Dest.x=0; Dest.y= (int) (i*Zoom);
			   g2d1.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
			   Dest.x=(int) (Image1.getWidth()-(2*Zoom)); Dest.y = (int) (i*Zoom);
			   if(Zoom==3.2) Dest.x=(int) (Image1.getWidth()-(2*Zoom))-1;
			   g2d1.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
			}
			if(Zoom==3.2 || Zoom==2.5)
			{
				Src.x=(int) (96*Zoom); Src.y=(int) (2*Zoom); Src.width=(int) (2*Zoom); Src.height=(int) (2+(1*(Zoom-1)));
			}
			else
			{
				Src.x=(int) (96*Zoom); Src.y=(int) (2*Zoom); Src.width=(int) (2*Zoom); Src.height=(int) (1+(1*(Zoom-1)));
			}
			cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
			TileW = createImage(new FilteredImageSource(improd, cif2));	  
			for (i=2;i<Image2.getHeight()-1;i++)
			{
			    Dest.x=0; Dest.y= (int) (i*Zoom);
				g2d2.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
			    Dest.x=(int) (Image2.getWidth()-(2*Zoom)); Dest.y= (int) (i*Zoom);
				g2d2.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
			}
		  //les coins
		  Src.x=(int) (64*Zoom); Src.y=0; Src.width=(int) (2*Zoom); Src.height=(int) (2*Zoom);  Dest.x=0; Dest.y= 0;
		  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
		  TileW = createImage(new FilteredImageSource(improd, cif2));	  
		  g2d1.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
		  Src.x=(int) (94*Zoom); Src.y=0; Src.width=(int) (2*Zoom); Src.height=(int) (2*Zoom);  Dest.x=(int) (Image1.getWidth()-(2*Zoom)); Dest.y=0;
		  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
		  TileW = createImage(new FilteredImageSource(improd, cif2));	  
		  g2d1.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
		  Src.x=(int) (64*Zoom); Src.y=(int) (28*Zoom); Src.width=(int) (2*Zoom); Src.height=(int) (2*Zoom);  Dest.x=0; Dest.y=(int) (Image1.getHeight()-(2*Zoom));
		  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
		  TileW = createImage(new FilteredImageSource(improd, cif2));	  
		  g2d1.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
		  Src.x=(int) (94*Zoom); Src.y=(int) (28*Zoom); Src.width=(int) (2*Zoom); Src.height=(int) (2*Zoom);  Dest.x=(int) (Image1.getWidth()-(2*Zoom)); Dest.y=(int) (Image1.getHeight()-(2*Zoom));
		  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
		  TileW = createImage(new FilteredImageSource(improd, cif2));	  
		  g2d1.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
		  Src.x=(int) (96*Zoom); Src.y=0; Src.width=(int) (2*Zoom); Src.height=(int) (2*Zoom);  Dest.x=0; Dest.y= 0;
		  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
		  TileW = createImage(new FilteredImageSource(improd, cif2));	  
		  g2d2.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
		  Src.x=(int) (126*Zoom); Src.y=0; Src.width=(int) (2*Zoom); Src.height=(int) (2*Zoom);  Dest.x=(int) (Image1.getWidth()-(2*Zoom)); Dest.y=0;
		  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
		  TileW = createImage(new FilteredImageSource(improd, cif2));	  
		  g2d2.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
		  Src.y=(int) (96*Zoom); Src.y=(int) (28*Zoom); Src.width=(int) (2*Zoom); Src.height=(int) (2*Zoom);  Dest.x=0; Dest.y=(int) (Image1.getHeight()-(2*Zoom));
		  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
		  TileW = createImage(new FilteredImageSource(improd, cif2));	  
		  g2d2.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
		  Src.x=(int) (126*Zoom); Src.y=(int) (28*Zoom); Src.width=(int) (2*Zoom); Src.height=(int) (2*Zoom);  Dest.x=(int) (Image1.getWidth()-(2*Zoom)); Dest.y=(int) (Image1.getHeight()-(2*Zoom));
		  cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
		  TileW = createImage(new FilteredImageSource(improd, cif2));	  
		  g2d2.drawImage(TileW,Dest.x,Dest.y,Src.width,Src.height, null);
		  x=(int) (3*Zoom);
		  PositionX=0;
		  PositionY=0;
		  Clignote=0;
		  Sens=1;
		  g2d1.dispose();
		  g2d2.dispose();
		}
		public void Draw(Graphics2D g)
		{
		  Dest.x= x+ScreenX;
		  Dest.y= y+ScreenY;
		  if (Sens==1)
		  {
		    Clignote++;
		    if (Transparency!=1)
		    	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			DrawImage(g,Image1,Dest.x,Dest.y,w,h, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		    if(Clignote>30)
		      Sens=-1;
		  }
		  else
		  {
		    Clignote--;
		    if (Transparency!=1)
		    	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			DrawImage(g,Image2,Dest.x,Dest.y,w,h, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		    if(Clignote==0)
		      Sens=1;
		  }			
		}
	}
	
	public class EvInputString extends Sprite {
	    Rectangle DestRect;
	    BufferedImage textesurf,Surface;
	    public EvInputString(JFrame _Source,String Question)
	    {
			super("",(int)(320*Zoom),(int)(30*Zoom),_Source);
			GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
	    	DestRect=new Rectangle();
	    	textesurf=CreateWindow((int)(320*Zoom),(int)(30*Zoom),false);
	        Graphics2D g2d = textesurf.createGraphics();
  	        g2d.setFont(new Font("Arial", Font.PLAIN,  (int)(10*Zoom)));
		    g2d.setColor(Color.WHITE);
	    	FontMetrics fontMetrics = g2d.getFontMetrics();
	        DestRect.x = (int) (3*Zoom);
	        DestRect.y = fontMetrics.getHeight();
		    g2d.drawString(Question,DestRect.x,DestRect.y);
	    	g2d.dispose();
	    	Surface=gc.createCompatibleImage((int)(320*Zoom),(int)(30*Zoom));
	        g2d =Surface.createGraphics();
	        g2d.drawImage(textesurf, 0,0,(int)(320*Zoom),(int)(30*Zoom),null);
	        g2d.dispose();
	    	IsInInputString=true;
	    	keyboard.setIsInputText(true);
	    	x=0;
	    	y=0;
	    	z=(int) ((240*Zoom)+(180*Zoom));
	    }
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
	    	if ((Dead)||(IsInEvent==false))
   			{
	    		IsInInputString=false;
		    	keyboard.setIsInputText(false);
	    		Kill();
	    		return;
   			}
	    	if ((IsInInputString==false) || ((keys[KRETURN]==KeyState.ONCE) && (InputString.compareTo("")!=0)))
	    	{
	    		IsInInputString=false;
		    	keyboard.setIsInputText(false);
		    	Kill();
		    	return;
	    	}
	    	if (InputString.compareTo(keyboard.getText())!=0)
	    	{
				InputString=keyboard.getText();
	    		Graphics2D g2d =Surface.createGraphics();
		        g2d.drawImage(textesurf, 0,0,(int)(320*Zoom),(int)(30*Zoom),null);
	  	        g2d.setFont(new Font("Arial", Font.PLAIN,  (int)(10*Zoom)));
			    g2d.setColor(Color.WHITE);
			    if (InputString.compareTo("")!=0)
			    {
	    	    	FontMetrics fontMetrics = g2d.getFontMetrics();
	    	        DestRect.x = (int) (3*Zoom);
	    	        DestRect.y = (int) ((10*Zoom)+fontMetrics.getHeight());
	    		    g2d.drawString(InputString,DestRect.x,DestRect.y);
			    }
		        g2d.dispose();	    			
			}
	    }
		public void Draw(Graphics2D g)
		{
		  DestRect.x= x+ScreenX;
		  DestRect.y= y+ScreenY;
		  if (Transparency!=1)
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
		  DrawImage(g,Surface,DestRect.x,DestRect.y,w,h, null);
		  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}
	
	public class EvMessage extends Sprite 
	{
	    Rectangle DestRect;
	    String Said;
	    int textedelay;
	    BufferedImage textesurf;
	    public EvMessage(JFrame _Source,String Message_)
	    {
			super("",(int)(320*Zoom),(int)(10*Zoom),_Source);
	    	int j ,k, maxlength,lgt;
	    	String temp,temp2;
	    	maxlength=65;
	    	boolean positionne;
	    	if (!Message_.startsWith("'") && !Message_.equals(""))
	    	{
	    		x=(int) (Integer.parseInt(Message_.substring(0,Message_.indexOf(",")))*Zoom);
	    		Message_=Message_.substring(Message_.indexOf(",")+1);
	    		y=(int) (Integer.parseInt(Message_.substring(0,Message_.indexOf(",")))*Zoom);
	    		Message_=Message_.substring(Message_.indexOf(",")+1);
	    		w=(int) (Integer.parseInt(Message_.substring(0,Message_.indexOf(",")))*Zoom);
	    		Message_=Message_.substring(Message_.indexOf(",")+1);
	    		h=(int) (Integer.parseInt(Message_.substring(0,Message_.indexOf(",")))*Zoom);
	    		Message_=Message_.substring(Message_.indexOf(",")+2);
	    		positionne=true;
	    	}
	    	else
	    	{
		    	x=0;
		    	y=0;
		    	if(!Message_.equals("")) Message_=Message_.substring(1);
		    	positionne=false;
	    	}
	    	Message_=Message_+"\\n";
	    	DestRect=new Rectangle();
	    	Said = Message_;
	    	k=0;
	    	while(Said.indexOf("\\n")>=0)
	    	{
	    		temp=Said.substring(0,Said.indexOf("\\n"));
	    	    k=k+((temp.length() / maxlength))+1;
	    	    Said=Said.substring(Said.indexOf("\\n")+2);
	    	}
	    	j=k+1;
	    	if (j<5) j=5;
	    	if (positionne==false)
	    		h=(int) (j*10*Zoom);
	    	textesurf=CreateWindow(w,h,false);
	        Graphics2D g2d = textesurf.createGraphics();
  	        g2d.setFont(new Font("Arial", Font.PLAIN,  (int)(10*Zoom)));
		    g2d.setColor(Color.WHITE);
	    	FontMetrics fontMetrics = g2d.getFontMetrics();
	    	Said = Message_;
	    	j=k;
	    	while(Said.indexOf("\\n")>=0)
	    	{
	    	    temp=Said.substring(0,Said.indexOf("\\n"));
	    	    Said=Said.substring(Said.indexOf("\\n")+2);
	    	    if (temp.length()<maxlength)
	    	    	lgt=temp.length();
	    	    else
	    	    	lgt=maxlength;
	    	    temp2=temp.substring(0,lgt);
	    	    if (temp.compareTo("")==0)
	    	      j--;
	    	    while (temp.compareTo("")!=0)
	    	    {
	    	        DestRect.x = (int) (3*Zoom); //+fontMetrics.stringWidth(temp2)
	    	        DestRect.y = (int) (((k-j)*10*Zoom)+fontMetrics.getHeight());
	    		    g2d.drawString(temp2,DestRect.x,DestRect.y);
	    	        j--;
	    	        if (temp.length()>maxlength)
	    	        {
	    	        	temp=temp.substring(maxlength);
	    	    	    if (temp.length()<maxlength)
	    	    	    	lgt=temp.length();
	    	    	    else
	    	    	    	lgt=maxlength;
		    	        temp2=temp.substring(0,lgt);
	    	        }
	    	        else
	    	        {
	    	        	temp="";
	    	        	temp2="";
	    	        }
	    	    }
	    	}
	    	g2d.dispose();
	    	Image=textesurf;
	    	z=(int) ((240*Zoom)+(180*Zoom));
	    	textedelay=0;
	    }
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
	    	textedelay++;
			if (textedelay>16)
			  if ((keys[KRETURN] == KeyState.ONCE) || (keys[KSPACE] == KeyState.ONCE) || (AttenteEvenement==0)||(Dead==true))
			  {
		    	  try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			    Kill();
			    SpriteEv=null;
			  }	    	
	    }
		public void Draw(Graphics2D g)
		{
		  DestRect.x= x+ScreenX;
		  DestRect.y= y+ScreenY;
		  if (Transparency!=1)
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
		  DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
		  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}

	public class ConfirmDialog extends Sprite {
		Rectangle DestRect;
	    Curseur curs;
	    int Attente,TypeBouton;
		public ConfirmDialog(JFrame _Source,String Texte,int _TypeBouton)
		{
		  super("",(int)(220*Zoom),(int)(56*Zoom),_Source);
		  z=(int) ((240*Zoom)+(210*Zoom));
		  x=(int) (50*Zoom);
		  y=(int)(92*Zoom);
		  Attente=0;
		  IsInConfirm=true;
		  DestRect=new Rectangle();
		  TypeBouton=_TypeBouton;
		  BufferedImage textesurf;
		  textesurf=CreateWindow((int)(220*Zoom),(int)(56*Zoom),false);
	      Graphics2D g2d = textesurf.createGraphics();
  	      g2d.setFont(new Font("Arial", Font.PLAIN,  (int)(10*Zoom)));
	      FontMetrics fontMetrics = g2d.getFontMetrics();
	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (2*Zoom+fontMetrics.getHeight());
   		  g2d.drawString(Texte,DestRect.x,DestRect.y);
	      switch(TypeBouton)
	      {
	      	case 0 :
	      	  DestRect.x=(int) (55*Zoom); DestRect.y=(int) (36*Zoom+fontMetrics.getHeight());
	   		  g2d.drawString("Oui",DestRect.x,DestRect.y);
		      DestRect.x=(int) (145*Zoom); DestRect.y=(int) (36*Zoom+fontMetrics.getHeight());
	   		  g2d.drawString("Non",DestRect.x,DestRect.y);
	   		  break;
	      	case 1 :
	      	  DestRect.x=(int) (69*Zoom); DestRect.y=(int) (36*Zoom+fontMetrics.getHeight());
	   		  g2d.drawString("Ok",DestRect.x,DestRect.y);
		      DestRect.x=(int) (147*Zoom); DestRect.y=(int) (36*Zoom+fontMetrics.getHeight());
	   		  g2d.drawString("Annuler",DestRect.x,DestRect.y);
	   		  break;
	      	case 2 :
	      	  DestRect.x=(int) (100*Zoom); DestRect.y=(int) (36*Zoom+fontMetrics.getHeight());
	   		  g2d.drawString("Ok",DestRect.x,DestRect.y);
	   		  break;
	      }
	      g2d.dispose();
	      Image=textesurf;
	      if (TypeBouton==1)
	    	  curs=new Curseur(_Source,70,z+1);
	      else
	    	  curs=new Curseur(_Source,45,z+1);
		  SpriteE.AddSprite(curs);
		}
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
    		if ((IsInConfirm==false)||(IsMenuActive==false))
    		{
    			if (curs!=null)
    				curs.Kill();
    			Kill();
    			IsInConfirm=false;
    			return;
    		}
    		if (curs==null)
	    	{
	    	    Attente=0;
	    	    return;
	    	}
    		if (curs.isDead)
	    	{
    			curs=null;
	    	    Attente=0;
	    	    return;
	    	}
    		if (TypeBouton<2)
    		{
		    	if ((keys[KLEFT] == KeyState.PRESSED) && (curs.PositionX > 0) && (Attente >= 10))
		    	{
		    	    curs.PositionX-=2;
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Attente = 0;
		    	}
		    	if ((keys[KRIGHT] == KeyState.PRESSED) && (curs.PositionX < 2) && (Attente >= 10))
		    	{
		    	    curs.PositionX+=2;
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Attente= 0;
		    	}
		    	if (((keys[KRETURN] == KeyState.PRESSED) || (keys[KSPACE] == KeyState.PRESSED)) && (Attente >= 10))
		    	{
		    		PlaySound(SOUND_VALIDE,"",false);
		    		IsConfirmStatus=(curs.PositionX>0) ? false : true;
		    		IsInConfirm=false;
		    		curs.Kill();
		    		Kill();
		    	}
		    	curs.x=(int) (((curs.PositionX*45)+93)*Zoom);
		    	curs.y=(int) (129*Zoom);
    		}
    		else
    		{
		    	curs.x=(int) (135*Zoom);
		    	curs.y=(int) (129*Zoom);
		    	if (((keys[KRETURN] == KeyState.PRESSED) || (keys[KSPACE] == KeyState.PRESSED)) && (Attente >= 10))
		    	{
		    		PlaySound(SOUND_VALIDE,"",false);
		    		IsInConfirm=false;
		    		curs.Kill();
		    		Kill();
		    	}
    		}
	    	if (Attente < 10) Attente++;
	    }		
		public void Draw(Graphics2D g)
		{
			DestRect.x= x+ScreenX;
			DestRect.y= y+ScreenY;
			if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}
	public class Fleche extends Sprite {
		Rectangle DestRect;
		int Attente;
		public Fleche(JFrame _Source,int X_,int Y_,int Z_, int TypeFleche)
		{
			  super("",(int)(10*Zoom),(int)(6*Zoom),_Source);
			  DestRect=new Rectangle();
  			  ImageFilter cif2;
			  ImageProducer improd=SystemSurf.getSource();
			  x = (int) (X_*Zoom);
			  y = (int) (Y_*Zoom);
			  z = Z_;
			  switch(TypeFleche)
			  {
			  	case 0 :
			  		 DestRect.x=(int) (43*Zoom); DestRect.y=(int) (9*Zoom); DestRect.width=(int) (10*Zoom); DestRect.height=(int) (6*Zoom);
		    		 cif2 =new CropImageFilter(DestRect.x, DestRect.y,DestRect.width,DestRect.height);
		    		 Image = createImage(new FilteredImageSource(improd, cif2));	  
			  		break;
			  	case 1 :
			  		 DestRect.x=(int) (43*Zoom); DestRect.y=(int) (17*Zoom); DestRect.width=(int) (10*Zoom); DestRect.height=(int) (6*Zoom);
		    		 cif2 =new CropImageFilter(DestRect.x, DestRect.y,DestRect.width,DestRect.height);
		    		 Image = createImage(new FilteredImageSource(improd, cif2));
		    		 break;
			  }
			  Attente=0;
		}
		public void Draw(Graphics2D g)
		{
			  if (Attente>32)
			  {
				  DestRect.x= x+ScreenX;
				  DestRect.y= y+ScreenY;
				  if (Transparency!=1)
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
				  DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
				  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
			  }
			  Attente++;
			  if (Attente>64) Attente=0;
		}
	}
	public class InventaireEquipe extends Sprite {
		Rectangle DestRect;
	    Curseur curs;
	    int Attente,InvPos,Objet1,Objet2;
	    InventaireMenu parent;
		public InventaireEquipe(JFrame _Source,int invpos_,InventaireMenu invm)
		{
		  super("",(int)(240*Zoom),(int)(120*Zoom),_Source);
		  z=(int) ((240*Zoom)+(198*Zoom));
		  x=(int) (40*Zoom);
		  y=(int) (60*Zoom);
		  Attente=0;
		  InvPos=invpos_;
		  parent=invm;
		  switch(general.getObjetByIndex(PlayerInfo.Inventaire[InvPos][0]-1).ObjType)
		  {
		  		case 0 :
		  			Objet1=PlayerInfo.Arme;
		  			break;
		  		case 2 :
		  			Objet1=PlayerInfo.Arme;
		  			Objet2=PlayerInfo.Bouclier;
		  			break;
		  		case 3 :
		  			Objet1=PlayerInfo.Casque;
		  			break;
		  		case 4 :
		  			Objet1=PlayerInfo.Armure;
		  			break;
		  		case 5 :
		  			Objet1=PlayerInfo.Bouclier;
		  			if (PlayerInfo.Arme>0)
		  				if (general.getObjetByIndex(PlayerInfo.Arme-1).ObjType==2)
		  					Objet2=PlayerInfo.Arme;
		  			break;
		  }	  
		  DestRect=new Rectangle();
		  BufferedImage textesurf;
		  textesurf=CreateWindow((int)(240*Zoom),(int)(120*Zoom),false);
	      Graphics2D g2d = textesurf.createGraphics();
  	      g2d.setFont(new Font("Arial", Font.PLAIN,  (int)(10*Zoom)));
	      FontMetrics fontMetrics = g2d.getFontMetrics();
	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (2*Zoom+fontMetrics.getHeight());
	      if (Objet1>0)
	    	  g2d.drawString("Ancien : "+general.getObjetByIndex(Objet1-1).Name,DestRect.x,DestRect.y);
	      else
	    	  g2d.drawString("Ancien : aucun",DestRect.x,DestRect.y);
	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
    	  g2d.drawString("Nouveau : "+general.getObjetByIndex(PlayerInfo.Inventaire[InvPos][0]-1).Name,DestRect.x,DestRect.y);
    	  int posy=28; boolean ok;
    	  for (int i=0;i<general.getStatsBase().size();i++)
    	  {
    		  ok=false;
    		  if (Objet1>0)
    			  if (general.getObjetByIndex(Objet1-1).Stats.get(i)>0)
    				  ok=true;
    		  if ((general.getObjetByIndex(PlayerInfo.Inventaire[InvPos][0]-1).Stats.get(i)>0)||(ok==true))
    		  {
    			  	  DestRect.x=(int) (5*Zoom); DestRect.y=(int) (posy*Zoom+fontMetrics.getHeight());
    				  g2d.drawString(general.getStatsBase().get(i)+" : "+general.getObjetByIndex(Objet1-1).Stats.get(i)+" -> "+general.getObjetByIndex(PlayerInfo.Inventaire[InvPos][0]-1).Stats.get(i),DestRect.x,DestRect.y);
    				  posy+=13;
    		  }
    	  }

	      DestRect.x=(int) (120*Zoom); DestRect.y=(int) (28*Zoom+fontMetrics.getHeight());
    	  g2d.drawString("Attaque : "+(Objet1 > 0 ? general.getObjetByIndex(Objet1-1).Attaque : "0")+" -> "+general.getObjetByIndex(PlayerInfo.Inventaire[InvPos][0]-1).Attaque,DestRect.x,DestRect.y);
	      DestRect.x=(int) (120*Zoom); DestRect.y=(int) (41*Zoom+fontMetrics.getHeight());
    	  g2d.drawString("Défense : "+(Objet1 > 0 ? general.getObjetByIndex(Objet1-1).Defense : "0")+" -> "+general.getObjetByIndex(PlayerInfo.Inventaire[InvPos][0]-1).Defense,DestRect.x,DestRect.y);
	      DestRect.x=(int) (120*Zoom); DestRect.y=(int) (54*Zoom+fontMetrics.getHeight());
    	  g2d.drawString("Dégat : "+(Objet1 > 0 ? general.getObjetByIndex(Objet1-1).PV : "0")+" -> "+general.getObjetByIndex(PlayerInfo.Inventaire[InvPos][0]-1).PV,DestRect.x,DestRect.y);
	      DestRect.x=(int) (120*Zoom); DestRect.y=(int) (67*Zoom+fontMetrics.getHeight());
    	  g2d.drawString("Magie : "+(Objet1 > 0 ? general.getObjetByIndex(Objet1-1).PM : "0")+" -> "+general.getObjetByIndex(PlayerInfo.Inventaire[InvPos][0]-1).PM,DestRect.x,DestRect.y);
	      DestRect.x=(int) (48*Zoom); DestRect.y=(int) (100*Zoom+fontMetrics.getHeight());
    	  g2d.drawString("Equiper",DestRect.x,DestRect.y);
	      DestRect.x=(int) (140*Zoom); DestRect.y=(int) (100*Zoom+fontMetrics.getHeight());
    	  g2d.drawString("Annuler",DestRect.x,DestRect.y);
    	  g2d.dispose();
	      Image=textesurf;
    	  curs=new Curseur(_Source,45,z+1);
		  SpriteE.AddSprite(curs);
		  IsInvEquipActive=true;
		}
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
    		if ((IsInvEquipActive==false)||(IsMenuActive==false))
    		{
    			if (curs!=null)
    				curs.Kill();
    			Kill();
    			IsInvEquipActive=false;
    			return;
    		}
    		if ((curs==null)||(IsInConfirm==true))
	    	{
	    	    Attente=0;
	    	    return;
	    	}
    		if (curs.isDead)
	    	{
    			curs=null;
	    	    Attente=0;
	    	    return;
	    	}
	    	if ((keys[KLEFT] == KeyState.PRESSED) && (curs.PositionX > 0) && (Attente >= 10))
	    	{
	    	    curs.PositionX-=2;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente = 0;
	    	}
	    	if ((keys[KRIGHT] == KeyState.PRESSED) && (curs.PositionX < 2) && (Attente >= 10))
	    	{
	    	    curs.PositionX+=2;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente= 0;
	    	}
	    	if (((keys[KRETURN] == KeyState.PRESSED) || (keys[KSPACE] == KeyState.PRESSED)) && (Attente >= 10))
	    	{
	    		PlaySound(SOUND_VALIDE,"",false);
	    		if (curs.PositionX==0)
	    		{
	    			int i=0; boolean ok=false; int posok[];
	    			posok=new int[2];
    				if (Objet1>0)
    				{
	    				while ((i<100)&&(ok==false))
	    				{
	    					if (PlayerInfo.Inventaire[i][0]==Objet1)
	    					{
	    						ok=true;
	    						posok[0]=i;
	    					}
	    					i++;
	    				}
	    				if (ok==false)
	    				{
		    				while ((i<100)&&(ok==false))
		    				{
		    					if (PlayerInfo.Inventaire[i][0]==0)
		    					{
		    						ok=true;
		    						posok[0]=i;
		    					}
		    					i++;
		    				}		    					
	    				}
    				}
    				if (Objet2>0)
    				{
    					ok=false;
	    				while ((i<100)&&(ok==false))
	    				{
	    					if (PlayerInfo.Inventaire[i][0]==Objet2)
	    					{
	    						ok=true;
	    						posok[1]=i;
	    					}
	    					i++;
	    				}
	    				if (ok==false)
	    				{
		    				while ((i<100)&&(ok==false))
		    				{
		    					if (PlayerInfo.Inventaire[i][0]==0)
		    					{
		    						ok=true;
		    						posok[1]=i;
		    					}
		    					i++;
		    				}		    					
	    				}
    				}
    				if ((Objet1==0)&&(Objet2==0))
    					ok=true;    				
    		        if (ok==true)
    		        {
    		        	if (Objet1>0)
    		        	{
    		        		for(i=0;i<general.getStatsBase().size();i++)
    		        		{
    		        			PlayerInfo.Stats.setProperty(general.getStatsBase().get(i), Integer.toString(Integer.parseInt(PlayerInfo.Stats.getProperty(general.getStatsBase().get(i),"0"))-general.getObjetByIndex(Objet1-1).Stats.get(i)));
    		        		}
    		        		if (PlayerInfo.Inventaire[posok[0]][0]>0)
    		        			PlayerInfo.Inventaire[posok[0]][1]++;
    		        		else
    		        		{
    		        			PlayerInfo.Inventaire[posok[0]][0]=(short) Objet1;
    		        			PlayerInfo.Inventaire[posok[0]][1]=1;
    		        		}
    		        	}
    		        	if (Objet2>0)
    		        	{
    		        		for(i=0;i<general.getStatsBase().size();i++)
    		        		{
    		        			PlayerInfo.Stats.setProperty(general.getStatsBase().get(i), Integer.toString(Integer.parseInt(PlayerInfo.Stats.getProperty(general.getStatsBase().get(i),"0"))-general.getObjetByIndex(Objet2-1).Stats.get(i)));
    		        		}
    		        		if (PlayerInfo.Inventaire[posok[1]][0]>0)
    		        			PlayerInfo.Inventaire[posok[1]][1]++;
    		        		else
    		        		{
    		        			PlayerInfo.Inventaire[posok[1]][0]=(short) Objet2;
    		        			PlayerInfo.Inventaire[posok[1]][1]=1;
    		        		}
    		        	}
    		        	switch(general.getObjetByIndex(PlayerInfo.Inventaire[InvPos][0]-1).ObjType)
    		        	{
    		        		case 1 :
    		        		case 2 :
    		        			PlayerInfo.Arme=PlayerInfo.Inventaire[InvPos][0];
    		        			break;
    		        		case 3 :
    		        			PlayerInfo.Casque=PlayerInfo.Inventaire[InvPos][0];
    		        			break;
    		        		case 4 :
    		        			PlayerInfo.Armure=PlayerInfo.Inventaire[InvPos][0];
    		        			break;
    		        		case 5 :
    		        			PlayerInfo.Bouclier=PlayerInfo.Inventaire[InvPos][0];
    		        			break;
    		        	}
    		        	if (PlayerInfo.Inventaire[InvPos][1]>1)
    		        		PlayerInfo.Inventaire[InvPos][1]--;
    		        	else
    		        	{
    		        		PlayerInfo.Inventaire[InvPos][0]=0;
    		        		PlayerInfo.Inventaire[InvPos][1]=0;
    		        	}
    		        	Attente=-1;
        		        RecalculCarac();
        		        GerePageEvenement();
    		        }
	    		}
	    		IsInvEquipActive=false;
	    		parent.Redraw();
	    		curs.Kill();
	    		Kill();
	    	}
	    	curs.x=(int) (((curs.PositionX*45)+83)*Zoom);
	    	curs.y=(int) (161*Zoom);
	    	if (Attente < 10) Attente++;
	    }		
		public void Draw(Graphics2D g)
		{
			DestRect.x= x+ScreenX;
			DestRect.y= y+ScreenY;
			if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}
	public void UtiliseObjet(int PosInventaire)
	{
		if (general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).ObjType==0)
		{
		    // on vérifie d'abord les stats min;
			int i=0;
			for(i=0;i<general.getStatsBase().size();i++)
			{
				if (general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).StatsMin.get(i)>Integer.parseInt(PlayerInfo.Stats.getProperty(general.getStatsBase().get(i),"0")))
					return;
			}
			MagicObject=-1;
		    if (general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).PV>0)
		    {
		        PlayerInfo.Vie=PlayerInfo.Vie+general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).PV;
		        if (PlayerInfo.Vie > PlayerInfo.VieMax) PlayerInfo.Vie=PlayerInfo.VieMax;
		        SpriteE.AddSprite(new Degat(this, Integer.toString(general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).PV), player, Color.GREEN));
		        if (IsInvActive==false)
		        {
		          SpriteE.AddSprite(new Animation(this,player,"Chipset\\heal.png",((CentreX+PlayerInfo.CentreX-(ScX / 8))*8),((CentreY+PlayerInfo.CentreY-(ScY / 8))*8),0,0,48,56,0,42,255));
		          PlaySound(DEFAULT_LVLUP_SOUND,"",false);
		          player.AttenteAttaque=0;
		          player.Action=3;
		        }
		    }
		    else if (general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).PM>0)
		    {
		      PlayerInfo.CurrentMag=PlayerInfo.CurrentMag+general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).PM;
		      if (PlayerInfo.CurrentMag > PlayerInfo.MagMax) PlayerInfo.CurrentMag=PlayerInfo.MagMax;
		      SpriteE.AddSprite(new Degat(this,Integer.toString(general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).PM), player, Color.BLUE));
		      if (IsInvActive==false)
		      {
		          SpriteE.AddSprite(new Animation(this,player,"Chipset\\mana.png",((CentreX+PlayerInfo.CentreX-(ScX / 8))*8),((CentreY+PlayerInfo.CentreY-(ScY / 8))*8),0,0,48,56,0,42,255));
		          PlaySound(DEFAULT_LVLUP_SOUND,"",false);
		          player.AttenteAttaque=0;
		          player.Action=3;
		      }
		    }
		    for(i=0;i<general.getStatsBase().size();i++)
		    {
		    	if (general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).Stats.get(i)>0)
		    		PlayerInfo.Stats.setProperty(general.getStatsBase().get(i), Integer.toString(Integer.parseInt(PlayerInfo.Stats.getProperty(general.getStatsBase().get(i),"0"))+general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).Stats.get(i)));
		    }
		    if ((PlayerInfo.BloqueMagie==false)&&(general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).MagieAssoc>0))
		    {
 				MagicObject=PosInventaire;
 				MagieUsed=general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).MagieAssoc-1;
 				if (general.getMagieByIndex(MagieUsed).OnMonster==0)
 					SpriteE.AddSprite(new SelectMChar(this,0,Sprite.idSprite.idMonstre));
		        else
			        SpriteE.AddSprite(new SelectMChar(this,0,Sprite.idSprite.idPLAYER));
		        IsInvActive=false;
		        IsMenuActive=false;		      
		    }
		    if (PlayerInfo.Inventaire[PosInventaire][1]>1)
		    	PlayerInfo.Inventaire[PosInventaire][1]--;
		    else
		    {
		    	PlayerInfo.Inventaire[PosInventaire][0]=0;
		    	PlayerInfo.Inventaire[PosInventaire][1]=0;
		    }
		    if (fiolevie!=null)
		    	fiolevie.Redraw();
		    if (fiolemana!=null)
		    	fiolemana.Redraw();
		    //if (Stats<>nil) then Stats.Redraw;
	 	    GerePageEvenement();
		}
		 else
		 if (general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).ObjType==7)
		 {
			 boolean ok=false; int i=0;
			 while((i<100)&&(ok==false))
			 {
				 if (PlayerInfo.OwnSpell[i]==general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).MagieAssoc)
					 ok=true;
			 }
			 if (ok==false)
			 {
				 i=0;
				 while((i<100)&&(ok==false))
				 {
					 if (PlayerInfo.OwnSpell[i]==0)
					 {
						 PlayerInfo.OwnSpell[i]=general.getObjetByIndex(PlayerInfo.Inventaire[PosInventaire][0]-1).MagieAssoc;
						 ok=true;
					 }
				 }				 
			 }
			 if (PlayerInfo.Inventaire[PosInventaire][1]>1)
		    	PlayerInfo.Inventaire[PosInventaire][1]--;
		    else
		    {
		    	PlayerInfo.Inventaire[PosInventaire][0]=0;
		    	PlayerInfo.Inventaire[PosInventaire][1]=0;
		    }
			if (fiolevie!=null)
				fiolevie.Redraw();
			if (fiolemana!=null)
				fiolemana.Redraw();			 
			GerePageEvenement();
		 }
	}
	public class MagasinEv extends Sprite {
		Rectangle DestRect;
	    Curseur curs;
	    int Etape,Attente,NbPlaceInventaire,Position;
	    Fleche FlecheHaute,FlecheBasse;
	    short[][] items,vente;
	    BufferedImage blocsurf,window;
		String[] Said;
		public MagasinEv(JFrame _Source,String Message_)
		{
			  super("",(int)(320*Zoom),(int)(240*Zoom),_Source);
			  int i,j;
			  boolean trouve;
			  DestRect=new Rectangle();
			  Said=Message_.split(",");
			  for(i=0;i<Said.length;i++)
				  Said[i]=Said[i].substring(1,Said[i].length()-1);
			  z=(int) ((240*Zoom)+(190*Zoom));
			  x=0;
			  y=0;
			  Etape=0;
			  Attente=0;
			  Position=0;
			  window=CreateWindow((int)(320*Zoom),(int)(240*Zoom),false);
			  blocsurf=CreateWindow((int)(320*Zoom),(int)(60*Zoom),false);
  		      NbPlaceInventaire=100;
		      vente=new short[100][2];
		      for(i=0;i<Said.length-1;i++)
		      {
		    	  j=0; trouve=false;
		    	  while((j<general.getObjets().size())&&(trouve==false))
		    	  {
		    		  if (general.getObjetByIndex(j).Name.compareTo(Said[i+1])==0)
		    			  trouve=true;
		    		  j++;
		    	  }
		    	  if (trouve==true)
		    		  vente[i][0]=(short) j;
		    	  else
		    		  vente[i][0]=0;
		    	  vente[i][1]=0;
		      }
			  Redraw("","");
		}

		public void Redraw(String txt,String txt2)
		{
			  BufferedImage im,textesurf;
  			  ImageFilter cif2;
			  ImageProducer improd;
			  int width,i;
			  String texte;
			  
			  GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
			  im=gc.createCompatibleImage((int)(320*Zoom),(int)(240*Zoom));			  
		      Graphics2D g2d = im.createGraphics();
	    	  g2d.drawImage(window,0,0,(int)(320*Zoom),(int)(240*Zoom), null);
	  	      g2d.setFont(new Font("Arial", Font.PLAIN,  (int)(10*Zoom)));
		      FontMetrics fontMetrics = g2d.getFontMetrics();
	    	  g2d.drawImage(blocsurf,0,(int)((240-60)*Zoom),(int)(320*Zoom),(int)(60*Zoom), null);
	    	  switch(Etape)
	    	  {
	    	  	case 0 :
	    	  		if (Said[0].length() > "Acheter".length())
	    	  			width=fontMetrics.stringWidth(Said[0]);
	    	  		else
	    	  			width=fontMetrics.stringWidth("Acheter");
	    	  		textesurf=CreateWindow((int)(width+(10*Zoom)),(int)(57*Zoom),false);
	    	  		Graphics2D g2dts = textesurf.createGraphics();
	    	  		g2dts.setFont(new Font("Arial", Font.PLAIN,  (int)(10*Zoom)));
    			    curs=new Curseur(Source,(width+(6*Zoom)) / Zoom,z+1);
	    			SpriteE.AddSprite(curs);
	    			if (Said[0].compareTo("")!=0)
	    			{
	            	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) ((2*Zoom)+fontMetrics.getHeight());
	            	      g2dts.drawString(Said[0],DestRect.x,DestRect.y);	    				
	    			}
	    			g2dts.drawString("Acheter",DestRect.x,(int)((15*Zoom)+fontMetrics.getHeight()));	    				
	    			g2dts.drawString("Vendre",DestRect.x,(int)((28*Zoom)+fontMetrics.getHeight()));	    				
	    			g2dts.drawString("Quitter",DestRect.x,(int)((41*Zoom)+fontMetrics.getHeight()));
	    			g2dts.dispose();
  	  		        DestRect.x=(int) ((160*Zoom)-((width+10) / 2)); DestRect.y=(int) ((240-80)*Zoom);
  		    	    g2d.drawImage(textesurf,DestRect.x,DestRect.y,textesurf.getWidth(null),textesurf.getHeight(null), null);
  		    	    break;
	    	  	case 1 :
	    	  	case 2 :
	    	  	  if (items==null)
	    	  	  {
		    	  	  if (Etape==1)
		    	  		  items=vente;
		    	  	  else
		    	  	  {
		    	  		  items=new short[100][2];
		    	  		  for(i=0;i<100;i++)
		    	  		  {
		    	  			  items[i][0]=PlayerInfo.Inventaire[i][0];
		    	  			  items[i][1]=0;
		    	  		  }
		    	  	  }
	    	  	  }
				  for (i=0;i<13;i++)
				  {
					    if ((Position+i) < 100-12)
					    if (items[Position+i][0]>0)
					    {
					    	texte = general.getObjetByIndex(items[Position+i][0]-1).Name;
					    	if (general.getObjetByIndex(items[Position+i][0]-1).Explication.compareTo("")!=0)
					    		texte=texte+" : "+general.getObjetByIndex(items[Position+i][0]-1).Explication;
					    	if (texte.compareTo("")!=0)
					    	{
					    		  g2d.setColor(ObjectColor(items[Position+i][0]-1));
				    	    	  if (general.getObjetByIndex(items[Position+i][0]-1).Chipset.compareTo("")==0)
				    	    	  {
								      DestRect.x=(int) (4*Zoom); DestRect.y=(int) ((((13*i)+2)*Zoom)+fontMetrics.getHeight());
				    	    	  }
				    	    	  else
				    	    	  {
				    	    		  Image ico=LoadImage(general.getName()+"/"+general.getObjetByIndex(items[Position+i][0]-1).Chipset.replace("Chipset\\", "Chipset/"), false);
				    	    		  improd=ico.getSource();
				    	    		  DestRect.x=general.getObjetByIndex(items[Position+i][0]-1).X; DestRect.y=general.getObjetByIndex(items[Position+i][0]-1).Y; 
				    	    		  DestRect.width=general.getObjetByIndex(items[Position+i][0]-1).W; DestRect.height=general.getObjetByIndex(items[Position+i][0]-1).H;
				    		    	  cif2 =new CropImageFilter(DestRect.x, DestRect.y,DestRect.width,DestRect.height);
				    		    	  ico = createImage(new FilteredImageSource(improd, cif2));	  
				            	      DestRect.x=(int) (4*Zoom); DestRect.y=(int) (((13*i)+2)*Zoom);
				    		    	  g2d.drawImage(ico,DestRect.x,DestRect.y,(int)(16*Zoom),(int)(16*Zoom), null);
				            	      DestRect.x=(int) (24*Zoom); DestRect.y=(int) ((((13*i)+2)*Zoom)+fontMetrics.getHeight());
				    	    	  }
				    			g2d.drawString("+/-"+items[Position+i][1]+" "+texte,DestRect.x,DestRect.y);
					    	}
					    }
				  }
	  			  if (FlecheHaute!=null)
				  {
				    FlecheHaute.Kill();
				    FlecheHaute=null;
				  }
				  if (FlecheBasse!=null)
				  {
				    FlecheBasse.Kill();
				    FlecheBasse=null;
				  }
				  if (Position > 0)
				  {
				    FlecheHaute=new Fleche(Source,305,5,z+2,0);
				    SpriteE.AddSprite(FlecheHaute);
				  }
				  if (Position < 87)
				  {
				    FlecheBasse=new Fleche(Source,305,170,z+2,1);
				    SpriteE.AddSprite(FlecheBasse);
				  }
	    		  g2d.setColor(Color.WHITE);
				  g2d.drawString("Or disponible : "+Integer.toString(PlayerInfo.Gold),(int)(4*Zoom),(int)((197*Zoom)+fontMetrics.getHeight()));
		    	  if (txt.compareTo("")!=0)
				    g2d.drawString(txt,(int)(4*Zoom),(int)((184*Zoom)+fontMetrics.getHeight()));
		    	  if (txt2.compareTo("")!=0)
					    g2d.drawString(txt2,(int)(4*Zoom),(int)((210*Zoom)+fontMetrics.getHeight()));
			      break;
	    	  }
	    	  g2d.dispose();
	    	  Image=im;		
		}
		private String ObjectProperty(int Objet)
		{
		  String Result="";
		  if ((general.getObjetByIndex(Objet).Classe>0) && (general.getClassesJoueur().get(general.getObjetByIndex(Objet).Classe-1)!=PlayerInfo.Classe))
		  {
		    Result="ne convient pas aux "+general.getClassesJoueur().get(general.getObjetByIndex(Objet).Classe-1).Name;
		  }
		  else
		  {
		    Result="nécessite ";
		    for(int i=0;i<general.getStatsBase().size();i++)
		    {
		    	if (Integer.parseInt(PlayerInfo.Stats.getProperty(general.getStatsBase().get(i),"0"))<general.getObjetByIndex(Objet).StatsMin.get(i))
		    		Result+="+"+Integer.toString(general.getObjetByIndex(Objet).StatsMin.get(i)-Integer.parseInt(PlayerInfo.Stats.getProperty(general.getStatsBase().get(i),"0")))+" "+general.getStatsBase().get(i)+" ";
		    }
		    if (Result.compareTo("nécessite ")==0) Result="";
		  }
		  return Result;
		}

	    public void Move(KeyboardInput.KeyState[] keys)
	    {
	    	int i,nbobj=0;
	    	boolean trouve;
	    	String texte="",texte2="";
	    	if ((curs.isDead) || (IsMagasinActive==false) || (keys[KESCAPE] == KeyState.ONCE))
	    	{
	  			  if (FlecheHaute!=null)
				  {
				    FlecheHaute.Kill();
				    FlecheHaute=null;
				  }
				  if (FlecheBasse!=null)
				  {
				    FlecheBasse.Kill();
				    FlecheBasse=null;
				  }
	    		curs.Kill();
	    		Kill();
	    	    Attente=0;
	    	    IsMagasinActive=false;
		    	  try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    	    return;
	    	}
	    	switch(Etape)
	    	{
	    		case 0 :
	    			curs.x=(int) ((160*Zoom)-((curs.w+(4*Zoom)) / 2)+(2*Zoom)+(5*(Zoom-1)));
	    		    curs.y=(int) (((curs.PositionY*13)+256-80)*Zoom);
	    	    	if ((keys[KUP] == KeyState.PRESSED) && (curs.PositionY > 0) && (Attente >= 10))
	    	    	{
	    	    	    curs.PositionY--;
	    	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    	    Attente = 0;
	    	    	}
	    	    	if ((keys[KDOWN] == KeyState.PRESSED) && (curs.PositionY < 2) && (Attente >= 10))
	    	    	{
	    	    	    curs.PositionY++;
	    	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    	    Attente= 0;
	    	    	}
	    	    	if (((keys[KRETURN] == KeyState.ONCE) || (keys[KSPACE] == KeyState.ONCE)) && (Attente >= 10))
	    	    	{
	    	    	    PlaySound(SOUND_VALIDE,"",false);
	    	    	    switch(curs.PositionY)
	    	    	    {
	    	    	    	case 0 :
	    	    	    	case 1 :
	    	    	    		Etape=curs.PositionY+1;
	    	    	    		curs.Kill();
	    	    			    curs=new Curseur(Source,320-4,z+1);
	    		    			SpriteE.AddSprite(curs);
	    		    			Redraw("","");
	    	    	    	    Attente=-1;
	    		    			break;
	    	    	    	case 2 :
	    	    	    		IsMagasinActive=false;
	    	    	    		SpriteEv=null;
	    	    	    		Kill();
	    	    	    		curs.Kill();
	    	    	    	    Attente=-1;
		  	    		    	  try {
		  	    						Thread.sleep(100);
		  	    					} catch (InterruptedException e) {
		  	    						e.printStackTrace();
		  	    					}
	    	    	    }
	    	    	}
	    	    	break;
	    		case 1 :
	    		  curs.x=(int) (2*Zoom);
	    		  curs.y=(int) (((curs.PositionY*13)+3)*Zoom);
	    		  if (((keys[KLEFT] == KeyState.PRESSED) || (keys[KeyEvent.VK_SUBTRACT] == KeyState.PRESSED)) &&  (items[(Position+curs.PositionY)][0]>0) && (Attente >= 10))
	    		  {
	    		    if (items[(Position+curs.PositionY)][1]>0)
	    		    	items[(Position+curs.PositionY)][1]--;
	    		    else
	    		    if (items[(Position+curs.PositionY)][1]==0)
	    		    	items[(Position+curs.PositionY)][1]=(short) NbPlaceInventaire;	    		    	
    	    	    PlaySound(DEFAULT_SOUND,"",false);
	    		    Attente=0;
	    		  }
	    		  if (((keys[KRIGHT] == KeyState.PRESSED) || (keys[KeyEvent.VK_ADD] == KeyState.PRESSED)) &&  (items[(Position+curs.PositionY)][0]>0) && (Attente >= 10))
	    		  {
	    		    if (items[(Position+curs.PositionY)][1]<NbPlaceInventaire)
	    		    	items[(Position+curs.PositionY)][1]++;
	    		    else
	    		    if (items[(Position+curs.PositionY)][1]==NbPlaceInventaire)
	    		    	items[(Position+curs.PositionY)][1]=0;
    	    	    PlaySound(DEFAULT_SOUND,"",false);
	    		    Attente=0;
	    		  }
	    		  if ((keys[KUP] == KeyState.PRESSED) && (curs.PositionY > 0) && (Attente >= 10))
	    		  {
	    		    curs.PositionY--;
    	    	    PlaySound(DEFAULT_SOUND,"",false);
	    		    Attente=0;
	    		  }
	    		  if ((keys[KDOWN] == KeyState.PRESSED) && (curs.PositionY < 12) && (Attente >= 10))
	    		  {
	    		    curs.PositionY++;
	  	    	    PlaySound(DEFAULT_SOUND,"",false);
	    		    Attente=0;
	    		  }
	    		  if ((keys[KDOWN] == KeyState.PRESSED) && (curs.PositionY == 12) && (Attente >= 10))
	    		  {
	  	    	    PlaySound(DEFAULT_SOUND,"",false);
	    		    if (Position<87)
	    		    {
	    		      Position++;
	    		    }
	    		    Attente= 0;
	    		  }
	    		  if ((keys[KUP] == KeyState.PRESSED) && (curs.PositionY == 0) && (Attente >= 10))
	    		  {
	  	    	    PlaySound(DEFAULT_SOUND,"",false);
	    		    if (Position>0)
	    		    {
	    		      Position--;
	    		    }
	    		    Attente= 0;
	    		  }
	    		  if (((keys[KRETURN] == KeyState.ONCE) || (keys[KSPACE] == KeyState.ONCE)) && (Attente >= 10))
	    		  {
	    		    PlaySound(SOUND_VALIDE,"",false);
	    		    if (((Position+curs.PositionY) < 100) && (items[(Position+curs.PositionY)][0]>0))
	    		    {
	    		      i=0; trouve=false;
	    		      while ((i<100)&&(trouve==false))
	    		      {
	    		    	  if (PlayerInfo.Inventaire[i][0]==items[Position+curs.PositionY][0])
	    		    	  {
	    		    		  if (items[Position+curs.PositionY][1]+PlayerInfo.Inventaire[i][1]>100)
	    		    			  nbobj=100-PlayerInfo.Inventaire[i][1];
	    		    		  else
	    		    			  nbobj=items[Position+curs.PositionY][1];
	    		    		  if (nbobj<=0)
	    		    		  {
	    		    			  trouve=false;
	    		    			  nbobj=-1;
	    		    		  }
	    		    		  else
	    		    		      trouve=true;
	    		    	  }
	    		    	  i++;
	    		      }
	    		      if ((trouve==false)&&(nbobj!=-1))
	    		      {
		    		      i=0; trouve=false;
		    		      while ((i<100)&&(trouve==false))
		    		      {
		    		    	  if (PlayerInfo.Inventaire[i][0]==0)
		    		    	  {
		    		    		  trouve=true;
	    		    			  nbobj=items[Position+curs.PositionY][1];
		    		    	  }
			    		      i++;
		    		      }
	    		      }
	    		      if (trouve==true)
	    		      {
	    		        i--;
	    		    	if (items[Position+curs.PositionY][1] > 0)
	    		        	if (PlayerInfo.Gold >= general.getObjetByIndex(items[Position+curs.PositionY][0]-1).Prix*nbobj)
	    		        	{
	    		        		texte="Bon achat!";
	    		        		PlayerInfo.Gold-=general.getObjetByIndex(items[Position+curs.PositionY][0]-1).Prix*nbobj;
	    		                PlayerInfo.Inventaire[i][0]=items[Position+curs.PositionY][0];
	    		                PlayerInfo.Inventaire[i][1]+=(short) nbobj;
	    		                items[Position+curs.PositionY][1]=0;
	    		        	}
	    		            else
	    		              texte="Vous n'êtes pas suffisamment riche...";
	    		      }
	    		      else	    		      
	    		        texte="Votre inventaire est plein!";
	    		      Attente=0;
	    		    }
	    		  }
		  	    	if (Attente==0)
			    	{
					  if (items==null)
						  Redraw("","");
			    	  if (items[Position+curs.PositionY][0]>0)
					  {
						if (texte.compareTo("")==0)
							texte=ObjectProperty(items[Position+curs.PositionY][0]-1);
					    if (items[Position+curs.PositionY][1]>1)
					    	texte2="Je vous vends ces objets pour "+(general.getObjetByIndex(items[Position+curs.PositionY][0]-1).Prix*items[Position+curs.PositionY][1])+" Pièces d'or";
					    else
					    	texte2="Je vous vends cet objet pour "+general.getObjetByIndex(items[Position+curs.PositionY][0]-1).Prix+" Pièces d'or";
		    			Redraw(texte,texte2);
					  }
					  else
						  Redraw(texte,"");
			    	}
	    		  break;
	    		case 2 :
		    		  curs.x=(int) (2*Zoom);
		    		  curs.y=(int) (((curs.PositionY*13)+3)*Zoom);
		    		  if (((keys[KLEFT] == KeyState.PRESSED) || (keys[KeyEvent.VK_SUBTRACT] == KeyState.PRESSED)) &&  (items[(Position+curs.PositionY)][0]>0) && (Attente >= 10))
		    		  {
		    		    if (items[(Position+curs.PositionY)][1]>0)
		    		    	items[(Position+curs.PositionY)][1]--;
		    		    else
		    		    if (items[(Position+curs.PositionY)][1]==0)
		    		    	items[(Position+curs.PositionY)][1]=(short) PlayerInfo.Inventaire[(Position+curs.PositionY)][1];	    		    	
	    	    	    PlaySound(DEFAULT_SOUND,"",false);
		    		    Attente=0;
		    		  }
		    		  if (((keys[KRIGHT] == KeyState.PRESSED) || (keys[KeyEvent.VK_ADD] == KeyState.PRESSED)) &&  (items[(Position+curs.PositionY)][0]>0) && (Attente >= 10))
		    		  {
		    		    if (items[(Position+curs.PositionY)][1]<PlayerInfo.Inventaire[(Position+curs.PositionY)][1])
		    		    	items[(Position+curs.PositionY)][1]++;
		    		    else
		    		    if (items[(Position+curs.PositionY)][1]==PlayerInfo.Inventaire[(Position+curs.PositionY)][1])
		    		    	items[(Position+curs.PositionY)][1]=0;
	    	    	    PlaySound(DEFAULT_SOUND,"",false);
		    		    Attente=0;
		    		  }
		    		  if ((keys[KUP] == KeyState.PRESSED) && (curs.PositionY > 0) && (Attente >= 10))
		    		  {
		    		    curs.PositionY--;
	    	    	    PlaySound(DEFAULT_SOUND,"",false);
		    		    Attente=0;
		    		  }
		    		  if ((keys[KDOWN] == KeyState.PRESSED) && (curs.PositionY < 12) && (Attente >= 10))
		    		  {
		    		    curs.PositionY++;
		  	    	    PlaySound(DEFAULT_SOUND,"",false);
		    		    Attente=0;
		    		  }
		    		  if ((keys[KDOWN] == KeyState.PRESSED) && (curs.PositionY == 12) && (Attente >= 10))
		    		  {
		  	    	    PlaySound(DEFAULT_SOUND,"",false);
		    		    if (Position<87)
		    		    {
		    		      Position++;
		    		    }
		    		    Attente= 0;
		    		  }
		    		  if ((keys[KUP] == KeyState.PRESSED) && (curs.PositionY == 0) && (Attente >= 10))
		    		  {
		  	    	    PlaySound(DEFAULT_SOUND,"",false);
		    		    if (Position>0)
		    		    {
		    		      Position--;
		    		    }
		    		    Attente= 0;
		    		  }
		    		  if (((keys[KRETURN] == KeyState.ONCE) || (keys[KSPACE] == KeyState.ONCE)) && (Attente >= 10))
		    		  {
		    		    PlaySound(SOUND_VALIDE,"",false);
		    		    if (((Position+curs.PositionY) < 100) && (items[(Position+curs.PositionY)][0]>0))
		    		    {
		    		    	texte="Voila votre argent!";
		    		    	if (PlayerInfo.Inventaire[Position+curs.PositionY][0]>0)
		    		    	{
	    		        		PlayerInfo.Gold+=general.getObjetByIndex(items[Position+curs.PositionY][0]-1).Prix*items[Position+curs.PositionY][1]*0.1;
	    		                PlayerInfo.Inventaire[Position+curs.PositionY][1]-=items[Position+curs.PositionY][1];
	    		                if (PlayerInfo.Inventaire[Position+curs.PositionY][1]<=0)
	    		                {
	    		                	PlayerInfo.Inventaire[Position+curs.PositionY][0]=0;
	    		                	PlayerInfo.Inventaire[Position+curs.PositionY][1]=0;
	    		                }
	    		                items[Position+curs.PositionY][0]=PlayerInfo.Inventaire[Position+curs.PositionY][0];
	    		                items[Position+curs.PositionY][1]=0;		    		    		
		    		    	}
		    		    }
		    		    Attente=0;
		    		  }
			  	    	if (Attente==0)
				    	{
						  if (items==null)
							  Redraw("","");
				    	  if (items[Position+curs.PositionY][0]>0)
						  {
							if (texte.compareTo("")==0)
								texte=ObjectProperty(items[Position+curs.PositionY][0]-1);
						    if (items[Position+curs.PositionY][1]>1)
						    	texte2="Je vous achète ces objets pour "+(int)(general.getObjetByIndex(items[Position+curs.PositionY][0]-1).Prix*items[Position+curs.PositionY][1]*0.10)+" Pièces d'or";
						    else
						    	texte2="Je vous achète cet objet pour "+(int)(general.getObjetByIndex(items[Position+curs.PositionY][0]-1).Prix*0.10)+" Pièces d'or";
			    			Redraw(texte,texte2);
						  }
						  else
							  Redraw(texte,"");
				    	}
		    		  break;
	    	}
	    	if (Attente < 10) Attente++;
	    }
		public void Draw(Graphics2D g)
		{
			  DestRect.x= x+ScreenX;
			  DestRect.y= y+ScreenY;
			  if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			  DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
			  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}
	
	public class MagieMenu extends Sprite {
		Rectangle DestRect;
	    Curseur cursv,cursh;
	    int Attente,MenuY;
	    boolean InConfirm;
	    Fleche FlecheHaute,FlecheBasse;
	    BufferedImage blocsurf,window;
		public MagieMenu(JFrame _Source)
		{
		  super("",(int)(260*Zoom),(int)(240*Zoom),_Source);
		  z=(int) ((240*Zoom)+(190*Zoom));
		  x=(int) (60*Zoom);
		  y=0;
		  Attente=0;
		  MenuY=0;
		  DestRect=new Rectangle();
		  InConfirm=false;
		  blocsurf=CreateWindow((int)(260*Zoom),(int)(32*Zoom),false);
		  window=CreateWindow((int)(260*Zoom),(int)(240*Zoom),false);
		  cursv=new Curseur(_Source,242,z+1);
		  SpriteE.AddSprite(cursv);
		  cursh=new Curseur(_Source,50,z+1);
		  SpriteE.AddSprite(cursh);
		  Redraw();
		}
		public int MagieUsable(int magie)
		{
			if (PlayerInfo.Classe!=null)
			{
				if (general.getMagieByIndex(magie).Classe>0)
					if (general.getClassesJoueur().get(general.getMagieByIndex(magie).Classe-1).Name.compareTo(PlayerInfo.Classe.Name)!=0)
						return 0;
			}
			if (PlayerInfo.Lvl<general.getMagieByIndex(magie).LvlMin)
				return 1;
			return 3;			
		}
		public Color MagieColor(int magie)
		{
			switch(MagieUsable(magie))
			{
				case 0 : return new Color(0,0,0);  
				case 1 : return new Color(0xFF,0,0); 
				case 2 : return new Color(0xFF,0xFF,0xC0);
				case 3 : return Color.WHITE;
			}
			return Color.WHITE;
		}
		
		public void Redraw()
		{
			  BufferedImage textesurf;
  			  ImageFilter cif2;
			  ImageProducer improd;
			  GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
			  textesurf=gc.createCompatibleImage((int)(260*Zoom),(int)(240*Zoom),java.awt.Transparency.TRANSLUCENT);			  
			  if ((FlecheHaute!=null)&&(MenuY==0))
			  {
			    FlecheHaute.Kill();
			    FlecheHaute=null;
			  }
			  if ((FlecheBasse!=null)&&(MenuY==85))
			  {
			    FlecheBasse.Kill();
			    FlecheBasse=null;
			  }
			  if ((FlecheHaute==null)&&(MenuY > 0))
			  {
			    FlecheHaute=new Fleche(Source,305,37,z+2,0);
			    SpriteE.AddSprite(FlecheHaute);
			  }
			  if ((FlecheBasse==null)&&(MenuY < 85))
			  {
			    FlecheBasse=new Fleche(Source,305,230,z+2,1);
			    SpriteE.AddSprite(FlecheBasse);
			  }
		      Graphics2D g2d = textesurf.createGraphics();
	    	  g2d.drawImage(window,0,0,(int)(260*Zoom),(int)(240*Zoom), null);
	  	      g2d.setFont(new Font("Arial", Font.PLAIN,  (int)(10*Zoom)));
		      FontMetrics fontMetrics = g2d.getFontMetrics();
	    	  g2d.drawImage(blocsurf,0,0,(int)(260*Zoom),(int)(32*Zoom), null);
		      DestRect.x=(int)(5*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
		      for(int i=0;i<15;i++)
		      {
		    	  if (PlayerInfo.OwnSpell[MenuY+i]>0)
		    	  {
		    		  g2d.setColor(MagieColor(PlayerInfo.OwnSpell[MenuY+i]-1));
	    	    	  if (general.getMagieByIndex(PlayerInfo.OwnSpell[MenuY+i]-1).Chipset.compareTo("")==0)
	    	    	  {
					      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (32*Zoom+fontMetrics.getHeight()+(i*13*Zoom));
	    	    	  }
	    	    	  else
	    	    	  {
	    	    		  Image ico=LoadImage(general.getName()+"/"+general.getMagieByIndex(PlayerInfo.OwnSpell[MenuY+i]-1).Chipset.replace("Chipset\\", "Chipset/"), false);
	    	    		  improd=ico.getSource();
	    	    		  DestRect.x=general.getMagieByIndex(PlayerInfo.OwnSpell[MenuY+i]-1).X; DestRect.y=general.getMagieByIndex(PlayerInfo.OwnSpell[MenuY+i]-1).Y; 
	    	    		  DestRect.width=general.getMagieByIndex(PlayerInfo.OwnSpell[MenuY+i]-1).W; DestRect.height=general.getMagieByIndex(PlayerInfo.OwnSpell[MenuY+i]-1).H;
	    		    	  cif2 =new CropImageFilter(DestRect.x, DestRect.y,DestRect.width,DestRect.height);
	    		    	  ico = createImage(new FilteredImageSource(improd, cif2));	  
	            	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (32*Zoom+(i*13*Zoom));
	    		    	  g2d.drawImage(ico,DestRect.x,DestRect.y,(int)(16*Zoom),(int)(16*Zoom), null);
	            	      DestRect.x=(int)(25*Zoom); DestRect.y=(int) (32*Zoom+fontMetrics.getHeight()+(i*13*Zoom));
	    	    	  }
	    	    	  String racc="";
	    	    	  for(int j=0;j<10;j++)
	    	    		  if (PlayerInfo.Raccourcis[j]==-(MenuY+i+1))
	    	    			  racc=(j==9 ? Integer.toString(0) : Integer.toString(j+1));
	    	    	  if (racc.compareTo("")==0)
	    	    		  g2d.drawString(general.getMagieByIndex(PlayerInfo.OwnSpell[MenuY+i]-1).Name,DestRect.x,DestRect.y);
	    	    	  else
	    	    		  g2d.drawString(general.getMagieByIndex(PlayerInfo.OwnSpell[MenuY+i]-1).Name+" R"+racc,DestRect.x,DestRect.y);
		    	  }
		      }
			  g2d.setColor(Color.WHITE);
    	      DestRect.x=(int) (10*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
    		  g2d.drawString("Quitter",DestRect.x,DestRect.y);		    		  
		      if (PlayerInfo.OwnSpell[MenuY+cursv.PositionY]>0)
		      {
    			  g2d.setColor(Color.WHITE);
        	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (2*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString(general.getMagieByIndex(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1).Explication,DestRect.x,DestRect.y);		    		  
		    	  if (MagieUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)<2)
		    	  {
	    			  g2d.setColor(Color.BLACK);
	        	      DestRect.x=(int) (62*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
		    		  g2d.drawString("Utiliser",DestRect.x,DestRect.y);		    		  
		    	  }
		    	  else
		    	  {
	    			  g2d.setColor(Color.WHITE);
	        	      DestRect.x=(int) (62*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
		    		  g2d.drawString("Utiliser",DestRect.x,DestRect.y);		    		  
		    	  }
    			  g2d.setColor(Color.WHITE);
        	      DestRect.x=(int) (166*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString("Trier",DestRect.x,DestRect.y);
    			  g2d.setColor(Color.WHITE);
        	      DestRect.x=(int) (213*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString("Suppr",DestRect.x,DestRect.y);		    		  
		      }
		      else
		    	  cursh.PositionX=0;
		      Image=textesurf;
		      g2d.dispose();
		}
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
    		if ((IsInvActive==false)||(IsMenuActive==false))
    		{
    			if (cursv!=null)
    				cursv.Kill();
    			if (cursh!=null)
    				cursh.Kill();
    			if (FlecheBasse!=null)
    				FlecheBasse.Kill();
    			if (FlecheHaute!=null)
    				FlecheHaute.Kill();
    			Kill();
    			IsInvActive=false;
    			return;
    		}
    		if ((cursv==null)||(IsInConfirm==true)||(cursh==null))
	    	{
	    	    Attente=0;
	    	    return;
	    	}
    		if ((cursv.isDead)||(cursh.isDead))
	    	{
    			cursv=null;
    			cursh=null;
	    	    Attente=0;
	    	    return;
	    	}
			if (InConfirm==true)
			{
				if (IsConfirmStatus==true)
				{
					for (int i=0;i<10;i++)
						if (PlayerInfo.Raccourcis[i]==-(MenuY+cursv.PositionY+1))
							PlayerInfo.Raccourcis[i]=0;
					PlayerInfo.OwnSpell[MenuY+cursv.PositionY]=0;
					cursh.PositionX=0;
					if (barreicone!=null)
						barreicone.Redraw();
					Redraw();
				}
				InConfirm=false;
				IsConfirmStatus=false;
			}
	    	cursv.x=(int) (62*Zoom);
	    	cursv.y=(int) (((cursv.PositionY*13)+34)*Zoom);
	    	cursh.x=(int) (((cursh.PositionX*50)+62)*Zoom);
	    	cursh.y=(int) (16*Zoom);
	    	if ((keys[K0] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.OwnSpell[MenuY+cursv.PositionY]>0)&&(MagieUsable(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1)>=2))
	    		{
		    		if (PlayerInfo.Raccourcis[9]==-(MenuY+cursv.PositionY+1))
		    			PlayerInfo.Raccourcis[9]=0;
		    		else
		    			PlayerInfo.Raccourcis[9]=(short) -(MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K1] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.OwnSpell[MenuY+cursv.PositionY]>0)&&(MagieUsable(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1)>=2))
	    		{
		    		if (PlayerInfo.Raccourcis[0]==-(MenuY+cursv.PositionY+1))
		    			PlayerInfo.Raccourcis[0]=0;
		    		else
		    			PlayerInfo.Raccourcis[0]=(short) -(MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K2] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.OwnSpell[MenuY+cursv.PositionY]>0)&&(MagieUsable(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1)>=2))
	    		{
		    		if (PlayerInfo.Raccourcis[1]==-(MenuY+cursv.PositionY+1))
		    			PlayerInfo.Raccourcis[1]=0;
		    		else
		    			PlayerInfo.Raccourcis[1]=(short) -(MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K3] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.OwnSpell[MenuY+cursv.PositionY]>0)&&(MagieUsable(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1)>=2))
	    		{
		    		if (PlayerInfo.Raccourcis[2]==-(MenuY+cursv.PositionY+1))
		    			PlayerInfo.Raccourcis[2]=0;
		    		else
		    			PlayerInfo.Raccourcis[2]=(short) -(MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K4] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.OwnSpell[MenuY+cursv.PositionY]>0)&&(MagieUsable(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1)>=2))
	    		{
		    		if (PlayerInfo.Raccourcis[3]==-(MenuY+cursv.PositionY+1))
		    			PlayerInfo.Raccourcis[3]=0;
		    		else
		    			PlayerInfo.Raccourcis[3]=(short) -(MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K5] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.OwnSpell[MenuY+cursv.PositionY]>0)&&(MagieUsable(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1)>=2))
	    		{
		    		if (PlayerInfo.Raccourcis[4]==MenuY+cursv.PositionY+1)
		    			PlayerInfo.Raccourcis[4]=0;
		    		else
		    			PlayerInfo.Raccourcis[4]=(short) -(MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K6] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.OwnSpell[MenuY+cursv.PositionY]>0)&&(MagieUsable(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1)>=2))
	    		{
		    		if (PlayerInfo.Raccourcis[5]==-(MenuY+cursv.PositionY+1))
		    			PlayerInfo.Raccourcis[5]=0;
		    		else
		    			PlayerInfo.Raccourcis[5]=(short) -(MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K7] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.OwnSpell[MenuY+cursv.PositionY]>0)&&(MagieUsable(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1)>=2))
	    		{
		    		if (PlayerInfo.Raccourcis[6]==-(MenuY+cursv.PositionY+1))
		    			PlayerInfo.Raccourcis[6]=0;
		    		else
		    			PlayerInfo.Raccourcis[6]=(short) -(MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K8] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.OwnSpell[MenuY+cursv.PositionY]>0)&&(MagieUsable(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1)>=2))
	    		{
		    		if (PlayerInfo.Raccourcis[7]==-(MenuY+cursv.PositionY+1))
		    			PlayerInfo.Raccourcis[7]=0;
		    		else
		    			PlayerInfo.Raccourcis[7]=(short) -(MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K9] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.OwnSpell[MenuY+cursv.PositionY]>0)&&(MagieUsable(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1)>=2))
	    		{
		    		if (PlayerInfo.Raccourcis[8]==-(MenuY+cursv.PositionY+1))
		    			PlayerInfo.Raccourcis[8]=0;
		    		else
		    			PlayerInfo.Raccourcis[8]=(short) -(MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[KUP] == KeyState.PRESSED) && (cursv.PositionY == 0) && (MenuY>0) && (Attente >= 10))
	    	{
	    	    MenuY--;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Redraw();
	    	    Attente = 0;
	    	}
	    	if ((keys[KUP] == KeyState.PRESSED) && (cursv.PositionY > 0) && (Attente >= 10))
	    	{
	    	    cursv.PositionY--;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Redraw();
	    	    Attente = 0;
	    	}
	    	if ((keys[KDOWN] == KeyState.PRESSED) && (cursv.PositionY == 14) && (MenuY<85) && (Attente >= 10))
	    	{
	    	    MenuY++;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Redraw();
	    	    Attente= 0;
	    	}
	    	if ((keys[KDOWN] == KeyState.PRESSED) && (cursv.PositionY < 14) && (Attente >= 10))
	    	{
	    	    cursv.PositionY++;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Redraw();
	    	    Attente= 0;
	    	}
	    	if ((keys[KLEFT] == KeyState.PRESSED) && (cursh.PositionX > 0) && (Attente >= 10))
	    	{
	    	    if (cursh.PositionX==3)
	    	    	cursh.PositionX-=2;
	    	    else
	    	    	cursh.PositionX--;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente = 0;
	    	}
	    	if ((keys[KRIGHT] == KeyState.PRESSED) && (cursh.PositionX < 4) && (Attente >= 10) && (PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0))
	    	{
	    	    if (cursh.PositionX==1)
	    	    	cursh.PositionX+=2;
	    	    else
	    	    	cursh.PositionX++;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente= 0;
	    	}
	    	if (((keys[KRETURN] == KeyState.PRESSED) || (keys[KSPACE] == KeyState.PRESSED)) && (Attente >= 10))
	    	{
	    	    switch(cursh.PositionX)
	    	    {
	    	    	case 0 :
	    	    	    PlaySound(SOUND_VALIDE,"",false);
	    	    		IsInvActive=false;
	    	    		break;
	    	    	case 1 :
		  		    	if (MagieUsable(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1)>=2)
		  		    	{
		  		    	    PlaySound(SOUND_VALIDE,"",false);
		  		    	    // lancement d'un sortilege
		  		    	    MagieUsed=PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1;
		  		    	    MagicObject=-1;
		  		    	    if (general.getMagieByIndex(MagieUsed).OnMonster==0)
		  		    	    {
		  		    	    	IsInSelectMChar=true;
		  		    	        SpriteE.AddSprite(new SelectMChar(Source,0,Sprite.idSprite.idMonstre));
		  		    	    }
		  		    	    else
		  		    	      CreateMagie(null, null, general.getMagieByIndex(MagieUsed));
		  		    	    IsMenuActive=false;
		  		    	}
		  		    	else
		  		    	    PlaySound(DEFAULT_SOUND,"",false);
		  		    	break;
	    	    	case 3 :
	    	    	    PlaySound(SOUND_VALIDE,"",false);
	                    int i=0; int numvar=-1;
		                while (i<100)
		                {
			                  if ((PlayerInfo.OwnSpell[i]==0) && (numvar<0)) numvar=i;
			                  if ((PlayerInfo.OwnSpell[i]>0) && (numvar>=0))
			                  {
				                    PlayerInfo.OwnSpell[numvar]=PlayerInfo.OwnSpell[i];
				                    PlayerInfo.OwnSpell[i]=0;
				                    i=-1;
				                    numvar=-1;
			                  }
			                  i++;
		                }
		                MenuY=0;
		                cursv.PositionY=0;
		                cursh.PositionX=0;
		                Redraw();
		                break;
	    	    	case 4 :
	    	    	    PlaySound(SOUND_VALIDE,"",false);
    	    			InConfirm=true;
    	    			SpriteE.AddSprite(new ConfirmDialog(Source, "Supprimer "+general.getMagieByIndex(PlayerInfo.OwnSpell[MenuY+cursv.PositionY]-1).Name+"?",0));	    	    		
	    	    }
	    	    Attente=-1;
	    	}
	    	if (Attente < 10) Attente++;
	    }
		public void Draw(Graphics2D g)
		{
			  DestRect.x= x+ScreenX;
			  DestRect.y= y+ScreenY;
			  if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			  DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
			  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}
	
	public int ObjectUsable(int objet)
	{
		if (PlayerInfo.Classe!=null)
		{
			if (general.getObjetByIndex(objet).Classe>0)
				if (general.getClassesJoueur().get(general.getObjetByIndex(objet).Classe-1).Name.compareTo(PlayerInfo.Classe.Name)!=0)
					return 0;
		}
		for(int i=0;i<general.getStatsBase().size();i++)
		{
			int stat=Integer.parseInt(PlayerInfo.Stats.getProperty(general.getStatsBase().get(i),"0"));
			if (stat<general.getObjetByIndex(objet).StatsMin.get(i))
				return 1;
		}
		for(int i=0;i<general.getStatsBase().size();i++)
		{
			if (general.getObjetByIndex(objet).Stats.get(i)>0)
				return 2;
		}
		return 3;			
	}
	public Color ObjectColor(int objet)
	{
		switch(ObjectUsable(objet))
		{
			case 0 : return new Color(0,0,0);  
			case 1 : return new Color(0xFF,0,0); 
			case 2 : return new Color(0xFF,0xFF,0xC0);
			case 3 : return Color.WHITE;
		}
		return Color.WHITE;
	}
	public class InventaireMenu extends Sprite {
		Rectangle DestRect;
	    Curseur cursv,cursh;
	    int Attente,MenuY;
	    boolean InConfirm;
	    Fleche FlecheHaute,FlecheBasse;
	    BufferedImage blocsurf,window;
		public InventaireMenu(JFrame _Source)
		{
		  super("",(int)(260*Zoom),(int)(240*Zoom),_Source);
		  z=(int) ((240*Zoom)+(190*Zoom));
		  x=(int)(60*Zoom) ;
		  y=0;
		  Attente=0;
		  MenuY=0;
		  DestRect=new Rectangle();
		  InConfirm=false;
		  blocsurf=CreateWindow((int)(260*Zoom),(int)(32*Zoom),false);
		  window=CreateWindow((int)(260*Zoom),(int)(240*Zoom),false);
		  cursv=new Curseur(_Source,242,z+1);
		  SpriteE.AddSprite(cursv);
		  cursh=new Curseur(_Source,50,z+1);
		  SpriteE.AddSprite(cursh);
		  Redraw();
		}	
		public void Redraw()
		{
			  BufferedImage textesurf;
  			  ImageFilter cif2;
			  ImageProducer improd;
			  GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
			  textesurf=gc.createCompatibleImage((int)(260*Zoom),(int)(240*Zoom),java.awt.Transparency.TRANSLUCENT);			  
			  if ((FlecheHaute!=null)&&(MenuY==0))
			  {
			    FlecheHaute.Kill();
			    FlecheHaute=null;
			  }
			  if ((FlecheBasse!=null)&&(MenuY==85))
			  {
			    FlecheBasse.Kill();
			    FlecheBasse=null;
			  }
			  if ((FlecheHaute==null)&&(MenuY > 0))
			  {
			    FlecheHaute=new Fleche(Source,305,37,z+2,0);
			    SpriteE.AddSprite(FlecheHaute);
			  }
			  if ((FlecheBasse==null)&&(MenuY < 85))
			  {
			    FlecheBasse=new Fleche(Source,305,230,z+2,1);
			    SpriteE.AddSprite(FlecheBasse);
			  }
		      Graphics2D g2d = textesurf.createGraphics();
	    	  g2d.drawImage(window,0,0,(int)(260*Zoom),(int)(240*Zoom), null);
	  	      g2d.setFont(new Font("Arial", Font.PLAIN,  (int)(10*Zoom)));
		      FontMetrics fontMetrics = g2d.getFontMetrics();
	    	  g2d.drawImage(blocsurf,0,0,(int)(260*Zoom),(int)(32*Zoom), null);
		      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
		      for(int i=0;i<15;i++)
		      {
		    	  if (PlayerInfo.Inventaire[MenuY+i][0]>0)
		    	  {
		    		  g2d.setColor(ObjectColor(PlayerInfo.Inventaire[MenuY+i][0]-1));
	    	    	  if (general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+i][0]-1).Chipset.compareTo("")==0)
	    	    	  {
					      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (32*Zoom+fontMetrics.getHeight()+(i*13*Zoom));
	    	    	  }
	    	    	  else
	    	    	  {
	    	    		  Image ico=LoadImage(general.getName()+"/"+general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+i][0]-1).Chipset.replace("Chipset\\", "Chipset/"), false);
	    	    		  improd=ico.getSource();
	    	    		  DestRect.x=general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+i][0]-1).X; DestRect.y=general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+i][0]-1).Y; 
	    	    		  DestRect.width=general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+i][0]-1).W; DestRect.height=general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+i][0]-1).H;
	    		    	  cif2 =new CropImageFilter(DestRect.x, DestRect.y,DestRect.width,DestRect.height);
	    		    	  ico = createImage(new FilteredImageSource(improd, cif2));	  
	            	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (32*Zoom+(i*13*Zoom));
	    		    	  g2d.drawImage(ico,DestRect.x,DestRect.y,(int)(16*Zoom),(int)(16*Zoom), null);
	            	      DestRect.x=(int) (25*Zoom); DestRect.y=(int) (32*Zoom+fontMetrics.getHeight()+(i*13*Zoom));
	    	    	  }
	    	    	  String racc="";
	    	    	  for(int j=0;j<10;j++)
	    	    		  if (PlayerInfo.Raccourcis[j]==MenuY+i+1)
	    	    			  racc=(j==9 ? Integer.toString(0) : Integer.toString(j+1));
	    	    	  if (racc.compareTo("")==0)
	    	    		  g2d.drawString(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+i][0]-1).Name+" x "+PlayerInfo.Inventaire[MenuY+i][1],DestRect.x,DestRect.y);
	    	    	  else
	    	    		  g2d.drawString(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+i][0]-1).Name+" x "+PlayerInfo.Inventaire[MenuY+i][1]+" R"+racc,DestRect.x,DestRect.y);
		    	  }
		      }
			  g2d.setColor(Color.WHITE);
    	      DestRect.x=(int) (10*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
    		  g2d.drawString("Quitter",DestRect.x,DestRect.y);		    		  
		      if (PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0)
		      {
    			  g2d.setColor(Color.WHITE);
        	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (2*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).Explication,DestRect.x,DestRect.y);		    		  
		    	  if (ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)<2)
		    	  {
	    			  g2d.setColor(Color.BLACK);
	        	      DestRect.x=(int) (62*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
		    		  g2d.drawString("Utiliser",DestRect.x,DestRect.y);		    		  
	        	      DestRect.x=(int) (110*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
		    		  g2d.drawString("Equiper",DestRect.x,DestRect.y);		    		  		    		  
		    	  }
		    	  else
		    	  {
		    		  if ((general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==0) || (general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==7))
		    			  g2d.setColor(Color.WHITE);
		    		  else
		    			  g2d.setColor(Color.BLACK);
	        	      DestRect.x=(int) (62*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
		    		  g2d.drawString("Utiliser",DestRect.x,DestRect.y);		    		  
		    		  if ((general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType>0) && (general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType<7))
		    			  g2d.setColor(Color.WHITE);
		    		  else
		    			  g2d.setColor(Color.BLACK);
	        	      DestRect.x=(int) (110*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
		    		  g2d.drawString("Equiper",DestRect.x,DestRect.y);		    		  
		    	  }
    			  g2d.setColor(Color.WHITE);
        	      DestRect.x=(int) (166*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString("Trier",DestRect.x,DestRect.y);
	    		  if (general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==6)
	    			  g2d.setColor(Color.BLACK);
	    		  else
	    			  g2d.setColor(Color.WHITE);
        	      DestRect.x=(int) (213*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString("Suppr",DestRect.x,DestRect.y);		    		  
		      }
		      else
		    	  cursh.PositionX=0;
		      Image=textesurf;
		      g2d.dispose();
		}
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
    		if ((IsInvActive==false)||(IsMenuActive==false))
    		{
    			if (cursv!=null)
    				cursv.Kill();
    			if (cursh!=null)
    				cursh.Kill();
    			if (FlecheBasse!=null)
    				FlecheBasse.Kill();
    			if (FlecheHaute!=null)
    				FlecheHaute.Kill();
    			Kill();
    			IsInvActive=false;
    			return;
    		}
    		if ((cursv==null)||(IsInConfirm==true)||(cursh==null)||(IsInvEquipActive==true))
	    	{
	    	    Attente=0;
	    	    return;
	    	}
    		if ((cursv.isDead)||(cursh.isDead))
	    	{
    			cursv=null;
    			cursh=null;
	    	    Attente=0;
	    	    return;
	    	}
			if (InConfirm==true)
			{
				if (IsConfirmStatus==true)
				{
					for (int i=0;i<10;i++)
						if (PlayerInfo.Raccourcis[i]==MenuY+cursv.PositionY+1)
							PlayerInfo.Raccourcis[i]=0;
					PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]=0;
					PlayerInfo.Inventaire[MenuY+cursv.PositionY][1]=0;
					cursh.PositionX=0;
					if (barreicone!=null)
						barreicone.Redraw();
					Redraw();
				}
				InConfirm=false;
				IsConfirmStatus=false;
			}
	    	cursv.x=(int) (62*Zoom);
	    	cursv.y=(int) (((cursv.PositionY*13)+34)*Zoom);
	    	cursh.x=(int) (((cursh.PositionX*50)+62)*Zoom);
	    	cursh.y=(int) (16*Zoom);
	    	if ((keys[K0] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0)&&(ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)>=2)&&(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==0))
	    		{
		    		if (PlayerInfo.Raccourcis[9]==MenuY+cursv.PositionY+1)
		    			PlayerInfo.Raccourcis[9]=0;
		    		else
		    			PlayerInfo.Raccourcis[9]=(short) (MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K1] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0)&&(ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)>=2)&&(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==0))
	    		{
		    		if (PlayerInfo.Raccourcis[0]==MenuY+cursv.PositionY+1)
		    			PlayerInfo.Raccourcis[0]=0;
		    		else
		    			PlayerInfo.Raccourcis[0]=(short) (MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K2] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0)&&(ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)>=2)&&(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==0))
	    		{
		    		if (PlayerInfo.Raccourcis[1]==MenuY+cursv.PositionY+1)
		    			PlayerInfo.Raccourcis[1]=0;
		    		else
		    			PlayerInfo.Raccourcis[1]=(short) (MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K3] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0)&&(ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)>=2)&&(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==0))
	    		{
		    		if (PlayerInfo.Raccourcis[2]==MenuY+cursv.PositionY+1)
		    			PlayerInfo.Raccourcis[2]=0;
		    		else
		    			PlayerInfo.Raccourcis[2]=(short) (MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K4] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0)&&(ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)>=2)&&(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==0))
	    		{
		    		if (PlayerInfo.Raccourcis[3]==MenuY+cursv.PositionY+1)
		    			PlayerInfo.Raccourcis[3]=0;
		    		else
		    			PlayerInfo.Raccourcis[3]=(short) (MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K5] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0)&&(ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)>=2)&&(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==0))
	    		{
		    		if (PlayerInfo.Raccourcis[4]==MenuY+cursv.PositionY+1)
		    			PlayerInfo.Raccourcis[4]=0;
		    		else
		    			PlayerInfo.Raccourcis[4]=(short) (MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K6] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0)&&(ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)>=2)&&(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==0))
	    		{
		    		if (PlayerInfo.Raccourcis[5]==MenuY+cursv.PositionY+1)
		    			PlayerInfo.Raccourcis[5]=0;
		    		else
		    			PlayerInfo.Raccourcis[5]=(short) (MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K7] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0)&&(ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)>=2)&&(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==0))
	    		{
		    		if (PlayerInfo.Raccourcis[6]==MenuY+cursv.PositionY+1)
		    			PlayerInfo.Raccourcis[6]=0;
		    		else
		    			PlayerInfo.Raccourcis[6]=(short) (MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K8] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0)&&(ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)>=2)&&(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==0))
	    		{
		    		if (PlayerInfo.Raccourcis[7]==MenuY+cursv.PositionY+1)
		    			PlayerInfo.Raccourcis[7]=0;
		    		else
		    			PlayerInfo.Raccourcis[7]=(short) (MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[K9] == KeyState.PRESSED) && (Attente >= 10))
	    	{
	    		if ((PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0)&&(ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)>=2)&&(general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==0))
	    		{
		    		if (PlayerInfo.Raccourcis[8]==MenuY+cursv.PositionY+1)
		    			PlayerInfo.Raccourcis[8]=0;
		    		else
		    			PlayerInfo.Raccourcis[8]=(short) (MenuY+cursv.PositionY+1);
		    	    PlaySound(DEFAULT_SOUND,"",false);
		    	    Redraw();
		    	    if (barreicone!=null)
		    	    	barreicone.Redraw();
	    		}
	    	    Attente = 0;
	    	}
	    	if ((keys[KUP] == KeyState.PRESSED) && (cursv.PositionY == 0) && (MenuY>0) && (Attente >= 10))
	    	{
	    	    MenuY--;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Redraw();
	    	    Attente = 0;
	    	}
	    	if ((keys[KUP] == KeyState.PRESSED) && (cursv.PositionY > 0) && (Attente >= 10))
	    	{
	    	    cursv.PositionY--;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Redraw();
	    	    Attente = 0;
	    	}
	    	if ((keys[KDOWN] == KeyState.PRESSED) && (cursv.PositionY == 14) && (MenuY<85) && (Attente >= 10))
	    	{
	    	    MenuY++;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Redraw();
	    	    Attente= 0;
	    	}
	    	if ((keys[KDOWN] == KeyState.PRESSED) && (cursv.PositionY < 14) && (Attente >= 10))
	    	{
	    	    cursv.PositionY++;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Redraw();
	    	    Attente= 0;
	    	}
	    	if ((keys[KLEFT] == KeyState.PRESSED) && (cursh.PositionX > 0) && (Attente >= 10))
	    	{
	    	    cursh.PositionX--;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente = 0;
	    	}
	    	if ((keys[KRIGHT] == KeyState.PRESSED) && (cursh.PositionX < 4) && (Attente >= 10) && (PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]>0))
	    	{
	    	    cursh.PositionX++;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente= 0;
	    	}
	    	if (((keys[KRETURN] == KeyState.PRESSED) || (keys[KSPACE] == KeyState.PRESSED)) && (Attente >= 10))
	    	{
	    	    PlaySound(SOUND_VALIDE,"",false);
	    	    switch(cursh.PositionX)
	    	    {
	    	    	case 0 :
	    	    		IsInvActive=false;
	    	    		break;
	    	    	case 1 :
		  		    	if (ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)>=2)
				    		  if ((general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==0) || (general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType==7))
				    		  {
				    			  UtiliseObjet(MenuY+cursv.PositionY);
				    			  Redraw();
				    		  }
		  		    	else
		  		    	    PlaySound(DEFAULT_SOUND,"",false);
		  		    	break;
	    	    	case 2 :
	    	    		if (ObjectUsable(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1)>=2)
	  		    		  if ((general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType>0) && (general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).ObjType<7))
	  		    		  {
	  		    			  SpriteE.AddSprite(new InventaireEquipe(Source, MenuY+cursv.PositionY,this));
	  		    		  }
	    	    		break;
	    	    	case 3 :
	                    int i=0; int numvar=-1;
		                while (i<100)
		                {
			                  if ((PlayerInfo.Inventaire[i][0]==0) && (numvar<0)) numvar=i;
			                  if ((PlayerInfo.Inventaire[i][0]>0) && (numvar>=0))
			                  {
				                    PlayerInfo.Inventaire[numvar][0]=PlayerInfo.Inventaire[i][0];
				                    PlayerInfo.Inventaire[numvar][1]=PlayerInfo.Inventaire[i][1];			                    
				                    PlayerInfo.Inventaire[i][0]=0;
				                    PlayerInfo.Inventaire[i][1]=0;
				                    i=-1;
				                    numvar=-1;
			                  }
			                  i++;
		                }
		                MenuY=0;
		                cursv.PositionY=0;
		                cursh.PositionX=0;
		                Redraw();
		                break;
	    	    	case 4 :
    	    			InConfirm=true;
    	    			SpriteE.AddSprite(new ConfirmDialog(Source, "Supprimer "+general.getObjetByIndex(PlayerInfo.Inventaire[MenuY+cursv.PositionY][0]-1).Name+"?",0));	    	    		
	    	    }
	    	    Attente=-1;
	    	}
	    	if (Attente < 10) Attente++;
	    }
		public void Draw(Graphics2D g)
		{
			  DestRect.x= x+ScreenX;
			  DestRect.y= y+ScreenY;
			  if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			  DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
			  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}
	public class Options extends Sprite {
		Rectangle DestRect;
	    Curseur curs;
	    int Attente,keycode,etape;
		double lezoom;
	    boolean mus,son,ful,indefinekeys,InConfirm;
		BufferedImage window;
		KeyListener keyListener;
		private int lKS,lKD,lKLEFT,lKRIGHT,lKUP,lKDOWN,lKRETURN,lKSPACE,lKESCAPE;
		private int lK0,lK1,lK2,lK3,lK4,lK5,lK6,lK7,lK8,lK9;
		
		public Options(JFrame _Source)
		{
		  super("",(int)(320*Zoom),(int)(240*Zoom),_Source);
		  z=(int) ((240*Zoom)+(200*Zoom));
		  x=0;
		  y=0;
		  Attente=0;
		  DestRect=new Rectangle();
		  window=CreateWindow((int)(320*Zoom),(int)(240*Zoom),false);
		  lezoom=Zoomopt;
		  mus=HasMusic;
		  son=HasSound;
		  ful=HasFullScreen;
		  keycode=-1;
		  lKS=-1;
		  InConfirm=false;
		  indefinekeys=false;
		  keyListener = new KeyListener() {
			      public void keyTyped(KeyEvent keyEvent) {
			      }
			      public void keyPressed(KeyEvent keyEvent) {
			      }
			      public void keyReleased(KeyEvent keyEvent) {
			    	  keycode=keyEvent.getKeyCode();
			      }
			};
		  Redraw("","");
		  curs=new Curseur(_Source,200,z+1);
		  SpriteE.AddSprite(curs);
		}
		private String recuperetaille()
		{
			if(lezoom==0) return "automatique";
			if(lezoom==1) return "320*240";
			if(lezoom==2) return "640*480";
			if(lezoom==2.5) return "800*600";
			if(lezoom==3) return "960*720";
			if(lezoom==3.2) return "1024*768";
			if(lezoom==4) return "1280*960";
			return "automatique";
		}
		public void Redraw(String texte,String texte2)
		{
			  BufferedImage textesurf;
			  GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
			  textesurf=gc.createCompatibleImage((int)(320*Zoom),(int)(240*Zoom));
		      Graphics2D g2d = textesurf.createGraphics();
	  	      g2d.setFont(new Font("Arial", Font.PLAIN,  (int)(10*Zoom)));
		      FontMetrics fontMetrics = g2d.getFontMetrics();
			  g2d.drawImage(window,0,0,(int)(320*Zoom),(int)(240*Zoom), null);
		      if (texte.compareTo("")==0)
		      {
			      DestRect.x=(int)(80*Zoom); DestRect.y=(int)(67*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString("Fullscreen : "+(ful==true ? "Oui" : "Non"),DestRect.x,DestRect.y);			  
			      DestRect.x=(int) (80*Zoom); DestRect.y=(int) (80*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString("Résolution : "+recuperetaille(),DestRect.x,DestRect.y);			  
			      DestRect.x=(int) (80*Zoom); DestRect.y=(int) (93*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString("Musique : "+(mus==true ? "Oui" : "Non"),DestRect.x,DestRect.y);			  
			      DestRect.x=(int) (80*Zoom); DestRect.y=(int) (106*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString("Son : "+(son==true ? "Oui" : "Non"),DestRect.x,DestRect.y);			  
			      DestRect.x=(int) (80*Zoom); DestRect.y=(int) (119*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString("Redéfinir les touches",DestRect.x,DestRect.y);			  
			      DestRect.x=(int) (80*Zoom); DestRect.y=(int) (132*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString("Valider les modifications",DestRect.x,DestRect.y);
		      }
		      else
		      {
			      DestRect.x=(int) (10*Zoom); DestRect.y=(int) (67*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString(texte,DestRect.x,DestRect.y);
			      DestRect.x=(int) (10*Zoom); DestRect.y=(int) (80*Zoom+fontMetrics.getHeight());
	    		  g2d.drawString(texte2,DestRect.x,DestRect.y);			  
		      }
		      g2d.dispose();
	  	      Image=textesurf;
		}
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
    		if ((IsStatActive==false)||(IsMenuActive==false))
    		{
    			if (curs!=null)
    				curs.Kill();
    			Kill();
    			IsStatActive=false;
    			return;
    		}
    		if (indefinekeys==true)
    		{
    			if (keycode>=0)
    			{
    				switch(etape)
    				{
    					case 0 :
    						break;
    					case 1 :
    						lKUP=keycode;
    						Redraw("Appuyer sur la touche pour se déplacer vers le bas","Defaut : Touche Fleche vers le bas");
    						break;
    					case 2 :
    						lKDOWN=keycode;
    						Redraw("Appuyer sur la touche pour se déplacer vers la gauche","Defaut : Touche Fleche vers la gauche");
    						break;
    					case 3 :
    						lKLEFT=keycode;
    						Redraw("Appuyer sur la touche pour se déplacer vers la droite","Defaut : Touche Fleche vers la droite");
    						break;
    					case 4 :
    						lKRIGHT=keycode;
    						Redraw("Appuyer sur la touche pour interargir avec le monde","(passer une porte, discuter avec un personnage...) Defaut : Espace");
    						break;
    					case 5 :
    						lKSPACE=keycode;
    						Redraw("Appuyer sur la touche pour valider les menus","Defaut : Entrée");
    						break;
    					case 6 :
    						lKRETURN=keycode;
    						Redraw("Appuyer sur la touche pour activer le menu"," et quitter les différentes fenêtres Defaut : Echap");
    						break;
    					case 7 :
    						lKESCAPE=keycode;
    						Redraw("Appuyer sur la touche pour attaquer","Defaut : S");
    						break;
    					case 8 :
    						lKS=keycode;
    						Redraw("Appuyer sur la touche pour parer un coup","Defaut : D");
    						break;
    					case 9 :
    						lKD=keycode;
    						Redraw("Appuyer sur la touche pour le 1er raccourci","Defaut : 1");
    						break;
    					case 10 :
    						lK0=keycode;
    						Redraw("Appuyer sur la touche pour le 2eme raccourci","Defaut : 2");
    						break;
    					case 11 :
    						lK1=keycode;
    						Redraw("Appuyer sur la touche pour le 3eme raccourci","Defaut : 3");
    						break;
    					case 12 :
    						lK2=keycode;
    						Redraw("Appuyer sur la touche pour le 4eme raccourci","Defaut : 4");
    						break;
    					case 13 :
    						lK3=keycode;
    						Redraw("Appuyer sur la touche pour le 5eme raccourci","Defaut : 5");
    						break;
    					case 14 :
    						lK4=keycode;
    						Redraw("Appuyer sur la touche pour le 6eme raccourci","Defaut : 6");
    						break;
    					case 15 :
    						lK5=keycode;
    						Redraw("Appuyer sur la touche pour le 7eme raccourci","Defaut : 7");
    						break;
    					case 16 :
    						lK6=keycode;
    						Redraw("Appuyer sur la touche pour le 8eme raccourci","Defaut : 8");
    						break;
    					case 17 :
    						lK7=keycode;
    						Redraw("Appuyer sur la touche pour le 9eme raccourci","Defaut : 9");
    						break;
    					case 18 :
    						lK8=keycode;
    						Redraw("Appuyer sur la touche pour le 10eme raccourci","Defaut : 0");
    						break;
    					case 19 :
    						lK9=keycode;
    						break;
    				}
    				etape++;
    				if (etape==20)
    				{
    					indefinekeys=false;
	    	    		Source.removeKeyListener(keyListener);
	    	    		Source.addKeyListener(keyboard);
	    	    		etape=0;
	    	  		    curs=new Curseur(Source,200,z+1);
		    	  		SpriteE.AddSprite(curs);
	    	  		    Redraw("","");
	    	    	    Attente= 0;
    				}
    				keycode=-1;
    			}
    		}
    		if ((curs==null)||(IsInConfirm==true))
	    	{
	    	    Attente=0;
	    	    return;
	    	}
    		if (InConfirm==true)
    		{
   				IsStatActive=false;
   				InConfirm=false;
    		}
    		if (curs.isDead)
	    	{
    			curs=null;
	    	    Attente=0;
	    	    return;
	    	}
			curs.x=(int) (62*Zoom);
			curs.y=(int) (((curs.PositionY*13)+69)*Zoom);
	    	if ((keys[KUP] == KeyState.PRESSED) && (curs.PositionY > 0) && (Attente >= 10))
	    	{
	    	    curs.PositionY--;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente = 0;
	    	}
	    	if ((keys[KDOWN] == KeyState.PRESSED) && (curs.PositionY < 5) && (Attente >= 10))
	    	{
	    	    curs.PositionY++;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente= 0;
	    	}
	    	if (((keys[KRETURN] == KeyState.ONCE) || (keys[KSPACE] == KeyState.ONCE)) && (Attente >= 10))
	    	{
	    	    switch(curs.PositionY)
	    	    {
	    	    	case 0 : ful=!ful; Redraw("",""); break;
	    	    	case 1 : 
	    	    		if(lezoom==0.0) lezoom=1;
	    	    		else if(lezoom==1) lezoom=2;
	    	    		else if(lezoom==2) lezoom=2.5;
	    	    		else if(lezoom==2.5) lezoom=3;
	    	    		else if(lezoom==3) lezoom=3.2;
	    	    		else if(lezoom==3.2) lezoom=4;
	    	    		else if(lezoom==4) lezoom=0;
	    	    		Redraw("","");
	    	    		break;
	    	    	case 2 : mus=!mus; Redraw("",""); break;
	    	    	case 3 : son=!son; Redraw("",""); break;
	    	    	case 4 :
	    	    		Source.addKeyListener(keyListener);
	    	    		Source.removeKeyListener(keyboard);
	    	    		curs.Kill();
	    	    		curs=null;
	    	    		indefinekeys=true;
	    	  		    etape=0;
	    	    		Redraw("Appuyer sur la touche pour se déplacer vers le haut","Defaut : Touche Fleche vers le haut");
	    	    		break;
	    	    	case 5 :
	    	    		if (son!=HasSound)
	    	    			HasSound=son;
	    	    		if (mus!=HasMusic)
	    	    		{
	    	    			HasMusic=mus;
	    	    			if (HasMusic==true)
	    	  		          PlayMusic(CurrentMap.Music,false);
	    	    			else
	    	  		          PlayMusic("",false);
	    	    		}
	    	    		if (lKS!=-1)
	    	    		{
		    	    		KS=lKS; KD=lKD; KLEFT=lKLEFT; KRIGHT=lKRIGHT;
		    	    		KUP=lKUP; KDOWN=lKDOWN; KRETURN=lKRETURN; 
		    	    		KSPACE=lKSPACE; KESCAPE=lKESCAPE;
		    	    		K0=lK0; K1=lK1; K2=lK2; K3=lK3; K4=lK4; K5=lK5; K6=lK6; K7=lK7; K8=lK8; K9=lK9;	    	    			
	    	    		}
		  				  Properties p = new Properties();
					      try {
					    	  p.put("Zoom", Double.toString(lezoom));
					    	  p.put("FullScreen", Integer.toString(((ful==true) ? 1 : 0 )));
					    	  p.put("Musique", Integer.toString(((HasMusic==true) ? 1 : 0 )));
					    	  p.put("Sound", Integer.toString(((HasSound==true) ? 1 : 0 )));
					    	  p.put("KS", Integer.toString(KS));
					    	  p.put("KD", Integer.toString(KD));
					    	  p.put("KLEFT", Integer.toString(KLEFT));
					    	  p.put("KRIGHT", Integer.toString(KRIGHT));
					    	  p.put("KUP", Integer.toString(KUP));
					    	  p.put("KDOWN", Integer.toString(KDOWN));
					    	  p.put("KRETURN", Integer.toString(KRETURN));
					    	  p.put("KSPACE", Integer.toString(KSPACE));
					    	  p.put("KESCAPE", Integer.toString(KESCAPE));
					    	  p.put("K0", Integer.toString(K0));
					    	  p.put("K1", Integer.toString(K1));
					    	  p.put("K2", Integer.toString(K2));
					    	  p.put("K3", Integer.toString(K3));
					    	  p.put("K4", Integer.toString(K4));
					    	  p.put("K5", Integer.toString(K5));
					    	  p.put("K6", Integer.toString(K6));
					    	  p.put("K7", Integer.toString(K7));
					    	  p.put("K8", Integer.toString(K8));
					    	  p.put("K9", Integer.toString(K9));
					    	  p.store(new FileOutputStream(System.getProperty("user.dir")+"/"+general.getName()+".ini"),"Fichier de configuration de "+general.getName());
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
						}
	    	    		if ((lezoom!=Zoom)||(ful!=HasFullScreen))
	    	    		{
	    	    			lezoom=Zoom;
	    	    			ful=HasFullScreen;
	    	    			SpriteE.AddSprite(new ConfirmDialog(Source, "Relancer le jeu pour le changement de résolution",2));
	    	    		}
	    	    		else
	    	    			SpriteE.AddSprite(new ConfirmDialog(Source, "Modifications effectuées",2));
	    	    		InConfirm=true;
	    	    		break;
	    	    }
	    		Attente=-1;
	    	}
	    	if (Attente < 10) Attente++;
	    }
		public void Draw(Graphics2D g)
		{
			  DestRect.x= x+ScreenX;
			  DestRect.y= y+ScreenY;
			  if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			  DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
			  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}
	
	public class Statistiques extends Sprite {
		Rectangle DestRect;
	    Curseur curs;
	    int Attente;
	    boolean InConfirm;
		public Statistiques(JFrame _Source)
		{
		  super("",(int)(260*Zoom),(int)(240*Zoom),_Source);
		  z=(int) ((240*Zoom)+(190*Zoom));
		  x=(int) (60*Zoom);
		  y=0;
		  Attente=0;
		  DestRect=new Rectangle();
		  InConfirm=false;
		  Redraw();
		  curs=new Curseur(_Source,200,z+1);
		  SpriteE.AddSprite(curs);
		}
	
		public void Redraw()
		{
			  BufferedImage textesurf;
  			  ImageFilter cif2;
			  ImageProducer improd;
			  textesurf=CreateWindow((int)(260*Zoom),(int)(240*Zoom),false);
		      Graphics2D g2d = textesurf.createGraphics();
	  	      g2d.setFont(new Font("Arial", Font.PLAIN,  (int) (10*Zoom)));
		      FontMetrics fontMetrics = g2d.getFontMetrics();
		      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (2*Zoom+fontMetrics.getHeight());
		      if (PlayerInfo.Name!=null)
		    	  if (PlayerInfo.Name.compareTo("")!=0)
		    		  g2d.drawString("Nom : "+PlayerInfo.Name,DestRect.x,DestRect.y);
    	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (15*Zoom+fontMetrics.getHeight());
    	      if (PlayerInfo.Classe!=null)
    	    	  g2d.drawString("Classe : "+PlayerInfo.Classe.Name,DestRect.x,DestRect.y);
    	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (28*Zoom+fontMetrics.getHeight());
	    	  g2d.drawString("Level : "+PlayerInfo.Lvl,DestRect.x,DestRect.y);
    	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (41*Zoom+fontMetrics.getHeight());
    	      if (PlayerInfo.CurrentXP > 100000)
    	    	  g2d.drawString("XP : "+(PlayerInfo.CurrentXP / 1000)+"K/"+(PlayerInfo.NextXP/1000)+"K",DestRect.x,DestRect.y);
    	      else
    	      if (PlayerInfo.CurrentXP > 10000000)
    	    	  g2d.drawString("XP : "+(PlayerInfo.CurrentXP / 1000000)+"M/"+(PlayerInfo.NextXP/1000000)+"M",DestRect.x,DestRect.y);
    	      else
  	    	    g2d.drawString("XP : "+PlayerInfo.CurrentXP+"/"+PlayerInfo.NextXP,DestRect.x,DestRect.y);
    	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (54*Zoom+fontMetrics.getHeight());
    	      g2d.drawString("Vie : "+PlayerInfo.Vie+"/"+PlayerInfo.VieMax,DestRect.x,DestRect.y);
    	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (67*Zoom+fontMetrics.getHeight());
    	      g2d.drawString("Magie : "+PlayerInfo.CurrentMag+"/"+PlayerInfo.MagMax,DestRect.x,DestRect.y);
    	      if (PlayerInfo.Arme>0)
    	      {
    	    	  if (general.getObjetByIndex(PlayerInfo.Arme-1).Chipset.compareTo("")==0)
    	    	  {
            	      DestRect.x=(int) (125*Zoom); DestRect.y=(int) (3*Zoom+fontMetrics.getHeight());
            	      g2d.drawString("Arme : "+general.getObjetByIndex(PlayerInfo.Arme-1).Name,DestRect.x,DestRect.y);    	    		  
    	    	  }
    	    	  else
    	    	  {
    	    		  Image ico=LoadImage(general.getName()+"/"+general.getObjetByIndex(PlayerInfo.Arme-1).Chipset.replace("Chipset\\", "Chipset/"), false);
    	    		  improd=ico.getSource();
    	    		  DestRect.x=general.getObjetByIndex(PlayerInfo.Arme-1).X; DestRect.y=general.getObjetByIndex(PlayerInfo.Arme-1).Y; 
    	    		  DestRect.width=general.getObjetByIndex(PlayerInfo.Arme-1).W; DestRect.height=general.getObjetByIndex(PlayerInfo.Arme-1).H;
    		    	  cif2 =new CropImageFilter(DestRect.x, DestRect.y,DestRect.width,DestRect.height);
    		    	  ico = createImage(new FilteredImageSource(improd, cif2));	  
            	      DestRect.x=(int) (125*Zoom); DestRect.y=(int) (3*Zoom);
    		    	  g2d.drawImage(ico,DestRect.x,DestRect.y,(int)(16*Zoom),(int) (16*Zoom), null);
            	      DestRect.x=(int) (145*Zoom); DestRect.y=(int) (3*Zoom+fontMetrics.getHeight());
            	      g2d.drawString(general.getObjetByIndex(PlayerInfo.Arme-1).Name,DestRect.x,DestRect.y);    	    		  
    	    	  }
    	      }
    	      else
    	      {
        	      DestRect.x=(int) (125*Zoom); DestRect.y=(int) (3*Zoom+fontMetrics.getHeight());
        	      g2d.drawString("Pas d'arme",DestRect.x,DestRect.y);
    	      }
    	      if (PlayerInfo.Armure>0)
    	      {
    	    	  if (general.getObjetByIndex(PlayerInfo.Armure-1).Chipset.compareTo("")==0)
    	    	  {
            	      DestRect.x=(int) (125*Zoom); DestRect.y=(int) (19*Zoom+fontMetrics.getHeight());
            	      g2d.drawString("Armure : "+general.getObjetByIndex(PlayerInfo.Armure-1).Name,DestRect.x,DestRect.y);    	    		  
    	    	  }
    	    	  else
    	    	  {
    	    		  Image ico=LoadImage(general.getName()+"/"+general.getObjetByIndex(PlayerInfo.Armure-1).Chipset.replace("Chipset\\", "Chipset/"), false);
    	    		  improd=ico.getSource();
    	    		  DestRect.x=general.getObjetByIndex(PlayerInfo.Armure-1).X; DestRect.y=general.getObjetByIndex(PlayerInfo.Armure-1).Y; 
    	    		  DestRect.width=general.getObjetByIndex(PlayerInfo.Armure-1).W; DestRect.height=general.getObjetByIndex(PlayerInfo.Armure-1).H;
    		    	  cif2 =new CropImageFilter(DestRect.x, DestRect.y,DestRect.width,DestRect.height);
    		    	  ico = createImage(new FilteredImageSource(improd, cif2));	  
            	      DestRect.x=(int) (125*Zoom); DestRect.y=(int) (19*Zoom);
    		    	  g2d.drawImage(ico,DestRect.x,DestRect.y,(int)(16*Zoom),(int)(16*Zoom), null);
            	      DestRect.x=(int) (145*Zoom); DestRect.y=(int) (19*Zoom+fontMetrics.getHeight());
            	      g2d.drawString(general.getObjetByIndex(PlayerInfo.Armure-1).Name,DestRect.x,DestRect.y);    	    		  
    	    	  }
    	      }
    	      else
    	      {
        	      DestRect.x=(int) (125*Zoom); DestRect.y=(int) (19*Zoom+fontMetrics.getHeight());
        	      g2d.drawString("Pas d'armure",DestRect.x,DestRect.y);    	    	  
    	      }
    	      if (PlayerInfo.Bouclier>0)
    	      {
    	    	  if (general.getObjetByIndex(PlayerInfo.Bouclier-1).Chipset.compareTo("")==0)
    	    	  {
            	      DestRect.x=(int) (125*Zoom); DestRect.y=(int) (35*Zoom+fontMetrics.getHeight());
            	      g2d.drawString("Bouclier : "+general.getObjetByIndex(PlayerInfo.Bouclier-1).Name,DestRect.x,DestRect.y);    	    		  
    	    	  }
    	    	  else
    	    	  {
    	    		  Image ico=LoadImage(general.getName()+"/"+general.getObjetByIndex(PlayerInfo.Bouclier-1).Chipset.replace("Chipset\\", "Chipset/"), false);
    	    		  improd=ico.getSource();
    	    		  DestRect.x=general.getObjetByIndex(PlayerInfo.Bouclier-1).X; DestRect.y=general.getObjetByIndex(PlayerInfo.Bouclier-1).Y; 
    	    		  DestRect.width=general.getObjetByIndex(PlayerInfo.Bouclier-1).W; DestRect.height=general.getObjetByIndex(PlayerInfo.Bouclier-1).H;
    		    	  cif2 =new CropImageFilter(DestRect.x, DestRect.y,DestRect.width,DestRect.height);
    		    	  ico = createImage(new FilteredImageSource(improd, cif2));	  
            	      DestRect.x=(int) (125*Zoom); DestRect.y=(int) (35*Zoom);
    		    	  g2d.drawImage(ico,DestRect.x,DestRect.y,(int)(16*Zoom),(int)(16*Zoom), null);
            	      DestRect.x=(int) (145*Zoom); DestRect.y=(int) (35*Zoom+fontMetrics.getHeight());
            	      g2d.drawString(general.getObjetByIndex(PlayerInfo.Bouclier-1).Name,DestRect.x,DestRect.y);    	    		  
    	    	  }    	    	  
    	      }
    	      else
    	      {
        	      DestRect.x=(int) (125*Zoom); DestRect.y=(int) (35*Zoom+fontMetrics.getHeight());
        	      g2d.drawString("Pas de bouclier",DestRect.x,DestRect.y);    	    	      	    	  
    	      }
    	      if (PlayerInfo.Casque>0)
    	      {
    	    	  if (general.getObjetByIndex(PlayerInfo.Casque-1).Chipset.compareTo("")==0)
    	    	  {
            	      DestRect.x=(int) (125*Zoom); DestRect.y=(int) (51*Zoom+fontMetrics.getHeight());
            	      g2d.drawString("Bouclier : "+general.getObjetByIndex(PlayerInfo.Bouclier-1).Name,DestRect.x,DestRect.y);    	    		  
    	    	  }
    	    	  else
    	    	  {
    	    		  Image ico=LoadImage(general.getName()+"/"+general.getObjetByIndex(PlayerInfo.Casque-1).Chipset.replace("Chipset\\", "Chipset/"), false);
    	    		  improd=ico.getSource();
    	    		  DestRect.x=general.getObjetByIndex(PlayerInfo.Casque-1).X; DestRect.y=general.getObjetByIndex(PlayerInfo.Casque-1).Y; 
    	    		  DestRect.width=general.getObjetByIndex(PlayerInfo.Casque-1).W; DestRect.height=general.getObjetByIndex(PlayerInfo.Casque-1).H;
    		    	  cif2 =new CropImageFilter(DestRect.x, DestRect.y,DestRect.width,DestRect.height);
    		    	  ico = createImage(new FilteredImageSource(improd, cif2));	  
            	      DestRect.x=(int) (125*Zoom); DestRect.y=(int) (51*Zoom);
    		    	  g2d.drawImage(ico,DestRect.x,DestRect.y,(int)(16*Zoom),(int)(16*Zoom), null);
            	      DestRect.x=(int) (145*Zoom); DestRect.y=(int) (51*Zoom+fontMetrics.getHeight());
            	      g2d.drawString(general.getObjetByIndex(PlayerInfo.Casque-1).Name,DestRect.x,DestRect.y);    	    		  
    	    	  }    	    	      	    	  
    	      }
    	      else
    	      {
        	      DestRect.x=(int) (125*Zoom); DestRect.y=(int) (51*Zoom+fontMetrics.getHeight());
        	      g2d.drawString("Pas de casque",DestRect.x,DestRect.y);    	    	  
    	      }
    	      // Les statistiques
    	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (93*Zoom+fontMetrics.getHeight());
    	      g2d.drawString("Point a distribuer : "+PlayerInfo.LvlPoint,DestRect.x,DestRect.y);    	      
    	      for(int i=0;i<general.getStatsBase().size();i++)
    	      {
        	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) ((106*Zoom)+(i*13*Zoom)+fontMetrics.getHeight());
        	      g2d.drawString(general.getStatsBase().get(i)+" : "+PlayerInfo.Stats.getProperty(general.getStatsBase().get(i),"0"),DestRect.x,DestRect.y);    	    	  
    	      }
		      g2d.dispose();
	  	      Image=textesurf;
		}
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
    		if ((IsStatActive==false)||(IsMenuActive==false))
    		{
    			if (curs!=null)
    				curs.Kill();
    			Kill();
    			IsStatActive=false;
    			return;
    		}
    		if ((curs==null)||(IsInConfirm==true))
	    	{
	    	    Attente=0;
	    	    return;
	    	}
    		if (curs.isDead)
	    	{
    			curs=null;
	    	    Attente=0;
	    	    return;
	    	}
    		if (curs.PositionX==0)
    		{
    			curs.x=(int) (62*Zoom);
    			curs.y=(int) (((curs.PositionY*13)+108)*Zoom);
    	    	if ((keys[KUP] == KeyState.PRESSED) && (curs.PositionY > 0) && (Attente >= 10))
    	    	{
    	    	    curs.PositionY--;
    	    	    PlaySound(DEFAULT_SOUND,"",false);
    	    	    Attente = 0;
    	    	}
    	    	if ((keys[KDOWN] == KeyState.PRESSED) && (curs.PositionY < general.getStatsBase().size()-1) && (Attente >= 10))
    	    	{
    	    	    curs.PositionY++;
    	    	    PlaySound(DEFAULT_SOUND,"",false);
    	    	    Attente= 0;
    	    	}
    	    	if (((keys[KRETURN] == KeyState.PRESSED) || (keys[KSPACE] == KeyState.PRESSED)) && (Attente >= 10))
    	    	{
    	    	    if (PlayerInfo.LvlPoint>0)
    	    	    {	
    	    	    	PlaySound(SOUND_VALIDE,"",false);
    	    	    	PlayerInfo.Stats.setProperty(general.getStatsBase().get(curs.PositionY), Integer.toString(Integer.parseInt(PlayerInfo.Stats.getProperty(general.getStatsBase().get(curs.PositionY),"0"))+1));
    	    	    	PlayerInfo.LvlPoint--;
    	    	    	RecalculCarac();
    	    	    	if (fiolevie!=null)
    	    	    		fiolevie.Redraw();
    	    	    	if (fiolemana!=null)
    	    	    		fiolemana.Redraw();
    	    	    	Redraw();
    	    	    }
    	    	    Attente=-1;
    	    	}
    		}
    		else
    		{
    			curs.x=(int) (180*Zoom);
    			curs.y=(int) (((curs.PositionY*16)+5)*Zoom);
    			if (InConfirm==true)
    			{
    				if (IsConfirmStatus==true)
    				{
    					int objet=-1;
    					switch(curs.PositionY)
    					{
    						case 0 :
    							objet=PlayerInfo.Arme;
    							break;
    						case 1 :
    							objet=PlayerInfo.Armure;
    							break;
    						case 2 :
    							objet=PlayerInfo.Bouclier;
    							break;
    						case 3 :
    							objet=PlayerInfo.Casque;
    							break;
    					}
    					if (objet>0)
    					{
    					    int i=0; boolean ok=false;
    					    while ((i<100)&&(ok==false))
    					    {
    						   if (PlayerInfo.Inventaire[i][0]==objet)
    						   {
    							   ok=true;
    							   PlayerInfo.Inventaire[i][1]++;
    							   if (PlayerInfo.Inventaire[i][1]>100)
    								   PlayerInfo.Inventaire[i][1]=100;
    						   }
    						   i++;
    					   }
    					   // Dans le cas ou on a pas déjà l'objet dans l'inventaire
    					   if (ok==false)
    					   {
    						    i=0; ok=false;
    						    while ((i<100)&&(ok==false))
    						    {
    							   if (PlayerInfo.Inventaire[i][0]==0)
    							   {
    								   ok=true;
    								   PlayerInfo.Inventaire[i][0]=(short) objet;
    								   PlayerInfo.Inventaire[i][1]=1;
    								   if (PlayerInfo.Inventaire[i][1]>100)
    									   PlayerInfo.Inventaire[i][1]=100;
    								   ok=true;
    							   }
    							   i++;
    						   }					   
    					   }
    					   if (ok==false)
    					   {
	   	    	    			InConfirm=true;
		    	    			SpriteE.AddSprite(new ConfirmDialog(Source, "Inventaire plein...",2));
    					   }
    					   else
    					   {
    						   switch(curs.PositionY)
    						   {
    						   		case 0 :
    						   			PlayerInfo.Arme=0;
    						   			break;
    						   		case 1 :
    						   			PlayerInfo.Armure=0;
    						   			break;
    						   		case 2 :
    						   			PlayerInfo.Bouclier=0;
    						   			break;
    						   		case 3 :
    						   			PlayerInfo.Casque=0;
    						   			break;
    						   }
    						   for(i=0;i<general.getStatsBase().size();i++)
    						   {
    							   if (general.getObjetByIndex(objet-1).Stats.get(i)>0)
    								   PlayerInfo.Stats.setProperty(general.getStatsBase().get(i),Integer.toString(Integer.parseInt(PlayerInfo.Stats.getProperty(general.getStatsBase().get(i),"0"))-general.getObjetByIndex(objet-1).Stats.get(i)));
    						   }
    						   RecalculCarac();
    	    	    	       if (fiolevie!=null)
    	    	    	    	  fiolevie.Redraw();
    	    	    	       if (fiolemana!=null)
    	    	    	    	  fiolemana.Redraw();
    						   Redraw();
    					   }
    					}
    				}
    				InConfirm=false;
    				IsConfirmStatus=false;
    			}
    	    	if ((keys[KUP] == KeyState.PRESSED) && (curs.PositionY > 0) && (Attente >= 10))
    	    	{
    	    	    curs.PositionY--;
    	    	    PlaySound(DEFAULT_SOUND,"",false);
    	    	    Attente = 0;
    	    	}
    	    	if ((keys[KDOWN] == KeyState.PRESSED) && (curs.PositionY < 3) && (Attente >= 10))
    	    	{
    	    	    curs.PositionY++;
    	    	    PlaySound(DEFAULT_SOUND,"",false);
    	    	    Attente= 0;
    	    	}
    	    	if (((keys[KRETURN] == KeyState.PRESSED) || (keys[KSPACE] == KeyState.PRESSED)) && (Attente >= 10))
    	    	{
    	    	    switch(curs.PositionY)
    	    	    {
    	    	    	case 0 :
    	    	    		if (PlayerInfo.Arme>0)
    	    	    		{
    	        	    	    PlaySound(SOUND_VALIDE,"",false);
    	    	    			InConfirm=true;
    	    	    			SpriteE.AddSprite(new ConfirmDialog(Source, "Voulez vous retirer votre arme?",0));
    	    	    		}
    	    	    		break;
    	    	    	case 1 :
    	    	    		if (PlayerInfo.Armure>0)
    	    	    		{
    	        	    	    PlaySound(SOUND_VALIDE,"",false);
    	    	    			InConfirm=true;
    	    	    			SpriteE.AddSprite(new ConfirmDialog(Source, "Voulez vous retirer votre armure?",0));    	    	    			
    	    	    		}
    	    	    		break;
    	    	    	case 2 :
    	    	    		if (PlayerInfo.Bouclier>0)
    	    	    		{
    	        	    	    PlaySound(SOUND_VALIDE,"",false);
    	    	    			InConfirm=true;
    	    	    			SpriteE.AddSprite(new ConfirmDialog(Source, "Voulez vous retirer votre bouclier?",0));    	    	    			
    	    	    		}
    	    	    		break;
    	    	    	case 3:
    	    	    		if (PlayerInfo.Casque>0)
    	    	    		{
    	        	    	    PlaySound(SOUND_VALIDE,"",false);
    	    	    			InConfirm=true;
    	    	    			SpriteE.AddSprite(new ConfirmDialog(Source, "Voulez vous retirer votre casque?",0));    	    	    			
    	    	    		}
    	    	    		break;
    	    	    }
    	    	    Attente=-1;
    	    	}
    		}
	    	if ((keys[KLEFT] == KeyState.PRESSED) && (curs.PositionX > 0) && (Attente >= 10))
	    	{
	    		curs.Kill();
		  		curs=new Curseur(Source,200,z+1);
				SpriteE.AddSprite(curs);
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente = 0;
	    	}
	    	if ((keys[KRIGHT] == KeyState.PRESSED) && (curs.PositionX == 0) && (Attente >= 10))
	    	{
	    		curs.Kill();
		  		curs=new Curseur(Source,138,z+1);
		  		curs.PositionX=1;
				SpriteE.AddSprite(curs);
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente = 0;
	    	}
	    	if (Attente < 10) Attente++;
	    }
		public void Draw(Graphics2D g)
		{
			  DestRect.x= x+ScreenX;
			  DestRect.y= y+ScreenY;
			  if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			  DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
			  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}

	public class Menu extends Sprite {
		Rectangle DestRect;
	    Curseur curs;
	    int Attente;
	    ArrayList<String> MenuItem;
		public Menu(JFrame _Source)
		{
		  super("",(int)(60*Zoom),(int)(10*Zoom),_Source);
		  BufferedImage textesurf;
		  DestRect=new Rectangle();
		  z=(int) ((240*Zoom)+(190*Zoom));
		  y=(int) (240*Zoom);
		  textesurf=CreateWindow((int)(60*Zoom),(int)((((PlayerInfo.Menu.size()-1)*15)+15)*Zoom),false);
	      Graphics2D g2d = textesurf.createGraphics();
  	      g2d.setFont(new Font("Arial", Font.PLAIN,  (int) (10*Zoom)));
  	      if ((Dead)||(Bloque))
  			  g2d.setColor(Color.BLACK);
  	      else
  			  g2d.setColor(Color.WHITE);
	      FontMetrics fontMetrics = g2d.getFontMetrics();
	      DestRect.x=(int) (5*Zoom); DestRect.y=(int) (2*Zoom+fontMetrics.getHeight());
	      MenuItem=new ArrayList<String>();
	      for(int i=0;i<MenuPossibles.size();i++)
	      {
		      if (PlayerInfo.Menu.indexOf(MenuPossibles.get(i))>=0)
		      {
		    	  g2d.drawString(MenuPossibles.get(i),DestRect.x,DestRect.y);
		    	  MenuItem.add(MenuPossibles.get(i));
		    	  DestRect.y+=13*Zoom;
		      }
	      }
	      h=textesurf.getHeight();
	      g2d.dispose();
	      Image=textesurf;
		  Attente=0;
		  curs=new Curseur(_Source,56,z+1);
		  SpriteE.AddSprite(curs);
		}
	
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
	    	if (y > ((8*CentreY*Zoom)-(h / 2)))
	    	    y=(int) (y-(4*Zoom));
	    	curs.x=(int) (2*Zoom);
	    	curs.y=(int) (((curs.PositionY*13)+3)*Zoom+y);
    		if (IsMenuActive==false)
    		{
    			if (curs!=null)
    				curs.Kill();
    			Kill();
    			return;
    		}
    		if (curs==null)
	    	{
	    	    Attente=0;
	    	    return;
	    	}
    		if (curs.isDead)
	    	{
    			curs=null;
	    	    Attente=0;
	    	    return;
	    	}
   		    if ((IsStatActive==true) || (IsInvActive==true))
   		    	return;
	    	if ((keys[KUP] == KeyState.PRESSED) && (curs.PositionY > 0) && (Attente >= 10))
	    	{
	    	    curs.PositionY--;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente = 0;
	    	}
	    	if ((keys[KDOWN] == KeyState.PRESSED) && (curs.PositionY < MenuItem.size()-1) && (Attente >= 10))
	    	{
	    	    curs.PositionY++;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente= 0;
	    	}
	    	if (((keys[KRETURN] == KeyState.PRESSED) || (keys[KSPACE] == KeyState.PRESSED)) && (Attente >= 10))
	    	{
	    	    PlaySound(SOUND_VALIDE,"",false);
	    	    switch(MenuPossibles.indexOf(MenuItem.get(curs.PositionY)))
	    	    {
	    	    	case 0 :
	    	    	      if ((!Dead)&&(!Bloque))
	    	    	      {
	    	    	    	  SpriteE.AddSprite(new InventaireMenu(Source));
	    	    	    	  IsInvActive=true;
	    	    	      }
	    	    	      break;
	    	    	case 1 :
	    	    	      if ((!Dead)&&(!Bloque))
	    	    	      {
	    	    	    	  SpriteE.AddSprite(new MagieMenu(Source));
	    	    	    	  IsInvActive=true;
	    	    	      }
	    	    	      break;
	    	    	case 2 :
	    	    	      if ((!Dead)&&(!Bloque))
	    	    	      {
	    	    	    	  SpriteE.AddSprite(new Statistiques(Source));
	    	    	    	  IsStatActive=true;
	    	    	      }
	    	    	      break;
	    	    	case 3 :
	    	    	      if ((!Dead)&&(!Bloque))
	    	    	      {
	    	    	    	  IsMenuActive=false;
	    	    	    	  ListeEv=new ArrayList<String>();
	    	    	    	  ListeEv.add("Chargement('')");
	    	    	    	  EventAttX=-1;
	    	    	    	  EventAttY=-1;
	    	    	    	  CurrentLigneEv=0;
	    	    	    	  DoEvenements(EventAttX, EventAttY, 0, ListeEv, CurrentLigneEv);
	    	    	      }
	    	    	      break;
	    	    	case 4 :
	    	    	      if ((!Dead)&&(!Bloque))
	    	    	      {
	    	    	    	  IsMenuActive=false;
	    	    	    	  ListeEv=new ArrayList<String>();
	    	    	    	  ListeEv.add("Sauvegarder('')");
	    	    	    	  EventAttX=-1;
	    	    	    	  EventAttY=-1;
	    	    	    	  CurrentLigneEv=0;
	    	    	    	  DoEvenements(EventAttX, EventAttY, 0, ListeEv, CurrentLigneEv);
	    	    	      }
	    	    	      break;
	    	    	case 5 :
	    	    	      if ((!Dead)&&(!Bloque))
	    	    	      {
	    	    	    	  SpriteE.AddSprite(new Options(Source));
	    	    	    	  IsStatActive=true;
	    	    	      }
	    	    		  break;
	    	    	case 6 :
	    	    		  Quit=true;
	    	    	      break; 
	    	    }
	    	    Attente=-1;
	    	}
	    	if (Attente < 10) Attente++;
	    }
		public void Draw(Graphics2D g)
		{
			  DestRect.x= x+ScreenX;
			  DestRect.y= y+ScreenY;
			  if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			  DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
			  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}
	public class EvSave extends Sprite {
		Rectangle DestRect;
	    Curseur curs;
	    int Attente,nbelement;
	    File[] files;
	    boolean Sauvegarde,InConfirm;
		public EvSave(JFrame _Source,boolean Sauve)
		{
		  super("",(int)(320*Zoom),(int)(10*Zoom),_Source);
		  BufferedImage textesurf;
		  Rectangle Dest;
		  int i;
		  Sauvegarde=Sauve;
		  DestRect=new Rectangle();
		  Dest=new Rectangle();
		  File dir = new File(System.getProperty("user.dir"));
		  FileFilter fileFilter = new FileFilter() { 
			 public boolean accept(File file) {
				 if (file.isDirectory())
					 return false;
				 String filename = file.getName(); 
				 if (filename.startsWith(general.getName()+"_"))
					return filename.endsWith(".sav");
				 else
					return false;				 
			 } 
		  }; 
		  files = dir.listFiles(fileFilter);
		  if (Sauvegarde)
			  nbelement=files.length+1;
		  else
			  nbelement=files.length;
		  if (nbelement==0)
		  {
			  Kill();
			  SpriteEv=null;
			  return;
		  }
		  IsInInputString=true;
		  InConfirm=false;
		  textesurf=CreateWindow((int)(320*Zoom),(int)(((nbelement*15)+18)*Zoom),false);
	      Graphics2D g2d = textesurf.createGraphics();
  	      g2d.setFont(new Font("Arial", Font.PLAIN,  (int) (10*Zoom)));
		  g2d.setColor(Color.WHITE);
	      FontMetrics fontMetrics = g2d.getFontMetrics();
		  Dest.x=(int) (5*Zoom); Dest.y=(int) (2*Zoom+fontMetrics.getHeight());
		  if (Sauve)
			  g2d.drawString("Sauvegarder sous...",Dest.x,Dest.y);
		  else
			  g2d.drawString("Chargement...",Dest.x,Dest.y);
		  for (i=0;i<files.length;i++)
		  {
		    Dest.x=(int) (5*Zoom); Dest.y=(int) ((17+(i*15))*Zoom+fontMetrics.getHeight());
		    String temp=files[i].getName().substring(general.getName().length()+1);
		    temp=temp.substring(0, temp.length()-4);
		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
	        Date d = new Date(files[i].lastModified());
	        temp+=" ("+sdf.format(d)+")";
		    g2d.drawString(temp, Dest.x,Dest.y);
		  }
		  if (Sauvegarde)
		  {
			  Dest.x=(int) (5*Zoom); Dest.y=(int) ((17+(files.length*15))*Zoom+fontMetrics.getHeight());
			  g2d.drawString("Vide...", Dest.x,Dest.y);
		  }
	      g2d.dispose();
	      h=textesurf.getHeight();
	      Image=textesurf;
		  z=(int) ((240*Zoom)+(160*Zoom));
		  x=0;
		  y=0;
		  Attente=0;
		  curs=new Curseur(_Source,316,z+1);
		  SpriteE.AddSprite(curs);
		}
	
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
	    	if ((curs.isDead) || (IsInInputString==false))
	    	{
	    	  Attente=0;
	    	  return;
	    	}
    		if ((curs==null)||(IsInConfirm==true))
	    	{
	    	    Attente=0;
	    	    return;
	    	}
			if (InConfirm==true)
			{
				IsMenuActive=false;
				if (IsConfirmStatus==true)
				{
				    IsInInputString=false;
	        	    Sauvegarder(System.getProperty("user.dir")+"/"+files[curs.PositionY].getName());
		    	    SpriteEv.Kill();
		    	    SpriteEv=null;
		    	    curs.Kill();
		    	    Attente=-1;
			    	  try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					return;
				}
				InConfirm=false;
				IsConfirmStatus=false;
			}
	    	curs.x=(int) (2*Zoom);
	    	curs.y=(int) (((curs.PositionY*15)+18)*Zoom);
	    	if ((keys[KUP] == KeyState.PRESSED) && (curs.PositionY > 0) && (Attente >= 10))
	    	{
	    	    curs.PositionY--;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente = 0;
	    	}
	    	if ((keys[KDOWN] == KeyState.PRESSED) && (curs.PositionY < nbelement-1) && (Attente >= 10))
	    	{
	    	    curs.PositionY++;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente= 0;
	    	}
	    	if (((keys[KRETURN] == KeyState.ONCE) || (keys[KSPACE] == KeyState.ONCE)) && (Attente >= 10))
	    	{
	    	    PlaySound(SOUND_VALIDE,"",false);
	    	    if (Sauvegarde)
	    	    {
		    	    if (curs.PositionY<files.length)
		    	    {
		    	    	IsMenuActive=true;
		    	    	InConfirm=true;
		    			SpriteE.AddSprite(new ConfirmDialog(Source, "Remplacer cette sauvegarde?",0));
		    	    }
		    	    else
		    	    {
			    	    AttenteEvenement=5;
			    	    SpriteEv.Kill();
			    	    SpriteEv=null;
			    	    curs.Kill();
			            SpriteE.AddSprite(new EvInputString(FrmSrc,"Nom de la sauvegarde..."));
		    	    }
	    	    }
	    	    else
	    	    {
		        	Chargement(System.getProperty("user.dir")+"/"+files[curs.PositionY].getName());
		    	    SpriteEv.Kill();
		    	    SpriteEv=null;
		    	    curs.Kill();
		    	    ArrayList<String> StringList=ListeEv;
		    	    while(StringList.size()>CurrentLigneEv+1)
		    	    	StringList.remove(CurrentLigneEv+1);
		    	    StringList.set(CurrentLigneEv,"Teleport("+PlayerInfo.CurrentMap+","+(PlayerInfo.pmapx / 2)+","+(PlayerInfo.pmapy / 2)+")");
		    	    AttenteEvenement=0;
			        DoEvenements(EventAttX,EventAttY,EventEv,ListeEv,CurrentLigneEv);
	    	    }
	    	    Attente=-1;
		    	  try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    	}
	    	if (Attente < 10) Attente++;
	    }
		public void Draw(Graphics2D g)
		{
			  DestRect.x= x+ScreenX;
			  DestRect.y= y+ScreenY;
			  if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			  DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
			  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}
	public class InputQuery extends Sprite {
		Rectangle DestRect;
	    Curseur curs;
	    int Attente;
		String[] Said;
		public InputQuery(JFrame _Source,String Message_)
		{
		  super("",(int)(320*Zoom),(int)(10*Zoom),_Source);
		  BufferedImage textesurf;
		  Rectangle Dest;
		  int i;
		  DestRect=new Rectangle();
		  Dest=new Rectangle();
		  if (!Message_.startsWith("'"))
		  {
			 String temp=Message_.substring(0,Message_.indexOf(","));
			 x=(int) (Integer.parseInt(temp)*Zoom);
			 Message_=Message_.substring(Message_.indexOf(",")+1);
			 temp=Message_.substring(0,Message_.indexOf(","));
			 y=(int) (Integer.parseInt(temp)*Zoom);
			 Message_=Message_.substring(Message_.indexOf(",")+1);
			 w=(int) ((320*Zoom)-(x*2));
		  }
		  else
		  {
			  x=0;
			  y=0;
		  }
		  Said=Message_.split("','");
		  Said[0]=Said[0].substring(1);
		  Said[Said.length-1]=Said[Said.length-1].substring(0,Said[Said.length-1].length()-1);
		  IsInInputString=true;
		  
		  textesurf=CreateWindow(w,(int)((((Said.length-1)*15)+18)*Zoom),false);
	      Graphics2D g2d = textesurf.createGraphics();
  	      g2d.setFont(new Font("Arial", Font.PLAIN,  (int) (10*Zoom)));
		  g2d.setColor(Color.WHITE);
	      FontMetrics fontMetrics = g2d.getFontMetrics();
		  Dest.x=(int) (5*Zoom); Dest.y=(int) (2*Zoom+fontMetrics.getHeight());
		  g2d.drawString(Said[0],Dest.x,Dest.y);
		  for (i=1;i<Said.length;i++)
		  {
		    Dest.x=(int) (5*Zoom); Dest.y=(int) ((17+((i-1)*15))*Zoom+fontMetrics.getHeight());
		    g2d.drawString(Said[i], Dest.x,Dest.y);
		  }
	      g2d.dispose();
	      h=textesurf.getHeight();
	      Image=textesurf;
		  z=(int) ((240*Zoom)+(160*Zoom));
		  Attente=0;
		  curs=new Curseur(_Source,(w/Zoom)-4,z+1);
		  SpriteE.AddSprite(curs);
		}
	
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
	    	if ((curs.isDead) || (IsInInputString==false))
	    	{
	    	  Attente=0;
	    	  return;
	    	}
	    	curs.x=(int) (x+(2*Zoom));
	    	curs.y=(int) (y +((curs.PositionY*15)+18)*Zoom);
	    	if ((keys[KUP] == KeyState.PRESSED) && (curs.PositionY > 0) && (Attente >= 10))
	    	{
	    	    curs.PositionY--;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente = 0;
	    	}
	    	if ((keys[KDOWN] == KeyState.PRESSED) && (curs.PositionY < Said.length-2) && (Attente >= 10))
	    	{
	    	    curs.PositionY++;
	    	    PlaySound(DEFAULT_SOUND,"",false);
	    	    Attente= 0;
	    	}
	    	if (((keys[KRETURN] == KeyState.ONCE) || (keys[KSPACE] == KeyState.ONCE)) && (Attente >= 10))
	    	{
	    	    IsInInputString=false;
	    	    PlaySound(SOUND_VALIDE,"",false);
	    	    ResultQueryEv=Said[curs.PositionY+1];
	    	    SpriteEv.Kill();
	    	    SpriteEv=null;
	    	    curs.Kill();
	    	    Attente=-1;
		    	  try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    	}
	    	if (Attente < 10) Attente++;
	    }
		public void Draw(Graphics2D g)
		{
			  DestRect.x= x+ScreenX;
			  DestRect.y= y+ScreenY;
			  if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			  DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
			  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}
	public class MDead extends Sprite {

		Image[][][] Rects;
		int Alpha,mmapx,mmapy,mrealx,mrealy,Direction;
		Rectangle DestRect;
		
		public MDead(JFrame _Source,TMChar parent)
	    {
	    	super("",parent.w,parent.h,_Source);
		    ID=Sprite.idSprite.idAnimation;
		    Rects=parent.Rects;
		    DestRect=new Rectangle();
		    Alpha=240;
		    x = (int) ((8 * (parent.mmapx + CentreX + PlayerInfo.CentreX -(ScX / 8) - PlayerInfo.pmapx))*Zoom);
		    y = (int) ((8 * (parent.mmapy + CentreY + PlayerInfo.CentreY -(ScY / 8) - PlayerInfo.pmapy))*Zoom);
		    mmapx = parent.mmapx;
		    mmapy = parent.mmapy;
		    Direction = parent.Direction;
		    mrealx= parent.mrealx;
		    mrealy= parent.mrealy;    	
	    }
	
		public void Draw(Graphics2D g)
		{
			  if (Alpha>2)
			  {
				    DestRect.x=(int) (x - ((w-(16*Zoom)) / 2) +ScreenX);
				    DestRect.y=(int) (y - h + (8*Zoom*(h / (16*Zoom))) + ScreenY);
					if (Transparency!=1)
						g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
					DrawImage(g,Rects[0][Direction][1],DestRect.x,DestRect.y,w,h, null);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
			  }
		}

	    public void Move(KeyboardInput.KeyState[] keys)
	    {
			  if (((mmapx - PlayerInfo.pmapx+PlayerInfo.CentreX-(ScX / 8))<NDetect) ||
			      ((mmapx - PlayerInfo.pmapx+PlayerInfo.CentreX-(ScX / 8))>Detect) ||
			      ((mmapy - PlayerInfo.pmapy+PlayerInfo.CentreY-(ScY / 8))<NDetect) ||
			      ((mmapy - PlayerInfo.pmapy+PlayerInfo.CentreY-(ScY / 8))>Detect))
			  {
			     Kill();
			     return;
			  }
		
			  if (Alpha >2)
			  {
				  Alpha-=2;
				  Transparency=(float) Alpha/240;
			  }
			  else
				  Kill();
			  x = (int) ((8  * (mmapx + CentreX + PlayerInfo.CentreX - PlayerInfo.pmapx) - prealx -ScX + mrealx)*Zoom);
			  y = (int) ((8 *(mmapy + CentreY + PlayerInfo.CentreY - PlayerInfo.pmapy+1-((h / (16*Zoom))-1)) - prealy -ScY + mrealy)*Zoom);
			  z=y;
		}
	}
	
	public class Degat extends Sprite {
		Rectangle DestRect;
		Sprite parent;
		int MoveY,TexteDelay;
		
	    public Degat(JFrame _Source,String Degat,Sprite Parent_,Color color)
	    {
	    	super("",0,0,_Source);
			parent = Parent_;
			GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
			BufferedImage Surface=gc.createCompatibleImage(1,1);
	        Graphics2D g2d = Surface.createGraphics();
	    	g2d.setFont(new Font("Arial", Font.PLAIN,  (int) (10*Zoom)));
	    	FontMetrics fontMetrics = g2d.getFontMetrics();
	    	w=fontMetrics.stringWidth(Degat);
	    	h=fontMetrics.getHeight();
	    	g2d.dispose();
			Surface=gc.createCompatibleImage(w,h,java.awt.Transparency.TRANSLUCENT);
		    g2d = Surface.createGraphics();
	    	g2d.setFont(new Font("Arial", Font.PLAIN,  (int) (10*Zoom)));
		    g2d.setColor(color);
			DestRect=new Rectangle();
	        g2d.drawString(Degat,0,h);
	    	g2d.dispose();
		    Image=Surface;
			ID=Sprite.idSprite.idDegat;
			MoveY=0;
			z=(int) ((240*Zoom)+(150*Zoom));
			TexteDelay = 0;
	    }
	
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
			  if ((parent==null) || (parent.isDead))
			  {
			    Kill();
			    return;
			  }
			  MoveY=(int) ((TexteDelay*Zoom) / 2);
			  x = (int) (parent.x - ((parent.w-(16*Zoom)) / 2)+ (parent.w / 2)-(w / 2));
			  y = parent.y -(parent.h / 2) - MoveY;
			  TexteDelay++;
			  if (TexteDelay > (MaxTexteDelay / 4))
			  {
			    parent=null;
			    Kill();
			  }
	    }
	
		public void Draw(Graphics2D g)
		{
			  DestRect.x= x+ScreenX;
			  DestRect.y= y+ScreenY;
			  if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			  DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
			  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}

	public class BarreIcone extends Sprite {
		Rectangle DestRect;
		ImageFilter cif2;
		ImageProducer improd;
		boolean SomeIcon;
	    public BarreIcone(JFrame _Source)
	    {
			super("",(int)(160*Zoom),(int)(16*Zoom),_Source);
		    DestRect=new Rectangle();
		    x = (int) (72*Zoom);
			y = (int) ((240-15-15)*Zoom);
			z =(int) ((240*Zoom)+(160*Zoom));
			Redraw();
	    }
	    public void Redraw()
	    {
	    	SomeIcon=false;
	    	BufferedImage Surface;
	    	Image ico;
	    	Image cadre=CreateWindow((int)(16*Zoom), (int)(16*Zoom), true);
	    	GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
	    	Surface=gc.createCompatibleImage(w,h,java.awt.Transparency.TRANSLUCENT);
	    	Graphics2D g2d = Surface.createGraphics();
	    	g2d.setFont(new Font("Arial", Font.PLAIN,  (int) (5*Zoom)));
		    g2d.setColor(Color.BLACK);
	    	for(int i=0;i<10;i++)
	    	{
	    		if (PlayerInfo.Raccourcis[i]>0)
	    		{
	    			SomeIcon=true;
	    			if (general.getObjetByIndex(PlayerInfo.Inventaire[PlayerInfo.Raccourcis[i]-1][0]-1).Chipset.compareTo("")==0)
	    			{
	    				BufferedImage icot=gc.createCompatibleImage((int)(16*Zoom),(int)(16*Zoom),java.awt.Transparency.TRANSLUCENT);
	    				Graphics2D g2dicot=icot.createGraphics();
	    		    	g2dicot.setFont(new Font("Arial", Font.PLAIN,  (int) (10*Zoom)));
	    			    g2dicot.setColor(Color.WHITE);
	    		        g2dicot.drawString(general.getObjetByIndex(PlayerInfo.Inventaire[PlayerInfo.Raccourcis[i]-1][0]-1).Name,(int)(3*Zoom),(int)(12*Zoom));
	    		        g2dicot.dispose();
	    		        ico=icot;
	    			}
	    			else
	    			{
			    		ico=LoadImage(general.getName()+"/"+general.getObjetByIndex(PlayerInfo.Inventaire[PlayerInfo.Raccourcis[i]-1][0]-1).Chipset.replace("Chipset\\", "Chipset/"), false);
			    		improd=ico.getSource();
			    		DestRect.x=general.getObjetByIndex(PlayerInfo.Inventaire[PlayerInfo.Raccourcis[i]-1][0]-1).X; DestRect.y=general.getObjetByIndex(PlayerInfo.Inventaire[PlayerInfo.Raccourcis[i]-1][0]-1).Y; 
			    		DestRect.width=general.getObjetByIndex(PlayerInfo.Inventaire[PlayerInfo.Raccourcis[i]-1][0]-1).W; DestRect.height=general.getObjetByIndex(PlayerInfo.Inventaire[PlayerInfo.Raccourcis[i]-1][0]-1).H;
			    		cif2 =new CropImageFilter(DestRect.x, DestRect.y,DestRect.width,DestRect.height);
			    		ico = createImage(new FilteredImageSource(improd, cif2));
	    			}
		    		DestRect.x=(int) (i*16*Zoom); DestRect.y=0;
		    		g2d.drawImage(ico,DestRect.x,DestRect.y,(int)(16*Zoom),(int)(16*Zoom), null);
		    		g2d.drawImage(cadre,DestRect.x,DestRect.y,(int)(16*Zoom),(int)(16*Zoom), null);		    		
    		        g2d.drawString((i==9) ? Integer.toString(0) : Integer.toString(i+1),(int)(i*16*Zoom+(7*Zoom)),(int)(15*Zoom));
	    		}
	    		if (PlayerInfo.Raccourcis[i]<0)
	    		{
	    			SomeIcon=true;
	    			if (general.getMagieByIndex(PlayerInfo.OwnSpell[Math.abs(PlayerInfo.Raccourcis[i])-1]-1).Chipset.compareTo("")==0)
	    			{
	    				BufferedImage icot=gc.createCompatibleImage((int)(16*Zoom),(int)(16*Zoom),java.awt.Transparency.TRANSLUCENT);
	    				Graphics2D g2dicot=icot.createGraphics();
	    		    	g2dicot.setFont(new Font("Arial", Font.PLAIN,  (int) (10*Zoom)));
	    			    g2dicot.setColor(Color.WHITE);
	    		        g2dicot.drawString(general.getMagieByIndex(PlayerInfo.OwnSpell[Math.abs(PlayerInfo.Raccourcis[i])-1]-1).Name,(int)(3*Zoom),(int)(12*Zoom));
	    		        g2dicot.dispose();
	    		        ico=icot;
	    			}
	    			else
	    			{
			    		ico=LoadImage(general.getName()+"/"+general.getMagieByIndex(PlayerInfo.OwnSpell[Math.abs(PlayerInfo.Raccourcis[i])-1]-1).Chipset.replace("Chipset\\", "Chipset/"), false);
			    		improd=ico.getSource();
			    		DestRect.x=general.getMagieByIndex(PlayerInfo.OwnSpell[Math.abs(PlayerInfo.Raccourcis[i])-1]-1).X; DestRect.y=general.getMagieByIndex(PlayerInfo.OwnSpell[Math.abs(PlayerInfo.Raccourcis[i])-1]-1).Y; 
			    		DestRect.width=general.getMagieByIndex(PlayerInfo.OwnSpell[Math.abs(PlayerInfo.Raccourcis[i])-1]-1).W; DestRect.height=general.getMagieByIndex(PlayerInfo.OwnSpell[Math.abs(PlayerInfo.Raccourcis[i])-1]-1).H;
			    		cif2 =new CropImageFilter(DestRect.x, DestRect.y,DestRect.width,DestRect.height);
			    		ico = createImage(new FilteredImageSource(improd, cif2));
	    			}
		    		DestRect.x=(int) (i*16*Zoom); DestRect.y=0;
		    		g2d.drawImage(ico,DestRect.x,DestRect.y,(int)(16*Zoom),(int)(16*Zoom), null);
		    		g2d.drawImage(cadre,DestRect.x,DestRect.y,(int)(16*Zoom),(int)(16*Zoom), null);		    		
    		        g2d.drawString((i==9) ? Integer.toString(0) : Integer.toString(i+1),(int)(i*16*Zoom+(7*Zoom)),(int)(15*Zoom));
	    		}
	    	}
	    	Image=Surface;
	    	g2d.dispose();
	    }
		public void Draw(Graphics2D g)
		{
			if (SomeIcon==true)
			{
				  DestRect.x= x+ScreenX;
				  DestRect.y= y+ScreenY;
				  if (Transparency!=1)
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
				  DrawImage(g,Image,DestRect.x,DestRect.y,w,h, null);
				  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
			}
		}
	}
	public class FioleVie extends Sprite {
	    Image[] Rects;
		Rectangle DestRect;
		int attenteanim,animphase;
		ImageFilter cif2;
		ImageProducer improd;
		
	    public FioleVie(JFrame _Source)
	    {
			super(general.getName()+"/Chipset/fioles.png",(int)(16*Zoom),(int)(72*Zoom),_Source);
			Rects=new Image[3];
		    DestRect=new Rectangle();
		    x = 0;
			y = (int) ((240-72-15)*Zoom);
			z =(int) ((240*Zoom)+(160*Zoom));
			Redraw();
	    }
	    public void Redraw()
	    {
	    	 int i, percentwidth;
	    	 Rectangle Src,Dest;
	    	 BufferedImage Surface;
	    	 Image TileW;
	    	 if (PlayerInfo.VieMax==0) return;
	    	 Src=new Rectangle();
	    	 Dest=new Rectangle();
	    	 percentwidth=Math.round((48*PlayerInfo.Vie)/PlayerInfo.VieMax);
    		 improd=Image.getSource();
    		 GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
	    	 for (i=0;i<3;i++)
	    	 {
	    		 Surface=gc.createCompatibleImage(w,h,java.awt.Transparency.TRANSLUCENT);
	    	     Graphics2D g2d = Surface.createGraphics();
	    	     // on construit la fiole. tout d'abord on dessine le haut.
	    		 Src.x=0; Src.y=0; Src.width=16; Src.height=21;
	    		 Dest.x = 0; Dest.y = 0;
	    		 cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
	    		 TileW = createImage(new FilteredImageSource(improd, cif2));	  
	    		 TileW = TileW.getScaledInstance((int)(Src.width*Zoom), (int)(Src.height*Zoom), BufferedImage.SCALE_FAST);
	    		 g2d.drawImage(TileW,Dest.x,Dest.y,(int)(Src.width*Zoom),(int)(Src.height*Zoom), null);
	    	      // maintenant qu'on a dessiner le haut on dessine le haut de la fiole vide.
	    		 if (percentwidth!=48)
	    		 {
		    	     Src.x=16; Src.y=(21)+(72*i); Src.width=16; Src.height=(48-percentwidth);
		    	     if(Zoom==3.2 || Zoom==2.5) 
		    	    	 Src.height++;
		    		 Dest.x=0; Dest.y=(int) (21*Zoom);
		    		 cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
		    		 TileW = createImage(new FilteredImageSource(improd, cif2));	  
		    		 TileW = TileW.getScaledInstance((int)(Src.width*Zoom), (int)(Src.height*Zoom), BufferedImage.SCALE_FAST);
		    		 g2d.drawImage(TileW,Dest.x,Dest.y,(int)(Src.width*Zoom), (int)(Src.height*Zoom), null);
	    		 }
	    	      // maintenant on dessine la fiole pleine plus le bas
	    	     Src.x=0; Src.y=((21+(48-percentwidth))+(72*i)); Src.width=16; Src.height=(percentwidth+2) ;
	    		 Dest.x=0; Dest.y=(int) ((21+(48-percentwidth))*Zoom);
	    		 cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
	    		 TileW = createImage(new FilteredImageSource(improd, cif2));	  
	    		 TileW = TileW.getScaledInstance((int)(Src.width*Zoom), (int)(Src.height*Zoom), BufferedImage.SCALE_FAST);
	    		 g2d.drawImage(TileW,Dest.x,Dest.y,(int)(Src.width*Zoom), (int) (Src.height*Zoom), null);
	    		 g2d.dispose();
	    		 Rects[i]=Surface;
	    	 }
	    }
		public void Draw(Graphics2D g)
		{
    	   if (PlayerInfo.VieMax==0) return;
		  attenteanim++;
		  if (attenteanim > 32)
		  {
		    attenteanim=0;
		    animphase++;
		    if (animphase==3) animphase=0;
		  }
		  DestRect.x= x+ScreenX;
		  DestRect.y= y+ScreenY;
		  if (Transparency!=1)
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
		  DrawImage(g,Rects[animphase],DestRect.x,DestRect.y,w,h, null);
		  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}

	public class FioleMana extends Sprite {
	    Image[] Rects;
		Rectangle DestRect;
		int attenteanim,animphase;
		ImageFilter cif2;
		ImageProducer improd;
		
	    public FioleMana(JFrame _Source)
	    {
			super(general.getName()+"/Chipset/fioles.png",(int)(16*Zoom),(int)(72*Zoom),_Source);
			Rects=new Image[3];
		    DestRect=new Rectangle();
		    x = (int) ((320-16)*Zoom);
		    y = (int) ((240-72-15)*Zoom);
		    z = (int) ((240*Zoom)+(160*Zoom));
			Redraw();
	    }
	    public void Redraw()
	    {
	    	 int i, percentwidth;
	    	 Rectangle Src,Dest;
	    	 BufferedImage Surface;
	    	 Image TileW;
	    	 if (PlayerInfo.MagMax==0) return;
	    	 Src=new Rectangle();
	    	 Dest=new Rectangle();
	    	 percentwidth=Math.round((48*PlayerInfo.CurrentMag)/PlayerInfo.MagMax);
    		 improd=Image.getSource();
    		 GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
	    	 for (i=0;i<3;i++)
	    	 {
	    		 Surface=gc.createCompatibleImage(w,h,java.awt.Transparency.TRANSLUCENT);
	    	     Graphics2D g2d = Surface.createGraphics();
	    	     // on construit la fiole. tout d'abord on dessine le haut.
	    		 Src.x=32; Src.y=0; Src.width=16; Src.height=21;
	    		 Dest.x = 0; Dest.y = 0;
	    		 cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
	    		 TileW = createImage(new FilteredImageSource(improd, cif2));	  
	    		 TileW = TileW.getScaledInstance((int)(Src.width*Zoom), (int)(Src.height*Zoom), BufferedImage.SCALE_FAST);
	    		 g2d.drawImage(TileW,Dest.x,Dest.y,(int)(Src.width*Zoom),(int)(Src.height*Zoom), null);	    		
	    	      // maintenant qu'on a dessiner le haut on dessine le haut de la fiole vide.
	    		 if (percentwidth!=48)
	    		 {
		    	     Src.x=48; Src.y=(21)+(72*i); Src.width=16; Src.height=(48-percentwidth);
		    	     if(Zoom==3.2 || Zoom==2.5) 
		    	    	 Src.height++;
		    		 Dest.x=0; Dest.y=(int) (21*Zoom);
		    		 cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
		    		 TileW = createImage(new FilteredImageSource(improd, cif2));	  
		    		 TileW = TileW.getScaledInstance((int)(Src.width*Zoom), (int)(Src.height*Zoom), BufferedImage.SCALE_FAST);
		    		 g2d.drawImage(TileW,Dest.x,Dest.y,(int)(Src.width*Zoom), (int)(Src.height*Zoom), null);
	    		 }
	    	      // maintenant on dessine la fiole pleine plus le bas
	    	     Src.x=32; Src.y=((21+(48-percentwidth))+(72*i)); Src.width=16; Src.height=(percentwidth+2) ;
	    		 Dest.x=0; Dest.y=(int) ((21+(48-percentwidth))*Zoom);
	    		 cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
	    		 TileW = createImage(new FilteredImageSource(improd, cif2));	  
	    		 TileW = TileW.getScaledInstance((int)(Src.width*Zoom), (int)(Src.height*Zoom), BufferedImage.SCALE_FAST);
	    		 g2d.drawImage(TileW,Dest.x,Dest.y,(int)(Src.width*Zoom), (int)(Src.height*Zoom), null);
	    		 g2d.dispose();
	    		 Rects[i]=Surface;
	    	 }
	    }
		public void Draw(Graphics2D g)
		{
    	  if (PlayerInfo.VieMax==0) return;
		  attenteanim++;
		  if (attenteanim > 32)
		  {
		    attenteanim=0;
		    animphase++;
		    if (animphase==3) animphase=0;
		  }
		  DestRect.x= x+ScreenX;
		  DestRect.y= y+ScreenY;
		  if (Transparency!=1)
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
		  DrawImage(g,Rects[animphase],DestRect.x,DestRect.y,w,h, null);
		  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}
	
	public class BarreXP extends Sprite {
	    Image Rects;
		Rectangle DestRect;
		ImageFilter cif2;
		ImageProducer improd;
		
	    public BarreXP(JFrame _Source)
	    {
			super(general.getName()+"/Chipset/xp.png",(int)(320*Zoom),(int)(15*Zoom),_Source);
		    DestRect=new Rectangle();
		    x=0;
		    y=(int) ((240-15)*Zoom);
		    z=(int) ((240*Zoom)+(180*Zoom));
			Redraw();
	    }
	    public void Redraw()
	    {
	    	 int percentwidth;
	    	 Rectangle Src,Dest;
	    	 BufferedImage Surface;
	    	 Image TileW;
	    	 Src=new Rectangle();
	    	 Dest=new Rectangle();
    		 GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();

    		 Surface=gc.createCompatibleImage(w,h,java.awt.Transparency.TRANSLUCENT);
    	     Graphics2D g2d = Surface.createGraphics();
    		 
    	     if (PlayerInfo.CurrentXP > PlayerInfo.PrevXP)
    	     {
    	       Src.x=(int) (64*Zoom); Src.y=(int) (48*Zoom); Src.width=(int) (16*Zoom); Src.height=(int) (16*Zoom);
    	       if(Zoom==3.2) Src.x++;
    	       percentwidth=Math.round(((320-30)*(PlayerInfo.CurrentXP-PlayerInfo.PrevXP))/ ((PlayerInfo.NextXP-PlayerInfo.PrevXP)==0 ? (PlayerInfo.NextXP-PlayerInfo.PrevXP+1) : (PlayerInfo.NextXP-PlayerInfo.PrevXP)) );//Evite la division par 0 si nextxp-prevxp=0
    	       if (percentwidth > 320-30) percentwidth=320-30;
    	       if (percentwidth>0)
    	       {
    	         Dest.x=(int) (15*Zoom); Dest.y=(int) (5*Zoom); Dest.width=(int) (((320-30)-((320-30)-percentwidth))*Zoom) ; Dest.height=(int) (6*Zoom);
	    		 improd=SystemSurf.getSource();
	    		 cif2 =new CropImageFilter(Src.x, Src.y,Src.width,Src.height);
	    		 TileW = createImage(new FilteredImageSource(improd, cif2));	  
	    		 g2d.drawImage(TileW,Dest.x,Dest.y,Dest.width,Dest.height, null);
    	       }
    	       else
    	         Dest.width=0;
    	     }
    	     else
    	     {
    	       Dest.width=0;
    	       percentwidth=0;
    	     }
    		 improd=Image.getSource();
    		 
    	     Src.x=(int) ((15*Zoom)+Dest.width); Src.y=(int) (5*Zoom); Src.width=(int) (((320-30)-percentwidth)*Zoom); Src.height=(int) (6*Zoom);
    		 g2d.clearRect(Src.x,Src.y,Src.width,Src.height);
    	     Src.x=0; Src.y=0; Src.width=Image.getWidth(null); Src.height=Image.getHeight(null);
    	     Dest.x=0; Dest.y=0;
    		 g2d.drawImage(Image,Dest.x,Dest.y,(int)(Src.width*Zoom), (int)(Src.height*Zoom), null);
	    	 g2d.dispose();
	    	 Rects=Surface;
	    }
		public void Draw(Graphics2D g)
		{
		  DestRect.x= x+ScreenX;
		  DestRect.y= y+ScreenY;
		  if (Transparency!=1)
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
		  DrawImage(g,Rects,DestRect.x,DestRect.y,w,h, null);
		  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}

	public class Animation extends Sprite {
		int PosX,PosY,PosZ,DecX,DecY,tempsattente,attente;
		Rectangle DestRect;
	    Image[] Rects;
		Sprite Parent;
		ImageFilter cif2;
		ImageProducer improd;

		public Animation(JFrame _Source,Sprite Parent_,String Chipset_,int PosX_,int PosY_,int DecX_,int DecY_,int w_,int h_,int PosZ_,int tempsattente_,int transparence_)
	    {
			super("",(int)(w_*Zoom),(int)(h_*Zoom),_Source);
			Image=LoadImage(general.getName()+"/"+Chipset_.replace("Chipset\\", "Chipset/"),true);
            Transparency=transparence_ / 255;
			ID=Sprite.idSprite.idAnimation;
			DestRect=new Rectangle();
			PosX=PosX_;
			PosY=PosY_;
			PosZ=PosZ_;
			DecX=DecX_;
			DecY=DecY_;
		    x = (int) ((DecX+(8 * (PosX + CentreX + PlayerInfo.CentreX - PlayerInfo.pmapx) - prealx - ScX))*Zoom);
		    y = (int) ((DecY+(8 * (PosY + CentreY + PlayerInfo.CentreY - PlayerInfo.pmapy) - prealy - ScY))*Zoom);
		    tempsattente=tempsattente_;
		    if (tempsattente==0) tempsattente=42;
		    Parent=Parent_;
		    attente=0;
			improd=Image.getSource();
        	Rects=new Image[Image.getWidth(null) / w_];
        	for(int i=0;i<Image.getWidth(null) / w_;i++)
        	{
		    	cif2 =new CropImageFilter((int)((w/Zoom)*i), 0,(int)(w/Zoom),(int)(h/Zoom));
		    	Rects[i] = createImage(new FilteredImageSource(improd, cif2));	  
			   	Rects[i] = Rects[i].getScaledInstance(w, h, BufferedImage.SCALE_FAST);
        	}
	    }

		public void Draw(Graphics2D g)
		{
			  if (attente<tempsattente)
			  {
			    if (Parent!=null)
			    {
			      if (Parent.isDead==false)
			      {
			        x=(int) (Parent.x+(DecX*Zoom));
			        y=(int) (Parent.y+(DecY*Zoom));
			      }
			    }
			    else
			    {
			      x = (int) ((DecX+(8 * (PosX + CentreX + PlayerInfo.CentreX - PlayerInfo.pmapx) - prealx -ScX))*Zoom);
			      y = (int) ((DecY+(8 * (PosY + CentreY + PlayerInfo.CentreY - PlayerInfo.pmapy) - prealy -ScY))*Zoom);
			    }
			    switch(PosZ)
			    {
			    	case 0 : z=(int) ((240*Zoom)+(15*Zoom)); break;
			    	case 1 : z=y; break;
			    }
				DestRect.x=(int) (x - ((w-(16*Zoom)) / 2)+ScreenX);
				DestRect.y=(int) (y - h + (8*Zoom*(h / (16*Zoom)))+ScreenY);
				if (Transparency!=1)
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
				DrawImage(g,Rects[(int) ((attente * (Image.getWidth(null) / (w/Zoom))) / tempsattente)],DestRect.x,DestRect.y,w,h, null);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
			    attente++;
			  }
			  else
			    Kill();
		}
	}
	public class Neige extends Sprite {
	    int AttenteMove;
		Rectangle DestRect;
	    public Neige(JFrame _Source)
	    {
			super("",(int)(5*Zoom),(int)(5*Zoom),_Source);
			DestRect=new Rectangle();
			ID=Sprite.idSprite.idAnimation;
			AttenteMove=0;
			if (Util.random(3)>0)
			{
			    x=Util.random((int) (320*Zoom));
			    y=0;
			}
			else
			{
			    if (Util.random(2)>0)
			      x=(int) (320*Zoom);
			    else
			      x=0;
			    y=Util.random((int) (220*Zoom));
			}
			z=(int) ((240*Zoom)+(150*Zoom));
	    }
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
	    	  int compx =0; 
	    	  int compy=0;
			  if (Zoom==1)
			  {
			      switch(ScrollDirection)
			      {
			      		case 1 : compy=2; break;
			      		case 2 : compx=1; break;
			      		case 3 : compy=0; break;
			      		case 4 : compx=-1; break;
			      }
			  }
			  else
			  {
			      switch(ScrollDirection)
			      {
			      		case 1 : compy=(int) (2*Zoom); break;
			      		case 2 : compx=1; break;
			      		case 3 : compy=-1; break;
			      		case 4 : compx=(int) (-2*Zoom); break;
			      }
			  }
		  	  AttenteMove++;
			  if (AttenteMove>=2)
			  {
				  x+=Util.random(4)-2-compx;
			  	  y+=Zoom+compy;
			      AttenteMove=0;
			  }
			  if (y>=240*Zoom) Kill();
	    }
		public void Draw(Graphics2D g)
		{
			  DestRect.x= x+ScreenX;
			  DestRect.y= y+ScreenY;
			  if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			  DrawImage(g,NeigeSurf,DestRect.x,DestRect.y,w,h, null);
			  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}

	}

	public class Pluie extends Sprite {
		Rectangle DestRect;
	    public Pluie(JFrame _Source)
	    {
			super("",(int)(5*Zoom),(int)(5*Zoom),_Source);
			DestRect=new Rectangle();
			ID=Sprite.idSprite.idAnimation;
			if (Util.random(2)>0)
			{
			    x=Util.random((int) (320*Zoom));
			    y=0;
			}
			else
			{
			    x=(int) (320*Zoom);
			    y=Util.random((int) (220*Zoom));
			}
			z=(int) ((240*Zoom)+(150*Zoom));
	    }
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
		      double compx=0; 
		      double compy=0;
			  switch(ScrollDirection)
			  {
			  	  case 1 : compy=(1*Zoom); break;
			  	  case 2 : compx=(1*Zoom); break;
			  	  case 3 : compy=-1*(Zoom-1); break;
			  	  case 4 : compx=-1*(Zoom-1); break;
		      }
		  	  x-=Zoom+compx;
			  y+=Zoom+compy;
			  if (y>=240*Zoom) Kill();
	    }
		public void Draw(Graphics2D g)
		{
			  DestRect.x= x+ScreenX;
			  DestRect.y= y+ScreenY;
			  if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			  DrawImage(g,PluieSurf,DestRect.x,DestRect.y,w,h, null);
			  g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}

	}

	public class TFChar extends Sprite {
	    Rectangle DestRect;
	    Image[] Rects;
	    String Name;
	    int mmapx,mmapy,PosZ;
		ImageFilter cif2;
		ImageProducer improd;
	    public TFChar(JFrame _Source,String name_,String Chipset_,int PosX_ ,int PosY_ ,int W_,int H_,int mapx ,int mapy,int PosZ_,boolean Transparent)
	    {
			super("",(int)(W_*Zoom),(int)(H_*Zoom),_Source);
	    	if (Chipset_.compareTo(CurrentMap.Chipset)==0)
	    	  Image=Tile;
	    	else
	    	{
			  Image=LoadImage(general.getName()+"/"+Chipset_.replace("Chipset\\", "Chipset/"),true);
	    	}
	    	if (Transparent)
          	  Transparency=0.5f;
			ID = Sprite.idSprite.idFChar;
		   	PosZ=PosZ_;
	    	mmapx = mapx;
	    	mmapy = mapy;
	    	Name = name_;
	    	x = (int) ((8 * (mmapx + CentreX + PlayerInfo.CentreX - PlayerInfo.pmapx) - prealx -ScX)*Zoom);
	    	y = (int) ((8 * (mmapy + CentreY + PlayerInfo.CentreY - PlayerInfo.pmapy+1-((h / (16*Zoom))-1)) - prealy -ScY)*Zoom);
			DestRect = new Rectangle();
	    	Redraw(PosX_,PosY_);
	    }
	    	
		public void Draw(Graphics2D g)
		{
			DestRect.x=(int) (x - ((w-(16*Zoom)) / 2)+ScreenX);
			DestRect.y=(int) (y - h + (8*Zoom*(h / (16*Zoom)))+ScreenY);
			if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			DrawImage(g,Rects[Evenements[mmapx / 2][mmapy / 2].moveevenement],DestRect.x,DestRect.y,w,h, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	    public void Move(KeyboardInput.KeyState[] keys)
	    {	
	    	  if (((mmapx - PlayerInfo.pmapx+PlayerInfo.CentreX-(ScX / 8))<NDetect) ||
	    		      ((mmapx - PlayerInfo.pmapx+PlayerInfo.CentreX-(ScX / 8))>Detect) ||
	    		      ((mmapy - PlayerInfo.pmapy+PlayerInfo.CentreY-(ScY / 8))<NDetect) ||
	    		      ((mmapy - PlayerInfo.pmapy+PlayerInfo.CentreY-(ScY / 8))>Detect)) 
	    	  {
	    		    if (IsInEvent==false) 
	    		    {
	    		      Evenements[mmapx / 2][mmapy / 2].Sprite=null;
	    		      Kill();
	    		    }
	    	  }
	    	  
	    	  switch(PosZ)
	    	  {
	    	  	case 0 : z=y; break;
	    	  	case 1 : z=(int) (y+(60*Zoom)); break;
	    	  	case 2 : z=(int) (y-(60*Zoom)); break;
	    	  }
	    	  
    		  x = (int) ((8 * (mmapx + CentreX + PlayerInfo.CentreX - PlayerInfo.pmapx) - prealx -ScX)*Zoom);
    		  y = (int) ((8 * (mmapy + CentreY + PlayerInfo.CentreY - PlayerInfo.pmapy+1-((h / (16*Zoom))-1)) - prealy -ScY)*Zoom);
	    }
	    public void Redraw(int PosX_, int PosY_)
	    {
			improd=Image.getSource();
	    	if (Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).TypeAnim==3)
	    	{
                if (Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).NumAnim > 0)
                {
                	Rects=new Image[Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).NumAnim];
                	for(int i=0;i<Rects.length;i++)
                	{
                		PosX_=Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).X
                		     +(i*Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).W);
                		if ((Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).W==16)&&(Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).H==16)
                			&&(Image==Tile))
                		{
                			Rects[i]=Background[PosX_/16][PosY_/16];
                		}
                		else
                		{
	        		    	cif2 =new CropImageFilter(PosX_, PosY_,(int)(w/Zoom),(int)(h/Zoom));
	        		    	Rects[i] = createImage(new FilteredImageSource(improd, cif2));	  
	        			   	Rects[i] = Rects[i].getScaledInstance(w, h, BufferedImage.SCALE_FAST);
                		}
                	}
                }
                else if (Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).Direction > 0)
                {
                	Rects=new Image[Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).Direction];
                	for(int i=0;i<Rects.length;i++)
                	{
                		PosY_=Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).Y
                		     +(i*Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).H);
                		if ((Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).W==16*Zoom)&&(Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).H==16*Zoom)
                    			&&(Image==Tile))
                		{
                			Rects[i]=Background[PosX_/16][PosY_/16];
                		}
                		else
                		{
	        		    	cif2 =new CropImageFilter(PosX_, PosY_,(int)(w/Zoom),(int)(h/Zoom));
	        		    	Rects[i] = createImage(new FilteredImageSource(improd, cif2));	  
	        			   	Rects[i] = Rects[i].getScaledInstance(w, h, BufferedImage.SCALE_FAST);
                		}
                	}
                }
	    	}
	    	else
	    	{
        		if ((Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).W==16*Zoom)&&(Evenements[mmapx/2][mmapy/2].evenement.get(Evenements[mmapx/2][mmapy/2].Ev).H==16*Zoom)
            			&&(Image==Tile))
        		{
        			Rects[0]=Background[PosX_][PosY_];
        		}
        		else
        		{
					Rects=new Image[1];
			    	cif2 =new CropImageFilter(PosX_, PosY_,(int)(w/Zoom),(int)(h/Zoom));
			    	Rects[0] = createImage(new FilteredImageSource(improd, cif2));	  
				   	Rects[0] = Rects[0].getScaledInstance(w, h, BufferedImage.SCALE_FAST);
        		}
	    	}
	    }
	}
	
    public boolean VerifieSpriteVisible(int mmapx,int mmapy)
    {
      boolean Result=true;
      if ((PlayerInfo.CentreX!=0) || (PlayerInfo.CentreY!=0) || (ScX!=0) || (ScY!=0))
      {
        if  ((((mmapx - PlayerInfo.pmapx)<NDetect) ||
            ((mmapx - PlayerInfo.pmapx)>Detect) ||
            ((mmapy - PlayerInfo.pmapy)<NDetect) ||
            ((mmapy - PlayerInfo.pmapy)>Detect)) &&
            (((mmapx - PlayerInfo.pmapx + PlayerInfo.CentreX-(ScX / 8))<NDetect) ||
            ((mmapx - PlayerInfo.pmapx + PlayerInfo.CentreX-(ScX / 8))>Detect) ||
            ((mmapy - PlayerInfo.pmapy + PlayerInfo.CentreY-(ScY / 8))<NDetect) ||
            ((mmapy - PlayerInfo.pmapy + PlayerInfo.CentreY-(ScY / 8))>Detect)))
        {
          Result=false;
        }
      }
      else
      {
        if (((mmapx - PlayerInfo.pmapy)<NDetect) ||
            ((mmapx - PlayerInfo.pmapy)>Detect) ||
            ((mmapy - PlayerInfo.pmapy)<NDetect) ||
            ((mmapy - PlayerInfo.pmapy)>Detect))
        {
          Result=false;
        }
      }
      return Result;
    }

    public class TMChar extends Sprite
	{
	    Rectangle DestRect;
		Image[][][] Rects;
		ImageFilter cif2;
		ImageProducer improd;
	    String Name,Chipset,SoundAttaque , SoundBlesse,SoundConcentration;
	    boolean Visible, Dead;
	    int Direction, Sens , AttenteAnim, vit, vitesse , AttenteAttaque , AttenteTotal;
	    int Action,Alignement;
	    int evversx, evversy , EventX, EventY, posx, posy , mtextedelay , mmapx , mmapy , mrealx , mrealy;
	    int typechar;
	    int attentenotmove;
	    double mposx,mposy;
		int PosZ,versx , versy;
	    boolean Bloquant;
	    
	    OtherPlayer pl=null;
	    
	    
	    public TMChar(JFrame _Source,String Name_,String Chipset_,String SoundAttaque_,String SoundBlesse_,String SoundConcentration_,int PosX_ ,int PosY_ ,int W_,int H_,int mapx ,int mapy,int eventx_,int eventy_,int direction_,int isdead_,int PosZ_,Sprite.idSprite idnum,int typechar_,int vitesse_,boolean Bloquant_)
	    {
			super("",(int)(W_*Zoom),(int)(H_*Zoom),_Source);
			Image=LoadImage(general.getName()+"/"+Chipset_.replace("Chipset\\", "Chipset/"),true);
			Name=Name_;
	    	if (!VerifieSpriteVisible(mapx,mapy))
	    	{
	    	    Kill();
	    	    return;
	    	}
	    	Bloquant=Bloquant_;
	    	SoundAttaque=SoundAttaque_;
	    	SoundBlesse=SoundBlesse_;
	    	SoundConcentration=SoundConcentration_;
	    	ID=idnum;
			Rects=new Image[3][4][3];
			DestRect = new Rectangle();
			improd=Image.getSource();
			for (int i=0;i<3;i++)
			{
			  for(int j=0;j<4;j++)
			  {
			      for (int k=0;k<3;k++)
			      {
			    	cif2 =new CropImageFilter((i*W_*3) +(PosX_)+(k*W_), (PosY_) + (j*H_),W_,H_);
			    	Rects[i][j][k] = createImage(new FilteredImageSource(improd, cif2));	  
    			   	Rects[i][j][k]=Rects[i][j][k].getScaledInstance((int)(W_*Zoom), (int)(H_*Zoom), BufferedImage.SCALE_FAST); 
			      }
			  }
			}
			if (isdead_==1)
        	  Transparency=0.5f;
	    	PosZ =PosZ_;
	        Chipset = Chipset_;
	    	typechar=typechar_;
	    	vitesse=vitesse_;
	    	vit=Math.abs(vitesse);
	    	Visible=true;
	    	posx = PosX_;
	    	posy = PosY_;
	    	x = (int) (8 * Zoom * (mapx + CentreX + PlayerInfo.CentreX -(ScX / 8) - PlayerInfo.pmapx));
	    	y = (int) (8 * Zoom * (mapy + CentreY + PlayerInfo.CentreY -(ScY / 8) - PlayerInfo.pmapy));
	    	mmapx = mapx;
	    	mmapy = mapy;
	    	EventX=eventx_;
	    	EventY=eventy_;
	    	evversx=-1;
	    	evversy=-1;
	    	versx =mapx;
	    	versy = mapy;
	    	Direction = direction_;
	    	AnimPhase=1;
	    	Sens = 1;
	    	AttenteAnim = 0;
	    	Action =0;
	    	mrealx = 0;
	    	mrealy = 0;	    	
	    }
		public void Draw(Graphics2D g)
		{
			int CurrentAction;
			if (Visible)
			{
			    if (Action==5) CurrentAction=0;
			    else if (Action == 4) CurrentAction=1;
			    else
			      CurrentAction=Action;
			    DestRect.x=(int) (x - ((w-(16*Zoom)) / 2) + mposx+ ScreenX);
			    DestRect.y=(int) (y - h + (8*Zoom*(h / (16*Zoom))) + mposy+ScreenY);
				if (Transparency!=1)
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
				
				if(ID==idSprite.idPLAYER && !IsInvActive) 
				{
					if(!ListeJMap.containsKey(pl.name))
					{
						Kill();
						ListePlayer.remove(pl.name);
					}
					g.setColor(Color.white);
					g.setFont(new Font(Font.SERIF, Font.PLAIN, (int) (10*Zoom)));
					if(pl.fmP==null)
					{
						pl.fmP=g.getFontMetrics();
						
					}
					
						
					pl.pseudoCentre=(pl.fmP.stringWidth(pl.name))/2;
					pl.xPs=(int) (DestRect.x+12*Zoom);
					pl.xPs=(int) (pl.xPs-pl.pseudoCentre);
				    pl.yPs=(int) (DestRect.y+50*Zoom);
				    if(versx==mmapx && versy==mmapy && ListeJMap.containsKey(pl.name))
				    	DrawImage(g,Rects[CurrentAction][ListeJMap.get(pl.name).D][AnimPhase],DestRect.x,DestRect.y,w,h, null);
				    else
				    	DrawImage(g,Rects[CurrentAction][Direction][AnimPhase],DestRect.x,DestRect.y,w,h, null);
					g.drawString(pl.name, pl.xPs, pl.yPs);//TODO ici
					
					
					if(pl.timerMsg > System.currentTimeMillis())
					{
						if(pl.fm==null && pl.talk[0]==null)
						{
							FontMetrics fm=g.getFontMetrics();
							String[] str=new String[4];
							for(int j=0;j<4;j++)
							{
								for(int i=0;str[j]==null || (i<=pl.tMsgFinal.length() && fm.stringWidth(str[j])<60*Zoom);i++)
								{
									str[j]=pl.tMsgFinal.substring(0, i);
								}
								pl.tMsgFinal=pl.tMsgFinal.substring(str[j].length());
								//if(str[j]==null)
							}
							pl.talk=str;
							pl.fm=fm;
							System.out.println(Arrays.toString(str));
							pl.strWCentre=(fm.stringWidth(str[0]))/2;
							
							
						    int siz=0;
						    for(int i=0;i<4;i++)
						    	if(pl.talk[i]!=null && pl.talk[i].length()>0) siz++;
						    pl.sizMs=siz;
						}
						int x = pl.xPs+pl.pseudoCentre;
						x-=pl.strWCentre;
				    	int y = (int) (pl.yPs-24*Zoom);
				    	y=(int) (y-12*pl.sizMs*Zoom);
						
						for (int i=0;i<4;i++)
						{
							if(pl.talk[i]==null)
							{
								break;
							}
							else
							{
								g.drawString(pl.talk[i], x, y);
								y=(int) (y+12*Zoom);
							}
						}
						//CreateWindow(60,(int)((((Global.tMsgFinal.length()-1)*15)+18)*Zoom),false);
						//g2.drawString(Global.tMsgFinal, (float)(16*Zoom*(CentreX-PlayerInfo.pmapx)), (float)(16*Zoom*(CentreY-PlayerInfo.pmapy)));
						//System.out.println( Integer.toString(CentreX-PlayerInfo.pmapx) +","+ Integer.toString(CentreY-PlayerInfo.pmapy) );
					}
					
				}
				else
					DrawImage(g,Rects[CurrentAction][Direction][AnimPhase],DestRect.x,DestRect.y,w,h, null);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
			}
		}
	    public void Move(KeyboardInput.KeyState[] keys)
	    {
	    	  int maxx=CurrentMap.TailleX*2;
	    	  int maxy=CurrentMap.TailleY*2;
	    	  if (this!=SpriteEv)
	    	  {
	    	    if ((PlayerInfo.CentreX!=0) || (PlayerInfo.CentreY!=0) || (ScX!=0) || (ScY!=0))
	    	    {
	    	      if  ((((mmapx - PlayerInfo.pmapx)<NDetect) ||
	    	          ((mmapx - PlayerInfo.pmapx)>Detect) ||
	    	          ((mmapy - PlayerInfo.pmapy)<NDetect) ||
	    	          ((mmapy - PlayerInfo.pmapy)>Detect)) &&
	    	          (((mmapx - PlayerInfo.pmapx + PlayerInfo.CentreX -(ScX / 8))<NDetect) ||
	    	          ((mmapx - PlayerInfo.pmapx + PlayerInfo.CentreX -(ScX / 8))>Detect) ||
	    	          ((mmapy - PlayerInfo.pmapy + PlayerInfo.CentreY -(ScY / 8))<NDetect) ||
	    	          ((mmapy - PlayerInfo.pmapy + PlayerInfo.CentreY -(ScY / 8))>Detect)))
	    	      {
	    	         if ((IsInEvent==false) || (ID!=Sprite.idSprite.idEvenement))
	    	         {
	    	           if (ID==Sprite.idSprite.idEvenement)
	    	             Evenements[EventX][EventY].Sprite=null;
	    	           Kill();
	    	           return;
	    	         }
	    	      }
	    	    }
	    	    else
	    	    {
	    	      if (((mmapx - PlayerInfo.pmapx)<NDetect) ||
	    	          ((mmapx - PlayerInfo.pmapx)>Detect) ||
	    	          ((mmapy - PlayerInfo.pmapy)<NDetect) ||
	    	          ((mmapy - PlayerInfo.pmapy)>Detect))
	    	      {
	    	         if ((IsInEvent==false) || (ID!=Sprite.idSprite.idEvenement))
	    	         {
	    	           if (ID==Sprite.idSprite.idEvenement)
	    	              Evenements[EventX][EventY].Sprite=null;
	    	           Kill();
	    	           return;
	    	         }
	    	      }
	    	    }
	    	  }

	    	  if (typechar==1)
	    	  {
	    	    if ((versx==mmapx) && (versy==mmapy))
	    	    {
	    	      Kill();
	    	      return;
	    	    }
	    	  }

	    	  if (typechar==2)
	    	  {
	    	    if ((versx==mmapx) && (versy==mmapy))
	    	    {
	    	      if (player==null)
	    	      {
	    	        player=new Player(Source,Zoom);
	    	        SpriteE.AddSprite(player);
	    	        PlayerInfo.pmapx=(short) mmapx;
	    	        PlayerInfo.pmapy=(short) mmapy;
	    	        prealx=0; prealy=0;
	    	        ScrollDirection=0;
	    	        Kill();
	    	        return;
	    	      }
	    	      else
	    	      {
	    	        typechar=0;
	    	        vitesse=1;
	    	        vit=vitesse;
	    	      }
	    	    }
	    	  }

	    	  if (Action==1)
	    	  {
	    	    if (AttenteAttaque==0)
	    	      PlaySound(SoundAttaque,"",false);
	    	    if (AttenteAttaque<42)
	    	    {
	    	      AnimPhase=(AttenteAttaque / 14);
	    	      x = (int) ((8  * (mmapx + CentreX + PlayerInfo.CentreX - PlayerInfo.pmapx) - prealx -ScX + mrealx)*Zoom);
	    	      y = (int) ((8 *(mmapy + CentreY + PlayerInfo.CentreY - PlayerInfo.pmapy+1-((h / (16*Zoom))-1)) - prealy -ScY + mrealy)*Zoom);
	    	      z=y;
	    	      AttenteAttaque++;
	    	      if (typechar==0)
	    	        return;
	    	    }
	    	    else
	    	    {
	    	      Action=0;
	    	      AttenteAttaque=0;
	    	      AnimPhase=1;
	    	      if ((ID==Sprite.idSprite.idEvenement) && (Bloque==true))
	    	      {
	    	        SpriteEv=null;
	    	      }
	    	    }
	    	  }

	    	  if (Action==2)
	    	  {
	    	    if (AttenteAttaque==0)
	    	      PlaySound(SoundConcentration,"",false);
	    	    if (AttenteAttaque<AttenteTotal)
	    	    {
	    	      if (AttenteTotal >= 3)
	    	        AnimPhase=(int) (AttenteAttaque / (AttenteTotal / 3f));
	    	      x = (int) ((8  * (mmapx + CentreX + PlayerInfo.CentreX - PlayerInfo.pmapx) - prealx -ScX + mrealx)*Zoom);
	    	      y = (int) ((8 *(mmapy + CentreY + PlayerInfo.CentreY - PlayerInfo.pmapy+1-((h / (16*Zoom))-1)) - prealy -ScY + mrealy)*Zoom);
	    	      z=y;
	    	      AttenteAttaque++;
	    	      return;
	    	    }
	    	    else
	    	    {
	    	      Action=0;
	    	      AttenteAttaque=0;
	    	      AttenteTotal=0;
	    	      AnimPhase=1;
	    	      if ((ID==Sprite.idSprite.idEvenement) && (Bloque==true))
	    	      {
	    	        SpriteEv=null;
	    	      }
	    	    }
	    	  }

	    	  if (Action==4)
	    	  {
	    	    if ((AttenteAttaque<AttenteTotal) && (Dead==false) && (Bloque==false))
	    	    {
	    	      if (AttenteTotal >= 3)
	    	        AnimPhase=(int) (AttenteAttaque / (AttenteTotal / 3f));
	    	      AttenteAttaque++;
	    	      return;
	    	    }
	    	    else
	    	    {
	    	      AttenteTotal=0;
	    	      Action=0;
	    	      AnimPhase=1;
	    	    }
	    	  }

	    	  if (Action==5)
	    	  {
	    	    if (AttenteAttaque<60)
	    	    {
	    	      AnimPhase=(AttenteAttaque / 20);
	    	      switch(Direction)
	    	      {
	    	        case 0 : mposy =  ((AnimPhase*2) * Zoom); break;
	    	        case 1 : mposx = 0 - ((AnimPhase*2) * Zoom); break;
	    	        case 2 : mposy = 0 - ((AnimPhase*2) * Zoom); break;
	    	        case 3 : mposx =  ((AnimPhase*2) * Zoom); break;
	    	      }
	    	      x = (int) ((8  * (mmapx + CentreX + PlayerInfo.CentreX - PlayerInfo.pmapx) - prealx -ScX + mrealx)*Zoom);
	    	      y = (int) ((8 *(mmapy + CentreY + PlayerInfo.CentreY - PlayerInfo.pmapy+1-((h / (16*Zoom))-1)) - prealy -ScY + mrealy)*Zoom);
	    	      z=y;
	    	      AttenteAttaque++;
	    	    }
	    	    else
	    	    {
	    	      mposx=0; mposy=0;
	    	      Action=0;
	    	      AttenteAttaque=0;
	    	      AnimPhase=1;
	    	      if ((ID==Sprite.idSprite.idEvenement) && (Bloque==true))
	    	      {
	    	        SpriteEv=null;
	    	      }
	    	    }
	    	  }
	    	  
	    	  if ((versx==mmapx) && (versy==mmapy) && (Action==0))
	    	    attentenotmove++;
	    	  else
	    	    attentenotmove=0;
	    	  if(attentenotmove > 16)
	    	  {
	    	    if (ID==Sprite.idSprite.idEvenement)
	    	    {
	    	      if ((versx / 2!=EventX) || (versy / 2!=EventY))
	    	        if (Evenements[EventX][EventY].Ev>=0)
	    	        if (Evenements[EventX][EventY].evenement.get(Evenements[EventX][EventY].Ev).EvSuisSprite==true)
	    	        {
	    	          versx=EventX*2;
	    	          versy=EventY*2;
	    	        }
	    	    }
	    	    attentenotmove=0;
	    	    AnimPhase=1;
	    	  }

	    	  if (versx < mmapx)
	    	  {
	    	    mrealx=mrealx-vit;
	    	    if (mrealx <= -8)
	    	    {
	    	      mmapx--;
	    	      if (ID==Sprite.idSprite.idEvenement)
	    	      {
	    	        if ((versx / 2!=EventX) || (versy / 2!=EventY))
	    	          if (Evenements[EventX][EventY].Ev>=0)
	    	          if ((Evenements[EventX][EventY].evenement.get(Evenements[EventX][EventY].Ev).EvSuisSprite==true) && (mmapx % 2==0))
	    	            ChangeCaseEvent(EventX,EventY,mmapx / 2,EventY);
	    	      }
	    	      mrealx=0;
	    	    }
	    	    if (Action==0) Direction = 3;
	    	    Animate();
	    	  }
	    	  if (versx > mmapx)
	    	  {
	    	    mrealx=mrealx+vit;
	    	    if (mrealx >= 8)
	    	    {
	    	      mmapx++;
	    	      if (ID==Sprite.idSprite.idEvenement)
	    	      {
	    	        if ((versx / 2!=EventX) || (versy / 2!=EventY))
	    	          if (Evenements[EventX][EventY].Ev>=0)
	    	          if ((Evenements[EventX][EventY].evenement.get(Evenements[EventX][EventY].Ev).EvSuisSprite==true) && (mmapx % 2==0))
	    	            ChangeCaseEvent(EventX,EventY,mmapx / 2,EventY);
	    	      }
	    	      mrealx=0;
	    	    }
	    	    if (Action==0) Direction =1;
	    	    Animate();
	    	  }
	    	  if (versy < mmapy)
	    	  {
	    	    if (Action==0) Direction =0;
	    	    mrealy-=vit;
	    	    if (mrealy <= -8)
	    	    {
	    	      mmapy--;
	    	      if (ID==Sprite.idSprite.idEvenement)
	    	      {
	    	        if ((versx / 2!=EventX) || (versy / 2!=EventY))
	    	          if (Evenements[EventX][EventY].Ev>=0)
	    	          if ((Evenements[EventX][EventY].evenement.get(Evenements[EventX][EventY].Ev).EvSuisSprite==true) && (mmapx % 2==0))
	    	            ChangeCaseEvent(EventX,EventY,EventX,mmapy / 2);
	    	      }
	    	      mrealy=0;
	    	    }
	    	    Animate();
	    	  }
	    	  if (versy > mmapy)
	    	  {
	    	    if (Action==0) Direction =2;
	    	    mrealy+=vit;
	    	    if (mrealy >= 8)
	    	    {
	    	      mmapy++;
	    	      if (ID==Sprite.idSprite.idEvenement)
	    	      {
	    	        if ((versx / 2!=EventX) || (versy / 2!=EventY))
	    	          if (Evenements[EventX][EventY].Ev>=0)
	    	          if ((Evenements[EventX][EventY].evenement.get(Evenements[EventX][EventY].Ev).EvSuisSprite==true) && (mmapx % 2==0))
	    	            ChangeCaseEvent(EventX,EventY,EventX,mmapy / 2);
	    	      }
	    	      mrealy=0;
	    	    }
	    	    Animate();
	    	  }
	    	  if ((Math.abs(mrealy)>=8) || (Math.abs(mrealx)>=8))
	    	  {
	    	    // on va calculer la position reel pour stopper le gars au bon moment
	    	    if ((mmapx+(mrealx / 8)<=0) || (mmapx+(mrealx / 8)>=maxx)
	    	    || (mmapy+(mrealy / 8)<=0) || (mmapy+(mrealy / 8)>=maxy))
	    	    {
	    	      versx=mmapx;
	    	      versy=mmapy;
	    	      mrealx=0;
	    	      mrealy=0;
	    	      Visible=false;
	    	    }
	    	  }
	    	  x = (int) ((8  * (mmapx + CentreX + PlayerInfo.CentreX - PlayerInfo.pmapx) - prealx -ScX + mrealx)*Zoom);
	    	  y = (int) ((8 *(mmapy + CentreY + PlayerInfo.CentreY - PlayerInfo.pmapy+1-((h / (16*Zoom))-1)) - prealy -ScY + mrealy)*Zoom);
	    	  switch(PosZ)
	    	  {
	    	    case 0 : z=y; break;
	    	    case 1 : z=(int) (y+(60*Zoom)); break;
	    	    case 2 : z=(int) (y-(60*Zoom)); break;
	    	    case 3 : z=(int) (y+(96*Zoom)); break;
	    	    case 4 : z=(int) (y-(96*Zoom)); break;
	    	  }
	    }
	    public void Animate()
	    {
	    	  AttenteAnim++;
	    	  if (AttenteAnim > 8)
	    	  {
	    	    AttenteAnim = 0;
	    	    AnimPhase = AnimPhase + Sens;
	    	    if (AnimPhase >= 3)
	    	    {
	    	      AnimPhase =1;
	    	      Sens = -1;
	    	    }
	    	    if (AnimPhase <= -1) 
	    	    {
	    	      AnimPhase =1;
	    	      Sens = 1;
	    	    }
	    	  }
	    }
	}

    private void LevelUp()
    {
    	if ((PlayerInfo.CurrentXP > PlayerInfo.NextXP)&&(PlayerInfo.Classe!=null))
    	{
    	      if ((PlayerInfo.Lvl<PlayerInfo.Classe.LvlMax)&&(PlayerInfo.Lvl-1<general.getCourbeXP().size()))
    	      {
    	            PlayerInfo.Lvl++;
    	            PlayerInfo.PrevXP=general.getCourbeXP().get(PlayerInfo.Lvl-2);
    	            PlayerInfo.NextXP=general.getCourbeXP().get(PlayerInfo.Lvl-1);
    	            if (PlayerInfo.Lvl>2)
    	            {
    	                if (PlayerInfo.CurrentXP < general.getCourbeXP().get(PlayerInfo.Lvl-2))
    	                   PlayerInfo.CurrentXP = general.getCourbeXP().get(PlayerInfo.Lvl-2);
    	            }
    	            PlayerInfo.LvlPoint+=PlayerInfo.Classe.LvlupPoint;
  					PlaySound(DEFAULT_LVLUP_SOUND,"",false);
   					SpriteE.AddSprite(new Degat(this, "LEVEL UP!", player, Color.RED));
      	    		 if (barrexp!=null)
       	    			 barrexp.Redraw();
    	      }
    	}
    }
    
    private void GereMonsterKill(MonstreGame Mort)
    {
    	 int compte,i,pos,objectwin,poswin;
    	 boolean trouve;
    	 if (Mort==null) return;
   	     if (Mort.vie<=0)
   	     {
   	    	 if (lastmonsteratt==Mort)
   	    		 lastmonsteratt=null;
   	    	 if (Dead==false)
   	    	 {
   	    		 if (Mort.monstre.VarSpecial.compareTo("")!=0)
   	    		 {
   	    			AffectationVarPlayer("Variable["+Mort.monstre.VarSpecial+"]","Variable["+Mort.monstre.VarSpecial+"]+"+Integer.toString(Mort.monstre.ResSpecial));
   	    		 }
   	    		 PlayerInfo.CurrentXP+=(int) Calcule.Calcule(Formule.ReplaceStatVariable(PlayerInfo.Classe.FormuleXP,Mort));
   	    		 PlayerInfo.Gold+=(int) Calcule.Calcule(Formule.ReplaceStatVariable(PlayerInfo.Classe.FormuleGold,Mort));
   	    		 if (barrexp!=null)
   	    			 barrexp.Redraw();
   	    		 if (Mort.monstre.ObjectWin.size()>0)
   	    		 {
   	    			 pos=Util.random(100);
   	    			 i=0; trouve=false;
   	    			 compte=0;
   	    			 while ((i<Mort.monstre.ObjectWin.size())&&(trouve==false))
   	    			 {
   	    				 compte+=Mort.monstre.ObjectWin.get(i);
   	    				 if (pos<compte)
   	    					 trouve=true;
   	    				 i++;
   	    			 }
   	    			 if (trouve)
   	    			 {
   	    				 // on vérifie si l'objet n'est pas déjà dans l'inventaire du joueur
   	    				 objectwin=i-1;
   	    				 poswin=0;
   	    				 i=0; trouve=false;
   	    				 while((i<PlayerInfo.Inventaire.length)&&(trouve==false))
   	    				 {
   	    					 if (PlayerInfo.Inventaire[i][0]==objectwin+1)
   	    					 {
   	    						 trouve=true;
   	    						 poswin=i;
   	    					 }
   	    					 i++;
   	    				 }
   	    				 // on vérifie qu'il y'a de la place dans l'inventaire du joueur
   	    				 if (trouve==false)
   	    				 {
   	    					 i=0;
   	   	    				 while((i<PlayerInfo.Inventaire.length)&&(trouve==false))
   	   	   	    			 {
   	   	    					 if (PlayerInfo.Inventaire[i][0]==0)
   	   	    					 {
   	   	    						 trouve=true;
   	   	    						 poswin=i;
   	   	    					 }
   	   	    					 i++;
   	   	   	    			}   	    					 
   	    				 }
   	    				 if (trouve==true)
   	    				 {
   	    					 if (PlayerInfo.Inventaire[poswin][0]==0)
   	    					 {
   	    						 PlayerInfo.Inventaire[poswin][0]=(short) (objectwin+1);
   	    						 PlayerInfo.Inventaire[poswin][1]=0;
   	    					 }
   	    					 if (PlayerInfo.Inventaire[poswin][1]<99)
   	    					 {
	   	    					 PlayerInfo.Inventaire[poswin][1]++;
	   	    					 PlaySound(DEFAULT_LVLUP_SOUND,"",false);
	   	    					 SpriteE.AddSprite(new Degat(this, "Trouvé : "+general.getObjetByIndex(objectwin).Name, player, Color.WHITE));
   	    					 }
   	    				 }
   	    			 }
   	    		 }
   	    		 LevelUp();
   	    		 if (Mort.zone>=0)
   	    			CurrentMonstre.set(Mort.zone,CurrentMonstre.get(Mort.zone)-1);
   	    		 i=0; trouve=false;
   	    		 synchronized(ListeMonstre) { ListeMonstre.remove(Mort); }
   	    		 if (Mort.sprite!=null)
   	    		 {
   	    			 SpriteE.AddSprite(new MDead(this, Mort.sprite));
   	    			 Mort.sprite.Kill();
   	    		 }
   	    	 }
   	     }
    }
    
	public void GereAttaqueClient()
	{
		 int i , Dext , Dext2 , Degat;
		 boolean trouve ;
		 int numvar,mmapx , mmapy , pmapx , pmapy;
		 MonstreGame RMonstre;
		  // on commence d'abord par verifier les monstres
		  i=0;
		  RMonstre=null;
	      trouve=false;
	      if (lastmonsteratt!=null)
	      {
		      if (lastmonsteratt.vie>0)
		      {
			      RMonstre=lastmonsteratt;
			      mmapx=RMonstre.mapx / 2;
			      mmapy=RMonstre.mapy / 2;
			      pmapx=PlayerInfo.pmapx / 2;
			      pmapy=PlayerInfo.pmapy / 2;
		          if ((mmapx==pmapx) && (mmapy==pmapy))
		            trouve=true;
		          else
		          {
		        	  switch(PlayerDirection)
		        	  {
		        	  	  case 0 : trouve =(Math.abs(mmapx-pmapx)<=1) && ((mmapy==pmapy-1) || (mmapy==pmapy)); break;
		        	  	  case 1 : trouve =((mmapx==pmapx+1) || (mmapx==pmapx)) && (Math.abs(mmapy-pmapy)<=1); break;
		        	  	  case 2 : trouve =(Math.abs(mmapx-pmapx)<=1) && ((mmapy==pmapy+1) || (mmapy==pmapy)); break;
		        	  	  case 3 : trouve =((mmapx==pmapx-1) || (mmapx==pmapx)) && (Math.abs(mmapy-pmapy)<=1); break;
		        	  }
		          }
		      }
	      }
	      if (trouve==false)
	      {
		      while((i<ListeMonstre.size())&&(trouve==false))
		      {
		    	  RMonstre=ListeMonstre.get(i);
		    	  if (RMonstre.vie>0)
			      {
				      mmapx=RMonstre.mapx / 2;
				      mmapy=RMonstre.mapy / 2;
				      pmapx=PlayerInfo.pmapx / 2;
				      pmapy=PlayerInfo.pmapy / 2;
			          if ((mmapx==pmapx) && (mmapy==pmapy))
			            trouve=true;
			          else
			          {
			        	  switch(PlayerDirection)
			        	  {
			        	  	  case 0 : trouve =(Math.abs(mmapx-pmapx)<=1) && ((mmapy==pmapy-1) || (mmapy==pmapy)); break;
			        	  	  case 1 : trouve =((mmapx==pmapx+1) || (mmapx==pmapx)) && (Math.abs(mmapy-pmapy)<=1); break;
			        	  	  case 2 : trouve =(Math.abs(mmapx-pmapx)<=1) && ((mmapy==pmapy+1) || (mmapy==pmapy)); break;
			        	  	  case 3 : trouve =((mmapx==pmapx-1) || (mmapx==pmapx)) && (Math.abs(mmapy-pmapy)<=1); break;
			        	  }
			          }
			      }
		    	  i++;
		      }
	      }
	      
	      if (trouve==false)
	      {
		      lastmonsteratt=null;
		      RMonstre=null;
	      }
	      else
	      {
	    	  if ((PlayerInfo.Classe==null) || (general.getClassesMonstre().size()==0))
	    		  return;
	    	  Dext=(int) Calcule.Calcule(Formule.ReplaceStatVariable(PlayerInfo.Classe.FormuleAttaque,RMonstre))+MagicAttaque;
	    	  Dext2=(int) Calcule.Calcule(Formule.ReplaceStatVariable(general.getClassesMonstre().get(RMonstre.monstre.ClasseMonstre).FormuleEsquive,RMonstre))+RMonstre.MagicEsquive;
		      RMonstre.totalattente=1;
		      if (RMonstre.TypeMonstre==0)
		        RMonstre.TypeMonstre=1;
		      lastmonsteratt=RMonstre;
		      if (Dext > Dext2)
		      {
		         // touché!
		    	  Degat=(int) Calcule.Calcule(Formule.ReplaceStatVariable(PlayerInfo.Classe.FormuleAttaque,RMonstre))+MagicDommage;
		    	  Degat-=(int) Calcule.Calcule(Formule.ReplaceStatVariable(general.getClassesMonstre().get(RMonstre.monstre.ClasseMonstre).FormuleDefense,RMonstre))+RMonstre.MagicDefense;
		    	  if (Degat<0) Degat=0;
		          RMonstre.vie-=Degat;
		          if (RMonstre.vie>0)
		          {
		           if (PlayerInfo.Arme > 0)
		           {
		        	 if (general.getObjetByIndex(PlayerInfo.Arme-1).MagieAssoc>0)
		             {
		              numvar=general.getObjetByIndex(PlayerInfo.Arme-1).MagieAssoc-1;
		              trouve=PlayerInfo.BloqueMagie;
		              if (trouve==false)
		                trouve=HasMagie(RMonstre,general.getMagieByIndex(numvar));
		              if (trouve==false)
		              {
		            	CreateMagie(null,RMonstre,general.getMagieByIndex(numvar));
		              }
		             }
		           }
		           SpriteE.AddSprite(new Degat(this,Integer.toString(Degat),RMonstre.sprite,Color.WHITE));
		          }
		          else
		          {
		        	  GereMonsterKill(RMonstre);
		          }
		      }
		      else
		      {
		    	  if (RMonstre.sprite!=null)
		    		  SpriteE.AddSprite(new Degat(this,"Miss",RMonstre.sprite,Color.WHITE));
		      }
	      }
	}

	public class Player extends Sprite {    
		Rectangle DestRect;
		Image[][][] Rect;
		ImageFilter cif2;
		ImageProducer improd;
		int PosX; int PosY; int Sens; int AttenteAnim; int AttenteAttaque; int AttenteTotal;
		int Action; double Zoom;
		int CentreX; int CentreY;
		public Player(JFrame _Source,double zoom2)
		{
			super("",(int)(24*zoom2),(int)(32*zoom2),_Source);
			Image=LoadImage(general.getName()+"/"+PlayerInfo.Chipset.replace("Chipset\\", "Chipset/"),true);
			Rect=new Image[3][4][3];
			ID = Sprite.idSprite.idPLAYER;
			Zoom=zoom2;
			improd=Image.getSource();
			for (int i=0;i<3;i++)
			{
			  for(int j=0;j<4;j++)
			  {
			      for (int k=0;k<3;k++)
			      {
			    	cif2 =new CropImageFilter(((i*24*3)+(k*24)), j*32,24,32);
			    	Rect[i][j][k] = createImage(new FilteredImageSource(improd, cif2));	  
    			   	Rect[i][j][k]=Rect[i][j][k].getScaledInstance((int)(24*Zoom), (int)(32*Zoom), BufferedImage.SCALE_FAST); 
			      }
			  }
			}
		    CentreX = 20;
			CentreY = 14;
			x = (int) (8 * Zoom * (CentreX+PlayerInfo.CentreX));
			y = (int) (8 * Zoom * (CentreY+PlayerInfo.CentreY));
			Action=0;
			PlayerDirection = 2;
			AnimPhase=1;
			Sens = 1;
			AttenteAnim = 0;
			DestRect = new Rectangle();
		}
		public void Draw(Graphics2D g)
		{
			int CurrentAction;
			if ((Action == 3) || (Action==5)) CurrentAction=0;
			else if (Action == 4) CurrentAction=1;
			else
				CurrentAction=Action;
			DestRect.x=(int) (x - ((w-(16*Zoom)) / 2) + PosX + ScreenX);
			DestRect.y=(int) (y - h + (8*Zoom*(h / (16*Zoom))) + PosY +ScreenY);
			if (Transparency!=1)
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,Math.min(Transparency,1.0f)));
			DrawImage(g,Rect[CurrentAction][PlayerDirection][AnimPhase],DestRect.x,DestRect.y,w,h, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
		public void MoveSpecial(KeyboardInput.KeyState[] keys)
		{
			int i,compx,compy, prmapx , prmapy;
			boolean acheval,access;
			TMChar MChar;
			if (Action==1)
			{
			    if ((AttenteAttaque<42) && (Dead==false) && (Bloque==false))
			    {
			      AnimPhase=(AttenteAttaque/14);
			      AttenteAttaque++;
			      return;
			    }
			    else
			    {
			      if ((keys[KS]==KeyState.PRESSED) && (IsMenuActive==false) && (IsInEvent==false) && (ImportantThing==false) && (IsInSelectMChar==false) && (IsInvEquipActive==false)
			      && (Bloque==false) && (IsInInputString==false) && (HasChangedMap==false)
			      && (CurrentMap.TypeCarte > 0) && (!Dead) && (PlayerInfo.Vie > 0) && (PlayerInfo.BloqueAttaque==false))
			      {
			          Action=1;
			          AttenteAttaque=0;
			          AnimPhase=1;
			          GereAttaqueClient();
			          String to=Global.toucher();
			          if(!to.equals("tp:")) 
			        	  System.out.println(to);
			          CheckEvenement("Attaque");
			          PlaySound(PlayerInfo.SoundAttaque,"",false);
			          return;
			      }
			      else
			      {
			        Action=0;
			        AnimPhase=1;
			      }
			    }
			}
			if (Action==2)
			{
			    if ((AttenteAttaque<AttenteTotal) && (Dead==false) && (Bloque==false))
			    {
			      if (AttenteTotal >= 3)
			        AnimPhase=(int) (AttenteAttaque / (AttenteTotal / 3f));
			      AttenteAttaque++;
			      return;
			    }
			    else
			    {
			      AttenteTotal=0;
			      Action=0;
			      AnimPhase=1;
			    }
			}
			if (Action==3)
			{
			    AnimPhase =1;
			    Sens = 1;
			    if (AttenteAttaque<42)
			    {
			      AttenteAttaque++;
			      return;
			    }
			    else
			      Action=0;
			}
			if (Action==4)
			{
			    if ((AttenteAttaque<AttenteTotal) && (Dead==false) && (Bloque==false))
			    {
			      if (AttenteTotal >= 3)
			        AnimPhase=(int) (AttenteAttaque / (AttenteTotal / 3f));
			      AttenteAttaque++;
			      return;
			    }
			    else
			    {
			      AttenteTotal=0;
			      Action=0;
			      AnimPhase=1;
			    }
			}
			if (Action==5)
			{
			    if ((AttenteAttaque<60) && (Dead==false) && (Bloque==false))
			    {
			      AnimPhase=(AttenteAttaque / 20);
			      switch(PlayerDirection)
			      {
			        case 0 : PosY =  (int) ((AnimPhase*2) * Zoom); break;
			        case 1 : PosX = (int) (0 - ((AnimPhase*2) * Zoom)); break;
			        case 2 : PosY = (int) (0 - ((AnimPhase*2) * Zoom)); break;
			        case 3 : PosX =  (int) ((AnimPhase*2) * Zoom);
			      }
			      AttenteAttaque++;
			    }
			    else
			    {
			      if ((keys[KD] == KeyState.PRESSED) && (IsMenuActive==false) && (IsInEvent==false) && (ImportantThing==false) && (IsInSelectMChar==false) && (IsInvEquipActive==false)
			      && (Bloque==false) && (Freeze==false) && (IsInInputString==false) && (HasChangedMap==false) && (CurrentMap.TypeCarte > 0) && (!Dead) && (PlayerInfo.Vie > 0) && (PlayerInfo.BloqueDefense==false))
			      {
			        Action=5;
			        AttenteAttaque=0;
			        PosX=0; PosY=0;
			        AnimPhase=1;
			      }
			      else
			      {
			        Action=0;
			        PosX=0; PosY=0;
			      }
			    }
			}
			z=(int) (y+(96*PlayerInfo.Position*Zoom));
			if ((Action==0) && ((IsMenuActive==true) || (IsInEvent==true) || (ImportantThing==true) || (IsInSelectMChar==true) || (IsInvEquipActive==true)
			     || (Bloque==true) || (IsInInputString==true) || (HasChangedMap==true)))
			{
			    AnimPhase =1;
			    Sens = 1;
			    return;
			}
			ScrollDirection=0;
			compx=0; compy=0;
			//  compx:=-8; compy:=4;
			if (keys[KLEFT] == KeyState.PRESSED) compx=8;
			else
			if (keys[KRIGHT] ==KeyState.PRESSED) compx=-8;
			else
			if (keys[KUP] ==KeyState.PRESSED) compy=8;
			else
			if (keys[KDOWN] ==KeyState.PRESSED) compy=-8;
			prmapx=(int) Math.round((((PlayerInfo.pmapx * 16f)) + prealx+ compx) / 32f);
			prmapy=(int) Math.round((((PlayerInfo.pmapy * 16f)) + prealy+compy) / 32f);
			if ((keys[KS]==KeyState.PRESSED) && (CurrentMap.TypeCarte > 0) && (!Dead) && (PlayerInfo.Vie > 0) 
				&& (PlayerInfo.BloqueAttaque==false) && (Action==0))
			{
			    access=true;
			    Action=1;
			    AttenteAttaque=0;
			    GereAttaqueClient();
		        CheckEvenement("Attaque");
		        String to=Global.toucher();
		        	if(!to.equals("tp:")) System.out.println(to);
			    PlaySound(PlayerInfo.SoundAttaque,"",false);
			}
			if ((keys[KD]==KeyState.PRESSED) && (CurrentMap.TypeCarte > 0) && (!Dead) && (PlayerInfo.Vie > 0)
					&& (Freeze==false) && (PlayerInfo.BloqueDefense==false) && (Action==0))
			{
			    Action=5;
			    AttenteAttaque=0;
			}
			for(i=0;i<10;i++)
			{
				int key=0,racc=0;
				switch(i)
				{
					case 0 : key=K0; racc=9; break;
					case 1 : key=K1; racc=0; break;
					case 2 : key=K2; racc=1; break;
					case 3 : key=K3; racc=2; break;
					case 4 : key=K4; racc=3; break;
					case 5 : key=K5; racc=4; break;
					case 6 : key=K6; racc=5; break;
					case 7 : key=K7; racc=6; break;
					case 8 : key=K8; racc=7; break;
					case 9 : key=K9; racc=8; break;
				}
				if ((keys[key]==KeyState.PRESSED) && (!Dead) && (Action==0) )
				{
				    if (PlayerInfo.Raccourcis[racc] > 0)
				    {
				      access=false;
				      if (PlayerInfo.Inventaire[PlayerInfo.Raccourcis[racc]-1][0]>0)
				    	  if (general.getObjetByIndex(PlayerInfo.Inventaire[PlayerInfo.Raccourcis[racc]-1][0]-1).ObjType==0)
				    		  access=true;
				      if (access)
				        UtiliseObjet(PlayerInfo.Raccourcis[racc]-1);
				      if (PlayerInfo.Inventaire[PlayerInfo.Raccourcis[racc]-1][1]==0)
				      {
				    	  PlayerInfo.Raccourcis[racc]=0;
				    	  if (barreicone!=null)
				    		  barreicone.Redraw();
				      }
				    }
				    if (PlayerInfo.Raccourcis[racc]<0)
				    {
					      MagicObject=-1;
					      MagieUsed=PlayerInfo.OwnSpell[Math.abs(PlayerInfo.Raccourcis[racc])-1]-1;
					      if (general.getMagieByIndex(MagieUsed).OnMonster==0)
					    	  SpriteE.AddSprite(new SelectMChar(Source,0,Sprite.idSprite.idMonstre));
					      else
					    	  SpriteE.AddSprite(new SelectMChar(Source,0,Sprite.idSprite.idPLAYER));
					      return;				    	
				    }
				}
			}
			if ((keys[KLEFT]==KeyState.PRESSED) && (prmapx > 0))
			{
			    if ((Action==0) || (Action==1)) PlayerDirection = 3;
			    if (Freeze==false)
			    {
				    access=true;
				    acheval=(PlayerInfo.pmapy % 2==1);
				    if (acheval)
				      prmapy=PlayerInfo.pmapy / 2;
				    if (CurrentMap.cases[prmapx-1][prmapy].X1 > 0)
				        access=block[0][CurrentMap.cases[prmapx-1][prmapy].X1-1][CurrentMap.cases[prmapx-1][prmapy].Y1-1];
				    if ((CurrentMap.cases[prmapx-1][prmapy].X2 > 0) && (access==true))
				        access=block[1][CurrentMap.cases[prmapx-1][prmapy].X2-1][CurrentMap.cases[prmapx-1][prmapy].Y2-1];
				    if ((Evenements[prmapx-1][prmapy].evenement != null) && (access==true))
				        access=!Evenements[prmapx-1][prmapy].evenement.get(Evenements[prmapx-1][prmapy].Ev).Bloquant;
				    if ((acheval) && (access==true))
				    {
				        if (CurrentMap.cases[prmapx-1][prmapy+1].X1 > 0)
				          access=block[0][CurrentMap.cases[prmapx-1][prmapy+1].X1-1][CurrentMap.cases[prmapx-1][prmapy+1].Y1-1];
				        if ((CurrentMap.cases[prmapx-1][prmapy+1].X2 > 0) && (access==true))
				          access=block[1][CurrentMap.cases[prmapx-1][prmapy+1].X2-1][CurrentMap.cases[prmapx-1][prmapy+1].Y2-1];
				        if ((Evenements[prmapx-1][prmapy+1].evenement != null) && (access==true))
					        access=!Evenements[prmapx-1][prmapy+1].evenement.get(Evenements[prmapx-1][prmapy+1].Ev).Bloquant;
				    }
				      if ((access) && (PossibleBloqueMonstre==true))
				      {
				        i=0;
				        while ((i<SpriteE.Sprites.size()) && (access==true))
				        {
				          if (SpriteE.Sprites.get(i).ID==Sprite.idSprite.idMonstre)
				          {
				        	MChar=(TMChar) SpriteE.Sprites.get(i);
				            if (MChar.Bloquant==true)
				              if  (((MChar.mmapx/2==prmapx-1) && (MChar.mmapy/2==prmapy))
				              || ((acheval==true) && (MChar.mmapx/2==prmapx-1) && (MChar.mmapy/2==prmapy+1)))
				                access=false;
				          }
				          i++;
				        }
				      }
				      if (access==true)
				      {
				        prealx--;
				        if (prealx <= -8)
				        {
				            PlayerInfo.pmapx--;
				            prealx=0;
				            SpriteE.SortSprites();
				            if (!Dead)
				              CheckEvenement("En contact");
				        }
				        Animate();
				        ScrollDirection=4;
				      }
			    }
			}
			else if ((keys[KRIGHT]==KeyState.PRESSED) && (prmapx < CurrentMap.cases.length-1))
			{
			    if ((Action==0) || (Action==1)) PlayerDirection=1;
			    if (Freeze==false)
			    {
				    access=true;
				    acheval=(PlayerInfo.pmapy % 2==1)? true : false;
				    if (acheval)
				      prmapy=PlayerInfo.pmapy / 2;
				    if (CurrentMap.cases[prmapx+1][prmapy].X1 > 0)
				      access=block[0][CurrentMap.cases[prmapx+1][prmapy].X1-1][CurrentMap.cases[prmapx+1][prmapy].Y1-1];
				    if ((CurrentMap.cases[prmapx+1][prmapy].X2 > 0) && (access==true))
				      access=block[1][CurrentMap.cases[prmapx+1][prmapy].X2-1][CurrentMap.cases[prmapx+1][prmapy].Y2-1];
				    if ((Evenements[prmapx+1][prmapy].evenement != null) && (access==true))
				    	access=!Evenements[prmapx+1][prmapy].evenement.get(Evenements[prmapx+1][prmapy].Ev).Bloquant;
				    if ((acheval) && (access==true))
				    {
				        if (CurrentMap.cases[prmapx+1][prmapy+1].X1 > 0)
				          access=block[0][CurrentMap.cases[prmapx+1][prmapy+1].X1-1][CurrentMap.cases[prmapx+1][prmapy+1].Y1-1];
				        if ((CurrentMap.cases[prmapx+1][prmapy+1].X2 > 0) && (access=true))
				          access=block[1][CurrentMap.cases[prmapx+1][prmapy+1].X2-1][CurrentMap.cases[prmapx+1][prmapy+1].Y2-1];
				        if ((Evenements[prmapx+1][prmapy+1].evenement != null) && (access==true))
					        access=!Evenements[prmapx+1][prmapy+1].evenement.get(Evenements[prmapx+1][prmapy+1].Ev).Bloquant;
				    }
				      if ((access) && (PossibleBloqueMonstre==true))
				      {
				        i=0;
				        while ((i<SpriteE.Sprites.size()) && (access==true))
				        {
				          if (SpriteE.Sprites.get(i).ID==Sprite.idSprite.idMonstre)
				          {
				        	MChar=(TMChar) SpriteE.Sprites.get(i);
				            if (MChar.Bloquant==true)
				              if  (((MChar.mmapx/2==prmapx+1) && (MChar.mmapy/2==prmapy))
				              || ((acheval==true) && (MChar.mmapx/2==prmapx+1) && (MChar.mmapy/2==prmapy+1)))
				                access=false;
				          }
				          i++;
				        }
				      }
				    if (access==true)
				    {
				        prealx++;
				        if (prealx >= 8)
				        {
				            PlayerInfo.pmapx++;
				            SpriteE.SortSprites();
				            prealx=0;
				            if (!Dead)
				              CheckEvenement("En contact");
				        }
				        Animate();
				        ScrollDirection=2;
				    }
			    }
			}
			else if ((keys[KUP] == KeyState.PRESSED) && (prmapy >0 ))
			{
			    if ((Action==0) || (Action==1)) PlayerDirection =0;
			    if (Freeze==false)
			    {
				    access=true;
				    acheval=(PlayerInfo.pmapx % 2==1)? true : false;
				    if (acheval)
				      prmapx=PlayerInfo.pmapx/2;
				    if (CurrentMap.cases[prmapx][prmapy-1].X1 > 0)
				      access=block[0][CurrentMap.cases[prmapx][prmapy-1].X1-1][CurrentMap.cases[prmapx][prmapy-1].Y1-1];
				    if ((CurrentMap.cases[prmapx][prmapy-1].X2 > 0) && (access==true))
				      access=block[1][CurrentMap.cases[prmapx][prmapy-1].X2-1][CurrentMap.cases[prmapx][prmapy-1].Y2-1];
				    if ((Evenements[prmapx][prmapy-1].evenement != null) && (access==true))
				    	access=!Evenements[prmapx][prmapy-1].evenement.get(Evenements[prmapx][prmapy-1].Ev).Bloquant;
				    if ((acheval) && (access==true))
				    {
				        if (CurrentMap.cases[prmapx+1][prmapy-1].X1 > 0)
				          access=block[0][CurrentMap.cases[prmapx+1][prmapy-1].X1-1][CurrentMap.cases[prmapx+1][prmapy-1].Y1-1];
				        if ((CurrentMap.cases[prmapx+1][prmapy-1].X2 > 0) && (access=true))
				          access=block[1][CurrentMap.cases[prmapx+1][prmapy-1].X2-1][CurrentMap.cases[prmapx+1][prmapy-1].Y2-1];
				        if ((Evenements[prmapx+1][prmapy-1].evenement != null) && (access==true))
					    	access=!Evenements[prmapx+1][prmapy-1].evenement.get(Evenements[prmapx+1][prmapy-1].Ev).Bloquant;
				    }
				      if ((access) && (PossibleBloqueMonstre==true))
				      {
				        i=0;
				        while ((i<SpriteE.Sprites.size()) && (access==true))
				        {
				          if (SpriteE.Sprites.get(i).ID==Sprite.idSprite.idMonstre)
				          {
				        	MChar=(TMChar) SpriteE.Sprites.get(i);
				            if (MChar.Bloquant==true)
				              if  (((MChar.mmapx/2==prmapx) && (MChar.mmapy/2==prmapy-1))
				              || ((acheval==true) && (MChar.mmapx/2==prmapx+1) && (MChar.mmapy/2==prmapy-1)))
				                access=false;
				          }
				          i++;
				        }
				      }
				    if (access==true)
				    {
				        prealy--;
				        if (prealy <= -8)
				        {
				            PlayerInfo.pmapy--;
				            SpriteE.SortSprites();
				            prealy=0;
				            if (!Dead)
				              CheckEvenement("En contact");
				        }
				        Animate();
				        ScrollDirection=1;
				    }
			    }
			}
			else if ((keys[KDOWN]==KeyState.PRESSED) && (prmapy < CurrentMap.cases[0].length-1))
			{
			    if ((Action==0) || (Action==1)) PlayerDirection =2;
			    if (Freeze==false)
			    {
				    access=true;
				    acheval=(PlayerInfo.pmapx % 2==1)? true : false;
				    if (acheval)
				      prmapx=PlayerInfo.pmapx/2;
				    if (CurrentMap.cases[prmapx][prmapy+1].X1 > 0)
				      access=block[0][CurrentMap.cases[prmapx][prmapy+1].X1-1][CurrentMap.cases[prmapx][prmapy+1].Y1-1];
				    if ((CurrentMap.cases[prmapx][prmapy+1].X2 > 0) && (access==true))
				      access=block[1][CurrentMap.cases[prmapx][prmapy+1].X2-1][CurrentMap.cases[prmapx][prmapy+1].Y2-1];
				    if ((Evenements[prmapx][prmapy+1].evenement != null) && (access==true))
				    	access=!Evenements[prmapx][prmapy+1].evenement.get(Evenements[prmapx][prmapy+1].Ev).Bloquant;
				    if ((acheval) && (access==true))
				    {
				        if (CurrentMap.cases[prmapx+1][prmapy+1].X1 > 0)
				          access=block[0][CurrentMap.cases[prmapx+1][prmapy+1].X1-1][CurrentMap.cases[prmapx+1][prmapy+1].Y1-1];
				        if ((CurrentMap.cases[prmapx+1][prmapy+1].X2 > 0) && (access==true))
				          access=block[1][CurrentMap.cases[prmapx+1][prmapy+1].X2-1][CurrentMap.cases[prmapx+1][prmapy+1].Y2-1];
				        if ((Evenements[prmapx+1][prmapy+1].evenement != null) && (access==true))
					    	access=!Evenements[prmapx+1][prmapy+1].evenement.get(Evenements[prmapx+1][prmapy+1].Ev).Bloquant;
				    }
				      if ((access) && (PossibleBloqueMonstre==true))
				      {
				        i=0;
				        while ((i<SpriteE.Sprites.size()) && (access==true))
				        {
				          if (SpriteE.Sprites.get(i).ID==Sprite.idSprite.idMonstre)
				          {
				        	MChar=(TMChar) SpriteE.Sprites.get(i);
				            if (MChar.Bloquant==true)
				              if  (((MChar.mmapx/2==prmapx) && (MChar.mmapy/2==prmapy+1))
				              || ((acheval==true) && (MChar.mmapx/2==prmapx+1) && (MChar.mmapy/2==prmapy+1)))
				                access=false;
				          }
				          i++;
				        }
				      }
				    if (access==true)
				    {
				        prealy++;
				        if (prealy >= 8)
				        {
				            PlayerInfo.pmapy++;
				            SpriteE.SortSprites();
				            prealy=0;
				            if (!Dead)
				              CheckEvenement("En contact");
				        }
				        Animate();
				        ScrollDirection=3;
				    }
				}
			}
			else
			{
			    if ((Action==0) || (Action==1))
			    {
			      AnimPhase =1;
			      Sens = 1;
			    }
			}
		}
    
		public void Move(KeyboardInput.KeyState[] keys)
	    {
	    }
        public void Animate()
        {
        	  AttenteAnim++;
        	  if (AttenteAnim > 8)
        	  {
        	    AttenteAnim = 0;
        	    AnimPhase = AnimPhase + Sens;
        	    if (AnimPhase >= 3)
        	    {
        	      AnimPhase =1;
        	      Sens = -1;
        	    }
        	    if (AnimPhase <= -1) 
        	    {
        	      AnimPhase =1;
        	      Sens = 1;
        	    }
        	  }        	
        }
	}
	
	private void PlaySound(String sound,String name,boolean loop)
	{
		String fileext;
		  if ((HasSound) && (sound!=null))			  
		  {
		    if (sound.compareTo("")!=0)
		    {
		    		sound=general.getName()+"/"+sound.replace("Sound\\", "Sound/");
		    		try {
		        	  fileext=sound.substring(sound.lastIndexOf(".")+1);
		        	  if (fileext.compareTo("mp3")==0)
		        	  {
			        	  SoundTh=new SoundThread();
			        	  SoundTh.setSoundFic(sound);
			    	      SoundTh.start();
		        	  }
		        	  if (fileext.compareTo("wav")==0)
		        	  {
		        		   AudioPlayer.loadClip(name, sound);
		        		   AudioPlayer.play(name, loop);
		        	  }
		    	  }catch (Exception e) {e.printStackTrace();}
		    }
		  }
	}

	private void PlayMusic(String MusicFile,boolean ForcePlay)
	{
		String fileext;
		  if ((HasMusic) && (MusicFile!=null))			  
		  {
		    if (MusicFile.compareTo("")!=0)
		    {
			      if ((MusicFile.compareTo(LastMusic)!=0)||(ForcePlay==true))
			      {			    	  
			          try {
			        	  LastMusic="";
			        	  if (midiplayer!=null)
			        		  midiplayer.close();
			        	  if (mp3player!=null)
			        		  mp3player.close();
			        	  mp3player=null;
			        	  midiplayer=null;
			        	  MusicTh=null;
			        	  fileext=MusicFile.substring(MusicFile.lastIndexOf(".")+1);
			        	  if (fileext.compareTo("mp3")==0)
			        	  {
				    	      mp3player=new javazoom.jl.player.Player(new FileInputStream(general.getName()+"/"+MusicFile.replace("Sound\\", "Sound/")));
				    	      MusicTh=new MusicThread();
				    	      MusicTh.start();
			        	  }
			        	  if (fileext.compareTo("mid")==0)
			        	  {
			        		  midiplayer=new MidiPlayer();
			        		  midiseq=midiplayer.getSequence(general.getName()+"/"+MusicFile.replace("Sound\\", "Sound/"));
			        		  midiplayer.play(midiseq,true);
			        	  }
			    	  }catch (Exception e) {e.printStackTrace();}
			          LastMusic=MusicFile;
			      }
		    }
 	        else
 	        {
 	        	try {
 	        		  LastMusic="";
		        	  if (midiplayer!=null)
		        		  midiplayer.close();
		        	  if (mp3player!=null)
		        		  mp3player.close();
		        	  mp3player=null;
		        	  midiplayer=null;
		        	  MusicTh=null;
				} catch (Exception e) {
					e.printStackTrace();
				}
 	        }
		  }
	}
	
	private void KillPlayer()
	{
        player.Transparency=0.5f;
        if (fiolevie!=null)
        	fiolevie.Redraw();
        IsInEvent=false;
        IsInvActive=false;
        IsInInputString=false;
        IsMenuActive=false;
        IsStatActive=false;
        IsInvEquipActive=false;
        IsMagasinActive=false;
        IsInSelectMChar=false;
	}
	
	private void InitialisePlayerInfo(Projet gen)
	{
	    PlayerInfo=null;
		if ((general.getStyleProjet()==0)&&(gen!=null))
			if (new File(System.getProperty("user.dir")+"/"+general.getName()+".sav").exists())
				Chargement(System.getProperty("user.dir")+"/"+general.getName()+".sav");
		if (PlayerInfo==null)
		{
			PlayerInfo=new struct.Sauvegarde();
			Depart depart=general.getDepart();
			if (depart.Carte.equals(""))
			{
				PlayerInfo.pmapx=0;
				PlayerInfo.pmapy=0;
				PlayerInfo.CurrentMap=general.getCartes().get(0).Name;
			}
			else
			{
				PlayerInfo.pmapx=(short) (depart.X * 2);
				PlayerInfo.pmapy=(short) (depart.Y * 2);
				PlayerInfo.CurrentMap=depart.Carte;
			}
			depart=general.getMort();
			if (depart.Carte.equals(""))
			{
				PlayerInfo.ResX=0;
				PlayerInfo.ResY=0;
				PlayerInfo.ResCarte=PlayerInfo.CurrentMap;
			}
			else
			{
				PlayerInfo.ResX=(short) (depart.X * 2);
				PlayerInfo.ResY=(short) (depart.Y * 2);
				PlayerInfo.ResCarte=depart.Carte;
			}
			PlayerInfo.Chipset="Chipset\\guerrier01.png";
			PlayerInfo.PrevXP=0;
			PlayerInfo.CurrentXP=0;
			if (general.getCourbeXP().size()>0)
				PlayerInfo.NextXP=general.getCourbeXP().get(0);
			PlayerInfo.Menu.add(MenuPossibles.get(0));
			PlayerInfo.Menu.add(MenuPossibles.get(5));
			PlayerInfo.Menu.add(MenuPossibles.get(6));
			if (general.getStyleProjet()==1)
			{
				PlayerInfo.Menu.add(MenuPossibles.get(3));
				PlayerInfo.Menu.add(MenuPossibles.get(4));			
			}
			PlayerInfo.Classe=null;
			PlayerInfo.Lvl=1;
		}
	}
	
	private void LoadSurface()
	{
		Image Surf;
		BufferedImage Brouillard;
		SystemSurf = LoadImage("Ressources/Chipset/System.png",false);
	   	SystemSurf = SystemSurf.getScaledInstance((int)(SystemSurf.getWidth(null)*Zoom),(int)(SystemSurf.getHeight(null)*Zoom), Image.SCALE_FAST);                		
		CaverneSurf = LoadImage("Ressources/Chipset/caverne.png",false);
		CaverneSurf = CaverneSurf.getScaledInstance((int)(CaverneSurf.getWidth(null)*Zoom),(int)(CaverneSurf.getHeight(null)*Zoom), Image.SCALE_FAST);
		Surf = LoadImage("Ressources/Chipset/brouillard.png",true);
		Surf = Surf.getScaledInstance((int)(Surf.getWidth(null)*Zoom),(int)(Surf.getHeight(null)*Zoom), Image.SCALE_FAST);
	    GraphicsConfiguration gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice ().getDefaultConfiguration();
		Brouillard=gc.createCompatibleImage(Surf.getWidth(null)*3, Surf.getHeight(null)*3,java.awt.Transparency.TRANSLUCENT);
	    Graphics2D g2d = Brouillard.createGraphics();
	    for (int i=0;i<3;i++)
	    {
	        for (int j=0;j<3;j++)
	    	{
	    		  g2d.drawImage(Surf,i*Surf.getWidth(null),j*Surf.getHeight(null),Surf.getWidth(null),Surf.getHeight(null), null);
	    	}
	    }
	    g2d.dispose();
	    BrouillardSurf=Brouillard;		
		PluieSurf = LoadImage("Ressources/Chipset/pluie.png",true);
		PluieSurf = PluieSurf.getScaledInstance((int)(PluieSurf.getWidth(null)*Zoom),(int)(PluieSurf.getHeight(null)*Zoom), Image.SCALE_FAST);                		
		NeigeSurf = LoadImage("Ressources/Chipset/neige.png",true);
		NeigeSurf = NeigeSurf.getScaledInstance((int)(NeigeSurf.getWidth(null)*Zoom),(int)(NeigeSurf.getHeight(null)*Zoom), Image.SCALE_FAST);                		

	}
	
	private void ReadMap()
	{
		int OldEffect=Effect;
		int i;
		if (Evenements!=null)
		{
			for(i=0;i<CurrentMap.TailleX;i++)
				for(int j=0;j<CurrentMap.TailleY;j++)
				{
					  if (Evenements[i][j].Sprite!=null)
						  Evenements[i][j].Sprite.Kill();
				      Evenements[i][j].Sprite=null;
				}
		}
		HasToSendFinEv=false;
		AutoEvent=false;
		AttenteEvenement=0;
		FrmSrc=this;
		SpriteEv=null;
		IsInEvent=false;
		ImportantThing=true;
		IsInInputString=false;
		IsMenuActive=false;
		PossibleBloqueMonstre=false;
		ListeMonstre=new ArrayList<MonstreGame>();
		ListeMagie=new ArrayList<MagieGame>();
		//CurrentMap=general.getCarteByName(PlayerInfo.CurrentMap);
		
		
		Global.lock();
		Global.envoi("\\map");
		CurrentMap=(Carte) Global.recOb();
		
		zones.clear();
		CurrentSpawn.clear();
		CurrentMonstre.clear();
		for(i=0;i<CurrentMap.zones.size();i++)
 		{
			zones.add(CurrentMap.zones.get(i).clone());
			CurrentSpawn.add(0);
			CurrentMonstre.add(0);
		}
		Effect=CurrentMap.Effect;
		if (StaticSurf!=null)
			StaticSurf=null;
		if (CurrentMap.Static.compareTo("")!=0)
		{
			StaticSurf=LoadImage("Ressources/"+CurrentMap.Static.replace("Chipset\\", "Chipset/"), true);
		}
		if ((Effect>=1) && (Effect<=3)) PEffect=Effect;
		else PEffect=0;
	    if ((Effect==4) && (OldEffect!=4)) PlaySound("/Sound/pluie.wav","pluie",true);
	    else if ((OldEffect==4) && (Effect!=4)) AudioPlayer.stop("pluie");
	    if (OldEffect!=Effect)
	    	LoadSurface();
		if (SpriteE!=null)
		{
			for(i=0;i<SpriteE.Sprites.size();i++)
				SpriteE.Sprites.get(i).Kill();
			player=new Player(this,Zoom);			
			SpriteE.AddSprite(player);
			if (PlayerInfo.Classe!=null)
			{
				fiolevie=new FioleVie(this);
				SpriteE.AddSprite(fiolevie);
				fiolemana=new FioleMana(this);
				SpriteE.AddSprite(fiolemana);
				barrexp=new BarreXP(this);
				SpriteE.AddSprite(barrexp);
			}
			barreicone=new BarreIcone(this);
			SpriteE.AddSprite(barreicone);
		}
		Evenements=new Events[CurrentMap.TailleX][CurrentMap.TailleY];
		String s;
		for(i=0;i<CurrentMap.TailleX;i++)
			for(int j=0;j<CurrentMap.TailleY;j++)
			{
				  Evenements[i][j]=new Events();
			      Evenements[i][j].Ev=-1;
			      Evenements[i][j].Sprite=null;
			      Evenements[i][j].WaitingTimer=0;
			      Evenements[i][j].WaitingTimer2=0;
			      Evenements[i][j].WaitingTimer3=0;
			      if (CurrentMap.evenements[i][j]!=null)
			      {
				      Evenements[i][j].evenement=new ArrayList<Evenement>();
				      Evenements[i][j].CondDecl=new ArrayList<ArrayList<String>>();
				      Evenements[i][j].CommandeEv=new ArrayList<ArrayList<String>>();
				      for(int k=0;k<CurrentMap.evenements[i][j].evenement.size();k++)
				      {
				    	  Evenements[i][j].evenement.add(CurrentMap.evenements[i][j].evenement.get(k).clone());
				    	  Evenements[i][j].CondDecl.add(new ArrayList<String>());
			    		  for(int l=0;l<CurrentMap.evenements[i][j].CondDecl.get(k).size();l++)
			    		  {
			    				s=CurrentMap.evenements[i][j].CondDecl.get(k).get(l);
			    				Evenements[i][j].CondDecl.get(k).add(s);
			    		  }
				    	  Evenements[i][j].CommandeEv.add(new ArrayList<String>());
			    		  for(int l=0;l<CurrentMap.evenements[i][j].CommandeEv.get(k).size();l++)
			    		  {
			    				s=CurrentMap.evenements[i][j].CommandeEv.get(k).get(l);
			    				Evenements[i][j].CommandeEv.get(k).add(s);
			    		  }
				      }
				      Evenements[i][j].Done=new boolean[Evenements[i][j].evenement.size()];
				      for(int k=0;k<Evenements[i][j].evenement.size();k++)
				    	  Evenements[i][j].Done[k]=false;
			      }
			}
		//block=general.getBlocageByName(CurrentMap.Chipset).blocage;
		
		block=(boolean[][][]) Global.recOb();
		Global.unlock();
		
		
	    ImageFilter cif2;
		ImageProducer improd;
		Tile=null;
		String _Image="Ressources/"+CurrentMap.Chipset.replace("Chipset\\", "Chipset/");
		if (new File(_Image).exists())
		{
		  Tile=LoadImage(_Image,true);
		  Background= new Image[Tile.getWidth(null)/16][Tile.getHeight(null)/16];
		  improd=Tile.getSource();
		  for (i=0;i<Background.length;i++)
		  {
			for(int j=0;j<Background[i].length;j++)
			{
			  	cif2 =new CropImageFilter(i*16, j*16,16,16);
			   	Background[i][j] = createImage(new FilteredImageSource(improd, cif2));
			   	Background[i][j]=Background[i][j].getScaledInstance((int)(16*Zoom), (int)(16*Zoom), Image.SCALE_FAST); 
			}
		  }
		}
		/*if (general.getStyleProjet()==0)
			Sauvegarder(System.getProperty("user.dir")+"/"+general.getName()+".sav");*/
		//System.gc();
		PlayMusic(CurrentMap.Music,false);
	    GerePageEvenement();
		ImportantThing=false;		
		PopulateMonsterMap();
		HasChangedMap=false;
	}

	private void DrawBackGround(Graphics2D g)
	{
	  int x, y , prmapx , prmapy,Left , Right , Top , Bottom;
	  Rectangle Dest=new Rectangle();
	  if ((SVersX!=-1) || (SVersY!=-1))
	  {
	      if (SVersX>SScrollX) SVersX--;
	      if (SVersX<SScrollX) SVersX++;
	      if (SVersY>SScrollY) SVersY--;
	      if (SVersY<SScrollY) SVersY++;

	      ScX=SVersX-((PlayerInfo.pmapx*8)+prealx);
	      ScY=SVersY-((PlayerInfo.pmapy*8)+prealy);
	  }
	  else
	  {
	      ScX=0;
	      ScY=0;
	  }

	  prmapx = (PlayerInfo.pmapx / 2)-((CentreX+PlayerInfo.CentreX) / 2)+(ScX / 16);
	  prmapy = (PlayerInfo.pmapy / 2) - ((CentreY+PlayerInfo.CentreY) / 2)+(ScY / 16);
	  Dest.x=ScreenX; Dest.y=ScreenY;
	  Dest.width=(int) (320*Zoom);
	  Dest.height=(int) (240*Zoom);
	  if (StaticSurf!=null)
		  DrawImage(g,StaticSurf,Dest.x,Dest.y,Dest.width,Dest.height, null);
	   if (Effect==1)
	   {
	     Left=5;
	     Right=15;
	     Top=3;
	     Bottom=12;
	   }
	   else
	   {
	      Left=-1;
	      Right=31;
	      Top=-1;
	      Bottom=16;
	   }
	   for (x=Left;x<Right;x++)
	      for (y=Top;y<Bottom;y++)
	      {
	        Dest.x = (int) ((x * 16 * Zoom)- ((prealx * Zoom)+ (8 * Zoom * (PlayerInfo.pmapx % 2)))+ScreenX-((ScX % 16)*Zoom));
	        Dest.y = (int) ((y * 16 * Zoom)- ((prealy * Zoom)+ (8 * Zoom * (PlayerInfo.pmapy % 2)))+ScreenY-((ScY % 16)*Zoom));
	        Dest.width = (int) (16*Zoom);
	        Dest.height = (int) (16*Zoom);
	        if(Zoom==3.2) {Dest.width++;Dest.height++;}
	        if ((x+prmapx>=0) && (x+prmapx<CurrentMap.cases.length))
	          if ((y+prmapy>=0) && (y+prmapy<CurrentMap.cases[x+prmapx].length))
	          {
	        	if (CurrentMap.cases[x+prmapx][y+prmapy].X1 > 0)  
	        	{
	              Dest.x = (int) ((x * 16 * Zoom)- ((prealx * Zoom)+ (8 * Zoom *((PlayerInfo.pmapx) % 2)))+ScreenX-((ScX % 16)*Zoom));
	              Dest.y = (int) ((y * 16 * Zoom)- ((prealy * Zoom)+ (8 * Zoom *((PlayerInfo.pmapy) % 2)))+ScreenY-((ScY % 16)*Zoom));
	  			  DrawImage(g,Background[CurrentMap.cases[x+prmapx][y+prmapy].X1-1][CurrentMap.cases[x+prmapx][y+prmapy].Y1-1],Dest.x,Dest.y,Dest.width,Dest.height, null);
	        	}
	        	if (CurrentMap.cases[x+prmapx][y+prmapy].X2 > 0)  
	        	{
	              Dest.x = (int) ((x * 16 * Zoom)- ((prealx * Zoom)+ (8 * Zoom *((PlayerInfo.pmapx) % 2)))+ScreenX-((ScX % 16)*Zoom));
	              Dest.y = (int) ((y * 16 * Zoom)- ((prealy * Zoom)+ (8 * Zoom *((PlayerInfo.pmapy) % 2)))+ScreenY-((ScY % 16)*Zoom));
	  			  DrawImage(g,Background[CurrentMap.cases[x+prmapx][y+prmapy].X2-1][CurrentMap.cases[x+prmapx][y+prmapy].Y2-1],Dest.x,Dest.y,Dest.width,Dest.height, null);
	        	}
	          }
	      }
	     if ((Effect==1) || (Effect==6))
	     {
		      for (x=0;x<SpriteE.Sprites.size();x++)
		        if ((SpriteE.Sprites.get(x).ID==Sprite.idSprite.idMChar) || (SpriteE.Sprites.get(x).ID==Sprite.idSprite.idFChar) || (SpriteE.Sprites.get(x).ID==Sprite.idSprite.idEvenement)
		        || (SpriteE.Sprites.get(x).ID==Sprite.idSprite.idAnimation) || (SpriteE.Sprites.get(x).ID==Sprite.idSprite.idMonstre) || (SpriteE.Sprites.get(x).ID==Sprite.idSprite.idPLAYER))
		        	SpriteE.Sprites.get(x).Draw(g);
	          Dest.x=ScreenX; Dest.y=ScreenX;
		      if (Effect==1)
		      {
		    	    Dest.x=ScreenX; Dest.y=ScreenY;
		    	    Dest.width=(int) (320*Zoom);
		    	    Dest.height=(int) (240*Zoom);
		    	  	DrawImage(g,CaverneSurf,Dest.x,Dest.y,Dest.width,Dest.height, null);
		      }
		      if (Effect==6)
		      {
		    	  	switch(ScrollDirection)
		    	  	{
		    	  		case 1 : brouillardy--; break;
		    	  		case 3 : brouillardy++; break;		    	  		
		    	  	}
		    	  	if (Util.random(100)>30) brouillardx++;
		    	  	if (Util.random(100)>95)
		    	  	{
		    	  		if (Util.random(10)>3)
		    	  			brouillardy++;
		    	  		else
		    	  			brouillardy--;
		    	  	}
		    	  	if (brouillardx>(366*Zoom*2*8)) brouillardx=(int) (366*Zoom*1*8);
		    	  	if (brouillardx<=0) brouillardx=(int) (366*Zoom*1*8);
		    	  	if (brouillardy>(249*Zoom*2*3)) brouillardy=(int) (249*Zoom*1*3);
		    	  	if (brouillardy<=0) brouillardy=(int) (249*Zoom*1*3);
		    	  	Dest.x=-brouillardx / 8; Dest.y=-brouillardy / 3;
			        Dest.width=BrouillardSurf.getWidth(null); Dest.height=BrouillardSurf.getHeight(null);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,200f/255f));
			        DrawImage(g,BrouillardSurf,Dest.x,Dest.y,Dest.width,Dest.height, null);
				    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		      }
		      for (x=0;x<SpriteE.Sprites.size();x++)
		        if ((SpriteE.Sprites.get(x).ID!=Sprite.idSprite.idMChar) && (SpriteE.Sprites.get(x).ID!=Sprite.idSprite.idFChar) && (SpriteE.Sprites.get(x).ID!=Sprite.idSprite.idEvenement)
				        && (SpriteE.Sprites.get(x).ID!=Sprite.idSprite.idAnimation) && (SpriteE.Sprites.get(x).ID!=Sprite.idSprite.idMonstre) && (SpriteE.Sprites.get(x).ID!=Sprite.idSprite.idPLAYER))
		          SpriteE.Sprites.get(x).Draw(g);
		}
	    else
	    	SpriteE.Draw(g);
/*	    if (ScreenX>0)
	    {
	      Dest.x=DecoX;
	      Dest.y=DecoY;
	      Dest.width=ScreenX;
	      Dest.height=this.getHeight();
	      g.clearRect(Dest.x,Dest.y,Dest.width,Dest.height);
	      Dest.x=320*Zoom+ScreenX+DecoX;
	      Dest.y=DecoY;
	      Dest.width=ScreenX;
	      Dest.height=this.getHeight();
	      g.clearRect(Dest.x,Dest.y,Dest.width,Dest.height);
	    }
	    if (ScreenY>0)
	    {
	      Dest.x=DecoX;
	      Dest.y=DecoY;
	      Dest.width=this.getWidth();
	      Dest.height=ScreenY;
	      g.clearRect(Dest.x,Dest.y,Dest.width,Dest.height);
	      Dest.x=DecoX;
	      Dest.y=240*Zoom+ScreenY+DecoY;
	      Dest.width=this.getWidth();
	      Dest.height=ScreenY;
	      g.clearRect(Dest.x,Dest.y,Dest.width,Dest.height);
	    }		*/
	}
	private void GereJoystick()
	{
		// TODO @BETA2 A utiliser pour le joystick plus tard
		boolean UseKeyboard=false;
	    if ((keys[KSPACE]==KeyState.ONCE) && (UseKeyboard==false) && (IsInInputString==false) && (IsMenuActive==false)
	    && (IsInEvent==false)  && (IsInSelectMChar==false) && (Dead==false))
	    {
	      CheckEvenement("Appuie sur bouton");
	      try{
	    	  Thread.sleep(100);
	    	}
	    	catch(InterruptedException ie){
	    	}
	    }

	}
	

	private void SetScreen(int FullScreen,boolean setDecorated)
	{
		if (Zoom==0)
		{
				ScreenX=getWidth();
				ScreenY=getHeight();
			    // on va chercher la meilleur résolution possible
			    while ((ScreenX>320*Zoom) && (ScreenY>240*Zoom))
			      Zoom++;
			    Zoom--;
			    // On a choisit le meilleur zoom possible. Maintenant on calcule les valeurs des screenx / y
			    if (FullScreen>0)
				{
			    	ScreenX=(int) ((ScreenX - (320 * Zoom)) / 2);
			    	ScreenY=(int) ((ScreenY - (240 * Zoom)) / 2);
				}
			    else
			    {
			    	 ScreenX=0;
					 ScreenY=0;
			    }
		}
		else
		{
		    ScreenX=0;
		    ScreenY=0;
		}				
		if (FullScreen>0)
		{
			GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			boolean isFullScreen = device.isFullScreenSupported();
	        if (isFullScreen) {
	            // Full-screen mode
	        	if (setDecorated)
	        		this.setUndecorated(true);
	        	if ((ScreenX==0)&&(ScreenY==0))
	        	{
		        	try
		        	{
		        		DisplayMode mode[] = device.getDisplayModes();
		        		DisplayMode modeOk = new DisplayMode((int)(320*Zoom), (int)(240*Zoom), 16, DisplayMode.REFRESH_RATE_UNKNOWN);
		        		for(int i =0;i<mode.length;i++)
		        		{
		        			if(mode[i].getWidth()==(int)(320*Zoom) && mode[i].getHeight()==(int)(240*Zoom))
		        				modeOk=mode[i];
		        		}
		        		System.out.println("Fullscreen:"+modeOk.getWidth()+"x"+modeOk.getHeight()+":"+modeOk.getBitDepth() + ":" +modeOk.getRefreshRate());
		        		
			            device.setFullScreenWindow(this);
			            if(device.isDisplayChangeSupported()) device.setDisplayMode(modeOk);
		        	}catch (Exception e) 
		        	{
		        		System.out.println("Resolution change not supported");
		        		Zoom=0;
		        		SetScreen(FullScreen,false);
		        		return;
		        	}
		        	
	        	}
	        }

			// Cache le curseur
			 Toolkit t = Toolkit.getDefaultToolkit();
		     Dimension d = t.getBestCursorSize(1,1);
		     Cursor NO_CURSOR;
		     if(d.width!=0 && d.height!=0)
		     {
		         NO_CURSOR = t.createCustomCursor(new BufferedImage(d.width,d.height,BufferedImage.TYPE_INT_ARGB),new Point(0,0),"NO_CURSOR");
		     }
		     else
		     {
		         NO_CURSOR = null;
		     }
		     this.setCursor(NO_CURSOR);
		     DecoX=0;
		     DecoY=0;
			this.setVisible(true);
			this.createBufferStrategy(3);
		}
		else
		{
			setUndecorated(true);
			
			addMouseListener(new MouseAdapter() 
			{
				
				public void mousePressed(MouseEvent e) 
				{
					startDrag=e.getPoint();
				}
				
				public void mouseClicked(MouseEvent e) 
				{
					Point p=e.getPoint();
					if (p.y < 24 && p.x >320*Zoom-16 && p.x <320*Zoom)
			        {
			        	((Game)e.getSource()).Quit=true;
			        }
					if (p.y < 24 && p.x >320*Zoom-32 && p.x <320*Zoom-16)
			        {
			        	((Game)e.getSource()).setState(ICONIFIED);
			        }
					//((JFrame)e.getSource());
				}
			});
			
			addMouseMotionListener(new MouseMotionAdapter() 
			{
				
				public void mouseDragged(MouseEvent e)
				{
					
					if(startDrag.y>24) return;
					((JFrame)e.getSource()).setLocation(e.getXOnScreen()-startDrag.x, e.getYOnScreen()-startDrag.y);
				}
			});
			
			this.setVisible(true);
			this.createBufferStrategy(3);
			
			Insets DecoFen=getInsets();
			DecoFen.set(24, 1, 2, 1);
			DecoX=DecoFen.left;DecoY=DecoFen.top;
			getContentPane().setSize(320, 240);
			setSize((int)(320*Zoom+DecoX+DecoFen.right),(int)(240*Zoom+DecoY+DecoFen.bottom));
			setPreferredSize(new Dimension((int)(320*Zoom+DecoX+DecoFen.right),(int)(240*Zoom+DecoY+DecoFen.bottom)));
		}
	}
	
	private void drawWM() 
	{
		if(HasFullScreen) return;
		BufferStrategy bf = this.getBufferStrategy();
		Graphics g = null;
		
		try {
			g = bf.getDrawGraphics();
			//g.clearRect(0, 0, getWidth(), getHeight());
			Graphics2D g2 = (Graphics2D)g;
			// Affichage rapide
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
			//g2.setClip(ScreenX, ScreenY, (int)(320*Zoom+DecoX), (int)(240*Zoom+DecoY));
/*	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                            RenderingHints.VALUE_ANTIALIAS_ON);*/
			
			//g2.fillRect(1, 0, (int)(Zoom*320), 24);//Haut
			g2.fillRect(0, 0, 1, (int)(Zoom*240)+26);//Gauche
			g2.fillRect((int)(Zoom*320)+1, 0, 1, (int)(Zoom*240)+26);//Droite
			g2.fillRect(0, (int)(Zoom*240)+25, (int)(Zoom*320)+2, 3);//Bas
		    
			//img=img.getScaledInstance((int)(Zoom*320), 24, BufferedImage.SCALE_FAST);
			
			g2.drawImage(imgWM, 1, 0, (int)(Zoom*320)+1, 24, 0, 0, (int)(Zoom*320), 24, null);
			
			g2.setColor(new Color(255, 255, 255));
			g2.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,18));
			g2.drawString("Neo Late Registration", (int)(Zoom*30), 18);
			
			g2.drawString("×", (int)(Zoom*320)-15, 16);
			g2.drawString("_", (int)(Zoom*320)-30, 14);
			
			g2.setColor(new Color(200, 180, 150));
			g2.drawLine((int)(Zoom*25), 21, (int)(Zoom*200), 21);
			g.fillRect((int)(Zoom*25)-2, 4, 2, 15);
			
			
			g2.dispose();
			
			
			
		} finally {
			// It is best to dispose() a Graphics object when done with it.
			g.dispose();
		}
		
		// Shows the contents of the backbuffer on the screen.
		bf.show();
	 
	        //Tell the System to do the Drawing now, otherwise it can take a few extra ms until 
	        //Drawing is done which looks very jerky
	        Toolkit.getDefaultToolkit().sync();	
	} 
	
	private void LoadProject()
	{
			if (new File(System.getProperty("user.dir")+"/project_path").exists())
			{
			    Properties p = new Properties();
				try {
					//lecture de l'objet
			      try {
					p.load(new FileInputStream(System.getProperty("user.dir")+"/project_path"));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				ObjectInputStream in=new ObjectInputStream(new FileInputStream(System.getProperty("user.dir")+"/"+p.getProperty("project")+"/"+p.getProperty("project")+".prj"));
				try{
					in.read();
					general=(Projet)in.readObject();
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
			else
				Quit=true;
	}
	private double[] LoadParams()
	{
		double[] params=new double[4];
		int i;
		for(i=0;i<4;i++)
			params[i]=-1;
		if (new File(System.getProperty("user.dir")+"/"+general.getName()+".ini").exists())
		{
			  Properties p = new Properties();
		      try {
				p.load(new FileInputStream(System.getProperty("user.dir")+"/"+general.getName()+".ini"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return params;
				} catch (IOException e) {
					e.printStackTrace();
					return params;
				}
				params[0]=Double.parseDouble(p.getProperty("Zoom"));
				params[1]=Integer.parseInt(p.getProperty("FullScreen"));
				params[2]=Integer.parseInt(p.getProperty("Musique"));
				params[3]=Integer.parseInt(p.getProperty("Sound"));
				KS=Integer.parseInt(p.getProperty("KS"));
			    KD=Integer.parseInt(p.getProperty("KD"));
			    KLEFT=Integer.parseInt(p.getProperty("KLEFT"));
			    KRIGHT=Integer.parseInt(p.getProperty("KRIGHT"));
			    KUP=Integer.parseInt(p.getProperty("KUP"));
			    KDOWN=Integer.parseInt(p.getProperty("KDOWN"));
			    KRETURN=Integer.parseInt(p.getProperty("KRETURN"));
			    KSPACE=Integer.parseInt(p.getProperty("KSPACE"));
			    KESCAPE=Integer.parseInt(p.getProperty("KESCAPE"));
			    K0=Integer.parseInt(p.getProperty("K0"));
			    K1=Integer.parseInt(p.getProperty("K1"));
			    K2=Integer.parseInt(p.getProperty("K2"));
			    K3=Integer.parseInt(p.getProperty("K3"));
			    K4=Integer.parseInt(p.getProperty("K4"));
			    K5=Integer.parseInt(p.getProperty("K5"));
			    K6=Integer.parseInt(p.getProperty("K6"));
			    K7=Integer.parseInt(p.getProperty("K7"));
			    K8=Integer.parseInt(p.getProperty("K8"));
			    K9=Integer.parseInt(p.getProperty("K9"));				
		}
		else
		{
/*			    params[0]=0;
		    params[1]=1;
		    params[2]=1;
		    params[3]=1;*/
			params[0]=2;
			params[1]=0;
			params[2]=0;
			params[3]=0;
			KS=KeyEvent.VK_S;
		    KD=KeyEvent.VK_D;
		    KLEFT=KeyEvent.VK_LEFT;
		    KRIGHT=KeyEvent.VK_RIGHT;
		    KUP=KeyEvent.VK_UP;
		    KDOWN=KeyEvent.VK_DOWN;
		    KRETURN=KeyEvent.VK_ENTER;
		    KSPACE=KeyEvent.VK_SPACE;
		    KESCAPE=KeyEvent.VK_ESCAPE;
		    K0=KeyEvent.VK_0;
		    K1=KeyEvent.VK_1;
		    K2=KeyEvent.VK_2;
		    K3=KeyEvent.VK_3;
		    K4=KeyEvent.VK_4;
		    K5=KeyEvent.VK_5;
		    K6=KeyEvent.VK_6;
		    K7=KeyEvent.VK_7;
		    K8=KeyEvent.VK_8;
		    K9=KeyEvent.VK_9;				
		}
		return params;
	}
	private void Sauvegarder(String nomfic)
	{
		try {
			//creation du flux
			ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(nomfic));
			try{		
				//ecriture de l'objet
				out.writeObject(PlayerInfo);
				out.flush();
			}
			finally{
				out.close();
			}
			//lecture de l'objet
		} 
		catch (IOException e1) {
			e1.printStackTrace();
		}						
	}
	private void Chargement(String nomfic)
	{
		try {
			//lecture de l'objet
			ObjectInputStream in=new ObjectInputStream(new FileInputStream(nomfic));
			try{
				PlayerInfo=(struct.Sauvegarde)in.readObject();
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
	
	private long timeleft() {
	  long now = System.currentTimeMillis();
	  if (next_time <= now)
	  {
	    next_time = now + (TICK_INTERVAL -time_passed);
	    return 0;
	  }
	  return (next_time - now);
	}
	private void Sleep(int time)
	{
        try {
			  Thread.sleep(100);
         } catch (InterruptedException e) {
			  e.printStackTrace();
		   }
	}
	private void gameLoop() 
	{
		// Your game logic goes here.
		while(!Quit)
		{
		    Timer++;
		    Timer2++;
		    Timer3++;
			drawStuff();
		    if (((WaitTimer>0) && (Timer>=WaitTimer))
		    || ((WaitTimer2>0) && (Timer2>=WaitTimer2))
		    || ((WaitTimer3>0) && (Timer3>=WaitTimer3)))
		       GereEvenement(false);
			time_passed=System.currentTimeMillis();
	        keys=keyboard.poll();
	        if ( keys[KESCAPE]==KeyState.PRESSED && !IsInInputString && !IsInSelectMChar && System.currentTimeMillis()>(LastMenu+500) && !Global.typing)
	        {
	        	LastMenu=System.currentTimeMillis();
	            if (IsMenuActive==false)
	            {
		        	IsMenuActive=true;
		        	Sleep(100);
		            SpriteE.AddSprite(new Menu(this));
	            }
               else
               {
            	  Sleep(100);
	              IsMenuActive=false;
               }
	        }
	        if (Effect==4)
	          SpriteE.AddSprite(new Pluie(this));
	        if (!HasChangedMap)
	        {
		        if (player!=null)
		        	player.MoveSpecial(keys);
		        SpriteE.Move(keys);
		        GereJoystick();
		        BoucleEvent++;
	          if (BoucleEvent > 16)
	          {
	                if (Effect==5)
	                {
	                  SpriteE.AddSprite(new Neige(this));
	                  SpriteE.AddSprite(new Neige(this));
	                  SpriteE.AddSprite(new Neige(this));
	                }
//	                GereMsg;
	                GereEvenement(true);
	                SpriteE.SortSprites();
	                BoucleEvent = 0;
	              }
	        }
	        if (Dead)
	        {
 	          AttenteDead++;
	          if (AttenteDead > 300)
	          {
	        	  // On ressucite toujours le joueur. Si on veut faire un écran de fin il n'y a qu'a mettre le point de résu
	        	  // Dans une carte adapté.
	        	  PlayerInfo.CurrentMap=PlayerInfo.ResCarte;
	              PlayerInfo.pmapx=PlayerInfo.ResX;
	              PlayerInfo.pmapy=PlayerInfo.ResY;
	              PlayerInfo.CentreX=0;
	              PlayerInfo.CentreY=0;
	              SScrollX=-1;
	              SScrollY=-1;
	              ScX=0;
	              ScY=0;
	              SVersX=-1;
	              SVersY=-1;
	              PlayerInfo.BloqueAttaque=false;
	              PlayerInfo.BloqueMagie=false;
	              PlayerInfo.BloqueDefense=false;
	              PlayerInfo.Vie=PlayerInfo.VieMax;
	              AttenteDead=0;
	              Dead=false;
	              HasChangedMap=true;	              
	          }
	        }
	        if (HasChangedMap==true)
	        {
	            FadeScreen();
	            ReadMap();
	    	    GereEvenement(true);
	        }
			try 
			{
				time_passed=System.currentTimeMillis()-time_passed;
				Thread.sleep(timeleft());
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
			} 
			drawWM();
			//updatePlayers();
		}
		Global.connected=false;
		this.dispose();
   }
	
	 private void FadeScreen()
	 {
		BufferStrategy bf = this.getBufferStrategy();
		Graphics g = null;
		int Transparency=92;
		float t;
		BufferedImage bi=null;
		g = bf.getDrawGraphics();
		Graphics2D g2 = (Graphics2D)g;
		// Affichage rapide
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
    	Robot robot;
		try {
			robot = new Robot();
	    	bi=robot.createScreenCapture(new Rectangle(getWidth(), getHeight()));
		} catch (AWTException e1) {
			e1.printStackTrace();
		}

    	
		try {
			while(Transparency>0)
			{
				g2.clearRect(0, 0, getWidth(), getHeight());
				t=Transparency/92.0f;
				AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, t);
	            g2.setComposite(comp);
				g2.drawImage(bi,0,0,null);
				bf.show();
		        Toolkit.getDefaultToolkit().sync();	
				try {
					Thread.sleep(4);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Transparency--;
			}
			
		} finally {
			// It is best to dispose() a Graphics object when done with it.
			g.dispose();
		}
		
	} 
	
	private void drawStuff() {
		BufferStrategy bf = this.getBufferStrategy();
		Graphics g = null;
		
		try {
			g = bf.getDrawGraphics();
			g.clearRect(2, 25, getWidth()-3, getHeight()-26);
			Graphics2D g2 = (Graphics2D)g;
			// Affichage rapide
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
			//g2.setClip(ScreenX, ScreenY, (int)(320*Zoom+DecoX), (int)(240*Zoom+DecoY));
			g2.setClip(2, 25, getWidth()-4, getHeight()-1);
			
/*	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                            RenderingHints.VALUE_ANTIALIAS_ON);*/
			// It is assumed that mySprite is created somewhere else.
			// This is just an example for passing off the Graphics object.
			
			DrawBackGround(g2);
			g2.setColor(Color.white);
			g2.setFont(new Font(Font.SERIF, Font.PLAIN, (int) (10*Zoom)));
			if(Global.fmP==null)
			{
				Global.fmP=g2.getFontMetrics();
				Global.pseudoCentre=(Global.fmP.stringWidth(Global.pseudo)-25)/2;
				int x = (int) (8 * Zoom * (PlayerInfo.pmapx + CentreX + PlayerInfo.CentreX -(ScX / 8) - PlayerInfo.pmapx));
		    	int y = (int) (8 * Zoom * (PlayerInfo.pmapy + CentreY + PlayerInfo.CentreY -(ScY / 8) - PlayerInfo.pmapy));
		    	x=(int) (x - ((24-(16*Zoom)) / 2) +  ScreenX);
			    y=(int) (y - 32 + (8*Zoom*(32 / (16*Zoom))) +ScreenY);
			    x=(int) (x-Global.pseudoCentre);
			    y=(int) (y+43*Zoom);
			    Global.xPs=x;
			    Global.yPs=y;
			}
			if(!IsInvActive) g2.drawString(Global.pseudo, Global.xPs, Global.yPs);
			
			
			if(Global.timerMsg > System.currentTimeMillis() && !IsInvActive)
			{
				if(Global.fm==null && Global.talk[0]==null)
				{
					FontMetrics fm=g2.getFontMetrics();
					String[] str=new String[4];
					for(int j=0;j<4;j++)
					{
						for(int i=0;str[j]==null || (i<=Global.tMsgFinal.length() && fm.stringWidth(str[j])<60*Zoom);i++)
						{
							str[j]=Global.tMsgFinal.substring(0, i);
						}
						Global.tMsgFinal=Global.tMsgFinal.substring(str[j].length());
						//if(str[j]==null)
					}
					if(str[0]!=null && str[0].length()>0)
						Global.Msgs.add("t|"+str[0]+str[1]+str[2]+str[3]+"\n");
					Global.talk=str;
					Global.fm=fm;
					System.out.println(Arrays.toString(str));
					Global.strWCentre=(fm.stringWidth(str[0])-20)/2;
					
					int x = (int) (8 * Zoom * (PlayerInfo.pmapx + CentreX + PlayerInfo.CentreX -(ScX / 8) - PlayerInfo.pmapx));
			    	int y = (int) (8 * Zoom * (PlayerInfo.pmapy + CentreY + PlayerInfo.CentreY -(ScY / 8) - PlayerInfo.pmapy));
			    	x=(int) (x - ((24-(16*Zoom)) / 2) +  ScreenX);
				    y=(int) (y - 32 + (8*Zoom*(32 / (16*Zoom))) +ScreenY);
				    x=(int) (x-Global.strWCentre);
				    y=(int) (y+20*Zoom);
				    int siz=0;
				    for(int i=0;i<4;i++)
				    	if(Global.talk[i]!=null && Global.talk[i].length()>0) siz++;
				    Global.sizMs=siz;
				    y=(int) (y-12*siz*Zoom);
				    Global.xMs=x;
				    Global.yMs=y;
				}
				
				int y=Global.yMs;
				for (int i=0;i<4;i++)
				{
					if(Global.talk[i]==null)
					{
						break;
					}
					else
					{
						g2.drawString(Global.talk[i], Global.xMs, y);
						y=(int) (y+12*Zoom);
					}
				}
				//CreateWindow(60,(int)((((Global.tMsgFinal.length()-1)*15)+18)*Zoom),false);
				//g2.drawString(Global.tMsgFinal, (float)(16*Zoom*(CentreX-PlayerInfo.pmapx)), (float)(16*Zoom*(CentreY-PlayerInfo.pmapy)));
				//System.out.println( Integer.toString(CentreX-PlayerInfo.pmapx) +","+ Integer.toString(CentreY-PlayerInfo.pmapy) );
			}
			if(Global.typing)
			{
				g2.drawString(">>> "+Global.tMsg+"_", (float)(10*Zoom), (float)(220*Zoom));
			}
			
			
			
		} finally {
			// It is best to dispose() a Graphics object when done with it.
			g.dispose();
		}
		
		// Shows the contents of the backbuffer on the screen.
		bf.show();
	 
	        //Tell the System to do the Drawing now, otherwise it can take a few extra ms until 
	        //Drawing is done which looks very jerky
	        Toolkit.getDefaultToolkit().sync();	
	} 
	
	class PositionJ implements Runnable
	{
		
		@Override
		public void run()
		{
			try 
			{
				while(Global.connected)
				{
					Thread.sleep(200);
					if(!Global.lock)	
					{
						if(Global.Msgs.isEmpty())
						{
							Global.envoi("p"+PlayerInfo.pmapx+"|"+PlayerInfo.pmapy+"|"+PlayerDirection);
						}
						else
						{
							Global.envoi(Global.Msgs.getFirst());
							Global.Msgs.removeFirst();
						}
					}
				}
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	class ReceptionJ implements Runnable
	{
		@Override
		public void run()
		{
			try 
			{
				while(Global.connected)
				{
					Thread.sleep(200);
					if(!Global.lockR)	
					{
						Global.dummy=true;
						traiterReponseSrv(Global.rec());
						Global.dummy=false;
					}
				}
			} catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	synchronized public void traiterReponseSrv(String msg)
	{
		System.out.println(msg);
		if(msg.startsWith("pp:"))
		{//pp:Pseudo|X|Y|D~Pseudo2|X|Y|D~Echo|5|17|1~
			//long start = System.currentTimeMillis();
			psdUsedOld=(HashMap<String, PosXY>) ListeJMap.clone();
			psdUsed.clear();
			String psd;
			int x, y,d;
			msg=msg.substring(3);
			while(msg.length()>1)
			{
				psd=msg.split("\\|")[3];
				x=Integer.parseInt(msg.split("\\|")[1]);
				y=Integer.parseInt(msg.split("\\|")[2]);
				d=Integer.parseInt(psd.substring(0, psd.indexOf("~")));
				psd=msg.split("\\|")[0];
				if(!psd.equals(Global.pseudo))
					psdUsed.put(psd, new PosXY(x, y, d));
				msg=msg.substring(msg.indexOf("~")+1);
			}
			ListeJMap=(HashMap<String, PosXY>) psdUsed.clone();
			/**Affichage**/
			PosXY tmpP;
			Iterator<String> it = ListeJMap.keySet().iterator();
			Iterator<PosXY> it2= ListeJMap.values().iterator();
			while(it.hasNext())
			{
				tmpP=it2.next();
				psd=it.next();
				//System.out.println(psd+" en "+tmpP.X+","+tmpP.Y+":"+tmpP.D);
				if(!psdUsedOld.containsKey(psd))
					CreatePlayer(new OtherPlayer(psd, tmpP, "Chipset/guerrier01.png"));
				else
				{
					OtherPlayer xM = ListePlayer.get(psd);
					xM.setPos(tmpP);
				}
			}
			/*long end = System.currentTimeMillis();
			System.out.println(end-start);*/
		}
		else if (msg.startsWith("t:"))
		{
			msg=msg.substring(2);
			try{
				String psd=msg.split("\\|")[0];
				String tlk=msg.split("\\|")[1];
				if(psd.equals(Global.pseudo)) return;
				OtherPlayer pl = ListePlayer.get(psd);
				pl.tMsgFinal=tlk;
				pl.timerMsg=System.currentTimeMillis() + 5000;
				pl.talk[0]=null;
				pl.fm=null;
				System.out.println(psd +" : "+tlk);
			}catch(java.lang.ArrayIndexOutOfBoundsException e){System.out.println("Talk error");}
			
		}
		else if (msg.startsWith("statmsg:"))
		{
			msg=msg.replace("statmsg:", "");
			System.out.println(msg.replace("~", "\n"));
		}
		else if (msg.equals("dmy")){}
	}
	
	class PosXY
	{
		int X=0;
		int Y=0;
		int Rnd=0;
		int D=0;
		public PosXY(int x, int y,int d)
		{X=x; Y=y; D=d;}
	}

	class OtherPlayer
	{
		public String name=null;
		public int level=-1;
		public int HP=-1;
		public String chipset;
		public PosXY pos;
		public boolean dead=false;
		public TMChar sprite;
		public int idx=-1;
	    public FontMetrics fmP,fm;
	    public int xPs,yPs,pseudoCentre,strWCentre;
		public String tMsgFinal="";
		public long timerMsg=0;
		public String[] talk=new String[4];
		public int xMs,yMs,sizMs;
		
		public OtherPlayer(String name,PosXY pos)
		{
			this.name=name;
			this.pos=pos;
		}
		public OtherPlayer(String name,PosXY pos,int level)
		{
			this.name=name;
			this.pos=pos;
			this.level=level;
		}
		public OtherPlayer(String name,PosXY pos,String chipset)
		{
			this.name=name;
			this.pos=pos;
			this.chipset=chipset;
		}
		public OtherPlayer(String name,PosXY pos,String chipset,int level, int HP)
		{
			this.name=name;
			this.pos=pos;
			this.chipset=chipset;
			this.level=level;
			this.HP=HP;
		}
		public void setPos(PosXY pp)
		{
			pos=pp;
			sprite.versx=pp.X;
			sprite.versy=pp.Y;
		}
	}
	
	private void CreatePlayer(OtherPlayer pl)
	{   
		//if(pl.name.equals(PlayerInfo.Name))
    	if (VerifieSpriteVisible(pl.pos.X,pl.pos.X))
    	{
    		pl.sprite=new TMChar(Self,pl.name,pl.chipset,null,null,null,0,0,24,32,pl.pos.X,pl.pos.Y,0,0,pl.pos.D,0,0,Sprite.idSprite.idPLAYER,0,1,false);
    		pl.sprite.pl=pl;
    		SpriteE.AddSprite(pl.sprite);
    	}
    	int idx= ListePlayer.size();
    	pl.idx=idx;
    	ListePlayer.put(pl.name, pl);
    	ListeJMap.put(pl.name, pl.pos);
	}
	
	public void updatePlayers()
	{
		PosXY tmpP;
		String psd;
		Iterator<String> it = ListeJMap.keySet().iterator();
		Iterator<PosXY> it2= ListeJMap.values().iterator();
		while(it.hasNext())
		{
			tmpP=it2.next();
			psd=it.next();
			//System.out.println(psd+" en "+tmpP.X+","+tmpP.Y+":"+tmpP.D);
			if(!psdUsedOld.containsKey(psd))
				CreatePlayer(new OtherPlayer(psd, tmpP, "Chipset/guerrier01.png"));
			else if(ListePlayer.containsKey(psd))
			{
				OtherPlayer xM = ListePlayer.get(psd);
				xM.setPos(tmpP);
			}
		}
	}



	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void keyPressed(KeyEvent e) 
	{
		if ( e.getKeyChar() == KeyEvent.VK_ENTER ) 
		{
			if(Global.typing) Global.stopTyping();
			else if (!IsInvActive) Global.startTyping();
		}
		if(e.getKeyChar() == KeyEvent.VK_ESCAPE && Global.typing)
		{
				Global.stopTyping();
				Global.tMsg="";
		}
		if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE)
			if(Global.typing && Global.tMsg.length() > 0) 
				Global.tMsg=Global.tMsg.substring(0,Global.tMsg.length()-1);
			
	}
	@Override
	public void keyReleased(KeyEvent e) 
	{
		if(!Global.typing) return;
		Global.keyT=e.getKeyChar();
		if ( Global.keyT != KeyEvent.CHAR_UNDEFINED && Global.keyT != KeyEvent.VK_BACK_SPACE && Global.keyT != KeyEvent.VK_ENTER) 
	        Global.tMsg = Global.tMsg + Global.keyT;
	    e.consume();
	}
	
}