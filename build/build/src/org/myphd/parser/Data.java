package org.myphd.parser;

import java.util.ArrayList;
import java.util.List;


public class Data implements Comparable<Data> {
	private static final String IMPLICATION = "Implies";
	private static final String OPEN_BRACKET = "(";
	private static final String CLOSE_BRACKET = ")";
	private static final String AND = "And";
	private static final String COMMA = ",";
	
	private List<Value> inputs = new ArrayList<Value>();
	private List<Value> outputs = new ArrayList<Value>();

	public void setInputs(List<Value> inputs) {
		this.inputs = inputs;
	}

	public List<Value> getInputs() {
		return inputs;
	}

	public void setOutputs(List<Value> outputs) {
		this.outputs = outputs;
	}

	boolean equalRules(Data other) {
		for (Value value : inputs) {
			if (!other.containsInputs(value)) {
				return false;
			}
		}

		for (Value value : outputs) {
			if (value.isNumeric()) {
				if (!other.containsOutputs(value)) {
					return false;
				}
			} else {
				return other.equalsOutputs(value);
			}

		}

		return true;

	}
	

	private boolean equalsOutputs(Value value) {
		for (Value valueHere : outputs) {
			if (valueHere.equalsComplete(value)) {
				return true;
			}
		}
		return false;
	}

	private boolean containsInputs(Value value) {
		for (Value valueHere : inputs) {
			if (valueHere.equals(value)) {
				return true;
			}
		}
		return false;
	}

	private boolean containsOutputs(Value value) {
		for (Value valueHere : outputs) {
			if (valueHere.equals(value)) {
				return true;
			}
		}
		return false;
	}

	public static Data parse(String line) {
		String[] inOutPutArray = line.split("->");

		if (inOutPutArray.length != 2) {
			return null;
		}
		Data data = new Data();

		data.setInputs(parseValues(inOutPutArray[0]));

		data.setOutputs(parseValues(inOutPutArray[1]));

		return data;
	}

	private static List<Value> parseValues(String valuesString) {
		List<Value> values = new ArrayList<>();
		String[] valueArray = valuesString.split("&");

		for (String valueString : valueArray) {
			valueString = valueString.trim();
			if (valueString.isEmpty()) {
				continue;
			}

			String[] nameValueArray = valueString.split("==");
			if (nameValueArray.length != 2) {
				continue;
			}
			Value value = new Value();

			value.setName(nameValueArray[0].trim());
			value.setValue(nameValueArray[1].trim());
			values.add(value);

		}
		return values;
	}

