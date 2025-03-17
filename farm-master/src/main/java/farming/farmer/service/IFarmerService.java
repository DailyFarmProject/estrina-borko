package farming.farmer.service;

import farming.farmer.dto.FarmerDto;
import farming.user.entity.User;

import java.util.List;

public interface IFarmerService {

    FarmerDto getFarmer(Long farmerId);

    FarmerDto getFarmerByProduct(Long productId);

    List<FarmerDto> getAllFarmers();

    void createFarmerProfile(User user);

    FarmerDto updateFarmer(Long farmerId, FarmerDto dto);

    void deleteFarmer(Long farmerId);

    void addProductToFarmer(Long farmerId, Long productId);

    void removeProductFromFarmer(Long farmerId, Long productId);

    Double getFarmerBalance(Long farmerId);

}
