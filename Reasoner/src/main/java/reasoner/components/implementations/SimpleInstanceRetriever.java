package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import reasoner.components.InstanceRetriever;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleInstanceRetriever implements InstanceRetriever {

    OWLOntology ontology;

    public SimpleInstanceRetriever(OWLOntology ontology){
        this.ontology = ontology;
    }
    @Override
    public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression owlClassExpression, boolean onlyDirect) {
        return switch (owlClassExpression.getClassExpressionType()) {
            case OWL_CLASS -> getSingleClassExtension((OWLClass) owlClassExpression);
            case OBJECT_INTERSECTION_OF -> getIntersectionClassExtension((OWLObjectIntersectionOf) owlClassExpression);
            case OBJECT_UNION_OF -> getUnionClassExtension((OWLObjectUnionOf) owlClassExpression);
            default -> null;
        };
    }

    private NodeSet<OWLNamedIndividual> getIntersectionClassExtension(OWLObjectIntersectionOf owlClassExpression) {
        Set<OWLClassExpression> components =  owlClassExpression.getOperands();
        Set<NodeSet<OWLNamedIndividual>> toIntersect = components.stream().map(ce -> getInstances(ce, true)).collect(Collectors.toSet());

        Iterator<NodeSet<OWLNamedIndividual>> iter = toIntersect.iterator();
        NodeSet<OWLNamedIndividual> res = iter.next();
        NodeSet<OWLNamedIndividual> current = iter.next();
        do{
            System.out.println(res.getFlattened());
            System.out.println(current.getFlattened());
            res = new OWLNamedIndividualNodeSet(current.getFlattened()
                    .stream()
                    .filter(res::containsEntity).map(OWLNamedIndividualNode::new));
            if(iter.hasNext()) current = iter.next();
        }while(iter.hasNext());
        return res;
    }

    private NodeSet<OWLNamedIndividual> getUnionClassExtension(OWLObjectUnionOf owlClassExpression) {
        Set<OWLClassExpression> components =  owlClassExpression.getOperands();
        Set<NodeSet<OWLNamedIndividual>> toUnion = components.stream().map(ce -> getInstances(ce, true)).collect(Collectors.toSet());
        return new OWLNamedIndividualNodeSet(toUnion.stream()
                .map(NodeSet::getFlattened)
                .flatMap(Collection::stream)
                .map(OWLNamedIndividualNode::new));

    }

    private NodeSet<OWLNamedIndividual> getSingleClassExtension(OWLClass owlClass) {
        DefaultNodeSet<OWLNamedIndividual> result = new OWLNamedIndividualNodeSet();
        Set<OWLNamedIndividual> extension = ontology.classAssertionAxioms(owlClass)
                .map(HasIndividualsInSignature::getIndividualsInSignature)
                .reduce(new HashSet<>(), (acc, val) -> {
                    acc.addAll(val);
                return acc;
            });
        result.addDifferentEntities(extension);
        return result;
    }
}
