import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.CoreMap;


public class nerPOSCombined {
	public static ArrayList<gramResult> gramResultList=new ArrayList<gramResult>();
	public static final int docsToRead=200;
	
	public static void Main(String[] args) throws IOException
	{	 
		Properties props = new Properties();
	     boolean useRegexner = true;
	     if (useRegexner) {
	       props.put("annotators", "tokenize, ssplit, pos, lemma, ner, regexner");
	      // props.put("regexner.mapping", "locations.txt");
	     } else {
	       props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
	     }
	     StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	     //POS tagger
	     MaxentTagger tagger = new MaxentTagger(
	             "english-left3words-distsim.tagger");
	     
	     Path filepath = Paths.get("-----filepath-----");
	     BufferedReader reader = new BufferedReader(new FileReader(filepath.toString()));
	     String line = null;
	     int numberLines=0;
		while ((line = reader.readLine()) != null&&numberLines<=docsToRead)
		{
			numberLines=numberLines+1;
			Multimap<String, String> myMultimap = ArrayListMultimap.create();
			 ArrayList<String> myPhraseList = new ArrayList<String>();
			 
				Annotation document = new Annotation(line);
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
		            	 myMultimap.put(prevNeToken,sb.toString());
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
		        	   myMultimap.put(prevNeToken,sb.toString());
		        	 //  System.out.println(prevNeToken+" "+sb+" ");
		        	   sb.setLength(0);
		             newToken = true;
		           }
		           prevNeToken = currNeToken;
		         }
		       }
		       /* -------POS -------*/
		       String tagged = tagger.tagString(line);
		       boolean found=false;
		       String[] wordsTags=tagged.split(" ");
		       StringBuilder sbPhrase=new StringBuilder();
		       for(String wordTag:wordsTags)
		       {
		      	 if(wordTag.contains("_NN"))
		      	 {
		      		 String[] separatedWord=wordTag.split("_");
		      		 sbPhrase.append(separatedWord[0]).append(" "); 
		      		 found=true;
		      	 }
		      	 else
		      	 {
		      		 if(found)
		      		 {
		      			sbPhrase.setLength(sbPhrase.length()-1);
		      			myPhraseList.add(sbPhrase.toString()); 
		      		 }
		      		 found=false;
		      		 sbPhrase.setLength(0);	 
		      	 }
		       }
		       gramResult gramResultObject=new gramResult(myMultimap,myPhraseList);
		       gramResultList.add(gramResultObject);
		       
		} //while each sentence
		reader.close();
		
		for(gramResult g:gramResultList)
		{
			Multimap<String, String> m=g.nerTagMap;
			ArrayList<String> a=g.nounPhraseList;
			System.out.println("---------------------------------new line-------------------------------------");
			for(Entry<String, String> entry:m.entries())
			{
				System.out.println(entry.getKey()+" - "+entry.getValue());
			}
			for(String nounp:a)
			{
				System.out.println(nounp);
			}
		}
		
	}//main
}//class
