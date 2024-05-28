package reasoner.components;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;

public interface TypeRetriever {
    /**
     * For a given named individual, returns all classes which it is an instance of.
     * @param owlNamedIndividual The individual whose types are to be retrieved.
     * @param b Specifies if the direct types should be retrieved ( {@code true}), or if all
     *        types should be retrieved ( {@code false}).
     * @return NodeSet of all OWL Classes which the named individual is part of.
     */
    NodeSet<OWLClass> getTypes(OWLNamedIndividual owlNamedIndividual, boolean b);
}
