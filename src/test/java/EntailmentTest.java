import filemanager.Reader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.semanticweb.owlapi.model.*;
import reasoner.Reasoner;

class EntailmentTest {
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
    void subClassOfAxiom(){
        // Test SubClassInference
        OWLClass female = factory.getOWLClass(IOR + "#Female");
        OWLClass mother = factory.getOWLClass(IOR + "#Mother");
        OWLSubClassOfAxiom subClassOfAxiom = factory.getOWLSubClassOfAxiom(mother, female);
        Assertions.assertTrue(reasoner.isEntailed(subClassOfAxiom));
    }

    @Test
    void hannaIsMother(){
        OWLNamedIndividual hanna = factory.getOWLNamedIndividual(IOR + "#Hanna");
        OWLClass mother = factory.getOWLClass(IOR + "#Mother");
        Assertions.assertTrue((
                reasoner.isEntailed(
                    factory.getOWLClassAssertionAxiom(mother, hanna)
                )
        ));
    }
    @Test
    void justinIsParent(){
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
    void justinIsFather(){
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

    @Test
    void declarationAxiomTest(){
        Assertions.assertTrue(reasoner.isEntailed(
                factory.getOWLDeclarationAxiom(
                        factory.getOWLNamedIndividual(IOR + "#Frank")
                )
        ));
    }

    @Test
    void equivalentClassesAxiomTest(){
        Assertions.assertFalse(
                reasoner.isEntailed(
                        factory.getOWLEquivalentClassesAxiom(
                                factory.getOWLClass(IOR + "#Parent"),
                                factory.getOWLClass(IOR + "#Male")
                        )
                )
        );
    }

    @Test
    void disjointClassesAxiomTest(){
        Assertions.assertTrue(
                reasoner.isEntailed(
                        factory.getOWLDisjointClassesAxiom(
                                factory.getOWLClass(IOR+ "#Mother"),
                                factory.getOWLClass(IOR + "#Father")
                        )
                )
        );
    }
    @Test
    void propertyTest(){
        Assertions.assertTrue(
                reasoner.isEntailed(
                        factory.getOWLObjectPropertyAssertionAxiom(
                                factory.getOWLObjectProperty(IOR + "#hasAncestor"),
                                factory.getOWLNamedIndividual(IOR + "#Annika"),
                                factory.getOWLNamedIndividual(IOR + "#Rolt")
                                )
                        )
                );
    }
}
