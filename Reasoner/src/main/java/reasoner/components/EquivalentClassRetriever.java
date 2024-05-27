package reasoner.components;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.Node;

public interface EquivalentClassRetriever {
    /**
     * For a given class expression, finds all the equivalent OWLClasses.
     * @param owlClassExpression The class expression whose equivalent classes are to be retrieved.
     * @return Node containing all equivalent classes.
     */
    Node<OWLClass> getEquivalentClasses(OWLClassExpression owlClassExpression);
}
