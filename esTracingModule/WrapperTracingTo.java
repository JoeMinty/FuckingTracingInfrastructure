package es;

import java.io.Serializable;
import java.util.Date;

public class WrapperTracingTo implements Serializable {

    private String index;
    private String source;
    private String id;
    private String label;
    private String target;
    private TracingTo properties;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public TracingTo getProperties() {
        return properties;
    }

    public void setProperties(TracingTo properties) {
        this.properties = properties;
    }
}
