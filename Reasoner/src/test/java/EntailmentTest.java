import filemanager.Reader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.semanticweb.owlapi.model.*;
import reasoner.Reasoner;

import static org.junit.Assert.*;

public class EntailmentTest {
    private static final String FILEPATH = "src/test/resources/family_base.owl";
    private static Reasoner reasoner;
    private static OWLDataFactory factory;
    private static IRI IOR;
    private static OWLOntology ontology;

    @BeforeAll
    static void setup() throws OWLOntologyCreationException{
        ontology = new Reader().read(FILEPATH);
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        factory = manager.getOWLDataFactory();
        IOR = IRI.create("http://www.semanticweb.org/doo5i/ontologies/2024/2/Family");
        reasoner = new Reasoner(ontology, factory);
    }


    @Test
    public void subClassOfAxiom(){
        // Test SubClassInference
        OWLClass female = factory.getOWLClass(IOR + "#Female");
        OWLClass mother = factory.getOWLClass(IOR + "#Mother");
        OWLSubClassOfAxiom subClassOfAxiom = factory.getOWLSubClassOfAxiom(mother, female);
        Assertions.assertTrue(reasoner.isEntailed(subClassOfAxiom));
    }

    @Test
    public void classAssertionAxioms(){
        OWLClass pureGrandParent = factory.getOWLClass(IOR + "#PureGrandParent");
        OWLClass grandParent = factory.getOWLClass(IOR + "#GrandParent");


        OWLNamedIndividual pascal = factory.getOWLNamedIndividual(IOR + "#Pascal");
        OWLNamedIndividual rolt = factory.getOWLNamedIndividual(IOR + "#Rolt");


        System.out.println("Pascal is GrandParent:");
        System.out.println(reasoner.isEntailed(factory.getOWLClassAssertionAxiom(grandParent, pascal)));
        System.out.println("Rolt is GrandParent:");
        System.out.println(reasoner.isEntailed(factory.getOWLClassAssertionAxiom(grandParent, rolt)));
        System.out.println("Pascal is PureGrandParent:");
        System.out.println(reasoner.isEntailed(factory.getOWLClassAssertionAxiom(pureGrandParent, pascal)));
        System.out.println("Rolt is PureGrandParent:");
        System.out.println(reasoner.isEntailed(factory.getOWLClassAssertionAxiom(pureGrandParent, rolt)));
    }

    @Test
    public void hannaIsMother(){
        OWLNamedIndividual hanna = factory.getOWLNamedIndividual(IOR + "#Hanna");
        OWLClass mother = factory.getOWLClass(IOR + "#Mother");
        System.out.println(ontology.getAxioms());
        System.out.println(ontology.getClassAssertionAxioms(hanna));


        System.out.println((
                reasoner.isEntailed(
                    factory.getOWLClassAssertionAxiom(mother, hanna)
                )
        ));
    }
    @Test
    public void justinIsParent(){
        OWLNamedIndividual justin = factory.getOWLNamedIndividual(IOR + "#Justin");
        OWLClass parent = factory.getOWLClass(IOR + "#Parent");
        System.out.println(ontology.getClassAssertionAxioms(justin));

        Assertions.assertTrue(
                reasoner.isEntailed(
                        factory.getOWLClassAssertionAxiom(parent, justin)
                )
        );
    }
    @Test
    public void justinIsFather(){
        OWLNamedIndividual justin = factory.getOWLNamedIndividual(IOR + "#Justin");
        OWLClass father = factory.getOWLClass(IOR + "#Father");
        OWLClass parent = factory.getOWLClass(IOR + "#Parent");
        Assertions.assertFalse(reasoner.isEntailed(
                factory.getOWLClassAssertionAxiom(father, justin)
        ));
        // justin not yet as parent in ontology
        // add it via entailment
        Assertions.assertTrue(reasoner.isEntailed(
                factory.getOWLClassAssertionAxiom(parent, justin))
        );
        reasoner.applyPendingChanges();
        Assertions.assertTrue(reasoner.isEntailed(
                factory.getOWLClassAssertionAxiom(father, justin)
        ));

    }
}
