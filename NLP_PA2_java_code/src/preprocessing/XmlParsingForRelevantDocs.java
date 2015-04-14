package preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parses the top docs XML
 * @author Meghana
 *
 */
public class XmlParsingForRelevantDocs {

	/**
	 * Creates a new file with plain text of the top docs file passed as argument
	 * Loses distinction between the different documents within top docs.
	 * 
	 * @param fileToBeParsed
	 * @param resultFileName
	 * @throws IOException
	 */
	public static void xmlParse(String fileToBeParsed, String resultFileName) throws IOException {

		BufferedWriter output = new BufferedWriter(new FileWriter(
				resultFileName));
		BufferedReader reader = new BufferedReader(new FileReader(
				fileToBeParsed));
		String input = null;

		while ((input = reader.readLine()) != null) {

			if(input.contains(("Qid:"))){
				BufferedWriter writerIntermed = new BufferedWriter(new FileWriter(
						"intermedFile"));
				//get content between DOC and /DOC and write to intermed file
				while( (input = reader.readLine()) != null && !input.equals("</DOC>")) {
					writerIntermed.write(input); 
					writerIntermed.newLine();
				   }
				writerIntermed.write("</DOC>");

				writerIntermed.newLine();
				writerIntermed.close();
			}
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				Document doc = docBuilder.parse("intermedFile");

				NodeList nList = doc.getElementsByTagName("DOC");

				for (int i = 0; i < nList.getLength(); i++) {
					Node nNode = nList.item(i);
					String s = nNode.getTextContent();
					output.write(s);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		output.close();
		reader.close();
		File file = new File("intermedFile");
		file.delete();
		
	}

	public static void main(String[] args) throws IOException {

		xmlParse("top_docs.0", "resultFileName");
	}
}
