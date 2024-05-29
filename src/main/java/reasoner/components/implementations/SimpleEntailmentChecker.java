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

    /**
     * Calls an EntailmentVisitor to visit the axiom which has to be checked.
     * @param owlAxiom The axiom
     * @return boolean representing whether the axiom is entailed
     */
    @Override
    public boolean isEntailed(OWLAxiom owlAxiom) {
        if(ontology.getAxioms().contains(owlAxiom)) return true;
        return owlAxiom.accept(new EntailmentVisitor(ontology, reasoner));

    }

    /**
     * Checks for a set of axioms, whether they are entailed.
     * @param set The set of axioms to be tested
     * @return Optional Set of axioms which have been entailed.
     */
    @Override
    public Optional<Set<OWLAxiom>> isEntailed(Set<? extends OWLAxiom> set) {
        Set<OWLAxiom> entailedAxioms = new HashSet<>();
        for(OWLAxiom ax : set){
            if(ontology.getAxioms().contains(ax)){
                entailedAxioms.add(ax);
                continue;
            }
            if(Boolean.TRUE.equals(ax.accept(new EntailmentVisitor(ontology, reasoner)))){
                entailedAxioms.add(ax);
            }
        }
        return entailedAxioms.isEmpty() ? Optional.empty() : Optional.of(entailedAxioms);
    }




}
