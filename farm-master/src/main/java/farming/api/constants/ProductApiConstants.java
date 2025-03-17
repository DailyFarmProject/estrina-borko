package farming.api.constants;

public interface ProductApiConstants {
    String BASE_PATH = "/products";
    String ADD = BASE_PATH + "/add";
    String UPDATE = BASE_PATH + "/update";
    String BY_ID = BASE_PATH + "/{productId}";
    String BY_FARMER = BASE_PATH + "/byFarmer/{farmerId}";
    String PRICE_RANGE = BASE_PATH + "/priceRange";
    String ALL = BASE_PATH + "/all";
    String BUY = BASE_PATH + "/buy";
    String SOLD = BASE_PATH + "/sold/{farmerId}";
    String PURCHASED = BASE_PATH + "/purchased/{customerId}";
    String HISTORY = BASE_PATH + "/history/{farmerId}";
    String REMOVE = BASE_PATH + "/remove";
    String SURPRISE_BAG_BASE = BASE_PATH + "/surprise-bag";
    String SURPRISE_BAG_BUY = SURPRISE_BAG_BASE + "/buy";
    String SURPRISE_BAG_CREATE = SURPRISE_BAG_BASE + "/create";
    String SURPRISE_BAG_AVAILABLE = SURPRISE_BAG_BASE + "/available";
}