/*
 * Copyright 2024-2026 Ping Identity Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2024-2026 Ping Identity Corporation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */

package com.unboundid.scim2.server.utils;

import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.utils.Version;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.fail;


/**
 * This class provides a version of the {@code NullabilityAnnotationTest} that
 * evaluates the classes within the {@code scim2-sdk-server} module.
 */
public class ServerNullabilityAnnotationTest
{
  /**
   * The end-of-line character for the operating system.
   */
  public static final String EOL = System.getProperty("line.separator", "\n");

  /**
   * Retrieves the fully-qualified names of all classes included in the SDK.
   *
   * @return  The fully-qualified names of all classes included in the SDK.
   *
   * @throws  Exception  If a problem occurs during processing.
   */
  @DataProvider(name="sdkClasses")
  public Object[][] getSDKClasses()
      throws Exception
  {
    var baseDirValue = System.getProperty("basedir");
    if (baseDirValue == null)
    {
      throw new NullPointerException("Could not obtain the 'basedir' system"
          + " property. Try running the test via Maven on the command line.");
    }

    File baseDir = new File(baseDirValue);
    File buildDir = new File(baseDir, "target");
    File classesDir = new File(buildDir, "classes");

    ArrayList<Class<?>> classList = new ArrayList<>();
    findClasses("", classesDir,  classList);

    Object[][] classes = new Object[classList.size()][1];
    for (int i=0; i < classes.length; i++)
    {
      classes[i][0] = classList.get(i);
    }

    return classes;
  }



  /**
   * Recursively identifies all classes in the provided directory.
   *
   * @param  p  The package name associated with the provided directory.
   * @param  d  The directory to be processed.
   * @param  l  The list that will contain the classes.
   *
   * @throws  Exception  If a problem occurs during processing.
   */
  private static void findClasses(final String p, final File d,
                                  final ArrayList<Class<?>> l)
          throws Exception
  {
    if (d.listFiles() == null)
    {
      // TestNG skips tests with no output when data providers fail, so print
      // the error message to stderr.
      System.err.println("ERROR: Could not find any Java classes in the '"
          + d + "' folder.");
      fail("No classes found.");
    }

    try
    {
      for (File f : Objects.requireNonNull(d.listFiles()))
      {
        if (f.isDirectory())
        {
          if (p.isEmpty())
          {
            findClasses(f.getName(), f, l);
          }
          else
          {
            findClasses(p + '.' + f.getName(), f, l);
          }
        }
        else if (f.getName().endsWith(".class") &&
                 (! f.getName().contains("$")))
        {
          int dotPos = f.getName().lastIndexOf('.');
          String baseName = f.getName().substring(0, dotPos);
          String className = p + '.' + baseName;

          // Include the class, as well as any subclasses.
          Class<?> baseClass = Class.forName(className);
          l.add(baseClass);
          var classes = Arrays.stream(baseClass.getClasses()).filter(c ->
              c.getCanonicalName().contains("com.unboundid.scim2")).toList();
          l.addAll(classes);
        }
      }
    }
    catch (Exception e)
    {
      // TestNG skips tests with no output when data providers fail, so print
      // the failure to stderr.
      System.err.println("ERROR: Failed to retrieve the Java classes. The"
          + " exception was: " + e);
      throw e;
    }
  }



