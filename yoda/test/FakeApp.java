import java.io.IOException;
import org.junit.*;
import java.util.*;
import play.test.FakeApplication;
import play.test.Helpers;
import play.mvc.Http;


public abstract class FakeApp {
    public static FakeApplication app;

    @BeforeClass
    public static void startApp() throws IOException {
        app = Helpers.fakeApplication();
        Helpers.start(app);
        //tip: Fixtures.loadAll(); with @BeforeClass in inherited class
    }

    @AfterClass
    public static void stopApp() {
        Helpers.stop(app);
    }
}
