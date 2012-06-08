package net.praqma.monkit;

import static org.junit.Assert.*;

import org.junit.Test;


public class MonKitTest {
    
    @Test
    public void testMonKit() {
	MonKit mk = new MonKit();
	mk.addCategory("category", "scale");
	mk.add("name", "1", "category");
		
	assertTrue(mk.validate());
	
	System.out.println( mk );
    }
    
    @Test
    public void testMonKit2() {
	MonKit mk = new MonKit();
	
	mk.addCategory("category", "scale");
	mk.addCategory("category3", "scale3");
	
	mk.add("name1", "2", "category");
	mk.add("name2", "3", "category");
	mk.add("name3", "4", "category3");
	
	assertTrue(mk.validate());
	
	System.out.println( mk );
    }
    
    
    @Test
    public void testMonKit3() {
	MonKit mk = new MonKit();
	
	mk.addCategory("category", "scale");
	mk.addCategory("category2", "scale2");
	
	mk.add( new MonKitObservation("name1", "5"), "category" );
	mk.add( new MonKitObservation("name1", "6"), "category" );
	
	assertTrue(mk.validate());
	
	System.out.println( mk );
    }
  
    @Test
    public void testMonKit4() throws MonKitException {
	System.out.println( "test 41\n" );
	
	MonKit mk1 = MonKit.fromString("<categories><category name=\"category\" scale=\"scale\"><observation name=\"name1\">7</observation><observation name=\"name2\">10</observation></category><category name=\"category2\" scale=\"scale2\"/></categories>" );
	MonKit mk2 = MonKit.fromString("<categories><category name=\"category\" scale=\"scale\"><observation name=\"name1\">8</observation><observation name=\"name4\">11</observation></category><category name=\"category2\" scale=\"scale2\"/></categories>" );
	MonKit mk3 = MonKit.fromString("<categories><category name=\"category2\" scale=\"scale\"><observation name=\"name44\">9</observation><observation name=\"name22222\">12</observation></category></categories>" );
	
	MonKit mk = MonKit.merge(mk1,mk2, mk3);
	
	for( MonKitCategory mkc : mk.getCategories() ) {
	    System.out.println(mkc.toString() );
	}
	System.out.println( mk );
	assertTrue(mk.validate());
	
	
    }
    
    @Test
    public void testAllCategoriesEmpty() throws MonKitException {
        MonKit mk1 = MonKit.fromString("<categories><category name=\"category\" scale=\"scale\"><observation name=\"name1\">7</observation><observation name=\"name2\">10</observation></category><category name=\"category2\" scale=\"scale2\"/></categories>" );
        MonKit mk2 = MonKit.fromString("<categories><category name=\"category\" scale=\"scale\"><observation name=\"name1\">8</observation><observation name=\"name4\">11</observation></category><category name=\"category2\" scale=\"scale2\"/></categories>" );
        MonKit mk3 = MonKit.fromString("<categories><category name=\"category2\" scale=\"scale\"><observation name=\"name44\">9</observation><observation name=\"name22222\">12</observation></category></categories>" );
	
        MonKit mk = MonKit.merge(mk1,mk2, mk3);
        
        assertFalse(mk.isAllCategoriesEmpty());
        
        MonKit empty = new MonKit();
        empty.add(new MonKitCategory("Scale", "Megabytes"));
        
        assertTrue(empty.isAllCategoriesEmpty());
        
        MonKit emptyNoCats = new MonKit();
        assertTrue(emptyNoCats.isAllCategoriesEmpty());
        
    }
    
}
