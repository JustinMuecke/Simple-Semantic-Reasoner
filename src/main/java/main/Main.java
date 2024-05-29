package main;

import filemanager.Reader;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reasoner.Reasoner;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger("main.Main");

    private static final IRI IOR = IRI.create("http://www.semanticweb.org/doo5i/ontologies/2024/2/Family");
    private static final String FILEPATH = "src/main/resources/family_base.owl";

    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntology ontology = new Reader().read(FILEPATH);
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        Reasoner reasoner = new Reasoner(ontology, factory);
        logger.info("Reasoner {} has been created", reasoner.getReasonerName());
    }
}