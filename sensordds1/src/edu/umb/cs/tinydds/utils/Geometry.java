/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umb.cs.tinydds.utils;

/**
 *
 * @author francesco
 */
public class Geometry {

	public class Point2D extends Geometry {
        private double x;
        private double y;

        public Point2D(double x, double y){
            this.x = x;
            this.y = y;
        }

        public double getX(){
            return x;
        }

        public double getY() {
            return y;
        }

        public void setX(double x){
            this.x = x;
        }

        public void setY(double y){
            this.y = y;
        }
	}

	public class Rectangle2D extends Geometry {
        private double x1;
        private double y1;
        private double x2;
        private double y2;

        public Rectangle2D (double topLeftX, double topLeftY,
                            double bottomRightX, double bottomRightY){
            x1 = topLeftX;
            y1 = topLeftY;
            x2 = bottomRightX;
            y2 = bottomRightY;
        }

        public Rectangle2D (Point2D topLeftCorner, Point2D bottomRightCorner){
            x1 = topLeftCorner.getX();
            y1 = topLeftCorner.getY();
            x2 = bottomRightCorner.getX();
            y2 = bottomRightCorner.getY();
        }

        public double width(){
            return Math.abs(x1 - x2);
        }

        public double height(){
            return Math.abs(y1 - y2);
        }

        public double area(){
            return width() * height();
        }

        public Point2D getTopLeftCorner(){
           return new Point2D(x1, y1);
        }

        public Point2D getBottomRightCorner(){
           return new Point2D(x2, y2);
        }

        public boolean includes(Point2D p){
            return (p.getX() >= x1) && (p.getX() <= x2) &&
                   (p.getY() >= y2) && (p.getY() <= y1);
        }
	}

    public class Circle extends Geometry{
        private double x;
        private double y;
        private double radius;

        public Circle(double x, double y, double radius){
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public Circle(Point2D center, double radius){
            this.x = center.getX();
            this.y = center.getY();
            this.radius = radius;
        }

        public double area (){
            return radius * radius * Math.PI;
        }

        public Point2D getCenter(){
            return new Point2D(x, y);
        }

        public boolean includes(Point2D p){
            return Math.sqrt((p.getX()-x)*(p.getX()-x) +
                             (p.getY()-y)* (p.getY() - y)) <= radius;
        }
    }

    public class Polygon extends Geometry {
        // todo: stub only
    }
}
