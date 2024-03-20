import filemanager.Reader;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import reasoner.Reasoner;

import java.util.stream.Collectors;

public class Main {

    private static final String FILEPATH = "src/main/resources/PaNET.owl";
    private static final int PORT = 50051;
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntology ontology = new Reader().read(FILEPATH);
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        IRI IOR = IRI.create("http://purl.org/pan-science/PaNET/");


        Reasoner reasoner = new Reasoner(ontology);

        OWLClass cl1 = factory.getOWLClass(IOR + "PaNET00001");
        OWLClass cl2 = factory.getOWLClass(IOR + "PaNET00005");

        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(cl1);

        OWLNamedIndividual ni = factory.getOWLNamedIndividual("PaNET:mySingleCrystalDiffractionTechnique");
        OWLClassExpression intersectionOfClasses = factory.getOWLObjectIntersectionOf(cl1, cl2);
        OWLClassExpression ce = factory.getOWLClass(IOR + "PaNET01269");
        OWLClassAssertionAxiom axiom = factory.getOWLClassAssertionAxiom(intersectionOfClasses, ni);
        //System.out.println("Sub Classes: " + reasoner.getSubClasses(ce, false).entities().collect(Collectors.toSet()));
        reasoner.getSameIndividuals(ni);


        /*
        try{
            server.start();
        } catch (IOException e){
            e.printStackTrace();
        }


        //TODO SPARQL Parser
        while(true){

        }
        //TODO Verarbeiten

         */
    }
}
