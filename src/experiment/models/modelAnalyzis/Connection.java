package experiment.models.modelAnalyzis;

import java.util.ArrayList;
import java.util.List;

public class Connection {
	String name = new String();
	List<Connection> connectedTo = new ArrayList<>();
	List<Integer> aliases = new ArrayList<>();
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Connection> getConnectedTo() {
		return connectedTo;
	}
	
	public List<Integer> getAliases() {
		return aliases;
	}
}
