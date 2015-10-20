package experiment.models;

import java.util.HashMap;
import java.util.Map;

public enum StoppingCriterion {
	 NO_INVARIANTS("No Invariants"),  NUMBER_OF_ITERATIONS("Number of Iterations");
	    
	    private final String abbreviation;
	    // Reverse-lookup map for getting a day from an abbreviation
	    private static final Map<String, StoppingCriterion> lookup = new HashMap<String, StoppingCriterion>();
	    static {
	        for (StoppingCriterion d : StoppingCriterion.values())
	            lookup.put(d.getAbbreviation(), d);
	    }

	    private StoppingCriterion(String abbreviation) {
	        this.abbreviation = abbreviation;
	    }

	    public String getAbbreviation() {
	        return abbreviation;
	    }

	    public static StoppingCriterion get(String abbreviation) {
	        return lookup.get(abbreviation);
	    }
	    
	    @Override
	    public String toString() {
	    	return abbreviation;
	    }
}
