package filemanager;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;

public class Reader implements FileReader{

    public Reader(){}
    @Override
    public OWLOntology read(String path2file) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File ontologyFile = new File(path2file);
        return manager.loadOntologyFromOntologyDocument(ontologyFile);
    }
}
