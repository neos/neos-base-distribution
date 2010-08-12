package extdoc.jsdoc.tags;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 3:46:06
 */
public interface MemberTag extends Tag {
    String getClassName();

    String getMethodName();
}
