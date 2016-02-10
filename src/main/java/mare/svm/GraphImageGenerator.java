package mare.svm;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GraphImageGenerator {

    private final XYSeriesCollection data;
    private final String fileName;

    public GraphImageGenerator(String fileName) {
        data = new XYSeriesCollection();
        this.fileName = fileName;
    }

    public void addSeries(List<Data> list, String label) {
        XYSeries series = new XYSeries(label);
        list.forEach(d -> series.add(d.x, d.y));
        data.addSeries(series);
    }

    public void generate() {

        JFreeChart chart = ChartFactory.createScatterPlot(
                "結果",
                "x",
                "y",
                data,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
        XYPlot plot = (XYPlot) chart.getPlot();
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setRange(0, 1.1);
        ValueAxis domainAxis = plot.getDomainAxis();
        domainAxis.setRange(0, 1.1);

        try {
            ChartUtilities.saveChartAsPNG(new File("file/" + fileName), chart, 600, 600);
            System.out.println("generate file:" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
