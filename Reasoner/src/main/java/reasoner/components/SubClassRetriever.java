package reasoner.components;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.NodeSet;

public interface SubClassRetriever {
    /**
     * For a given class expression, finds all its subclasses which have been declared in the ontology.
     * @param owlClassExpression The class expression whose strict (direct) subclasses are to be retrieved.
     * @param onlyDirectSubClasses Specifies if the direct subclasses should be retrieved ( {@code true}) or if
     *        the all subclasses (descendant) classes should be retrieved ({@code false}).
     * @return NodeSet of all the OWLClasses which are a subclass.
     */
    NodeSet<OWLClass> getSubClasses(OWLClassExpression owlClassExpression, boolean onlyDirectSubClasses);
}
