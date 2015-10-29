package experiment.dataMining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.AlgoTNR;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.AlgoTNRLHSRHS;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.AlgoTopKRulesLHSRHS;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.Database;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.RuleG;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AlgoAgrawalFaster94;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.AlgoTNS;
import ca.pfv.spmf.algorithms.sequential_rules.topseqrules_and_tns.Rule;
import ca.pfv.spmf.datastructures.redblacktree.RedBlackTree;
import ca.pfv.spmf.input.sequence_database_array_integers.SequenceDatabase;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import experiment.models.invariants.Implication;
import experiment.models.invariants.ImplicationValue;
import experiment.models.tests.TestCase;
import experiment.models.tests.TestData;
import experiment.models.tests.TestSuite;

public class AssociationRuleMining {

	private HashMap<String, Integer> alias;
	private Map<Integer, Integer> lhsAllowed;
	private Map<Integer, Integer> rhsAllowed;

	public AssociationRuleMining() {

	}

	public static void main(String[] args) {

		String input = ".//input.txt";
		String output = ".//output.txt";
		// String output = "C:\\patterns\\association_rules.txt";

		// STEP 1: Applying the FP-GROWTH algorithm to find frequent itemsets
		double minsupp = 0.5;
		AlgoFPGrowth fpgrowth = new AlgoFPGrowth();
		Itemsets patterns = null;
		try {
			patterns = fpgrowth.runAlgorithm(input, null, minsupp);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// patterns.printItemsets(database.size());
		fpgrowth.printStats();
		int databaseSize = fpgrowth.getDatabaseSize();

		// STEP 2: Generating all rules from the set of frequent itemsets (based
		// on Agrawal & Srikant, 94)
		double minconf = 1.0;
		AlgoAgrawalFaster94 algoAgrawal = new AlgoAgrawalFaster94();
		try {
			algoAgrawal.runAlgorithm(patterns, output, databaseSize, minconf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		algoAgrawal.printStats();
	}

	public Map<String, Integer> printForAssociationRuleMining(
			TestSuite testSuite, String filePath) {
		alias = new HashMap<>();
		lhsAllowed = new HashMap<>();
		rhsAllowed = new HashMap<>();
		int ID = 0;
		File file = new File(filePath);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
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
						}
						if (first) {
							writer.write(Integer.toString(id));
							first = false;
						} else {
							writer.write(" " + id);
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
					writer.write(System.lineSeparator());
				}
			}

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return alias;
	}

	public List<Implication> parseMiningOutputs(TestSuite suite,
			PriorityQueue<RuleG> rules) {
		List<Implication> implications = new ArrayList<>();

		for (RuleG rule : rules) {
			Implication implication = parseImplicationLine(rule);
			if (implication == null) {
				continue;
			}
			implications.add(implication);
		}

		return implications;
	}

	public List<Implication> parseMiningOutputs(List<RuleG> ruleListPruned) {
		List<Implication> implications = new ArrayList<>();

		for (RuleG rule : ruleListPruned) {
			Implication implication = parseImplicationLine(rule);
			if (implication == null) {
				continue;
			}
			implications.add(implication);
		}

		return implications;
	}
	

	public List<Implication> parseMiningOutputs(RedBlackTree<RuleG> rules) {
		List<Implication> implications = new ArrayList<>();

		for (RuleG rule : rules) {
			Implication implication = parseImplicationLine(rule);
			if (implication == null) {
				continue;
			}
			implications.add(implication);
		}

		return implications;
	}

	public List<Implication> parseMiningOutputs(PriorityQueue<RuleG> rules) {
		List<Implication> implications = new ArrayList<>();

		for (RuleG rule : rules) {
			Implication implication = parseImplicationLine(rule);
			if (implication == null) {
				continue;
			}
			implications.add(implication);
		}

		return implications;
	}

	public List<Implication> prune(List<Implication> toPrune) {
		List<Implication> copyToPrune = new ArrayList<>(toPrune.size());
		copyToPrune.addAll(toPrune);
		List<Implication> toKeep = new ArrayList<>();
		List<Implication> removed = new ArrayList<>();

		while (copyToPrune.size() > 0) {
			Implication data = copyToPrune.remove(copyToPrune.size() - 1);
			boolean keep = true;
			for (Implication dataToCompare : copyToPrune) {
				if (dataToCompare.isSubsetOf(data)) {
					keep = false;
					break;
				}
			}
			if (keep) {
				toKeep.add(data);
			}
			/*
			 * else{ removed.add(data); }
			 */
		}

		return toKeep;
	}

	public List<RuleG> pruneRuleGComplete(PriorityQueue<RuleG> rules) {

		List<RuleG> prunedImplications = pruneRuleG(rules);
		int previousNumberOfInvariants = 0;
		do {
			previousNumberOfInvariants = prunedImplications.size();
			prunedImplications = pruneRuleG(prunedImplications);
		} while (previousNumberOfInvariants > prunedImplications.size());

		return prunedImplications;

	}
	
	public List<RuleG> pruneRuleGComplete(RedBlackTree<RuleG> rules) {
		List<RuleG> prunedImplications = pruneRuleG(rules);
		int previousNumberOfInvariants = 0;
		do {
			previousNumberOfInvariants = prunedImplications.size();
			prunedImplications = pruneRuleG(prunedImplications);
		} while (previousNumberOfInvariants > prunedImplications.size());

		return prunedImplications;
	}

	private List<RuleG> pruneRuleG(RedBlackTree<RuleG> rules) {
		
		List<RuleG> toKeep = new ArrayList<>();

		while (rules.size() > 0) {
			RuleG rule = rules.popMaximum();
			
			if(rule == null){
				rule = rules.popMinimum();
			}
			
			boolean keep = true;
			for (RuleG dataToCompare : rules) {
				if (lhsIsSubsetOf(dataToCompare, rule)) {
					keep = false;
					break;
				}
				if(rhsIsSubsetOf(dataToCompare, rule)){
					keep = false;
					break;
				}
			}
			if (keep) {
				toKeep.add(rule);
			}
		}
		return toKeep;
	}

	private List<RuleG> pruneRuleG(List<RuleG> rules) {
		List<RuleG> toKeep = new ArrayList<>();

		while (rules.size() > 0) {
			RuleG rule = rules.remove(rules.size() - 1);
			boolean keep = true;
			for (RuleG dataToCompare : rules) {
				if (lhsIsSubsetOf(dataToCompare, rule)) {
					keep = false;
					break;
				}
				if(rhsIsSubsetOf(dataToCompare, rule)){
					keep = false;
					break;
				}
			}
			if (keep) {
				toKeep.add(rule);
			}
		}
		return toKeep;
	}

	List<RuleG> pruneRuleG(PriorityQueue<RuleG> rules) {
		List<RuleG> toKeep = new ArrayList<>();

		while (rules.size() > 0) {
			RuleG rule = rules.remove();
			boolean keep = true;
			for (RuleG dataToCompare : rules) {
				if (lhsIsSubsetOf(dataToCompare, rule)) {
					keep = false;
					break;
				}
				if(rhsIsSubsetOf(dataToCompare, rule)){
					keep = false;
					break;
				}
			}
			if (keep) {
				toKeep.add(rule);
			}
		}
		return toKeep;
	}

	private boolean lhsIsSubsetOf(RuleG dataToCompare, RuleG rule) {
		if (dataToCompare == null || rule == null) {
			return false;
		}
		if (dataToCompare.getItemset1().length >= rule.getItemset1().length
				|| dataToCompare.getItemset2().length != rule.getItemset2().length) {
			return false;
		}

		for (Integer outputValueToCompare : dataToCompare.getItemset2()) {
			boolean found = false;
			for (Integer value : rule.getItemset2()) {
				if (outputValueToCompare == value) {
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}

		int numberOfOperators = dataToCompare.getItemset1().length;

		for (Integer inputValueToCompare : dataToCompare.getItemset1()) {
			for (Integer value : rule.getItemset1()) {
				if (inputValueToCompare == value) {
					numberOfOperators--;
				}
			}
		}

		if (numberOfOperators > 0) {
			return false;
		}

		return true;
	}
	
	private boolean rhsIsSubsetOf(RuleG dataToCompare, RuleG rule) {
		if (dataToCompare == null || rule == null) {
			return false;
		}
		if (dataToCompare.getItemset1().length != rule.getItemset1().length
				|| dataToCompare.getItemset2().length >= rule.getItemset2().length) {
			return false;
		}

		for (Integer outputValueToCompare : dataToCompare.getItemset1()) {
			boolean found = false;
			for (Integer value : rule.getItemset1()) {
				if (outputValueToCompare == value) {
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}

		int numberOfOperators = dataToCompare.getItemset2().length;

		for (Integer inputValueToCompare : dataToCompare.getItemset2()) {
			for (Integer value : rule.getItemset2()) {
				if (inputValueToCompare == value) {
					numberOfOperators--;
				}
			}
		}

		if (numberOfOperators > 0) {
			return false;
		}

		return true;
	}

	private Implication parseImplicationLine(RuleG rule) {

		Implication implication = new Implication();

		for (int value : rule.getItemset1()) {
			/*
			 * if (!lhsAllowed.containsKey(value)) { return null; }
			 */
			String aliasValue = getAliasValue(value);

			ImplicationValue implicationValue = ImplicationValue
					.parse(aliasValue);
			implication.getInputs().add(implicationValue);
		}

		for (int value : rule.getItemset2()) {
			/*
			 * if (!rhsAllowed.containsKey(value)) { return null; }
			 */
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

	public AssocRules mineRulesOld(String input, String output) {
		// STEP 1: Applying the FP-GROWTH algorithm to find frequent itemsets
		double minsupp = 0.01;
		AlgoFPGrowth fpgrowth = new AlgoFPGrowth();
		Itemsets patterns = null;
		try {
			patterns = fpgrowth.runAlgorithm(input, null, minsupp);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// patterns.printItemsets(database.size());
		// fpgrowth.printStats();
		int databaseSize = fpgrowth.getDatabaseSize();

		// STEP 2: Generating all rules from the set of frequent itemsets (based
		// on Agrawal & Srikant, 94)
		double minconf = 1.0;
		AssocRules rules = null;
		AlgoAgrawalFaster94 algoAgrawal = new AlgoAgrawalFaster94();
		try {
			rules = algoAgrawal.runAlgorithm(patterns, null, databaseSize,
					minconf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// algoAgrawal.printStats();
		return rules;
	}

	public PriorityQueue<RuleG> mineRules(String input, String output,
			int maxInvariants) {
		try {
			Database database = new Database();
			database.loadFile(input);

			double minConf = 1.0; //

			AlgoTopKRulesLHSRHS algo = new AlgoTopKRulesLHSRHS(lhsAllowed,
					rhsAllowed);
			algo.runAlgorithm(maxInvariants, minConf, database);

			algo.printStats();

			return algo.getkRules();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public RedBlackTree<RuleG> mineRulesPruned(String input, String output,
			int maxInvariants) {
		try {
			// Load database into memory
			Database database = new Database(); 
			database.loadFile(input);

			double minConf = 1; 
			int delta =  2;
			
			AlgoTNRLHSRHS algo = new AlgoTNRLHSRHS(lhsAllowed,
					rhsAllowed);
			RedBlackTree<RuleG> kRules = algo.runAlgorithm(maxInvariants, minConf, database,  delta );
			//algo.writeResultTofile(".//output.txt");   // to save results to file
			
			algo.printStats();
			
			return kRules;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		// Load database into memory

	}

	


}