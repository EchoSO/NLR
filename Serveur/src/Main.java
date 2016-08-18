import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.security.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import struct.Projet;
import struct.Sauvegarde;

//TODO @ALPHA Création compte, connexion, déplacement, dialogue
//TODO @ALPHA commandes, maps, attaques, monstres, events, sauvegarde
//TODO @ALPHA sécurisation,logs
//TODO @ALPHA Fonction de purge des Vector<> (copie, réattribution numéros) toutes les heures
//TODO @ALPHA Reboot
//TODO @ALPHA2 Cryptage
//TODO @ALPHA lire les commandes dans un fichier toutes les secondes

public class Main
{
	private Vector<PrintWriter> clients = new Vector<>(); // Contient les flux de sortie vers les clients
	private Vector<PrintWriter> launch = new Vector<>(); // Contient les flux de sortie vers les launchers
	public static Vector<ClientThread> clients2 = new Vector<>(); // Contient les clients
	private short nombreclients = 0;
	public static Main serveur;
	public static String Projet;
	public static Projet general;
	public static LinkedList<String> mapList = new LinkedList<>();
	public static Object[] banHWO;
	public static String[] banHW;
	public static boolean started;

	
/**################################################################**/
	  public static void main(String args[])
/**################################################################
 * Lance le serveur**/
	  {
		  
		 Projet="test";
		 loadPrj(Projet);
	     serveur = new Main(); 
	     banHWO=ReadBanFile("ban.txt").toArray();
	     banHW = new String[banHWO.length];
	     for(int i=0;i<banHWO.length;i++)
	    	 banHW[i]=(String)banHWO[i];
	     started=true;
	    
	    try 
	    {
	      Integer port;
	      
	      if(args.length <= 0)
	    	  port = 4001;
	      else
	    	  port = new Integer(args[0]);
	      
	      new Commandes(serveur);//Ecoute les commandes dans la console
	      
	      //Crée un serveur 
	     ServerSocket ss=new ServerSocket(port);
	      
	      System.out.println("---------------\nServeur de Neo Late Registration\nDemarrage du serveur sur le port " + port);
	     
	      Thread Launcher = new Thread(new LauncherThread());
	      Launcher.start();//Lance le thread d'acceptation des launchers
	      Thread posThread= new Thread(serveur.new PosT());
	      posThread.start();
	      
	      for(;;)
	      {//Attente en boucle de connexion
	         try{new ClientThread(ss.accept());}catch(Exception e){System.out.println("Client error");} 
	         System.out.print(">>>");//Si un client se connecte, un nouveau thread ClientThread est lancé !
	         if(!posThread.isAlive() || posThread.isInterrupted())
	         {
	        	 System.out.println(Arrays.toString(posThread.getStackTrace()));
	         }
	      }
	    }
	    catch (Exception e) {System.out.println("ERREUR LANCEMENT ÉCOUTE SUR LE PORT 4001/2 :"+e.getMessage());}
	  }    

	  
/**################################################################**/
	  public void envoieglobal(String message)
/**################################################################
 * Envoie un message à tous les clients non null**/
	  {
	    PrintWriter out;
	    for (int i = 0; i < clients.size(); i++)
	    {
	      out = (PrintWriter) clients.elementAt(i);
	      if (out != null)
	      {
	        out.print(message);
	        out.flush();
	      }
	    }
	  }
  	
/**################################################################**/
  	public void envoieGlobalMap(String message,String Map)
/**################################################################
 * Envoie un message à tous les clients sur la Map**/
	  {
	    PrintWriter out;
	    ClientThread c;
	    for (int i = 0; i < clients.size(); i++)
	    {
	    	try
	    	{
	    		out = clients.elementAt(i);
	  	      c = clients2.elementAt(i);
	  	      if (out != null && c!=null && !c.socket.isClosed() && c.connected && c.Map.equals(Map) && !c.lock)
	  	      {
	  	        out.print(message);
	  	        out.flush();
	  	      }
	    	}catch(Exception e){e.printStackTrace();}
	      
	    }
	  }
	  
/**################################################################**/
	  synchronized public void envoyer (String message, int numeroclient)
/**################################################################
 * Envoie un message au client correspondant**/
	  {
		  PrintWriter out = (PrintWriter) clients.elementAt(numeroclient);
		  out.print(message);
		  out.flush();
	  }
	
/**################################################################**/
	  synchronized public void envoyerL (String message, int numeroLauncher)
/**################################################################
 * Envoie un message au Launcher correspondant**/
	  {
		  PrintWriter out = (PrintWriter) launch.elementAt(numeroLauncher);
		  out.print(message);
		  out.flush();
	  }
	  
/**################################################################**/
	  public void supprimerclient(int i)
/**################################################################
 * Supprime un client**/
	  { 
		nombreclients--;
		/*try{
	    if (clients.elementAt(i) != null)
	      clients.removeElementAt(i);
		}catch(ArrayIndexOutOfBoundsException e){System.out.println("Error deleting client(Too fast - Port scan?)");}*/
	  }
	
/**################################################################**/
	  synchronized public int ajouterclient(PrintWriter out,ClientThread out2)
/**################################################################
 * Ajoute un client**/
	  {
		nombreclients++;
		
	    clients.addElement(out);
	    clients2.addElement(out2);
		
	    return clients.size() - 1;
	  }
	  
/**################################################################**/
	  synchronized public int ajouterLauncher(PrintWriter out)
/**################################################################
 * Ajoute un launcher**/
	  {
	    launch.addElement(out);
		
	    return launch.size() - 1;
	  }
	  
/**################################################################**/
	  public int getnombreclients()
/**################################################################
 * Renvoie le nombre de clients connectés**/
	  {
	    return nombreclients;
	  }
	  
