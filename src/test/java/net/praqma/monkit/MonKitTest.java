package net.praqma.monkit;

import static org.junit.Assert.*;

import org.junit.Test;


public class MonKitTest {
    
    @Test
    public void testMonKit() {
	MonKit mk = new MonKit();
	mk.addCategory("category", "scale");
	mk.add("name", "value", "category");
	
	System.out.println( mk );
    }
    
    @Test
    public void testMonKit2() {
	MonKit mk = new MonKit();
	
	mk.addCategory("category", "scale");
	mk.addCategory("category3", "scale3");
	
	mk.add("name1", "value1", "category");
	mk.add("name2", "value2", "category");
	mk.add("name3", "value", "category3");
	
	System.out.println( mk );
    }
    
    
    @Test
    public void testMonKit3() {
	MonKit mk = new MonKit();
	
	mk.addCategory("category", "scale");
	mk.addCategory("category2", "scale2");
	
	mk.add( new MonKitObservation("name1", "value1"), "category" );
	mk.add( new MonKitObservation("name1", "value1"), "category" );
	
	System.out.println( mk );
    }
  
    @Test
    public void testMonKit4() throws MonKitException {
	System.out.println( "test 41\n" );
	
	MonKit mk1 = MonKit.fromString("<categories><category name=\"category\" scale=\"scale\"><observation name=\"name1\">value1</observation><observation name=\"name2\">value2</observation></category><category name=\"category2\" scale=\"scale2\"/></categories>" );
	MonKit mk2 = MonKit.fromString("<categories><category name=\"category\" scale=\"scale\"><observation name=\"name1\">value3</observation><observation name=\"name4\">value1</observation></category><category name=\"category2\" scale=\"scale2\"/></categories>" );
	MonKit mk3 = MonKit.fromString("<categories><category name=\"category2\" scale=\"scale\"><observation name=\"name44\">value1412</observation><observation name=\"name22222\">valu123123e13</observation></category></categories>" );
	
	MonKit mk = MonKit.merge(mk1,mk2, mk3);
	
	for( MonKitCategory mkc : mk.getCategories() ) {
	    System.out.println(mkc.toString() );
	}
	
	System.out.println( mk );
    }
    
}
