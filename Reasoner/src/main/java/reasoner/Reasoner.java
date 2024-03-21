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

public class Reasoner implements OWLReasoner {


    private final OWLOntology ontology;

    public Reasoner(OWLOntology ontology){
        this.ontology = ontology;
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

    // ________ // ____________ CLASS AXIOMS ___________________________________________________________________________
    @Override
    public Node<OWLClass> getTopClassNode() {
        return null;
    }

    @Override
    public Node<OWLClass> getBottomClassNode() {
        return null;
    }


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




    // ____________________________________ OBJECT PROPERTY ___________________________________________________

    @Override
    public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
        return null;
    }

    @Override
    public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    public Node<OWLObjectPropertyExpression> getInverseObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    // ____________________________ DATA PROPERTY _________________________________________________
    @Override
    public Node<OWLDataProperty> getTopDataPropertyNode() {
        return null;
    }

    @Override
    public Node<OWLDataProperty> getBottomDataPropertyNode() {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty owlDataProperty, boolean b) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty owlDataProperty, boolean b) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    public Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty owlDataProperty) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLDataProperty> getDisjointDataProperties(OWLDataPropertyExpression owlDataPropertyExpression) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty owlDataProperty, boolean b) {
        return null;
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
    public void precomputeInferences(InferenceType... inferenceTypes) {

    }

    @Override@ParametersAreNonnullByDefault
    public boolean isSatisfiable(OWLClassExpression owlClassExpression) {
        return false;
    }







    @Override
    public String getReasonerName() {
        return "Simple Semantic Reasoner";
    }
    @Override
    public Version getReasonerVersion() {
        return null;
    }
    @Override
    public BufferingMode getBufferingMode() {
        return null;
    }
    @Override
    public void flush() {

    }
    @Override
    public List<OWLOntologyChange> getPendingChanges() {
        return null;
    }
    @Override
    public Set<OWLAxiom> getPendingAxiomAdditions() {
        return null;
    }
    @Override
    public Set<OWLAxiom> getPendingAxiomRemovals() {
        return null;
    }
    @Override
    public OWLOntology getRootOntology() {
        return ontology;
    }
    @Override
    public void interrupt() {

    }
    @Override@ParametersAreNonnullByDefault
    public boolean isPrecomputed(InferenceType inferenceType) {
        return false;
    }
    @Override
    public Set<InferenceType> getPrecomputableInferenceTypes() {
        return null;
    }
    @Override
    public Node<OWLClass> getUnsatisfiableClasses() {
        return null;
    }
    @Override
    public long getTimeOut() {
        return 0;
    }

    @Override
    public FreshEntityPolicy getFreshEntityPolicy() {
        return null;
    }

    @Override
    public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return null;
    }

    @Override
    public void dispose() {

    }
}