	@Override
	public String toString() {
		String returnString = new String();
		boolean first = true;
		for (Value value : inputs) {
			if (first) {
				returnString += value.toString();
				first = false;
			} else {
				returnString += " & " + value.toString();
			}
		}

		returnString += " -> ";

		first = true;
		for (Value value : outputs) {
			if (first) {
				returnString += value.toString();
				first = false;
			} else {
				returnString += " & " + value.toString();
			}
		}

		return returnString;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Data) {
			return equalRules((Data) obj);
		}
		return false;
	}
	
	public boolean isTheSame(Data data){
		if(data == null){
			return false;
		}
		if(inputs.size() != data.inputs.size() && outputs.size() != data.outputs.size()){
			return false;
		}
		
		for (Value inputValueToCompare : inputs) {
			boolean found = false;
			for (Value value : data.inputs) {
				if(inputValueToCompare.equalsComplete(value)){
					found =  true;
				}
			}
			if(!found){
				return false;
			}
		}
		for (Value outputValueToCompare : outputs) {
			boolean found = false;
			for (Value value : data.outputs) {
				if(outputValueToCompare.equalsComplete(value)){
					found =  true;
				}
			}
			if(!found){
				return false;
			}
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		String hashValue = getHashValue();
		return hashValue.hashCode();
	}

	private String getHashValue() {
		String hashString = "";
		for (Value value : inputs) {
			hashString += " " + value.getName();
		}

		for (Value value : outputs) {
			if (value.isNumeric()) {
				hashString += " " + value.getName();
			} else {
				hashString += value.toString();
			}
		}
		return hashString;
	}

	public void process(Data data) {
		for (Value value : data.getInputs()) {
			Value processValue = this.getValueByName(value.getName());
			if (processValue == null) {
				continue;
			}
			processValue.process(value);
		}
		for (Value value : data.getOutputs()) {
			Value processValue = this.getValueByName(value.getName());
			if (processValue == null) {
				continue;
			}
			processValue.process(value);
		}

	}

	private List<Value> getOutputs() {
		return outputs;
	}

	private Value getValueByName(String name) {
		for (Value value : inputs) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		for (Value value : outputs) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		return null;
	}

	public String getPartitionString() {
		String returnString = new String();
		boolean first = true;
		for (Value value : inputs) {
			if (first) {
				returnString += value.getPartitionString();
				first = false;
			} else {
				returnString += " & " + value.getPartitionString();
			}
		}

		returnString += " -> ";

		first = true;
		for (Value value : outputs) {
			if (first) {
				returnString += value.getPartitionString();
				first = false;
			} else {
				returnString += " & " + value.getPartitionString();
			}
		}

		return returnString;
	}

	@Override
	public int compareTo(Data data) {
		for (Value value : inputs) {
			Value compareValue = data.getValueByName(value.getName());
			if (value.isNumeric()) {
				int compare = Double.compare(value.getNumeric(),
						compareValue.getNumeric());
				if (compare == 0) {
					continue;
				}
				return compare;

			} else {
				int compare = value.getValue().compareTo(
						compareValue.getValue());
				if (compare == 0) {
					continue;
				}
				return compare;
			}
		}
		return 0;
	}

	public String getPartitionCode() {
		String returnString = new String();
		returnString += "if(!(";
		boolean first = true;
		for (Value value : inputs) {
			if (first) {
				returnString += value.getCodeStringRange();
				first = false;
			} else {
				returnString += " && " + value.getCodeStringRange();
			}
		}

		returnString += ") || ( ";

		first = true;
		for (Value value : outputs) {
			if (first) {
				returnString += value.getCodeString();
				first = false;
			} else {
				returnString += " && " + value.getCodeString();
			}
		}
		returnString += ")){\n";
		returnString += "\ti=a;\n";
		returnString += "}\nelse{\n";
		returnString += "\ti=b;\n}";

		return returnString;
	}

	public static Data parseZ3(String implicationLine) {
		Data data = new Data();
		
		if(implicationLine.startsWith(IMPLICATION)){
			implicationLine = implicationLine.substring(implicationLine.indexOf(OPEN_BRACKET) + 1);
		}
		if(implicationLine.startsWith(AND)){
			parseAndLHS(implicationLine, data);
		}
		else{
			parseNoAndLHS(implicationLine, data);
		}
		return data;
	}
	
	private static void parseAndLHS(String implicationLine, Data data) {
		implicationLine = implicationLine.substring(implicationLine.indexOf(OPEN_BRACKET));
		int closingParen = findClosingParen(implicationLine, 0);
		String lhs = implicationLine.substring(0, closingParen);
		String rhs = implicationLine.substring(closingParen);
		
		
		parseLHS(lhs, data);
		parseRHS(rhs, data);
	}

	private static void parseLHS(String lhs, Data data) {
		lhs =  removeUnwantedCharacters(lhs);
		String[] implicationPieces = lhs.split(COMMA);
		for (String piece : implicationPieces) {
			Value value = Value.parse(piece.trim());
			if(value != null){
				data.inputs.add(value);
			}
		}
	}

	private static void parseRHS(String rhs, Data data) {
		rhs = removeUnwantedCharacters(rhs);	
		String[] implicationPieces = rhs.split(COMMA);
		for (String piece : implicationPieces) {
			Value value = Value.parse(piece.trim());
			if(value != null){
				data.outputs.add(value);
			}
		}
	}
	
	private static String removeUnwantedCharacters(String stringToClean){
		return stringToClean = stringToClean.replace(AND, "").replace(OPEN_BRACKET, "").replace(CLOSE_BRACKET, "");
	}

	private static void parseNoAndLHS(String implicationLine, Data data) {
		implicationLine = removeUnwantedCharacters(implicationLine);
		String[] implicationPieces = implicationLine.split(COMMA);
		boolean first = true;
		for (String piece : implicationPieces) {
			if(first){
				Value value = Value.parse(piece.trim());
				if(value != null){
					data.inputs.add(value);
				}
				first = false;
			}
			else{
				Value value = Value.parse(piece.trim());
				if(value != null){
					data.outputs.add(value);
				}
			}
		}
	}

	public static int findClosingParen(String implicationLine, int openPos) {
	    int closePos = openPos;
	    int counter = 1;
	    while (counter > 0) {
	        char c = implicationLine.charAt(++closePos);
	        if (c == '(') {
	            counter++;
	        }
	        else if (c == ')') {
	            counter--;
	        }
	    }
	    return closePos;
	}

	public boolean isSubsetOf(Data data) {
		if(data == null){
			return false;
		}
		if(inputs.size() >= data.inputs.size() || outputs.size() != data.outputs.size()){
			return false;
		}
		
		
		for (Value outputValueToCompare : outputs) {
			boolean found = false;
			for (Value value : data.outputs) {
				if(outputValueToCompare.equalsComplete(value)){
					found =  true;
				}
			}
			if(!found){
				return false;
			}
		}
		
		int numberOfOperators = inputs.size();
		
		for (Value inputValueToCompare : inputs) {
			for (Value value : data.inputs) {
				if(inputValueToCompare.equalsComplete(value)){
					numberOfOperators--;
				}
			}
		}
		
		if(numberOfOperators>0){
			return false;
		}
		
		return true;
	}
}
