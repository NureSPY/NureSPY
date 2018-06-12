package ua.nure.nurespy;

import android.location.Location;

public interface LocationUpdateListener {
    public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime);
}
