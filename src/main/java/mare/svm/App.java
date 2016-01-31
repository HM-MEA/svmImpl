package mare.svm;

import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) throws IOException {
        List<Data> versicolorData = new ArrayList<>();
        List<Data> setosaData = new ArrayList<>();

        CsvMapReader reader = new CsvMapReader(new FileReader("data.csv"), CsvPreference.TAB_PREFERENCE);
        Map<String, String> map = null;
        String[] header = reader.getHeader(true);
        int no = 0;
        while ((map = reader.read(header)) != null) {
            Data data = new Data();
            if (map.get("Species").equals("setosa")) {
                data.t = 1;
                setosaData.add(data);
            } else if (map.get("Species").equals("versicolor")) {
                data.t = -1;
                versicolorData.add(data);
            } else {
                continue;
            }
            data.x = Double.valueOf(map.get("Petal.Width"));
            data.y = Double.valueOf(map.get("Petal.Length"));
            data.no = no;
            no++;
        }

        for (int i = 1; i < 11; i++) {
            System.out.println("-----------" + i + "回目の試行-----------");
            List<Data> testData = new ArrayList<>();
            Collections.shuffle(setosaData);
            Collections.shuffle(versicolorData);
            setosaData.stream().unordered().limit(5).forEach(testData::add);
            versicolorData.stream().unordered().limit(5).forEach(testData::add);

            List<Data> datas = Stream.concat(setosaData.stream(), versicolorData.stream()).filter(d -> !testData.contains(d)).collect(Collectors.toList());
            SvmImpl svm = new SvmImpl(datas);
            svm.learning();

            testData.stream().forEach(d -> {
                System.out.println("label:" + d.t + " testResult:" + svm.test(d.x, d.y));
            });
        }
    }
}
