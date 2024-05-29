import filemanager.Reader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.DefaultNodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLNamedIndividualNodeSet;
import reasoner.Reasoner;

import java.util.HashSet;
import java.util.Set;

class GetTypeTest {
    private static final String FILEPATH = "src/test/resources/family_base.owl";
    private static Reasoner reasoner;
    private static OWLDataFactory factory;
    private static IRI IOR;
    @BeforeAll
    static void setup() throws OWLOntologyCreationException{
        OWLOntology ontology = null;
        ontology = new Reader().read(FILEPATH);
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        factory = manager.getOWLDataFactory();
        IOR = IRI.create("http://www.semanticweb.org/doo5i/ontologies/2024/2/Family");
        reasoner = new Reasoner(ontology, factory);
    }
    @Test
    void getTypeTest(){
        OWLNamedIndividual hanna = factory.getOWLNamedIndividual(IOR + "#Hanna");
        Set<OWLClass> types = reasoner.getTypes(hanna).getFlattened();
        Set<OWLClass> comparison = new HashSet<>();
        comparison.add(factory.getOWLClass(IOR + "#Parent"));
        comparison.add(factory.getOWLClass(IOR + "#Female"));
        Assertions.assertEquals(comparison, types);
    }
    @Test
    void childlessPersons(){
        DefaultNodeSet<OWLNamedIndividual> expected = new OWLNamedIndividualNodeSet();
        OWLObjectComplementOf childless = factory.getOWLObjectComplementOf(
                factory.getOWLObjectSomeValuesFrom(factory.getOWLObjectProperty(IOR + "#hasChild"), factory.getOWLClass(IOR + "#Child"))
        );
        expected.addEntity(factory.getOWLNamedIndividual(IOR + "#Annika"));
        expected.addEntity(factory.getOWLNamedIndividual(IOR + "#Marc"));
        expected.addEntity(factory.getOWLNamedIndividual(IOR + "#Rolt"));
        expected.addEntity(factory.getOWLNamedIndividual(IOR + "#Pascal"));
        expected.addEntity(factory.getOWLNamedIndividual(IOR + "#Olga"));
        NodeSet<OWLNamedIndividual> childlessPersons = reasoner.getInstances(childless);
        Assertions.assertEquals(expected, childlessPersons);
    }
}
