package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import reasoner.components.DisjointClassRetriever;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleDisjointClassRetriever implements DisjointClassRetriever {

    private final OWLOntology ontology;

    public SimpleDisjointClassRetriever(OWLOntology ontology) {
        this.ontology = ontology;
    }

    /**
     * Gets all disjoint classes axioms, takes all classes in the signature of the axiom into a set and filters
     * out the parameter class expression.
     * @param owlClassExpression The class expression whose disjoint classes are to be retrieved.
     * @return NodeSet containing all disjoint classes.
     */
    @Override
    public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression owlClassExpression) {
        OWLClass cls = owlClassExpression.asOWLClass();
        Set<OWLDisjointClassesAxiom> axiomSet = ontology.getDisjointClassesAxioms(cls);
        Set<OWLClass> disjointClasses = axiomSet.stream()
                .map(HasClassesInSignature::getClassesInSignature)
                .filter(classesInSignature -> classesInSignature.contains(cls))
                .flatMap(Collection::stream)
                .filter(streamClass -> !streamClass.equals(cls))
                .collect(Collectors.toSet());
        DefaultNodeSet<OWLClass> res = new OWLClassNodeSet();
        res.addDifferentEntities(disjointClasses);
        return res;
    }
}
