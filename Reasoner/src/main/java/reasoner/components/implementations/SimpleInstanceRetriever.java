package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNode;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import org.semanticweb.owlapi.util.EscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reasoner.components.InstanceRetriever;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleInstanceRetriever implements InstanceRetriever {

    OWLOntology ontology;
    OWLDataFactory df;
    private static final Logger logger = LoggerFactory.getLogger("SimpleInstanceRetriever");
    public SimpleInstanceRetriever(OWLOntology ontology, OWLDataFactory df){
        this.ontology = ontology;
        this.df = df;
    }

    @Override
    public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression owlClassExpression, boolean onlyDirect) {
        return switch (owlClassExpression.getClassExpressionType()) {
            case OWL_CLASS -> getSingleClassExtension((OWLClass) owlClassExpression);
            case OBJECT_INTERSECTION_OF -> getIntersectionClassExtension((OWLObjectIntersectionOf) owlClassExpression);
            case OBJECT_UNION_OF -> getUnionClassExtension((OWLObjectUnionOf) owlClassExpression);
            case OBJECT_COMPLEMENT_OF -> getComplementClassExtension((OWLObjectComplementOf) owlClassExpression);
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
        logger.info("Class Expression is of Type {}", expression.getClassExpressionType());

        Set<OWLNamedIndividual> extension = new HashSet<>();
        OWLObjectPropertyExpression property = expression.getProperty();
        Set<OWLClassExpression> classExpressions = expression.getNestedClassExpressions().stream().filter(ce -> !ce.equals(expression)).collect(Collectors.toSet());
        Iterator<OWLClassExpression> iter = classExpressions.iterator();
        OWLClassExpression subject = iter.next();
        while(iter.hasNext()){
            OWLClassExpression next = iter.next();
            if(subject.getNestedClassExpressions().contains(next)) continue;
            subject = next;
        }
        logger.info("Property: {}", property);
        logger.info("Object: {}", subject);
        logger.info("Recursively Getting Instances of Object");
        NodeSet<OWLNamedIndividual> possibleIndividuals = this.getInstances(subject, true);
        logger.info("Found Instances satisfying Object: {}", possibleIndividuals);
        Set<OWLObjectPropertyAssertionAxiom> propertyAssertionAxioms = ontology.axioms().filter(axiom -> axiom.getAxiomType()
                .equals(AxiomType.OBJECT_PROPERTY_ASSERTION))
                .map(axiom -> (OWLObjectPropertyAssertionAxiom) axiom)
                .collect(Collectors.toSet());
        logger.info("Finding all Individuals with desired Property to at least one Instance of Object");
        for(OWLObjectPropertyAssertionAxiom axiom : propertyAssertionAxioms){
            OWLIndividual axiomSubject = axiom.getSubject();
            OWLIndividual axiomObject = axiom.getObject();
            OWLPropertyExpression axiomProperty = axiom.getProperty();
            if(axiom.getSubject().isNamed()){
                if(axiomProperty.equals(property)){
                    if(possibleIndividuals.containsEntity(axiomObject.asOWLNamedIndividual())) extension.add(axiomSubject.asOWLNamedIndividual());
                }
            }
        }
        DefaultNodeSet<OWLNamedIndividual> result = new OWLNamedIndividualNodeSet();
        result.addDifferentEntities(extension);
        logger.info("Satisfied for Individuals: {}", extension);
        return result;
    }

    private NodeSet<OWLNamedIndividual> getObjectAllValueFromExtension(OWLObjectAllValuesFrom expression) {
        logger.info("Class Expression is of Type {}", expression.getClassExpressionType());

        Set<OWLNamedIndividual> extension = new HashSet<>();
        OWLObjectPropertyExpression property = expression.getProperty();

        Set<OWLClassExpression> classExpressions = expression.getNestedClassExpressions().stream().filter(ce -> !ce.equals(expression)).collect(Collectors.toSet());
        Iterator<OWLClassExpression> iter = classExpressions.iterator();
        OWLClassExpression subject = iter.next();
        while(iter.hasNext()){
            OWLClassExpression next = iter.next();
            if(subject.getNestedClassExpressions().contains(next)) continue;
            subject = next;
        }
        logger.info("Property: {}", property);
        logger.info("Object: {}", subject);
        logger.info("Recursively Getting Instances of Object");
        NodeSet<OWLNamedIndividual> possibleIndividuals = this.getInstances(subject, true);
        logger.info("Found Instances satisfying Object: {}", possibleIndividuals);

        Set<OWLObjectPropertyAssertionAxiom> propertyAssertionAxioms = ontology.axioms().filter(axiom -> axiom.getAxiomType()
                        .equals(AxiomType.OBJECT_PROPERTY_ASSERTION))
                .map(axiom -> (OWLObjectPropertyAssertionAxiom) axiom)
                .collect(Collectors.toSet());
        HashMap<OWLNamedIndividual, Set<OWLNamedIndividual>> association= new HashMap<>();
        logger.info("Finding all Individuals with desired Property");
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
        logger.info("Possible Individuals: {}", association.keySet());
        logger.info("Check whether Individuals are related to all Instances of Object");
        for (OWLNamedIndividual possibleInstance : association.keySet()){
            if(possibleIndividuals.getFlattened().containsAll(association.get(possibleInstance))){
                extension.add(possibleInstance);
            }
        }
        logger.info("Satisfied for Individuals: {}", extension);
        DefaultNodeSet<OWLNamedIndividual> result = new OWLNamedIndividualNodeSet();
        result.addDifferentEntities(extension);
        return result;
    }

    private NodeSet<OWLNamedIndividual> getComplementClassExtension(OWLObjectComplementOf expression){
        Set<OWLClassExpression> classExpressions = expression.getNestedClassExpressions().stream().filter(ce -> !ce.equals(expression)).collect(Collectors.toSet());
        Iterator<OWLClassExpression> iter = classExpressions.iterator();
        OWLClassExpression subject = iter.next();
        while(iter.hasNext()){
            OWLClassExpression next = iter.next();
            if(subject.getNestedClassExpressions().contains(next)) continue;
            subject = next;
        }
        System.out.println("SUBJECT");
        System.out.println(subject);
        NodeSet<OWLNamedIndividual> complement = this.getInstances(subject, true);
        Set<OWLNamedIndividual> extension = new HashSet<>();
        Set<OWLDeclarationAxiom> declarationAxioms = ontology.getAxioms(AxiomType.DECLARATION).stream().filter(axiom -> !axiom.getIndividualsInSignature().isEmpty()).collect(Collectors.toSet());
        for (OWLDeclarationAxiom declarationAxiom : declarationAxioms){
            //Decleration has either 1 or 0 individuals in Signature
            for(OWLNamedIndividual individual : declarationAxiom.getIndividualsInSignature()){
                if (!complement.containsEntity(individual)) extension.add(individual);
            }
        }
        DefaultNodeSet<OWLNamedIndividual> result = new OWLNamedIndividualNodeSet();
        result.addDifferentEntities(extension);
        return result;

    }
}
