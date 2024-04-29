package reasoner.components.implementations;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNode;
import reasoner.components.EquivalentClassRetriever;

import java.util.Collection;
import java.util.Set;

public class SimpleEquivalentClassRetriever implements EquivalentClassRetriever {

    private final OWLOntology ontology;

    public SimpleEquivalentClassRetriever(OWLOntology ontology) {
        this.ontology = ontology;
    }

    @Override
    public Node<OWLClass> getEquivalentClasses(OWLClassExpression owlClassExpression) {
        OWLClass cls = owlClassExpression.asOWLClass();
        Set<OWLEquivalentClassesAxiom> axiomSet = ontology.getEquivalentClassesAxioms(cls);
        return new OWLClassNode(
                    axiomSet.stream()
                        .map(HasClassesInSignature::getClassesInSignature)
                        .filter(set -> set.contains(cls))
                        .flatMap(Collection::stream)
                    );
    }
}
