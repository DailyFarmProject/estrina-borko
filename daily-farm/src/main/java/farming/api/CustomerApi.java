package farming.api;

public interface CustomerApi {
     String BASE = "/api/customer";
     String ME = "/me";
     String BY_ID = "/{customerId}";
     String ALL = "/all";
     String UPDATE = "/{customerId}";
     String DELETE = "/{customerId}";
}