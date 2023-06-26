package RuleHandling.matchVisitors;

import org.semanticweb.owlapi.model.*;

public class RightDisjunctionVisitor extends BooleanMatchVisitor {
    @Override
    public Boolean visit(OWLObjectUnionOf ce) {
        return true;
    }

}
