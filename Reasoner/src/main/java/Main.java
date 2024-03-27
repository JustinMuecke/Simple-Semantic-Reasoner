import filemanager.Reader;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import reasoner.Reasoner;

import java.util.Set;

public class Main {

    private static final String FILEPATH = "src/main/resources/family_base.owl";
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntology ontology = new Reader().read(FILEPATH);
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        IRI IOR = IRI.create("http://www.semanticweb.org/doo5i/ontologies/2024/2/Family");

        OWLClass owlClass = factory.getOWLClass(IOR + "#Parent");
        OWLNamedIndividual namedIndividual1 = factory.getOWLNamedIndividual(IOR + "#Rolt");
        OWLNamedIndividual namedIndividual2 = factory.getOWLNamedIndividual(IOR + "#Justin");
        OWLObjectProperty property = factory.getOWLObjectProperty(IOR + "#hasChild");
        Reasoner reasoner = new Reasoner(ontology);
        System.out.println(reasoner.isEntailed(factory.getOWLClassAssertionAxiom(owlClass, namedIndividual2)));

    }
}