  /**
   * Ensures that all non-primitive fields, constructor and method parameters,
   * and method return values are marked with either the {@link NotNull} or
   * {@link Nullable} annotation types.
   *
   * @param  c  The class to be examined.
   */
  @Test(dataProvider="sdkClasses")
  public void testNullability(final Class<?> c)
  {
    // If the class is dynamically generated by something outside of the
    // codebase, then it won't be annotated.
    if (c.isSynthetic())
    {
      return;
    }

    // If the class is a dynamically generated messages file, then it won't be
    // annotated.
    if (c.isEnum() && c.getName().endsWith("Messages"))
    {
      try
      {
        c.getDeclaredField("defaultText");
        return;
      }
      catch (final Exception e)
      {
        // Ignore this.
      }
    }


    // If the class is the dynamically generated Version file, then ignore it.
    if (c.equals(Version.class))
    {
      return;
    }


    final List<String> errors = new ArrayList<>();


    // Make sure that all fields are annotated properly.
    for (final Field field : c.getDeclaredFields())
    {
      // If the field is dynamically generated, then it won't be annotated.
      if (field.isSynthetic())
      {
        continue;
      }

      // Ignore enum constants.
      if (field.isEnumConstant())
      {
        continue;
      }

      final Annotation notNullAnnotation = field.getAnnotation(NotNull.class);
      final Annotation nullableAnnotation = field.getAnnotation(Nullable.class);
      if (field.getType().isPrimitive())
      {
        if (notNullAnnotation != null)
        {
          errors.add("Primitive field '" + field.getName() +
               "' is marked @NotNull.");
        }

        if (nullableAnnotation != null)
        {
          errors.add("Primitive field '" + field.getName() +
               "' is marked @Nullable.");
        }
      }
      else
      {
        if (notNullAnnotation != null)
        {
          if (nullableAnnotation != null)
          {
            errors.add("Field '" + field.getName() +
                 "' is marked with both @NotNull and @Nullable.");
          }
        }
        else if (nullableAnnotation == null)
        {
          errors.add("Non-primitive field '" + field.getName() +
               "' is not marked with either @NotNull or @Nullable.");
        }
      }
    }


    // Make sure that all constructor parameters are annotated properly.
    // Note that enums can have dynamically generated constructors and there
    // doesn't seem to be a good way to detect them, so we'll just skip this
    // validation entirely for enums.
    if (! c.isEnum())
    {
      for (final Constructor<?> constructor : c.getDeclaredConstructors())
      {
        // If the constructor is dynamically generated, then it won't be
        // annotated.
        if (constructor.isSynthetic())
        {
          continue;
        }

        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Annotation[][] parameterAnnotations =
             constructor.getParameterAnnotations();
        if (parameterTypes.length != parameterAnnotations.length)
        {
          // This can happen in some cases with enums, anonymous classes, and
          // local classes.  In that case, we can't check it.
          continue;
        }

        for (int i=0; i < parameterTypes.length; i++)
        {
          boolean isNotNull = false;
          boolean isNullable = false;
          for (final Annotation a : parameterAnnotations[i])
          {
            if (a.annotationType().equals(NotNull.class))
            {
              isNotNull = true;
            }
            else if (a.annotationType().equals(Nullable.class))
            {
              isNullable = true;
            }
          }

          if (parameterTypes[i].isPrimitive())
          {
            if (isNotNull)
            {
              errors.add("Constructor " + constructor + " parameter " + i +
                   " is primitive but is marked @NotNull.");
            }

            if (isNullable)
            {
              errors.add("Constructor " + constructor + " parameter " + i +
                   " is primitive but is marked @Nullable.");
            }
          }
          else
          {
            if (isNotNull)
            {
              if (isNullable)
              {
                errors.add("Constructor " + constructor + " parameter " + i +
                     " is marked both @NotNull and @Nullable.");
              }
            }
            else if (! isNullable)
            {
              // Enums can have a default constructor that takes two
              // parameters (String and int).  Since those constructors don't
              // exist in the codebase, they won't be annotated.
              if (c.isEnum() && (parameterTypes.length == 2) &&
                   (parameterTypes[0].equals(String.class) &&
                        parameterTypes[1].equals(Integer.TYPE)))
              {
                continue;
              }

              errors.add("Constructor " + constructor + " parameter " + i +
                   " is not primitive but is not marked @NotNull or " +
                   "@Nullable.");
            }
          }
        }
      }
    }


    // Make sure that all method return values and parameters are annotated
    // properly.
    for (final Method method : c.getDeclaredMethods())
    {
      // If the class is an enum, then we'll skip the valueOf(String) and
      // values() methods that are generated by the compiler.
      if (c.isEnum())
      {
        if (method.getName().equals("valueOf") &&
             (method.getParameterTypes().length == 1) &&
             (method.getParameterTypes()[0].equals(String.class)))
        {
          continue;
        }

        if (method.getName().equals("values") &&
             (method.getParameterTypes().length == 0))
        {
          continue;
        }
      }


      // If the method is dynamically generated, then it won't be
      // annotated.
      if (method.isSynthetic())
      {
        continue;
      }


      // Check the method return type.
      final Class<?> returnType = method.getReturnType();
      final Annotation notNullReturnTypeAnnotation =
           method.getAnnotation(NotNull.class);
      final Annotation nullableReturnTypeAnnotation =
           method.getAnnotation(Nullable.class);
      if (returnType.equals(Void.TYPE))
      {
        if (notNullReturnTypeAnnotation != null)
        {
          errors.add("Method " + method + " has a void return type but is " +
               "marked @NotNull.");
        }

        if (nullableReturnTypeAnnotation != null)
        {
          errors.add("Method " + method + " has a void return type but is " +
               "marked @Nullable.");
        }
      }
      else if (returnType.isPrimitive())
      {
        if (notNullReturnTypeAnnotation != null)
        {
          errors.add("Method " + method + " has a primitive return type but " +
               "is marked @NotNull.");
        }

        if (nullableReturnTypeAnnotation != null)
        {
          errors.add("Method " + method + " has a primitive return type but " +
               "is marked @Nullable.");
        }
      }
      else if (notNullReturnTypeAnnotation == null)
      {
        if (nullableReturnTypeAnnotation == null)
        {
          errors.add("Method " + method + " has a non-primitive return " +
               "type but is not marked @NotNull or @Nullable.");
        }
      }
      else if (nullableReturnTypeAnnotation != null)
      {
        errors.add("Method " + method + " is declared both @NotNull and " +
             "@Nullable.");
      }



      // Check the method parameters.
      final Class<?>[] parameterTypes = method.getParameterTypes();
      final Annotation[][] parameterAnnotations =
           method.getParameterAnnotations();
      if (parameterTypes.length != parameterAnnotations.length)
      {
        // This can happen in some cases with enums, anonymous classes, and
        // local classes.  In that case, we can't check it.
        continue;
      }

      for (int i=0; i < parameterTypes.length; i++)
      {
        boolean isNotNull = false;
        boolean isNullable = false;
        for (final Annotation a : parameterAnnotations[i])
        {
          if (a.annotationType().equals(NotNull.class))
          {
            isNotNull = true;
          }
          else if (a.annotationType().equals(Nullable.class))
          {
            isNullable = true;
          }
        }

        if (parameterTypes[i].isPrimitive())
        {
          if (isNotNull)
          {
            errors.add("Method " + method + " parameter " + i +
                 " is primitive but is marked @NotNull.");
          }

          if (isNullable)
          {
            errors.add("Method " + method + " parameter " + i +
                 " is primitive but is marked @Nullable.");
          }
        }
        else
        {
          if (isNotNull)
          {
            if (isNullable)
            {
              errors.add("Method " + method + " parameter " + i +
                   " is marked both @NotNull and @Nullable.");
            }
          }
          else if (! isNullable)
          {
            // Enums can have a default constructor that takes two
            // parameters (String and int).  Since those constructors don't
            // exist in the codebase, they won't be annotated.
            if (c.isEnum() && (parameterTypes.length == 2) &&
                 (parameterTypes[0].equals(String.class) &&
                      parameterTypes[1].equals(Integer.TYPE)))
            {
              continue;
            }

            errors.add("Method " + method + " parameter " + i +
                 " is not primitive but is not marked @NotNull or " +
                 "@Nullable.");
          }
        }
      }
    }


    if (! errors.isEmpty())
    {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Found ");
      stringBuilder.append(errors.size());
      stringBuilder.append(" nullability error");
      if (errors.size() > 1)
      {
        stringBuilder.append("s");
      }
      stringBuilder.append(" in the ");
      stringBuilder.append(c.getName());
      stringBuilder.append(" class:");
      stringBuilder.append(EOL);

      for (String error: errors)
      {
        stringBuilder.append(error);
        stringBuilder.append(EOL);
      }

      stringBuilder.append(EOL);
      stringBuilder.append(EOL);

      String error = stringBuilder.toString();
      fail(error);
    }
  }
}
