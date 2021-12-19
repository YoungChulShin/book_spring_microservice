package api.event;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Event<K, T> {

  public enum Type {CREATE, DELETE}

  private Event.Type eventType;
  private K key;
  private T data;
  private LocalDateTime eventCreatedAt;

  public Event(Type eventType, K key, T data) {
    this.eventType = eventType;
    this.key = key;
    this.data = data;
    this.eventCreatedAt = LocalDateTime.now();
  }
}
