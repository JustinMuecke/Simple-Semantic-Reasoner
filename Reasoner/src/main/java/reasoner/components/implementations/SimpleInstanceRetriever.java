package reasoner.components.implementations;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import reasoner.components.InstanceRetriever;

import java.util.Set;
import java.util.stream.Collectors;

public class SimpleInstanceRetriever implements InstanceRetriever {

    OWLOntology ontology;

    public SimpleInstanceRetriever(OWLOntology ontology){
        this.ontology = ontology;
    }
    @Override
    public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression owlClassExpression, boolean b) {
        DefaultNodeSet<OWLNamedIndividual> result = new OWLNamedIndividualNodeSet();
        var namedIndividuals = ontology.getIndividualsInSignature();
        for(OWLNamedIndividual ind : namedIndividuals){
            Set<OWLClassExpression> expr = ontology.classAssertionAxioms(ind)
                    .map(OWLClassAssertionAxiom::getClassExpression)
                    .collect(Collectors.toSet());
            switch (owlClassExpression.getClassExpressionType()){
                case OWL_CLASS -> {if(expr.contains(owlClassExpression)) result.addEntity(ind);}
                case OBJECT_INTERSECTION_OF -> {if(expr.containsAll(owlClassExpression.asConjunctSet())) result.addEntity(ind);}
                case OBJECT_UNION_OF -> {if(expr.stream().anyMatch(i->owlClassExpression.asDisjunctSet().contains(i))) result.addEntity(ind);}
            }
        }
        return result;

    }
}
