package reasoner.components.implementations;

import reasoner.components.ConsistencyChecker;

public class SimpleConsistencyChecker implements ConsistencyChecker {
    @Override
    public boolean isConsistent() {
        //TODO: Consistency Check Algorithm -> HermiT / Pellet use Tableau
        return false;
    }
}
