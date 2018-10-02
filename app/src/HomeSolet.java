import org.softuni.broccolina.solet.BaseHttpSolet;
import org.softuni.broccolina.solet.HttpSoletRequest;
import org.softuni.broccolina.solet.HttpSoletResponse;
import org.softuni.broccolina.solet.WebSolet;
import org.softuni.javache.http.HttpStatus;

@WebSolet(route = "/")
public class HomeSolet extends BaseHttpSolet {

    public HomeSolet() {
    }

    @Override
    protected void doGet(HttpSoletRequest request, HttpSoletResponse response) {
        response.setStatusCode(HttpStatus.OK);

        response.addHeader("Content-Type", "text/html");

        response.setContent((
                        "<head>"
                        + "<link rel=\"stylesheet\" href=\"bootstrap.min.css\"/>"
                        + "</head>"
                        + "<body>"
                        + "<h1>Hi, from Home Solet!</h1>"
                        + "<button class=\"btn btn-primary\">Click me!</button>"
                        + "</body>")
                .getBytes());
    }
}
