package filemanager;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;

public class Reader implements FileReader{

    private final OWLOntologyManager manager;
    public Reader(){
        this.manager = OWLManager.createOWLOntologyManager();
    }
    @Override
    public OWLOntology read(String path2file) throws OWLOntologyCreationException {
        File ontologyFile = new File(path2file);
        return manager.loadOntologyFromOntologyDocument(ontologyFile);
    }
}
