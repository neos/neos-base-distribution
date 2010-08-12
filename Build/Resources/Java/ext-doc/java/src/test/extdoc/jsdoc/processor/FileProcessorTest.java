package extdoc.jsdoc.processor;

import extdoc.jsdoc.tags.impl.Comment;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * User: Andrey Zubkov
 * Date: 06.12.2008
 * Time: 19:42:49
 */
public class FileProcessorTest {

    @Test
    public void testCommentTypeClass(){
        String test = " * @class Test";
        FileProcessor.CommentType commentType =
                FileProcessor.resolveCommentType(new Comment(test));
        assertEquals(FileProcessor.CommentType.CLASS, commentType);
    }

   @Test
    public void testCommentTypeCfg(){
        String test =
                "     * @cfg {Boolean/Object} autoCreate A DomHelper element spec, or true for a default element spec (defaults to:\n" +
                "     * {tag: \"input\", type: \"text\", size: \"24\", autocomplete: \"off\"})";
        FileProcessor.CommentType commentType =
                FileProcessor.resolveCommentType(new Comment(test));
        assertEquals(FileProcessor.CommentType.CFG, commentType);
    }

    @Test
    public void testCommentTypePropertyWithType(){
        String test =
                "         * True if the detected platform is Linux.\n" +
                "         * @type Boolean";
        FileProcessor.CommentType commentType =
                FileProcessor.resolveCommentType(new Comment(test));
        assertEquals(FileProcessor.CommentType.PROPERTY, commentType);
    }

    @Test
    public void testCommentTypePropertySimple(){
        String test = " The normal browser event ";
        FileProcessor.CommentType commentType =
                FileProcessor.resolveCommentType(new Comment(test));
        assertEquals(FileProcessor.CommentType.PROPERTY, commentType);
    }

    @Test
    public void testCommentTypePropertySimpleMethod(){
        String test = " The normal browser event ";
        FileProcessor.CommentType commentType =
                FileProcessor.resolveCommentType(new Comment(test), "", "function");
        assertEquals(FileProcessor.CommentType.METHOD, commentType);
    }


    @Test
    public void testCommentTypeMethod(){
        String test =
                "        * Returns the current HTML document object as an {@link Ext.Element}.\n" +
                "        * @return Ext.Element The document";
        FileProcessor.CommentType commentType =
                FileProcessor.resolveCommentType(new Comment(test));
        assertEquals(FileProcessor.CommentType.METHOD, commentType);
    }


    @Test
    public void testCommentTypeEventWithParam(){
        String test =
                "            * @event beforeselect\n" +
                "             * Fires before a list item is selected. Return false to cancel the selection.\n" +
                "             * @param {Ext.form.ComboBox} combo This combo box\n" +
                "             * @param {Ext.data.Record} record The data record returned from the underlying store\n" +
                "             * @param {Number} index The index of the selected item in the dropdown list";
        FileProcessor.CommentType commentType =
                FileProcessor.resolveCommentType(new Comment(test));
        assertEquals(FileProcessor.CommentType.EVENT, commentType);
    }



}
