package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
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
     * A class assertion axiom of a Named Individual NI to a class C is entailed if the NI is asserted to be part of a subclass of the class C
     *
     * @param axiom Visited Axiom of type OWLClassAssertionAxiom
     * @return true if the axiom is entailed in the ontology
     */
    public Boolean visit(OWLClassAssertionAxiom axiom) {
        OWLNamedIndividual ind = axiom.getIndividual().asOWLNamedIndividual();
        Set<OWLClassAssertionAxiom> axiomSet = ontology.getClassAssertionAxioms(ind);
        // If axioms already in ontology trivially return true
        if (axiomSet.contains(axiom)) return true;

        // If Asserted ClassExpression is Super Class of a Class the individual is already instance of return true
        Set<OWLClass> superClasses = getSuperClassesOfNI(ontology, ind, reasoner);
        OWLClassExpression axiomClass = axiom.getClassExpression();
        if (superClasses.contains(axiomClass.asOWLClass())) return true;

        // If Asserted Class is Equivalent Class of a Class the Individual is already instance of return true
        // If Class Assertion is for OWL Class:
        switch(axiomClass.getClassExpressionType()){
            case OWL_CLASS : if(equivalentForClass(ontology, (OWLClass) axiomClass, ind)) return Boolean.TRUE;
            //case OBJECT_INTERSECTION_OF: if( equivalentForIntersection(ontology, (OWLClass) axiomClass)) return Boolean.TRUE;
            //case OBJECT_UNION_OF: if(equivalentForUnion(ontology, (OWLClass) axiomClass)) return Boolean.TRUE;
        }



        return Boolean.FALSE;
    }

    private Boolean equivalentForClass(OWLOntology ontology, OWLClass axiomClass, OWLNamedIndividual individual){
        Set<OWLEquivalentClassesAxiom> equivalentClassesAxiomSet = ontology.getEquivalentClassesAxioms(axiomClass);
        Set<OWLClassAssertionAxiom> classAssertionAxioms = ontology.getClassAssertionAxioms(individual);
        // If individual is instance of equivalent class -> instance of asserted class
        for (OWLEquivalentClassesAxiom axiom : equivalentClassesAxiomSet){
            System.out.println(axiom);
            for (OWLClassExpression expression : axiom.getClassExpressions()){
                System.out.println("llll");System.out.println(expression);
                try {
                    if (reasoner.getInstances(expression).getFlattened().contains(individual)) return Boolean.TRUE;
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
