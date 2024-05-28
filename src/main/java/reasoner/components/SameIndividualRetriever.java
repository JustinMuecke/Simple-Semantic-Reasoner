package reasoner.components;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;

public interface SameIndividualRetriever {
    /**
     * For a given named individual, it finds all equivalent individuals and returns them.
     * @param owlNamedIndividual The individual whose same individuals are to be retrieved.
     * @return Node containing all equivalent named individuals.
     */
    Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual owlNamedIndividual);
}
