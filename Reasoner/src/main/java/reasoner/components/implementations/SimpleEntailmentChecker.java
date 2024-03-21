package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import reasoner.Reasoner;
import reasoner.components.EntailmentChecker;

import java.util.Set;

public class SimpleEntailmentChecker implements EntailmentChecker {

    private final Reasoner reasoner;
    private final OWLOntology ontology;

    public SimpleEntailmentChecker(Reasoner reasoner, OWLOntology ontology) {
        this.reasoner = reasoner;
        this.ontology = ontology;
    }

    @Override
    public boolean isEntailed(OWLAxiom owlAxiom) {
        return owlAxiom.accept(new EntailmentVisitor(ontology, reasoner));

    }

    @Override
    public boolean isEntailed(Set<? extends OWLAxiom> set) {
        for(OWLAxiom ax : set){
            if(!ax.accept(new EntailmentVisitor(ontology, reasoner))) return false;
        }
        return true;
    }




}
