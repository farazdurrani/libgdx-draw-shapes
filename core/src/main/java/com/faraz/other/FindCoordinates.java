package com.faraz.other;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindCoordinates {
  public static void main(String[] args) {
    List<Point> polygonVertices = new ArrayList<>();
    polygonVertices.add(new Point(0, 0));
    polygonVertices.add(new Point(0, 100));
    polygonVertices.add(new Point(100, 100));
    polygonVertices.add(new Point(100, 0));

    Point target = new Point(24, 40);
    boolean notFound = true;
    while (notFound) {
      Point centroid = calculateCentroid(polygonVertices);
      if (target.equals(centroid)) {
        System.out.println("Got it!");
        notFound = false;
      } else {
        if (target.getX() < centroid.getX() && target.getY() < centroid.getY()) {
          polygonVertices = quadrants(polygonVertices.get(0), midPoint(polygonVertices.get(0), polygonVertices.get(1)), centroid, midPoint(polygonVertices.get(0), polygonVertices.get(3)));
        } else if (target.getX() < centroid.getX() && target.getY() > centroid.getY()) {
          polygonVertices = quadrants(midPoint(polygonVertices.get(0), polygonVertices.get(1)), polygonVertices.get(1), midPoint(polygonVertices.get(1), polygonVertices.get(2)), centroid);
        } else if (target.getX() > centroid.getX() && target.getY() > centroid.getY()) {
          polygonVertices = quadrants(centroid, midPoint(polygonVertices.get(1), polygonVertices.get(2)), polygonVertices.get(2), midPoint(polygonVertices.get(2), polygonVertices.get(3)));
        } else if (target.getX() > centroid.getX() && target.getY() < centroid.getY()) {
          polygonVertices = quadrants(midPoint(polygonVertices.get(0), polygonVertices.get(3)), centroid, midPoint(polygonVertices.get(2), polygonVertices.get(3)), polygonVertices.get(3));
        }
      }
    }
  }

  private static List<Point> quadrants(Point bottomLeft, Point topLeft, Point topRight, Point bottomRight) {
    return Arrays.asList(bottomLeft, topLeft, topRight, bottomRight);
  }

  private static Point midPoint(Point x, Point y) {
//    M = ((x₁ + x₂)/2, (y₁ + y₂)/2)
    return new Point((int) Math.round((x.getX() + y.getX()) / 2), (int) Math.round((x.getY() + y.getY()) / 2));
  }

  public static Point calculateCentroid(List<Point> points) {
    double sumX = 0;
    double sumY = 0;

    for (Point p : points) {
      sumX += p.getX();
      sumY += p.getY();
    }

    int numPoints = points.size();
    int centroidX = (int) Math.round(sumX / numPoints);
    int centroidY = (int) Math.round(sumY / numPoints);

    return new Point(centroidX, centroidY);
  }
}
