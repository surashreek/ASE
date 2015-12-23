/**
 * Created by wh2307 on 12/2/15.
 */

import models.Rating;
import models.Session;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class RatingsTest extends FakeApp {
    @Test
    public void testRatings() { // rating should be >= -1, <= 6. Also, if not complete, should be -1.
        List<Session> allSessions = Session.getAllSessions();

        for (Session x : allSessions) {
            Rating y = Rating.getRating(x.session_id);
            assertTrue(y.rating >= -1 && y.rating <= 6);

            if (!x.status.equalsIgnoreCase("COMPLETE")) {
                assertEquals((int) y.rating, -1);
            }
        }
    }
}
