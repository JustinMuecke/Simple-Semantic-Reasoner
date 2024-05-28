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

    /**
     * For the given owlClassExpression, calls the {@link #getSuperClassAxioms} method to get its super classes. If
     * retrieving all ancestors, adds them as new root nodes and iterates until no new nodes are added.
     * @param owlClassExpression The class expression whose strict (direct) super classes are to be retrieved.
     * @param onlyDirectSuperClasses Specifies if the direct super classes should be retrieved ( {@code true}) or if
     *        the all super classes (ancestors) classes should be retrieved ({@code false}).
     * @return NodeSet of all superclasses
     */
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

    /**
     * For each axiom which contains one class of the current iteration, gets the classes for the next iteration and adds the current iteration to the result set.
     * @param axioms axioms which contain the super class axioms of the current iteration.
     * @param nextLevelSuperClasses should be empty when method is called, afterward contains all classes of the next iteration.
     * @param result contains all ancestors found up to this point.
     * @param rootClass the root class expression. Needed to prevent cycles in finding ancestors, as we can filter for classes being unequal to the root.
     */
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

    /**
     * For all classes in current iteration, find all axioms which declare super classes for them.
     * @param nextLevelSuperClasses classes in current iteration.
     * @param axioms the super class axioms which contain one of the classes in the current iteration.
     */
    private void getSuperClassAxioms(Set<OWLClass> nextLevelSuperClasses, Set<OWLSubClassOfAxiom> axioms) {
        for(OWLClass cls : nextLevelSuperClasses){
            axioms.addAll(ontology.getSubClassAxiomsForSubClass(cls));
        }
    }
}