	  /*
	  public int getnombrejoueurs()
	  //Renvoie le nombre de joueurs connectés
	  {
		return nombrejoueurs;
	  }*/
	  
	  
/**################################################################**/
	  public static String md5(String chaine)
/**################################################################
* Renvoie le MD5 de chaine**/
	  {
		  byte[] defaultBytes = chaine.getBytes();
		  try{
		  	MessageDigest algorithm = MessageDigest.getInstance("MD5");
		  	algorithm.reset();
		  	algorithm.update(defaultBytes);
		  	byte messageDigest[] = algorithm.digest();
		    //System.out.println(messageDigest.length+"\n"+Arrays.toString(messageDigest));
		  	String hexString="";
		  	String t="";
		  	for (int i=0;i<messageDigest.length;i++) 
		  	{
		  		t= Integer.toHexString(messageDigest[i]);
		  		if (t.length() == 1)
		        {
		                hexString+="0";
		                hexString+=t.charAt(t.length() - 1);
		        }
		        else
		  		hexString+=t.substring(t.length()-2);
		  	}
		  	chaine=hexString+"";
		  }catch(NoSuchAlgorithmException nsae){}
		  return chaine;

	  }
	  
/**################################################################**/
	  public static void CreateAccount(String login, String password, String LoginFile, String KeyFile)
/**################################################################
 * Crée le compte login**/
	  {
		  try
		  {
			  File key = new File(KeyFile);
			  key.createNewFile();
			  FileWriter Writer = null;
			  Writer = new FileWriter(LoginFile);
			  BufferedWriter out = new BufferedWriter(Writer);
			  out.write(password + "\n");
			  out.write(login+"\n");
			  out.write("0\n");
			  
			  out.close();		  
		  }catch(IOException e){e.printStackTrace();}
	  }
	  
	  
/**################################################################**/
	  public static String[] ReadLoginFile(String LoginFile)
/**################################################################
 * Lis le fichier de login LoginFile et renvoie un string, une ligne/case**/
	  {
		String[] valeurs = new String[3];
		try
		{
			BufferedReader b=null;
			b = new BufferedReader(new FileReader(LoginFile));
			valeurs[0]=b.readLine();
			valeurs[1]=b.readLine();
			valeurs[2]=b.readLine();
		}catch(IOException e){e.printStackTrace();}
		return valeurs;
	  }
	  
/**################################################################**/
	  public static LinkedList<String> ReadBanFile(String BanFile)
/**################################################################
 * Lis le fichier de login LoginFile et renvoie un string, une ligne/case**/
	  {
		LinkedList<String> ar = new LinkedList<>();
		try
		{
			BufferedReader b=null;
			b = new BufferedReader(new FileReader(BanFile));
			do
			{
				ar.addLast(b.readLine());
			}while(ar.getLast() !=null);
			ar.removeLast();
			
		}catch(IOException e){e.printStackTrace();}
		return ar;
	  }
	  
/**################################################################**/
	  public static String generateKey(String login , String password)
/**################################################################
 * Génère une clé unique pour le login, le password et la date**/
	  {
		  java.util.Date x = new java.util.Date();
		  long timestamp =x.getTime();
		  return Main.md5("|55" + login + "|66" + password + "|77" + timestamp + "|88");
	  }
	  
/**################################################################**/
	  public static void writeKeyFile(String Key, String KeyFile)
/**################################################################
 * Ecrit Key dans KeyFile**/
	  {
		  FileWriter Writer = null;
		  try
		  {
			  Writer = new FileWriter(KeyFile);
			  BufferedWriter out = new BufferedWriter(Writer);
			  out.write(Key);
			  out.close();		  
		  }catch(IOException e){e.printStackTrace();}
	  }
	  
/**################################################################**/
	  public static String ReadKey(String KeyFile) 
/**################################################################
 * Lis la clé dans KeyFile et la renvoie**/
	  {
		String valeur="";
		try
		{
			BufferedReader b=null;
			b = new BufferedReader(new FileReader(KeyFile));
			valeur=b.readLine();
		}catch(IOException e){e.printStackTrace();}
		return valeur;
	  }
	  
	  
	  public static void loadPrj(String NomCarte)
	  {
		  try
		  {
			  ObjectInputStream in=new ObjectInputStream(new FileInputStream(System.getProperty("user.dir")+"/"+NomCarte+"/"+NomCarte+".prj"));
			  in.read();
			  general=(Projet)in.readObject();
		  } catch(IOException | ClassNotFoundException e){System.err.println(e.getMessage());}
		  
	  }
	  
