package mare.svm;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SvmImpl {

    double C = Integer.MAX_VALUE;
    final int n;
    final double[] xx;
    final double[] yy;
    final int[] tt;
    double[] aa;
    private final Random rnd;

    int count = 0;

    String name;

    private Consumer<String> imageGenerate;

    public SvmImpl(Set<Data> dataSe) {
        this.xx = dataSe.stream().mapToDouble(d -> d.x).toArray();
        this.yy = dataSe.stream().mapToDouble(d -> d.y).toArray();
        this.tt = dataSe.stream().mapToInt(d -> d.t).toArray();
        n = xx.length;
        aa = new double[n];
        Arrays.fill(aa, 0);
        rnd = new Random();
        name = "test.png";
    }

    public void setImageGenerate(Consumer<String> imageGenerate) {
        this.imageGenerate = imageGenerate;
    }

    public void learning() {
        boolean f = true;
        int c = 0;
        while (f) {
            f = false;
            for (int j = 0; j < n; j++) {
                if (kktVio(j)) {
                    f = true;
                    boolean u = false;
                    {
                        int i = selectP21(j);
                        u = update(i, j);
                    }
                    OptionalInt oi = selectP22(j);
                    if (oi.isPresent() && !u) {
                        int i = oi.getAsInt();
                        u = update(i, j);
                    }
                    if (!u) {
                        int i = selectP23(j);
                        u = update(i, j);
                    }

                    if (u) {
                        c = 0;
                    } else {
                        c++;
                    }
                }
            }
            if (c > 10000) {
                break;
            }
        }
        System.out.printf("learning finished. %d update done.\n", count);
        if (imageGenerate != null) {
            imageGenerate.accept(name);
        }
    }

    private double y(int i) {
        double ans = 0;
        for (int j = 0; j < n; j++) {
            ans += aa[j] * tt[j] * k(i, j);
        }
        return ans + b();
    }

    private double y(double x, double y) {
        double ans = 0;
        for (int j = 0; j < n; j++) {
            ans += aa[j] * tt[j] * k(x, y, j);
        }
        return ans + b();
    }

    private double b() {
        List<Pair<Double, Integer>> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (0.1 < aa[i] && aa[i] < C) {
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
                tmp += m.getLeft() * tt[mi] * k(ni, mi);
            }

            b += tt[ni] - tmp;
        }

        return b / list.size();
    }

    protected double k(int i, int j) {
        return xx[i] * xx[j] + yy[i] * yy[j];
    }

    protected double k(double x, double y, int i) {
        return x * xx[i] + y * yy[i];
    }

    private boolean kktVio(int i) {
        if (aa[i] == 0 && tt[i] * y(i) <= 1.001) {
            return true;
        }
        if ((aa[i] > 0 && aa[i] < C) && (tt[i] * y(i) > 1.001 && tt[i] * y(i) < 0.999)) {
            return true;
        }
        if (aa[i] == C && tt[i] * y(i) >= 0.999) {
            return true;
        }

        return false;
    }

    /**
     * 2点目選択その1
     */
    private int selectP21(int j) {
        double maxE = 0;
        int maxI = 0;
        for (int i = 0; i < n; i++) {
            if (i != j) {
                double t = Math.abs(e(i) - e(j));
                if (maxE < t) {
                    maxE = t;
                    maxI = i;
                }
            }
        }
        return maxI;
    }

    /**
     * 2点目選択その2
     */
    private OptionalInt selectP22(int j) {
        List<Integer> list = IntStream.range(0, n).filter(i -> i != j).filter(i -> aa[i] > 0 && aa[i] < C).mapToObj(Integer::new).collect(Collectors.toList());
        if (list.isEmpty()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(list.get(rnd.nextInt(list.size())));
    }

    /**
     * 2点目選択その3
     */
    private int selectP23(int j) {
        return rand(j);
    }

    private int rand(int j) {
        while (true) {
            int i = rnd.nextInt(n);
            if (i != j) return i;
        }
    }

    private boolean update(int i, int j) {
        double L;
        double H;
        if (tt[i] != tt[j]) {
            L = Math.max(0, aa[j] - aa[i]);
            H = Math.min(C, C - aa[j] + aa[i]);
        } else {
            L = Math.max(0, aa[j] + aa[i] - C);
            H = Math.min(C, aa[j] + aa[i]);
        }
        double ajNew = aa[j] + (tt[j] * (e(i) - e(j))) / (k(i, i) - 2 * k(i, j) + k(j, j));
        if (ajNew < L) {
            ajNew = L;
        } else if (ajNew > H) {
            ajNew = H;
        }

        if (Math.abs(aa[j] - ajNew) < 0.001 * (aa[j] + ajNew + 0.001)) {
            return false;
        }
        double aiNew = aa[i] + tt[i] * tt[j] * (aa[j] - ajNew);
        if (Double.isNaN(ajNew) || aiNew < 0 || aiNew > C) {
            return false;
        }
        aa[j] = ajNew;
        aa[i] = aiNew;
        count++;
        imageGenerate.accept(String.format("test%s.png", new DecimalFormat("000").format(count)));
        return true;
    }

    private double e(int i) {
        double ans = 0;
        for (int j = 0; j < n; j++) {
            ans += aa[j] * tt[j] * k(i, j);
        }
        return ans - tt[i];
    }

    public boolean test(double x, double y, int t) {
        double ans = 0;
        for (int j = 0; j < n; j++) {
            ans += aa[j] * tt[j] * k(x, y, j);
        }
        ans = ans + b();
        if (ans >= 0 && t == 1) {
            return true;
        } else if (ans < 0 && t == -1) {
            return true;
        } else {
            return false;
        }
    }

    public double testP(Set<Data> set) {
        return (double) set.stream().filter(d -> test(d.x, d.y, d.t)).count() / (double) set.size();
    }

    public List<Data> getBoundaryData() {
        List<Data> data = new ArrayList<>();
        for (double i = 0.0; i < 1.5; i += 0.01) {
            for (double j = 0.0; j < 1.5; j += 0.01) {
                if (y(i, j) * y(i + 0.01, j + 0.01) <= 0) {
                    Data d = new Data();
                    d.x = i;
                    d.y = j;
                    data.add(d);
                }
            }
        }

        return data;
    }
}
