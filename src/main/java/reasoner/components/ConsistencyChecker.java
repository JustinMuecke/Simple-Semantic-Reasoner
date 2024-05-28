package reasoner.components;

public interface ConsistencyChecker {
    /**
     * Checks whether the underlying ontology is consistent.
     * @return boolean representing consistency
     */
    boolean isConsistent();
}
