package org.myphd.parser;

import java.util.List;

public class CompareImplications {
	
	public static void main(String[] args) {
		ImplicationParser parser = new ImplicationParser();
		
		
		List<Data> toCompare = parser.parseZ3("golden.txt");
		List<Data> golden = parser.parse("mo_output.csv");
		
		CompareImplications comparison = new CompareImplications();
		comparison.compare(toCompare, golden);
	}

	public void compare(List<Data> toCompare, List<Data> golden) {
		int equal = 0;
		int missing = 0;
		int extra = 0;
		int completeness = 0;
		
		for (Data dataCompare : toCompare) {
			for (Data dataGolden : golden) {
				if (dataCompare.isTheSame(dataGolden)) {
					equal++;
				}
			}
		}
		
		extra = toCompare.size() - equal;
		missing = golden.size() - equal;
		
		if(equal == 0){
			completeness = 0;
		}
		else{
			completeness = golden.size() / equal;
		}
		
		System.out.println("Completeness: " + completeness);
		System.out.println("Extra: " + extra);
		System.out.println("Missing: " + missing);
		
	}
}

