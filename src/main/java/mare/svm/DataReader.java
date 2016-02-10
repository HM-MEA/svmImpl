package mare.svm;

import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataReader {

    public static List<Data> getData() throws IOException {
        CsvMapReader reader = new CsvMapReader(new FileReader("data.csv"), CsvPreference.TAB_PREFERENCE);
        Map<String, String> map = null;
        String[] header = reader.getHeader(true);
        int no = 0;
        List<Data> list = new ArrayList<>();
        double maxX = 0;
        double maxY = 0;
        while ((map = reader.read(header)) != null) {
            Data data = new Data();
            if (map.get("Species").equals("setosa")) {
                data.t = 1;
            } else if (map.get("Species").equals("versicolor")) {
                data.t = -1;
            } else {
                continue;
            }
            data.x = Double.valueOf(map.get("Petal.Width"));
            data.y = Double.valueOf(map.get("Petal.Length"));
            data.no = no;
            list.add(data);
            no++;

            maxX = Math.max(maxX, data.x);
            maxY = Math.max(maxY, data.y);
        }

        final double x = maxX;
        final double y = maxY;
        //データの正規化
        return list.stream().map(d -> {
            Data data = new Data();
            data.x = d.x / x;
            data.y = d.y / y;
            data.t = d.t;
            return data;
        }).collect(Collectors.toList());
    }
}
