import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;


import struct.Projet;

//TODO @BETA : Verif de CondDecl + coloration minimale
public class VerifieSyntaxe 
/** Classe qui permet de verifier la syntaxe du code dans l'Editeur **/
{
	ArrayList<String> EvVarCondition,EvEventCondition,EvCommande,MenuPossibles;
	
	public VerifieSyntaxe(Projet projet)
	{/** Constructeur. Ajoute les fonctions de l'editeur avec la bonne syntaxe dans des tableaux **/
		
		
 		EvCommande=new ArrayList<String>();
		EvVarCondition=new ArrayList<String>();
		EvEventCondition=new ArrayList<String>();
		MenuPossibles=new ArrayList<String>();
		EvVarCondition.add("%Name%"); EvVarCondition.add("%UpperName%"); EvVarCondition.add("%Classe%");
		for(int i=0;i<projet.getStatsBase().size();i++)//Récup les stats de base et les add a la liste des variables
			EvVarCondition.add("%"+projet.getStatsBase().get(i)+"%"); 
		EvVarCondition.add("%Vie%");EvVarCondition.add("%VieMax%"); EvVarCondition.add("%CurrentMag%"); EvVarCondition.add("%MagMax%");
		EvVarCondition.add("%Gold%");
		EvVarCondition.add("%Lvl%"); EvVarCondition.add("%LvlPoint%"); EvVarCondition.add("%CurrentXP%");
		EvVarCondition.add("%NextXP%"); EvVarCondition.add("%Inventaire%"); EvVarCondition.add("%Timer%"); EvVarCondition.add("%Visible%"); EvVarCondition.add("%Bloque%");
		EvVarCondition.add("%CaseX%"); EvVarCondition.add("%CaseY%"); EvVarCondition.add("%EvCaseX%"); EvVarCondition.add("%EvCaseY%");
		EvVarCondition.add("%Direction%"); EvVarCondition.add("%Groupe%"); EvVarCondition.add("%Guilde%");
		EvVarCondition.add("%Rang%"); EvVarCondition.add("%KillPlayer%"); EvVarCondition.add("%CentreX%"); EvVarCondition.add("%CentreY%");  
		EvVarCondition.add("%BloqueChangeSkin%");//Crée la liste des variables
		EvVarCondition.add("%BloqueAttaque%"); EvVarCondition.add("%BloqueMagie%"); EvVarCondition.add("%BloqueDialogue%"); EvVarCondition.add("%Effect%");
		EvVarCondition.add("%BloqueDefense%"); EvVarCondition.add("%Position%");
		EvVarCondition.add("%NbObjetInventaire%"); EvVarCondition.add("%Arme%"); EvVarCondition.add("%Bouclier%");
		EvVarCondition.add("%Casque%"); EvVarCondition.add("%Armure%"); EvVarCondition.add("%Timer%"); EvVarCondition.add("%Timer2%"); EvVarCondition.add("%Timer3%");
		EvEventCondition.add(".Name%"); EvEventCondition.add(".CaseX%"); EvEventCondition.add(".CaseY%"); EvEventCondition.add(".Chipset%");
		EvEventCondition.add(".Bloquant%"); EvEventCondition.add(".Transparent%"); EvEventCondition.add(".Visible%");
		EvEventCondition.add(".TypeAnim%"); EvEventCondition.add(".Direction%"); EvEventCondition.add(".X%"); EvEventCondition.add(".Y%"); EvEventCondition.add(".W%");
		EvEventCondition.add(".CaseNBX%"); EvEventCondition.add(".CaseNBY%");//Crée la liste variables d'event
		EvEventCondition.add(".H%"); EvEventCondition.add(".NumAnim%"); EvEventCondition.add(".Vitesse%");
		EvEventCondition.add(".AnimAttaque%"); EvEventCondition.add(".AnimDefense%"); EvEventCondition.add(".AnimMagie%");
		EvCommande.add("Message("); EvCommande.add("Conditio"); EvCommande.add("AddObjec"); EvCommande.add("DelObjec"); EvCommande.add("Teleport");
		EvCommande.add("ChangeRe"); EvCommande.add("ChangeSk");  EvCommande.add("InputQue"); EvCommande.add("OnResult");
		EvCommande.add("QueryEnd"); EvCommande.add("Magasin(");  EvCommande.add("Coffre("); EvCommande.add("PlayMusi");
		EvCommande.add("StopMusi"); EvCommande.add("PlaySoun");//Crée la liste des commandes
		EvCommande.add("ChAttaqu"); EvCommande.add("ChBlesse"); EvCommande.add("AddMagie"); EvCommande.add("DelMagie"); EvCommande.add("GenereMo");
		EvCommande.add("TueMonst"); EvCommande.add("SScroll("); EvCommande.add("Attente(");
		EvCommande.add("Sauvegar"); EvCommande.add("Chargeme"); EvCommande.add("Quitter("); EvCommande.add("Options(");
		EvCommande.add("ChangeCl"); EvCommande.add("ShowInte"); EvCommande.add("HideInte");
		EvCommande.add("AddMenu("); EvCommande.add("DelMenu(");
	    MenuPossibles.add("Inventaire"); MenuPossibles.add("Magie"); MenuPossibles.add("Statistique");
	    MenuPossibles.add("Charger"); MenuPossibles.add("Sauver");//Crée la liste des menus possibles IG 
	    MenuPossibles.add("Options"); MenuPossibles.add("Quitter");
	}
	
	public ArrayList<String> getMenuPossibles() 
	/** Retourne les menus possibles **/
	{
		return MenuPossibles;
	}
	public boolean Verifie(String LaCarte,int PosX,int PosY,ArrayList<ArrayList<String>> CarteList) 
	/** Vérif la syntaxe des events sur une carte en PosX,PosY**/
	{
	   	 String ligne,variable,temp;
		 int i,j;
		 boolean ok;
    	    i=0;
    	    while (i<CarteList.size())
    	    {//Pour chaque page
    	      j=0;
    	      while (j<CarteList.get(i).size())
    	      {//Pour chaque ligne d'event
    	        ok=false; // Ok est faux par défaut (Erreur)
    	        ligne=CarteList.get(i).get(j).trim();
    	        if (ligne.compareTo("")==0) // Si la ligne est vide, Ok
    	        	ok=true;
    	        else if (ligne.substring(0,2).compareTo("//")==0) // Si c'est un commentaire, Ok
    	          ok=true;
    	        else
    	        {//Si il y a une commande sur la ligne
    	        	if (ligne.length()>8) 
    	        		variable=ligne.substring(0,8); // On recup la fonction
    	        	else
    	        		variable=ligne;    	        	
	    	        if (InArray(EvCommande,variable)>=0) // Si variable fait partie des commandes, OK
	    	          ok=true;
	    	        else
	    	        {//Si ce n'est pas une commande
	    	        	if (ligne.indexOf("=")>=0) // Si y'a un "=" dans ligne
	    	        	{
	    	        		variable=ligne.substring(0,ligne.indexOf("="));//Récup la partie avant =
	    	        		if (variable.length()>9)
	    	        			temp=variable.substring(0,9);
	    	        		else
	    	        			temp=variable;
	    	        		if ((InArray(EvVarCondition,variable)>=0) || temp.equals("Variable[") || temp.startsWith("Serveur[") || temp.startsWith("Bool["))
	    	        			ok=true;//Si c'est une Variable[] ou si c'est une %Variable%, Ok
	    	        		else
	    	        		{//Sinon, vérifie si c'est une %NomEv.Variable%
	    	        			if (variable.indexOf(".")>=0)
	    	        			{
		    	        			variable=variable.substring(variable.indexOf("."));
		    	        			if (InArray(EvEventCondition,variable)>=0) // On verifie variable avec EvEventCondition
		    	        				ok=true;//Si variable d'event, Ok
	    	        			}
	    	        			else
	    	        				ok=false;
	    	        		}
	    	        	}
	    	        }
	    	        if (ok==false)
	    	        { // Si il y a une erreur, un message indique position et ligne
				    	if (JOptionPane.showConfirmDialog(null,
				                "Evénement mal formaté\n"+LaCarte+"\nX: "+PosX+" / Y: "+PosY+"\nPage "+i+", Ligne "+j+"\n\n"+ligne+"\n\nIgnorer?",
				                "Erreur",
				                JOptionPane.YES_NO_OPTION,
				                JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION)
				    		return true;//Si ignorer, renvoie true
				    	else
				    		return false;//Sinon renvoie false
	    	        }
    	        }
    	        j++;
    	      }
    	      i++;
    	    }
    	    return true;
	}
	
	public void Highlight(String ligne,StyledDocument doc,Style style, Style commentaire,Style fonc,Style vari,Style vari2)
	{/**Coloration syntaxe de "ligne" d'event. Fonctions:bleu,variable:orange,commentaires:verts,erreurs:rouges**/
		try 
		{	
			if(!VerifieLigne(ligne)) 
			{/**Si erreur dans la ligne, met en rouge souligné**/
				doc.insertString(0, ligne+"\n", style);
			}
			else if(LineIsComment(ligne))
			{/**Si commentaire, met en vert**/
				doc.insertString(0, ligne+"\n", commentaire);
			}
			else if(ligne.indexOf("(") >=0 && ligne.lastIndexOf(")") >=0 && !(ligne.startsWith("%") || ligne.startsWith("Variable[") || ligne.startsWith("Serveur[") || ligne.startsWith("Bool["))) 
			{/**Si fonction avec parenthèses**/
				String line=ligne.substring(0, ligne.lastIndexOf(")")+1);//Récup avant/après dernière ")"
				String com=ligne.substring(ligne.lastIndexOf(")")+1);
				
				int posPar =line.indexOf("(");
				String fonction=line.substring(0, posPar+1);//Récup "Message(" et contenu parenthèses
				String msg=line.substring(fonction.length(), line.lastIndexOf(")"));
				doc.insertString(0, fonction, fonc);//Insère la fonction en bleu, contenu des parenthèses,
				doc.insertString(fonction.length(), msg, null);
				doc.insertString(fonction.length()+msg.length(), ")", fonc);//Insère la fin de parenth bleue
				
				if(LineIsComment(com)) 
					doc.insertString(line.length(), com+"\n", commentaire);
				else //Si le contenu après ) est un commentaire, le met en vert
					doc.insertString(line.length(), com+"\n", null);
				
			}
			else
			{/**Si autre fonction avec ou sans commentaire ou assignation de variable**/
				String line=ligne;
				String com="";
				if(ligne.indexOf("//") !=-1)
				{
					line=ligne.substring(0, ligne.indexOf("//"));
					com=ligne.substring(ligne.indexOf("//"));//Récup partie avant et après commentaire
				}
				
				String variable=ligne;
				
				if (ligne.length()>8)
	        		variable=ligne.substring(0,8);
		        if (InArray(EvCommande,variable)>=0)
		        	if(line.contains("(") || line.contains(")"))
		        		throw new Exception();//Fonction avec parenthèse manquante
		        	else/**Commande sans parenthèses**/
		        		doc.insertString(0, line, fonc);//Si c'est une commande, colore en bleu
		        else if(line.contains("=") && (line.startsWith("%") || variable.equals("Variable") || variable.equals("Serveur[") || variable.startsWith("Bool[")))
		        {/**Variables**/
		        	variable =line.substring(0, line.indexOf("="));
		        	
		        	if (InArray(EvVarCondition,variable)>=0 || InArray(EvEventCondition, variable.substring( variable.indexOf(".")>=0 ? variable.indexOf(".") : 0 ))>=0)
		        		doc.insertString(0, variable, vari);/** %Variable% **/
		        	else if (line.startsWith("Variable[") || line.startsWith("Serveur[")|| line.startsWith("Bool["))
		        		doc.insertString(0, variable, vari2);/** Variable[] **/
		        	else/**Rien**/
		        		doc.insertString(0, variable, null);
		        	doc.insertString(variable.length(), line.substring(line.indexOf("=")), null);//Fin de ligne
		        }
		        else /**Rien**/
		        	doc.insertString(0, line, null);//Si rien à colorer
				
				doc.insertString(line.length(), com+"\n", commentaire);//Met le commentaire en vert
				
			}
		}catch(Exception e){e.printStackTrace(); try {doc.insertString(0, ligne+"\n", style);} catch (BadLocationException e1) {e1.printStackTrace();}}
		
	}	
	
	public boolean LineIsComment(String ligne)
	{/**Vérifie si la ligne entière est un commentaire**/
		if (ligne.length()<2) // Si la ligne fais moins de 2 carracteres
    		return false;
        if (ligne.substring(0,2).equals("//")) // Si c'est un commentaire
          return true;
        return false;
	}
	
	public boolean VerifieLigne(String ligne)
	/** Methode qui permet de vérifier en temps réel la syntaxe des lignes de code des events **/
	{
	   	 String variable,temp;
		 boolean ok;
        ok=false;
    	ligne=ligne.trim();
    	if (ligne.compareTo("")==0) // Si ligne vide, ok
    		return true;
    	if (ligne.length()<2) // Si la ligne fais moins de 2 caracteres, erreur
    		return false;
        if (ligne.substring(0,2).compareTo("//")==0) // Si c'est un commentaire
          ok=true;
        else
        {//S'il y a une commande sur la ligne
        	if (ligne.length()>8)
        		variable=ligne.substring(0,8);
        	else
        		variable=ligne;
	        if (InArray(EvCommande,variable)>=0)//Si c'est une commande de la liste, Ok
	          ok=true;
	        else
	        {//Sinon, c'est une variable
	        	if (ligne.indexOf("=")>=0)
	        	{
	        		variable=ligne.substring(0,ligne.indexOf("="));//On récup la partie avant le =
	        		if (variable.length()>9)
	        			temp=variable.substring(0,9);
	        		else
	        			temp=variable;//Si c'est une variable joueurs ou Variable[], Ok
	        		if ((InArray(EvVarCondition,variable)>=0) || temp.equals("Variable[") || temp.startsWith("Serveur[") || temp.startsWith("Bool["))
	        			ok=true;
	        		else
	        		{//C'est une variable d'event ou erreur
	        			if (variable.indexOf(".")>=0)
	        			{
		        			variable=variable.substring(variable.indexOf("."));//Récup après le point
		        			if (InArray(EvEventCondition,variable)>=0) 
		        				ok=true;//Si dans la liste des variables d'event, ok
	        			}
	        			else
	        				ok=false;//Sinon, erreur
	        		}
	        	}
	        }
        }
   	    return ok;
	}
	
	private int InArray(ArrayList<String> tableau,String tofind)
	/** Vérifie si tofind se trouve dans tableau. Si présent, renvoie la position, sinon -1**/
	{
		int i=0;
		while (i<tableau.size())
		{//Parcoure le tableau
			if (tableau.get(i).compareTo(tofind)==0)//Si la valeur i du tab=tofind, renvoie i
				return i;
			i++;
		}
		return -1;
	}
}
