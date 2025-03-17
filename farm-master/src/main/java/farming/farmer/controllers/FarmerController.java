//package farming.farmer.controllers;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Set;
//
//import farming.accounting.entity.UserAccount;
//import farming.farmer.dto.FarmerDto;
//import farming.farmer.entity.Farmer;
//import farming.farmer.repo.FarmerRepository;
//import farming.farmer.service.FarmerService;
//
//
//@RestController
//@RequestMapping("/api/farmer")
//public class FarmerController {
//
//    private final FarmerService farmerService;
//    private final FarmerRepository farmerRepo;
//
//    public FarmerController(FarmerService farmerService, FarmerRepository farmerRepo) {
//        this.farmerService = farmerService;
//        this.farmerRepo = farmerRepo;
//    }
//
//    @GetMapping("/{farmerId}")
//    public ResponseEntity<FarmerDto> getFarmer(@PathVariable Long farmerId) {
//        return ResponseEntity.ok(farmerService.getFarmer(farmerId));
//    }
//
//    @GetMapping("/by-product/{productId}")
//    public ResponseEntity<Set<FarmerDto>> getFarmersByProduct(@PathVariable Long productId) {
//        return ResponseEntity.ok(farmerService.getFarmersByProduct(productId));
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<List<FarmerDto>> getAllFarmers() {
//        return ResponseEntity.ok(farmerService.getAllFarmers());
//    }
//
//    @PutMapping("/{farmerId}")
//    public ResponseEntity<FarmerDto> updateFarmer(@PathVariable Long farmerId, @RequestBody FarmerDto dto) {
//        return ResponseEntity.ok(farmerService.updateFarmer(farmerId, dto));
//    }
//
//    @DeleteMapping("/{farmerId}")
//    public ResponseEntity<Void> deleteFarmer(@PathVariable Long farmerId) {
//        farmerService.deleteFarmer(farmerId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PostMapping("/{farmerId}/products/{productId}")
//    public ResponseEntity<Void> addProductToFarmer(@PathVariable Long farmerId, @PathVariable Long productId) {
//        farmerService.addProductToFarmer(farmerId, productId);
//        return ResponseEntity.ok().build();
//    }
//
//    @DeleteMapping("/{farmerId}/products/{productId}")
//    public ResponseEntity<Void> removeProductFromFarmer(@PathVariable Long farmerId, @PathVariable Long productId) {
//        farmerService.removeProductFromFarmer(farmerId, productId);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/{farmerId}/balance")
//    public ResponseEntity<Double> getFarmerBalance(@PathVariable Long farmerId) {
//        return ResponseEntity.ok(farmerService.getFarmerBalance(farmerId));
//    }
//
//    @GetMapping("/me")
//    public ResponseEntity<FarmerDto> getCurrentFarmer(@AuthenticationPrincipal UserAccount user) {
//    	if (user == null) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
//        }
//        Farmer farmer = farmerRepo.findByUserAccountLogin(user.getLogin())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
//                		"Farmer profile not found for user: " + user.getLogin()));
//        return ResponseEntity.ok(farmer.build());
//    }
//}

package farming.farmer.controllers;

import farming.farmer.dto.FarmerDto;
import farming.farmer.entity.Farmer;
import farming.farmer.repo.FarmerRepository;
import farming.farmer.service.FarmerService;
import farming.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/farmer")
@Slf4j
public class FarmerController {

    private final FarmerService farmerService;
    private final FarmerRepository farmerRepo;

    public FarmerController(FarmerService farmerService, FarmerRepository farmerRepo) {
        this.farmerService = farmerService;
        this.farmerRepo = farmerRepo;
    }

    @GetMapping("/{farmerId}")
    public ResponseEntity<FarmerDto> getFarmer(@PathVariable Long farmerId) {
        log.info("Request to get farmer with ID: {}", farmerId);
        FarmerDto farmerDto = farmerService.getFarmer(farmerId);
        log.debug("Returning farmer: {}", farmerDto);
        return ResponseEntity.ok(farmerDto);
    }

    @GetMapping("/by-product/{productId}")
    public ResponseEntity<FarmerDto> getFarmersByProduct(@PathVariable Long productId) {
        log.info("Request to get farmer by product ID: {}", productId);
        FarmerDto farmers = farmerService.getFarmerByProduct(productId);
        log.debug("Returning {} farmer for product ID: {}", farmers, productId);
        return ResponseEntity.ok(farmers);
    }

    @GetMapping("/all")
    public ResponseEntity<List<FarmerDto>> getAllFarmers() {
        log.info("Request to get all farmers");
        List<FarmerDto> farmers = farmerService.getAllFarmers();
        log.debug("Returning {} farmers", farmers.size());
        return ResponseEntity.ok(farmers);
    }

    @PutMapping("/{farmerId}")
    public ResponseEntity<FarmerDto> updateFarmer(@PathVariable Long farmerId, @RequestBody FarmerDto dto) {
        log.info("Request to update farmer with ID: {}", farmerId);
        FarmerDto updatedFarmer = farmerService.updateFarmer(farmerId, dto);
        log.debug("Farmer updated: {}", updatedFarmer);
        return ResponseEntity.ok(updatedFarmer);
    }

    @DeleteMapping("/{farmerId}")
    public ResponseEntity<Void> deleteFarmer(@PathVariable Long farmerId) {
        log.info("Request to delete farmer with ID: {}", farmerId);
        farmerService.deleteFarmer(farmerId);
        log.info("Farmer deleted with ID: {}", farmerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{farmerId}/products/{productId}")
    public ResponseEntity<Void> addProductToFarmer(@PathVariable Long farmerId, @PathVariable Long productId) {
        log.info("Request to add product ID {} to farmer ID {}", productId, farmerId);
        farmerService.addProductToFarmer(farmerId, productId);
        log.info("Product added successfully");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{farmerId}/products/{productId}")
    public ResponseEntity<Void> removeProductFromFarmer(@PathVariable Long farmerId, @PathVariable Long productId) {
        log.info("Request to remove product ID {} from farmer ID {}", productId, farmerId);
        farmerService.removeProductFromFarmer(farmerId, productId);
        log.info("Product removed successfully");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{farmerId}/balance")
    public ResponseEntity<Double> getFarmerBalance(@PathVariable Long farmerId) {
        log.info("Request to get balance for farmer ID: {}", farmerId);
        Double balance = farmerService.getFarmerBalance(farmerId);
        log.debug("Balance for farmer ID {}: {}", farmerId, balance);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/me")
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