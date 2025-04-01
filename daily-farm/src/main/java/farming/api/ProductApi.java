package farming.api;

public interface ProductApi {
     String BASE = "/products";
     String ADD = "/add";
     String UPDATE = "/update";
     String BY_ID = "/{productItemId}";
     String BY_FARMER = "/byFarmer/{farmerId}";
     String PRICE_RANGE = "/priceRange";
     String ALL = "/all";
     String BUY = "/buy";
     String SOLD = "/sold/{farmerId}";
     String PURCHASED = "/purchased/{customerId}";
     String HISTORY = "/history/{farmerId}";
     String REMOVE = "/remove";
     String SURPRISE_BAG_BUY = "/surprise-bag/buy";
     String SURPRISE_BAG_CREATE = "/surprise-bag/create";
     String SURPRISE_BAG_AVAILABLE = "/surprise-bag/available";
}