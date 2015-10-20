package org.myphd.parser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PruneImplications {
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		ImplicationParser parser = new ImplicationParser();
		List<Data> toPrune = parser.parse("mo_output.csv");
		PruneImplications pruning = new PruneImplications();
		pruning.prune(toPrune);
		long endTime = System.nanoTime();

		long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
		System.out.println(duration/1000000);
	}

	private List<Data> prune(List<Data> toPrune) {
		List<Data> toKeep = new ArrayList<>();
		List<Data> removed = new ArrayList<>();
		
		while(toPrune.size()>0){
			Data data = toPrune.remove(toPrune.size()-1);
			boolean keep = true;
			for (Data dataToCompare : toPrune) {
				if(dataToCompare.isSubsetOf(data)){
					keep = false;
					break;
				}
			}
			if(keep){
				toKeep.add(data);
			}
			/*else{
				removed.add(data);
			}*/
		}
		
		FileWriter fw;
		try {
			fw = new FileWriter("out.txt");
			
			fw.write("TO KEEP\n");
			
			for (Data data : toKeep) {
				fw.write(data.toString() + "\n");
			}
			
			fw.write("\nTO REMOVE\n");
			for (Data data : removed) {
				fw.write(data.toString() + "\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
		
		
		
		
		return toKeep;
	}
}
