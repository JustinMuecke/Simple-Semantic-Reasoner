package reasoner.components;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.NodeSet;

public interface SuperClassRetriever {
    /**
     * For a given class expression, finds all its superclasses which have been declared in the ontology.
     * @param owlClassExpression The class expression whose strict (direct) super classes are to be retrieved.
     * @param onlyDirectSuperClasses Specifies if the direct super classes should be retrieved ( {@code true}) or if
     *        the all super classes (ancestors) classes should be retrieved ({@code false}).
     * @return NodeSet of all the OWLClasses which are a superclass.
     */
    NodeSet<OWLClass> getSuperClasses(OWLClassExpression owlClassExpression, boolean onlyDirectSuperClasses);

}
