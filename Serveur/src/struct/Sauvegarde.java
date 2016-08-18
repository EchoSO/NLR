package struct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

public class Sauvegarde implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String Name;
	public String SoundAttaque, SoundWound, SoundConcentration;
	public Projet.ClasseJoueur Classe;
	public Properties Stats;
	public int Vie, VieMax;
	public int CurrentMag, MagMax;
	public int Arme, Bouclier, Armure, Casque;
	public int Gold;
	public int Lvl, LvlPoint;
	public int PrevXP, CurrentXP, NextXP;
	public short Inventaire[][];
	public short Raccourcis[];
	public ArrayList<String> Menu;
	public Properties Variable;
	public short OwnSpell[];
	public short Position;
	public String CurrentMap;
	public String Chipset;
	public boolean BloqueChangeSkin,BloqueAttaque,BloqueDefense,BloqueMagie;
	public short CentreX, CentreY;
	public short pmapx, pmapy;
	public short ResX, ResY;
	public String ResCarte;
	public Sauvegarde()
	{
		Inventaire=new short[100][2];
		Raccourcis=new short[10];
		OwnSpell=new short[100];
		Variable=new Properties();
		Stats=new Properties();
		Menu=new ArrayList<String>();
		BloqueChangeSkin=false;
		BloqueAttaque=false;
		BloqueDefense=false;
		BloqueMagie=false;
	}
	public Sauvegarde clone()
	{
		Sauvegarde copie=new Sauvegarde();
		copie.Name=this.Name;
		copie.SoundAttaque=this.SoundAttaque;
		copie.SoundWound=this.SoundWound;
		copie.SoundConcentration=this.SoundConcentration;
		copie.Classe=this.Classe;
		Enumeration<Object> em = this.Stats.keys();
	      while(em.hasMoreElements()){
	        String str = (String)em.nextElement();
	        copie.Stats.setProperty(str, this.Stats.getProperty(str));
	      }
		copie.Vie=this.Vie;
		copie.VieMax=this.VieMax;
		copie.CurrentMag=this.CurrentMag;
		copie.MagMax=this.MagMax;
		copie.Arme=this.Arme;
		copie.Bouclier=this.Bouclier;
		copie.Armure=this.Armure;
		copie.Casque=this.Casque;
		copie.Gold=this.Gold;
		copie.Lvl=this.Lvl;
		copie.LvlPoint=this.LvlPoint;
		copie.PrevXP=this.PrevXP;
		copie.CurrentXP=this.CurrentXP;
		copie.NextXP=this.NextXP;
		copie.Inventaire=this.Inventaire.clone();
		em = this.Variable.keys();
	      while(em.hasMoreElements()){
	        String str = (String)em.nextElement();
	        copie.Variable.setProperty(str, this.Variable.getProperty(str));
	      }
	    for(int i=0;i<Menu.size();i++)
	    	copie.Menu.add(this.Menu.get(i));
		copie.OwnSpell=this.OwnSpell.clone();
		copie.Position=this.Position;
		copie.CurrentMap=this.CurrentMap;
		copie.Chipset=this.Chipset;
		copie.BloqueChangeSkin=this.BloqueChangeSkin;
		copie.BloqueAttaque=this.BloqueAttaque;
		copie.BloqueDefense=this.BloqueDefense;
		copie.BloqueMagie=this.BloqueMagie;
		copie.CentreX=this.CentreX;
		copie.CentreY=this.CentreY;
		copie.pmapx=this.pmapx;
		copie.pmapy=this.pmapy;
		copie.ResX=this.ResX;
		copie.ResY=this.ResY;
		copie.ResCarte=this.ResCarte;		
		return copie;
	}
}