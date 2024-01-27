import filemanager.Reader;
import network.QueryServer;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import reasoner.Reasoner;

import java.io.IOException;

public class Main {

    private static final String FILEPATH = "src/main/resources/PaNET.owl";
    private static final int PORT = 50051;
    public static void main(String[] args){
        try {
            OWLOntology ontology = new Reader().read(FILEPATH);
        } catch (OWLOntologyCreationException e){
            e.printStackTrace();
        }

        Reasoner reasoner = new Reasoner();
        QueryServer server = new QueryServer(PORT, reasoner);

        try{
            server.start();
        } catch (IOException e){
            e.printStackTrace();
        }
        //TODO SPARQL Parser
        while(true){

        }
        //TODO Verarbeiten
    }
}
