package experiment.dataMining;

import experiment.dataMining.wrapper.AssocRuleList;
import experiment.dataMining.wrapper.RuleGList;

public interface Pruner {
	RuleGList prune(RuleGList rules);

	AssocRuleList prune(AssocRuleList rules);

}
