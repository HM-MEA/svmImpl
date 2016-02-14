package mare.svm;

import java.util.Set;

public class SvmImpl2 extends SvmImpl {

    private double gamma;

    public SvmImpl2(Set<Data> dataSet, double c, double g) {
        super(dataSet);
        C = c;
        gamma = g;
        name = "test.png";
    }

    @Override
    protected double k(int i, int j) {
        return Math.exp(-gamma * (Math.pow(xx[i] - xx[j], 2) + Math.pow(yy[i] - yy[j], 2)));
    }

    @Override
    protected double k(double x, double y, int j) {
        return Math.exp(-gamma * (Math.pow(x - xx[j], 2) + Math.pow(y - yy[j], 2)));
    }
}
