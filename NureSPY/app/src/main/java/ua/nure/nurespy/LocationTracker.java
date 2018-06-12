package ua.nure.nurespy;

import android.location.Location;

public interface LocationTracker {
    public void start();
    public void start(LocationUpdateListener update);

    public void stop();

    public boolean hasLocation();

    public boolean hasPossiblyStaleLocation();

    public Location getLocation();

    public Location getPossiblyStaleLocation();

}