package reasoner.components.implementations;

import reasoner.components.ConsistencyChecker;

public class SimpleConsistencyChecker implements ConsistencyChecker {
    /**
     * Should check whether an ontology is consistent. Currently only returns false.
     * @return false
     */
    @Override
    public boolean isConsistent() {
        return false;
    }
}
