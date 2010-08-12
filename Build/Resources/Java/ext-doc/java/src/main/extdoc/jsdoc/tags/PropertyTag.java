package extdoc.jsdoc.tags;

/**
 * User: Andrey Zubkov
 * Date: 27.11.2008
 * Time: 23:58:58
 */
public interface PropertyTag extends Tag{
    String getPropertyName();
    String getPropertyDescription();
}
