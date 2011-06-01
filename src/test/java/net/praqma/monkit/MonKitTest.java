package net.praqma.monkit;

import static org.junit.Assert.*;

import org.junit.Test;


public class MonKitTest {
    
    @Test
    public void testMonKit() {
	MonKit mk = new MonKit();
	mk.add("name", "scale", "value", "category");
	
	System.out.println( mk );
    }
    
}
