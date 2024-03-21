package reasoner.components;

public interface ConsistencyChecker {
    /**
     * When is an ontology not consistent?
     *  -> Union(a,b) + Disjoint(a,b)
     *  -> Cyclic SubClass Structure?
     *  -> ....
     * @return
     */
    boolean isConsistent();
}
