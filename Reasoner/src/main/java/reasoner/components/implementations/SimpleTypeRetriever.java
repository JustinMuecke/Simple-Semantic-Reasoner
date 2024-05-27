package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import reasoner.components.TypeRetriever;

import java.util.Set;
import java.util.stream.Collectors;

public class SimpleTypeRetriever implements TypeRetriever {

    OWLOntology ontology;

    public SimpleTypeRetriever(OWLOntology ontology) {
        this.ontology = ontology;
    }

    /**
     * Gets all class assertions axioms for the given named individual and collects the corresponding
     * class expressions into a nodeset.
     * @param owlNamedIndividual The individual whose types are to be retrieved.
     * @param b Specifies if the direct types should be retrieved ( {@code true}), or if all
     *        types should be retrieved ( {@code false}).
     * @return
     */
    @Override
    public NodeSet<OWLClass> getTypes(OWLNamedIndividual owlNamedIndividual, boolean b) {
        DefaultNodeSet<OWLClass> classes = new OWLClassNodeSet();
        Set<OWLClass> cls = ontology.classAssertionAxioms(owlNamedIndividual)
                .map(OWLClassAssertionAxiom::getClassExpression)
                .map(AsOWLClass::asOWLClass)
                .collect(Collectors.toSet());
        for(OWLClass cl : cls){
            classes.addEntity(cl);
        }
        return classes; 
    }
}
