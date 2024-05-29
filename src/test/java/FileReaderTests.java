import filemanager.FileReader;
import filemanager.Reader;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLOntology;

import static org.junit.jupiter.api.Assertions.*;

class FileReaderTests {

    @Test
    void readFileTest(){
        FileReader fileReader = new Reader();
        try {
            OWLOntology ontology = fileReader.read("src/test/resources/PaNET.owl");
            System.out.println(ontology);
            assertNotNull(ontology);
        }catch(Exception e){
            fail();
        }
    }
}
