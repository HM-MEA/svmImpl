package mare.svm;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) throws IOException {
        List<Data> dataList = DataReader.getData();
        List<Data> versicolorData = dataList.stream().filter(d -> d.t == -1).collect(Collectors.toList());
        List<Data> setosaData = dataList.stream().filter(d -> d.t == 1).collect(Collectors.toList());

        List<Data> datas = Stream.concat(setosaData.stream(), versicolorData.stream()).collect(Collectors.toList());
        SvmImpl svm = new SvmImpl(datas);
        Consumer<String> imageGenerate = (s) -> {
            GraphImageGenerator generator = new GraphImageGenerator(s);
            generator.addSeries(setosaData, "setosa");
            generator.addSeries(versicolorData, "verticolor");
            generator.addSeries(svm.getBoundaryData(), "boundary");
            generator.generate();
        };
        svm.setImageGenerate(imageGenerate);
        svm.learning();
    }
}
