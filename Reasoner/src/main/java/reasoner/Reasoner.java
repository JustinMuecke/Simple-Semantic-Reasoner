package reasoner;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.impl.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.Version;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reasoner implements OWLReasoner {


    private OWLOntology ontology;
    private OWLDataFactory factory;

    public Reasoner(OWLOntology ontology){
        this.ontology = ontology;
        this.factory = ontology.getOWLOntologyManager().getOWLDataFactory();
    }

    // __________________ INDIVIDUALS ______________________________
    @Override
    public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression owlClassExpression, boolean b) {
        DefaultNodeSet<OWLNamedIndividual> result = new OWLNamedIndividualNodeSet();
        var namedIndividuals = ontology.getIndividualsInSignature();
        for(OWLNamedIndividual ind : namedIndividuals){
            Set<OWLClassExpression> expr = ontology.classAssertionAxioms(ind)
                    .map(OWLClassAssertionAxiom::getClassExpression)
                    .collect(Collectors.toSet());
            switch (owlClassExpression.getClassExpressionType()){
                case OWL_CLASS -> {if(expr.contains(owlClassExpression)) result.addEntity(ind);}
                case OBJECT_INTERSECTION_OF -> {if(expr.containsAll(owlClassExpression.asConjunctSet())) result.addEntity(ind);}
                case OBJECT_UNION_OF -> {if(expr.stream().anyMatch(i->owlClassExpression.asDisjunctSet().contains(i))) result.addEntity(ind);}
            }
        }
        return result;
    }


    @Override
    public NodeSet<OWLClass> getTypes(OWLNamedIndividual owlNamedIndividual, boolean b) {
        DefaultNodeSet<OWLClass> classes = new OWLClassNodeSet();
        Set<OWLClass> cls = ontology.classAssertionAxioms(owlNamedIndividual)
                .map(OWLClassAssertionAxiom::getClassExpression)
                .map(AsOWLClass::asOWLClass)
                .collect(Collectors.toSet());
        for(OWLClass cl : cls){
            classes.addEntity(cl);
        }
        return classes;
    }




    @Override
    public NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual owlNamedIndividual, OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Override
    public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual owlNamedIndividual, OWLDataProperty owlDataProperty) {
        return null;
    }

    @Override
    public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual owlNamedIndividual) {
        return null;
    }

    @Override
    public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual owlNamedIndividual) {
        return null;
    }


    @Override
    public void precomputeInferences(InferenceType... inferenceTypes) {

    }

    @Override
    public boolean isSatisfiable(OWLClassExpression owlClassExpression) {
        return false;
    }



    @Override
    public String getReasonerName() {
        return "SSWR";
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
        return null;
    }

    @Override
    public void interrupt() {

    }



    @Override
    public boolean isPrecomputed(InferenceType inferenceType) {
        return false;
    }

    @Override
    public Set<InferenceType> getPrecomputableInferenceTypes() {
        return null;
    }

    @Override
    public boolean isConsistent() {
        return false;
    }



    @Override
    public Node<OWLClass> getUnsatisfiableClasses() {
        return null;
    }

    @Override
    public boolean isEntailed(OWLAxiom owlAxiom) {
        return false;
    }

    @Override
    public boolean isEntailed(Set<? extends OWLAxiom> set) {
        return false;
    }

    @Override
    public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
        return false;
    }

    @Override
    public Node<OWLClass> getTopClassNode() {
        return null;
    }

    @Override
    public Node<OWLClass> getBottomClassNode() {
        return null;
    }

    @Override
    public NodeSet<OWLClass> getSubClasses(OWLClassExpression owlClassExpression, boolean b) {
        return null;
    }

    @Override
    public NodeSet<OWLClass> getSuperClasses(OWLClassExpression owlClassExpression, boolean b) {
        return null;
    }

    @Override
    public Node<OWLClass> getEquivalentClasses(OWLClassExpression owlClassExpression) {
        return null;
    }

    @Override
    public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression owlClassExpression) {
        return null;
    }

    @Override
    public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
        return null;
    }

    @Override
    public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
        return null;
    }

    @Override
    public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    @Override
    public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    @Override
    public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Override
    public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Override
    public Node<OWLObjectPropertyExpression> getInverseObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Override
    public NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    @Override
    public NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    @Override
    public Node<OWLDataProperty> getTopDataPropertyNode() {
        return null;
    }

    @Override
    public Node<OWLDataProperty> getBottomDataPropertyNode() {
        return null;
    }

    @Override
    public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty owlDataProperty, boolean b) {
        return null;
    }

    @Override
    public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty owlDataProperty, boolean b) {
        return null;
    }

    @Override
    public Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty owlDataProperty) {
        return null;
    }

    @Override
    public NodeSet<OWLDataProperty> getDisjointDataProperties(OWLDataPropertyExpression owlDataPropertyExpression) {
        return null;
    }

    @Override
    public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty owlDataProperty, boolean b) {
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
