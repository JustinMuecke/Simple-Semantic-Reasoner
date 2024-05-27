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

    /**
     * For the given owlClassExpression, calls the {@link #getSubClassAxioms} method to get its subclasses. If
     * retrieving all descendents, adds them as new root nodes and iterates until no new nodes are added.
     * @param owlClassExpression The class expression whose strict (direct) subclasses are to be retrieved.
     * @param onlyDirectSubClasses Specifies if the direct subclasses should be retrieved ( {@code true}) or if
     *        the all subclasses (descendant) classes should be retrieved ({@code false}).
     * @return Nodeset of all subclasses
     */
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
    /**
     * For all classes in current iteration, find all axioms which declare subclasses for them.
     * @param nextLevelSubClasses classes in current iteration.
     * @param axioms the subclass axioms which contain one of the classes in the current iteration.
     */
    private void getSubClassAxioms(Set<OWLClass> nextLevelSubClasses, Set<OWLSubClassOfAxiom> axioms) {
        for(OWLClass cls : nextLevelSubClasses){
            axioms.addAll(ontology.getSubClassAxiomsForSuperClass(cls));
        }
    }
    /**
     * For each axiom which contains one class of the current iteration, gets the classes for the next iteration and adds the current iteration to the result set.
     * @param axioms axioms which contain the subclass axioms of the current iteration.
     * @param nextLevelSubClasses should be empty when method is called, afterward contains all classes of the next iteration.
     * @param result contains all ancestors found up to this point.
     */
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
