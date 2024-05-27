package reasoner;
import javax.annotation.ParametersAreNonnullByDefault;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

import reasoner.components.EntailmentChecker;
import reasoner.components.implementations.*;

import java.util.*;

public class Reasoner implements ALCReasoner {


    private final OWLOntology ontology;
    private final OWLDataFactory df;
    private final List<OWLOntologyChange> ontologyChanges;

    public Reasoner(OWLOntology ontology, OWLDataFactory df){
        this.ontology = ontology;
        this.df = df;
        this.ontologyChanges = new LinkedList<>();
    }

    /**
     * @return The ontology used by the reasoner
     */
    @Override
    public OWLOntology getRootOntology() {
        return ontology;
    }

    /**
     * The name is hardcoded into this function
     * @return the name of the reasoner
     */
    @Override
    public String getReasonerName() {
        return "Simple Semantic Reasoner";
    }


    // __________________ INDIVIDUALS ______________________________

    /**
     * Return all NamedIndividuals which are instances of the class expression.
     * @param owlClassExpression The class expression whose instances are to be retrieved.
     * @param b Specifies if the direct instances should be retrieved ( {@code true}), or if
     *        all instances should be retrieved ( {@code false}).
     * @return NodeSet of all the named individuals
     */
    @Override
    @ParametersAreNonnullByDefault
    public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression owlClassExpression, boolean b) {
        return new SimpleInstanceRetriever(ontology, df).getInstances(owlClassExpression, b);
    }

    /**
     * For a given named individual, returns all classes which it is an instance of.
     * @param owlNamedIndividual The individual whose types are to be retrieved.
     * @param b Specifies if the direct types should be retrieved ( {@code true}), or if all
     *        types should be retrieved ( {@code false}).
     * @return NodeSet of all OWL Classes which the named individual is part of.
     */
    @Override
    @ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getTypes(OWLNamedIndividual owlNamedIndividual, boolean b) {
        return new SimpleTypeRetriever(ontology).getTypes(owlNamedIndividual, b);

    }

    /**
     * For a given named individual, it finds all equivalent individuals and returns them.
     * @param owlNamedIndividual The individual whose same individuals are to be retrieved.
     * @return Node containing all equivalent named individuals.
     */
    @Override    @ParametersAreNonnullByDefault
    public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual owlNamedIndividual) {
        return new SimpleSameIndividualRetriever(ontology).getSameIndividuals(owlNamedIndividual);
    }

    /**
     * Finds all individuals which are not equivalent to the given named individual
     * @param owlNamedIndividual The individual whose different individuals are to be returned.
     * @return NodeSet of all named individuals
     */
    @Override@ParametersAreNonnullByDefault
    public NodeSet<OWLNamedIndividual> getDifferentIndividuals(OWLNamedIndividual owlNamedIndividual) {
        return null;
    }

    // ____________________ CLASS AXIOMS ___________________________________________________________________________

    /**
     * For a given class expression, finds all its subclasses which have been declared in the ontology.
     * @param owlClassExpression The class expression whose strict (direct) subclasses are to be retrieved.
     * @param b Specifies if the direct subclasses should be retrieved ( {@code true}) or if
     *        the all subclasses (descendant) classes should be retrieved ({@code false}).
     * @return NodeSet of all the OWLClasses which are a subclass.
     */
    @Override
    @ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getSubClasses(OWLClassExpression owlClassExpression, boolean b) {
        return new SimpleSubClassRetriever(ontology).getSubClasses(owlClassExpression,b);
    }

    /**
     * For a given class expression, finds all its superclasses which have been declared in the ontology.
     * @param owlClassExpression The class expression whose strict (direct) super classes are to be retrieved.
     * @param b Specifies if the direct super classes should be retrieved ( {@code true}) or if
     *        the all super classes (ancestors) classes should be retrieved ({@code false}).
     * @return NodeSet of all the OWLClasses which are a superclass.
     */
    @Override
    @ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getSuperClasses(OWLClassExpression owlClassExpression, boolean b) {
        return new SimpleSuperClassRetriever(ontology).getSuperClasses(owlClassExpression, b);
    }

    /**
     * For a given class expression, finds all the equivalent OWLClasses.
     * @param owlClassExpression The class expression whose equivalent classes are to be retrieved.
     * @return Node containing all equivalent classes.
     */
    @Override    @ParametersAreNonnullByDefault
    public Node<OWLClass> getEquivalentClasses(OWLClassExpression owlClassExpression) {
        return new SimpleEquivalentClassRetriever(ontology).getEquivalentClasses(owlClassExpression);
    }

    /**
     * For a given expression, finds all the classes which have no instance in common with this expression.
     * @param owlClassExpression The class expression whose disjoint classes are to be retrieved.
     * @return NodeSet of all disjoint classes
     */
    @Override    @ParametersAreNonnullByDefault
    public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression owlClassExpression) {
        return new SimpleDisjointClassRetriever(ontology).getDisjointClasses(owlClassExpression);
    }
    //______________________________ CONSISTENCY _______________________________________________________________________

    /**
     * Checks whether the underlying ontology is consistent.
     * @return boolean representing consistency
     */
    public boolean isConsistent() {
            return false;
    }
    //______________________________ ENTAILMENT ________________________________________________________________________

    /**
     * Returns for any AxiomType whether it is supported to check such axioms for entailment.
     * @param axiomType The axiom type
     * @return boolean representing entailment checking possibility.
     */
    @Override
    public boolean isEntailmentCheckingSupported(AxiomType<?> axiomType) {
        return switch (axiomType.toString()) {
            case "OWLClassAssertion",
                    "OWLSubClassOfAxiom",
                    "OWLClassAssertionAxiom",
                    "OWLDeclarationAxiom",
                    "OWLAnnotationPropertyRangeAxiom",
                    "OWLAnnotationPropertyDomainAxiom",
                    "OWLAnnotationAssertionAxiom",
                    "OWLSubAnnotationPropertyOfAxiom"
                    -> true;
            default -> false;
        };
    }

    /**
     * Checks whether a given axiom is entailed by the ontology. If the axiom is entailed, adds it to the list of pending changes.
     * @param owlAxiom The axiom
     * @return boolean representing entailment.
     */
    @ParametersAreNonnullByDefault
    public boolean isEntailed(OWLAxiom owlAxiom) {
        EntailmentChecker entailmentChecker = new SimpleEntailmentChecker(this, ontology);
        if(entailmentChecker.isEntailed(owlAxiom)){
            OWLOntologyChange addition = new AddAxiom(ontology, owlAxiom);
            ontologyChanges.add(addition);
            return true;
        }
        return false;
    }

    /**
     * Checks whether a set of given axioms is entailed by the ontology.
     * @param set The set of axioms to be tested
     * @return boolean representing whether all axioms are entailed.
     */
    @Override
    @ParametersAreNonnullByDefault
    public boolean isEntailed(Set<? extends OWLAxiom> set) {
        Optional<Set<OWLAxiom>> entailedAxioms = new SimpleEntailmentChecker(this,ontology).isEntailed(set);
        if(entailedAxioms.isPresent()){
            for(OWLAxiom owlAxiom : entailedAxioms.get()){
                OWLOntologyChange addition = new AddAxiom(ontology, owlAxiom);
                ontologyChanges.add(addition);
            }
            return set.size() == entailedAxioms.get().size();
        }
        return false;
    }
    /**
     * Checks whether a given class expression is satisfiable in regard to the underlying ontology.
     * @param owlClassExpression The class expression
     * @return boolean representing satisfiability.
     */
    @Override@ParametersAreNonnullByDefault
    public boolean isSatisfiable(OWLClassExpression owlClassExpression) {
        return false;
    }

    /**
     * Returns a list of all axioms which have been successfully entailed.
     * @return List of Ontology Changes
     */
    @Override
    public List<OWLOntologyChange> getPendingChanges() {
        return ontologyChanges;
    }

    /**
     * Applies all pending changes to the ontology.
     */
    public void applyPendingChanges(){
        try {
            for (OWLOntologyChange change : ontologyChanges) {
                if (change.isAddAxiom()) ontology.add(change.getAxiom());
                if (change.isRemoveAxiom()) ontology.remove(change.getAxiom());
            }
        } catch (Exception ignored){
        }
    }

}
