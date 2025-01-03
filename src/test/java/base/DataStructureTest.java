package base;

import analysis.CallGraph;
import analysis.Edge;
import java.util.Collections;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import sootup.core.signatures.MethodSignature;
import sootup.core.signatures.PackageName;
import sootup.core.types.ClassType;
import sootup.java.core.types.JavaClassType;

public class DataStructureTest {
  @Test
  public void edgeEquality() {

    ClassType testClass = new JavaClassType("TestClass", new PackageName("some.package"));
    MethodSignature source =
        new MethodSignature(
            testClass,
            "sourceMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());
    MethodSignature target =
        new MethodSignature(
            testClass,
            "targetMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());

    Assert.assertNotEquals(source, target);

    Edge<MethodSignature, MethodSignature> edge = new Edge<>(source, target);

    Assert.assertEquals(
        "(" + edge.source.toString() + " --> " + edge.target.toString() + ")", edge.toString());

    Edge<MethodSignature, MethodSignature> sameEdge = new Edge<>(source, target);
    Edge<MethodSignature, MethodSignature> anotherEdge = new Edge<>(target, source);

    Assert.assertEquals(sameEdge, edge);
    Assert.assertEquals(sameEdge.hashCode(), edge.hashCode());

    Assert.assertNotEquals(anotherEdge, edge);
    Assert.assertNotEquals(anotherEdge.hashCode(), edge.hashCode());
  }

  @Rule public ExpectedException thrown = ExpectedException.none();

  @Test
  public void callGraphDoubleNode() {

    ClassType testClass = new JavaClassType("TestClass", new PackageName("some.package"));
    MethodSignature source =
        new MethodSignature(
            testClass,
            "sourceMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());

    CallGraph testCG = new CallGraph("Test");
    Assert.assertEquals("Test", testCG.algorithm);

    thrown.expect(IllegalArgumentException.class);

    testCG.addNode(source);
    testCG.addNode(source);
  }

  @Test
  public void callGraphDoubleEdge() {
    ClassType testClass = new JavaClassType("TestClass", new PackageName("some.package"));
    MethodSignature source =
        new MethodSignature(
            testClass,
            "sourceMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());
    MethodSignature target =
        new MethodSignature(
            testClass,
            "targetMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());

    CallGraph testCG = new CallGraph("Test");
    Assert.assertEquals("Test", testCG.algorithm);

    thrown.expect(IllegalArgumentException.class);

    testCG.addNode(source);
    testCG.addNode(target);
    testCG.addEdge(source, target);
    testCG.addEdge(source, target);
  }

  @Test
  public void callGraphUnknownNode() {
    ClassType testClass = new JavaClassType("TestClass", new PackageName("some.package"));
    MethodSignature source =
        new MethodSignature(
            testClass,
            "sourceMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());
    MethodSignature target =
        new MethodSignature(
            testClass,
            "targetMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());

    ClassType anotherClass = new JavaClassType("AnotherClass", new PackageName("some.package"));
    MethodSignature firstMethod =
        new MethodSignature(
            anotherClass,
            "firstMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());
    MethodSignature secondMethod =
        new MethodSignature(
            anotherClass,
            "secondMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());

    CallGraph testCG = new CallGraph("Test");
    Assert.assertEquals("Test", testCG.algorithm);

    thrown.expect(IllegalArgumentException.class);

    testCG.addNode(source);
    testCG.addNode(target);
    testCG.addEdge(source, target);
    testCG.addEdge(source, firstMethod);
  }

  @Test
  public void callGraphValidScenario() {
    ClassType testClass = new JavaClassType("TestClass", new PackageName("some.package"));
    MethodSignature source =
        new MethodSignature(
            testClass,
            "sourceMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());
    MethodSignature target =
        new MethodSignature(
            testClass,
            "targetMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());

    ClassType anotherClass =
        new JavaClassType("AnotherClass", new PackageName("some.other.package"));
    MethodSignature firstMethod =
        new MethodSignature(
            anotherClass,
            "firstMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());
    MethodSignature secondMethod =
        new MethodSignature(
            anotherClass,
            "secondMethod",
            Collections.emptyList(),
            sootup.core.types.PrimitiveType.getBoolean());

    CallGraph testCG = new CallGraph("Test");
    Assert.assertEquals("Test", testCG.algorithm);

    testCG.addNode(source);
    testCG.addNode(target);
    testCG.addNode(firstMethod);
    testCG.addNode(secondMethod);
    testCG.addEdge(source, target);
    testCG.addEdge(source, firstMethod);
    testCG.addEdge(target, firstMethod);
    testCG.addEdge(firstMethod, secondMethod);

    Assert.assertTrue(testCG.hasNode(source));
    Assert.assertTrue(testCG.hasNode(target));
    Assert.assertTrue(testCG.hasNode(firstMethod));
    Assert.assertTrue(testCG.hasNode(secondMethod));

    Assert.assertTrue(testCG.hasEdge(source, target));
    Assert.assertTrue(testCG.hasEdge(source, firstMethod));
    Assert.assertTrue(testCG.hasEdge(target, firstMethod));
    Assert.assertTrue(testCG.hasEdge(firstMethod, secondMethod));

    Set<MethodSignature> eIntoSource = testCG.edgesInto(source);
    Set<MethodSignature> eIntoTarget = testCG.edgesInto(target);
    Set<MethodSignature> eIntoFirst = testCG.edgesInto(firstMethod);
    Set<MethodSignature> eIntoSecond = testCG.edgesInto(secondMethod);

    Set<MethodSignature> eOutOfSource = testCG.edgesOutOf(source);
    Set<MethodSignature> eOutOfTarget = testCG.edgesOutOf(target);
    Set<MethodSignature> eOutOfFirst = testCG.edgesOutOf(firstMethod);
    Set<MethodSignature> eOutOfSecond = testCG.edgesOutOf(secondMethod);

    Assert.assertEquals(0, eIntoSource.size());
    Assert.assertEquals(2, eOutOfSource.size());
    Assert.assertEquals(1, eIntoTarget.size());
    Assert.assertEquals(1, eOutOfTarget.size());
    Assert.assertEquals(2, eIntoFirst.size());
    Assert.assertEquals(1, eOutOfFirst.size());
    Assert.assertEquals(1, eIntoSecond.size());
    Assert.assertEquals(0, eOutOfSecond.size());

    Assert.assertTrue(eOutOfSource.contains(target));
    Assert.assertTrue(eOutOfSource.contains(firstMethod));
    Assert.assertArrayEquals(new MethodSignature[] {source}, eIntoTarget.toArray());
    Assert.assertArrayEquals(new MethodSignature[] {firstMethod}, eOutOfTarget.toArray());
    Assert.assertTrue(eIntoFirst.contains(target));
    Assert.assertTrue(eIntoFirst.contains(source));
    Assert.assertArrayEquals(new MethodSignature[] {secondMethod}, eOutOfFirst.toArray());
    Assert.assertArrayEquals(new MethodSignature[] {firstMethod}, eIntoSecond.toArray());
  }
}
