import org.softuni.broccolina.solet.BaseHttpSolet;
import org.softuni.broccolina.solet.HttpSoletRequest;
import org.softuni.broccolina.solet.HttpSoletResponse;
import org.softuni.broccolina.solet.SoletConfig;
import org.softuni.javache.http.HttpStatus;

public class HomeSolet extends BaseHttpSolet {
    protected HomeSolet(SoletConfig soletConfig) {
        super(soletConfig);
    }

    @Override
    protected void doGet(HttpSoletRequest request, HttpSoletResponse response) {
        response.setStatusCode(HttpStatus.OK);

        response.addHeader("Content-Type", "text/html");

        response.setContent("<h1>Hi, from Home Solet!</h1>".getBytes());
    }
}
