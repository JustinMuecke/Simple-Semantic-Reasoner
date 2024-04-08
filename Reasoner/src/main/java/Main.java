import filemanager.Reader;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reasoner.Reasoner;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger("Main");


    private static final String FILEPATH = "src/main/resources/family_base.owl";
    public static void main(String[] args) throws OWLOntologyCreationException {
        OWLOntology ontology = new Reader().read(FILEPATH);
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        IRI IOR = IRI.create("http://www.semanticweb.org/doo5i/ontologies/2024/2/Family");
        Reasoner reasoner = new Reasoner(ontology, factory);



        // Individual Class Assertion Axioms

        OWLNamedIndividual rolt = factory.getOWLNamedIndividual(IOR + "#Rolt");
        OWLObjectProperty hasAncestor = factory.getOWLObjectProperty(IOR + "#hasAncestor");
        OWLNamedIndividual annika = factory.getOWLNamedIndividual(IOR + "#Annika");
        System.out.println(reasoner.isEntailed(factory.getOWLObjectPropertyAssertionAxiom(hasAncestor, annika, rolt)));

        OWLObjectProperty property = factory.getOWLObjectProperty(IOR + "#hasGrandChild");
        OWLClassExpression hasGrandChildExpression = factory.getOWLObjectSomeValuesFrom(property, factory.getOWLClass(IOR + "Child"));
        OWLClassExpression expression = factory.getOWLObjectAllValuesFrom(
                factory.getOWLObjectProperty(IOR+"#hasChild"),
                    factory.getOWLObjectSomeValuesFrom(
                        factory.getOWLObjectProperty(IOR + "#hasChild"),
                            factory.getOWLClass(IOR + "#Child"))
        );

        logger.info(String.valueOf(reasoner.isEntailed(factory.getOWLClassAssertionAxiom(expression, rolt))));


        OWLClassExpression hasValueExpression = factory.getOWLObjectHasValue(factory.getOWLObjectProperty(IOR + "#hasChild"), annika);
        logger.info(reasoner.getInstances(hasValueExpression).toString());
        /*System.out.println(reasoner.isEntailed(factory.getOWLEquivalentClassesAxiom(hasGrandChildExpression, expression)));
        OWLObjectPropertyAssertionAxiom assertionAxiom = factory.getOWLObjectPropertyAssertionAxiom(
                factory.getOWLObjectProperty(property), rolt, factory.getOWLNamedIndividual(IOR + "#Annika")
        );
        OWLObjectComplementOf childless = factory.getOWLObjectComplementOf(
                    factory.getOWLObjectSomeValuesFrom(factory.getOWLObjectProperty(IOR + "#hasChild"), factory.getOWLClass(IOR + "#Child"))
        );
        System.out.println(reasoner.getInstances(childless));
        */


    }

}
/*
 OWLIndividual anon = factory.getOWLAnonymousIndividual();
 Set<OWLDeclarationAxiom> owlDeclarationAxioms = ontology.getAxioms(AxiomType.DECLARATION);
     for(OWLDeclarationAxiom declaration : owlDeclarationAxioms){
        System.out.println(declaration.getClassesInSignature());
     }
 */