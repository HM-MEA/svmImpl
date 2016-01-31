package mare.svm;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SvmImpl {

    final int C = Integer.MAX_VALUE;
    final int n;
    final double[] xx;
    final double[] yy;
    final int[] tt;
    double[] aa;

    public SvmImpl(List<Data> dataList) {
        this.xx = dataList.stream().mapToDouble(d -> d.x).toArray();
        this.yy = dataList.stream().mapToDouble(d -> d.y).toArray();
        this.tt = dataList.stream().mapToInt(d -> d.t).toArray();
        n = xx.length;
        aa = new double[n];
        Arrays.fill(aa, 0);
    }

    public void learning() {
        int no = 0;
        boolean f = true;
        while (f) {
            f = false;
            for (int j = 0; j < n; j++) {
                if (kktVio(j)) {
                    f = true;
                    int i = rand(j);
                    boolean u = update(i, j);
                    if (!u) {
                        no++;
                    } else {
                        no = 0;
                    }
                }
            }
            if (no > 1000) return;
        }
    }

    public double y(int i) {
        double ans = 0;
        for (int j = 0; j < n; j++) {
            ans += aa[j] * tt[j] * innerP(i, j);
        }
        return ans + b();
    }

    public double b() {
        List<Pair<Double, Integer>> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (aa[i] != 0) {
                list.add(new ImmutablePair(aa[i], i));
            }
        }
        if (list.isEmpty()) return 0;

        double b = 0;
        for (Pair<Double, Integer> n : list) {
            int ni = n.getRight();
            double tmp = 0;
            for (Pair<Double, Integer> m : list) {
                int mi = m.getRight();
                tmp += m.getLeft() * tt[mi] * innerP(ni, mi);
            }

            b += tt[ni] - tmp;
        }

        return b / list.size();
    }

    private double innerP(int i, int j) {
        return xx[i] * xx[j] + yy[i] * yy[j];
    }

    private double innerP(double x, double y, int i) {
        return x * xx[i] + y * yy[i];
    }

    private boolean kktVio(int i) {
        return (aa[i] > 0.00000001 && tt[i] * y(i) != 1) || (aa[i] < 0.00000001 && tt[i] * y(i) < 1);
    }

    public int rand(int i) {
        Random rnd = new Random();
        while (true) {
            int ran = rnd.nextInt(n);
            if (ran != i) return ran;
        }
    }

    public boolean update(int i, int j) {
        double L;
        double H;
        if (tt[i] != tt[j]) {
            L = Math.max(0, aa[j] - aa[i]);
            H = Math.min(C, C - aa[j] + aa[i]);
        } else {
            L = Math.max(0, aa[j] + aa[i] - C);
            H = Math.max(C, aa[j] + aa[i]);
        }

        double ajNew = aa[j] + tt[j] * (e(i) - e(j)) / (innerP(i, i) - 2 * innerP(i, j) + innerP(j, j));
        if (ajNew < L) {
            ajNew = L;
        } else if (ajNew > H) {
            ajNew = H;
        }
        double aiNew = aa[i] + tt[i] * tt[j] * (aa[j] - ajNew);
        if (ajNew - aa[j] < 0.0000001 || Double.isNaN(ajNew)) {
            return false;
        }
        aa[j] = ajNew;
        aa[i] = aiNew;

        return true;
    }

    public double e(int i) {
        double ans = 0;
        for (int j = 0; j < n; j++) {
            ans += aa[j] * tt[j] * innerP(i, j);
        }
        return ans - tt[i];
    }

    public int test(double x, double y) {
        double ans = 0;
        for (int j = 0; j < n; j++) {
            ans += aa[j] * tt[j] * innerP(x, y, j);
        }
        ans = ans + b();
        if (ans >= 0) {
            return 1;
        } else {
            return -1;
        }
    }
}
