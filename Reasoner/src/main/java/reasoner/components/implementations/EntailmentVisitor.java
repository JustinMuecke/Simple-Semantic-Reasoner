package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import reasoner.Reasoner;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.addAll;

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
     * Visit EACH axiom in entailment method to check wether they hold up
     * @param axiom
     * @return
     */

    //__________________ VISITOR PATTERN METHODS ________________________

    //_____               TRIVIAL AXIOMS              ___________________

    public Boolean visit(OWLAnnotationAssertionAxiom axiom) {
        return Boolean.TRUE;
    }
    public Boolean visit(OWLSubAnnotationPropertyOfAxiom axiom) {
        return Boolean.TRUE;
    }
    public Boolean visit(OWLAnnotationPropertyDomainAxiom axiom) {
        return Boolean.TRUE;
    }
    public Boolean visit(OWLAnnotationPropertyRangeAxiom axiom) {
        return Boolean.TRUE;
    }
    public Boolean visit(OWLDeclarationAxiom axiom) {
        return Boolean.TRUE;
    }


    //______              ASSERTIONS            _____

    /**
     * A class assertion axiom of a Named Individual NI to a class C is entailed if the NI is asserted to be part of a subclass of the class C
     * @param axiom
     * @return
     */
    public Boolean visit(OWLClassAssertionAxiom axiom){
        OWLNamedIndividual ind = axiom.getIndividual().asOWLNamedIndividual();
        Set<OWLClassAssertionAxiom> axiomSet = ontology.getClassAssertionAxioms(ind);

        if(axiomSet.contains(axiom)) return true;

        Set<OWLClass> superClasses = getSuperClassesOfNI(ontology, ind, reasoner);
        Set<OWLClass> axiomClass = axiom.getClassesInSignature();
        for(OWLClass c : axiomClass){
            if(!superClasses.contains(c)) return false;
        }
        return true;

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
                                .reduce(new HashSet<>(c), EntailmentVisitor::reduceSets)
                ).reduce(
                        new HashSet<>(), EntailmentVisitor::reduceSets
                );
    }
}
