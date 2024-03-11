package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;

public class EntailmentVisitor implements OWLAxiomVisitorEx<Boolean> {


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

    public Boolean visit(OWLClassAssertionAxiom axiom){
        System.out.println("Entered OWLClassAssertion");
        OWLNamedIndividual ind = axiom.getIndividual().asOWLNamedIndividual();
        System.out.println(ind);
        System.out.println(axiom.getClassExpression());
        return Boolean.FALSE;

    }
}
