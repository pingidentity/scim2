package com.unboundid.scim2;

import com.unboundid.scim2.filters.Filter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.unboundid.scim2.filters.Filter.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Created by boli on 3/27/15.
 */
public class PathParsingTestCase
{
  /**
   * Retrieves a set of valid path strings.
   *
   * @return  A set of valid path strings.
   */
  @DataProvider(name = "testValidPathStrings")
  public Object[][] getTestValidPathStrings()
  {
    return new Object[][]
        {
            new Object[] { "attr",
                Path.fromAttribute("attr") },
            new Object[] { "urn:extension:attr",
                Path.fromAttribute("urn:extension", "attr") },
            new Object[] { "attr.subAttr",
                Path.fromAttribute("attr").sub("subAttr") },
            new Object[] { "attr[subAttr eq \"78750\"].subAttr",
                Path.fromAttribute("attr", eq("subAttr", "78750")).sub("subAttr") },
            new Object[] { "urn:extension:attr[subAttr eq \"78750\"].subAttr",
                Path.fromAttribute("urn:extension", "attr",
                    eq("subAttr", "78750")).sub("subAttr") },

            // The following does not technically conform to the SCIM spec
            // but our path impl is lenient so it may be used with any JSON
            // object.
            new Object[] { "", Path.root() },
            new Object[] { "urn:extension:", Path.root("urn:extension") },
            new Object[] { "urn:extension:attr[subAttr eq \"78750\"]." +
                "subAttr[subSub pr].this.is.crazy[type eq \"good\"].deep",
                Path.fromAttribute("urn:extension", "attr", eq("subAttr", "78750")).
                    sub("subAttr", pr("subSub")).
                    sub("this").
                    sub("is").
                    sub("crazy", eq("type", "good")).
                    sub("deep") },

        };
  }



  /**
   * Retrieves a set of invalid path strings.
   *
   * @return  A set of invalid path strings.
   */
  @DataProvider(name = "testInvalidPathStrings")
  public Object[][] getTestInvalidPathStrings()
  {
    return new Object[][]
        {
            new Object[] { "." },
            new Object[] { "attr." },
            new Object[] { "urn:attr" },
            new Object[] { "attr[].subAttr" },
            new Object[] { ".attr" },
            new Object[] { "urn:extension:." },
            new Object[] { "urn:extension:.attr" },
            new Object[] { "attr[subAttr eq 123]." },
        };
  }



  /**
   * Tests the {@code fromString} method with a valid path string.
   *
   * @param  pathString  The string representation of the path to fromString.
   *
   * @throws Exception  If the test fails.
   */
  @Test(dataProvider = "testValidPathStrings")
  public void testParseValidPath(final String pathString,
                                   final Path expectedPath)
      throws Exception
  {
    final Path parsedPath = Path.fromString(pathString);
    assertEquals(parsedPath, expectedPath);
  }



  /**
   * Tests the {@code fromString} method with an invalid path string.
   *
   * @param  filterString  The string representation of the path to fromString.
   *
   * @throws Exception If the test fails.
   */
  @Test(dataProvider = "testInvalidPathStrings")
  public void testParseInvalidPath(final String filterString)
      throws Exception
  {
    try
    {
      Filter.fromString(filterString);
      fail("Unexpected successful fromString of invalid path: " + filterString);
    }
    catch (IllegalArgumentException e)
    {
//      System.out.println("Parse invalid path: " + filterString);
//      System.out.println("Error message: " + e.getMessage());
    }
  }
}
