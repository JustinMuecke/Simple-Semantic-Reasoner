package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import reasoner.Reasoner;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
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
     * A class assertion axiom of a Named Individual NI to a class C is entailed if the NI is asserted to be part of a subclass of the class C
     * @param axiom Visited Axiom of type OWLClassAssertionAxiom
     * @return true if the axiom is entailed in the ontology
     */
    public Boolean visit(OWLClassAssertionAxiom axiom){
        OWLNamedIndividual ind = axiom.getIndividual().asOWLNamedIndividual();
        Set<OWLClassAssertionAxiom> axiomSet = ontology.getClassAssertionAxioms(ind);
        // If axioms already in ontology trivially return true
        if(axiomSet.contains(axiom)) return true;
        // If Asserted Class is Super Class of a Class the individual is already instance of return true
        Set<OWLClass> superClasses = getSuperClassesOfNI(ontology, ind, reasoner);
        Set<OWLClass> axiomClass = axiom.getClassesInSignature();
        for(OWLClass c : axiomClass){
            if(superClasses.contains(c)) return true;
        }
        // If Asserted Class is Equivalent Class of a Class the Individual is already instance of return true
        for(OWLClass c : axiomClass){
            Set<OWLEquivalentClassesAxiom> equivalentClassesAxiomSet = ontology.getEquivalentClassesAxioms(c);
            Set<OWLClass> equivalentClasses = equivalentClassesAxiomSet.stream()
                    .map(HasClassesInSignature::getClassesInSignature)
                    .reduce(new HashSet<>(),EntailmentVisitor::reduceSets);
            Set<OWLClass> individualClasses = reasoner.getTypes(ind).getFlattened()
                    .stream()
                    .filter(equivalentClasses::contains)
                    .collect(Collectors.toSet());
            if(!individualClasses.isEmpty()) return true;
        }
        return false;

    }
    private static Set<OWLClass> reduceSets(Set<OWLClass> accumulator, Set<OWLClass> value){
        accumulator.addAll(value);
        return accumulator;
    }

    private static Set<OWLClass> getSuperClassesOfNI(OWLOntology ontology, OWLNamedIndividual ind, Reasoner reasoner){
        Set<OWLClassAssertionAxiom> axiomSet = ontology.getClassAssertionAxioms(ind);
        return axiomSet.stream()
                .map(HasClassesInSignature::getClassesInSignature)
                .map(
                        (c) -> c.stream()
                                .map(e -> reasoner.getSuperClasses(e, false).getFlattened())
                                .reduce(new HashSet<>(), EntailmentVisitor::reduceSets)
                ).reduce(
                        new HashSet<>(), EntailmentVisitor::reduceSets
                );
    }
}
