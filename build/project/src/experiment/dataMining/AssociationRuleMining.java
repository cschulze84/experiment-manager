package experiment.dataMining;

import java.util.List;

import experiment.models.invariants.Implication;
import experiment.models.tests.TestSuite;

public interface AssociationRuleMining {
	List<Implication> runDataMining(TestSuite testSuite, int maxInvariants, int strenght);
}
