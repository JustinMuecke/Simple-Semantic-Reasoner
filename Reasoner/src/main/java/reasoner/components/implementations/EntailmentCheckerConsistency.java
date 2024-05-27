package reasoner.components.implementations;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import reasoner.ALCReasoner;
import reasoner.components.EntailmentChecker;

import java.util.Optional;
import java.util.Set;

public class EntailmentCheckerConsistency implements EntailmentChecker {
    private ALCReasoner reasoner;

    public EntailmentCheckerConsistency(ALCReasoner reasoner, OWLOntology ontology) {
        this.reasoner = reasoner;
        this.ontology = ontology;
    }

    private OWLOntology ontology;
    @Override
    public boolean isEntailed(OWLAxiom owlAxiom) {

        return false;
    }

    @Override
    public Optional<Set<OWLAxiom>> isEntailed(Set<? extends OWLAxiom> set) {
        return Optional.empty();
    }
}
