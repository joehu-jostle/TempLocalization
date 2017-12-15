package org.gwtproject.tutorial;

import org.gwtproject.tutorial.client.TodoListTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TodoListSuite extends GWTTestSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite("Tests for TodoList");
    suite.addTestSuite(TodoListTest.class);
    return suite;
  }
}
