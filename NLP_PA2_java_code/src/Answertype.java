import java.util.regex.*;

public class Answertype {
 
	public static String findAnswerType(String question,String taggedQuestion){
		//This method takes a question String as input and returns Answer type
		boolean properPresent=false;
		if(taggedQuestion.contains("_NNP"))
			properPresent=true;
		
		 
		if(Pattern.compile("what",Pattern.CASE_INSENSITIVE).matcher(question).find())
		{	
			if(Pattern.compile("(year|date)",Pattern.CASE_INSENSITIVE).matcher(question).find())
				return "DATE";
			else if(Pattern.compile("(city|state|area|country|location|province|district|territory|region|continent|place)",Pattern.CASE_INSENSITIVE).matcher(question).find())
				return "LOCATION";
			else if(Pattern.compile("name",Pattern.CASE_INSENSITIVE).matcher(question).find())
				return "PROPERNOUN";
			else if(properPresent)
				return "PROPERNOUN";
			
			return "NOUNPHRASE";
		}
		else if(Pattern.compile("where",Pattern.CASE_INSENSITIVE).matcher(question).find())
		{
			return "LOCATION";
		}
		else if(Pattern.compile("who",Pattern.CASE_INSENSITIVE).matcher(question).find())
		{
			return "PERSON";
		}
		else if(Pattern.compile("when",Pattern.CASE_INSENSITIVE).matcher(question).find())
		{
			return "DATE";
		}
		else if(Pattern.compile("name",Pattern.CASE_INSENSITIVE).matcher(question).find())
		{
			return "PROPERNOUN";
		}
		else if(Pattern.compile("how (many|much)",Pattern.CASE_INSENSITIVE).matcher(question).find())
		{
			return "NUMBER";
		}
		
		//commit
		
		return "NOUNPHRASE";   //removed no match --trupti
	}
	
	//hello
	public static void main(String[] args){
		//Test harness
		//System.out.println(findAnswerType("is what country cat mat?","in_PP what_PP country"));
	}
	
}
