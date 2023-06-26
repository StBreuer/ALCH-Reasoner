import RuleHandling.Proofs.ProofHandler;
import RuleHandling.ReasoningRules.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RuleTest {

    @Test
    @DisplayName("A Plus Rule")
    void applyAPlusRule() throws OWLOntologyCreationException {
        // H is a conjunction
        // Our rule gonna be A and B and C subsumes D
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass A = dataFactory.getOWLClass(iri+"#A");
        OWLClass B = dataFactory.getOWLClass(iri+"#B");
        OWLClass C = dataFactory.getOWLClass(iri+"#C");
        OWLClass D = dataFactory.getOWLClass(iri+"#D");

        OWLObjectIntersectionOf ABC = dataFactory.getOWLObjectIntersectionOf(A,B,C);
        OWLSubClassOfAxiom goal = dataFactory.getOWLSubClassOfAxiom(ABC, D);

        ProofHandler handler = new ProofHandler();
        handler.setGoal(goal);

        APlusInferenceRule rule = new APlusInferenceRule(ontology, handler);

        rule.applyRule();
        assertEquals(3, handler.getActiveAxioms().size());
    }

    @Test
    @DisplayName("And N Rule")
    void applyAndNRule() throws OWLOntologyCreationException {
        // ontology : A1 and A2 and A3 sub B1 or B2 (M)
        // active  1 : C1 and C2 sub A1 or D1
        //         2   C1 and C2 sub D2 or A2
        //         3   C1 and C2 sub A3 or D3
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass A1 = dataFactory.getOWLClass(iri+"#A1");
        OWLClass A2 = dataFactory.getOWLClass(iri+"#A2");
        OWLClass A3 = dataFactory.getOWLClass(iri+"#A3");
        OWLClass B1 = dataFactory.getOWLClass(iri+"#B1");
        OWLClass B2 = dataFactory.getOWLClass(iri+"#B2");
        OWLClass C1 = dataFactory.getOWLClass(iri+"#C1");
        OWLClass C2 = dataFactory.getOWLClass(iri+"#C2");
        OWLClass D1 = dataFactory.getOWLClass(iri+"#D1");
        OWLClass D2 = dataFactory.getOWLClass(iri+"#D2");
        OWLClass D3 = dataFactory.getOWLClass(iri+"#D3");

        // ontology : A1 and A2 and A3 sub B1 or B2 (M)
        OWLObjectIntersectionOf A1andA2andA3 = dataFactory.getOWLObjectIntersectionOf(A1, A2, A3);
        OWLObjectUnionOf B1orB2 = dataFactory.getOWLObjectUnionOf(B1,B2);
        OWLSubClassOfAxiom ontologyAxiom = dataFactory.getOWLSubClassOfAxiom(A1andA2andA3, B1orB2);

        OWLObjectIntersectionOf C1andC2 = dataFactory.getOWLObjectIntersectionOf(C1,C2);

        // active  1 : C1 and C2 sub A1 or D1
        OWLObjectUnionOf A1orD1 = dataFactory.getOWLObjectUnionOf(A1, D1);
        OWLSubClassOfAxiom active1 = dataFactory.getOWLSubClassOfAxiom(C1andC2,A1orD1);

        //         2   C1 and C2 sub D2 or A2
        OWLObjectUnionOf D2orA2 = dataFactory.getOWLObjectUnionOf(D2, A2);
        OWLSubClassOfAxiom active2 = dataFactory.getOWLSubClassOfAxiom(C1andC2,D2orA2);


        //         3   C1 and C2 sub A3 or D3
        OWLObjectUnionOf A3orD3 = dataFactory.getOWLObjectUnionOf(A3, D3);
        OWLSubClassOfAxiom active3 = dataFactory.getOWLSubClassOfAxiom(C1andC2,A3orD3);

        ontology.addAxiom(ontologyAxiom);
        ProofHandler proofHandler = new ProofHandler();
        proofHandler.getActiveAxioms().add(active1);
        proofHandler.getActiveAxioms().add(active2);
        proofHandler.getActiveAxioms().add(active3);
        ConjunctionNInferenceRule rule = new ConjunctionNInferenceRule(ontology, proofHandler);
        rule.applyRule();
        Set<OWLSubClassOfAxiom> activeAxioms = proofHandler.getActiveAxioms();
        System.out.println("ready");
    }

    @Test
    @DisplayName("Exist Plus Rule")
     void testExistPlusRule() throws OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass H = dataFactory.getOWLClass(iri+"#H");
        OWLClass N = dataFactory.getOWLClass(iri+"#N");
        OWLClass A = dataFactory.getOWLClass(iri+"#A");
        OWLClass B = dataFactory.getOWLClass(iri+"#B");
        OWLObjectProperty r = dataFactory.getOWLObjectProperty(iri+"#r");

        //  ontology:   A sub exist r.B
        OWLObjectSomeValuesFrom existRtoB = dataFactory.getOWLObjectSomeValuesFrom(r,B);
        OWLSubClassOfAxiom ontologyAxiom = dataFactory.getOWLSubClassOfAxiom(A, existRtoB);
        //  active:     H sub N or A
        OWLObjectUnionOf NorA = dataFactory.getOWLObjectUnionOf(N,A);
        OWLSubClassOfAxiom activeAxiom = dataFactory.getOWLSubClassOfAxiom(H, NorA);

        ontology.addAxiom(ontologyAxiom);
        ProofHandler proofHandler = new ProofHandler();
        proofHandler.getActiveAxioms().add(activeAxiom);

        ExistPlusInferenceRule rule = new ExistPlusInferenceRule(ontology,proofHandler);
        rule.applyRule();

        Set<OWLSubClassOfAxiom> activeAxioms = proofHandler.getActiveAxioms();
        System.out.println("ready");
    }

    @Test
    @DisplayName("Exist Minus Rule")
    void testExistMinusRule() throws OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass H = dataFactory.getOWLClass(iri+"#H");
        OWLClass M = dataFactory.getOWLClass(iri+"#M");
        OWLClass K = dataFactory.getOWLClass(iri+"#K");
        OWLClass N = dataFactory.getOWLClass(iri+"#N");
        OWLClass A = dataFactory.getOWLClass(iri+"#A");
        OWLClass B = dataFactory.getOWLClass(iri+"#B");
        OWLObjectProperty r = dataFactory.getOWLObjectProperty(iri + "#r");
        OWLObjectProperty s = dataFactory.getOWLObjectProperty(iri + "#s");


        //  active:     H sub M or exist r.K
        OWLObjectSomeValuesFrom existRinK = dataFactory.getOWLObjectSomeValuesFrom(r,K);
        OWLObjectUnionOf MorExistRinK= dataFactory.getOWLObjectUnionOf(M, existRinK);
        OWLSubClassOfAxiom activeAxiom1 = dataFactory.getOWLSubClassOfAxiom(H, MorExistRinK);

        //  active:     K sub N or A
        OWLObjectUnionOf NorA = dataFactory.getOWLObjectUnionOf(N,A);
        OWLSubClassOfAxiom activeAxiom2 = dataFactory.getOWLSubClassOfAxiom(K,NorA);

        //  ontology:   exist s.A sub B
        OWLObjectSomeValuesFrom existSinA = dataFactory.getOWLObjectSomeValuesFrom(s,A);
        OWLSubClassOfAxiom ontologyAxiom1 = dataFactory.getOWLSubClassOfAxiom(existSinA, B);

        //  ontology:   r sub s
        OWLSubPropertyAxiom ontologyAxiom2 = dataFactory.getOWLSubObjectPropertyOfAxiom(r,s);

        ontology.addAxiom(ontologyAxiom1);
        ontology.addAxiom(ontologyAxiom2);

        ProofHandler proofHandler = new ProofHandler();
        proofHandler.getActiveAxioms().add(activeAxiom1);
        proofHandler.getActiveAxioms().add(activeAxiom2);

        ExistsMinusInferenceRule rule = new ExistsMinusInferenceRule(ontology, proofHandler);
        rule.applyRule();

        Set<OWLSubClassOfAxiom> activeAxioms = proofHandler.getActiveAxioms();
        System.out.println("finished");

    }

    @Test
    @DisplayName("Exist Bottom Rule")
    void testExistBottomsRule() throws OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass H = dataFactory.getOWLClass(iri+"#H");
        OWLClass M = dataFactory.getOWLClass(iri+"#M");
        OWLClass K = dataFactory.getOWLClass(iri+"#K");
        OWLClass bottom = dataFactory.getOWLNothing();
        OWLObjectProperty r = dataFactory.getOWLObjectProperty(iri + "#r");

        // H sub M or exist r in K
        OWLObjectSomeValuesFrom existRinK = dataFactory.getOWLObjectSomeValuesFrom(r,K);
        OWLObjectUnionOf MorExistsRinK = dataFactory.getOWLObjectUnionOf(M,existRinK);
        OWLSubClassOfAxiom premise1 = dataFactory.getOWLSubClassOfAxiom(H,MorExistsRinK);

        // K sub bottom
        OWLSubClassOfAxiom premise2 = dataFactory.getOWLSubClassOfAxiom(K,bottom);

        ProofHandler proofHandler = new ProofHandler();
        proofHandler.getActiveAxioms().add(premise1);
        proofHandler.getActiveAxioms().add(premise2);

        ExistsBottomInferenceRule rule = new ExistsBottomInferenceRule(ontology, proofHandler);
        rule.applyRule();

        Set<OWLSubClassOfAxiom> activeAxioms = proofHandler.getActiveAxioms();
        System.out.println("finished");


    }
    @Test
    @DisplayName("For All Rule")
    void testForAllRule() throws OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass H = dataFactory.getOWLClass(iri+"#H");
        OWLClass M = dataFactory.getOWLClass(iri+"#M");
        OWLClass K = dataFactory.getOWLClass(iri+"#K");
        OWLClass N = dataFactory.getOWLClass(iri+"#N");
        OWLClass A = dataFactory.getOWLClass(iri+"#A");
        OWLClass B = dataFactory.getOWLClass(iri+"#B");
        OWLObjectProperty r = dataFactory.getOWLObjectProperty(iri + "#r");
        OWLObjectProperty s = dataFactory.getOWLObjectProperty(iri + "#s");

        // H sub M or exist r in K
        OWLObjectSomeValuesFrom existRinK = dataFactory.getOWLObjectSomeValuesFrom(r,K);
        OWLObjectUnionOf MorexistRinK = dataFactory.getOWLObjectUnionOf(M,existRinK);
        OWLSubClassOfAxiom premise1 = dataFactory.getOWLSubClassOfAxiom(H,MorexistRinK);

        // H sub N or A
        OWLObjectUnionOf NorA = dataFactory.getOWLObjectUnionOf(N,A);
        OWLSubClassOfAxiom premise2 = dataFactory.getOWLSubClassOfAxiom(H,NorA);

        // A sub forall s.B
        OWLObjectAllValuesFrom forallStoB = dataFactory.getOWLObjectAllValuesFrom(s,B);
        OWLSubClassOfAxiom ontologyAxiom1 = dataFactory.getOWLSubClassOfAxiom(A,forallStoB);

        // r sub s
        OWLSubPropertyAxiom ontologyAxiom2 = dataFactory.getOWLSubObjectPropertyOfAxiom(r,s);

        ontology.addAxiom(ontologyAxiom1);
        ontology.addAxiom(ontologyAxiom2);

        ProofHandler proofHandler = new ProofHandler();
        proofHandler.getActiveAxioms().add(premise1);
        proofHandler.getActiveAxioms().add(premise2);

        ForAllInferenceRule rule = new ForAllInferenceRule(ontology, proofHandler);
        rule.applyRule();

        Set<OWLSubClassOfAxiom> activeAxioms = proofHandler.getActiveAxioms();
        System.out.println("finished");

    }

    @Test
    @DisplayName("A minus Rule")
    void testAMinusRule() throws OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass H = dataFactory.getOWLClass(iri+"#H");
        OWLClass N = dataFactory.getOWLClass(iri+"#N");
        OWLClass A = dataFactory.getOWLClass(iri+"#A");

        OWLObjectUnionOf NorA = dataFactory.getOWLObjectUnionOf(N,A);

        OWLObjectComplementOf notA = dataFactory.getOWLObjectComplementOf(A);
        OWLObjectIntersectionOf HandNotA = dataFactory.getOWLObjectIntersectionOf(H,notA);
        OWLSubClassOfAxiom premise = dataFactory.getOWLSubClassOfAxiom(HandNotA,NorA);

        ProofHandler proofHandler = new ProofHandler();
        proofHandler.getActiveAxioms().add(premise);

        AMinusInferenceRule rule = new AMinusInferenceRule(ontology,proofHandler);
        rule.applyRule();

        Set<OWLSubClassOfAxiom> activeAxioms = proofHandler.getActiveAxioms();
        System.out.println("finished");
    }
}
