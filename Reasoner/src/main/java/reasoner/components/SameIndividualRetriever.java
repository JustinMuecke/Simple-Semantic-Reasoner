package reasoner.components;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;

public interface SameIndividualRetriever {
    Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual owlNamedIndividual);
}
