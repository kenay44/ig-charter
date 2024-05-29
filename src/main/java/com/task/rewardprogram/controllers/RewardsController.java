package com.task.rewardprogram.controllers;

import com.task.rewardprogram.dto.CustomerPoints;
import com.task.rewardprogram.services.RewardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

@RestController()
@RequestMapping("/api/rewards")
public class RewardsController {

    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    @GetMapping
    @Operation(description = "Endpoint providing calculation of reward points based on transactions made by customers.",
            parameters = {
                    @Parameter(name = "customerId", description = "Optional - if present calculation takes place only for requested customer"),
                    @Parameter(name = "endMonth", required = true, example = "2024-04",
                            description = "End month of the 3 months period for which reward points calculation is being requested. " +
                                    "The month is included in the 3 months period. For the example parameter of 2024-04 calculation " +
                                    "will include months 02, 03 and 04 of 2024 year"
                    )})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned calculation results."),
            @ApiResponse(responseCode = "400", description = "Bad request - missing or incorrect end date format",
                    content = @Content)
    })
    public ResponseEntity<List<CustomerPoints>> getPointsCalculations(@RequestParam("customerId") @Nullable Long customerId,
                                                                      @NotNull @DateTimeFormat(pattern = "yyyy-MM")
                                                                      @RequestParam("endMonth") YearMonth endMonthForPointsCalculation) {

        if (forSelectedCustomerOnly(customerId)) {
            return ResponseEntity.ok(rewardsService.calculateForSingleCustomer(customerId, endMonthForPointsCalculation));
        }
        return ResponseEntity.ok(rewardsService.calculateForAllCustomers(endMonthForPointsCalculation));
    }

    private boolean forSelectedCustomerOnly(Long customerId) {
        return Objects.nonNull(customerId);
    }
}
