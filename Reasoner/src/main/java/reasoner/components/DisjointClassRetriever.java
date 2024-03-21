package reasoner.components;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.NodeSet;

public interface DisjointClassRetriever {

    NodeSet<OWLClass> getDisjointClasses(OWLClassExpression owlClassExpression);

}
