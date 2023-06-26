package RuleHandling.matchVisitors;

import org.semanticweb.owlapi.model.*;

public class RightExistentialVisitor extends BooleanMatchVisitor {
    @Override
    public Boolean visit(OWLObjectSomeValuesFrom ce) {
        return true;
    }

}
