package reasoner.interfaces;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;

public interface TypeRetriever {

    /**
     * For a given named individual in the ontology, the type retriever iterates through all axioms
     * which mention the individual and are of type class assertion
     * @param owlNamedIndividual named individual of which we want to get the types
     * @param b defines the depth of the search.
     *          False: Only consider the classes which are defined in the axiom.
     *          True: Also include entailed axioms.
     * @return The NodeSet of all OWL Classes that have been found.
     */
    NodeSet<OWLClass> getTypes(OWLNamedIndividual owlNamedIndividual, boolean b);
}
