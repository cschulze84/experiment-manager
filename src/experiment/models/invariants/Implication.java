package experiment.models.invariants;

import java.util.ArrayList;
import java.util.List;


public class Implication implements Comparable<Implication> {
	private List<ImplicationValue> inputs = new ArrayList<>();
	private List<ImplicationValue> outputs = new ArrayList<>();

	public void setInputs(List<ImplicationValue> inputs) {
		this.inputs = inputs;
	}

	public List<ImplicationValue> getInputs() {
		return inputs;
	}

	public void setOutputs(List<ImplicationValue> outputs) {
		this.outputs = outputs;
	}

	boolean equalRules(Implication obj) {
		for (ImplicationValue value : inputs) {
			if (!obj.containsInputs(value)) {
				return false;
			}
		}

		for (ImplicationValue value : outputs) {
			if (value.isNumeric()) {
				if (!obj.containsOutputs(value)) {
					return false;
				}
			} else {
				return obj.equalsOutputs(value);
			}

		}

		return true;

	}
	

	private boolean equalsOutputs(ImplicationValue value) {
		for (ImplicationValue valueHere : outputs) {
			if (valueHere.equalsComplete(value)) {
				return true;
			}
		}
		return false;
	}

	private boolean containsInputs(ImplicationValue value) {
		for (ImplicationValue valueHere : inputs) {
			if (valueHere.equals(value)) {
				return true;
			}
		}
		return false;
	}

	private boolean containsOutputs(ImplicationValue value) {
		for (ImplicationValue valueHere : outputs) {
			if (valueHere.equals(value)) {
				return true;
			}
		}
		return false;
	}

	public String reactisExport(){
		String returnString = new String();
		boolean first = true;
		
		returnString += "!(";
		for (ImplicationValue value : inputs) {
			if (first) {
				returnString += value.toString();
				first = false;
			} else {
				returnString += " && " + value.toString();
			}
		}

		returnString += ") || ";
		
		if(outputs.size() > 1){
			returnString += "(";
		}

		first = true;
		for (ImplicationValue value : outputs) {
			if (first) {
				returnString += value.toString();
				first = false;
			} else {
				returnString += " && " + value.toString();
			}
		}
		if(outputs.size() > 1){
			returnString += ")";
		}

		return returnString;
	}

	@Override
	public String toString() {
		String returnString = new String();
		boolean first = true;
		for (ImplicationValue value : inputs) {
			if (first) {
				returnString += value.toString();
				first = false;
			} else {
				returnString += " & " + value.toString();
			}
		}

		returnString += " -> ";

		first = true;
		for (ImplicationValue value : outputs) {
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
		if (obj instanceof Implication) {
			return equalRules((Implication) obj);
		}
		return false;
	}
	
	public boolean isTheSame(Implication data){
		if(data == null){
			return false;
		}
		if(inputs.size() != data.inputs.size() && outputs.size() != data.outputs.size()){
			return false;
		}
		
		for (ImplicationValue inputValueToCompare : inputs) {
			boolean found = false;
			for (ImplicationValue value : data.inputs) {
				if(inputValueToCompare.equalsComplete(value)){
					found =  true;
				}
			}
			if(!found){
				return false;
			}
		}
		for (ImplicationValue outputValueToCompare : outputs) {
			boolean found = false;
			for (ImplicationValue value : data.outputs) {
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
		for (ImplicationValue value : inputs) {
			hashString += " " + value.getName();
		}

		for (ImplicationValue value : outputs) {
			if (value.isNumeric()) {
				hashString += " " + value.getName();
			} else {
				hashString += value.toString();
			}
		}
		return hashString;
	}

	public void process(Implication data) {
		for (ImplicationValue value : data.getInputs()) {
			ImplicationValue processValue = this.getValueByName(value.getName());
			if (processValue == null) {
				continue;
			}
			processValue.process(value);
		}
		for (ImplicationValue value : data.getOutputs()) {
			ImplicationValue processValue = this.getValueByName(value.getName());
			if (processValue == null) {
				continue;
			}
			processValue.process(value);
		}

	}

	public List<ImplicationValue> getOutputs() {
		return outputs;
	}

	private ImplicationValue getValueByName(String name) {
		for (ImplicationValue value : inputs) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		for (ImplicationValue value : outputs) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		return null;
	}

	public String getPartitionString() {
		String returnString = new String();
		boolean first = true;
		for (ImplicationValue value : inputs) {
			if (first) {
				returnString += value.getPartitionString();
				first = false;
			} else {
				returnString += " & " + value.getPartitionString();
			}
		}

		returnString += " -> ";

		first = true;
		for (ImplicationValue value : outputs) {
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
	public int compareTo(Implication data) {
		for (ImplicationValue value : inputs) {
			ImplicationValue compareValue = data.getValueByName(value.getName());
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
		for (ImplicationValue value : inputs) {
			if (first) {
				returnString += value.getCodeStringRange();
				first = false;
			} else {
				returnString += " && " + value.getCodeStringRange();
			}
		}

		returnString += ") || ( ";

		first = true;
		for (ImplicationValue value : outputs) {
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

	public boolean isSubsetOf(Implication data) {
		if(data == null){
			return false;
		}
		if(inputs.size() >= data.inputs.size() || outputs.size() != data.outputs.size()){
			return false;
		}
		
		
		for (ImplicationValue outputValueToCompare : outputs) {
			boolean found = false;
			for (ImplicationValue value : data.outputs) {
				if(outputValueToCompare.equalsComplete(value)){
					found =  true;
				}
			}
			if(!found){
				return false;
			}
		}
		
		int numberOfOperators = inputs.size();
		
		for (ImplicationValue inputValueToCompare : inputs) {
			for (ImplicationValue value : data.inputs) {
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
	
	public String printReactis(){
		String reactisString = "!(";
		boolean first = true;
		for (ImplicationValue value : inputs) {
			if(first){
				first = false;
				reactisString += value.toString();
			}
			else{
				reactisString += " && " + value.toString();
			}
		}
		
		reactisString += ") || (";
		
		for (ImplicationValue value : outputs) {
			if(first){
				first = false;
				reactisString += value.toString();
			}
			else{
				reactisString += " && " + value.toString();
			}
		}
		
		reactisString += ")";
		
		return reactisString;
	}
}
