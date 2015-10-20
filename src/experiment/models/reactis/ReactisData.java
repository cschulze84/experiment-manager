package experiment.models.reactis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import experiment.reactis.utils.EditRSI;

public class ReactisData {

	private String modelName = new String();
	private String modelNameASCII = new String();
	private ObservableList<ReactisPort> inports = FXCollections.observableArrayList();
	private ObservableList<ReactisPort> outports = FXCollections.observableArrayList();
	private String connect = new String();

	public ObservableList<ReactisPort> getInports() {
		return inports;
	}

	public void setInports(ObservableList<ReactisPort> inports) {
		this.inports = inports;
	}

	public ObservableList<ReactisPort> getOutports() {
		return outports;
	}

	public void setOutports(ObservableList<ReactisPort> outports) {
		this.outports = outports;
	}

	public String getConnect() {
		return connect;
	}

	public void setConnect(String connect) {
		this.connect = connect;
	}
	
	public String getModelName() {
		return modelName;
	}
	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	public String getModelNameASCII() {
		return modelNameASCII;
	}
	
	public void setModelNameASCII(String modelNameASCII) {
		this.modelNameASCII = modelNameASCII;
	}

	public ReactisPort getPortByName(String portName) {
		for (ReactisPort inport : inports) {
			if(portName.equalsIgnoreCase(inport.getName())){
				return inport;
			}
		}
		
		for (ReactisPort outport : outports) {
			if(portName.equalsIgnoreCase(outport.getName())){
				return outport;
			}
		}
		
		return null;
		
	}

	public void loadData(String rsiFile) {
		EditRSI readRsi = new EditRSI();
		readRsi.rsiParser(this, rsiFile);
	}

	public void switchPortToOutput(ReactisPort port) {
		outports.add(port);
		inports.remove(port);
		
	}

	public void switchPortToInput(ReactisPort port) {
		inports.add(port);
		outports.remove(port);
		
	}

	public String getExpression(String name) {
		for (ReactisPort reactisPort : inports) {
			if(reactisPort.getName().equalsIgnoreCase(name)){
				return reactisPort.getExpressionAlias();
			}
		}
		for (ReactisPort reactisPort : outports) {
			if(reactisPort.getName().equalsIgnoreCase(name)){
				return reactisPort.getExpressionAlias();
			}
		}
		return "EXPRESSION_ALIAS_NOT_FOUND_FOR: name";
	}

}
