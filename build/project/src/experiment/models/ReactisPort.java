package experiment.models;

public class ReactisPort {
	private String name = new String();
	private String nameASCII = new String();
	private String wAlias = new String();
	private String connectAlias = new String();

	public String getName() {
		return name;
	}

	public String getNameASCII() {
		return nameASCII;
	}

	public void setNameASCII(String nameASCII) {
		this.nameASCII = nameASCII;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExpressionAlias() {
		return wAlias;
	}

	public void setAlias(String wAlias) {
		this.wAlias = wAlias;
	}

	public String getConnectAlias() {
		return connectAlias;
	}

	public void setConnectAlias(String connectAlias) {
		this.connectAlias = connectAlias;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
