package farming.api.constants;

public interface FarmerApiConstants {
    String BASE_PATH = "/api/farmer";
    String ME = BASE_PATH + "/me";
    String BY_ID = BASE_PATH + "/{farmerId}";
    String BY_PRODUCT = BASE_PATH + "/by-product/{productId}";
    String ALL = BASE_PATH + "/all";
    String UPDATE = BASE_PATH + "/{farmerId}";
    String DELETE = BASE_PATH + "/{farmerId}";
    String ADD_PRODUCT = BASE_PATH + "/{farmerId}/products/{productId}";
    String REMOVE_PRODUCT = BASE_PATH + "/{farmerId}/products/{productId}";
    String BALANCE = BASE_PATH + "/{farmerId}/balance";
}