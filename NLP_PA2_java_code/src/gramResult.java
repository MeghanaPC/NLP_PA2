import java.util.ArrayList;

import com.google.common.collect.Multimap;

public class gramResult {
	  public Multimap<String, String> nerTagMap;
	  public ArrayList<String> nounPhraseList;
	  public ArrayList<String> properNounPhraseList;
	 // public ArrayList<String> dateList;
	 // public ArrayList<String> numberList;
	 
	  gramResult()
	  {
		  
	  }
	  gramResult(Multimap<String, String> myMultimap,ArrayList<String> phrasemap,ArrayList<String> nounphrasemap)
	  {
		  nerTagMap=myMultimap;
		  nounPhraseList=phrasemap;
		  properNounPhraseList=nounphrasemap;
		 // dateList=mydateList;
		 // numberList=mynumberList;
		  
	  }
	 
}