	  public static Sauvegarde Chargement(String nomfic)
		{
			try {
				//lecture de l'objet
				ObjectInputStream in=new ObjectInputStream(new FileInputStream(nomfic));
				try
				{
					Sauvegarde PlayerInfo=(struct.Sauvegarde)in.readObject();
					return PlayerInfo;
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
			return null;
		}
	  
	  public String toucher(int X, int Y, int D,String Map, String Pseudo)
	  {
		  String rtn="tp:";
		  String tmp="";
		  int dX,dY;
		  for(int i=0;i<clients2.size();i++)
		  {
			  ClientThread c=clients2.get(i);
			  if(c!=null && !c.socket.isClosed() && c.connected && c.Map.equals(Map) && !c.Pseudo.equals(Pseudo))
			  {
				  dX=c.X-X;
				  dY=c.Y-Y;
				  if(dX==0 && dY==0)
					  tmp=c.Pseudo;
				  else if((Math.abs(dX)) <=1 && (Math.abs(dY)) <=1)
				  {
					  if(dX>0 && D==3) tmp=c.Pseudo;
					  if(dX<0 && D==1) tmp=c.Pseudo;
					  if(dY>0 && D==0) tmp=c.Pseudo;
					  if(dY<0 && D==2) tmp=c.Pseudo;
				  }
			  }
		  }
		  //TODO degats
		  System.out.println(rtn+tmp);
		  return rtn+tmp;
	  }
	  
/**################################################################**/
	  public String Positions(String Nom,String Map) 
/**################################################################
 * Renvoie position des autres joueurs**/
	  {
		  String rtn="pp:";
		  for(int i=0;i<clients2.size();i++)
		  {
			  ClientThread c=clients2.get(i);
			  if(c!=null && !c.socket.isClosed() && c.connected && c.Map.equals(Map))
			  {
				  rtn+=c.Pseudo+"|"+c.X+"|"+c.Y+"|"+c.D+"~";
			  }
		  }
		  return rtn;
	  }
	  
	  class PosT implements Runnable
	  {
		public void run()
		{
			String msp="";
			String tmp="";
			while(started)
			{
				try 
				{
					if(!mapList.isEmpty())
					{
						tmp=mapList.getFirst();
						msp=Positions("", tmp)+"\n";
						envoieGlobalMap(msp, tmp);
						Thread.sleep(250/mapList.size());
						mapList.removeFirst();
					}
					else
					{
						//System.out.print("LV");
						Thread.sleep(250);
					}
					
				} catch (Exception e) 
				{
					e.printStackTrace();
					try {
						Thread.sleep(250);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
			System.out.println("CRASH BOUCLE");
		  new Thread(new PosT()).start();
		  
		}
	  }
	  
	  
}
