package farming.farmer.controllers;

import farming.api.constants.FarmerApiConstants;
import farming.farmer.dto.FarmerDto;
import farming.farmer.entity.Farmer;
import farming.farmer.repo.FarmerRepository;
import farming.farmer.service.FarmerService;
import farming.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(FarmerApiConstants.BASE_PATH)
@Slf4j
public class FarmerController {

    private final FarmerService farmerService;
    private final FarmerRepository farmerRepo;

    public FarmerController(FarmerService farmerService, FarmerRepository farmerRepo) {
        this.farmerService = farmerService;
        this.farmerRepo = farmerRepo;
    }

    @GetMapping(FarmerApiConstants.BY_ID)
    public ResponseEntity<FarmerDto> getFarmer(@PathVariable Long farmerId) {
        log.info("Request to get farmer with ID: {}", farmerId);
        FarmerDto farmerDto = farmerService.getFarmer(farmerId);
        log.debug("Returning farmer: {}", farmerDto);
        return ResponseEntity.ok(farmerDto);
    }

    @GetMapping(FarmerApiConstants.BY_PRODUCT)
    public ResponseEntity<FarmerDto> getFarmersByProduct(@PathVariable Long productId) {
        log.info("Request to get farmer by product ID: {}", productId);
        FarmerDto farmers = farmerService.getFarmerByProduct(productId);
        log.debug("Returning {} farmer for product ID: {}", farmers, productId);
        return ResponseEntity.ok(farmers);
    }

    @GetMapping(FarmerApiConstants.ALL)
    public ResponseEntity<List<FarmerDto>> getAllFarmers() {
        log.info("Request to get all farmers");
        List<FarmerDto> farmers = farmerService.getAllFarmers();
        log.debug("Returning {} farmers", farmers.size());
        return ResponseEntity.ok(farmers);
    }

    @PutMapping(FarmerApiConstants.UPDATE)
    public ResponseEntity<FarmerDto> updateFarmer(@PathVariable Long farmerId, @RequestBody FarmerDto dto) {
        log.info("Request to update farmer with ID: {}", farmerId);
        FarmerDto updatedFarmer = farmerService.updateFarmer(farmerId, dto);
        log.debug("Farmer updated: {}", updatedFarmer);
        return ResponseEntity.ok(updatedFarmer);
    }

    @DeleteMapping(FarmerApiConstants.DELETE)
    public ResponseEntity<Void> deleteFarmer(@PathVariable Long farmerId) {
        log.info("Request to delete farmer with ID: {}", farmerId);
        farmerService.deleteFarmer(farmerId);
        log.info("Farmer deleted with ID: {}", farmerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(FarmerApiConstants.ADD_PRODUCT)
    public ResponseEntity<Void> addProductToFarmer(@PathVariable Long farmerId, @PathVariable Long productId) {
        log.info("Request to add product ID {} to farmer ID {}", productId, farmerId);
        farmerService.addProductToFarmer(farmerId, productId);
        log.info("Product added successfully");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(FarmerApiConstants.REMOVE_PRODUCT)
    public ResponseEntity<Void> removeProductFromFarmer(@PathVariable Long farmerId, @PathVariable Long productId) {
        log.info("Request to remove product ID {} from farmer ID {}", productId, farmerId);
        farmerService.removeProductFromFarmer(farmerId, productId);
        log.info("Product removed successfully");
        return ResponseEntity.ok().build();
    }

    @GetMapping(FarmerApiConstants.BALANCE)
    public ResponseEntity<Double> getFarmerBalance(@PathVariable Long farmerId) {
        log.info("Request to get balance for farmer ID: {}", farmerId);
        Double balance = farmerService.getFarmerBalance(farmerId);
        log.debug("Balance for farmer ID {}: {}", farmerId, balance);
        return ResponseEntity.ok(balance);
    }

    @GetMapping(FarmerApiConstants.ME)
    public ResponseEntity<FarmerDto> getCurrentFarmer(@AuthenticationPrincipal User user) {
        log.info("Fetching current farmer for user: {}", user != null ? user.getEmail() : "null");
        if (user == null) {
            log.error("User not authenticated");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        Farmer farmer = farmerRepo.findByUserAccountEmail(user.getEmail())
                .orElseThrow(() -> {
                    log.error("Farmer profile not found for user: {}", user.getEmail());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Farmer profile not found for user: " + user.getEmail());
                });
        log.debug("Current farmer found: {}", farmer);
        return ResponseEntity.ok(farmer.build());
    }
}