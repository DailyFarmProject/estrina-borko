package farming.farmer.controllers;

import farming.accounting.entity.UserAccount;
import farming.api.FarmerApi;
import farming.farmer.dto.FarmerDto;
import farming.farmer.entity.Farmer;
import farming.farmer.repo.FarmerRepository;
import farming.farmer.service.FarmerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(FarmerApi.BASE)
@Slf4j
public class FarmerController {

    private final FarmerService farmerService;
    private final FarmerRepository farmerRepo;

    public FarmerController(FarmerService farmerService, FarmerRepository farmerRepo) {
        this.farmerService = farmerService;
        this.farmerRepo = farmerRepo;
    }

    @GetMapping(FarmerApi.BY_ID)
    public ResponseEntity<FarmerDto> getFarmer(@PathVariable UUID farmerId) {
        log.info("Request to get farmer with ID: {}", farmerId);
        FarmerDto farmerDto = farmerService.getFarmer(farmerId);
        log.debug("Returning farmer: {}", farmerDto);
        return ResponseEntity.ok(farmerDto);
    }

    @GetMapping(FarmerApi.BY_PRODUCT)
    public ResponseEntity<FarmerDto> getFarmersByProduct(@PathVariable UUID productItemId) {
        log.info("Fetching farmer for product ID: {}", productItemId);
        FarmerDto farmerDto = farmerService.getFarmersByProduct(productItemId);
        return ResponseEntity.ok(farmerDto);
    }

    @GetMapping(FarmerApi.ALL)
    public ResponseEntity<List<FarmerDto>> getAllFarmers() {
        log.info("Request to get all farmers");
        List<FarmerDto> farmers = farmerService.getAllFarmers();
        log.debug("Returning {} farmers", farmers.size());
        return ResponseEntity.ok(farmers);
    }

    @PutMapping(FarmerApi.UPDATE)
    public ResponseEntity<FarmerDto> updateFarmer(@PathVariable UUID farmerId, @RequestBody FarmerDto dto) {
        log.info("Request to update farmer with ID: {}", farmerId);
        FarmerDto updatedFarmer = farmerService.updateFarmer(farmerId, dto);
        log.debug("Farmer updated: {}", updatedFarmer);
        return ResponseEntity.ok(updatedFarmer);
    }

    @DeleteMapping(FarmerApi.DELETE)
    public ResponseEntity<Void> deleteFarmer(@PathVariable UUID farmerId) {
        log.info("Request to delete farmer with ID: {}", farmerId);
        farmerService.deleteFarmer(farmerId);
        log.info("Farmer deleted with ID: {}", farmerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(FarmerApi.ME)
    public ResponseEntity<FarmerDto> getCurrentFarmer(@AuthenticationPrincipal UserAccount user) {
        log.info("Fetching current farmer for user: {}", user != null ? user.getLogin() : "null");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        Farmer farmer = farmerRepo.findByLogin(user.getLogin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Farmer profile not found for user: " + user.getLogin()));
        return ResponseEntity.ok(farmer.toDto()); // Используем toDto вместо build
    }
}