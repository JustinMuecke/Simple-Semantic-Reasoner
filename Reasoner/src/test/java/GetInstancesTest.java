import filemanager.Reader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import reasoner.Reasoner;
import static org.junit.jupiter.api.Assertions.*;


public class GetInstancesTest {

    private static final String FILEPATH = "src/main/resources/PaNET.owl";
    private static Reasoner reasoner;
    private static OWLDataFactory factory;
    private static IRI IOR;

    @BeforeAll
    static void setup(){
        OWLOntology ontology = null;
        try {
            ontology = new Reader().read(FILEPATH);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        factory = manager.getOWLDataFactory();
        IOR = IRI.create("http://purl.org/pan-science/PaNET/");

        System.out.println("Axioms: "+ontology.getAxiomCount()+", Format: "+ manager.getOntologyFormat(ontology));
        reasoner = new Reasoner(ontology, factory);
    }


    @Test
    public void singleClass(){
        OWLClass cl1 = factory.getOWLClass(IOR + "PaNET00001");
        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(cl1);
        assertEquals(4,individuals.getNodes().size());
    }
    @Test
    public void intersectingClasses(){
        OWLClass cl1 = factory.getOWLClass(IOR + "PaNET00001");
        OWLClass cl2 = factory.getOWLClass(IOR + "PaNET00002");
        OWLClassExpression intersectionOfClasses = factory.getOWLObjectIntersectionOf(cl1, cl2);
        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(intersectionOfClasses);
        System.out.println(individuals);
        assertEquals(3,individuals.getNodes().size());
    }
    @Test
    public void unionClasses(){
        OWLClass cl1 = factory.getOWLClass(IOR + "PaNET01029");
        OWLClass cl2 = factory.getOWLClass(IOR + "PaNET01322");
        OWLClassExpression unionOfClasses = factory.getOWLObjectUnionOf(cl1, cl2);
        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(unionOfClasses);

        assertEquals(2,individuals.getNodes().size());
    }

    @Test
    public void childlessPersons(){
        DefaultNodeSet<OWLNamedIndividual> expected = new OWLNamedIndividualNodeSet();
        OWLObjectComplementOf childless = factory.getOWLObjectComplementOf(
                factory.getOWLObjectSomeValuesFrom(factory.getOWLObjectProperty(IOR + "#hasChild"), factory.getOWLClass(IOR + "#Child"))
        );
        NodeSet<OWLNamedIndividual> childlessPersons = reasoner.getInstances(childless);

    }

}
