package Loading;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.apibinding.OWLManager;

import java.io.File;
import java.util.*;

public class OntologyLoader {

    private List<OWLOntology> ontologies = new ArrayList<>();


    public OntologyLoader(){}

    public List<OWLOntology> getOntologies() {
        return ontologies;
    }

    public void loadOwlFile(String path) throws OWLOntologyCreationException {
        OWLOntology ontology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(
                new File(path));
        ontologies.add(ontology);
    }
    private OWLOntology normalizeOntology(OWLOntology ontology){
        return null;
    }

    private void structuralTransformation(OWLOntology ontology){

    }

}
