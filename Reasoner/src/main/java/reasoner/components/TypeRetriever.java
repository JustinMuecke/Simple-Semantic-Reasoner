package reasoner.components;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;

public interface TypeRetriever {

    NodeSet<OWLClass> getTypes(OWLNamedIndividual owlNamedIndividual, boolean b);
}
