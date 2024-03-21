package reasoner.components.implementations;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.DefaultNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import reasoner.components.SameIndividualRetriever;

public class SimpleSameIndividualRetriever implements SameIndividualRetriever {

    private final OWLOntology ontology;

    public SimpleSameIndividualRetriever(OWLOntology ontology) {
        this.ontology = ontology;
    }

    @Override
    public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual owlNamedIndividual) {
        var namedIndividuals = ontology.getIndividualsInSignature();
        var dataProperties = owlNamedIndividual.getDataPropertiesInSignature();
        var dataTypes = owlNamedIndividual.getDatatypesInSignature();
        var objectProperties = owlNamedIndividual.getObjectPropertiesInSignature();
        var individuals = owlNamedIndividual.getIndividualsInSignature();
        DefaultNode<OWLNamedIndividual> result = new OWLNamedIndividualNode();

        for(OWLNamedIndividual ind : namedIndividuals){
            if(ind.getDataPropertiesInSignature().equals(dataProperties) &&
                    ind.getDatatypesInSignature().equals(dataTypes) &&
                    ind.getObjectPropertiesInSignature().equals(objectProperties) &&
                    ind.getIndividualsInSignature().equals(individuals)
            ) {
                result.add(ind);
            }
        }
        return result;
    }
}
