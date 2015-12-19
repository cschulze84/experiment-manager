package experiment.dataMining;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.AlgoTopKRulesLHSRHS;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.Database;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.DatabaseInMemory;
import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.RuleG;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRules;
import experiment.dataMining.wrapper.RuleGList;
import experiment.models.invariants.Implication;
import experiment.models.tests.TestSuite;

public class TopKRuleMiner implements AssociationRuleMining {
	
	private Parser parser;
	private Pruner pruner;

	public TopKRuleMiner(Parser parser, Pruner pruner){
		this.parser = parser;
		this.pruner = pruner;
	}
	
	@Override
	public List<Implication> runDataMining(TestSuite testSuite,
			int maxInvariants, int strenght) {
		String input = parser.printForAssociationRuleMining(testSuite);
		RuleGList rules = mineRulesTopK(input, parser, maxInvariants, strenght, testSuite.getInputs().size());
		if(pruner != null){
			RuleGList prunedRules = pruner.prune(rules);
			return parser.parseMiningOutputs(prunedRules);
		}
		else{
			return parser.parseMiningOutputs(rules);
		}
	}

	private RuleGList mineRulesTopK(String input, Parser parser,
			int maxInvariants, int strenght, int numberLHS) {
		try {
			Database database = new DatabaseInMemory();
			database.loadFile(input);

			double minConf = 1.0; //

			AlgoTopKRulesLHSRHS algo = new AlgoTopKRulesLHSRHS(parser.getLhsAllowed(),
					parser.getRhsAllowed(), numberLHS);
			algo.runAlgorithm(maxInvariants, minConf, database);

			algo.printStats();
			
			RuleGList ruleList = new RuleGList();
			ruleList.list = new ArrayList<RuleG>(algo.getkRules());
			return ruleList;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
