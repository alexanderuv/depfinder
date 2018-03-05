package com.yellowbox.depfinder;

import com.beust.jcommander.JCommander;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.yellowbox.depfinder.analyzer.DependencyExtractor;
import com.yellowbox.depfinder.analyzer.MethodDependency;

import java.io.FileWriter;
import java.io.IOException;

public class Program
{
    public static void main(String[] args) throws IOException
    {
        try {
            FinderArgs parsedArgs = new FinderArgs();
            JCommander.newBuilder()
                    .addObject(parsedArgs)
                    .build()
                    .parse(args);

            ColumnPositionMappingStrategy<CsvRow> mappingStrategy = new ColumnPositionMappingStrategy<>();

            mappingStrategy.setType(CsvRow.class);
            mappingStrategy.setColumnMapping("userClass", "declaringClass", "method");
            FileWriter writer = new FileWriter(parsedArgs.getOutputPath());
            StatefulBeanToCsv<CsvRow> btcsv = new StatefulBeanToCsvBuilder<>(writer)
                    .withQuotechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                    .withMappingStrategy(mappingStrategy)
                    .withSeparator(',')
                    .build();

            DependencyExtractor extractor = new DependencyExtractor(parsedArgs);
            extractor.setHandler(m -> {
                if (parsedArgs.isVerbose()) {
                    System.out.println("Found dependencies in: " + m.getClassName());
                }

                try {
                    for (MethodDependency dep : m.getCapturedDependencies()) {
                        btcsv.write(new CsvRow(m.getClassName(),
                                dep.getClassName(), dep.getMethodSignature().toString()));
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error while writing csv", e);
                }
            });

            extractor.run();
            writer.flush();
            writer.close();
        } catch (Exception e) {
//            System.err.println("Error: " + e.getMessage() +"\n");
//            System.exit(-1);
            throw e;
        }
    }
}
