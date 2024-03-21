import filemanager.Reader;
import org.semanticweb.owlapi.model.*;
import reasoner.Reasoner;

import java.util.Set;

public class Main {

    private static final String FILEPATH = "src/main/resources/goslim_agr.owl";
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntology ontology = new Reader().read(FILEPATH);
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        IRI IOR = IRI.create("http://purl.obolibrary.org/obo/");

        System.out.println(ontology.getAxioms());
        Reasoner reasoner = new Reasoner(ontology);



       OWLClass disjoint = factory.getOWLClass(IOR + "GO_0007610");

        Set<OWLDisjointClassesAxiom> eqSet = ontology.getDisjointClassesAxioms(disjoint);
        for (OWLAxiom ax : eqSet){
            System.out.println("____________________");
            System.out.println(ax.getNNF());
        }


    }
}
