package farming.farmer.service;

import farming.farmer.dto.FarmerDto;
import farming.farmer.entity.Address;
import farming.farmer.entity.Farmer;
import farming.farmer.repo.FarmerRepository;
import farming.products.entity.ProductItem;
import farming.products.repo.ProductItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FarmerService implements IFarmerService {

    private final FarmerRepository farmerRepo;
    private final ProductItemRepository productItemRepo;

    public FarmerService(FarmerRepository farmerRepo, ProductItemRepository productItemRepo) {
        this.farmerRepo = farmerRepo;
        this.productItemRepo = productItemRepo;
    }

    @Override
    public FarmerDto getFarmer(UUID farmerId) {
        log.info("Fetching farmer with ID: {}", farmerId);
        return farmerRepo.findById(farmerId)
                .map(Farmer::toDto)
                .orElseThrow(() -> {
                    log.error("Farmer not found with ID: {}", farmerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
                });
    }

    @Override
    public FarmerDto getFarmersByProduct(UUID productItemId) {
        log.info("Fetching farmer for product item ID: {}", productItemId);
        ProductItem productItem = productItemRepo.findById(productItemId)
                .orElseThrow(() -> {
                    log.error("Product item not found with ID: {}", productItemId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product item not found");
                });
        Farmer farmer = productItem.getFarmer();
        if (farmer == null) {
            log.error("No farmer associated with product item ID: {}", productItemId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No farmer associated with this product item");
        }
        return farmer.toDto();
    }

    @Override
    public List<FarmerDto> getAllFarmers() {
        log.info("Fetching all farmers");
        return farmerRepo.findAll().stream()
                .map(Farmer::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createFarmerProfile(Farmer farmer) {
        log.info("Creating farmer profile for user: {}", farmer.getLogin());
        if (farmerRepo.existsByLogin(farmer.getLogin())) {
            log.warn("Farmer profile already exists for login: {}", farmer.getLogin());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Farmer profile already exists for login: " + farmer.getLogin());
        }
        farmerRepo.save(farmer);
        log.info("Farmer profile created for login: {}", farmer.getLogin());
    }

    @Override
    @Transactional
    public FarmerDto updateFarmer(UUID farmerId, FarmerDto dto) {
        log.info("Updating farmer with ID: {}", farmerId);
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> {
                    log.error("Farmer not found with ID: {}", farmerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
                });
        if (dto.getPhone() != null) farmer.setPhone(dto.getPhone());
        if (dto.getAddress() != null) {
            farmer.setAddress(new Address(dto.getAddress().getCountry(), dto.getAddress().getCity(), dto.getAddress().getStreet()));
        }
        farmerRepo.save(farmer);
        log.info("Farmer updated successfully: {}", farmerId);
        return farmer.toDto();
    }

    @Override
    @Transactional
    public void deleteFarmer(UUID farmerId) {
        log.info("Deleting farmer with ID: {}", farmerId);
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> {
                    log.error("Farmer not found with ID: {}", farmerId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Farmer not found");
                });
        farmerRepo.delete(farmer);
        log.info("Farmer deleted: {}", farmerId);
    }
}