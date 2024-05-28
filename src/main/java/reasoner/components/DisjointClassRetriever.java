package reasoner.components;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.NodeSet;

public interface DisjointClassRetriever {
    /**
     * For a given expression, finds all the classes which have no instance in common with this expression.
     * @param owlClassExpression The class expression whose disjoint classes are to be retrieved.
     * @return NodeSet of all disjoint classes
     */
    NodeSet<OWLClass> getDisjointClasses(OWLClassExpression owlClassExpression);

}
