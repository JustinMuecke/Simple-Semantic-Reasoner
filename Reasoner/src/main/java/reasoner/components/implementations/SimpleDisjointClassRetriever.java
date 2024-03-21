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

    @Override
    public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression owlClassExpression) {
        OWLClass cls = owlClassExpression.asOWLClass();
        Set<OWLDisjointClassesAxiom> axiomSet = ontology.getDisjointClassesAxioms(cls);
        Set<OWLClass> disjointClasses = axiomSet.stream()
                .filter(ax -> ax.getClassesInSignature().contains(cls))
                .map(HasClassesInSignature::getClassesInSignature)
                .flatMap(Collection::stream)
                .filter(scls -> !scls.equals(cls)).collect(Collectors.toSet());
        DefaultNodeSet<OWLClass> res = new OWLClassNodeSet();
        res.addDifferentEntities(disjointClasses);
        return res;
    }
}
