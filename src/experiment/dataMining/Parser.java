package experiment.dataMining;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.RuleG;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import experiment.dataMining.wrapper.AssocRuleList;
import experiment.dataMining.wrapper.RuleGList;
import experiment.models.invariants.Implication;
import experiment.models.invariants.ImplicationValue;
import experiment.models.modelAnalyzis.Connection;
import experiment.models.tests.TestCase;
import experiment.models.tests.TestData;
import experiment.models.tests.TestSuite;

public class Parser {
	private HashMap<String, Integer> alias;
	private Map<Integer, Integer> lhsAllowed;
	private Map<Integer, Integer> rhsAllowed;
	private Map<String, Connection> connected;
	
	private Map<Integer, List<Integer>> connectedAlias;
	
	public Parser(Map<String, Connection> connected) {
		this.connected = connected;
	}

	public Map<Integer, List<Integer>> getConnectedAlias() {
		return connectedAlias;
	}
	
	public String printForAssociationRuleMining(
			TestSuite testSuite) {
		String associationRuleMiningInput = new String();
		alias = new HashMap<>();
		lhsAllowed = new HashMap<>();
		rhsAllowed = new HashMap<>();
		connectedAlias = new HashMap<>();
		int ID = 1;

			int timeData = testSuite.getTimeColumn();
			// System.out.println(testSuite.getTestCases().size());
			for (TestCase tc : testSuite.getTestCases()) {
				// System.out.println(tc.getTestCaseData().size());
				for (TestData testData : tc.getTestCaseData()) {
					boolean first = true;
					int i = 0;
					for (String dataString : testData.getData()) {
						if (i == timeData) {
							i++;
							continue;
						}
						String variableName = testSuite.getHeader().get(i++);
						String variable = variableName + "=" + dataString;

						Integer id;
						if (alias.containsKey(variable)) {
							id = alias.get(variable);
						} else {
							id = ID++;
							alias.put(variable, id);
							
							Connection connection = connected.get(variableName);
							connection.getAliases().add(id);
						}
						if (first) {
							associationRuleMiningInput += Integer.toString(id);
							first = false;
						} else {
							associationRuleMiningInput += (" " + id);
						}

						if (testSuite.isInput(variableName)) {
							if (!lhsAllowed.containsKey(id)) {
								lhsAllowed.put(id, id);
							}
						} else if (testSuite.isOutput(variableName)) {
							if (!rhsAllowed.containsKey(id)) {
								rhsAllowed.put(id, id);
							}
						}
					}
					associationRuleMiningInput += System.lineSeparator();
				}
			}
			
			for (Connection connection : connected.values()) {
				List<Integer> aliasList = new ArrayList<>();
				for (Connection connectedTo : connection.getConnectedTo()) {
					aliasList.addAll(connectedTo.getAliases());
				}
				for (Integer alias : connection.getAliases()) {
					connectedAlias.put(alias, aliasList);
				}
			}

			return associationRuleMiningInput;
	}
	
	public List<Implication> parseMiningOutputs(RuleGList prunedRules) {
		List<Implication> implications = new ArrayList<>();
		
		for (RuleG rule : prunedRules.list) {
			Implication implication = parseImplicationLine(rule);
			if (implication == null) {
				continue;
			}
			implications.add(implication);
		}

		return implications;
	}
	
	private Implication parseImplicationLine(RuleG rule) {
		Implication implication = new Implication();

		for (int value : rule.getItemset1()) {
			String aliasValue = getAliasValue(value);
			ImplicationValue implicationValue = ImplicationValue
					.parse(aliasValue);
			implication.getInputs().add(implicationValue);
		}

		for (int value : rule.getItemset2()) {
			String aliasValue = getAliasValue(value);
			ImplicationValue implicationValue = ImplicationValue
					.parse(aliasValue);
			implication.getOutputs().add(implicationValue);
		}

		return implication;
	}
	

	public List<Implication> parseMiningOutputs(AssocRuleList rules) {
		List<Implication> implications = new ArrayList<>();

		for (AssocRule rule : rules.list) {
			Implication implication = parseImplicationLine(rule);
			if (implication == null) {
				continue;
			}
			implications.add(implication);
		}

		return implications;
	}

	public List<Implication> parseMiningOutputs(AssocRules rules) {
		List<Implication> implications = new ArrayList<>();

		for (AssocRule rule : rules.rules) {
			Implication implication = parseImplicationLine(rule);
			if (implication == null) {
				continue;
			}
			implications.add(implication);
		}

		return implications;
	}

	
	
	private Implication parseImplicationLine(AssocRule rule) {
		Implication implication = new Implication();

		for (int value : rule.getItemset1()) {			 
			String aliasValue = getAliasValue(value);
			ImplicationValue implicationValue = ImplicationValue
					.parse(aliasValue);
			implication.getInputs().add(implicationValue);
		}

		for (int value : rule.getItemset2()) {
			String aliasValue = getAliasValue(value);
			ImplicationValue implicationValue = ImplicationValue
					.parse(aliasValue);
			implication.getOutputs().add(implicationValue);
		}

		// System.out.println(implication.toString());

		return implication;
	}
	
	private String getAliasValue(Integer intValue) {
		for (Entry<String, Integer> value : alias.entrySet()) {
			if (value.getValue().equals(intValue)) {
				return value.getKey();
			}
		}
		return null;
	}
	
	public HashMap<String, Integer> getAlias() {
		return alias;
	}
	
	public Map<Integer, Integer> getLhsAllowed() {
		return lhsAllowed;
	}
	
	public Map<Integer, Integer> getRhsAllowed() {
		return rhsAllowed;
	}

}
