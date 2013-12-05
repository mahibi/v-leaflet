package org.vaadin.addon.leaflet.shared;

import java.util.Collection;
import java.util.HashSet;

import org.peimari.gleaflet.client.Polyline;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolygon;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LeafletLayer;
import org.vaadin.addon.leaflet.shared.Point;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Helper methods to convert between JTS and v-leaflet objects.
 * 
 * Implementing: https://github.com/mstahv/v-leaflet/issues/18
 */
public class JTSUtil {

    /**
     * Translates between a JTS {@link Geometry} and one or more
     * {@link LeafletLayer}s
     * 
     * @param geom
     *            {@link Geometry}
     * 
     * @return
     */
    public static Collection<LeafletLayer> toLayers(Geometry geom) {

        Collection<LeafletLayer> layers = new HashSet<LeafletLayer>();

        if (geom instanceof Polygon) {

            Polygon polygon = (Polygon) geom;
            LPolygon lPolygon = toPolygon(polygon);
            layers.add(lPolygon);
            return layers;

        } else if (geom instanceof LineString) {

            LPolyline lPolyline = toPolyline((LineString) geom);
            layers.add(lPolyline);

        } else if (geom instanceof MultiPolygon) {

            for (int i = 0; i < geom.getNumGeometries(); i++) {
                Polygon polygon = (Polygon) geom.getGeometryN(i);
                LPolygon lPolygon = toPolygon(polygon);
                layers.add(lPolygon);
            }

        } else if (geom instanceof MultiLineString) {

            for (int i = 0; i < geom.getNumGeometries(); i++) {
                LineString lineString = (LineString) geom.getGeometryN(i);
                LPolyline lPolyline = toPolyline(lineString);
                layers.add(lPolyline);
            }

        } else if (geom instanceof com.vividsolutions.jts.geom.Point) {

            com.vividsolutions.jts.geom.Point point = (com.vividsolutions.jts.geom.Point) geom;
            LMarker lMarker = toLMarker(point);
            layers.add(lMarker);

        } else if (geom instanceof MultiPoint) {
            for (int i = 0; i < geom.getNumGeometries(); i++) {

                com.vividsolutions.jts.geom.Point point = (com.vividsolutions.jts.geom.Point) geom
                        .getGeometryN(i);
                LMarker lMarker = toLMarker(point);
                layers.add(lMarker);

            }
        }

        return layers;
    }

    /**
     * Translates between a {@link com.vividsolutions.jts.geom.Point} and a
     * {@link LMarker}
     * 
     * @param point
     * @return
     */
    private static LMarker toLMarker(com.vividsolutions.jts.geom.Point point) {
        LMarker lMarker = new LMarker();
        Point lPoint = new Point();
        lPoint.setLat(point.getY());
        lPoint.setLon(point.getX());
        lMarker.setPoint(lPoint);
        return lMarker;
    }

    /**
     * Translates between a JTS {@link LineString} and a v-leaflet
     * {@link Polyline}
     * 
     * @param lineString
     * @return
     */
    private static LPolyline toPolyline(LineString lineString) {

        Coordinate[] coords = lineString.getCoordinates();
        Point[] points = toPointArray(coords);
        LPolyline polyLine = new LPolyline(points);
        return polyLine;
    }

    /**
     * Translates between a JTS {@link Polygon} and a v-leaflet {@link LPolygon}
     * 
     * @param polygon
     * @return
     */
    private static LPolygon toPolygon(Polygon polygon) {
        Coordinate[] coords = polygon.getBoundary().getCoordinates();
        Point[] points = toPointArray(coords);

        LPolygon lPolygon = new LPolygon(points);
        return lPolygon;
    }

    /**
     * Translates between an array of v-leaflet {@link Point}s and JTS
     * {@link Coordinate}s
     * 
     * @param coords
     * @return
     */
    private static Point[] toPointArray(Coordinate[] coords) {
        Point[] points = new Point[coords.length];

        for (int i = 0; i < coords.length; i++) {
            points[i] = new Point(coords[i].y, coords[i].x);
        }
        return points;
    }

    public static org.vaadin.addon.leaflet.shared.Point[] toLeafletPointArray(
            LineString path) {
        Coordinate[] coordinates = path.getCoordinates();
        org.vaadin.addon.leaflet.shared.Point[] points = new org.vaadin.addon.leaflet.shared.Point[coordinates.length];
        for (int i = 0; i < points.length; i++) {
            Coordinate coordinate = coordinates[i];
            points[i] = new org.vaadin.addon.leaflet.shared.Point(coordinate.y,
                    coordinate.x);
        }
        return points;
    }

    public static LineString toLineString(
            org.vaadin.addon.leaflet.shared.Point[] points) {
        GeometryFactory factory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[points.length];
        for (int i = 0; i < coordinates.length; i++) {
            Point p = points[i];
            coordinates[i] = new Coordinate(p.getLon(), p.getLat());
        }
        return factory.createLineString(coordinates);
    }

    public static LineString toLineString(LPolyline polyline) {
        Point[] points = polyline.getPoints();
        return toLineString(points);
    }

    public static com.vividsolutions.jts.geom.Point toPoint(
            org.vaadin.addon.leaflet.shared.Point p) {
        com.vividsolutions.jts.geom.Point point = new GeometryFactory()
                .createPoint(new Coordinate(p.getLon(), p.getLat()));
        return point;
    }

    public static com.vividsolutions.jts.geom.Point toPoint(LMarker lMarker) {
        return toPoint(lMarker.getPoint());
    }

    public static org.vaadin.addon.leaflet.shared.Point toLeafletPoint(
            com.vividsolutions.jts.geom.Point location) {
        org.vaadin.addon.leaflet.shared.Point p = new org.vaadin.addon.leaflet.shared.Point(
                location.getY(), location.getX());
        return p;
    }

}