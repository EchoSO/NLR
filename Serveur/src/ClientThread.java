import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;

import struct.Projet.Carte;
import struct.Sauvegarde;
import struct.Projet.Depart;

class ClientThread  implements Runnable 
{
	
  private Thread thread;
  public Socket socket;
  private PrintWriter sortie;
  private BufferedReader entree;
  private int numeroclient = 0;
  public String Pseudo = "<NotIdentified>";
  private String Pass = "<NotIdentified>";
  private int Admin = 0;
  public String ipclient=null;
  int X;
  int Y;
  int D;
  public String Map="1";
  public boolean connected=false;
  private ObjectOutputStream oos;
  public Sauvegarde PlayerInfo;
  public Carte CurrentMap;
  public boolean lock=false;
  
  public ClientThread(Socket socket2) throws Exception
  {
    socket = socket2;
    
    try
    {
      //initialise les socket d'entrée et de sortie
      sortie = new PrintWriter(socket.getOutputStream());
      entree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      oos=new ObjectOutputStream(socket.getOutputStream());
      numeroclient = Main.serveur.ajouterclient(sortie,this);
      
      ipclient = socket.getInetAddress().toString().replace("/", "");
    }
    catch (IOException e){e.printStackTrace(); return;}

    thread = new Thread(this);
    thread.start();
  }

  public void run()
  {

    System.out.println("Connexion client : " + ipclient);
    
    try
    {
	  String message = "";

      while((message = entree.readLine()) != null)
      {
    	  if(message.split(":")[0].equals("c"))
    	  {//Si une /commande
    		  message=message.substring(2);
	    	  System.out.println(message);
	    	  playerCommands(message);
    	  }
    	  else if(message.equals("")){}
    	  else 
    	  {
    		  if(!serverCommands(message))
    		  {
    			  Main.serveur.envoyer("Bad command/Hack/Cheat. Logged. You'll be banned next time\n", numeroclient);
    			  break;
    		  }
    	  }
      }
    }catch (Exception e){}
    finally 
    {
      try 
      {
        System.out.println(">>>Client offline :" + ipclient + (connected ? " : "+Pseudo : ""));
        Main.serveur.supprimerclient(numeroclient);
        socket.close();
      }
      catch (IOException e){}
    }
  }
  
  private void playerTalk(String message) 
  {
	message="t:"+Pseudo + "|" + message+"\n";
	Main.serveur.envoieGlobalMap(message, Map);
  }

private void playerPosition(String message) throws IOException 
  {//   pX|Y|D
	  try
	  {
		  X = Integer.parseInt(message.split("\\|")[0]);
		  Y = Integer.parseInt(message.split("\\|")[1]);
		  D = Integer.parseInt(message.split("\\|")[2]);
		  
		  if(!Main.serveur.mapList.contains(Map)) Main.serveur.mapList.addLast(Map);
	  }catch(Exception e)
	  {
		  Main.serveur.envoyer("statmsg:Bad command/Hack/Cheat attempt. You have been logged.\n", numeroclient);
		  socket.close();
	  }
  }

/**################################################################**/
private void connection(String message) throws IOException
/**################################################################**/
  {/**   \connect|pseudo|pass|key
  Essaye de se connecter avec pseudo pass key**/
	
		String login = message.split("\\|")[1].trim();//Sépare login/pass/key
		String password = Engine64.crypt(Main.md5(message.split("\\|")[2].trim()));
		String key = message.split("\\|")[3].trim();
		
		File KeyFile= new File("Players" + File.separator + Main.md5(login.toUpperCase()) + ".key");
		File LoginFile= new File("Players" + File.separator + Main.md5(login.toUpperCase()) + ".connect");
		
		if(KeyFile.exists() && KeyFile.canRead() && KeyFile.isFile())
		{//Si il y a le .key
			
	  			String key2=Main.ReadKey(KeyFile.toString());
	  			if(key.equals(key2))
	  			{//Si la clé correspond
	  	  			if(LoginFile.exists() && LoginFile.canRead() && LoginFile.isFile())
	      	  		{//Si il y a un fichier de login
	      	  			String[] passcheck= Main.ReadLoginFile(LoginFile.toString());
	      	  			if(passcheck[0].equals(password) && passcheck[1].equals(login.toUpperCase()))
	      	  			{//Si le pass correspond, OK
	      	  				Main.serveur.envoyer("statmsg:Connected\n", numeroclient);
	      	  				Pseudo=login;
	      	  				Pass=message.split("\\|")[2].trim();
	      	  				connected=true;
	      	  				Main.writeKeyFile("lulz" + numeroclient + Main.serveur.getnombreclients(), KeyFile.toString());
	      	  				//Ecrit un keyFile fictif pour empêcher la reco avec la même clé.
	      	  				System.out.println(ipclient+" = "+Pseudo);
	      	  				
	      	  				oos.writeObject(Main.general);
	      	  				
	      	  				initPlayerInfo();
	      	  				oos.writeObject(PlayerInfo);
	      	  				
	      	  				CurrentMap=Main.general.getCarteByName(PlayerInfo.CurrentMap);
	      	  				oos.writeObject(CurrentMap);
	      	  				
	      	  				oos.writeObject(Main.general.getBlocageByName(CurrentMap.Chipset).blocage);
	      	  				
	      	  			}
	      	  			else
	      	  			{//Si le pass ne correspond pas, kick
	      	  				Main.serveur.envoyer("statmsg:Bad password\n", numeroclient);
	      	  				System.out.println(ipclient +" : "+login+" : Bad pass (client)");
	      	  				socket.close();
	      	  			}
	      	  		}
	      	  		else
	      	  		{//Si pas de fichier de login, le compte n'existe pas, kick
	      	  			Main.serveur.envoyer("statmsg:No player '" + login + "' on server. Create a new account.\n", numeroclient);
	      	  			socket.close();
	      	  		}
	  			}
	  			else
	  			{//Si la clé le correspond pas, kick
	  				Main.serveur.envoyer("statmsg:Bad key. Please connect using Launcher.\n", numeroclient);
	  				System.out.println(ipclient +" : "+login+" : Bad key");
	  				socket.close();
	  			}
			
		}
		else
		{//Pas de .key, on a pas utilisé le launcher
			Main.serveur.envoyer("statmsg:No Launcher Key for '" + login + "' on this server. Please connect using the launcher..\n", numeroclient);
			socket.close();
		}
  }
  
