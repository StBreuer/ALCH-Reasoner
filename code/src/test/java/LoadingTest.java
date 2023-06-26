import Loading.NegativeUniversalsRemover;
import Loading.OntologyLoader;
import RuleHandling.Proofs.ProofHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoadingTest {
    OntologyLoader loader;
    @BeforeEach
    void setUP(){
        loader = new OntologyLoader();

    }

    @Test
    @DisplayName("Simple Ontology Loading")
    void loadOntology() throws OWLOntologyCreationException {
        loader.loadOwlFile("/data/uni/ws2021-22/Theo_project/data/pizza-ontology/pizza.owl");
        assertEquals(false, loader.getOntologies().isEmpty());

    }

    @Test
    @DisplayName("applyNormalization to Conjunction Axiom")
    void normalizeConjAxiom(){

    }

    @Test
    @DisplayName("Remove Negative UniversalRestrictions")
    void removeNegativeUniversalRestrictions() throws OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass A = dataFactory.getOWLClass(iri+"#A");
        OWLClass B = dataFactory.getOWLClass(iri+"#B");
        OWLClass C = dataFactory.getOWLClass(iri+"#C");
        OWLObjectProperty r = dataFactory.getOWLObjectProperty(iri+"#r");
        OWLObjectAllValuesFrom forallR_B = dataFactory.getOWLObjectAllValuesFrom(r, B);
        OWLObjectComplementOf notForallR_B = dataFactory.getOWLObjectComplementOf(forallR_B);
        OWLObjectIntersectionOf A_and_notForallR_B = dataFactory.getOWLObjectIntersectionOf(A, notForallR_B);
        OWLSubClassOfAxiom A_and_notForallR_B_subclass_of_C = dataFactory.getOWLSubClassOfAxiom(A_and_notForallR_B, C);
        ontology.addAxiom(A_and_notForallR_B_subclass_of_C);

        NegativeUniversalsRemover remover = new NegativeUniversalsRemover();
        //remover.remove(ontology);
    }

    @Test
    @DisplayName("random stuff")
    void randomStuff() throws OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass A = dataFactory.getOWLClass(iri+"#A");
        OWLClassExpression notA = dataFactory.getOWLObjectComplementOf(A);
        List<OWLClassExpression> list = new ArrayList<>();
        list.add(notA);
        flatten(list, dataFactory);
        OWLClassExpression flattendNotA = flatten(notA, dataFactory);
        System.out.println(list.toString());


    }
    private List<OWLClassExpression> flatten(List<OWLClassExpression> classExpressions, OWLDataFactory dataFactory){
        classExpressions.replaceAll(classExpression ->{
            String string = stringify(classExpression);
            return toClassExpression(string,dataFactory);
        });
        return classExpressions;
    }
    private OWLClassExpression flatten(OWLClassExpression classExpression, OWLDataFactory dataFactory){
        String string = stringify(classExpression);
        return toClassExpression(string, dataFactory);
    }
    private String stringify(OWLClassExpression owlClassExpression){
        return "[" + owlClassExpression.toString() + "]";
    }
    private OWLClassExpression toClassExpression(String string, OWLDataFactory dataFactory){
        return dataFactory.getOWLClass(string);
    }

    @Test
    @DisplayName("ProofHandlerTest")
    void proofHandlerTest(){
        ProofHandler handler = new ProofHandler();

        System.out.println(handler);
    }

}
