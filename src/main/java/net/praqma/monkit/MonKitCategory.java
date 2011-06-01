package net.praqma.monkit;

import java.util.ArrayList;
import java.util.List;

public class MonKitCategory extends ArrayList<MonKitObservation> {

    private String name;
    private String scale;
    
    public MonKitCategory( String name, String scale ) {
	this.name = name;
	this.scale = scale;
    }
    
    public MonKitCategory( String name, String scale, List<MonKitObservation> list ) {
	this.name = name;
	this.scale = scale;
	this.addAll(list);
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getName() {
	return name;
    }

    public void setScale(String scale) {
	this.scale = scale;
    }

    public String getScale() {
	return scale;
    }
}
