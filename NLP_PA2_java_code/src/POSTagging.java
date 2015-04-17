import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
public class POSTagging {
	 public static void main(String[] args) throws IOException,
     ClassNotFoundException {

 // Initialize the tagger
 MaxentTagger tagger = new MaxentTagger(
         "english-left3words-distsim.tagger");

 // The sample string
 String sample = "Helicopters will patrol the temporary no-fly zone around New Jersey's MetLife Stadium Sunday, with F-16s based in Atlantic City ready to be scrambled if an unauthorized aircraft does enter the restricted airspace."
 		+ "Down below, bomb-sniffing dogs will patrol the trains and buses that are expected to take approximately 30,000 of the 80,000-plus spectators to Sunday's Super Bowl between the Denver Broncos and Seattle Seahawks."
 		+ "The Transportation Security Administration said it has added about two dozen dogs to monitor passengers coming in and out of the airport around the Super Bowl."
 		+ "On Saturday, TSA agents demonstrated how the dogs can sniff out many different types of explosives. Once they do, they're trained to sit rather than attack, so as not to raise suspicion or create a panic."
 		+ "TSA spokeswoman Lisa Farbstein said the dogs undergo 12 weeks of training, which costs about $200,000, factoring in food, vehicles and salaries for trainers."
 		+ "Dogs have been used in cargo areas for some time, but have just been introduced recently in passenger areas at Newark and JFK airports. JFK has one dog and Newark has a handful, Farbstein said.";

 // The tagged string
 /* (Adjective | Noun)* (Noun Preposition)? (Adjective | Noun)* Noun 
 Zero or more adjectives or nouns, followed by an option group of a noun and a preposition, 
 followed again by zero or more adjectives or nouns, followed by a single noun */ 
 String tagged = tagger.tagString(sample);
 List<String> phraseList=new ArrayList<String>();
 boolean found=false;
 String[] wordsTags=tagged.split(" ");
 StringBuilder sb=new StringBuilder();
 for(String wordTag:wordsTags)
 {
	 if(wordTag.contains("_NN"))
	 {
		 String[] separatedWord=wordTag.split("_");
		 sb.append(separatedWord[0]).append(" "); 
		 found=true;
	 }
	 else
	 {
		 if(found)
		 {
			 sb.setLength(sb.length()-1);
			 phraseList.add(sb.toString()); 
		 }
		 found=false;
		 sb.setLength(0);	 
	 }
 }
 // Output the result
 System.out.println(tagged);
 for(String s:phraseList)
 {
	 System.out.println(s);
 }
}
}
