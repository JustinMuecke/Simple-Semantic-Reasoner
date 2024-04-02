import filemanager.Reader;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import reasoner.Reasoner;

import java.util.Set;

public class Main {

    private static final String FILEPATH = "src/main/resources/family_base.owl";
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntology ontology = new Reader().read(FILEPATH);
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        IRI IOR = IRI.create("http://www.semanticweb.org/doo5i/ontologies/2024/2/Family");
        Reasoner reasoner = new Reasoner(ontology, factory);



        // Individual Class Assertion Axioms

        OWLNamedIndividual rolt = factory.getOWLNamedIndividual(IOR + "#Rolt");
        OWLClass parent = factory.getOWLClass(IOR + "#Parent");


        OWLObjectProperty property = factory.getOWLObjectProperty(IOR + "#hasGrandChild");
        OWLClassExpression hasGrandChildExpression = factory.getOWLObjectSomeValuesFrom(property, factory.getOWLClass(IOR + "Child"));
        OWLClassExpression expression = factory.getOWLObjectAllValuesFrom(
                factory.getOWLObjectProperty(IOR+"#hasChild"),
                    factory.getOWLObjectSomeValuesFrom(
                        factory.getOWLObjectProperty(IOR + "#hasChild"),
                            factory.getOWLClass(IOR + "#Child"))
        );
        System.out.println(reasoner.isEntailed(factory.getOWLClassAssertionAxiom(expression, rolt)));
        System.out.println(reasoner.isEntailed(factory.getOWLEquivalentClassesAxiom(hasGrandChildExpression, expression)));
    }
}
/*
 OWLIndividual anon = factory.getOWLAnonymousIndividual();
 Set<OWLDeclarationAxiom> owlDeclarationAxioms = ontology.getAxioms(AxiomType.DECLARATION);
     for(OWLDeclarationAxiom declaration : owlDeclarationAxioms){
        System.out.println(declaration.getClassesInSignature());
     }
 */