package filemanager;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public interface FileReader {

    OWLOntology read (String path2file) throws OWLOntologyCreationException;
}
