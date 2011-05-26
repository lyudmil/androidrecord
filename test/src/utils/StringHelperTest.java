package utils;

import junit.framework.TestCase;

import static com.androidrecord.utils.StringHelper.pluralize;
import static com.androidrecord.utils.StringHelper.underscorize;

public class StringHelperTest extends TestCase {

    public void testPluralize() {
        assertEquals("cats", pluralize("cat"));
        assertEquals("masses", pluralize("mass"));
        assertEquals("categories", pluralize("category"));
    }

    public void testUnderscorize() {
        assertEquals("red_something", underscorize("RedSomething"));
        assertEquals("red_some_thing", underscorize("redSomeThing"));
        assertEquals("red_some_thing", underscorize("Red Some Thing"));
        assertEquals("easy_shopping", underscorize("Easy Shopping"));

    }
}
