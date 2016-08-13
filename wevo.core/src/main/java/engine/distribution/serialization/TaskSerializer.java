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
import java.io.OutputStream;

/**
 * Interface that each task serializer must implement.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 *
 * @param <T> Type of individual in a population that will be serialized.
 */
public interface TaskSerializer<T> {
  /**
   * Deserializes task.
   * @param inputStream Stream to read from.
   * @return Evaluation task object.
   * @throws IOException Exception thrown on input errors. 
   * @throws ClassNotFoundException Exception thrown when deserialized.
   * object class file does not exist.
   */
  EvaluationTask<T> deserialize(final InputStream inputStream)
      throws IOException, ClassNotFoundException;

  /**
   * Serializes task.
   * @param outputStream Stream to write to.
   * @param task Task to be serialized.
   * @throws IOException Exception thrown on output errors.
   */
  void serialize(
      final OutputStream outputStream, 
      final EvaluationTask<T> task)
    throws IOException;
}
