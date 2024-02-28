package reasoner.interfaces;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;

public interface InstanceRetriever {
    NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression owlClassExpression, boolean b);
}
