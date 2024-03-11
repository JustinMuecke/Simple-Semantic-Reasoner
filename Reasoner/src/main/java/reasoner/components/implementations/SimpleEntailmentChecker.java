package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import reasoner.Reasoner;
import reasoner.components.EntailmentChecker;

import java.util.Set;

public class SimpleEntailmentChecker implements EntailmentChecker {

    private Reasoner reasoner;
    private OWLOntology ontology;

    public SimpleEntailmentChecker(Reasoner reasoner, OWLOntology ontology) {
        this.reasoner = reasoner;
        this.ontology = ontology;
    }

    @Override
    public boolean isEntailed(OWLAxiom owlAxiom) {
        return owlAxiom.accept(new EntailmentVisitor());

    }

    @Override
    public boolean isEntailed(Set<? extends OWLAxiom> set) {
        return false;
    }




}