  private void chmap(String message) throws IOException
  {//  \chmap|NomDeLaMap|X|Y|CacheMap|CacheChipset
	  	String map = message.split("\\|")[1].trim();
		X = Integer.parseInt(message.split("\\|")[2].trim());
		Y = Integer.parseInt(message.split("\\|")[3].trim());
		boolean InCacheM = Boolean.getBoolean(message.split("\\|")[4].trim());
		boolean InCacheC = Boolean.getBoolean(message.split("\\|")[5].trim());
		if(!InCacheM)
		{
			map(map);
		}
		if(!InCacheC)
		{
			sendChipset(getChipset(map));
		}
  }
  
  private void sendChipset(String chipset)
  {
	  
  }
  
  private String getChipset(String map)
  {
	return map;
  }
  
  private void map(String map)
  {
	  Map=map;
	  String[] taille = tailleMap(map);
	  if(taille==null){Main.serveur.envoyer("map:Not OK\n", numeroclient);return;}
	  System.out.println("Envoi");
	  Main.serveur.envoyer("map:OK\n", numeroclient);
	  Main.serveur.envoyer("maplarg:"+taille[0]+"\n", numeroclient);
	  Main.serveur.envoyer("maphaut:"+taille[1]+"\n", numeroclient);
	  Main.serveur.envoyer("mapchip:"+taille[2]+"\n", numeroclient);
	  File laMap=new File(Main.Projet+File.separator+"Maps"+File.separator+map+".map");
	  String[] MapCont=fileRead(laMap);
	  Main.serveur.envoyer("mapcont:" + Arrays.toString(MapCont).replace(" ", "") + "\n",numeroclient);
	  //System.out.println("Envoyé : " + Arrays.toString(MapCont));
  }
  
