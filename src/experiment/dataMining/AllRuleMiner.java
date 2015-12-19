package experiment.dataMining;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AlgoAgrawalFaster94;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AlgoAgrawalFaster94LHSRHS;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowthLHSRHS;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import experiment.dataMining.wrapper.AssocRuleList;
import experiment.models.invariants.Implication;
import experiment.models.tests.TestSuite;

public class AllRuleMiner implements AssociationRuleMining{
	
	private Parser parser;
	private Pruner pruner;
	private boolean analyze;

	public AllRuleMiner(Parser parser, Pruner pruner, boolean analyze){
		this.parser = parser;
		this.pruner = pruner;
		this.analyze = analyze;
	}
	
	@Override
	public List<Implication> runDataMining(TestSuite testSuite, int maxInvariants, int confidence) {
		String input = parser.printForAssociationRuleMining(testSuite);
		AssocRuleList rules = mineRulesAll(input, parser, confidence);
		if(pruner != null){
			AssocRuleList prunedRules = pruner.prune(rules);
			return parser.parseMiningOutputs(prunedRules);
		}
		else{
			return parser.parseMiningOutputs(rules);
		}
	}
	
	

	private AssocRuleList mineRulesAll(String input, Parser parser, int confidence) {
		// STEP 1: Applying the FP-GROWTH algorithm to find frequent itemsets
				double minsupp = 0.0000000000000001;
				AlgoFPGrowthLHSRHS fpgrowth = new AlgoFPGrowthLHSRHS(parser.getLhsAllowed(),
						parser.getRhsAllowed());
				Itemsets patterns = null;
				try {
					patterns = fpgrowth.runAlgorithm(input, null, minsupp);
				
					//patterns = preprunePatterns(patterns, lhsAllowed, rhsAllowed);
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
				double minconf = confidence;
				AssocRules rules = null;
				AlgoAgrawalFaster94LHSRHS algoAgrawal = new AlgoAgrawalFaster94LHSRHS(parser.getLhsAllowed(),
						parser.getRhsAllowed(), parser.getConnectedAlias(), analyze);
				try {
					rules = algoAgrawal.runAlgorithm(patterns, null, databaseSize,
							minconf);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//AssocRuleList list = removeUnconnected(rules, parser);
				//AssocRuleList list = pruneLHSRHS(rules, parser);
				
				AssocRuleList list = new AssocRuleList();
				list.list = rules.rules;
				
				algoAgrawal.printStats();
				return list;
	}

	private AssocRuleList removeUnconnected(AssocRules rules, Parser parser2) {
		Stream<AssocRule> stream = rules.rules.parallelStream();
		
		AssocRuleList list = new AssocRuleList();
		
		list.list = stream.filter(rule -> lhsRhsCorrect(rule, parser)).collect(Collectors.toList());
		return list;
	}
	
	private AssocRuleList pruneLHSRHS(AssocRules rules, Parser parser) {
		Stream<AssocRule> stream = rules.rules.parallelStream();
		
		AssocRuleList list = new AssocRuleList();
		
		list.list = stream.filter(rule -> lhsRhsCorrect(rule, parser)).collect(Collectors.toList());
		return list;
		
	}

	/*private AssocRuleList pruneLHSRHS(AssocRules rules, Parser parser) {
		Stream<AssocRule> stream = rules.rules.parallelStream();
		
		AssocRuleList list = new AssocRuleList();
		
		list.list = stream.filter(rule -> checkConnectedNess(rule.getItemset1(), rule.getItemset2(), parser.getConnectedAlias())).collect(Collectors.toList());
		return list;
		
	}*/
	
	private boolean checkConnectedNess(int[] is1, int[] is2, Map<Integer, List<Integer>> connected) {
		for (int value : is2) {
			List<Integer> connectedValues = connected.get(value);
			for (Integer rhsValue : is1) {
				if(!connectedValues.contains(rhsValue)){
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean checkLHSRHS(int[] is, Map<Integer, Integer> ruleMap) {
		for (Integer value : is) {
			if (!ruleMap.containsKey(value)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean lhsRhsCorrect(AssocRule rule, Parser parser) {
		boolean lhsCorrect = checkLHSRHS(rule.getItemset1(), parser.getLhsAllowed());
		boolean rhsCorrect = checkLHSRHS(rule.getItemset2(), parser.getRhsAllowed());

		return lhsCorrect && rhsCorrect;
	}

	
}
