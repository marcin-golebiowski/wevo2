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
package engine.distribution.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Standard serializer using ObjectStreams.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 * @param <T> type of individual in a population.
 */
public class JavaIOTaskSerializer<T> implements TaskSerializer<T> {

  /** {@inheritDoc} */
  public EvaluationTask<T> deserialize(final InputStream inputStream)
      throws IOException, ClassNotFoundException {

    ObjectInputStream input = inputStream instanceof ObjectInputStream
        ? (ObjectInputStream) inputStream
            : new ObjectInputStream(inputStream);

    return castToTask(input);
  }

  /**
   * @param input Input stream to read from.
   * @return Task object.
   * @throws IOException Thrown on IO errors.
   * @throws ClassNotFoundException Thrown when individual definition
   * was not found.
   */
  @SuppressWarnings("unchecked")
  private EvaluationTask<T> castToTask(final ObjectInputStream input)
      throws IOException, ClassNotFoundException {
    return (EvaluationTask<T>) input.readObject();
  }

  /** {@inheritDoc} */
  public void serialize(
      final OutputStream outputStream,
      final EvaluationTask<T> task) throws IOException {

    ObjectOutputStream output = outputStream instanceof ObjectOutputStream
        ? (ObjectOutputStream) outputStream
            : new ObjectOutputStream(outputStream);

    output.writeObject(task);
    output.flush();
    output.close();
  }
}
