package reasoner.components.implementations;

import org.semanticweb.owlapi.model.OWLAxiom;
import reasoner.components.EntailmentChecker;

import java.util.Optional;
import java.util.Set;

public class HeuristicEntailmentChecker implements EntailmentChecker {


    @Override
    public boolean isEntailed(OWLAxiom owlAxiom) {
        return false;
    }

    @Override
    public Optional<Set<OWLAxiom>> isEntailed(Set<? extends OWLAxiom> set) {
        return Optional.empty();
    }
}
