package experiment.models.tests;

public class CoverageData {
	public static final String TOTAL = "Total";
	public static final String COVERED = "Covered";
	public static final String UNREACHABLE = "Unreachable";
	public static final String UNCOVERED = "Uncovered";
	public static final String PERCENT = "Percent";
	
	private int total = 0;
	private int covered = 0;
	private int unreachable = 0;
	private int uncovered = 0;
	private double percent = 0;
	private String name = "";
	
	public CoverageData() {
		// TODO Auto-generated constructor stub
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getCovered() {
		return covered;
	}

	public void setCovered(int covered) {
		this.covered = covered;
	}

	public int getUnreachable() {
		return unreachable;
	}

	public void setUnreachable(int unreachable) {
		this.unreachable = unreachable;
	}

	public int getUncovered() {
		return uncovered;
	}

	public void setUncovered(int uncovered) {
		this.uncovered = uncovered;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		String coverageString = TOTAL + ": " + total + " " + COVERED + ": " + covered + " " + UNCOVERED + ": " + uncovered + " " + UNREACHABLE + ": " + unreachable + " " + PERCENT + ": " + percent;
		return coverageString;
	}
	
}


