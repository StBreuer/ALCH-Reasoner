import Loading.Normalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.Set;

public class NormalizationTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("remove not forall r to C")
    void testRemoveNotForall() throws OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass C = dataFactory.getOWLClass(iri+"#C");
        OWLClass D = dataFactory.getOWLClass(iri+"#D");
        OWLClass H = dataFactory.getOWLClass(iri+"#H");
        OWLObjectProperty r = dataFactory.getOWLObjectProperty(iri+"#r");
        OWLObjectAllValuesFrom forallRtoC = dataFactory.getOWLObjectAllValuesFrom(r,C);
        OWLObjectUnionOf DorForallRtoC = dataFactory.getOWLObjectUnionOf(D,forallRtoC);
        OWLObjectComplementOf notDorForallRtoC = dataFactory.getOWLObjectComplementOf(DorForallRtoC);
        OWLSubClassOfAxiom HSubNotDorForallRtoC = dataFactory.getOWLSubClassOfAxiom(H,notDorForallRtoC);

        ontology.addAxiom(HSubNotDorForallRtoC);

        Normalizer normalizer = new Normalizer();
        OWLOntology normalizedOntology = normalizer.normalize(ontology);
        Set<OWLAxiom> normalizedAxioms = normalizedOntology.getAxioms();
        System.out.println("finished");

    }

    @Test
    @DisplayName("test st() operator")
    void testSt() throws OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass C = dataFactory.getOWLClass(iri+"#C");
        OWLClass D = dataFactory.getOWLClass(iri+"#D");
        OWLObjectProperty r = dataFactory.getOWLObjectProperty(iri+"#r");

        //C and not D < exist r to D
        OWLObjectComplementOf notD = dataFactory.getOWLObjectComplementOf(D);
        OWLObjectIntersectionOf CAndNotD = dataFactory.getOWLObjectIntersectionOf(C, notD);

        OWLObjectSomeValuesFrom existRtoD = dataFactory.getOWLObjectSomeValuesFrom(r, D);

        OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(CAndNotD,existRtoD);
        ontology.addAxiom(axiom);

        Normalizer normalizer = new Normalizer();
        OWLOntology normalizedOntology = normalizer.normalize(ontology);
        Set<OWLAxiom> normalizedAxioms = normalizedOntology.getAxioms();
        System.out.println("finished");

    }

    @Test
    @DisplayName("check conjunction and disjunction normalization rules")
    void testConDisNormRule() throws OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass C = dataFactory.getOWLClass(iri + "#C");
        OWLClass D = dataFactory.getOWLClass(iri + "#D");

        OWLObjectUnionOf CorD = dataFactory.getOWLObjectUnionOf(C,D);
        OWLObjectIntersectionOf CandD = dataFactory.getOWLObjectIntersectionOf(C,D);
        OWLSubClassOfAxiom newAxiom = dataFactory.getOWLSubClassOfAxiom(CorD,CandD);
        ontology.addAxiom(newAxiom);

        Normalizer normalizer = new Normalizer();
        OWLOntology normalizedOntology = normalizer.normalize(ontology);
        Set<OWLAxiom> normalizedAxioms = normalizedOntology.getAxioms();
        System.out.println("finished");
    }

}
