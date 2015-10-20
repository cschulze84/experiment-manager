package experiment.models.invariants;

import java.util.HashSet;
import java.util.Set;


public class ImplicationValue {
	private String name = new String();
	private String value = new String();
	private double highest = 0;
	private double lowest = 0;
	private Set<String> values = new HashSet<>();
	
	private boolean highestUsed = false;
	private boolean lowestUsed = false;
	
	
	
	public Set<String> getValues() {
		return values;
	}
	
	public double getHighest() {
		return highest;
	}
	
	public void setHighest(double highest) {
		highestUsed = true;
		this.highest = highest;
	}
	
	public double getLowest() {
		return lowest;
	}
	
	public void setLowest(double lowest) {
		lowestUsed = true;
		this.lowest = lowest;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean isNumeric(){
		try {
			double valueDouble = Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public double getNumeric(){
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return Double.MIN_VALUE;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		
		return getName().equals(((ImplicationValue)obj).getName());
	}

	public boolean equalsComplete(ImplicationValue value) {
		if(this.equals(value)){
			if(this.getValue().equals(value.getValue())){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String resultString = new String();
		resultString += getName() + " == " + getValue();	
		return resultString;
	}

	public void process(ImplicationValue value) {
		if(value.isNumeric()){
			if(getHighest() <= value.getValueDouble()){
				setHighest(value.getValueDouble());
			}
			if(getLowest() >= value.getValueDouble()){
				setLowest(value.getValueDouble());
			}
		}
		else{
			values.add(value.getValue());
		}
		
	}

	private double getValueDouble() {
		return Double.parseDouble(value);
	}

	public String getPartitionString() {
		String resultString = "";
		if(isNumeric()){
			if(getLowest() == getHighest()){
				return toString();
			}
			else{
				if(highestUsed && lowestUsed){
					resultString += getName() + "= [" + getLowest() + " , " + getHighest() + "]";
				}
				else{
					return toString();
				}
				
			}
			
		}
		else{
			resultString += getName() + "=";
			boolean first = true;
			for (String string : values) {
				if(first){
					resultString += string;
					first = false;
				}
				else{
					resultString += " | " + string;
				}
				
			}
		}	
		return resultString;
	}

	public String getCodeString() {
		if(isNumeric()){
			return toCodeString();
			
		}
		else{
			return toCodeString();
		}	
	}

	public String getCodeStringRange() {
		String resultString = "";
		if(isNumeric()){
			if(getLowest() == getHighest()){
				return toCodeString();
			}
			else{
				if(highestUsed && lowestUsed){
					resultString += "(" + getName()  + " >= " + getLowest() + " && " + getName() + " <= " + getHighest() + ")";
				}
				else{
					return toCodeString();
				}
				
			}
			
		}
		else{
			resultString += "(";
			boolean first = true;
			for (String string : values) {
				if(first){
					resultString += getName() + " = " + string;
					first = false;
				}
				else{
					resultString += " || " + getName() + " = " + string;
				}
				
			}
		}	
		return resultString;
	}

	private String toCodeString() {
		String resultString = new String();
		resultString += getName() + "==" + getValue();	
		return resultString;
	}

	public static ImplicationValue parse(String valueString) {
		String[] nameValueArray = valueString.split("=");
		if (nameValueArray.length != 2) {
			return null;
		}
		ImplicationValue value = new ImplicationValue();

		value.setName(nameValueArray[0].trim());
		value.setValue(nameValueArray[1].trim());
		return value;
	}

}
