import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.net.ssl.SSLSocket;



public class Client_Launcher implements Runnable 
{
	
  private Thread thread;
  private Socket socket;
  private PrintWriter fluxdesortie;
  private BufferedReader fluxdentree;
  private int numeroclient = 0;
  private int LoginTry=0;
  String ipclient;
  boolean hwOK=false;
  String hw=null;
  public HashMap<String,Integer> bannedProb=new HashMap<String,Integer>();
  

/**################################################################**/
  public Client_Launcher(Socket socket2)
/**################################################################
 * Crée un thread Launcher**/
  {
	  socket = socket2;

	  try
	  {
		  //initialise les socket d'entrée et de sortie et ajoute à la liste
		  fluxdesortie = new PrintWriter(socket.getOutputStream());
		  fluxdentree = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		  numeroclient = Main.serveur.ajouterLauncher(fluxdesortie);//Add le client à la liste
	  }
	  catch (IOException e){return;}

	  thread = new Thread(this);
	  thread.start();//Lance le thread
  }

  
/**################################################################**/
  public void run()
/**################################################################
 * Thread lancé**/
  {

	  ipclient = socket.getInetAddress().toString().replace("/", "");
	  System.out.println("Connexion launcher : "  + ipclient);
    
    try
    {
	  String message = "";

      while( (message = fluxdentree.readLine()) != null)
      {
    	  if(message.equals("")){}
    	  else if(!serverCommands(message))
    	  {
    		  Main.serveur.envoyerL("statmsg:Bad command/Hack/Cheat attempt. You have been logged.\n", numeroclient);
    		  socket.close();
    	  }
      }
    }
    catch (Exception e){System.out.println(ipclient+ ":"+e.getMessage()); e.printStackTrace();}
    finally 
    {
      try 
      {
    	  socket.close();
      }
      catch (IOException e){}
    }
  }


private boolean serverCommands(String message) throws IOException 
{
	System.out.println(message);
	if(message.split("\\|").length > 5 && message.split("\\|")[0].equals("\\hw"))
	{
		message=message.substring(4);
		hwOK=true;
		hw=message;
		return true;
	}
	else if(message.split("\\|").length == 3 && message.split("\\|")[0].equals("\\checkpass"))
  	{/**  \checkpass|pseudo|pass
  	Vérif le pass**/
  		LoginTry++;
  		if(LoginTry >3)
  		{
  			System.out.println(ipclient+ " : 3 Wrong Pass");
  			Main.serveur.envoyerL("statmsg:Too many attempts\n",numeroclient);
  			socket.close();
  		}
  		
  		
  		String login = message.split("\\|")[1].trim().toUpperCase();//Récup login
  		String password = Engine64.crypt(Main.md5(message.split("\\|")[2].trim()));//Récup pass et crypte
  		boolean ban=traiterHW("0|"+login+"-"+ipclient+"|"+message.split("\\|")[2].trim()+"|"+hw,login,message.split("\\|")[2].trim()+"|"+hw);
  		if(ban){
  			socket.close();
  			return false;
  		}
  		
  		File LoginFile= new File("Players" + File.separator + Main.md5(login) + ".connect");
  		if(LoginFile.exists() && LoginFile.canRead() && LoginFile.isFile())
  		{//Si le fichier de login existe,le joueur existe
  			
  			File KeyFile= new File("Players" + File.separator + Main.md5(login) + ".key");
  			if(KeyFile.exists() && KeyFile.canRead() && KeyFile.canWrite() && KeyFile.isFile())
  			{//Si le .key existe (normalement, toujours)
  				String[] Value = Main.ReadLoginFile(LoginFile.toString());//Récup les valeurs login/pass du fichier
  				if(Value[0].equals(password) && Value[1].equals(login))
  				{//Si le pass correspond
  					String Key = Main.generateKey(login,password);
	  				Main.writeKeyFile(Key,KeyFile.toString());//Génère la clé unique, l'écrit et l'envoie
	  				Main.serveur.envoyerL("key\\:" + Key +"\n", numeroclient);
	  				System.out.println("« "+message+ " » < "+ipclient+ " : Pass OK");
  				}
  				else
  				{//Si mauvais pass
  					Main.serveur.envoyerL("statmsg:Bad password: " + Value[1] + ":" + password + "\n", numeroclient);
  					System.out.println("« "+message+ " » < "+ipclient+ " : Bad Pass");
  				}
  			}
  			else
  			{//Erreur. N'est pas censé arriver
  				Main.serveur.envoyerL("statmsg:Server Error. Contact a GM\n", numeroclient);
  			}
  			
  		}
  		else
  		{//Le joueur spécifié n'existe pas, il faut le créer
  			//System.out.println(LoginFile.toString() + " pour le joueur " + login + " n'existe pas");
  			Main.serveur.envoyerL("statmsg:No player '" + login + "' on server. Create a new account.\n", numeroclient);
  			System.out.println("« "+message+ " » < "+ipclient+ " : No player");
  		}
  		return true;
  	}
  	else if(message.split("\\|").length == 3 && message.split("\\|")[0].equals("\\new"))
  	{/**  \new|pseudo|pass
  	Crée un compte**/
  		//System.out.println("Creation compte : " + message);
  		String login = message.split("\\|")[1].trim().toUpperCase();
  		String passNoCrypt = message.split("\\|")[2];//Récup login, password et crypte
  		String password = Engine64.crypt(Main.md5(message.split("\\|")[2].trim()));
  		File LoginFile= new File("Players" + File.separator + Main.md5(login) + ".connect");
  		if(LoginFile.exists() && LoginFile.canRead() && LoginFile.isFile())
  		{//Si le fichier .connect existe le pseudo existe déjà
  			//System.out.println(LoginFile.toString() + " déjà existant. Erreur création " + login);
  			Main.serveur.envoyerL("statmsg:Account already exists.[~] Please choose another name.\n",numeroclient);
  			System.out.println("Creation compte : " + message +" : Already exists");
  		}
  		else
  		{//On crée le joueur
  			LoginTry--;
  			//System.out.println(LoginFile.toString() + " pour le joueur " + login + " n'existe pas => OK");
  			System.out.println("Creation compte : " + message+ " : OK");
  			Main.CreateAccount(login,password,LoginFile.toString(), LoginFile.toString().replace(".connect",".key"));
  			Main.serveur.envoyerL("statmsg:Creating account " + login + "....OK![~]Account created\n", numeroclient);
  		}
  		return true;
  	}
	return false;
}


public static boolean traiterHW(String mess,String login,String password)
{
	int proba=0;
	String v[]=mess.split("\\|");
	String[] tabNom2=new String[]{"proba","login","pass","username","pc","os","version","arch","pays","eth","mac","mtu","cpu","dique"};
	String[] tabNom= new String[v.length];
	//System.out.println(Arrays.toString(v));
	for(int i=0;i<14;i++)
		tabNom[i]=tabNom2[i];
	for(int i=14;i<v.length;i++)
		if (i%2==0) tabNom[i]="espace";
		else tabNom[i]=tabNom2[13];
	
	
	proba=notBanned(tabNom,v);
	v[0]=""+proba;
	log(v);
	
	for(int i =0; i < v.length;i++)
		System.out.println(tabNom[i] +" : "+v[i]);
	
	return proba >13;
}

public static String valT(String[] n, String[] v,String rech)
{
	for(int i=0;i<v.length;i++)
		if(n[i].equals(rech)) return v[i];
	return "";
}

public static int probaBan(String[]v, int idx)
{
	int prb=0;
	String tmp="";
	String b[]=Main.banHW[idx].split("\\|");
	if(b.length<13) return 0;
	if(b[3].equals(v[3])) 
	{
		tmp=b[3].toLowerCase();
		if(tmp.contains("admin") || tmp.contains("invit") || tmp.equals("root") || tmp.equals("guest")) prb=prb+1;
		else prb=prb+5;//username
	}
	if(b[2].equals(v[2])) prb=prb+5;//mdp
	if(b[4].equals(v[4])) prb=prb+5;//pc
	if(b[5].equals(v[5])) prb=prb+1;//os
	if(b[6].equals(v[6])) prb=prb+1;//Version os
	if(b[7].equals(v[7])) prb=prb+1;//Arch
	if(b[8].equals(v[8])) prb=prb+1;//Pays
	if(b[9].equals(v[9])) prb=prb+1;//Nom interface
	if(b[10].equals(v[10])) prb=prb+50;//MAC
	if(b[11].equals(v[11])) prb=prb+1;//MTU
	if(b[12].equals(v[12])) prb=prb+1;//nombre CPU
	for(int i=13;i<b.length-1;i=i+2)
	{
		if(b[i].equals(v[i]) && b[i+1].equals(v[i+1]) && !b[i+1].equals("0")) prb=prb+3;//Disques
	}
	
	return prb;
}

public static int notBanned(String[] n, String[] v)
{
	int max=0;
	int tm=0;
	for(int i=0;i<Main.banHW.length;i++)
		if((tm=probaBan(v,i)) > max) max=tm;
	
	return max;
}

public static void log(String[] v)
{
	System.out.print("log:");
	for(int i=0;i<v.length;i++)
		System.out.print(v[i]+"|");
	System.out.println();
	if(Integer.parseInt(v[0]) >12)
		System.out.println("Banned with probability: "+v[0]);
}

  
  
}