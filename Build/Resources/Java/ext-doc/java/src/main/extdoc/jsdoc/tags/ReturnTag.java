package extdoc.jsdoc.tags;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 3:46:56
 */
public interface ReturnTag extends Tag {
    String getReturnType();

    String getReturnDescription();
}
