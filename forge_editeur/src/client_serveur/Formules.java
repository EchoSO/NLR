package client_serveur;

import struct.Projet;
import struct.Projet.Objet;
import struct.Sauvegarde;
import struct.Projet.Evenement;
import client_serveur.Game;
import client_serveur.Game.MonstreGame;

public class Formules 
{
	public calcul Calcule;
	public Sauvegarde PlayerInfo;
	public Projet general;
	public Game P;
	
	public Formules(calcul Calcule, Sauvegarde PlayerInfo, Projet general, Game Parent)
	{
		this.Calcule=Calcule;
		this.PlayerInfo=PlayerInfo;
		this.general=general;
		this.P=Parent;
	}
	
	
/**################################################################**/
	public String ReplaceStringEv(String Input)
/**################################################################
	 * Remplace les variables relatives aux évenements dans les formules**/
		{
			 int i,j,k,compte,bit;
			 String variable,Result;
			 String varFinale="";
			 Evenement EvnFinal;
			 boolean trouve;
			 Result=Input;
			 for (i=0;i<P.EvEventCondition.size();i++)
			 {//Pour chaque variable d'event
				 compte=Input.indexOf(P.EvEventCondition.get(i));//Vérif si elle est dans Input
			     if (compte>=0)
			     {//Si elle est dans input
			    	 variable=Result;
			    	 bit=compte;
			    	 while (variable.charAt(bit)!='%' && (bit > 0))
			    		 bit--;
			    	 variable=variable.substring(bit+1,compte);//On récup le nom de l'event
			          j=0; k=0; trouve=false;
			          while ((trouve==false) & (j < P.CurrentMap.TailleX))
			          {//On parcoure la map en X
			        	  k=0;
			        	  while ((trouve==false) & (k < P.CurrentMap.TailleY))
			        	  {//On parcoure en Y
			        		  if (P.Evenements[j][k].evenement!=null)
			        		  {//S'il y a un evenement sur la map et que le nom correspond, ok
			        			  if (P.Evenements[j][k].Ev!=-1)
			        				  if (P.Evenements[j][k].evenement.get(P.Evenements[j][k].Ev).Name.equals(variable)) trouve=true;
			        		  }
			        		  k++;
			        	  }
			        	  j++;
			          }
			          if (trouve)
			          {
			        	  j--;//On décrémente pour revenir aux bonnes valeurs X Y
			        	  k--;
			        	  varFinale=variable+P.EvEventCondition.get(i);
			        	  EvnFinal=P.Evenements[j][k].evenement.get(P.Evenements[j][k].Ev);
			        	  
				          switch(i)
				          {//Remplace la variable par le texte correspondant
					          	case 0 : /*.Name%*/ 	Result=Result.replace("%"+varFinale, EvnFinal.Name); break;
				          		case 1 : /*.CaseX%*/	Result=Result.replace("%"+varFinale, j+""); break;
				          		case 2 : /*.CaseY%*/	Result=Result.replace("%"+varFinale, k+""); break;
				          		case 3 : /*.Chipset%*/	Result=Result.replace("%"+varFinale, EvnFinal.Chipset); break;
				          		case 4 : /*.Bloquant%*/	Result=Result.replace("%"+varFinale, EvnFinal.Bloquant ? "1" : "0"); break;
				          		case 5 : /*.Transparent%*/Result=Result.replace("%"+varFinale, EvnFinal.Transparent ? "1" : "0"); break;
				          		case 6 : /*.Visible%*/	Result=Result.replace("%"+varFinale, EvnFinal.Visible ? "1" : "0"); break;
				          		case 7 : /*.TypeAnim%*/	Result=Result.replace("%"+varFinale, EvnFinal.TypeAnim+""); break;
				          		case 8 : /*.Direction%*/Result=Result.replace("%"+varFinale, EvnFinal.Direction+""); break;
				          		case 9 : /*.X%*/		Result=Result.replace("%"+varFinale, EvnFinal.X+""); break;
				          		case 10 : /*.Y%*/		Result=Result.replace("%"+varFinale, EvnFinal.Y+""); break;
				          		case 11 : /*.W%*/		Result=Result.replace("%"+varFinale, EvnFinal.W+""); break;
				          		case 12 : /*.H%*/		Result=Result.replace("%"+varFinale, EvnFinal.H+""); break;
				          		case 13 : /*.NumAnim%*/	Result=Result.replace("%"+varFinale, EvnFinal.NumAnim+""); break;
				          		case 14 : /*.Vitesse%*/	Result=Result.replace("%"+varFinale, EvnFinal.Vitesse+""); break;
				          }
			          }
			     }
			 }
			 return Result;
		}

/**################################################################**/
	public String replacePlayerVar(String Input)
/**################################################################
 * Remplace les variables relatives au joueur dans une formule**/
	{
		String Result=Input;
		int compte=0;
		
		Result=Result.replace("%Name%",PlayerInfo.Name==null ? "" : PlayerInfo.Name);
		Result=Result.replace("%UpperName%",Util.EscapeChar(PlayerInfo.Name));
		if (PlayerInfo.Classe!=null)
		  Result=Result.replace("%Classe%",PlayerInfo.Classe.Name);
		for (int i=0;i<general.getStatsBase().size();i++)
			Result=Result.replace("%"+general.getStatsBase().get(i)+"%",PlayerInfo.Stats.getProperty(general.getStatsBase().get(i),"0"));
		Result=Result.replace("%Vie%",PlayerInfo.Vie+"");
		Result=Result.replace("%VieMax%",PlayerInfo.VieMax+"");
		Result=Result.replace("%CurrentMag%",PlayerInfo.CurrentMag+"");
		Result=Result.replace("%MagMax%",PlayerInfo.MagMax+"");
		Result=Result.replace("%Gold%",PlayerInfo.Gold+"");
		Result=Result.replace("%Lvl%",PlayerInfo.Lvl+"");
		Result=Result.replace("%LvlPoint%",PlayerInfo.LvlPoint+"");
		Result=Result.replace("%CurrentXP%",PlayerInfo.CurrentXP+"");
		Result=Result.replace("%NextXP%",PlayerInfo.NextXP+"");
		Result=Result.replace("%Timer%",P.Timer+"");
		Result=Result.replace("%Timer2%",P.Timer2+"");
		Result=Result.replace("%Timer3%",P.Timer3+"");
		Result=Result.replace("%CaseX%",PlayerInfo.pmapx/2+"");
		Result=Result.replace("%CaseY%",PlayerInfo.pmapy/2+"");
		Result=Result.replace("%CentreX%",PlayerInfo.CentreX+"");
		Result=Result.replace("%CentreY%",PlayerInfo.CentreY+"");
		Result=Result.replace("%Direction%",P.PlayerDirection+"");
		Result=Result.replace("%Position%",PlayerInfo.Position+"");
		Result=Result.replace("%Name%",PlayerInfo.Chipset);
		Result=Result.replace("%BloqueChangeSkin%",PlayerInfo.BloqueChangeSkin==true ? "1" : "0");
		Result=Result.replace("%BloqueAttaque%",PlayerInfo.BloqueAttaque==true ? "1" : "0");
		Result=Result.replace("%BloqueDefense%",PlayerInfo.BloqueDefense==true ? "1" : "0");
		Result=Result.replace("%BloqueMagie%",PlayerInfo.BloqueMagie==true ? "1" : "0");
		Result=Result.replace("%Effect%",P.Effect+"");
		compte=0;
		for (int i=0;i<100;i++)
		  if (PlayerInfo.Inventaire[i][0]>0)
		    compte++;
		Result=Result.replace("%NbObjetInventaire%",compte+"");
		return Result;
	}
	
	
/**################################################################**/
    public String ReplaceStringVariable(String Input)
/**################################################################
 * Remplace les variables dans un String génral TODO @BETA ajouter Bool[] Serveur[]**/
	{
	  int compte;
	  String Result,variablefirst,variable,resultat;
	  int montant,montant2;
	  Result = Input;
	  if(Input.equals("%EvCaseX%") || Input.equals("%EvCaseY%") || Input.equals("%Inventaire%") || Input.equals("%Arme%")
	  || Input.equals("%Bouclier%") || Input.equals("%Casque%") || Input.equals("%Armure%"))
	    return Result;//Si seulement une de ces variables, return. Sera traité après.
	  Result=ReplaceStringEv(Result);
	  while (Result.contains("Variable["))
	  {//Si il y a une Variable[]
	    variable=Result.substring(Result.indexOf("Variable[")+9);
	    variable=variable.substring(0,Util.PosExterne('[',']',variable.toCharArray())-1);
	    variablefirst=variable;
	    variable=ReplaceStringVariable(variable);
	    compte=(int) Calcule.Calcule(variable);
	    if (Calcule.Err==0)
	    	variable=compte+"";//Récup la valeur et remplace
	    resultat=PlayerInfo.Variable.getProperty(variable, "0");
	    Result=Result.replace("Variable["+variablefirst+"]",resultat);
	  }
	  while (Result.contains("%rand("))
	  {//Si il y a des %rand()%
	    variable=Result.substring(Result.indexOf("%rand(")+6);
	    variable=variable.substring(0,variable.indexOf(")%"));
	    resultat=ReplaceStringVariable(variable);
	    montant=(int) Calcule.Calcule(resultat);
	    montant=Util.random(montant);//Calcule une valeur pour chaque rand
	    Result=Result.replaceFirst("\\Q%rand("+variable+")%\\E", montant+"");//Remplace 1 par 1
	  }
	  while (Result.contains("%upper("))
	  {//S'il y a des %upper()%
 	     variable=Result.substring(Result.indexOf("%upper(")+7);
	     variable=variable.substring(0,variable.indexOf(")%"));
	     resultat=ReplaceStringVariable(variable);
	     resultat=Util.EscapeChar(resultat);//Met en majuscules et remplace
	     Result=Result.replace("%upper("+variable+")%",resultat);		  
	  }
	  while (Result.contains("%max("))
	  {//S'il y a des %max()%
 	     variable=Result.substring(Result.indexOf("%max(")+5);
	     variable=variable.substring(0,variable.indexOf(")%"));//Récup les 2 valeurs
	     variablefirst=variable;
	     resultat=variable.substring(variable.indexOf(",")+1);
	     variable=variable.substring(0,variable.indexOf(","));
	     variable=ReplaceStringVariable(variable);//Remplace les formules dedans
	     resultat=ReplaceStringVariable(resultat);
	     montant=(int) Calcule.Calcule(variable);
	     montant2=(int) Calcule.Calcule(resultat);
	     if (montant2>montant)
	    	 montant=montant2;//Récup la valeur max et remplace
	    Result=Result.replace("%max("+variablefirst+")%",montant+"");	     
	  }
	  while (Result.contains("%min("))
	  {//S'il y a des %min()%
 	     variable=Result.substring(Result.indexOf("%min(")+5);
	     variable=variable.substring(0,variable.indexOf(")%"));//Récup les 2 valeurs
	     variablefirst=variable;
	     resultat=variable.substring(variable.indexOf(",")+1);
	     variable=variable.substring(0,variable.indexOf(","));
	     variable=ReplaceStringVariable(variable);
	     resultat=ReplaceStringVariable(resultat);//Remplace les formules dedans
	     montant=(int) Calcule.Calcule(variable);
	     montant2=(int) Calcule.Calcule(resultat);
	     if (montant2<montant)
	    	 montant=montant2;//Récup la valeur min et remplace
	    Result=Result.replace("%min("+variablefirst+")%",montant+"");	     
	  }
	  if (Result.contains("%"))
	  {//S'il y a des variables joueurs, les remplace
		  Result=replacePlayerVar(Result);
	  }
	  return Result;//Renvoie la formule avec les variables remplacées par les valeurs
    }
	
    
    
/**################################################################**/
	public String ReplaceStringMagie(String Input,MonstreGame MWizard,MonstreGame Cible)
/**################################################################
 * Remplace les variables dans une formule de magie TODO @BETA remplacer Bool[] Serveur[] etc**/
	{
		  int i,compte;
		  String Result,variablefirst,variable,resultat;
		  int montant,montant2;	  
		  Result = Input;
		  while (Result.contains("Variable["))
		  {//Si y'a des Variable[]
		    variable=Result.substring(Result.indexOf("Variable[")+9);
		    variable=variable.substring(0,Util.PosExterne('[',']',variable.toCharArray())-1);
		    variablefirst=variable;//Récup nom var, remplace
		    variable=ReplaceStringMagie(variable,MWizard,Cible);
		    compte=(int) Calcule.Calcule(variable);
		    if (Calcule.Err==0)
		    	variable=compte+"";//Calcule
		    resultat=PlayerInfo.Variable.getProperty(variable, "0");//Récup la variable et remplace dans la form.
		    Result=Result.replace("Variable["+variablefirst+"]",resultat);
		  }
		  while (Result.contains("%rand("))
		  {//Si il y a des %rand()%
		    variable=Result.substring(Result.indexOf("%rand(")+6);
		    variable=variable.substring(0,variable.indexOf(")%"));
		    resultat=ReplaceStringMagie(variable,MWizard,Cible);
		    montant=(int) Calcule.Calcule(resultat);
		    montant=Util.random(montant);
		    //Remplace 1 par 1 les rand
		    Result=Result.replaceFirst("\\Q%rand("+variable+")%\\E", montant+"");
		  }
		  while (Result.contains("%upper("))
		  {//Si il y a des upper
	 	     variable=Result.substring(Result.indexOf("%upper(")+7);
		     variable=variable.substring(0,variable.indexOf(")%"));
		     resultat=ReplaceStringMagie(variable,MWizard,Cible);
		     resultat=Util.EscapeChar(resultat); //Remplace par la valeur en majuscules
		     Result=Result.replace("%upper("+variable+")%",resultat);		  
		  }
		  while (Result.contains("%max("))
		  {//Si il y a des %max()%
	 	     variable=Result.substring(Result.indexOf("%max(")+5);
		     variable=variable.substring(0,variable.indexOf(")%"));
		     variablefirst=variable;
		     resultat=variable.substring(variable.indexOf(",")+1);
		     variable=variable.substring(0,variable.indexOf(","));
		     variable=ReplaceStringMagie(variable,MWizard,Cible);
		     resultat=ReplaceStringMagie(resultat,MWizard,Cible);
		     montant=(int) Calcule.Calcule(variable);
		     montant2=(int) Calcule.Calcule(resultat);
		     if (montant2>montant)
		    	 montant=montant2;//Récup la valeur la plus grande et remplace
		    Result=Result.replace("%max("+variablefirst+")%",montant+"");	     
		  }
		  while (Result.contains("%min("))
		  {//Si il y a des %min()%
	 	     variable=Result.substring(Result.indexOf("%min(")+5);
		     variable=variable.substring(0,variable.indexOf(")%"));
		     variablefirst=variable;
		     resultat=variable.substring(variable.indexOf(",")+1);
		     variable=variable.substring(0,variable.indexOf(","));
		     variable=ReplaceStringMagie(variable,MWizard,Cible);
		     resultat=ReplaceStringMagie(resultat,MWizard,Cible);
		     montant=(int) Calcule.Calcule(variable);
		     montant2=(int) Calcule.Calcule(resultat);
		     if (montant2<montant)
		    	 montant=montant2;//Récup la valeur la plus petite et remplace
		    Result=Result.replace("%min("+variablefirst+")%",montant+"");	     
		  }
		  if (Result.contains("%"))
		  {//Si il y a des %Variable%
			  if (Result.contains("%Wizard."))
			  {//Si il y a des variables Wizard
			      if (MWizard==null)
			      {//Si pas de Wizard spécifié : joueur=Wizard
			    	  for (i=0;i<general.getStatsBase().size();i++)//Remplace les stats de base
			    		  Result=Result.replace("%Wizard."+general.getStatsBase().get(i)+"%",PlayerInfo.Stats.getProperty(general.getStatsBase().get(i),"0"));
				      Result=Result.replace("%Wizard.Attaque%",((int) Calcule.Calcule(ReplaceStatVariable(PlayerInfo.Classe.FormuleAttaque,Cible))+P.MagicAttaque)+"");        	    	  		    	     
				      Result=Result.replace("%Wizard.Esquive%",((int) Calcule.Calcule(ReplaceStatVariable(PlayerInfo.Classe.FormuleEsquive,Cible))+P.MagicEsquive)+"");        	    	  		    	     
				      Result=Result.replace("%Wizard.Vie%",PlayerInfo.Vie+"");
				      Result=Result.replace("%Wizard.VieMax%",PlayerInfo.VieMax+"");
				      Result=Result.replace("%Wizard.CurrentMag%",PlayerInfo.CurrentMag+"");
				      Result=Result.replace("%Wizard.MagMax%",PlayerInfo.MagMax+"");
				      Result=Result.replace("%Wizard.Gold%",PlayerInfo.Gold+"");
				      Result=Result.replace("%Wizard.Lvl%",PlayerInfo.Lvl+"");//Et les autres variables
				      Result=Result.replace("%Wizard.LvlPoint%",PlayerInfo.LvlPoint+"");
				      Result=Result.replace("%Wizard.CurrentXP%",PlayerInfo.CurrentXP+"");
				      Result=Result.replace("%Wizard.NextXP%",PlayerInfo.NextXP+"");
			      }
			      else
			      {//Si il y a un autre Wizard
			    	  for (i=0;i<general.getStatsBase().size();i++)//Remplace les stats de base
			    		  Result=Result.replace("%Wizard."+general.getStatsBase().get(i)+"%","0");
				      Result=Result.replace("%Wizard.Attaque%",((int) Calcule.Calcule(ReplaceStatVariable(general.getClassesMonstre().get(MWizard.monstre.ClasseMonstre).FormuleAttaque,MWizard))+MWizard.MagicAttaque)+"");        	    	  		    	     
				      Result=Result.replace("%Wizard.Esquive%",((int) Calcule.Calcule(ReplaceStatVariable(general.getClassesMonstre().get(MWizard.monstre.ClasseMonstre).FormuleEsquive,MWizard))+MWizard.MagicEsquive)+"");        	    	  		    	     
				      Result=Result.replace("%Wizard.Vie%",MWizard.vie+"");
				      Result=Result.replace("%Wizard.VieMax%",MWizard.monstre.Vie+"");
				      Result=Result.replace("%Wizard.CurrentMag%","0");
				      Result=Result.replace("%Wizard.MagMax%","0");
				      Result=Result.replace("%Wizard.Gold%","0");//Et les autres stats comme il faut
				      Result=Result.replace("%Wizard.Lvl%",MWizard.monstre.Lvl+"");
				      Result=Result.replace("%Wizard.LvlPoint%","0");
				      Result=Result.replace("%Wizard.CurrentXP%","0");
				      Result=Result.replace("%Wizard.NextXP%","0");
			      }				  
			  }
			  if (Result.contains("%Cible."))
			  {//S'il y a une cible dans la formule
				  if (Cible==null)
				  {//Si pas de cible précisée, Joueur=Cible
				      Result=Result.replace("%Cible.Attaque%",((int) Calcule.Calcule(ReplaceStatVariable(PlayerInfo.Classe.FormuleAttaque,MWizard))+P.MagicAttaque)+"");        	    	  		    	     
				      Result=Result.replace("%Cible.Esquive%",((int) Calcule.Calcule(ReplaceStatVariable(PlayerInfo.Classe.FormuleEsquive,MWizard))+P.MagicEsquive)+"");        	    	  		    	     
				      Result=Result.replace("%Cible.Degat%",((int) Calcule.Calcule(ReplaceStatVariable(PlayerInfo.Classe.FormuleDegat,MWizard))+P.MagicDommage)+"");        	    	  		    	     
				      Result=Result.replace("%Cible.Defense%",((int) Calcule.Calcule(ReplaceStatVariable(PlayerInfo.Classe.FormuleDefense,MWizard))+P.MagicDefense)+"");        	    	  		    	     
				      Result=Result.replace("%Cible.Lvl%",PlayerInfo.Lvl+"");
				      Result=Result.replace("%Cible.Vie%",PlayerInfo.Vie+"");
				      Result=Result.replace("%Cible.VieMax%",PlayerInfo.VieMax+"");
				      Result=Result.replace("%Cible.Bloque%",P.Freeze ? "1" : "0");
				  }
				  else
				  {//Si une cible est précisée, on utilise ses valeur
				      Result=Result.replace("%Cible.Attaque%",((int) Calcule.Calcule(ReplaceStatVariable(general.getClassesMonstre().get(Cible.monstre.ClasseMonstre).FormuleAttaque,Cible))+Cible.MagicAttaque)+"");        	    	  		    	     
				      Result=Result.replace("%Cible.Esquive%",((int) Calcule.Calcule(ReplaceStatVariable(general.getClassesMonstre().get(Cible.monstre.ClasseMonstre).FormuleEsquive,Cible))+Cible.MagicEsquive)+"");        	    	  		    	     
				      Result=Result.replace("%Cible.Degat%",((int) Calcule.Calcule(ReplaceStatVariable(general.getClassesMonstre().get(Cible.monstre.ClasseMonstre).FormuleDegat,Cible))+Cible.MagicDommage)+"");        	    	  		    	     
				      Result=Result.replace("%Cible.Defense%",((int) Calcule.Calcule(ReplaceStatVariable(general.getClassesMonstre().get(Cible.monstre.ClasseMonstre).FormuleDefense,Cible))+Cible.MagicDefense)+"");        	    	  		    	     
				      Result=Result.replace("%Cible.Lvl%",Cible.monstre.Lvl+"");
				      Result=Result.replace("%Cible.Vie%",Cible.vie+"");
				      Result=Result.replace("%Cible.VieMax%",Cible.monstre.Vie+"");
				      Result=Result.replace("%Cible.Bloque%",Cible.Bloque ? "1" : "0");
				  }
			  }
		  }
		  return Result;	//Renvoie la formule avec les variables remplacées	
	}
	
	
/**################################################################**/
    public String ReplaceStatVariable(String Input,MonstreGame monstre)
/**################################################################
 * Remplace les stats etc dans une formule TODO @BETA remplacer Bool[] Serveur[] etc **/
    {
		  String Result,variablefirst,variable,resultat;
		  int montant,montant2;
		  Objet equipActuel;
		  if (Input==null)
			  return "0";
    	  Result=Input;
    	  while (Result.contains("%rand("))
    	  {/**Remplace 1 par 1 les %rand par un nombre aléatoire**/
    	    variable=Result.substring(Result.indexOf("%rand(")+6);
    	    variable=variable.substring(0,variable.indexOf(")%"));//Récup l'intérieur du rand()
    	    resultat=ReplaceStatVariable(variable,monstre);//Remplace les stats etc et calcule le max du rand
    	    montant=(int) Calcule.Calcule(resultat);
    	    montant=Util.random(montant);
    	    //Génère le nombre aléatoire et remplace le rand
    	    Result=Result.replaceFirst("\\Q%rand("+variable+")%\\E", montant+"");
    	  }
    	  while (Result.contains("%max("))
    	  {
     	     variable=Result.substring(Result.indexOf("%max(")+5);
    	     variable=variable.substring(0,variable.indexOf(")%"));//Récup le contenu de max()
    	     variablefirst=variable;
    	     resultat=variable.substring(variable.indexOf(",")+1);
    	     variable=variable.substring(0,variable.indexOf(","));//Sépare les deux valeurs
    	     variable=ReplaceStatVariable(variable,monstre);
    	     resultat=ReplaceStatVariable(resultat,monstre);
    	     montant=(int) Calcule.Calcule(variable);
    	     montant2=(int) Calcule.Calcule(resultat);
    	     if (montant2>montant)//Récup la plus grande
    	    	 montant=montant2;
    	    //Place la plus grande à la place du %max()%
    	     Result=Result.replace("%max("+variablefirst+")%",montant+"");
    	  }
    	  while (Result.contains("%min("))
    	  {
     	     variable=Result.substring(Result.indexOf("%min(")+5);
    	     variable=variable.substring(0,variable.indexOf(")%"));//Récup le contenu de min()
    	     variablefirst=variable;
    	     resultat=variable.substring(variable.indexOf(",")+1);
    	     variable=variable.substring(0,variable.indexOf(","));//Sépare les 2 valeurs
    	     variable=ReplaceStatVariable(variable,monstre);
    	     resultat=ReplaceStatVariable(resultat,monstre);
    	     montant=(int) Calcule.Calcule(variable);
    	     montant2=(int) Calcule.Calcule(resultat);
    	     if (montant2<montant)//Récup la plus petite
    	    	 montant=montant2;
    	    //Place la plus petite à la place du %min()%
    	    Result=Result.replace("%min("+variablefirst+")%",montant+"");
    	  }
    	  if (Result.contains("%"))
    	  {/**Si il y a des variables, remplace les variables joueur**/
    		  
		      Result=replacePlayerVar(Result);
		      
		      if (PlayerInfo.Arme>0)
		      {//Si il y a une  arme, on remplace les valeurs
		    	  equipActuel=general.getObjetByIndex(PlayerInfo.Arme-1);
			      Result=Result.replace("%Arme.Prix%",equipActuel.Prix +"");
			      Result=Result.replace("%Arme.Attaque%",equipActuel.Attaque+"");
			      Result=Result.replace("%Arme.Defense%",equipActuel.Defense+"");
			      Result=Result.replace("%Arme.Vie%",equipActuel.PV+"");
			      Result=Result.replace("%Arme.Magie%",equipActuel.PM+"");
		      }
		      else
		      {//Si pas d'arme, on met 0 automatiquement
			      Result=Result.replace("%Arme.Prix%","0");
			      Result=Result.replace("%Arme.Attaque%","0");
			      Result=Result.replace("%Arme.Defense%","0");
			      Result=Result.replace("%Arme.Vie%","0");
			      Result=Result.replace("%Arme.Magie%","0");		    	  
		      }
		      if (PlayerInfo.Armure>0)
		      {//Si il y a une  armure, on remplace les valeurs
		    	  equipActuel=general.getObjetByIndex(PlayerInfo.Armure-1);
			      Result=Result.replace("%Armure.Prix%",equipActuel.Prix+"");
			      Result=Result.replace("%Armure.Attaque%",equipActuel.Attaque+"");
			      Result=Result.replace("%Armure.Defense%",equipActuel.Defense+"");
			      Result=Result.replace("%Armure.Vie%",equipActuel.PV+"");
			      Result=Result.replace("%Armure.Magie%",equipActuel.PM+"");
		      }
		      else
		      {//Si pas d'armure, met 0
			      Result=Result.replace("%Armure.Prix%","0");
			      Result=Result.replace("%Armure.Attaque%","0");
			      Result=Result.replace("%Armure.Defense%","0");
			      Result=Result.replace("%Armure.Vie%","0");
			      Result=Result.replace("%Armure.Magie%","0");		    	  
		      }
		      if (PlayerInfo.Casque>0)
		      {//Si il y a un casque, on remplace les valeurs
		    	  equipActuel=general.getObjetByIndex(PlayerInfo.Casque-1);
			      Result=Result.replace("%Casque.Prix%",equipActuel.Prix+"");
			      Result=Result.replace("%Casque.Attaque%",equipActuel.Attaque+"");
			      Result=Result.replace("%Casque.Defense%",equipActuel.Defense+"");
			      Result=Result.replace("%Casque.Vie%",equipActuel.PV+"");
			      Result=Result.replace("%Casque.Magie%",equipActuel.PM+"");
		      }
		      else
		      {//Si pas de casque, on met 0
			      Result=Result.replace("%Casque.Prix%","0");
			      Result=Result.replace("%Casque.Attaque%","0");
			      Result=Result.replace("%Casque.Defense%","0");
			      Result=Result.replace("%Casque.Vie%","0");
			      Result=Result.replace("%Casque.Magie%","0");		    	  
		      }
		      if (PlayerInfo.Bouclier>0)
		      {//Si il y a un bouclier, on remplace les valeurs
		    	  equipActuel=general.getObjetByIndex(PlayerInfo.Bouclier-1);
			      Result=Result.replace("%Bouclier.Prix%",equipActuel.Prix+"");
			      Result=Result.replace("%Bouclier.Attaque%",equipActuel.Attaque+"");
			      Result=Result.replace("%Bouclier.Defense%",equipActuel.Defense+"");
			      Result=Result.replace("%Bouclier.Vie%",equipActuel.PV+"");
			      Result=Result.replace("%Bouclier.Magie%",equipActuel.PM+"");
		      }
		      else
		      {//Si pas de bouclier, on met 0
			      Result=Result.replace("%Bouclier.Prix%","0");
			      Result=Result.replace("%Bouclier.Attaque%","0");
			      Result=Result.replace("%Bouclier.Defense%","0");
			      Result=Result.replace("%Bouclier.Vie%","0");
			      Result=Result.replace("%Bouclier.Magie%","0");		    	  
		      }
		      if (monstre!=null)
		      {/**Si un monstre est passé en paramètre**/
		    	  Result=Result.replace("%Monstre.Attaque%", //TODO @BETA Choisir MAtt/Att de façon appropriée ?
		    			  monstre.MagicAttaque>0 ? monstre.MagicAttaque+"" : monstre.monstre.Attaque+"");
		    	  Result=Result.replace("%Monstre.Esquive%",
		    			  monstre.MagicEsquive>0 ? monstre.MagicEsquive+"" : monstre.monstre.Esquive+"");		    	  
		    	  	   //Remplace ses stats etc 	  
			      Result=Result.replace("%Monstre.Vie%",monstre.vie+"");		    	  
			      Result=Result.replace("%Monstre.Lvl%",monstre.monstre.Lvl+"");		    	  
			      Result=Result.replace("%Monstre.VieMax%",monstre.monstre.Vie+"");
			      
			      Result=Result.replace("%Monstre.Degat%",
			    		  monstre.MagicDommage>0 ? monstre.MagicDommage+"" : monstre.monstre.Dommage+"");		    	  
			      		    	  
		    	  Result=Result.replace("%Monstre.Defense%",
		    			  monstre.MagicDefense>0 ? monstre.MagicDefense+"" : monstre.monstre.Defense+"");		    	  
		    	  		    	  
			      Result=Result.replace("%Monstre.XPMin%",monstre.XPMin+"");
			      Result=Result.replace("%Monstre.XPMax%",monstre.XPMax+"");		    	  
			      Result=Result.replace("%Monstre.GoldMin%",monstre.GoldMin+"");		    	  
			      Result=Result.replace("%Monstre.GoldMax%",monstre.GoldMax+"");		    	  
		      }
		      else
		      {//Si pas de monstre, remplace tout par 0
			      Result=Result.replace("%Monstre.Attaque%","0");		    	  
			      Result=Result.replace("%Monstre.Defense%","0");		    	  
			      Result=Result.replace("%Monstre.Vie%","0");		    	  
			      Result=Result.replace("%Monstre.Lvl%","0");		    	  
			      Result=Result.replace("%Monstre.VieMax%","0");		    	  
			      Result=Result.replace("%Monstre.Degat%","0");		    	  
			      Result=Result.replace("%Monstre.XPMin%","0");		    	  
			      Result=Result.replace("%Monstre.XPMax%","0");		    	  
			      Result=Result.replace("%Monstre.GoldMin%","0");		    	  
			      Result=Result.replace("%Monstre.GoldMax%","0");		    	  		    	  
		      }
    	  }
    	  return Result;//Renvoie la formule avec les variables remplacés correctement
    }
    
	
	

}
