package es;

import com.alibaba.fastjson.JSONReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TracingToUtils {

    public static List<WrapperTracingTo> parseTraceData() throws FileNotFoundException {
        String dataPath = "hello.json";
        JSONReader reader = new JSONReader(new FileReader(dataPath));
        reader.startArray();

        List<WrapperTracingTo> spanList = new ArrayList<>();
        while (reader.hasNext()) {
            spanList.add(reader.readObject(WrapperTracingTo.class));
        }
        return spanList;
    }

}
