package stats;

import java.util.*;
import java.util.stream.*;
import org.neo4j.graphdb.*;
import org.neo4j.procedure.*;
import org.neo4j.logging.Log;

public class GraphStatistics {

    @Context public org.neo4j.graphdb.GraphDatabaseService db;

    public static class Degree {
        public final String label;
        public long count, max, min = Long.MAX_VALUE;

        Degree(String label) { this.label = label; }

        void add(int degree) {
            if (degree < min) min = degree;
            if (degree > max) max = degree;
            count ++;
        }
    }

    @Procedure
    // not needed here @PerformsWrites
    public Stream<Degree> degree(@Name("label") String label) {
        Degree degree = new Degree(label);
        try (ResourceIterator<Node> it = db.findNodes(Label.label(label))) {
            while (it.hasNext()) {
               degree.add(it.next().getDegree());
            }
        }
        return Stream.of(degree);
    }
}