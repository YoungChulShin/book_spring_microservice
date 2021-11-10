package microservices.composite.product;

import api.event.Event;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsSameEvent extends TypeSafeMatcher<String> {

  private Event expectedEvent;

  private ObjectMapper mapper = new ObjectMapper();

  private IsSameEvent(Event expectedEvemt) {
    this.expectedEvent = expectedEvemt;
  }

  public static Matcher<String> sameEventExceptCreatedAt(Event expectedEvent) {
    return new IsSameEvent(expectedEvent);
  }

  @Override
  protected boolean matchesSafely(String item) {
    if (expectedEvent == null) {
      return false;
    }

    Map mapEvent = convertToStringMap(item);


    return false;
  }

  @Override
  public void describeTo(Description description) {

  }

  private Map convertToStringMap(String eventAsJson) {
    try {
      return mapper.readValue(eventAsJson, new TypeReference<HashMap>() {});
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
