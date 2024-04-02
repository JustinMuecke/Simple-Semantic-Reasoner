package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import org.semanticweb.owlapi.util.EscapeUtils;
import reasoner.components.InstanceRetriever;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleInstanceRetriever implements InstanceRetriever {

    OWLOntology ontology;
    OWLDataFactory df;

    public SimpleInstanceRetriever(OWLOntology ontology, OWLDataFactory df){
        this.ontology = ontology;
        this.df = df;
    }
    //TODO: Implement ClassExpression cases

    @Override
    public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression owlClassExpression, boolean onlyDirect) {
        return switch (owlClassExpression.getClassExpressionType()) {
            case OWL_CLASS -> getSingleClassExtension((OWLClass) owlClassExpression);
            case OBJECT_INTERSECTION_OF -> getIntersectionClassExtension((OWLObjectIntersectionOf) owlClassExpression);
            case OBJECT_UNION_OF -> getUnionClassExtension((OWLObjectUnionOf) owlClassExpression);
            case OBJECT_SOME_VALUES_FROM -> getObjectSomeValueFromExtension((OWLObjectSomeValuesFrom) owlClassExpression);
            case OBJECT_ALL_VALUES_FROM -> getObjectAllValueFromExtension((OWLObjectAllValuesFrom) owlClassExpression);
            case OBJECT_HAS_VALUE -> null;
            default -> null;
        };
    }



    private NodeSet<OWLNamedIndividual> getIntersectionClassExtension(OWLObjectIntersectionOf owlClassExpression) {
        System.out.println(owlClassExpression);
        Set<OWLClassExpression> components =  owlClassExpression.getOperands();
        Set<NodeSet<OWLNamedIndividual>> toIntersect = components.stream().map(ce -> getInstances(ce, true)).collect(Collectors.toSet());
        System.out.println(toIntersect);
        Iterator<NodeSet<OWLNamedIndividual>> iter = toIntersect.iterator();
        NodeSet<OWLNamedIndividual> res = iter.next();
        NodeSet<OWLNamedIndividual> current = iter.next();
        do{
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

    private NodeSet<OWLNamedIndividual> getObjectSomeValueFromExtension(OWLObjectSomeValuesFrom expression){
        Set<OWLNamedIndividual> extension = new HashSet<>();

        Set<OWLClassExpression> classExpressions = expression.getNestedClassExpressions().stream().filter(ce -> !ce.equals(expression)).collect(Collectors.toSet());
        Iterator<OWLClassExpression> iter = classExpressions.iterator();
        OWLClassExpression subject = iter.next();
        while(iter.hasNext()){
            OWLClassExpression next = iter.next();
            if(subject.getNestedClassExpressions().contains(next)) continue;
            subject = next;
        }
        NodeSet<OWLNamedIndividual> possibleIndividuals = this.getInstances(subject, true);
        OWLObjectPropertyExpression property = expression.getProperty();

        Set<OWLObjectPropertyAssertionAxiom> propertyAssertionAxioms = ontology.axioms().filter(axiom -> axiom.getAxiomType()
                .equals(AxiomType.OBJECT_PROPERTY_ASSERTION))
                .map(axiom -> (OWLObjectPropertyAssertionAxiom) axiom)
                .collect(Collectors.toSet());

        for(OWLObjectPropertyAssertionAxiom axiom : propertyAssertionAxioms){
            OWLIndividual axiomSubject = axiom.getSubject();
            OWLIndividual axiomObject = axiom.getObject();
            OWLPropertyExpression axiomProperty = axiom.getProperty();
            if(axiom.getSubject().isNamed()){
                if(axiomProperty.equals(property)){
                    System.out.println(axiomSubject + " " + axiomProperty + " " + axiomObject);

                    if(possibleIndividuals.containsEntity(axiomObject.asOWLNamedIndividual())) extension.add(axiomSubject.asOWLNamedIndividual());
                }
            }


        }
        DefaultNodeSet<OWLNamedIndividual> result = new OWLNamedIndividualNodeSet();
        result.addDifferentEntities(extension);
        System.out.println("Result for: " + expression +"\n" + result);
        return result;
    }

    private NodeSet<OWLNamedIndividual> getObjectAllValueFromExtension(OWLObjectAllValuesFrom expression) {
        Set<OWLNamedIndividual> extension = new HashSet<>();

        Set<OWLClassExpression> classExpressions = expression.getNestedClassExpressions().stream().filter(ce -> !ce.equals(expression)).collect(Collectors.toSet());
        Iterator<OWLClassExpression> iter = classExpressions.iterator();
        OWLClassExpression subject = iter.next();
        while(iter.hasNext()){
            OWLClassExpression next = iter.next();
            if(subject.getNestedClassExpressions().contains(next)) continue;
            subject = next;
        }
        NodeSet<OWLNamedIndividual> possibleIndividuals = this.getInstances(subject, true);
        OWLObjectPropertyExpression property = expression.getProperty();

        Set<OWLObjectPropertyAssertionAxiom> propertyAssertionAxioms = ontology.axioms().filter(axiom -> axiom.getAxiomType()
                        .equals(AxiomType.OBJECT_PROPERTY_ASSERTION))
                .map(axiom -> (OWLObjectPropertyAssertionAxiom) axiom)
                .collect(Collectors.toSet());
        HashMap<OWLNamedIndividual, Set<OWLNamedIndividual>> association= new HashMap<>();
        for(OWLObjectPropertyAssertionAxiom axiom : propertyAssertionAxioms){

            if(axiom.getSubject().isNamed()){
                OWLNamedIndividual axiomSubject = axiom.getSubject().asOWLNamedIndividual();
                OWLNamedIndividual axiomObject = axiom.getObject().asOWLNamedIndividual();
                OWLPropertyExpression axiomProperty = axiom.getProperty();
                if(axiomProperty.equals(property)){
                    if(association.containsKey(axiomSubject)){
                        Set<OWLNamedIndividual> current = association.get(axiomSubject);
                        current.add(axiomObject);
                        association.replace(axiomSubject, current);
                        continue;
                    }
                    Set<OWLNamedIndividual> newEntry =new HashSet<>();
                    newEntry.add(axiomObject);
                    association.put(axiomSubject, newEntry);

                }
            }
        }
        for (OWLNamedIndividual possibleInstance : association.keySet()){
            if(possibleIndividuals.getFlattened().containsAll(association.get(possibleInstance))){
                extension.add(possibleInstance);
            }
        }
        DefaultNodeSet<OWLNamedIndividual> result = new OWLNamedIndividualNodeSet();
        result.addDifferentEntities(extension);
        return result;
    }
}
