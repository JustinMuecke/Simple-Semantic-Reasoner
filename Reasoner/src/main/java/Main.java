import filemanager.Reader;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import reasoner.Reasoner;

import java.util.Set;

public class Main {

    private static final String FILEPATH = "src/main/resources/PreMedOnto.owl";
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntology ontology = new Reader().read(FILEPATH);
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        //IRI IOR = IRI.create("http://purl.org/pan-science/PaNET/");

        System.out.println(ontology.getAxioms());
        Reasoner reasoner = new Reasoner(ontology);


    }
}
