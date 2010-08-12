package extdoc.jsdoc.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * User: Andrey Zubkov
 * Date: 06.12.2008
 * Time: 14:34:24
 */
public class StringUtilsTest {

    @Test
    public void testProcessLinkFull(){
        StringUtils.ClsAttrName res  =
                StringUtils.processLink("Ext.Button#set setMethod");
        assertEquals(res.cls, "Ext.Button");
        assertEquals(res.attr, "set");
        assertEquals(res.name, "setMethod");
    }
    
    @Test
    public void testProcessLinkEmpty(){
        StringUtils.ClsAttrName res  = StringUtils.processLink("");
        assertEquals(res.cls, "");
        assertEquals(res.attr, "");
        assertEquals(res.name, "");
    }

    @Test
    public void testProcessLinkClsMethod(){
        StringUtils.ClsAttrName res  =
                StringUtils.processLink("Ext.Button#set");
        assertEquals(res.cls, "Ext.Button");
        assertEquals(res.attr, "set");
        assertEquals(res.name, "");
    }

    @Test
    public void testProcessLinkMethodName(){
        StringUtils.ClsAttrName res  =
                StringUtils.processLink("#set setMethod");
        assertEquals(res.cls, "");
        assertEquals(res.attr, "set");
        assertEquals(res.name, "setMethod");
    }

    @Test
    public void testProcessLinkMethod(){
        StringUtils.ClsAttrName res  =
                StringUtils.processLink("#set   ");
        assertEquals(res.cls, "");
        assertEquals(res.attr, "set");
        assertEquals(res.name, "");
    }

    @Test
    public void testProcessLinkSkipWhite(){
        StringUtils.ClsAttrName res  =
                StringUtils.processLink("Ext.Button#set        setMethod");
        assertEquals(res.cls, "Ext.Button");
        assertEquals(res.attr, "set");
        assertEquals(res.name, "setMethod");
    }

        @Test
    public void testProcessLinkClass(){
        StringUtils.ClsAttrName res  =
                StringUtils.processLink("Ext.Button");
        assertEquals(res.cls, "Ext.Button");
        assertEquals(res.attr, "");
        assertEquals(res.name, "");
    }

    @Test
    public void testProcessLinkClassName(){
        StringUtils.ClsAttrName res  =
                StringUtils.processLink("Ext.Button button");
        assertEquals(res.cls, "Ext.Button");
        assertEquals(res.attr, "");
        assertEquals(res.name, "button");
    }

    @Test
    public void testHighlightQuotes(){
        String res  =
                StringUtils.highlightQuotes("var test=\"val\" + 'test'; // Comment");
        assertEquals( "var test=<em>\"val\"</em> + <em>\'test\'</em>; // Comment", res);
    }

    @Test
    public void testHighlightKeywords(){
        String res  =
                StringUtils.highlightKeywords("var test=\"val\"; // Comment new");
        assertEquals( "<b>var</b> test=\"val\"; // Comment <b>new</b>", res);
    }

     @Test
    public void testHighlightKeywordsSimple(){
        String res  =
                StringUtils.highlightKeywords("var");
        assertEquals( "<b>var</b>", res);
    }

    @Test
    public void testHighlightKeywordsEmpty(){
        String res  =
                StringUtils.highlightKeywords("");
        assertEquals( "", res);
    }

    @Test
    public void testHighlightKeywordsTwo(){
        String res  =
                StringUtils.highlightKeywords("var new");
        assertEquals( "<b>var</b> <b>new</b>", res);
    }

    @Test
    public void testHighlightCommentsEmpty(){
        String res  =
                StringUtils.highlightComments("");
        assertEquals( "", res);
    }

    @Test
    public void testHighlightCommentsNorm(){
        String res  =
                StringUtils.highlightComments("var t=10; // this is\nnew line");
        assertEquals( "var t=10; <i>// this is</i>\nnew line", res);
    }

    @Test
    public void testHighlightCommentsEnds(){
        String res  =
                StringUtils.highlightComments("var t=10; // test");
        assertEquals( "var t=10; <i>// test</i>", res);
    }

    @Test
    public void testHighlightCommentsStrange(){
        String res  =
                StringUtils.highlightComments("/ //comment/");
        assertEquals( "/ <i>//comment/</i>", res);
    }

    @Test
    public void testHighlightCodeEmpty(){
        String res  =
                StringUtils.highlightCode("");
        assertEquals( "", res);
    }

    @Test
    public void testHighlightCodeNorm(){
        String res  =
                StringUtils.highlightCode("Text <code>This is \'high\'lighted</code> remain.");
        assertEquals( "Text <code>This is <em>\'high\'</em>lighted</code> remain.", res);
    }

    @Test
    public void testHighlightCodeBorder(){
        String res  =
                StringUtils.highlightCode("Text <code>This is \'high\'lighted</code>");
        assertEquals( "Text <code>This is <em>\'high\'</em>lighted</code>", res);
    }

    @Test
    public void testHighlightCodeBorder2(){
        String res  =
                StringUtils.highlightCode("<code>This is \'high\'lighted</code>");
        assertEquals( "<code>This is <em>\'high\'</em>lighted</code>", res);
    }


}
