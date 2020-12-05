package com.example.roamer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RoAmeRView extends View {
    Context context;
    private Location currentLocation;
    private float[] rotatedProjectionMatrix = new float[16];
    private List<ARPoint> arPoints;

    public RoAmeRView (Context context) {
        super(context);
        this.context=context;

        arPoints = new ArrayList<ARPoint>() {{
            add(new ARPoint("Ηλεκτρονικά Είδη Τεχνολογίας", 39.364851, 21.923120, 110));
            add(new ARPoint("Πλατεία Πλαστήρα", 39.3639828, 21.9272391, 110));
            add(new ARPoint("Παιδικά Ενδύματα", 39.364463, 21.923920, 110));
            add(new ARPoint("Πλατείσ Ελευθερίας", 39.364790, 21.923756, 110));
        }};
    }

    public double compDistance(Location currentLocation, ARPoint point){
        double cLat = currentLocation.getLatitude();
        double cLon = currentLocation.getLongitude();
        double cAlt = currentLocation.getAltitude();
        double distance = Distance(cLat, point.location.getLatitude(), cLon, point.location.getLongitude(), cAlt, point.location.getAltitude());
        return (int)distance;
    }

    public void updateCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;
        this.invalidate();
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if (currentLocation == null) {
            return;
        }

        final int radius = 30;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);

        for (int i = 0; i < arPoints.size(); i ++) {
            float[] currentLocationInECEF = locationConverter.WSG84toECEF(currentLocation);
            float[] pointInECEF = locationConverter.WSG84toECEF(arPoints.get(i).getLocation());
            float[] pointInENU = locationConverter.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

            float[] cameraCoordinateVector = new float[4];
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                float x  = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3]) * canvas.getHeight();

                if (compDistance(currentLocation,arPoints.get(i))<500){
                canvas.drawCircle(x, y, radius, paint);
                String textPoint = arPoints.get(i).getName()+" σε "+compDistance(currentLocation,arPoints.get(i))+" Μέτρα";
                canvas.drawText(textPoint, x - (30 * arPoints.get(i).getName().length() / 2), y - 80, paint);
                }
            }
        }
    }

    public static double Distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
