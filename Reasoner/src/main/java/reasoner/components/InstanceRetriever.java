package reasoner.components;


import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;

public interface InstanceRetriever {

    /**
     * /TODO
     * @param owlClassExpression
     * @param b
     * @return
     */
    NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression owlClassExpression, boolean b);


}
