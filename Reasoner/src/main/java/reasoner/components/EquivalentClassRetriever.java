package reasoner.components;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.Node;

public interface EquivalentClassRetriever {
    Node<OWLClass> getEquivalentClasses(OWLClassExpression owlClassExpression);
}
