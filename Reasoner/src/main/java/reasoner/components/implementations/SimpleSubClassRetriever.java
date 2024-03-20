package reasoner.components.implementations;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import reasoner.components.SubClassRetriever;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleSubClassRetriever implements SubClassRetriever {

    private final OWLOntology ontology;

    public SimpleSubClassRetriever(OWLOntology ontology) {
        this.ontology = ontology;
    }

    @Override
    public NodeSet<OWLClass> getSubClasses(OWLClassExpression owlClassExpression, boolean onlyDirectSubClasses) {

        OWLClass rootClass = owlClassExpression.asOWLClass();
        Set<OWLClass> nextLevelSubClasses = new HashSet<>();
        Set<OWLSubClassOfAxiom> axioms = new HashSet<>();
        DefaultNodeSet<OWLClass> result = new OWLClassNodeSet();
        nextLevelSubClasses.add(rootClass);

        do{
            axioms.clear();
            getSubClassAxioms(nextLevelSubClasses, axioms);
            nextLevelSubClasses.clear();
            getNextLevelSubClasses(axioms, nextLevelSubClasses, result);
        }while(!onlyDirectSubClasses && !nextLevelSubClasses.isEmpty());

        return result;
    }

    private void getSubClassAxioms(Set<OWLClass> nextLevelSubClasses, Set<OWLSubClassOfAxiom> axioms) {
        for(OWLClass cls : nextLevelSubClasses){
            axioms.addAll(ontology.getSubClassAxiomsForSuperClass(cls));
        }
    }

    private static void getNextLevelSubClasses(Set<OWLSubClassOfAxiom> axioms, Set<OWLClass> nextLevelSubClasses, DefaultNodeSet<OWLClass> result) {
        for(OWLSubClassOfAxiom axiom : axioms){
            nextLevelSubClasses.addAll(axiom
                    .getClassesInSignature()
                    .stream()
                    .filter(c -> !result.containsEntity(c))
                    .collect(Collectors.toSet()));
            result.addDifferentEntities(nextLevelSubClasses);
        }
    }
}
