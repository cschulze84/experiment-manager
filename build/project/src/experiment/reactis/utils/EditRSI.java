package experiment.reactis.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import application.FileIoService;
import experiment.models.invariants.Implication;
import experiment.models.invariants.ImplicationValue;
import experiment.models.reactis.Inport;
import experiment.models.reactis.Outport;
import experiment.models.reactis.ReactisData;
import experiment.models.reactis.ReactisPort;
import experiment.models.tests.TestSuite;

public class EditRSI {

	private static final String MODEL_NAME_ANCHOR = "$MODEL_NAME$";
	private static final String ASSERTION_HEADER = "[Predicates:$MODEL_NAME$]";
	private static final String ASSERTION_FOOTER = "[Predicates]"
			+ System.lineSeparator() + "Systems=$MODEL_NAME$";
	
	public static void main(String[] args) {
		EditRSI read = new EditRSI();

		// read.rsiParser(".//Emergency_Blinking.rsi");

	}
	
	public void addAssertions(String source, String target, TestSuite testSuite, ReactisData reactisData, boolean prune){
		int x=0,y=0;
		FileIoService fileIoService = new FileIoService();
		
		fileIoService.copyFile(source, target);
		
		List<Implication> implications;
		
		if(prune){
			implications = testSuite.getImplicationsPruned();
		}
		else{
			implications = testSuite.getImplications();
		}
		
		if(implications == null || implications.isEmpty()){
			return;
		}
		
		File rsiFile = new File(target);
		String names = "Names=";
		String assertionBaseName = "mm";
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter(rsiFile, true));
			
			writer.write(ASSERTION_HEADER.replace(MODEL_NAME_ANCHOR, reactisData.getModelNameASCII()) + System.lineSeparator());
			
			int i = 0;
			for (Implication implication : implications) {
				String assertionName= assertionBaseName + i;
				
				if(i==0){
					names+= assertionName;
				}
				else{
					names+= "," + assertionName;
				}
				i++;
				writer.write(assertionName + ".expressionW=" + getExpressionW(implication, reactisData) + System.lineSeparator() + System.lineSeparator());
				writer.write(assertionName + ".connect="+ reactisData.getConnect() + System.lineSeparator());
				writer.write(assertionName + ".implType=PredicateSLExpr" + System.lineSeparator());
				writer.write(assertionName + ".type=assertion" + System.lineSeparator());
				writer.write(assertionName + ".x="+ x + ".000000" + System.lineSeparator());
				writer.write(assertionName + ".y="+ y + ".000000" + System.lineSeparator());
				writer.write(assertionName + ".w=15.000000" + System.lineSeparator());
				writer.write(assertionName + ".h=15.000000"+ System.lineSeparator());
				writer.write(assertionName + ".enable=1"+ System.lineSeparator());
				writer.write(assertionName + ".expression=" +  getExpression(implication, reactisData) + System.lineSeparator() + System.lineSeparator());
				
				
				x += 30;
				if (x > 600){
					x = 0;
					y -= 30;
				}
							
			}
			writer.write(System.lineSeparator() + names + System.lineSeparator());
			writer.write(System.lineSeparator() + ASSERTION_FOOTER.replace(MODEL_NAME_ANCHOR, reactisData.getModelNameASCII()) + System.lineSeparator());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getExpression(Implication implication,
			ReactisData reactisData) {
		String returnString = new String();
		boolean first = true;
		
		returnString += "!(";
		for (ImplicationValue value : implication.getInputs()) {
			if (first) {
				returnString += reactisData.getExpression(value.getName()) + " == " + value.getValue();
				first = false;
			} else {
				returnString += " && " + reactisData.getExpression(value.getName()) + " == " + value.getValue();;
			}
		}

		returnString += ") || ";
		
		if(implication.getOutputs().size() > 1){
			returnString += "(";
		}

		first = true;
		for (ImplicationValue value : implication.getOutputs()) {
			if (first) {
				returnString += reactisData.getExpression(value.getName()) + " == " + value.getValue();
				first = false;
			} else {
				returnString += " && " + reactisData.getExpression(value.getName()) + " == " + value.getValue();;
			}
		}
		if(implication.getOutputs().size() > 1){
			returnString += ")";
		}

		return StringProcessor.getReactisASCII(returnString);
	}

	private String getExpressionW(Implication implication,
			ReactisData reactisData) {
		String expressionW = StringProcessor.getReactisASCII(implication.reactisExport());
		return expressionW;
	}

	public ReactisData rsiParser(ReactisData data, String rsiFile) {

		try {
			File file = new File(rsiFile);

			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			FileReader fr = new FileReader(file);
			BufferedReader reader = new BufferedReader(fr);

			String strLine;

			// Get Outports
			while ((strLine = reader.readLine()) != null) {
				if (strLine.startsWith("Names=")) {
					parseOutports(strLine.replace("Names=", "").trim(), data);
					break;
				}
			}
			// Close the input stream
			reader.close();

			// Get Inports
			parseInports(properties, data);

			parseAssertion(properties, data);
			
			parseName(properties, data);

			return data;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private void parseName(Properties properties, ReactisData data) {
		String modelNameAscii = properties.getProperty("Systems", "");
		data.setModelName(StringProcessor.replaceStringFromReactis(modelNameAscii));
		data.setModelNameASCII(modelNameAscii);
	}

	private void parseAssertion(Properties properties, ReactisData data) {
		data.setConnect(properties.getProperty("assertion1.connect", ""));

		String connectionArray[] = data.getConnect().split(",");

		for (String connectionStringPart : connectionArray) {
			String connectionStringArray[] = connectionStringPart.split("=");

			ReactisPort port = data.getPortByName(connectionStringArray[0]
					.trim());
			if (port == null) {
				System.out.println("Port Not Found: DEBUG!");

				for (ReactisPort portData : data.getInports()) {
					System.out.println(portData.toString());
				}

				for (ReactisPort portData : data.getOutports()) {
					System.out.println(portData.toString());
				}
				System.out.println(connectionStringArray[0]);
			} else {
				port.setAlias(StringProcessor.replaceStringFromReactis(connectionStringArray[2]));
			}

		}
	}

	private void parseOutports(String outportsString, ReactisData data) {
		String outportsArray[] = outportsString.split(",");

		for (String outportString : outportsArray) {
			Outport outport = new Outport();
			outport.setName(StringProcessor
					.replaceStringFromReactis(outportString));
			outport.setNameASCII(outportString);
			data.getOutports().add(outport);
		}
	}

	private void parseInports(Properties properties, ReactisData data) {
		String inportsArray[] = properties.getProperty("Inports").split(",");

		for (String inportString : inportsArray) {
			Inport inport = new Inport();
			inport.setName(StringProcessor
					.replaceStringFromReactis(inportString));
			inport.setNameASCII(inportString);
			inport.setData(properties.getProperty(inport.getNameASCII(), ""));

			data.getInports().add(inport);
		}

	}

	public String cleanAssertions(String originalRSI, String newRSI) {
		File originalFile = new File(originalRSI);
		File newFile = new File(newRSI);
		String emptyRSIFileData = "";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					originalFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
			// Get Outports
			String strLine;
			String end = ASSERTION_HEADER.replace("$MODEL_NAME$]", "");
			while ((strLine = reader.readLine()) != null) {
				if(strLine.startsWith(end)){
					break;
				}
				writer.write(strLine + System.lineSeparator());
				emptyRSIFileData += strLine + System.lineSeparator();
			}
			reader.close();
			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return emptyRSIFileData;
	}

}
