package RuleHandling.matchVisitors;

import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExistsMinusMatcherRight extends MatchVisitor{


    private OWLClass className;
    private OWLSubClassOfAxiom currentAxiom;
    private Map<OWLClass, List<OWLSubClassOfAxiom>> matchToAxiom;

    public Map<OWLClass, List<OWLSubClassOfAxiom>> matchToClass(List<OWLSubClassOfAxiom> activeAxioms, OWLClass className, OWLClassExpression leftMatch){
        this.matchToAxiom = new HashMap<>();
        this.className = className;
        activeAxioms.forEach(axiom -> {
            if(axiom.getSubClass().equals(leftMatch)){
                this.currentAxiom = axiom;
                axiom.getSuperClass().accept(this);
            }
        });
        return this.matchToAxiom;
    }

    //What if it does not match this structure? -> then it just does not fill anything and we are ok right? check weather an error is thrown.
    //Should Work!!!!


    @Override
    public void visit(OWLObjectUnionOf ce) {
        ce.getOperandsAsList().forEach(operand ->{
            operand.accept(this);
        });
    }

    @Override
    public void visit(OWLClass owlClass) {
        if(owlClass.equals(this.className)){
            if (this.matchToAxiom.containsKey(owlClass)){
                this.matchToAxiom.get(owlClass).add(this.currentAxiom);
            } else {
                List<OWLSubClassOfAxiom> list = new ArrayList<>();
                list.add(this.currentAxiom);
                this.matchToAxiom.put(owlClass, list);
            }
        }
    }
}
