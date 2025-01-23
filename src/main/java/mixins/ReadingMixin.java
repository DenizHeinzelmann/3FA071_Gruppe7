// src/main/java/mixins/ReadingMixin.java

package mixins;

import com.fasterxml.jackson.annotation.JsonProperty;
import interfaces.IReading;

import java.util.UUID;

public abstract class ReadingMixin implements IReading {

    @Override
    @JsonProperty("id")
    public abstract UUID getid();

    @Override
    @JsonProperty("id")
    public abstract void setid(UUID id);
}
