package extdoc.jsdoc.tags;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 3:45:40
 */
public interface ExtendsTag extends Tag {
    String getClassName();

    String getClassDescription();
}
