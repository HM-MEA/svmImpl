package mare.svm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws IOException {
        Set<Data> dataSet = DataReader2.getData();
        List<Data> data1 = dataSet.stream().filter(d -> d.t == -1).collect(Collectors.toList());
        List<Data> data2 = dataSet.stream().filter(d -> d.t == 1).collect(Collectors.toList());

        SvmImpl svm = new SvmImpl2(dataSet, 0.5, 32);
        Consumer<String> imageGenerate = (s) -> {
            GraphImageGenerator generator = new GraphImageGenerator(s);
            generator.addSeries(data1, "data1");
            generator.addSeries(data2, "data2");
            generator.addSeries(svm.getBoundaryData(), "boundary");
            generator.generate();
        };
        svm.setImageGenerate(imageGenerate);
        svm.learning();

//        List<Set<Data>> dataSetList = new ArrayList<>();
//        for(int i = 0;i < 3;i++){
//            Set<Data> set = dataSet.stream().limit(34).collect(Collectors.toSet());
//            dataSetList.add(set);
//            dataSet.removeAll(set);
//        }
//
//        double maxC = 0;
//        double maxG = 0;
//        double maxAc = 0;
//        for(int i = -5;i <= 5;i++){
//            for(int j = -5;j <= 5;j++){
//                double ac = cv(dataSetList,Math.pow(2,i),Math.pow(2,j));
//                if(ac > maxAc){
//                    maxC = Math.pow(2,i);
//                    maxG = Math.pow(2,j);
//                    maxAc = ac;
//                }
//            }
//        }
//        System.out.printf("C:%f,Gamma:%f,Accuracy:%f",maxC,maxG,maxAc);
    }

    public static double cv(List<Set<Data>> dataSetList, double C, double g) {
        List<Double> answerP = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Set<Data> testSet = dataSetList.get(i);
            Set<Data> set = dataSetList.stream().filter(s -> !s.containsAll(testSet)).flatMap(s -> s.stream()).collect(Collectors.toSet());
            SvmImpl svm = new SvmImpl2(set, C, g);
            svm.learning();
            answerP.add(svm.testP(testSet));
        }

        double avg = answerP.stream().mapToDouble(d -> d).average().getAsDouble();
        System.out.printf("Accuracy:%f\n", avg);
        return avg;
    }
}
