package reasoner;
import javax.annotation.ParametersAreNonnullByDefault;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import reasoner.components.EntailmentChecker;
import reasoner.components.implementations.*;

import java.util.*;

public class Reasoner implements ALCReasoner {


    private final OWLOntology ontology;
    private final OWLDataFactory df;
    private final List<OWLOntologyChange> ontologyChanges;

    public Reasoner(OWLOntology ontology, OWLDataFactory df){
        this.ontology = ontology;
        this.df = df;
        this.ontologyChanges = new LinkedList<>();
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
        return new SimpleInstanceRetriever(ontology, df).getInstances(owlClassExpression, b);
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
            case "OWLSubClassOfAxiom" -> true;
            default -> false;
        };
    }
    @ParametersAreNonnullByDefault
    public boolean isEntailed(OWLAxiom owlAxiom) {
        EntailmentChecker entailmentChecker = new SimpleEntailmentChecker(this, ontology);
        if(entailmentChecker.isEntailed(owlAxiom)){
            OWLOntologyChange addition = new AddAxiom(ontology, owlAxiom);
            ontologyChanges.add(addition);
            return true;
        }
        return false;
    }
    @Override
    @ParametersAreNonnullByDefault
    public boolean isEntailed(Set<? extends OWLAxiom> set) {
        Optional<Set<OWLAxiom>> entailedAxioms = new SimpleEntailmentChecker(this,ontology).isEntailed(set);
        if(entailedAxioms.isPresent()){
            for(OWLAxiom owlAxiom : entailedAxioms.get()){
                OWLOntologyChange addition = new AddAxiom(ontology, owlAxiom);
                ontologyChanges.add(addition);
            }
            return set.size() == entailedAxioms.get().size();
        }
        return false;
    }




    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty owlDataProperty, boolean b) {
        return null;
    }
    @Override@ParametersAreNonnullByDefault
    public boolean isSatisfiable(OWLClassExpression owlClassExpression) {
        return false;
    }

    @Override
    public List<OWLOntologyChange> getPendingChanges() {
        return ontologyChanges;
    }

    public boolean applyPendingChanges(){
        try {
            for (OWLOntologyChange change : ontologyChanges) {
                if (change.isAddAxiom()) ontology.add(change.getAxiom());
                if (change.isRemoveAxiom()) ontology.remove(change.getAxiom());
            }
            return true;
        } catch (Exception e){
            return false;
        }
    }

}
