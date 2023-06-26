package ProofPresentation;

import de.tu_dresden.inf.lat.evee.proofs.interfaces.IInference;
import de.tu_dresden.inf.lat.evee.proofs.interfaces.IProof;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import org.semanticweb.owlapi.model.OWLAxiom;
import uk.ac.man.cs.lethe.internal.tools.formatting.SimpleOWLFormatter;

import java.io.File;
import java.io.IOException;

import static guru.nidi.graphviz.model.Link.to;

public class ProofRenderer {
    public static void drawProof(IProof<OWLAxiom> proof, String fileName)
            throws IOException {
        MutableGraph g =
                guru.nidi.graphviz.model.Factory.mutGraph(fileName).setDirected(true);

        int edgeId = 0;

        for (IInference<OWLAxiom> inference : proof.getInferences()) {

            MutableNode hyperConnection =
                    guru.nidi.graphviz.model.Factory.mutNode(String.valueOf(edgeId)).add(
                            Label.of(" " + inference.getRuleName().replace(" ", "\n")),
                            Style.FILLED, Color.rgb(0, 191, 255),
                            Shape.RECTANGLE);
            edgeId++;

            g.add(hyperConnection

                    .addLink(to(guru.nidi.graphviz.model.Factory.mutNode(inference.getConclusion().toString())

                            .add(Label.of(SimpleOWLFormatter.format(inference.getConclusion()))))));

            for (OWLAxiom prem : inference.getPremises()) {
                g.add(guru.nidi.graphviz.model.Factory.mutNode(prem.toString())

                        .add(Label.of(SimpleOWLFormatter.format(prem))).addLink(hyperConnection));
            }
        }
        Graphviz.fromGraph(g).render(Format.PNG)
                .toFile(new File(fileName ));
    }

}
