package net.praqma.monkit;

/**
 * This class defines an observation for MonKit.
 * @author wolfgang
 *
 */
public class MonKitObservation {
    private String name;
    private String value;
    
    public MonKitObservation( String name, String value ) {
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "Name: " + name + ", value: " + value;
    }
}
