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
 * Standard Java I/O serialization.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 * 
 * @param <T> Type of the individual in the result.
 */
public class JavaIOResultSerializer<T> implements ResultSerializer<T> {

  /** {@inheritDoc} */
  public EvaluationResult<T> deserialize(final InputStream inputStream)
      throws IOException, ClassNotFoundException {

    ObjectInputStream input = inputStream instanceof ObjectInputStream
    ? (ObjectInputStream) inputStream
        : new ObjectInputStream(inputStream);

    return castToResult(input);
  }

  /**
   * @param input Input stream to read from.
   * @return Evaluation result.
   * @throws IOException Thrown on IO errors.
   * @throws ClassNotFoundException Thrown when individual definition
   * was not found.
   */
  @SuppressWarnings("unchecked")
  private EvaluationResult<T> castToResult(final ObjectInputStream input)
      throws IOException, ClassNotFoundException {
    return (EvaluationResult<T>) input.readObject();
  }

  /** {@inheritDoc} */
  public void serialize(
        final OutputStream outputStream,
        final EvaluationResult<T> result)
      throws IOException {

    ObjectOutputStream output = outputStream instanceof ObjectOutputStream
        ? (ObjectOutputStream) outputStream
            : new ObjectOutputStream(outputStream);

    output.writeObject(result);
    output.flush();
    output.close();
  }
}
