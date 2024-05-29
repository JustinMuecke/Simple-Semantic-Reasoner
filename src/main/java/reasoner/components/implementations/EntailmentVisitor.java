package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reasoner.Reasoner;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

/**
 * For each possible axiom type, a custom method to check whether an axiom of that type is entailed.
 *
 */
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
    @Override
    @ParametersAreNonnullByDefault
    public Boolean visit(OWLDeclarationAxiom axiom) {
        return Boolean.TRUE;
    }


    //______              ASSERTIONS            _____
    /**
     * Two Concepts are equivalent if they have the same extension, i.e. they all instance the same individuals
     * @param axiom Axiom which is supposed to be entailed.
     * @return Boolean representing whether the axiom is entailed.
     */
    @Override
    public Boolean visit(OWLEquivalentClassesAxiom axiom) {
        logger.info("Visiting Equivalent Classes Axiom");
        Set<OWLClassExpression> ceSet = axiom.getClassExpressions();
        List<Set<OWLNamedIndividual>> individuals = ceSet.stream().map(ce -> reasoner.getInstances(ce, false).getFlattened()).toList();
        logger.info("Retrieved instances of all classes");
        if(individuals.get(0).equals(individuals.get(1))){
            logger.info("Instances are equivalent.");
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * Two classes can be defined as disjoint of their extensions have no intersection
     * @param axiom Axiom which is supposed to be entailed.
     * @return Boolean representing whether the axiom is entailed.
     */
    @Override
    @ParametersAreNonnullByDefault
    public Boolean visit(OWLDisjointClassesAxiom axiom){
        Set<OWLClassExpression> ceSet = axiom.getClassExpressions();
        Set<NodeSet<OWLNamedIndividual>> extensions = ceSet.stream().map(ce -> reasoner.getInstances(ce)).collect(Collectors.toSet());
        logger.info("Retrieved instances of all classes");
        Iterator<NodeSet<OWLNamedIndividual>> iter = extensions.iterator();
        NodeSet<OWLNamedIndividual> last = iter.next();
        while(extensions.iterator().hasNext()){
            NodeSet<OWLNamedIndividual> current = iter.next();
            if(last.getFlattened().stream().anyMatch(c -> current.getFlattened().contains(c))){
                logger.info("Classes share at least on instance, thus not disjointed.");
                return Boolean.FALSE;
            }
            last = current;
        }
        return Boolean.TRUE;
    }

    /**
     * Concept ce1 is subClass of Concept ce2 if all instances of ce1 are also instances of ce2
     * @param axiom axiom to visit
     * @return Boolean representing whether the axiom is entailed.
     */
    @Override
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
     * @return Boolean representing whether the axiom is entailed.
     */
    @Override
    public Boolean visit(OWLObjectPropertyAssertionAxiom axiom){
        logger.info("Visiting Object Property Assertion Axioms");
        logger.info("Start: {}", axiom.getSubject());
        logger.info("Goal: {}", axiom.getObject());


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
        if(!allConfirmedIndividuals.isEmpty()) {
            logger.info("Found Transitive Related Individuals: \n {}", allConfirmedIndividuals);
        }
        return allConfirmedIndividuals.contains(axiom.getObject());
    }


    /**
     * A class assertion axiom of a Named Individual NI to a class C is entailed if the NI is asserted to be part of a subclass of the class C
     *
     * @param axiom Visited Axiom of type OWLClassAssertionAxiom
     * @return true if the axiom is entailed in the ontology
     */
    @Override
    public Boolean visit(OWLClassAssertionAxiom axiom) {
        logger.info("Visiting Class Assertion Axiom");
        OWLNamedIndividual individual = axiom.getIndividual().asOWLNamedIndividual();
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
        } catch (ClassCastException e){
            logger.info("Superclasses do not contained target class.");
        }

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

    /**
     * Checks whether a named individual is part of any class expression which is part of the union.
     * @param ontology underlying ontology used by the reasoner.
     * @param axiomClass Class expression representing a union of classes.
     * @param individual Named individual which is supposed to be checked.
     * @return Boolean representing whether the named individual is in any class expression.
     */
    private Boolean equivalentForUnion(OWLOntology ontology, OWLObjectUnionOf axiomClass, OWLNamedIndividual individual) {
        Set<OWLClassExpression> equivalentClassExpressions = getEquivalentClassesOfComplexClassExpression(ontology, axiomClass);
        for(OWLClassExpression expression : equivalentClassExpressions){
            if(reasoner.getInstances(expression).containsEntity(individual)) return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    /**
     * Checks whether a named individual is part of all class expression which are part of the intersection.
     * @param ontology underlying ontology used by the reasoner.
     * @param axiomClass Class expression representing an intersection of classes.
     * @param individual Named individual which is supposed to be checked.
     * @return Boolean representing whether the named individual is in all class expression.
     */
    private Boolean equivalentForIntersection(OWLOntology ontology, OWLObjectIntersectionOf axiomClass, OWLNamedIndividual individual) {
        Set<OWLClassExpression> equivalentClassExpressions = getEquivalentClassesOfComplexClassExpression(ontology, axiomClass);
        for(OWLClassExpression expression : equivalentClassExpressions){
            if(!reasoner.getInstances(expression).containsEntity(individual)) return Boolean.FALSE;
        }
        return Boolean.TRUE;

    }

    /**
     * Finds all equivalent classes for a OWLClass axiom. If the individual in question is instance of any
     * of the equivalent classes, it is also instance of the class in question.
     * @param ontology underlying ontology used by the reasoner.
     * @param axiomClass Class expression representing a single class.
     * @param individual Named individual which is supposed to be checked.
     * @return Boolean representing whether the named individual is in any class expression.
     */
    private Boolean equivalentForClass(OWLOntology ontology, OWLClass axiomClass, OWLNamedIndividual individual){
        Set<OWLEquivalentClassesAxiom> equivalentClassesAxiomSet = ontology.getEquivalentClassesAxioms(axiomClass);
        logger.info("Found equivalent classes: {}", equivalentClassesAxiomSet);
        // If individual is instance of equivalent class -> instance of asserted class
        for (OWLEquivalentClassesAxiom axiom : equivalentClassesAxiomSet){
            for (OWLClassExpression expression : axiom.getClassExpressions()){
                try {
                    Set<OWLNamedIndividual> instancesOfEquivalentClass = reasoner.getInstances(expression, true).getFlattened();
                    if (instancesOfEquivalentClass.contains(individual)) return Boolean.TRUE;
                }
                catch(Exception e){
                    logger.info("No Equivalent class contains individual.");
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

    /**
     * Reduce Function for Sets
     * @param accumulator Accumulator which holds the result
     * @param value to be added to the Accumulator
     * @return Reduced Set
     */
    private static Set<OWLClass> reduceSets(Set<OWLClass> accumulator, Set<OWLClass> value) {
        accumulator.addAll(value);
        return accumulator;
    }

    /**
     * For all classes a named individual is instance of, find all of their super classes.
     * @param ontology underlying ontology used by the reasoner.
     * @param ind named individual in question.
     * @param reasoner the Reasoner used to get the super classes
     * @return set of all super classes
     */
    private static Set<OWLClass> getSuperClassesOfNI(OWLOntology ontology, OWLNamedIndividual ind, Reasoner reasoner) {
        Set<OWLClassAssertionAxiom> axiomSet = ontology.getClassAssertionAxioms(ind);
        return axiomSet.stream()
                .map(HasClassesInSignature::getClassesInSignature)
                .map(
                        c -> c.stream()
                                .map(OWLClassExpression.class::cast)
                                .map(e -> reasoner.getSuperClasses(e, false).getFlattened())
                                .reduce(new HashSet<>(), EntailmentVisitor::reduceSets)
                ).reduce(
                        new HashSet<>(), EntailmentVisitor::reduceSets
                );
    }

    /**
     * Retrieves all equivalent classes for a complex class expression.
     * @param ontology underlying ontology used by the reasoner.
     * @param axiomClass the complex class expression in question.
     * @return Set of OWLClassExpressions which are equivalent to the axiom class.
     */
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
