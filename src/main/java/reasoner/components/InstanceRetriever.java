package reasoner.components;


import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;

public interface InstanceRetriever {

    /**
     * Return all NamedIndividuals which are instances of the class expression.
     * @param owlClassExpression The class expression whose instances are to be retrieved.
     * @param b Specifies if the direct instances should be retrieved ( {@code true}), or if
     *        all instances should be retrieved ( {@code false}).
     * @return NodeSet of all the named individuals
     */
    NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression owlClassExpression, boolean b);


}
