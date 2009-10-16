/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.mahout.common.distance;

import org.apache.mahout.matrix.Vector;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Tanimoto coefficient implementation.
 *
 * http://en.wikipedia.org/wiki/Jaccard_index
 */
public class TanimotoDistanceMeasure extends WeightedDistanceMeasure {


  /**
   * Calculates the distance between two vectors.
   * 
   * The coefficient (a measure of similarity) is:
   * T(a, b) = a.b / (|a|^2 + |b|^2 - a.b)
   *
   * The distance d(a,b) = 1 - T(a,b)
   *
   * @return 0 for perfect match, > 0 for greater distance
   */
  @Override
  public double distance(Vector a, Vector b) {
    double ab = dot(a, b);
    double denominator = dot(a, a) + dot(b, b) - ab;
    if(denominator < ab) {  // correct for fp round-off: distance >= 0
      denominator = ab;
    };
    if(denominator > 0) {
        // denom == 0 only when dot(a,a) == dot(b,b) == dot(a,b) == 0
      return 1 - ab / denominator;
    } else {
      return 0;
    }
  }
  
  public double dot(Vector a, Vector b) {
    Iterator<Vector.Element> it = a.iterateNonZero();
    Vector.Element el = null;
    Vector weights = getWeights();
    double dot = 0;
    while(it.hasNext() && (el = it.next()) != null) {
      try {
      dot += el.get() * (a == b ? el.get() : b.getQuick(el.index())) * (weights == null ? 1.0 : weights.getQuick(el.index()));
      } catch (NullPointerException npe) {
        System.out.println(a.asFormatString() + "\n" + b.asFormatString() + "\n" + weights.asFormatString());
        throw npe;
      }
    }
    return dot;
  }

  @Override
  public double distance(double centroidLengthSquare, Vector centroid, Vector v) {
    return distance(centroid, v); // TODO
  }

}
