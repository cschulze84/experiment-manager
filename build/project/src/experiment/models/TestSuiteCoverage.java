package experiment.models;

public class TestSuiteCoverage {
	public static final String SUBSYSTEM = "Subsystem";
	public static final String BRANCH = "Branch";
	public static final String STATE = "State";
	public static final String CONDITION_ACTION = "ConditionAction";
	public static final String TRANSITION_ACTION = "TransitionAction";
	public static final String CONDITION = "Condition";
	public static final String DECISION = "Decision";
	public static final String MCDC = "MCDC";
	public static final String MCC = "MCCSummary";
	public static final String BOUNDARY = "Boundary";
	public static final String USER_TARGET = "UserTarget";
	public static final String ASSERTION = "Assertion";
	public static final String CSTATEMENT = "CStatement";
	
	
	private CoverageData Subsystem = new CoverageData();
	private CoverageData Branch = new CoverageData();
	private CoverageData State = new CoverageData();
	private CoverageData ConditionAction = new CoverageData();
	private CoverageData TransitionAction = new CoverageData();
	private CoverageData Condition = new CoverageData();
	private CoverageData Decision = new CoverageData();
	private CoverageData Mcdc = new CoverageData();
	private CoverageData Mcc = new CoverageData();
	private CoverageData Boundary = new CoverageData();
	private CoverageData UserTarget = new CoverageData();
	private CoverageData Assertion = new CoverageData();
	private CoverageData CStatement = new CoverageData();

	public TestSuiteCoverage() {
		// TODO Auto-generated constructor stub
	}

	public CoverageData getSubsystem() {
		return Subsystem;
	}

	public void setSubsystem(CoverageData subsystem) {
		Subsystem = subsystem;
	}

	public CoverageData getBranch() {
		return Branch;
	}

	public void setBranch(CoverageData branch) {
		Branch = branch;
	}

	public CoverageData getState() {
		return State;
	}

	public void setState(CoverageData state) {
		State = state;
	}

	public CoverageData getConditionAction() {
		return ConditionAction;
	}

	public void setConditionAction(CoverageData conditionAction) {
		ConditionAction = conditionAction;
	}

	public CoverageData getTransitionAction() {
		return TransitionAction;
	}

	public void setTransitionAction(CoverageData transitionAction) {
		TransitionAction = transitionAction;
	}

	public CoverageData getCondition() {
		return Condition;
	}

	public void setCondition(CoverageData condition) {
		Condition = condition;
	}

	public CoverageData getDecision() {
		return Decision;
	}

	public void setDecision(CoverageData decision) {
		Decision = decision;
	}

	public CoverageData getMCDC() {
		return Mcdc;
	}

	public void setMCDC(CoverageData Mcdc) {
		this.Mcdc = Mcdc;
	}

	public CoverageData getMCC() {
		return Mcc;
	}

	public void setMCC(CoverageData Mcc) {
		this.Mcc = Mcc;
	}

	public CoverageData getBoundary() {
		return Boundary;
	}

	public void setBoundary(CoverageData boundary) {
		Boundary = boundary;
	}

	public CoverageData getUserTarget() {
		return UserTarget;
	}

	public void setUserTarget(CoverageData userTarget) {
		UserTarget = userTarget;
	}

	public CoverageData getAssertion() {
		return Assertion;
	}

	public void setAssertion(CoverageData assertion) {
		Assertion = assertion;
	}

	public CoverageData getCStatement() {
		return CStatement;
	}

	public void setCStatement(CoverageData cStatement) {
		CStatement = cStatement;
	}

}
