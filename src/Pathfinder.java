import java.lang.IndexOutOfBoundsException;
import java.lang.IllegalArgumentException;

/**
 * Pathfinder uses A* search to find a near optimal path
 * between to locations with given terrain.
 */

public class Pathfinder {
    private Terrain terrain;
    private Coord start;
    private Coord end;
    private float heuristic = 1.0f;
    //private float cost=0.0f;
    //private Coord loc;
    private float pathCost;
    private boolean foundPath;
    private int searchSize;
    private Stack <Coord> pathSolution;
    private PFNode[][] map;
    private int mapSize;

    /**
     * PFNode will be the key for MinPQ (used in computePath())
     */
    private class PFNode implements Comparable<PFNode> {
        final Coord loc;
        final PFNode fromNode;
        boolean valid;
        float cost;
        boolean used;
        final float costToEnd;
        // loc: the location of the PFNode
        // fromNode: how did we get here? (linked list back to start)

        public PFNode(Coord loc, PFNode fromNode) {
            this.loc=loc;
            this.fromNode=fromNode;
            this.valid=true;
            this.used=false;
            this.cost=0.0f;
            this.costToEnd=terrain.computeDistance(loc, end);
            if(fromNode!=null) {
                this.cost = fromNode.cost;
                this.cost += terrain.computeTravelCost(fromNode.loc, loc);
            }
        }

        // compares this with that, used to find minimum cost PFNode
        public int compareTo(PFNode that) {
            if(this.getCost(heuristic)<that.getCost(heuristic)){
                return -1;
            }
            else if (this.getCost(heuristic)>that.getCost(heuristic)){
                return 1;
            }
            else return 0;
        }

        // returns the cost to travel from starting point to this
        // via the fromNode chain
        public float getCost(float heuristic) {
            return cost+heuristic*costToEnd;
        }

        // returns if this PFNode is still valid
        public boolean isValid() {
            return valid;
        }

        // invalidates the PFNode
        public void invalidate() {
            valid=false;
        }

        // returns if the PFNode has been used
        public boolean isUsed() {
            return used;
        }

        // uses the PFNode
        public void use() {
            used=true;
        }

        // returns an Iterable of PFNodes that surround this
        public Iterable<PFNode> neighbors() {
            Stack<PFNode> s = new Stack<>();
            int i = this.loc.getI();
            int j = this.loc.getJ();
            if(i-1>=0){
                s.push(new PFNode(new Coord( i-1, j) , this));
            }
            if(i+1<mapSize){
                s.push(new PFNode(new Coord( i+1, j) , this));
            }
            if(j-1>=0){
                s.push(new PFNode(new Coord( i, j-1) , this));
            }
            if(j+1<mapSize){
                s.push(new PFNode(new Coord( i, j+1) , this));
            }

            return s;
        }
    }

    public Pathfinder(Terrain terrain) {
        this.terrain = terrain;
        this.mapSize = terrain.getN();
        resetPath();
    }

    public void setPathStart(Coord loc) {
        this.start = loc;
    }

    public Coord getPathStart() {
        return start;
    }

    public void setPathEnd(Coord loc) {
        this.end=loc;
    }

    public Coord getPathEnd() {
        return end;
    }

    public void setHeuristic(float v) {
        this.heuristic=v;
    }

    public float getHeuristic() {
        return heuristic;
    }

    public void resetPath() {
        map = new PFNode[mapSize][mapSize];
        searchSize=0;
        pathSolution = new Stack<>();
        foundPath=false;
        pathCost=0.0f;
    }

    public void computePath() {
        MinPQ<PFNode> pq=new MinPQ();
        pq.insert(new PFNode (start, null));
        PFNode temp = null;

        while(!pq.isEmpty()){
            temp=pq.delMin();
            map[temp.loc.getI()][temp.loc.getJ()] = temp;
            if(temp.isUsed())continue;
            if(!temp.isValid())continue;
            searchSize++;
            temp.use();
            if(temp.loc.equals(end)){break;}
            for(PFNode n: temp.neighbors()){
                int i =n.loc.getI();
                int j =n.loc.getJ();
                if(map[i][j]!=null) continue;
                map[i][j] = n;
                pq.insert(n);
            }
        }

        pathSolution=new Stack<>();
        pathCost=temp.getCost(heuristic);
        while (temp!=null){
            pathSolution.push(temp.loc);
            temp=temp.fromNode;
        }
        foundPath=true;
    }

    public boolean foundPath() {
        return foundPath;
    }

    public float getPathCost() {
        return pathCost;
    }

    public int getSearchSize() {
        return searchSize;
    }

    public Iterable<Coord> getPathSolution() {
        return pathSolution;
    }

    public boolean wasSearched(Coord loc) {
        int i = loc.getI();
        int j = loc.getJ();
        return map[i][j]!=null;
    }
}