  private String[] tailleMap(String map)
  {
	  String mapprj=null;
		BufferedInputStream aFile=null;
		int pos=0;//Position
		String x="";
		int y=0;int y1;
		char y2=0;//Diverses variables pour lecture et résultats
		int y3;int y4;String x2="";
		
		mapprj = Main.Projet + File.separator + Main.Projet + ".prj";
		int NombreMaps=0;
		try
		{
			File Prj= new File(mapprj);
			NombreMaps=(int)(Prj.length()/778);
			System.out.println("Chargement de '" + Main.Projet + "' comportant "+NombreMaps+" maps.");
			aFile = new BufferedInputStream(new FileInputStream(mapprj));//Ouverture en écriture
		}catch(FileNotFoundException e){e.printStackTrace();}
		
		try{
			for(int j=0;j<NombreMaps;j++,pos=0,x="",y=0,y2=0)
			{
				y=aFile.read(); pos++; 
				for(int i=0;i<y;i++)//Lis la taille du nom de la map puis le nom de la map 
				{
					y2=(char)aFile.read(); pos++;x=x+y2;
				}
				if(x.equals(map))
				{
					System.out.println(x + " = " + map);
					while( pos<155){aFile.skip(1); pos++;}
					y1=aFile.read(); pos++;
					for(int i=0;i<y1;i++)
					{
						y2=(char)aFile.read(); pos++;x2=x2+y2;
					}
					while( pos<206){aFile.skip(1); pos++;}
					y3=aFile.read(); pos++;//Lis et stocke la largeur de la map
					aFile.read();pos++;
					y4=aFile.read();
					String[] taille=new String[3];
					taille[0]=y3 +"";taille[1]=y4+ "";taille[2]=x2;
					System.out.println("Largeur:" + y3 +" longeur: " + y4);
					return taille;
				}
				
				while( pos<=777){aFile.skip(1); pos++;}//<=777
			}
			
			
		}catch(IOException e){e.printStackTrace();}
		return null;
  }
  
  private void playerCommands(String message) throws Exception
  {
	  	if(message.equals("/count"))
	  	{
	  		String nombresjoueursconnectes = String.valueOf(Main.serveur.getnombreclients());
	  		//paquet = new Paquet(message);//fluxdentree.read(charCur, 13, 1);//String idserveur = Character.toString(charCur[0]);//paquet.Add(idserveur);//paquet.Add("0");//System.out.println(idserveur);//paquet.Add(nombresjoueursconnectes);//paquet.Add("50");
	  		Main.serveur.envoyer("statmsg:"+nombresjoueursconnectes+"\n", numeroclient);
	  	}
	  	else if(message.equals("/ip"))
	  	{
	  		/*Paquet paquet = new Paquet("plainmsg");paquet.Add("Votre compte a été créé!");paquet.Add("1");*/
	  		Main.serveur.envoyer("statmsg:"+ipclient+"\n", numeroclient);
	  	}
	  	else if(message.equals("/ipserv"))
	  	{
	  		java.net.InetAddress ip = java.net.InetAddress.getLocalHost();
	  		Main.serveur.envoyer("statmsg:"+ip.getHostAddress()+"\n", numeroclient);
	  	}
	  	else if(message.split(" ")[0].equals("/admin"))
	  	{
	  		if(Admin ==0){Main.serveur.envoyer("statmsg:GM Only.\n",numeroclient);}
	  		else
	  		{
		  		message=message.replace("/admin ", "");
		  		Main.serveur.envoieglobal("statmsg:ADMIN:"+message+"\n");
	  		}
	  	}
	  	else if(message.equals("/exit"))
	  	{
	  		socket.close();
	  	}
	  	else if(message.equals("/whoami"))
	  	{
	  		Main.serveur.envoyer("statmsg:Votre pseudo est : " + Pseudo+"\n", numeroclient);
	  	}
	  	else if(message.equals("/pass"))
	  	{
	  		Main.serveur.envoyer("statmsg:Votre mot de passe est : " + Pass+"\n", numeroclient);
	  	}
	  	else if(message.equals("/reboot"))
	  	{
	  		if(Admin ==0){Main.serveur.envoyer("statmsg:GM Only.\n",numeroclient);}
	  		else
	  		{
	  			Main.serveur.envoyer("statmsg:Rebooting server...\n", numeroclient);
	  		}
	  	}
	  	else if(message.equals("/fermer"))
	  	{
	  		if(Admin == 0){Main.serveur.envoyer("statmsg:GM Only.\n",numeroclient);}
	  		else
	  		{
	  			Main.serveur.envoyer("statmsg:Closing server...\n", numeroclient);
	  			System.exit(0);
	  		}
	  	} 
	  	else if(message.equals("/listcon"))
	  	{
	  		String env="statmsg:";
	  		for(int i = 0; i<Main.clients2.size(); i++)
    		{
    			ClientThread ct=Main.clients2.get(i);
    			if (ct.socket!=null && ct.socket.isConnected() && !ct.socket.isClosed())
    				env+=ct.Pseudo + " : "+ct.ipclient+"~";
    		}
	  		Main.serveur.envoyer(env+"\n", numeroclient);
	  	}
  }
  
