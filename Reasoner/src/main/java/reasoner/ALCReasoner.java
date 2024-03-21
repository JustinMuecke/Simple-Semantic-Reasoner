package reasoner;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.Version;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Set;

public interface ALCReasoner extends OWLReasoner {

    @Override
    default Node<OWLClass> getTopClassNode() {
        return null;
    }

    @Override
    default Node<OWLClass> getBottomClassNode() {
        return null;
    }
    @Override
    default Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
        return null;
    }

    @Override
    default Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    default Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }


    @Override@ParametersAreNonnullByDefault
    default Node<OWLObjectPropertyExpression> getInverseObjectProperties(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLClass> getObjectPropertyDomains(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLClass> getObjectPropertyRanges(OWLObjectPropertyExpression owlObjectPropertyExpression, boolean b) {
        return null;
    }

    // ____________________________ DATA PROPERTY _________________________________________________
    @Override
    default Node<OWLDataProperty> getTopDataPropertyNode() {
        return null;
    }

    @Override
    default Node<OWLDataProperty> getBottomDataPropertyNode() {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty owlDataProperty, boolean b) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty owlDataProperty, boolean b) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    default Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty owlDataProperty) {
        return null;
    }

    @Override@ParametersAreNonnullByDefault
    default NodeSet<OWLDataProperty> getDisjointDataProperties(OWLDataPropertyExpression owlDataPropertyExpression) {
        return null;
    }


    @Override@ParametersAreNonnullByDefault
    default void precomputeInferences(InferenceType... inferenceTypes) {

    }

    @Override
    default Version getReasonerVersion() {
        return null;
    }
    @Override
    default BufferingMode getBufferingMode() {
        return null;
    }
    @Override
    default void flush() {

    }
    @Override
    default List<OWLOntologyChange> getPendingChanges() {
        return null;
    }
    @Override
    default Set<OWLAxiom> getPendingAxiomAdditions() {
        return null;
    }
    @Override
    default Set<OWLAxiom> getPendingAxiomRemovals() {
        return null;
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
        return null;
    }
    @Override
    default Node<OWLClass> getUnsatisfiableClasses() {
        return null;
    }
    @Override
    default long getTimeOut() {
        return 0;
    }

    @Override
    default FreshEntityPolicy getFreshEntityPolicy() {
        return null;
    }

    @Override
    default IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
        return null;
    }

    @Override
    default void dispose() {

    }
}
