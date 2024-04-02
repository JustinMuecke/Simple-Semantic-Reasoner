package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.util.EscapeUtils;
import reasoner.Reasoner;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class EntailmentVisitor implements OWLAxiomVisitorEx<Boolean> {

    OWLOntology ontology;

    /**
     * Be Careful of Cyclic Behaviour
     */
    Reasoner reasoner;

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
     * A class assertion axiom of a Named Individual NI to a class C is entailed if the NI is asserted to be part of a subclass of the class C
     *
     * @param axiom Visited Axiom of type OWLClassAssertionAxiom
     * @return true if the axiom is entailed in the ontology
     */
    public Boolean visit(OWLClassAssertionAxiom axiom) {
        OWLNamedIndividual individual = axiom.getIndividual().asOWLNamedIndividual();
        Set<OWLClassAssertionAxiom> classAssertionsOfIndividual = ontology.getClassAssertionAxioms(individual);
        OWLClassExpression axiomClassExpression = axiom.getClassExpression();

        Set<OWLNamedIndividual> instancesOfClassExpression = reasoner.getInstances(axiomClassExpression).getFlattened();
        if(instancesOfClassExpression.contains(individual)) return Boolean.TRUE;


        // If axioms already in ontology trivially return true
        if (classAssertionsOfIndividual.contains(axiom)) return true;

        // If Asserted ClassExpression is Super Class of a Class the individual is already instance of return true
        Set<OWLClass> superClasses = getSuperClassesOfNI(ontology, individual, reasoner);
        try {
            if (superClasses.contains(axiomClassExpression.asOWLClass())) return true;
        } catch (ClassCastException ignored){}
        // If Asserted Class is Equivalent Class of a Class the Individual is already instance of return true
        // If Class Assertion is for OWL Class:
        switch(axiomClassExpression.getClassExpressionType()){
            case OWL_CLASS : if(equivalentForClass(ontology, (OWLClass) axiomClassExpression, individual)) return Boolean.TRUE; break;
            case OBJECT_INTERSECTION_OF: if( equivalentForIntersection(ontology,  axiomClassExpression)) return Boolean.TRUE; break;
            case OBJECT_UNION_OF: if(equivalentForUnion(ontology, axiomClassExpression)) return Boolean.TRUE; break;
        }
        return Boolean.FALSE;
    }

    private Boolean equivalentForUnion(OWLOntology ontology, OWLClassExpression axiomClass) {
        System.out.println("UNION0");
        return false;
    }

    private Boolean equivalentForIntersection(OWLOntology ontology, OWLClassExpression axiomClass) {
        System.out.println("INTERSECTION");
        System.out.println(axiomClass);
        System.out.println(axiomClass.getClassExpressionType());
        return Boolean.FALSE;
    }

    private Boolean equivalentForClass(OWLOntology ontology, OWLClass axiomClass, OWLNamedIndividual individual){
        Set<OWLEquivalentClassesAxiom> equivalentClassesAxiomSet = ontology.getEquivalentClassesAxioms(axiomClass);
        Set<OWLClassAssertionAxiom> classAssertionAxioms = ontology.getClassAssertionAxioms(individual);
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
        for (OWLClassAssertionAxiom axiom : classAssertionAxioms){
            OWLClassExpression classExpression = axiom.getClassExpression();
            Set<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(classExpression).getEntities();
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

}
