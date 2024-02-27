import filemanager.Reader;
import network.QueryServer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import reasoner.Reasoner;

import java.io.IOException;

public class Main {

    private static final String FILEPATH = "src/main/resources/PaNET.owl";
    private static final int PORT = 50051;
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntology ontology = new Reader().read(FILEPATH);
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        IRI IOR = IRI.create("http://purl.org/pan-science/PaNET/");

        System.out.println("Axioms: "+ontology.getAxiomCount()+", Format: "+ manager.getOntologyFormat(ontology));
        Reasoner reasoner = new Reasoner(ontology);

        OWLClass cl1 = factory.getOWLClass(IOR + "PaNET00001");
        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(cl1);

        OWLNamedIndividual ni = factory.getOWLNamedIndividual("PaNET:mySingleCrystalDiffractionTechnique");
        NodeSet<OWLClass> cl = reasoner.getTypes(ni);
        System.out.println(cl);
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
