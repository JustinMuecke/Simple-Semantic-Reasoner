package reasoner.components.implementations;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import reasoner.ALCReasoner;
import reasoner.components.EntailmentChecker;

import java.util.Optional;
import java.util.Set;

public class EntailmentCheckerConsistency implements EntailmentChecker {

    /*
    Empty because we dont need to instantiate it with values to show exchange ability of modules
     */
    public EntailmentCheckerConsistency(ALCReasoner reasoner, OWLOntology ontology) {}

    @Override
    public boolean isEntailed(OWLAxiom owlAxiom) {
        return false;
    }

    @Override
    public Optional<Set<OWLAxiom>> isEntailed(Set<? extends OWLAxiom> set) {
        return Optional.empty();
    }
}