  /**################################################################**/
  private boolean serverCommands(String message) throws IOException
  /**################################################################
   * Gère les commandes serveur**/
  {
	  if(!message.startsWith("p")) System.out.println(message);
	  	if(message.startsWith("p"))
	  	{
	  		message=message.substring(1);
	  		playerPosition(message);
	  		return true;
	  	}
	  	else if (message.startsWith("t"))
	  	{
	  		message=message.substring(2);
	  		if(message.startsWith("/"))
				try 
	  			{
					playerCommands(message);
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
			else playerTalk(message);
	  		return true;
	  	}
	  	else if (message.equals("a:"))
	  	{
	  		String resultat=Main.serveur.toucher(X,Y,D,Map,Pseudo);
	  		Main.serveur.envoyer(resultat+"\n", numeroclient);
	  		return true;
	  	}
	  	else if (message.equals("lock")) {lock=true; return true;}
	  	else if (message.equals("unlock")) {lock=false; return true;}
	  	else if (message.equals("dummy")) {Main.serveur.envoyer("dmy\n", numeroclient); return true;}
	  	else if (message.equals("\\map"))
	  	{
	  		oos.writeObject(CurrentMap);
	  		oos.flush();
	  		oos.writeObject(Main.general.getBlocageByName(CurrentMap.Chipset).blocage);
	  		oos.flush();	  		
	  		return true;
	  	}
	  	else if(message.equals("\\prj") && connected)
	  	{
	  		oos.writeObject(Main.general);
	  		oos.flush();
	  		return true;
	  	}
	  	else if(message.split("\\|").length == 4 && message.split("\\|")[0].equals("\\connect"))
		{
			connection(message);
			return true;
		}
		else if(message.split("\\|").length == 6 && message.split("\\|")[0].equals("\\chmap"))
		{
			chmap(message);
		}
		else if(message.split("\\|").length == 2 && message.split("\\|")[0].equals("\\dlmap"))
		{
			map(message.split("\\|")[1]);
		}
		else
		{
  		  	Main.serveur.envoyer("statmsg:Bad command/Hack/Cheat attempt. You have been logged.\n", numeroclient);
  		  	socket.close();
  	  	}
	  	return false;
  }
  
  
  public static String[] fileRead(File file) 
  {
		try 
		{
			FileReader in = new FileReader(file);
			BufferedReader readIn = new BufferedReader(in);
			
			ArrayList<String> list = new ArrayList<>(50);
			String data;
			
			// reading text
			while ((data = readIn.readLine()) != null) {
				list.add(data);
			}
			
			readIn.close();
			
			return compactStrings((String[]) list
			        .toArray(new String[0]));
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String[] compactStrings(String[] s) {
		String[] result = new String[s.length];
		int offset = 0;
		for (int i = 0; i < s.length; ++i) {
			offset += s[i].length();
		}
		
		// can't use StringBuffer due to how it manages capacity
		char[] allchars = new char[offset];
		
		offset = 0;
		for (int i = 0; i < s.length; ++i) {
			s[i].getChars(0, s[i].length(), allchars, offset);
			offset += s[i].length();
		}
		
		String allstrings = new String(allchars);
		
		offset = 0;
		for (int i = 0; i < s.length; ++i) {
			result[i] = allstrings.substring(offset, offset += s[i].length());
		}
		
		return result;
	}
	
	public void initPlayerInfo()
	{
		if (new File(System.getProperty("user.dir")+"/"+Pseudo.toLowerCase()+".sav").exists())
			PlayerInfo=Main.Chargement(System.getProperty("user.dir")+"/"+Pseudo.toLowerCase()+".sav");
		
		if (PlayerInfo==null)
		{
			PlayerInfo=new struct.Sauvegarde();
			Depart depart=Main.general.getDepart();
			if (depart.Carte.equals(""))
			{
				PlayerInfo.pmapx=0;
				PlayerInfo.pmapy=0;
				PlayerInfo.CurrentMap=Main.general.getCartes().get(0).Name;
			}
			else
			{
				PlayerInfo.pmapx=(short) (depart.X * 2);
				PlayerInfo.pmapy=(short) (depart.Y * 2);
				X=PlayerInfo.pmapx;
				Y=PlayerInfo.pmapy;
				PlayerInfo.CurrentMap=depart.Carte;
			}
			depart=Main.general.getMort();
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
			if (Main.general.getCourbeXP().size()>0)
				PlayerInfo.NextXP=Main.general.getCourbeXP().get(0);
			PlayerInfo.Menu.add("Inventaire");
			PlayerInfo.Menu.add("Options");
			PlayerInfo.Menu.add("Quitter");
			/*if (general.getStyleProjet()==1)
			{
				PlayerInfo.Menu.add(MenuPossibles.get(3));
				PlayerInfo.Menu.add(MenuPossibles.get(4));			
			}*/
			PlayerInfo.Classe=null;
			PlayerInfo.Lvl=1;
		}
	}
}
