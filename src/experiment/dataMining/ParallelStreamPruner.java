package experiment.dataMining;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.pfv.spmf.algorithms.associationrules.TopKRules_and_TNR.RuleG;
import ca.pfv.spmf.algorithms.associationrules.agrawal94_association_rules.AssocRule;
import experiment.dataMining.wrapper.AssocRuleList;
import experiment.dataMining.wrapper.RuleGList;

public class ParallelStreamPruner implements Pruner {

	private Optional<AssocRule> evaluateAssocRule;
	private Optional<RuleG> evaluateRuleG;
	
	//private Optional<RuleG> evaluateRuleG;
	
	/**
	 * 
	 * RULEG
	 * 
	 */
	
	@Override
	public RuleGList prune(RuleGList rules) {
		RuleGList prunedImplications = pruneRuleGStreamLHS(rules);
		
		prunedImplications = pruneRuleGStreamRHS(prunedImplications);
		
		return prunedImplications;
	}
	
	public RuleGList pruneRuleGStreamLHS(RuleGList rules) {
		final List<RuleG> evaluationList = new ArrayList<RuleG>();
		Stream<RuleG> stream = rules.list.parallelStream();
		//int i = 0;
		long startTime = System.currentTimeMillis();

		while(sortAndEvaluate(rules, stream, evaluationList)){
			stream = rules.list.parallelStream().filter(element -> !lhsIsSubsetOf(evaluationList, element));
			//i++;
		}
		// ... do something ...
		long estimatedTime = System.currentTimeMillis() - startTime;
		
		System.out.println("Pruning Time: " + estimatedTime);
		return rules;
		
	}
	
	private boolean sortAndEvaluate(RuleGList resultList, Stream<RuleG> stream, List<RuleG> evaluationList) {
		Comparator<RuleG> bySize = (RuleG o1, RuleG o2)->compareRuleG(o1, o2);
		resultList.list = stream.sorted(bySize).collect(Collectors.toList());
		
		int searchLimit = 1000;
		int counter = 0;
		
		evaluationList.clear();
		
		for (RuleG ruleG : resultList.list) {
			if(ruleG.evaluated){
				break;
			}
			else{
				if(counter++ < searchLimit){
					ruleG.evaluated = true;
					evaluationList.add(ruleG);
				}
				else{
					break;
				}
			}
		}
	
		if(evaluationList.size() == 0){
			return false;
		}
		else{
			return true;
		}
		
	}

	private int compareRuleG(RuleG o1, RuleG o2) {
		if(o1.evaluated && !o2.evaluated){
			return 1;
		}
		else if(!o1.evaluated && o2.evaluated){
			return -1;
		}
		else if(o1.evaluated && o2.evaluated){
			return 0;
		}
		else{
			Integer o1Int = o1.getItemset1().length;
			Integer o2Int = o2.getItemset1().length;
			
			return o1Int.compareTo(o2Int);
		}
		
 		
	}

	public RuleGList pruneRuleGStreamRHS(RuleGList rules) {
		Stream<RuleG> filterStream = rules.list.parallelStream();
		HashMap<RuleG, Boolean> evaluated = new HashMap<>(rules.list.size());
		RuleGList resultList = new RuleGList();
		
		evaluateRuleG = filterStream.filter(e -> !evaluated.containsKey(e)).findFirst();
		if(evaluateRuleG.isPresent()){
			evaluated.put(evaluateRuleG.get(), true);
		}
		else{
			resultList.list.addAll(rules.list);
			return resultList;
		}
		
		filterStream = rules.list.parallelStream();
		
		int i = 0;
		while(true){
			resultList.list = filterStream.filter(element -> !rhsIsSubsetOf(evaluateRuleG.get(), element)).collect(Collectors.toList());
			filterStream = resultList.list.parallelStream();
			
			//System.out.println(i + " : " + list.size() + " : " + evaluated.size());
			
			evaluateRuleG = filterStream.filter(e -> !evaluated.containsKey(e)).findFirst();
			if(evaluateRuleG.isPresent()){
				evaluated.put(evaluateRuleG.get(), true);
				filterStream = resultList.list.parallelStream();
			}
			else{
				return resultList;
				
			}
		}
		
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
	
	private boolean lhsIsSubsetOf(List<RuleG> dataToCompare, RuleG rule) {
		return dataToCompare.parallelStream().anyMatch(e -> lhsIsSubsetOf(e, rule));
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

	
	/**
	 * 
	 * ASSOC RULES
	 * 
	 */

	@Override
	public AssocRuleList prune(AssocRuleList rules) {
		AssocRuleList prunedImplications = pruneAssocRuleStreamLHS(rules.list);
		
		prunedImplications = pruneAssocRuleStreamRHS(prunedImplications);
		
		return prunedImplications;
	}
	
	private AssocRuleList pruneAssocRuleStreamLHS(List<AssocRule> rules) {
		Stream<AssocRule> filterStream = rules.parallelStream();
		HashMap<AssocRule, Boolean> evaluated = new HashMap<>(rules.size());
		AssocRuleList resultList = new AssocRuleList();
		
		evaluateAssocRule = filterStream.filter(e -> !evaluated.containsKey(e)).findFirst();
		if(evaluateAssocRule.isPresent()){
			evaluated.put(evaluateAssocRule.get(), true);
		}
		else{
			resultList.list.addAll(rules);
			return resultList;
		}
		
		filterStream = rules.parallelStream();
		
		int i = 0;
		while(true){
			resultList.list = filterStream.filter(element -> !lhsIsSubsetOf(evaluateAssocRule.get(), element)).collect(Collectors.toList());
			filterStream = resultList.list.parallelStream();
			
			//System.out.println(i + " : " + list.size() + " : " + evaluated.size());
			
			evaluateAssocRule = filterStream.filter(e -> !evaluated.containsKey(e)).findFirst();
			if(evaluateAssocRule.isPresent()){
				evaluated.put(evaluateAssocRule.get(), true);
				filterStream = resultList.list.parallelStream();
			}
			else{
				return resultList;
				
			}
		}
	}
	
	private boolean lhsIsSubsetOf(AssocRule dataToCompare, AssocRule rule) {
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

	private AssocRuleList pruneAssocRuleStreamRHS(
			AssocRuleList rules) {
		Stream<AssocRule> filterStream = rules.list.parallelStream();
		HashMap<AssocRule, Boolean> evaluated = new HashMap<>(rules.list.size());
		AssocRuleList resultList = new AssocRuleList();
		
		evaluateAssocRule = filterStream.filter(e -> !evaluated.containsKey(e)).findFirst();
		if(evaluateAssocRule.isPresent()){
			evaluated.put(evaluateAssocRule.get(), true);
		}
		else{
			resultList.list.addAll(rules.list);
			return resultList;
		}
		
		filterStream = rules.list.parallelStream();
		
		int i = 0;
		while(true){
			resultList.list = filterStream.filter(element -> !rhsIsSubsetOf(evaluateAssocRule.get(), element)).collect(Collectors.toList());
			filterStream = resultList.list.parallelStream();
			
			//System.out.println(i + " : " + list.size() + " : " + evaluated.size());
			
			evaluateAssocRule = filterStream.filter(e -> !evaluated.containsKey(e)).findFirst();
			if(evaluateAssocRule.isPresent()){
				evaluated.put(evaluateAssocRule.get(), true);
				filterStream = resultList.list.parallelStream();
			}
			else{
				return resultList;
				
			}
		}
	}

	private boolean rhsIsSubsetOf(AssocRule dataToCompare, AssocRule rule) {
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


}
