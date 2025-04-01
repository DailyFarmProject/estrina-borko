package farming.farmer.service;

import farming.farmer.dto.FarmerDto;
import farming.farmer.entity.Farmer;

import java.util.List;
import java.util.UUID;

public interface IFarmerService {
    FarmerDto getFarmer(UUID farmerId);
    FarmerDto getFarmersByProduct(UUID productId);
    List<FarmerDto> getAllFarmers();
    void createFarmerProfile(Farmer farmer);
    FarmerDto updateFarmer(UUID farmerId, FarmerDto dto);
    void deleteFarmer(UUID farmerId);
}