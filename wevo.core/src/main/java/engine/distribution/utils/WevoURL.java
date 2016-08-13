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
package engine.distribution.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class containing references to URLs used in WEvo.
 *
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class WevoURL {

  /** Proxed URL. */
  private final URL proxiedURL;

  /** 
   * Constructor.
   * @param url Proxed URL.
   */
  public WevoURL(URL url) {
    this.proxiedURL = url;
  }

  /**
   * Convenience constructor.
   * @param url Proxed URL.
   * @throws MalformedURLException Thrown on malformed URLs.
   */
  public WevoURL(String url) throws MalformedURLException {
    this(new URL(url));
  }

  /**
   * Opens connection with server located under proxed URL.
   * @return Connection to the server.
   * @throws IOException Thrown on connection errors.
   */
  public HttpURLConnection openConnection() throws IOException {
    return (HttpURLConnection) proxiedURL.openConnection();
  }
}
