package extdoc.jsdoc.tree;

import extdoc.jsdoc.docs.DocClass;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Andrey Zubkov
 * Date: 01.11.2008
 * Time: 16:48:21
 */
@XmlRootElement
public class TreePackage implements Comparable<TreePackage>{

    @XmlAttribute
    public String name;

    @XmlAttribute
    public String fullName;

    public List<TreePackage> packages =
        new ArrayList<TreePackage>();

    public List<TreeClass> classes =
            new ArrayList<TreeClass>();

    public void addClass(DocClass docClass){
        addClass(docClass.packageName, docClass);
    }

    public void addClass(String packageName, DocClass docClass){
        if (packageName.equals("")){
            TreeClass treeClass = new TreeClass();
            treeClass.className = docClass.className;
            treeClass.shortClassName = docClass.shortClassName;
            treeClass.singleton = docClass.singleton;
            treeClass.component = docClass.component;
            treeClass.customTags = docClass.customTags;
            classes.add(treeClass);
        }else{
            int i=0;
            int len = packageName.length();
            while (i<len && packageName.charAt(i)!='.'){
                i++;
            }
            String pkg = packageName.substring(0,i);
            String remains = (i<len)?packageName.substring(i+1,len):"";
            TreePackage p = addPackage(pkg);
            p.addClass(remains, docClass);
        }
    }

    /**
     * Returns existing or creates new package if not exists
     * @return returns new or existing package
     */
    protected TreePackage addPackage(String packageName){
        for(TreePackage p: packages){
            if (p.name.equals(packageName)) return p;
        }
        TreePackage p = new TreePackage();
        p.name = packageName;
        if (fullName!=null){
            p.fullName = fullName + '.' + packageName;
        }else{
            p.fullName = packageName;
        }
        packages.add(p);
        return p;
    }

    /**
     * Recursive sort for all packages and classes
     */
    public void sort(){
        for(TreePackage p: packages){
            p.sort();
        }
        Collections.sort(packages);
        Collections.sort(classes);
    }

    public int compareTo(TreePackage o) {
        return name.compareTo(o.name);
    }
}
