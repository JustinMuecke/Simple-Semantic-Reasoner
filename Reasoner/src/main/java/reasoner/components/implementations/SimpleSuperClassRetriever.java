package reasoner.components.implementations;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;
import reasoner.components.SuperClassRetriever;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleSuperClassRetriever implements SuperClassRetriever {


    private final OWLOntology ontology;

    public SimpleSuperClassRetriever(OWLOntology ontology) {
        this.ontology = ontology;
    }

    @Override
    public NodeSet<OWLClass> getSuperClasses(OWLClassExpression owlClassExpression, boolean onlyDirectSuperClasses) {
        OWLClass rootClass = owlClassExpression.asOWLClass();
        Set<OWLClass> nextLevelSuperClasses = new HashSet<>();
        Set<OWLSubClassOfAxiom> axioms = new HashSet<>();
        DefaultNodeSet<OWLClass> result = new OWLClassNodeSet();
        nextLevelSuperClasses.add(rootClass);

        do{
            axioms.clear();
            getSuperClassAxioms(nextLevelSuperClasses, axioms);
            nextLevelSuperClasses.clear();
            getNextLevelSuperClasses(axioms, nextLevelSuperClasses, result, rootClass);
        } while (!onlyDirectSuperClasses && !nextLevelSuperClasses.isEmpty());
        return result;
    }

    private static void getNextLevelSuperClasses(Set<OWLSubClassOfAxiom> axioms, Set<OWLClass> nextLevelSuperClasses, DefaultNodeSet<OWLClass> result, OWLClass rootClass) {
        for(OWLSubClassOfAxiom axiom : axioms){
            nextLevelSuperClasses.addAll(axiom
                    .getClassesInSignature()
                    .stream()
                    .filter(c -> !result.containsEntity(c) && !c.equals(rootClass))
                    .collect(Collectors.toSet()));
            result.addDifferentEntities(nextLevelSuperClasses);
        }
    }

    private void getSuperClassAxioms(Set<OWLClass> nextLevelSuperClasses, Set<OWLSubClassOfAxiom> axioms) {
        for(OWLClass cls : nextLevelSuperClasses){
            axioms.addAll(ontology.getSubClassAxiomsForSubClass(cls));
        }
    }
}
