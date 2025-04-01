package farming.api;

public interface FarmerApi {
     String BASE = "/api/farmer";
     String ME = "/me";
     String BY_ID = "/{farmerId}";
     String ALL = "/all";
     String UPDATE = "/{farmerId}";
     String DELETE = "/{farmerId}";
     String BY_PRODUCT = "/byProduct/{productItemId}";
     String TEST = "/test";
}