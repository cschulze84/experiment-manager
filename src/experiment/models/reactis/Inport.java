package experiment.models.reactis;

public class Inport extends ReactisPort{
	private String data;
	private boolean configuration = false;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public void setConfiguration(boolean configuration) {
		this.configuration = configuration;
	}
	
	public boolean isConfiguration() {
		return configuration;
	}
}
