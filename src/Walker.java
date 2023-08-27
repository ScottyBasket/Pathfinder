import java.util.Iterator;

/**
 * Walker takes an Iterable of Coords and simulates an individual
 * walking along the path over the given Terrain
 */
public class Walker {

    private Coord location;
    private Terrain terrain;
    private Iterator<Coord> path;
    private boolean doneWalking=false;

    // terrain: the Terrain the Walker traverses
    // path: the sequence of Coords the Walker follows
    public Walker(Terrain terrain, Iterable<Coord> path) {
        this.terrain=terrain;
        this.path=path.iterator();
        location=this.path.next();
    }

    // returns the Walker's current location
    public Coord getLocation() {
        return location;
    }

    // returns true if Walker has reached the end Coord (last in path)
    public boolean doneWalking() {
        return !path.hasNext();
    }

    // advances the Walker along path
    // byTime: how long the Walker should traverse (may be any non-negative value)
    public void advance(float byTime) {
        Coord next;
        while(path.hasNext() && byTime > 0){
            next=path.next();
            byTime = terrain.computeTravelCost(location,next);
            location=next;
            doneWalking = !path.hasNext();
        }
    }

}
