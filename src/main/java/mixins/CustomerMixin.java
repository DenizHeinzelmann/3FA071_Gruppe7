// src/main/java/mixins/CustomerMixin.java

package mixins;

import com.fasterxml.jackson.annotation.JsonProperty;
import interfaces.ICustomer;

import java.util.UUID;

public abstract class CustomerMixin implements ICustomer {

    @Override
    @JsonProperty("id")
    public abstract UUID getid();

    @Override
    @JsonProperty("id")
    public abstract void setid(UUID id);
}
