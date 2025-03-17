package farming.api.constants;

public interface CustomerApiConstants {
    String BASE_PATH = "/api/customer";
    String ME = BASE_PATH + "/me";
    String BY_ID = BASE_PATH + "/{customerId}";
    String ALL = BASE_PATH + "/all";
    String UPDATE = BASE_PATH + "/{customerId}";
    String DELETE = BASE_PATH + "/{customerId}";
    String TOP_UP = BASE_PATH + "/{customerId}/top-up";
}