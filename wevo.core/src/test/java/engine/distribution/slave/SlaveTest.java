/*
 * Wevo2 - Distributed Evolutionary Computation Library.
 * Copyright (C) 2009 Marcin Brodziak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *    Boston, MA  02110-1301  USA
 */
package engine.distribution.slave;

import java.io.IOException;
import java.util.Arrays;

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.testng.annotations.Test;

import samples.objectivefunctions.EuclideanTSP;
import engine.CachedObjectiveFunction;
import engine.Population;
import engine.PopulationEvaluator;
import engine.SingleThreadedEvaluator;
import engine.PopulationEvaluatorTest.DummyIndividual;
import engine.distribution.serialization.EvaluationResult;
import engine.distribution.serialization.EvaluationTask;
import engine.individuals.Permutation;
import engine.utils.SystemClock;

/**
 * Tests for {@link Slave}.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
@Test(sequential = true)
public class SlaveTest {

  /** Tested instance. */
  private Slave<DummyIndividual> slave;

  /** Population evaluator mock. */
  private PopulationEvaluator<DummyIndividual> evaluatorMock;

  /** Server connector mock. */
  private TaskExchanger<DummyIndividual> exchangerMock;

  /** Slave registrator mock. */
  private SlaveRegistrator registratorMock;

  /** Mock control. */
  private IMocksControl mockControl = EasyMock.createControl();

  /**
   * Tests whether evaluation works when fatal errors occur.
   * @throws IOException Never thrown.
   * @throws ClassNotFoundException Thrown when a class definition for object
   * sent by master is missing.
   */
  @Test(expectedExceptions = { ClassNotFoundException.class })
  public void evaluationWithFatalExceptionTest()
      throws IOException, ClassNotFoundException {
    setUp();

    EasyMock.expect(exchangerMock.getTask("", 0))
        .andThrow(new ClassNotFoundException());

    slave.setSlaveId("");
    mockControl.replay();
    // In this test we expect that
    // no single run is executed.
    slave.run(0, -1);
    mockControl.verify();
  }

  /**
   * Tests whether registration works when no exceptional situations occur.
   * @throws IOException Never thrown.
   * @throws ClassNotFoundException Never thrown.
   */
  @Test
  public void registrationTest() throws IOException, ClassNotFoundException {
    final int runs = 10;

    setUp();

    EasyMock.expect(registratorMock.register("slave"))
        .andReturn("")
        .times(1);

    mockControl.replay();
    slave.register(runs, 0, -1);
    mockControl.verify();
  }

  /**
   * Tests whether registration works when error situations related
   * to connection occur. All connection trials fail.
   * @throws IOException Never thrown.
   * @throws ClassNotFoundException Never thrown.
   */
  @Test(expectedExceptions = { IllegalStateException.class })
  public void registrationWithIOExceptionTest()
      throws IOException, ClassNotFoundException {
    final int runs = 10;

    setUp();

    EasyMock.expect(registratorMock.register("slave"))
        .andThrow(new IOException())
        .times(runs);

    mockControl.replay();
    slave.register(runs, 0, -1);
    mockControl.verify();
  }

  /**
   * Tests whether registration works when few error situations related
   * to connection occurs. After that connection is established. 
   * @throws IOException Never thrown.
   * @throws ClassNotFoundException Never thrown.
   */
  @Test
  public void registrationWithFewIOExceptionsTest()
      throws IOException, ClassNotFoundException {
    final int runs  = 10;

    setUp();

    // MagicNumber off
    EasyMock.expect(registratorMock.register("slave"))
        .andThrow(new IOException())
        .times(5);
    // MagicNumber on
    EasyMock.expect(registratorMock.register("slave"))
        .andReturn("")
        .times(1);

    mockControl.replay();
    slave.register(runs, 0, -1);
    mockControl.verify();
  }

  /** Creates mock objects for tests, without behavior. */
  @SuppressWarnings("unchecked")
  public void setUp() {
    mockControl.reset();
    evaluatorMock = mockControl.createMock(PopulationEvaluator.class);
    exchangerMock = mockControl.createMock(TaskExchanger.class);
    registratorMock = mockControl.createMock(SlaveRegistrator.class);

    slave = new Slave<DummyIndividual>("",
        evaluatorMock, exchangerMock, registratorMock,
        "slave", new SystemClock());
  }

  /**
   * Tests whether caching fails while executing this scenario.
   * Test introduced because of issue 3
   * (http://code.google.com/p/wevo2/issues/detail?id=3).
   * TODO(karol.stosiek): Consider removing it after fix verification.
   * @throws ClassNotFoundException Never thrown.
   * @throws IOException Never thrown.
   */
  @SuppressWarnings("unchecked")
  @Test(expectedExceptions = { IllegalStateException.class })
  public void testCacheFailure() throws ClassNotFoundException, IOException {
    // LineLength off
    // MagicNumber off
    Population<Permutation> pop1 = new Population<Permutation>(Arrays.asList(
        new Permutation(new int[]{80, 25, 21, 58, 66, 27, 96, 32, 98, 41, 64, 57, 15, 56, 30, 49, 12, 88, 20, 31, 7, 94, 9, 18, 92, 23, 60, 81, 22, 69, 52, 2, 89, 5, 24, 29, 11, 37, 51, 72, 47, 44, 73, 43, 1, 78, 17, 93, 85, 19, 97, 99, 95, 38, 45, 63, 61, 71, 90, 79, 75, 53, 34, 65, 77, 36, 14, 70, 74, 4, 10, 46, 28, 84, 82, 39, 48, 40, 68, 91, 87, 3, 13, 0, 59, 35, 42, 6, 83, 33, 86, 67, 55, 50, 54, 8, 62, 76, 16, 26}),
        new Permutation(new int[]{94, 22, 18, 16, 80, 98, 31, 3, 61, 95, 84, 48, 67, 17, 6, 0, 96, 71, 52, 62, 47, 26, 97, 81, 40, 86, 27, 91, 69, 76, 88, 53, 11, 58, 92, 72, 8, 24, 46, 77, 30, 51, 39, 12, 5, 33, 75, 35, 9, 41, 68, 73, 7, 21, 23, 66, 38, 37, 65, 60, 63, 19, 29, 83, 64, 34, 20, 74, 87, 99, 78, 79, 93, 44, 4, 55, 15, 25, 50, 89, 49, 43, 32, 54, 82, 28, 13, 10, 85, 45, 56, 36, 70, 14, 57, 1, 42, 2, 59, 90}),
        new Permutation(new int[]{81, 19, 62, 3, 67, 83, 5, 54, 36, 98, 97, 63, 0, 58, 14, 18, 69, 91, 42, 86, 29, 99, 93, 8, 55, 79, 77, 45, 89, 75, 35, 60, 24, 17, 39, 25, 66, 30, 92, 76, 12, 47, 74, 73, 41, 10, 22, 70, 6, 26, 34, 48, 95, 87, 59, 46, 40, 15, 49, 33, 56, 1, 11, 21, 78, 31, 64, 38, 72, 27, 84, 37, 28, 85, 57, 7, 32, 51, 16, 88, 68, 44, 2, 9, 53, 50, 96, 52, 23, 43, 13, 71, 80, 65, 82, 90, 4, 94, 20, 61}),
        new Permutation(new int[]{56, 0, 17, 78, 96, 23, 86, 91, 98, 68, 36, 54, 33, 26, 27, 46, 85, 58, 28, 83, 21, 13, 18, 10, 42, 77, 37, 88, 79, 2, 43, 82, 53, 76, 62, 32, 22, 90, 5, 65, 24, 59, 80, 38, 93, 52, 50, 1, 95, 4, 57, 44, 14, 60, 69, 94, 89, 40, 16, 30, 84, 67, 48, 25, 47, 72, 41, 81, 51, 66, 29, 64, 8, 3, 87, 63, 20, 45, 55, 6, 74, 11, 75, 70, 31, 12, 99, 19, 9, 15, 7, 49, 97, 39, 73, 61, 92, 35, 34, 71}),
        new Permutation(new int[]{60, 92, 89, 10, 42, 20, 51, 35, 19, 65, 70, 58, 4, 2, 79, 73, 7, 88, 25, 93, 23, 69, 82, 86, 71, 76, 28, 6, 22, 94, 90, 13, 62, 63, 30, 5, 41, 18, 12, 85, 87, 74, 48, 97, 84, 3, 31, 45, 61, 1, 11, 64, 15, 27, 46, 66, 8, 24, 77, 72, 40, 44, 47, 36, 81, 52, 57, 68, 98, 80, 91, 43, 50, 78, 49, 29, 37, 54, 56, 26, 9, 53, 55, 39, 83, 67, 96, 21, 59, 95, 17, 32, 38, 16, 34, 99, 0, 75, 33, 14}),
        new Permutation(new int[]{4, 21, 82, 36, 34, 15, 50, 39, 54, 49, 91, 92, 0, 97, 42, 56, 8, 41, 35, 53, 2, 65, 18, 23, 46, 17, 10, 75, 70, 45, 24, 32, 43, 27, 14, 88, 13, 77, 66, 22, 61, 94, 52, 7, 6, 57, 86, 1, 89, 78, 16, 72, 71, 90, 68, 79, 40, 99, 64, 59, 37, 19, 38, 28, 63, 20, 67, 55, 5, 26, 93, 51, 74, 60, 80, 69, 47, 95, 96, 9, 33, 25, 30, 62, 87, 81, 83, 84, 3, 73, 48, 85, 12, 31, 29, 11, 58, 44, 76, 98}),
        new Permutation(new int[]{89, 36, 0, 12, 87, 69, 67, 91, 57, 19, 95, 63, 61, 90, 44, 62, 1, 10, 65, 58, 42, 28, 56, 64, 93, 48, 35, 76, 85, 78, 29, 70, 13, 39, 21, 30, 92, 7, 46, 32, 4, 55, 59, 37, 26, 45, 23, 83, 11, 86, 72, 52, 82, 20, 22, 94, 25, 2, 80, 98, 40, 88, 6, 51, 31, 66, 75, 18, 84, 17, 8, 49, 9, 77, 3, 79, 5, 99, 54, 60, 47, 97, 74, 33, 43, 71, 24, 53, 34, 27, 73, 50, 68, 41, 96, 81, 14, 16, 38, 15}),
        new Permutation(new int[]{11, 60, 22, 49, 95, 5, 32, 13, 93, 62, 70, 64, 48, 57, 9, 69, 90, 6, 97, 50, 10, 24, 76, 55, 85, 23, 54, 27, 91, 19, 29, 31, 84, 15, 98, 28, 67, 52, 79, 94, 87, 42, 75, 7, 51, 58, 34, 46, 81, 39, 16, 71, 17, 4, 86, 92, 56, 89, 77, 82, 36, 8, 37, 43, 72, 66, 25, 44, 63, 96, 21, 38, 45, 3, 80, 12, 73, 41, 88, 20, 14, 33, 65, 35, 53, 59, 26, 0, 78, 30, 68, 74, 47, 40, 2, 83, 61, 18, 1, 99}),
        new Permutation(new int[]{37, 81, 3, 33, 60, 55, 10, 93, 99, 8, 95, 73, 70, 62, 63, 74, 54, 7, 0, 5, 75, 46, 18, 64, 59, 65, 92, 61, 66, 9, 41, 32, 31, 80, 51, 78, 22, 43, 30, 29, 42, 4, 34, 25, 14, 88, 68, 58, 69, 87, 91, 23, 96, 11, 72, 47, 6, 19, 21, 49, 97, 50, 28, 85, 39, 98, 1, 16, 20, 40, 77, 83, 90, 52, 27, 12, 44, 94, 17, 36, 2, 56, 45, 15, 48, 79, 13, 35, 84, 24, 38, 71, 57, 82, 86, 76, 67, 89, 53, 26}),
        new Permutation(new int[]{42, 86, 85, 45, 74, 48, 24, 0, 92, 84, 98, 8, 51, 87, 71, 59, 83, 32, 2, 64, 21, 20, 58, 41, 27, 38, 54, 67, 37, 10, 82, 15, 17, 76, 1, 60, 62, 99, 75, 35, 3, 30, 80, 50, 72, 31, 97, 40, 18, 47, 5, 61, 16, 56, 25, 81, 7, 49, 88, 95, 44, 69, 28, 78, 6, 77, 26, 52, 89, 34, 96, 39, 94, 23, 53, 79, 43, 63, 19, 12, 14, 11, 13, 68, 65, 4, 93, 55, 9, 29, 70, 22, 57, 33, 91, 36, 73, 90, 66, 46}),
        new Permutation(new int[]{69, 54, 29, 99, 38, 39, 49, 55, 94, 25, 79, 68, 72, 1, 86, 88, 95, 75, 35, 11, 67, 87, 18, 0, 70, 98, 19, 10, 37, 5, 50, 76, 45, 82, 47, 6, 81, 59, 65, 4, 28, 36, 22, 52, 20, 61, 2, 77, 78, 12, 31, 73, 62, 60, 56, 85, 51, 89, 84, 97, 44, 63, 90, 21, 74, 3, 9, 26, 16, 23, 80, 57, 43, 91, 8, 64, 32, 24, 46, 48, 42, 53, 7, 41, 30, 96, 93, 33, 40, 34, 27, 13, 83, 58, 17, 71, 66, 14, 92, 15}),
        new Permutation(new int[]{70, 82, 20, 30, 27, 48, 19, 53, 42, 26, 0, 31, 72, 2, 32, 8, 85, 99, 65, 91, 16, 97, 4, 93, 62, 80, 35, 1, 79, 86, 3, 43, 75, 9, 40, 51, 74, 13, 89, 67, 24, 58, 69, 18, 60, 83, 81, 55, 12, 52, 57, 54, 34, 94, 71, 96, 38, 15, 73, 14, 22, 7, 90, 41, 63, 25, 46, 50, 23, 78, 11, 45, 88, 6, 76, 77, 87, 49, 5, 64, 68, 44, 84, 28, 56, 61, 47, 37, 59, 29, 36, 21, 92, 10, 17, 33, 98, 66, 95, 39}),
        new Permutation(new int[]{87, 84, 31, 19, 71, 3, 35, 67, 22, 56, 28, 98, 99, 25, 83, 57, 11, 81, 12, 89, 14, 91, 47, 4, 73, 63, 10, 72, 75, 77, 85, 13, 16, 48, 66, 33, 50, 18, 93, 54, 40, 37, 2, 41, 8, 0, 78, 39, 64, 24, 60, 45, 74, 90, 55, 97, 6, 9, 27, 32, 29, 1, 7, 58, 69, 20, 79, 21, 86, 96, 43, 92, 53, 82, 30, 95, 42, 62, 52, 38, 36, 51, 59, 94, 26, 17, 61, 49, 76, 15, 80, 23, 34, 88, 5, 65, 70, 68, 44, 46}),
        new Permutation(new int[]{67, 71, 57, 89, 68, 51, 5, 55, 19, 8, 61, 45, 95, 54, 56, 87, 37, 24, 84, 49, 47, 26, 58, 93, 97, 99, 82, 94, 7, 3, 11, 17, 28, 48, 98, 74, 12, 59, 36, 60, 42, 79, 1, 6, 65, 77, 50, 33, 14, 91, 81, 75, 21, 62, 2, 78, 53, 34, 69, 0, 30, 44, 16, 4, 80, 13, 39, 29, 52, 23, 92, 35, 85, 66, 63, 73, 88, 9, 38, 70, 15, 41, 72, 83, 31, 76, 86, 43, 64, 90, 20, 10, 40, 25, 22, 46, 18, 27, 32, 96}),
        new Permutation(new int[]{82, 60, 71, 81, 26, 9, 87, 94, 99, 16, 41, 39, 38, 37, 5, 46, 45, 18, 49, 68, 74, 86, 70, 52, 97, 33, 57, 20, 44, 69, 78, 10, 35, 55, 2, 28, 93, 36, 54, 96, 1, 48, 31, 4, 85, 47, 50, 6, 95, 92, 32, 3, 17, 21, 12, 64, 61, 42, 51, 63, 67, 11, 19, 89, 91, 40, 53, 15, 29, 98, 43, 90, 56, 62, 84, 58, 88, 79, 34, 73, 27, 76, 14, 7, 30, 72, 25, 83, 23, 8, 0, 13, 75, 66, 24, 65, 77, 22, 59, 80}),
        new Permutation(new int[]{35, 88, 16, 72, 7, 12, 36, 15, 85, 5, 82, 98, 83, 3, 47, 67, 74, 66, 42, 64, 44, 59, 96, 92, 17, 79, 81, 65, 31, 26, 8, 51, 71, 28, 76, 50, 22, 90, 25, 37, 61, 30, 45, 77, 11, 80, 20, 33, 69, 75, 19, 70, 94, 58, 24, 99, 68, 53, 10, 40, 23, 18, 60, 6, 21, 63, 73, 4, 86, 43, 54, 56, 13, 2, 84, 32, 89, 87, 57, 55, 62, 39, 38, 29, 48, 52, 9, 41, 93, 49, 91, 95, 78, 27, 14, 46, 97, 1, 0, 34}),
        new Permutation(new int[]{11, 80, 81, 75, 3, 64, 17, 78, 42, 9, 2, 97, 74, 86, 47, 71, 98, 1, 10, 77, 32, 88, 43, 94, 46, 25, 99, 13, 60, 14, 51, 76, 82, 19, 93, 79, 33, 57, 89, 30, 72, 50, 49, 26, 35, 90, 22, 20, 41, 70, 38, 21, 23, 27, 63, 83, 0, 52, 87, 95, 56, 66, 16, 24, 69, 67, 92, 84, 40, 68, 34, 61, 8, 55, 53, 37, 15, 39, 45, 29, 59, 12, 62, 4, 54, 58, 36, 65, 28, 48, 6, 73, 96, 85, 31, 7, 18, 5, 91, 44}),
        new Permutation(new int[]{42, 24, 46, 43, 81, 54, 23, 91, 94, 9, 50, 16, 89, 49, 22, 73, 53, 97, 59, 29, 21, 70, 36, 27, 87, 68, 28, 75, 78, 52, 47, 25, 96, 74, 72, 31, 76, 93, 5, 17, 58, 12, 44, 30, 2, 32, 48, 71, 62, 18, 19, 8, 7, 40, 3, 39, 84, 88, 10, 99, 80, 92, 34, 85, 57, 95, 1, 4, 35, 65, 86, 33, 60, 61, 55, 66, 67, 0, 51, 82, 15, 14, 63, 6, 26, 41, 79, 11, 83, 13, 90, 20, 45, 69, 98, 37, 64, 38, 56, 77}),
        new Permutation(new int[]{4, 10, 7, 12, 60, 92, 50, 76, 6, 93, 99, 2, 39, 51, 18, 79, 85, 53, 55, 17, 1, 41, 97, 37, 74, 32, 61, 27, 91, 25, 90, 24, 52, 95, 78, 31, 38, 46, 82, 83, 62, 48, 20, 72, 65, 36, 26, 0, 34, 22, 15, 80, 28, 87, 16, 9, 13, 94, 68, 66, 58, 3, 23, 81, 40, 70, 11, 14, 57, 8, 43, 96, 19, 88, 89, 63, 54, 33, 84, 75, 21, 71, 98, 73, 77, 59, 29, 45, 5, 35, 30, 47, 49, 69, 86, 67, 44, 56, 64, 42}),
        new Permutation(new int[]{73, 92, 30, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 60, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 71, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{42, 1, 70, 11, 79, 40, 47, 7, 30, 88, 92, 76, 84, 10, 83, 34, 38, 29, 36, 90, 54, 98, 87, 16, 23, 46, 12, 49, 14, 37, 59, 4, 69, 45, 41, 31, 18, 6, 73, 33, 95, 51, 65, 85, 77, 74, 17, 93, 13, 71, 66, 5, 24, 57, 55, 67, 8, 64, 9, 78, 86, 97, 58, 72, 22, 62, 15, 3, 94, 81, 2, 26, 48, 19, 96, 35, 91, 75, 20, 99, 60, 0, 52, 89, 32, 82, 68, 39, 56, 61, 80, 50, 44, 28, 63, 43, 27, 53, 25, 21}),
        new Permutation(new int[]{65, 93, 28, 33, 42, 51, 77, 75, 95, 5, 40, 24, 82, 58, 72, 7, 55, 54, 80, 49, 36, 97, 6, 59, 1, 27, 79, 47, 91, 22, 23, 44, 34, 86, 84, 26, 74, 69, 96, 31, 15, 3, 20, 63, 8, 73, 35, 46, 45, 39, 43, 92, 85, 90, 62, 71, 14, 18, 21, 89, 11, 9, 70, 67, 32, 4, 29, 50, 56, 98, 61, 10, 0, 41, 53, 25, 81, 13, 78, 57, 99, 64, 16, 68, 2, 52, 88, 87, 12, 37, 76, 60, 83, 19, 66, 48, 30, 17, 94, 38}),
        new Permutation(new int[]{88, 75, 33, 24, 40, 25, 66, 31, 59, 60, 65, 50, 38, 13, 74, 2, 70, 98, 73, 76, 43, 95, 16, 10, 45, 86, 99, 87, 22, 54, 82, 49, 79, 67, 17, 91, 21, 92, 81, 85, 46, 72, 28, 89, 20, 3, 42, 51, 8, 12, 34, 0, 96, 41, 36, 5, 55, 44, 64, 56, 90, 52, 39, 4, 83, 80, 23, 18, 61, 6, 97, 32, 57, 78, 1, 47, 37, 19, 30, 62, 7, 53, 48, 11, 9, 71, 58, 69, 15, 27, 84, 93, 94, 68, 14, 63, 35, 77, 26, 29}),
        new Permutation(new int[]{43, 76, 46, 75, 37, 30, 10, 19, 83, 8, 51, 26, 17, 79, 16, 21, 88, 74, 60, 99, 56, 95, 44, 50, 27, 85, 58, 84, 41, 68, 6, 97, 54, 32, 4, 3, 25, 45, 35, 13, 82, 49, 63, 2, 9, 7, 20, 62, 28, 18, 23, 0, 5, 87, 40, 61, 80, 93, 67, 59, 12, 98, 15, 64, 70, 52, 72, 39, 31, 29, 55, 71, 77, 90, 24, 91, 48, 34, 78, 86, 57, 38, 36, 53, 96, 73, 92, 81, 22, 1, 69, 94, 14, 66, 33, 11, 47, 42, 89, 65}),
        new Permutation(new int[]{31, 92, 60, 39, 18, 64, 1, 79, 76, 98, 15, 43, 91, 19, 52, 94, 4, 44, 89, 26, 47, 0, 87, 71, 59, 10, 27, 36, 46, 11, 49, 35, 5, 77, 45, 72, 48, 40, 65, 84, 13, 73, 69, 61, 74, 82, 6, 56, 17, 34, 99, 23, 86, 2, 32, 29, 38, 68, 9, 22, 8, 70, 25, 58, 85, 62, 37, 81, 93, 16, 28, 41, 96, 12, 7, 88, 20, 51, 24, 30, 97, 50, 14, 66, 57, 75, 33, 80, 3, 21, 55, 42, 90, 53, 95, 54, 67, 83, 78, 63})));

    Population<Permutation> pop2 = new Population<Permutation>(Arrays.asList(
        new Permutation(new int[]{37, 92, 30, 33, 60, 55, 10, 93, 99, 8, 95, 73, 70, 62, 63, 74, 54, 7, 0, 5, 75, 46, 18, 64, 59, 65, 81, 61, 66, 9, 41, 32, 31, 80, 51, 78, 22, 43, 3, 29, 42, 4, 34, 25, 14, 88, 68, 58, 69, 87, 91, 23, 96, 11, 72, 47, 6, 19, 21, 49, 97, 50, 28, 85, 39, 98, 1, 16, 20, 40, 77, 83, 90, 52, 27, 12, 44, 94, 17, 36, 2, 56, 45, 15, 48, 79, 13, 35, 84, 24, 38, 71, 57, 82, 86, 76, 67, 89, 53, 26}),
        new Permutation(new int[]{73, 81, 3, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 92, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 60, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 30, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 71, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{87, 10, 7, 19, 71, 3, 35, 67, 22, 56, 28, 98, 99, 25, 83, 57, 11, 81, 12, 89, 14, 91, 47, 4, 73, 63, 84, 72, 75, 77, 85, 13, 16, 48, 66, 33, 50, 18, 93, 54, 40, 37, 2, 41, 8, 0, 78, 39, 64, 24, 60, 45, 74, 90, 55, 97, 6, 9, 27, 32, 29, 1, 31, 58, 69, 20, 79, 21, 86, 96, 43, 92, 53, 82, 30, 95, 42, 62, 52, 38, 36, 51, 59, 94, 26, 17, 61, 49, 76, 15, 80, 23, 34, 88, 5, 65, 70, 68, 44, 46}),
        new Permutation(new int[]{4, 84, 31, 12, 60, 92, 50, 76, 6, 93, 99, 2, 39, 51, 18, 79, 85, 53, 55, 17, 1, 41, 97, 37, 74, 32, 61, 27, 91, 25, 90, 24, 52, 95, 78, 7, 38, 46, 82, 83, 62, 48, 20, 72, 65, 36, 26, 0, 34, 22, 15, 80, 28, 87, 16, 9, 13, 94, 68, 66, 58, 3, 23, 81, 40, 70, 11, 14, 57, 8, 43, 96, 19, 88, 89, 63, 54, 33, 10, 75, 21, 71, 98, 73, 77, 59, 29, 45, 5, 35, 30, 47, 49, 69, 86, 67, 44, 56, 64, 42}),
        new Permutation(new int[]{82, 10, 7, 81, 26, 9, 87, 94, 99, 16, 41, 39, 38, 37, 5, 46, 45, 18, 49, 68, 74, 86, 70, 52, 97, 33, 57, 20, 44, 69, 78, 60, 35, 55, 2, 28, 93, 36, 54, 96, 1, 48, 31, 4, 85, 47, 50, 6, 95, 92, 32, 3, 17, 21, 12, 64, 61, 42, 51, 63, 67, 11, 19, 89, 91, 40, 53, 15, 29, 98, 43, 90, 56, 62, 84, 58, 88, 79, 34, 73, 27, 76, 14, 71, 30, 72, 25, 83, 23, 8, 0, 13, 75, 66, 24, 65, 77, 22, 59, 80}),
        new Permutation(new int[]{4, 60, 71, 12, 10, 92, 50, 76, 6, 93, 99, 2, 39, 51, 18, 79, 85, 53, 55, 17, 1, 41, 97, 37, 74, 32, 61, 27, 91, 25, 90, 24, 52, 95, 78, 31, 38, 46, 82, 83, 62, 48, 20, 72, 65, 36, 26, 0, 34, 22, 15, 80, 28, 87, 16, 9, 13, 94, 68, 66, 58, 3, 23, 81, 40, 70, 11, 14, 57, 8, 43, 96, 19, 88, 89, 63, 54, 33, 84, 75, 21, 7, 98, 73, 77, 59, 29, 45, 5, 35, 30, 47, 49, 69, 86, 67, 44, 56, 64, 42}),
        new Permutation(new int[]{87, 10, 7, 19, 71, 3, 35, 67, 22, 56, 28, 98, 99, 25, 83, 57, 11, 81, 12, 89, 14, 91, 47, 4, 73, 63, 84, 72, 75, 77, 85, 13, 16, 48, 66, 33, 50, 18, 93, 54, 40, 37, 2, 41, 8, 0, 78, 39, 64, 24, 60, 45, 74, 90, 55, 97, 6, 9, 27, 32, 29, 1, 31, 58, 69, 20, 79, 21, 86, 96, 43, 92, 53, 82, 30, 95, 42, 62, 52, 38, 36, 51, 59, 94, 26, 17, 61, 49, 76, 15, 80, 23, 34, 88, 5, 65, 70, 68, 44, 46}),
        new Permutation(new int[]{4, 84, 31, 12, 60, 92, 50, 76, 6, 93, 99, 2, 39, 51, 18, 79, 85, 53, 55, 17, 1, 41, 97, 37, 74, 32, 61, 27, 91, 25, 90, 24, 52, 95, 78, 7, 38, 46, 82, 83, 62, 48, 20, 72, 65, 36, 26, 0, 34, 22, 15, 80, 28, 87, 16, 9, 13, 94, 68, 66, 58, 3, 23, 81, 40, 70, 11, 14, 57, 8, 43, 96, 19, 88, 89, 63, 54, 33, 10, 75, 21, 71, 98, 73, 77, 59, 29, 45, 5, 35, 30, 47, 49, 69, 86, 67, 44, 56, 64, 42}),
        new Permutation(new int[]{89, 84, 31, 12, 87, 69, 67, 91, 57, 19, 95, 63, 61, 90, 44, 62, 1, 10, 65, 58, 42, 28, 56, 64, 93, 48, 35, 76, 85, 78, 29, 70, 13, 39, 21, 30, 92, 7, 46, 32, 4, 55, 59, 37, 26, 45, 23, 83, 11, 86, 72, 52, 82, 20, 22, 94, 25, 2, 80, 98, 40, 88, 6, 51, 0, 66, 75, 18, 36, 17, 8, 49, 9, 77, 3, 79, 5, 99, 54, 60, 47, 97, 74, 33, 43, 71, 24, 53, 34, 27, 73, 50, 68, 41, 96, 81, 14, 16, 38, 15}),
        new Permutation(new int[]{87, 36, 0, 19, 71, 3, 35, 67, 22, 56, 28, 98, 99, 25, 83, 57, 11, 81, 12, 89, 14, 91, 47, 4, 73, 63, 10, 72, 75, 77, 85, 13, 16, 48, 66, 33, 50, 18, 93, 54, 40, 37, 2, 41, 8, 31, 78, 39, 64, 24, 60, 45, 74, 90, 55, 97, 6, 9, 27, 32, 29, 1, 7, 58, 69, 20, 79, 21, 86, 96, 43, 92, 53, 82, 30, 95, 42, 62, 52, 38, 84, 51, 59, 94, 26, 17, 61, 49, 76, 15, 80, 23, 34, 88, 5, 65, 70, 68, 44, 46}),
        new Permutation(new int[]{37, 36, 0, 33, 60, 55, 10, 93, 99, 8, 95, 73, 70, 62, 63, 74, 54, 7, 3, 5, 75, 46, 18, 64, 59, 65, 92, 61, 66, 9, 41, 32, 31, 80, 51, 78, 22, 43, 30, 29, 42, 4, 34, 25, 14, 88, 68, 58, 69, 87, 91, 23, 96, 11, 72, 47, 6, 19, 21, 49, 97, 50, 28, 85, 39, 98, 1, 16, 20, 40, 77, 83, 90, 52, 27, 12, 44, 94, 17, 81, 2, 56, 45, 15, 48, 79, 13, 35, 84, 24, 38, 71, 57, 82, 86, 76, 67, 89, 53, 26}),
        new Permutation(new int[]{89, 81, 3, 12, 87, 69, 67, 91, 57, 19, 95, 63, 61, 90, 44, 62, 1, 10, 65, 58, 42, 28, 56, 64, 93, 48, 35, 76, 85, 78, 29, 70, 13, 39, 21, 30, 92, 7, 46, 32, 4, 55, 59, 37, 26, 45, 23, 83, 11, 86, 72, 52, 82, 20, 22, 94, 25, 2, 80, 98, 40, 88, 6, 51, 31, 66, 75, 18, 84, 17, 8, 49, 9, 77, 0, 79, 5, 99, 54, 60, 47, 97, 74, 33, 43, 71, 24, 53, 34, 27, 73, 50, 68, 41, 96, 36, 14, 16, 38, 15}),
        new Permutation(new int[]{73, 10, 7, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 92, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 60, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 30, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 71, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{4, 92, 30, 12, 60, 10, 50, 76, 6, 93, 99, 2, 39, 51, 18, 79, 85, 53, 55, 17, 1, 41, 97, 37, 74, 32, 61, 27, 91, 25, 90, 24, 52, 95, 78, 31, 38, 46, 82, 83, 62, 48, 20, 72, 65, 36, 26, 0, 34, 22, 15, 80, 28, 87, 16, 9, 13, 94, 68, 66, 58, 3, 23, 81, 40, 70, 11, 14, 57, 8, 43, 96, 19, 88, 89, 63, 54, 33, 84, 75, 21, 71, 98, 73, 77, 59, 29, 45, 5, 35, 7, 47, 49, 69, 86, 67, 44, 56, 64, 42}),
        new Permutation(new int[]{43, 84, 31, 75, 37, 30, 10, 19, 83, 8, 51, 26, 17, 79, 16, 21, 88, 74, 60, 99, 56, 95, 44, 50, 27, 85, 58, 76, 41, 68, 6, 97, 54, 32, 4, 3, 25, 45, 35, 13, 82, 49, 63, 2, 9, 7, 20, 62, 28, 18, 23, 0, 5, 87, 40, 61, 80, 93, 67, 59, 12, 98, 15, 64, 70, 52, 72, 39, 46, 29, 55, 71, 77, 90, 24, 91, 48, 34, 78, 86, 57, 38, 36, 53, 96, 73, 92, 81, 22, 1, 69, 94, 14, 66, 33, 11, 47, 42, 89, 65}),
        new Permutation(new int[]{87, 76, 46, 19, 71, 3, 35, 67, 22, 56, 28, 98, 99, 25, 83, 57, 11, 81, 12, 89, 14, 91, 47, 4, 73, 63, 10, 72, 75, 77, 85, 13, 16, 48, 66, 33, 50, 18, 93, 54, 40, 37, 2, 41, 8, 0, 78, 39, 64, 24, 60, 45, 74, 90, 55, 97, 6, 9, 27, 32, 29, 1, 7, 58, 69, 20, 79, 21, 86, 96, 43, 92, 53, 82, 30, 95, 42, 62, 52, 38, 36, 51, 59, 94, 26, 17, 61, 49, 84, 15, 80, 23, 34, 88, 5, 65, 70, 68, 44, 31}),
        new Permutation(new int[]{37, 36, 0, 33, 60, 55, 10, 93, 99, 8, 95, 73, 70, 62, 63, 74, 54, 7, 3, 5, 75, 46, 18, 64, 59, 65, 92, 61, 66, 9, 41, 32, 31, 80, 51, 78, 22, 43, 30, 29, 42, 4, 34, 25, 14, 88, 68, 58, 69, 87, 91, 23, 96, 11, 72, 47, 6, 19, 21, 49, 97, 50, 28, 85, 39, 98, 1, 16, 20, 40, 77, 83, 90, 52, 27, 12, 44, 94, 17, 81, 2, 56, 45, 15, 48, 79, 13, 35, 84, 24, 38, 71, 57, 82, 86, 76, 67, 89, 53, 26}),
        new Permutation(new int[]{89, 81, 3, 12, 87, 69, 67, 91, 57, 19, 95, 63, 61, 90, 44, 62, 1, 10, 65, 58, 42, 28, 56, 64, 93, 48, 35, 76, 85, 78, 29, 70, 13, 39, 21, 30, 92, 7, 46, 32, 4, 55, 59, 37, 26, 45, 23, 83, 11, 86, 72, 52, 82, 20, 22, 94, 25, 2, 80, 98, 40, 88, 6, 51, 31, 66, 75, 18, 84, 17, 8, 49, 9, 77, 0, 79, 5, 99, 54, 60, 47, 97, 74, 33, 43, 71, 24, 53, 34, 27, 73, 50, 68, 41, 96, 36, 14, 16, 38, 15}),
        new Permutation(new int[]{43, 76, 46, 75, 37, 30, 10, 19, 83, 8, 51, 26, 17, 79, 16, 21, 88, 74, 60, 99, 56, 95, 44, 50, 27, 85, 58, 84, 41, 68, 6, 97, 54, 32, 4, 3, 25, 45, 35, 13, 82, 49, 63, 2, 9, 7, 20, 62, 28, 18, 23, 0, 5, 87, 40, 61, 80, 93, 67, 59, 12, 98, 15, 64, 70, 52, 72, 39, 31, 29, 55, 71, 77, 90, 24, 91, 48, 34, 78, 86, 57, 38, 36, 53, 96, 73, 92, 81, 22, 1, 69, 94, 14, 66, 33, 11, 47, 42, 89, 65}),
        new Permutation(new int[]{43, 76, 46, 75, 37, 30, 10, 19, 83, 8, 51, 26, 17, 79, 16, 21, 88, 74, 60, 99, 56, 95, 44, 50, 27, 85, 58, 84, 41, 68, 6, 97, 54, 32, 4, 3, 25, 45, 35, 13, 82, 49, 63, 2, 9, 7, 20, 62, 28, 18, 23, 0, 5, 87, 40, 61, 80, 93, 67, 59, 12, 98, 15, 64, 70, 52, 72, 39, 31, 29, 55, 71, 77, 90, 24, 91, 48, 34, 78, 86, 57, 38, 36, 53, 96, 73, 92, 81, 22, 1, 69, 94, 14, 66, 33, 11, 47, 42, 89, 65}),
        new Permutation(new int[]{82, 92, 30, 81, 26, 9, 87, 94, 99, 16, 41, 39, 38, 37, 5, 46, 45, 18, 49, 68, 74, 86, 70, 52, 97, 33, 57, 20, 44, 69, 78, 10, 35, 55, 2, 28, 93, 36, 54, 96, 1, 48, 31, 4, 85, 47, 50, 6, 95, 60, 32, 3, 17, 21, 12, 64, 61, 42, 51, 63, 67, 11, 19, 89, 91, 40, 53, 15, 29, 98, 43, 90, 56, 62, 84, 58, 88, 79, 34, 73, 27, 76, 14, 7, 71, 72, 25, 83, 23, 8, 0, 13, 75, 66, 24, 65, 77, 22, 59, 80}),
        new Permutation(new int[]{73, 60, 71, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 92, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 30, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{73, 76, 46, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 60, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 92, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 30, 44, 71, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{43, 92, 30, 75, 37, 46, 10, 19, 83, 8, 51, 26, 17, 79, 16, 21, 88, 74, 60, 99, 56, 95, 44, 50, 27, 85, 58, 84, 41, 68, 6, 97, 54, 32, 4, 3, 25, 45, 35, 13, 82, 49, 63, 2, 9, 7, 20, 62, 28, 18, 23, 0, 5, 87, 40, 61, 80, 93, 67, 59, 12, 98, 15, 64, 70, 52, 72, 39, 31, 29, 55, 71, 77, 90, 24, 91, 48, 34, 78, 86, 57, 38, 36, 53, 96, 73, 76, 81, 22, 1, 69, 94, 14, 66, 33, 11, 47, 42, 89, 65})));

    Population<Permutation> pop3 = new Population<Permutation>(Arrays.asList(
        new Permutation(new int[]{73, 60, 71, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 92, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 10, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 30, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 7, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{73, 10, 7, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 60, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 92, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 71, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 30, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{73, 60, 71, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 76, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 92, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 30, 44, 46, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{73, 76, 46, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 92, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 60, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 71, 44, 30, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{87, 81, 3, 19, 71, 7, 35, 67, 22, 56, 28, 98, 99, 25, 83, 57, 11, 10, 12, 89, 14, 91, 47, 4, 73, 63, 84, 72, 75, 77, 85, 13, 16, 48, 66, 33, 50, 18, 93, 54, 40, 37, 2, 41, 8, 0, 78, 39, 64, 24, 60, 45, 74, 90, 55, 97, 6, 9, 27, 32, 29, 1, 31, 58, 69, 20, 79, 21, 86, 96, 43, 92, 53, 82, 30, 95, 42, 62, 52, 38, 36, 51, 59, 94, 26, 17, 61, 49, 76, 15, 80, 23, 34, 88, 5, 65, 70, 68, 44, 46}),
        new Permutation(new int[]{73, 10, 7, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 81, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 92, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 60, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 30, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 3, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 71, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{73, 76, 46, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 92, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 60, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 71, 44, 30, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{73, 60, 71, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 76, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 92, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 30, 44, 46, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{73, 81, 3, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 76, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 60, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 92, 98, 37, 97, 40, 51, 42, 29, 46, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 30, 44, 71, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{73, 76, 46, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 92, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 60, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 81, 98, 37, 97, 40, 51, 42, 29, 30, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 3, 44, 71, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{87, 92, 30, 19, 71, 3, 35, 67, 22, 56, 28, 98, 99, 25, 83, 57, 11, 81, 12, 89, 14, 91, 47, 4, 73, 63, 84, 72, 75, 77, 85, 13, 16, 48, 66, 33, 50, 18, 93, 54, 40, 37, 2, 41, 8, 0, 78, 39, 64, 24, 60, 45, 74, 90, 55, 97, 6, 9, 27, 32, 29, 1, 31, 58, 69, 20, 79, 21, 86, 96, 43, 10, 53, 82, 7, 95, 42, 62, 52, 38, 36, 51, 59, 94, 26, 17, 61, 49, 76, 15, 80, 23, 34, 88, 5, 65, 70, 68, 44, 46}),
        new Permutation(new int[]{43, 10, 7, 75, 37, 46, 92, 19, 83, 8, 51, 26, 17, 79, 16, 21, 88, 74, 60, 99, 56, 95, 44, 50, 27, 85, 58, 84, 41, 68, 6, 97, 54, 32, 4, 3, 25, 45, 35, 13, 82, 49, 63, 2, 9, 30, 20, 62, 28, 18, 23, 0, 5, 87, 40, 61, 80, 93, 67, 59, 12, 98, 15, 64, 70, 52, 72, 39, 31, 29, 55, 71, 77, 90, 24, 91, 48, 34, 78, 86, 57, 38, 36, 53, 96, 73, 76, 81, 22, 1, 69, 94, 14, 66, 33, 11, 47, 42, 89, 65}),
        new Permutation(new int[]{73, 10, 7, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 92, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 60, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 30, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 71, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{87, 10, 7, 19, 71, 3, 35, 67, 22, 56, 28, 98, 99, 25, 83, 57, 11, 81, 12, 89, 14, 91, 47, 4, 73, 63, 84, 72, 75, 77, 85, 13, 16, 48, 66, 33, 50, 18, 93, 54, 40, 37, 2, 41, 8, 0, 78, 39, 64, 24, 60, 45, 74, 90, 55, 97, 6, 9, 27, 32, 29, 1, 31, 58, 69, 20, 79, 21, 86, 96, 43, 92, 53, 82, 30, 95, 42, 62, 52, 38, 36, 51, 59, 94, 26, 17, 61, 49, 76, 15, 80, 23, 34, 88, 5, 65, 70, 68, 44, 46}),
        new Permutation(new int[]{73, 10, 7, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 92, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 60, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 30, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 71, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{87, 10, 7, 19, 71, 3, 35, 67, 22, 56, 28, 98, 99, 25, 83, 57, 11, 81, 12, 89, 14, 91, 47, 4, 73, 63, 84, 72, 75, 77, 85, 13, 16, 48, 66, 33, 50, 18, 93, 54, 40, 37, 2, 41, 8, 0, 78, 39, 64, 24, 60, 45, 74, 90, 55, 97, 6, 9, 27, 32, 29, 1, 31, 58, 69, 20, 26, 94, 59, 51, 36, 38, 52, 62, 42, 95, 30, 82, 53, 92, 43, 96, 86, 21, 79, 17, 61, 49, 76, 15, 80, 23, 34, 88, 5, 65, 70, 68, 44, 46}),
        new Permutation(new int[]{73, 92, 30, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 60, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 71, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{43, 76, 46, 75, 37, 30, 10, 19, 83, 8, 51, 26, 17, 79, 16, 21, 88, 74, 60, 99, 56, 95, 44, 50, 27, 85, 58, 84, 41, 68, 6, 97, 54, 32, 4, 3, 25, 45, 35, 13, 82, 49, 63, 2, 9, 7, 20, 62, 28, 18, 23, 0, 5, 87, 40, 61, 80, 93, 67, 59, 12, 98, 15, 64, 70, 52, 72, 39, 31, 29, 55, 71, 77, 90, 24, 91, 48, 34, 78, 86, 57, 38, 36, 53, 96, 73, 92, 81, 22, 1, 69, 94, 14, 66, 33, 11, 47, 42, 89, 65}),
        new Permutation(new int[]{73, 92, 30, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 81, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 60, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 3, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 71, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{43, 81, 3, 75, 37, 46, 10, 19, 83, 8, 51, 26, 17, 79, 16, 21, 88, 74, 60, 99, 56, 95, 44, 50, 27, 85, 58, 84, 41, 68, 6, 97, 54, 32, 4, 30, 25, 45, 35, 13, 82, 49, 63, 2, 9, 7, 20, 62, 28, 18, 23, 0, 5, 87, 40, 61, 80, 93, 67, 59, 12, 98, 15, 64, 70, 52, 72, 39, 31, 29, 55, 71, 77, 90, 24, 91, 48, 34, 78, 86, 57, 38, 36, 53, 96, 73, 76, 92, 22, 1, 69, 94, 14, 66, 33, 11, 47, 42, 89, 65}),
        new Permutation(new int[]{73, 60, 71, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 92, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 81, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 30, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 3, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{73, 81, 3, 82, 84, 18, 57, 67, 77, 79, 8, 34, 26, 1, 88, 70, 6, 10, 9, 55, 93, 69, 61, 16, 23, 59, 0, 13, 47, 91, 43, 17, 72, 36, 31, 22, 60, 83, 68, 62, 90, 5, 14, 74, 48, 28, 87, 92, 21, 27, 32, 58, 56, 11, 15, 39, 35, 25, 33, 76, 98, 37, 97, 40, 51, 42, 29, 71, 4, 2, 19, 86, 52, 53, 66, 89, 41, 12, 45, 7, 80, 85, 24, 63, 78, 50, 65, 95, 94, 38, 49, 46, 44, 30, 99, 64, 75, 96, 54, 20}),
        new Permutation(new int[]{87, 10, 7, 19, 71, 3, 35, 67, 22, 56, 28, 98, 99, 25, 83, 57, 11, 81, 12, 89, 14, 91, 47, 4, 73, 63, 84, 72, 75, 77, 85, 13, 16, 48, 66, 33, 50, 18, 93, 54, 40, 37, 2, 41, 8, 0, 78, 39, 64, 24, 60, 45, 74, 90, 55, 97, 6, 9, 27, 32, 29, 1, 31, 58, 69, 20, 79, 21, 86, 96, 43, 92, 53, 82, 30, 95, 42, 62, 52, 38, 36, 51, 59, 94, 26, 17, 61, 49, 76, 15, 80, 23, 34, 88, 5, 65, 70, 68, 44, 46}),
        new Permutation(new int[]{87, 10, 7, 19, 71, 3, 35, 67, 22, 56, 28, 98, 99, 25, 83, 57, 11, 81, 12, 89, 14, 91, 47, 4, 73, 63, 84, 72, 75, 77, 85, 13, 16, 48, 66, 33, 50, 18, 93, 54, 40, 37, 2, 41, 8, 0, 78, 39, 64, 24, 60, 45, 74, 90, 55, 97, 6, 9, 27, 32, 29, 1, 31, 58, 69, 20, 79, 21, 86, 96, 43, 92, 53, 82, 30, 95, 42, 62, 52, 38, 36, 51, 59, 94, 26, 17, 61, 49, 76, 15, 80, 23, 34, 88, 5, 65, 70, 68, 44, 46})));

    mockControl.reset();
    TaskExchanger<Permutation> taskExchangerMock =
        mockControl.createMock(TaskExchanger.class);
    SlaveRegistrator slaveRegistratorMock =
        mockControl.createMock(SlaveRegistrator.class);

    EasyMock.expect(taskExchangerMock.getTask(
        (String) EasyMock.anyObject(), EasyMock.anyInt()))
            .andReturn(new EvaluationTask<Permutation>(pop1));
    EasyMock.expect(taskExchangerMock.getTask(
        (String) EasyMock.anyObject(), EasyMock.anyInt()))
            .andReturn(new EvaluationTask<Permutation>(pop2));
    EasyMock.expect(taskExchangerMock.getTask(
        (String) EasyMock.anyObject(), EasyMock.anyInt()))
            .andReturn(new EvaluationTask<Permutation>(pop3));

    // TODO(karol.stosiek|anglart.michal): after introducing "evolution-over"
    // signal, remove this behaviour.
    EasyMock.expect(taskExchangerMock.getTask(
        (String) EasyMock.anyObject(), EasyMock.anyInt()))
            .andThrow(new IllegalStateException("Stop the slave execution."));

    taskExchangerMock.sendResult(
        (EvaluationResult<Permutation>) EasyMock.anyObject(),
        (String) EasyMock.anyObject());
    EasyMock.expectLastCall().anyTimes();

    Slave<Permutation> testedSlave = new Slave<Permutation>(
        "",
        new SingleThreadedEvaluator<Permutation>(
            Arrays.asList(new CachedObjectiveFunction<Permutation>(
                new EuclideanTSP(), 25))),
        taskExchangerMock, 
        slaveRegistratorMock, 
        "slave",
        new SystemClock());
    testedSlave.setSlaveId("slaveid");

    mockControl.replay();
    testedSlave.run(500, 500);
    mockControl.verify();
    // LineLength on
    // MagicNumber on
  }
}
