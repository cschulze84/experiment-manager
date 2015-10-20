package experiment.models;

import java.util.HashMap;
import java.util.Map;

public enum ExecutionOrder {
	 PARALLEL("Parallel"),  SEQUENTIAL("Sequential");
	    
	    private final String abbreviation;
	    // Reverse-lookup map for getting a day from an abbreviation
	    private static final Map<String, ExecutionOrder> lookup = new HashMap<String, ExecutionOrder>();
	    static {
	        for (ExecutionOrder d : ExecutionOrder.values())
	            lookup.put(d.getAbbreviation(), d);
	    }

	    private ExecutionOrder(String abbreviation) {
	        this.abbreviation = abbreviation;
	    }

	    public String getAbbreviation() {
	        return abbreviation;
	    }

	    public static ExecutionOrder get(String abbreviation) {
	        return lookup.get(abbreviation);
	    }
	    
	    @Override
	    public String toString() {
	    	return abbreviation;
	    }
}