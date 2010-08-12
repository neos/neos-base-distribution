package extdoc.jsdoc.tags;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 3:44:33
 */
public interface ClassTag extends Tag{
    String getClassName();

    String getClassDescription();
}
