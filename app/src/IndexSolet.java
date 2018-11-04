import org.softuni.broccolina.solet.BaseHttpSolet;
import org.softuni.broccolina.solet.HttpSoletRequest;
import org.softuni.broccolina.solet.HttpSoletResponse;
import org.softuni.broccolina.solet.WebSolet;
import org.softuni.javache.http.HttpStatus;

@WebSolet(route = "/index")
public class IndexSolet extends BaseHttpSolet {
    @Override
    protected void doGet(HttpSoletRequest request, HttpSoletResponse response) {

        response.setStatusCode(HttpStatus.OK);

        response.addHeader("Content-Type", "text/html");

        response.setContent((
                "<head>"
                        + "<link rel=\"stylesheet\" href=\"bootstrap.min.css\"/>"
                        + "</head>"
                        + "<body>"
                        + "<h1>Hi, from IndexSolet!</h1>"
                        + "</body>")
                .getBytes());
    }
}
