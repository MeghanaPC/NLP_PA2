import java.util.regex.*;

public class Answertype {
 
	public static String findAnswerType(String question){
		//This method takes a question String as input and returns Answer type
		if(Pattern.compile("what",Pattern.CASE_INSENSITIVE).matcher(question).find())
		{	
			if(Pattern.compile("year",Pattern.CASE_INSENSITIVE).matcher(question).find())
				return "DATE";
			else if(Pattern.compile("(city|state|area|country|location|continent|place)",Pattern.CASE_INSENSITIVE).matcher(question).find())
				return "LOCATION";
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
			return "NOUNPHRASE";
		}
		else if(Pattern.compile("how (many|much)",Pattern.CASE_INSENSITIVE).matcher(question).find())
		{
			return "NUMBER";
		}
		
		
		
		return "NOUNPHRASE";   //removed no match --trupti
	}
	
	public static void main(String[] args){
		//Test harness
		System.out.println(findAnswerType("is what country cat mat?"));
	}
	
}
