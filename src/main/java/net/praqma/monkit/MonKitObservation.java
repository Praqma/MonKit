package net.praqma.monkit;

/**
 * This class defines an observation for MonKit.
 * @author wolfgang
 *
 */
public class MonKitObservation {
    private String name;
    private String value;
    private String scale;
    
    public MonKitObservation( String name, String scale, String value ) {
	this.setName(name);
	this.setScale(scale);
	this.setValue(value);
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

    public void setScale(String scale) {
	this.scale = scale;
    }

    public String getScale() {
	return scale;
    }
    
    public String toString() {
	return "Name: " + name + ", scale: " + scale + ", value: " + value;
    }
}
