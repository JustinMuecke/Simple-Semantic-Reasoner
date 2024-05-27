package reasoner.components;

import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Optional;
import java.util.Set;

public interface EntailmentChecker {
    /**
     * Checks whether a given axiom is entailed by the ontology. If the axiom is entailed, adds it to the list of pending changes.
     * @param owlAxiom The axiom
     * @return boolean representing entailment.
     */
    boolean isEntailed(OWLAxiom owlAxiom);
    /**
     * Checks whether a set of given axioms is entailed by the ontology.
     * @param set The set of axioms to be tested
     * @return boolean representing whether all axioms are entailed.
     */
    Optional<Set<OWLAxiom>> isEntailed(Set<? extends OWLAxiom> set);
}
