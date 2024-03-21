package reasoner;
import javax.annotation.ParametersAreNonnullByDefault;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.impl.*;
import org.semanticweb.owlapi.util.Version;
import reasoner.components.EntailmentChecker;
import reasoner.components.implementations.*;

import java.util.List;
import java.util.Set;

public class Reasoner implements ALCReasoner {


    private final OWLOntology ontology;

    public Reasoner(OWLOntology ontology){
        this.ontology = ontology;
    }


    @Override
    public OWLOntology getRootOntology() {
        return ontology;
    }
    @Override
    public String getReasonerName() {
        return "Simple Semantic Reasoner";
    }


    // __________________ INDIVIDUALS ______________________________
    @Override
    @ParametersAreNonnullByDefault
    public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression owlClassExpression, boolean b) {
        return new SimpleInstanceRetriever(ontology).getInstances(owlClassExpression, b);
    }
    @Override
    @ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getTypes(OWLNamedIndividual owlNamedIndividual, boolean b) {
        return new SimpleTypeRetriever(ontology).getTypes(owlNamedIndividual, b);

    }

    @Override    @ParametersAreNonnullByDefault
    public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual owlNamedIndividual) {
        return new SimpleSameIndividualRetriever(ontology).getSameIndividuals(owlNamedIndividual);
    }

    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual owlNamedIndividual) {
        return null;
    }

    @Override    @ParametersAreNonnullByDefault
    public NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual owlNamedIndividual, OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }
    @Override    @ParametersAreNonnullByDefault
    public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual owlNamedIndividual, OWLDataProperty owlDataProperty) {
        return null;
    }

    // ____________________ CLASS AXIOMS ___________________________________________________________________________

    @Override
    @ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getSubClasses(OWLClassExpression owlClassExpression, boolean b) {
        return new SimpleSubClassRetriever(ontology).getSubClasses(owlClassExpression,b);
    }
    @Override
    @ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getSuperClasses(OWLClassExpression owlClassExpression, boolean b) {
        return new SimpleSuperClassRetriever(ontology).getSuperClasses(owlClassExpression, b);
    }
    @Override    @ParametersAreNonnullByDefault
    public Node<OWLClass> getEquivalentClasses(OWLClassExpression owlClassExpression) {
        return new SimpleEquivalentClassRetriever(ontology).getEquivalentClasses(owlClassExpression);
    }
    @Override    @ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression owlClassExpression) {
        return new SimpleDisjointClassRetriever(ontology).getDisjointClasses(owlClassExpression);
    }
    //______________________________ CONSISTENCY _______________________________________________________________________
    public boolean isConsistent() {
            return false;
    }
    //______________________________ ENTAILMENT ________________________________________________________________________
    @Override
    public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
        return switch (axiomType.toString()) {
            case "OWLClassAssertion" -> true;
            case "OWLSubAnnotationPropertyOfAxiom" -> true;
            case "OWLAnnotationAssertionAxiom" -> true;
            case "OWLAnnotationPropertyDomainAxiom" -> true;
            case "OWLAnnotationPropertyRangeAxiom" -> true;
            case "OWLDeclarationAxiom" -> true;

            case "OWLClassAssertionAxiom" -> true;
            default -> false;
        };
    }
    @ParametersAreNonnullByDefault
    public boolean isEntailed(OWLAxiom owlAxiom) {
        EntailmentChecker entailmentChecker = new SimpleEntailmentChecker(this, ontology);
        return entailmentChecker.isEntailed(owlAxiom);
    }
    @Override
    @ParametersAreNonnullByDefault
    public boolean isEntailed(Set<? extends OWLAxiom> set) {
        return new SimpleEntailmentChecker(this, ontology).isEntailed(set);
    }
    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty owlDataProperty, boolean b) {
        return null;
    }
    @Override@ParametersAreNonnullByDefault
    public boolean isSatisfiable(OWLClassExpression owlClassExpression) {
        return false;
    }


}
