package RuleHandling;

import java.util.Objects;

public class Tupel <X,Y>{
    private final X x;
    private final Y y;

    public Tupel(X x, Y y){
        this.x = x;
        this.y = y;
    }

    public X getX() {
        return x;
    }

    public Y getY() {
        return y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tupel<?, ?> tupel = (Tupel<?, ?>) o;
        return x.equals(tupel.x) && y.equals(tupel.y);
    }
}
