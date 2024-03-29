import Loading.Normalizer;
import ProofPresentation.ProofRenderer;
import Reasoner.ALCH_Reasoner;
import com.opencsv.CSVWriter;
import de.tu_dresden.inf.lat.evee.eliminationProofs.LetheBasedALCHProofGenerator;
import de.tu_dresden.inf.lat.evee.proofs.data.exceptions.ParsingException;
import de.tu_dresden.inf.lat.evee.proofs.data.exceptions.ProofGenerationException;
import de.tu_dresden.inf.lat.evee.proofs.interfaces.IInference;
import de.tu_dresden.inf.lat.evee.proofs.interfaces.IProof;
import de.tu_dresden.inf.lat.evee.proofs.json.JsonProofParser;
import de.tu_dresden.inf.lat.evee.proofs.json.JsonProofWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ProofTest {

    OWLDataFactory dataFactory;
    OWLOntology ontology;
    OWLOntologyManager ontologyManager;
    @BeforeEach
     void setUp() throws OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        ontologyManager = OWLManager.createOWLOntologyManager();
        ontology = ontologyManager.createOntology();
        dataFactory = ontologyManager.getOWLDataFactory();
    }

    @Test
    @DisplayName("longer proofs caused by normalisation example")
    void longProofTest1() throws ProofGenerationException, IOException {
        String inputPath = "src/main/resources/proves/exampleProof_longerProofs.json";
        String outputJsonPath = "src/main/resources/proves/exampleProof_longerProofs_normalized";
        String outputPNGPath = "src/main/resources/proves/exampleProof_longerProofs_normalized.png";
        generateALCHProof(inputPath, outputJsonPath, outputPNGPath, ontology);
    }
    @Test
    @DisplayName("longer proofs caused by normalisation example")
    void longProofTest2() throws ProofGenerationException, IOException {
        String inputPath = "src/main/resources/proves/longerProofExample2.json";
        String outputJsonPath = "src/main/resources/proves/longerProofExample2_normalized";
        String outputPNGPath = "src/main/resources/proves/longerProofExample2_normalized.png";
        generateALCHProof(inputPath, outputJsonPath, outputPNGPath, ontology);
    }

    @Test
    @DisplayName("No proof should be found test")
    void noProofTest() throws OWLOntologyCreationException, ProofGenerationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass A = dataFactory.getOWLClass(iri+"#A");
        OWLClass B = dataFactory.getOWLClass(iri+"#B");
        OWLClass C = dataFactory.getOWLClass(iri+"#C");
        OWLClass D = dataFactory.getOWLClass(iri+"#D");
        OWLClass E = dataFactory.getOWLClass(iri+"#E");

        OWLObjectIntersectionOf BnC = dataFactory.getOWLObjectIntersectionOf(B,C);
        OWLSubClassOfAxiom BnC_c_A = dataFactory.getOWLSubClassOfAxiom(BnC,A);

        OWLObjectUnionOf BuC = dataFactory.getOWLObjectUnionOf(B,C);
        OWLSubClassOfAxiom D_c_BuC = dataFactory.getOWLSubClassOfAxiom(D,BuC);

        OWLSubClassOfAxiom E_c_D = dataFactory.getOWLSubClassOfAxiom(E,D);

        //GOAL
        OWLSubClassOfAxiom E_c_A = dataFactory.getOWLSubClassOfAxiom(E,A);

        ontology.addAxioms(BnC_c_A,D_c_BuC,E_c_D);
        ALCH_Reasoner reasoner = new ALCH_Reasoner();
        reasoner.setOntology(ontology);
        IProof<OWLAxiom> proof = reasoner.getTreeProve(E_c_A);
        System.out.println("finished");
    }

    @Test
    @DisplayName("why no proof debug")
    void finalDebug() throws ProofGenerationException, OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass A = dataFactory.getOWLClass(iri+"#A");
        OWLClass B = dataFactory.getOWLClass(iri+"#B");
        OWLClass C = dataFactory.getOWLClass(iri+"#C");
        OWLClass D = dataFactory.getOWLClass(iri+"#D");
        OWLClass E = dataFactory.getOWLClass(iri+"#E");
        OWLClass F = dataFactory.getOWLClass(iri+"#F");

        OWLObjectIntersectionOf CnD = dataFactory.getOWLObjectIntersectionOf(C,D);
        OWLObjectIntersectionOf CnE = dataFactory.getOWLObjectIntersectionOf(C,E);
        OWLObjectUnionOf CnD_u_CnE = dataFactory.getOWLObjectUnionOf(CnD,CnE);

        OWLSubClassOfAxiom AcB = dataFactory.getOWLSubClassOfAxiom(A,B);
        OWLSubClassOfAxiom BcCnD_u_CnE = dataFactory.getOWLSubClassOfAxiom(B,CnD_u_CnE);
        OWLSubClassOfAxiom CcF = dataFactory.getOWLSubClassOfAxiom(C,F);
        OWLSubClassOfAxiom DcF = dataFactory.getOWLSubClassOfAxiom(D,F);
        OWLSubClassOfAxiom EcF = dataFactory.getOWLSubClassOfAxiom(E,F);

        OWLSubClassOfAxiom query = dataFactory.getOWLSubClassOfAxiom(A,F);

        ontology.addAxioms(AcB,BcCnD_u_CnE,CcF,DcF,EcF);
        ALCH_Reasoner reasoner = new ALCH_Reasoner();
        reasoner.setOntology(ontology);
        IProof<OWLAxiom> proof = reasoner.getTreeProve(query);
        System.out.println("finished");



    }

    @Test
    @DisplayName("basic proof test")
    void basicProofTest() throws ProofGenerationException, OWLOntologyCreationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass A = dataFactory.getOWLClass(iri+"#A");
        OWLClass B = dataFactory.getOWLClass(iri+"#B");
        OWLClass C = dataFactory.getOWLClass(iri+"#C");
        OWLClass D = dataFactory.getOWLClass(iri+"#D");
        OWLClass E = dataFactory.getOWLClass(iri+"#E");

        OWLObjectIntersectionOf BnC = dataFactory.getOWLObjectIntersectionOf(B,C);
        OWLSubClassOfAxiom BnC_c_A = dataFactory.getOWLSubClassOfAxiom(BnC,A);

        OWLObjectUnionOf BuC = dataFactory.getOWLObjectUnionOf(B,C);
        OWLSubClassOfAxiom D_c_B = dataFactory.getOWLSubClassOfAxiom(D,B);
        OWLSubClassOfAxiom D_c_C = dataFactory.getOWLSubClassOfAxiom(D,C);

        OWLSubClassOfAxiom E_c_D = dataFactory.getOWLSubClassOfAxiom(E,D);

        OWLSubClassOfAxiom E_c_A = dataFactory.getOWLSubClassOfAxiom(E,A);

        ontology.addAxioms(BnC_c_A,E_c_D,D_c_B,D_c_C);
        ALCH_Reasoner reasoner = new ALCH_Reasoner();
        reasoner.setOntology(ontology);
        IProof<OWLAxiom> proof = reasoner.getTreeProve(E_c_A);
        System.out.println("finished");

    }

    @Test
    @DisplayName("report example")
    void testReportExample() throws OWLOntologyCreationException, ProofGenerationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass A = dataFactory.getOWLClass(iri+"#A");
        OWLClass B = dataFactory.getOWLClass(iri+"#B");
        OWLClass C = dataFactory.getOWLClass(iri+"#C");
        OWLClass D = dataFactory.getOWLClass(iri+"#D");
        OWLClass E = dataFactory.getOWLClass(iri+"#E");
        OWLObjectProperty r = dataFactory.getOWLObjectProperty(iri + "#r");
        OWLObjectProperty s = dataFactory.getOWLObjectProperty(iri + "#s");

        OWLObjectSomeValuesFrom rSomeD = dataFactory.getOWLObjectSomeValuesFrom(r,D);
        OWLSubClassOfAxiom B_c_rSomeD = dataFactory.getOWLSubClassOfAxiom(B,rSomeD);

        OWLObjectAllValuesFrom sAllE = dataFactory.getOWLObjectAllValuesFrom(s,E);
        OWLSubClassOfAxiom A_c_sAllE = dataFactory.getOWLSubClassOfAxiom(A,sAllE);

        OWLObjectSomeValuesFrom rSomeE = dataFactory.getOWLObjectSomeValuesFrom(r,E);
        OWLSubClassOfAxiom rSomeE_c_C = dataFactory.getOWLSubClassOfAxiom(rSomeE,C);

        OWLSubObjectPropertyOfAxiom r_c_s = dataFactory.getOWLSubObjectPropertyOfAxiom(r,s);
        OWLSubObjectPropertyOfAxiom r_c_r = dataFactory.getOWLSubObjectPropertyOfAxiom(r,r);
        OWLSubObjectPropertyOfAxiom s_c_s = dataFactory.getOWLSubObjectPropertyOfAxiom(r,r);


        OWLObjectIntersectionOf AnB = dataFactory.getOWLObjectIntersectionOf(A,B);
        OWLSubClassOfAxiom AnB_c_C = dataFactory.getOWLSubClassOfAxiom(AnB,C);

        ontology.addAxioms(B_c_rSomeD,A_c_sAllE,rSomeE_c_C,r_c_s, r_c_r, s_c_s);
        ALCH_Reasoner reasoner = new ALCH_Reasoner();
        reasoner.setOntology(ontology);
        IProof<OWLAxiom> proof = reasoner.getTreeProve(AnB_c_C);
        System.out.println("finished");
    }

    @Test
    @DisplayName("report example lethe")
    void testReportExampleLethe() throws OWLOntologyCreationException, ProofGenerationException {
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass A = dataFactory.getOWLClass(iri + "#A");
        OWLClass B = dataFactory.getOWLClass(iri + "#B");
        OWLClass C = dataFactory.getOWLClass(iri + "#C");
        OWLClass D = dataFactory.getOWLClass(iri + "#D");
        OWLClass E = dataFactory.getOWLClass(iri + "#E");
        OWLObjectProperty r = dataFactory.getOWLObjectProperty(iri + "#r");
        OWLObjectProperty s = dataFactory.getOWLObjectProperty(iri + "#s");

        OWLObjectSomeValuesFrom rSomeD = dataFactory.getOWLObjectSomeValuesFrom(r, D);
        OWLSubClassOfAxiom B_c_rSomeD = dataFactory.getOWLSubClassOfAxiom(B, rSomeD);

        OWLObjectAllValuesFrom sAllE = dataFactory.getOWLObjectAllValuesFrom(s, E);
        OWLSubClassOfAxiom A_c_sAllE = dataFactory.getOWLSubClassOfAxiom(A, sAllE);

          OWLObjectSomeValuesFrom rSomeE = dataFactory.getOWLObjectSomeValuesFrom(r, E);
        OWLSubClassOfAxiom rSomeE_c_C = dataFactory.getOWLSubClassOfAxiom(rSomeE, C);

        OWLSubObjectPropertyOfAxiom r_c_s = dataFactory.getOWLSubObjectPropertyOfAxiom(r, s);
        OWLSubObjectPropertyOfAxiom r_c_r = dataFactory.getOWLSubObjectPropertyOfAxiom(r, r);
        OWLSubObjectPropertyOfAxiom s_c_s = dataFactory.getOWLSubObjectPropertyOfAxiom(r, r);

        //GOAL
        OWLObjectIntersectionOf AnB = dataFactory.getOWLObjectIntersectionOf(A, B);
        OWLSubClassOfAxiom AnB_c_C = dataFactory.getOWLSubClassOfAxiom(AnB, C);


        ontology.addAxiom(r_c_s);
        ontology.addAxiom(r_c_r);
        ontology.addAxiom(s_c_s);
        ontology.addAxiom(B_c_rSomeD);
        ontology.addAxiom(A_c_sAllE);
        ontology.addAxiom(rSomeE_c_C);


        LetheBasedALCHProofGenerator proofGenerator = new LetheBasedALCHProofGenerator();
        proofGenerator.setOntology(ontology);
        IProof<OWLAxiom> proof = proofGenerator.getProof(AnB_c_C);
        System.out.println("finished");

    }


    @Test
    @DisplayName("test output extension proof representation")
    void testOutputExtensionProofRepresentation() throws OWLOntologyCreationException, ProofGenerationException, IOException, OWLOntologyStorageException {

        JsonProofParser parser = new JsonProofParser();
        //load task 00001
        int taskNumber = 1;
        String letheInput = "src/main/resources/proves/Lethe_task0000"+ taskNumber + ".json.json";
        String generatedALCH = "src/main/resources/proves/ALCH_task0000"+ taskNumber + ".json";
        //Output

        //ProofRenderer.drawProof(parser.fromFile(new File(letheInput)), "src/main/resources/proves/Lethe_task0000task0000"+ taskNumber +".png");
        ProofRenderer.drawProof(parser.fromFile(new File(generatedALCH)), "src/main/resources/proves/ALCH_task0000"+ taskNumber +".png");

    }

    @Test
    @DisplayName("test concept stringification in order to print proof")
    void stringificationTest() throws IOException, ProofGenerationException {
        JsonProofParser parser = new JsonProofParser();
        JsonProofWriter<OWLAxiom> writer = new JsonProofWriter<>();

        IProof<OWLAxiom> proof = parser.fromFile(new File("src/main/resources/proves/bioportal-alc-tasks/task00003.json"));
        OWLAxiom finalConclusion  = proof.getFinalConclusion();
        Collection<IInference<OWLAxiom>> finalInference = proof.getInferences(finalConclusion);
        finalInference.forEach(inference -> {
            ontology.addAxioms(inference.getPremises());
        });

        ALCH_Reasoner reasoner = new ALCH_Reasoner();
        reasoner.setOntology(ontology);
        IProof<OWLAxiom> alchProof = reasoner.getTreeProve(finalConclusion);

        System.out.println("finished");

        writer.writeToFile(alchProof, "src/main/resources/proves/ALCH_task00003");
    }

    @Test
    @DisplayName("test proof patterns")
    void ProofPatterns() throws ParsingException, ProofGenerationException, IOException, OWLOntologyStorageException {
        JsonProofParser parser = new JsonProofParser();
        JsonProofWriter<OWLAxiom> writer = new JsonProofWriter<>();

        IProof<OWLAxiom> proof = parser.fromFile(new File("src/main/resources/proves/bioportal-alc-tasks/task00001.json"));
        OWLAxiom finalConclusion  = proof.getFinalConclusion();
        Collection<IInference<OWLAxiom>> finalInference = proof.getInferences(finalConclusion);
        finalInference.forEach(inference -> {
            ontology.addAxioms(inference.getPremises());
        });
        LetheBasedALCHProofGenerator proofGenerator = new LetheBasedALCHProofGenerator();
        proofGenerator.setOntology(ontology);
        IProof<OWLAxiom> generatedProof = proofGenerator.getProof(finalConclusion);
        System.out.println("finished");

        writer.writeToFile(generatedProof, "src/main/resources/proves/Lethe_task00001");

        ALCH_Reasoner reasoner = new ALCH_Reasoner();
        reasoner.setOntology(ontology);
        IProof<OWLAxiom> alchProof = reasoner.getProof(finalConclusion);
        System.out.println("finished");

        writer.writeToFile(alchProof, "src/main/resources/proves/ALCH_task00001");

        FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/proves/ontology_task00001.owl");
        OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
        ontologyManager.saveOntology(ontology, owlxmlFormat, fileOutputStream);

    }
    @Test
    @DisplayName("test outputting alot of proof pics ")
    void testGenerateProofPics() throws ProofGenerationException, IOException, OWLOntologyStorageException, OWLOntologyCreationException {
        String basePath = "src/main/resources/proves/";
        String csvOutputPath = basePath + "FoundProofsQuerstionMark";

        CSVWriter csvWriter = new CSVWriter(new FileWriter(csvOutputPath));

        for (int i = 13; i <= 138;i++){
            setUp();
            if(i == 4 ){
                continue;
            }
            String taskNumber = pad(i);
            System.out.println("Start task" + taskNumber);
            generateProof(basePath, taskNumber, ontology, ontologyManager, csvWriter);
        }

    }
    static String pad (int number){
        String paddedNumber = String.format("%05d", number);
        return paddedNumber;
    }
    static void generateALCHProof(String taskPath, String outputJsonPath, String outputPNGPath, OWLOntology ontology) throws IOException, ProofGenerationException {
        JsonProofParser parser = new JsonProofParser();
        JsonProofWriter<OWLAxiom> writer = new JsonProofWriter<>();

        IProof<OWLAxiom> proof = parser.fromFile(new File(taskPath));
        OWLAxiom finalConclusion  = proof.getFinalConclusion();
        Collection<IInference<OWLAxiom>> finalInference = proof.getInferences(finalConclusion);
        finalInference.forEach(inference -> {
            ontology.addAxioms(inference.getPremises());
        });

        ALCH_Reasoner reasoner = new ALCH_Reasoner();
        reasoner.setOntology(ontology);
        IProof<OWLAxiom> alchProof = reasoner.getTreeProve(finalConclusion);
        System.out.println("finished");

        writer.writeToFile(alchProof, outputJsonPath);
        ProofRenderer.drawProof(alchProof, outputPNGPath);


    }
    static void generateProof(String basePath, String taskNumber, OWLOntology ontology, OWLOntologyManager ontologyManager, CSVWriter csvWriter ) throws IOException, OWLOntologyStorageException, ProofGenerationException {
        String taskPath = basePath + "bioportal-alc-tasks/task" + taskNumber + ".json";
        String letheOutputPath = basePath + "Lethe_task" + taskNumber;
        String alchOutputPath  = basePath + "ALCH_task" + taskNumber;
        String lethePicPath = basePath + "Lehte_task" + taskNumber + ".png";
        String alchPicPath = basePath + "ALCH_task" + taskNumber + ".png";

        String alchTimePath = basePath + "ALCH_time.txt";
        String letheTimePath = basePath + "Lethe_time.txt";


        JsonProofParser parser = new JsonProofParser();
        JsonProofWriter<OWLAxiom> writer = new JsonProofWriter<>();

        IProof<OWLAxiom> proof = parser.fromFile(new File(taskPath));
        OWLAxiom finalConclusion  = proof.getFinalConclusion();
        Collection<IInference<OWLAxiom>> finalInference = proof.getInferences(finalConclusion);
        finalInference.forEach(inference -> {
            ontology.addAxioms(inference.getPremises());
        });
        LetheBasedALCHProofGenerator proofGenerator = new LetheBasedALCHProofGenerator();
        proofGenerator.setOntology(ontology);
        //time
        long startTime = System.nanoTime();

        IProof<OWLAxiom> letheProof = proofGenerator.getProof(finalConclusion);

        int letheNrAxioms = letheProof.getNumberOfAxioms();
        int letheSizeLargestInferencePremise = letheProof.getSizeOfLargestInferencePremise();
        int letheNrRuleApplications = letheProof.getNumberOfRuleApplications();


        long endTime = System.nanoTime();
        writeTimeToFile(letheTimePath, taskNumber, startTime, endTime, letheNrAxioms, letheSizeLargestInferencePremise, letheNrRuleApplications, true);
        System.out.println("lehte finished");

        writer.writeToFile(letheProof, letheOutputPath);

        ALCH_Reasoner reasoner = new ALCH_Reasoner();
        reasoner.setOntology(ontology);

        //time
        startTime = System.nanoTime();

        IProof<OWLAxiom> alchProof = reasoner.getTreeProve(finalConclusion);

        int alchNrAxioms = 0;
        int alchSizeLargestInferencePremise = 0;
        int alchNrRuleApplications = 0;
        if (alchProof != null){
            alchNrAxioms = alchProof.getNumberOfAxioms();
            alchSizeLargestInferencePremise = alchProof.getSizeOfLargestInferencePremise();
            alchNrRuleApplications = alchProof.getNumberOfRuleApplications();
        }


        endTime = System.nanoTime();


        if (alchProof != null){
            writeTimeToFile(alchTimePath, taskNumber, startTime, endTime, alchNrAxioms, alchSizeLargestInferencePremise, alchNrRuleApplications, true);
            //String[] line  = {"task"+ taskNumber, "ALCH", "Found"};
            //csvWriter.writeNext(line);
        } else {
            //String[] line = {"task"+ taskNumber, "ALCH", "Not Found"};
            //csvWriter.writeNext(line);
            writeTimeToFile(alchTimePath, taskNumber, startTime, endTime, alchNrAxioms, alchSizeLargestInferencePremise, alchNrRuleApplications, false);

        }
        System.out.println("alch finished");

        writer.writeToFile(alchProof, alchOutputPath);

//TODO add again
        if (alchProof != null){
            ProofRenderer.drawProof(letheProof, lethePicPath);
            ProofRenderer.drawProof(alchProof, alchPicPath);
        }


    }

    private static void writeTimeToFile(String path, String task, long start, long end, int nrAxioms, int sizeLargestInferencePremise, int nrRuleApplications, boolean valid) throws IOException {
        FileWriter fileWriter = new FileWriter(path, true);
        long executionTime = end - start;
        executionTime = TimeUnit.NANOSECONDS.toMillis(executionTime);
        fileWriter.write("Task " + task + " Time:" + executionTime + "ms " + "nrAxioms: " +nrAxioms + " sizeLargestInferencePremise: " +sizeLargestInferencePremise + " nrRuleApplications: "+ nrRuleApplications + ": " + valid +  "\n");
        fileWriter.close();
    }

    @Test
    @DisplayName("test00001 bug")
    void test0001ALCH() throws OWLOntologyCreationException {
        //create Axiom E = (D u H u I) n F
        IRI iri = IRI.create("http://steffen.testOntology");
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = ontologyManager.createOntology();
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager().getOWLDataFactory();
        OWLClass E = dataFactory.getOWLClass(iri + "#E");
        OWLClass D = dataFactory.getOWLClass(iri + "#D");
        OWLClass H = dataFactory.getOWLClass(iri + "#H");
        OWLClass I = dataFactory.getOWLClass(iri + "#I");
        OWLClass F = dataFactory.getOWLClass(iri + "#F");

        OWLObjectUnionOf D_u_H_u_I = dataFactory.getOWLObjectUnionOf(D, H, I);
        OWLObjectIntersectionOf D_u_H_u_I_n_F = dataFactory.getOWLObjectIntersectionOf(D_u_H_u_I, F);
        OWLEquivalentClassesAxiom E_eq_D_u_H_u_I_n_F = dataFactory.getOWLEquivalentClassesAxiom(E, D_u_H_u_I_n_F);
        ontology.addAxiom(E_eq_D_u_H_u_I_n_F);
        Normalizer normalizer = new Normalizer();
        Set<OWLAxiom> axioms = normalizer.normalize(ontology).getAxioms();
        System.out.println("finished");

        /*
        JsonProofParser parser = new JsonProofParser();
        JsonProofWriter<OWLAxiom> writer = new JsonProofWriter<>();

        IProof<OWLAxiom> proof = parser.fromFile(new File("src/main/resources/proves/bioportal-alc-tasks/task00001.json"));
        OWLAxiom finalConclusion  = proof.getFinalConclusion();
        Collection<IInference<OWLAxiom>> finalInference = proof.getInferences(finalConclusion);
        finalInference.forEach(inference -> {
            ontology.addAxioms(inference.getPremises());
        });

        ALCH_Reasoner reasoner = new ALCH_Reasoner();
        reasoner.setOntology(ontology);
        IProof<OWLAxiom> alchProof = reasoner.getProof(finalConclusion);
        System.out.println("finished");

         */
    }


}
