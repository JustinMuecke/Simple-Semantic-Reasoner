package reasoner;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.impl.*;
import org.semanticweb.owlapi.util.Version;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;

public interface ALCReasoner extends OWLReasoner {

    @Override
    default Node<OWLClass> getTopClassNode() {
        return new OWLClassNode();
    }

    @Override
    default Node<OWLClass> getBottomClassNode() {
        return new OWLClassNode();
    }
    @Override
    default Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
        return new OWLObjectPropertyNode();
    }

    @Override
    default Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
        return new OWLObjectPropertyNode();
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return new OWLObjectPropertyNodeSet();
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return new OWLObjectPropertyNodeSet();
    }

    @Override@ParametersAreNonnullByDefault
    default Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return new OWLObjectPropertyNode();
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return new OWLObjectPropertyNodeSet();
    }


    @Override@ParametersAreNonnullByDefault
    default Node<OWLObjectPropertyExpression> getInverseObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return new OWLObjectPropertyNode();
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return new OWLClassNodeSet();
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return new OWLClassNodeSet();
    }

    // ____________________________ DATA PROPERTY _________________________________________________
    @Override
    default Node<OWLDataProperty> getTopDataPropertyNode() {
        return new OWLDataPropertyNode();
    }

    @Override
    default Node<OWLDataProperty> getBottomDataPropertyNode() {
        return new OWLDataPropertyNode();
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty owlDataProperty, boolean b) {
        return new OWLDataPropertyNodeSet();
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty owlDataProperty, boolean b) {
        return new OWLDataPropertyNodeSet();
    }

    @Override@ParametersAreNonnullByDefault
    default Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty owlDataProperty) {
        return new OWLDataPropertyNode();
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLDataProperty> getDisjointDataProperties(OWLDataPropertyExpression owlDataPropertyExpression) {
        return new OWLDataPropertyNodeSet();
    }    @Override    @ParametersAreNonnullByDefault
    default Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual owlNamedIndividual, OWLDataProperty owlDataProperty) {
        return new HashSet<>();
    }
    @Override    @ParametersAreNonnullByDefault
    default NodeSet<OWLNamedIndividual> getObjectPropertyValues(OWLNamedIndividual owlNamedIndividual, OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return new OWLNamedIndividualNodeSet();
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty owlDataProperty, boolean b) {
        return new OWLClassNodeSet();
    }

    @Override@ParametersAreNonnullByDefault
    default void precomputeInferences(InferenceType... inferenceTypes) {

    }

    @Override
    default Version getReasonerVersion() {
        return new Version(0, 0, 0, 0);
    }
    @Override
    default BufferingMode getBufferingMode() {
        return BufferingMode.NON_BUFFERING;
    }
    @Override
    default void flush() {

    }

    @Override
    default Set<OWLAxiom> getPendingAxiomAdditions() {
        return new HashSet<>();
    }
    @Override
    default Set<OWLAxiom> getPendingAxiomRemovals() {
        return new HashSet<>();
    }

    @Override
    default void interrupt() {

    }
    @Override@ParametersAreNonnullByDefault
    default boolean isPrecomputed(InferenceType inferenceType) {
        return false;
    }
    @Override
    default Set<InferenceType> getPrecomputableInferenceTypes() {
        return new HashSet<>();
    }
    @Override
    default Node<OWLClass> getUnsatisfiableClasses() {
        return new OWLClassNode();
    }
    @Override
    default long getTimeOut() {
        return 0;
    }

    @Override
    default FreshEntityPolicy getFreshEntityPolicy() {
        return FreshEntityPolicy.DISALLOW;
    }

    @Override
    default IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return IndividualNodeSetPolicy.BY_NAME;
    }

    @Override
    default void dispose() {

    }
}
