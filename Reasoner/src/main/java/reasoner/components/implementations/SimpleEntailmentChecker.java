package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import reasoner.Reasoner;
import reasoner.components.EntailmentChecker;

import java.util.HashSet;
import java.util.Optional;
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
    public Optional<Set<OWLAxiom>> isEntailed(Set<? extends OWLAxiom> set) {
        Set<OWLAxiom> entailedAxioms = new HashSet<>();
        for(OWLAxiom ax : set){
            if(ax.accept(new EntailmentVisitor(ontology, reasoner))){
                entailedAxioms.add(ax);
            }
        }
        return entailedAxioms.isEmpty() ? Optional.empty() : Optional.of(entailedAxioms);
    }




}
