package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reasoner.Reasoner;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

public class EntailmentVisitor implements OWLAxiomVisitorEx<Boolean> {

    OWLOntology ontology;

    /**
     * Be Careful of Cyclic Behaviour
     */
    Reasoner reasoner;
    private static final Logger logger = LoggerFactory.getLogger("EntailmentVisitor");


    public EntailmentVisitor(OWLOntology ontology, Reasoner reasoner) {
        this.ontology = ontology;
        this.reasoner = reasoner;
    }

    /**
     * Visit EACH axiom in entailment method to check whether they hold up
     */

    //__________________ VISITOR PATTERN METHODS ________________________

    //_____               TRIVIAL AXIOMS              ___________________
    @ParametersAreNonnullByDefault
    public Boolean visit(OWLAnnotationAssertionAxiom axiom) {
        return Boolean.TRUE;
    }

    @ParametersAreNonnullByDefault
    public Boolean visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        return Boolean.TRUE;
    }

    @ParametersAreNonnullByDefault
    public Boolean visit(OWLAnnotationPropertyDomainAxiom axiom) {
        return Boolean.TRUE;
    }

    @ParametersAreNonnullByDefault
    public Boolean visit(OWLAnnotationPropertyRangeAxiom axiom) {
        return Boolean.TRUE;
    }

    @ParametersAreNonnullByDefault
    public Boolean visit(OWLDeclarationAxiom axiom) {
        return Boolean.TRUE;
    }


    //______              ASSERTIONS            _____
    /**
     * Two Concepts are equivalent if they have the same extension, i.e. they all instance the same individuals
     * @param axiom
     * @return
     */
    public Boolean visit(OWLEquivalentClassesAxiom axiom) {
        logger.info("Visiting Equivalent Classes Axiom");
        Set<OWLClassExpression> ceSet = axiom.getClassExpressions();
        if(ceSet.stream().map(ce -> reasoner.getInstances(ce, false)).distinct().count() > 1) return Boolean.TRUE;
        return Boolean.FALSE;
    }

    /**
     * Two classes can be defined as disjoint of their extensions have no intersection
     * @param axiom
     * @return
     */
    public Boolean visit(OWLDisjointClassesAxiom axiom){
        System.out.println(axiom);
        Set<OWLClassExpression> ceSet = axiom.getClassExpressions();
        Set<NodeSet<OWLNamedIndividual>> extensions = ceSet.stream().map(ce -> reasoner.getInstances(ce)).collect(Collectors.toSet());

        Iterator<NodeSet<OWLNamedIndividual>> iter = extensions.iterator();
        NodeSet<OWLNamedIndividual> last = iter.next();
        while(extensions.iterator().hasNext()){
            NodeSet<OWLNamedIndividual> current = iter.next();
            if(last.getFlattened().stream().anyMatch(c -> current.getFlattened().contains(c))) return Boolean.FALSE;
            last = current;
        }
        return Boolean.TRUE;
    }

    /**
     * Concept ce1 is subClass of Concept ce2 if all instances of ce1 are also instances of ce2
     * @param axiom axiom to visit
     * @return
     */
    public Boolean visit(OWLSubClassOfAxiom axiom){
        OWLClassExpression subClass = axiom.getSubClass();
        OWLClassExpression superClass = axiom.getSuperClass();

        Set<OWLNamedIndividual> subClassInstances = reasoner.getInstances(subClass).getFlattened();
        Set<OWLNamedIndividual> superClassInstances = reasoner.getInstances(superClass).getFlattened();

        return superClassInstances.containsAll(subClassInstances);
    }

    /**
     * Checks whether the Object Property is Transitive, and if so, checks whether the Assertion is entailed through the Transitivity
     * @param axiom axiom to visit
     * @return
     */
    public Boolean visit(OWLObjectPropertyAssertionAxiom axiom){
        logger.info("Visiting Object Property Assertion Axioms");
        logger.info("Start: " + axiom.getSubject());
        logger.info("Goal: " + axiom.getObject());


        Set<OWLObjectPropertyAssertionAxiom> objectPropertyAssertionAxioms = ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);

        if(objectPropertyAssertionAxioms.contains(axiom)){
            logger.info("Axiom already contained in ontology");
            return true;
        }
        logger.info("Checking Transitive Properties");
        objectPropertyAssertionAxioms = objectPropertyAssertionAxioms.stream().filter(ax -> ax.getProperty().equals(axiom.getProperty())).collect(Collectors.toSet());
        List<OWLIndividual> transitiveIndividuals = new LinkedList<>();
        Set<OWLIndividual> allConfirmedIndividuals = new HashSet<>();
        transitiveIndividuals.add(axiom.getSubject());
        while(!transitiveIndividuals.isEmpty()) {
            List<OWLIndividual> foundRelators = new LinkedList<>();
            for (OWLObjectPropertyAssertionAxiom assertionAxiom : objectPropertyAssertionAxioms) {
                for (OWLIndividual individual : transitiveIndividuals) {
                    if (assertionAxiom.getSubject().equals(individual)) {
                        allConfirmedIndividuals.add(assertionAxiom.getObject());
                        foundRelators.add(assertionAxiom.getObject());
                    }
                }
            }
            transitiveIndividuals = new LinkedList<>(foundRelators);

        }
        logger.info("Found Transitive Related Individuals: ");
        logger.info(allConfirmedIndividuals.toString());
        return allConfirmedIndividuals.contains(axiom.getObject());
    }


    /**
     * A class assertion axiom of a Named Individual NI to a class C is entailed if the NI is asserted to be part of a subclass of the class C
     *
     * @param axiom Visited Axiom of type OWLClassAssertionAxiom
     * @return true if the axiom is entailed in the ontology
     */
    public Boolean visit(OWLClassAssertionAxiom axiom) {
        logger.info("Visiting Class Assertion Axiom");
        OWLNamedIndividual individual = axiom.getIndividual().asOWLNamedIndividual();
        Set<OWLClassAssertionAxiom> classAssertionsOfIndividual = ontology.getClassAssertionAxioms(individual);
        OWLClassExpression axiomClassExpression = axiom.getClassExpression();
        logger.info("Individual: {}",  individual);
        logger.info("Class Expression: {}", axiomClassExpression);

        // If Individual is instance of ClassExpression -> Assertion is Entailed
        logger.info("Checking if Individual is Instance of ClassExpression");
        Set<OWLNamedIndividual> instancesOfClassExpression = reasoner.getInstances(axiomClassExpression).getFlattened();
        if(instancesOfClassExpression.contains(individual)){
            logger.info("Individual is Instance");
            return Boolean.TRUE;
        }

        // If Asserted ClassExpression is Super Class of a Class the individual is already instance of return true
        logger.info("Getting SuperClasses of Class Expression");
        Set<OWLClass> superClasses = getSuperClassesOfNI(ontology, individual, reasoner);
        try {
            logger.info("Found Super Classes: {}", superClasses);
            if (superClasses.contains(axiomClassExpression.asOWLClass())) return true;
        } catch (ClassCastException ignored){}

        // If Asserted Class is Equivalent Class of a Class the Individual is already instance of return true
        // If Class Assertion is for OWL Class:
        logger.info("Checking if Individual is Instance of an Equivalent Class of Class Expression");
        return switch (axiomClassExpression.getClassExpressionType()) {
            case OWL_CLASS -> equivalentForClass(ontology, (OWLClass) axiomClassExpression, individual);
            case OBJECT_INTERSECTION_OF -> equivalentForIntersection(ontology, (OWLObjectIntersectionOf) axiomClassExpression, individual);
            case OBJECT_UNION_OF -> equivalentForUnion(ontology, (OWLObjectUnionOf) axiomClassExpression, individual);
            default -> false;
        };
    }

    private Boolean equivalentForUnion(OWLOntology ontology, OWLObjectUnionOf axiomClass, OWLNamedIndividual individual) {
        Set<OWLClassExpression> equivalentClassExpressions = getEquivalentClassesOfComplexClassExpression(ontology, axiomClass);
        for(OWLClassExpression expression : equivalentClassExpressions){
            if(reasoner.getInstances(expression).containsEntity(individual)) return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    /**
     *
     * @param ontology
     * @param axiomClass
     * @return
     */
    private Boolean equivalentForIntersection(OWLOntology ontology, OWLObjectIntersectionOf axiomClass, OWLNamedIndividual individual) {
        Set<OWLClassExpression> equivalentClassExpressions = getEquivalentClassesOfComplexClassExpression(ontology, axiomClass);
        for(OWLClassExpression expression : equivalentClassExpressions){
            if(!reasoner.getInstances(expression).containsEntity(individual)) return Boolean.FALSE;
        }
        return Boolean.TRUE;

    }

    private Boolean equivalentForClass(OWLOntology ontology, OWLClass axiomClass, OWLNamedIndividual individual){
        Set<OWLEquivalentClassesAxiom> equivalentClassesAxiomSet = ontology.getEquivalentClassesAxioms(axiomClass);
        // If individual is instance of equivalent class -> instance of asserted class
        for (OWLEquivalentClassesAxiom axiom : equivalentClassesAxiomSet){
            for (OWLClassExpression expression : axiom.getClassExpressions()){
                try {
                    Set<OWLNamedIndividual> instancesOfEquivalentClass = reasoner.getInstances(expression).getFlattened();
                    if (instancesOfEquivalentClass.contains(individual)) return Boolean.TRUE;
                }
                catch(Exception ignored){
                }
            }
        }

        Set<OWLClassAssertionAxiom> classAssertionAxioms = ontology.getClassAssertionAxioms(individual);
        for (OWLClassAssertionAxiom axiom : classAssertionAxioms){
            OWLClassExpression classExpression = axiom.getClassExpression();
            Set<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(classExpression).getEntities();
            equivalentClasses.remove(axiomClass);
            for(OWLClass owlClass : equivalentClasses){
                if(reasoner.getInstances(owlClass).containsEntity(individual)) return Boolean.TRUE;
            }
        }
        return false;
    }

    private static Set<OWLClass> reduceSets(Set<OWLClass> accumulator, Set<OWLClass> value) {
        accumulator.addAll(value);
        return accumulator;
    }


    private static Set<OWLClass> getSuperClassesOfNI(OWLOntology ontology, OWLNamedIndividual ind, Reasoner reasoner) {
        Set<OWLClassAssertionAxiom> axiomSet = ontology.getClassAssertionAxioms(ind);
        return axiomSet.stream()
                .map(HasClassesInSignature::getClassesInSignature)
                .map(
                        (c) -> c.stream()
                                .map(cls -> (OWLClassExpression) cls)
                                .map(e -> reasoner.getSuperClasses(e, false).getFlattened())
                                .reduce(new HashSet<>(), EntailmentVisitor::reduceSets)
                ).reduce(
                        new HashSet<>(), EntailmentVisitor::reduceSets
                );
    }

    private static Set<OWLClassExpression> getEquivalentClassesOfComplexClassExpression(OWLOntology ontology, OWLClassExpression axiomClass) {
        Set<OWLAxiom> declarations = ontology.getAxioms().stream().filter(owlAxiom -> owlAxiom.getAxiomType().equals(AxiomType.DECLARATION)).collect(Collectors.toSet());
        Set<OWLClassExpression> equivalentClassExpressions = new HashSet<>();
        for(OWLAxiom axiom : declarations){
            if(axiom.getClassesInSignature().size() != 1) continue;
            for(OWLClass classExpression : axiom.getClassesInSignature()) {
                Set<OWLEquivalentClassesAxiom> equivalentClassAxioms = ontology.getEquivalentClassesAxioms(classExpression);
                Set<OWLClassExpression> equivalentExpressions = equivalentClassAxioms.stream().map(equivalentClassesAxiom -> equivalentClassesAxiom.getClassExpressionsMinus(classExpression)).reduce(new HashSet<>(), (acc, val) -> {acc.addAll(val); return acc;});
                if(equivalentExpressions.contains(axiomClass)) equivalentClassExpressions.add(classExpression);
            }
        }
        return equivalentClassExpressions;
    }

}
