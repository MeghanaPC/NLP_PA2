import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
public class NerTagging {
	
	   public static void main(String[] args) {
	    
		  HashMap<String,String> nerMap=new HashMap<String,String>();
	     //creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and
	     Properties props = new Properties();
	     boolean useRegexner = true;
	     if (useRegexner) {
	       props.put("annotators", "tokenize, ssplit, pos, lemma, ner, regexner");
	      // props.put("regexner.mapping", "locations.txt");
	     } else {
	       props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
	     }
	     StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	  /*  String[] tests =
	         {
	             "Partial invoice (€100,000, so roughly 40%) for the consignment C27655 we shipped on 15th August to London from the Make Believe Town depot. INV2345 is for the balance.. Customer contact (Sigourney) says they will pay this on the usual credit terms (30 days)."
	         }; */
	    String s ="HOCKEY PLAYERS LOS ANGELES KINGS HOCKEY TEAM GRETZKY WAYNE";

	    // for (String s : tests) {

	       Annotation document = new Annotation(s);
	       pipeline.annotate(document);

	       List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	       StringBuilder sb = new StringBuilder();
	       
	       for (CoreMap sentence : sentences) {
	         // traversing the words in the current sentence, "O" is a sensible default to initialise
	         // tokens to since we're not interested in unclassified / unknown things..
	         String prevNeToken = "O";
	         String currNeToken = "O";
	         boolean newToken = true;
	         for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
	           currNeToken = token.get(NamedEntityTagAnnotation.class);
	           String word = token.get(TextAnnotation.class);
	           // Strip out "O"s completely, makes code below easier to understand
	           if (currNeToken.equals("O")) {
	             // LOG.debug("Skipping '{}' classified as {}", word, currNeToken);
	             if (!prevNeToken.equals("O") && (sb.length() > 0)) {
	               //handleEntity(prevNeToken, sb, tokens);
	            	 nerMap.put(sb.toString(), prevNeToken);
	            	// System.out.println(prevNeToken+" "+sb+" ");
	            	 sb.setLength(0);
	               newToken = true;
	             }
	             continue;
	           }

	           if (newToken) {
	             prevNeToken = currNeToken;
	             newToken = false;
	             sb.append(word);
	             continue;
	           }

	           if (currNeToken.equals(prevNeToken)) {
	             sb.append(" " + word);
	           } else {
	             // We're done with the current entity - print it out and reset
	        	   nerMap.put(sb.toString(), prevNeToken);
	        	 //  System.out.println(prevNeToken+" "+sb+" ");
	        	   sb.setLength(0);
	             newToken = true;
	           }
	           prevNeToken = currNeToken;
	         }
	       }
	       
	   
	     
	     
	     for(Entry<String, String> e:nerMap.entrySet())
	     {
	    	 System.out.println(e.getKey()+" = "+e.getValue());
	     }
	   }//main
	 
}

