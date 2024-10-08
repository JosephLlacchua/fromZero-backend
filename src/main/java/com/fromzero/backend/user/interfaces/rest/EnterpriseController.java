package com.fromzero.backend.user.interfaces.rest;

import com.fromzero.backend.user.domain.model.queries.GetAllEnterprisesAsyncQuery;
import com.fromzero.backend.user.domain.model.queries.GetEnterpriseByIdQuery;
import com.fromzero.backend.user.domain.services.EnterpriseCommandService;
import com.fromzero.backend.user.domain.services.EnterpriseQueryService;
import com.fromzero.backend.user.interfaces.rest.resources.EnterpriseResource;
import com.fromzero.backend.user.interfaces.rest.resources.UpdateEnterpriseResource;
import com.fromzero.backend.user.interfaces.rest.transform.EnterpriseResourceFromEntityAssembler;
import com.fromzero.backend.user.interfaces.rest.transform.UpdateEnterpriseCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enterprises")
@Tag(name = "Enterprises", description = "Enterprise Management Endpoints")
public class EnterpriseController {
    private final EnterpriseQueryService enterpriseQueryService;
    private final EnterpriseCommandService enterpriseCommandService;

    public EnterpriseController(EnterpriseQueryService enterpriseQueryService, EnterpriseCommandService enterpriseCommandService) {
        this.enterpriseQueryService = enterpriseQueryService;
        this.enterpriseCommandService = enterpriseCommandService;
    }

    @GetMapping
    public ResponseEntity<List<EnterpriseResource>> getAllEnterprises() {
        var getAllEnterprisesQuery = new GetAllEnterprisesAsyncQuery();
        var enterprises = enterpriseQueryService.handle(getAllEnterprisesQuery);
        var enterpriseResources = enterprises.stream().map(EnterpriseResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(enterpriseResources);
    }

    @GetMapping(value = "/{enterpriseId}")
    public ResponseEntity<EnterpriseResource> getEnterpriseById(@PathVariable Long enterpriseId) {
        var getEnterpriseByIdQuery = new GetEnterpriseByIdQuery(enterpriseId);
        var enterprise = enterpriseQueryService.handle(getEnterpriseByIdQuery);
        if (enterprise.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var enterpriseResource = EnterpriseResourceFromEntityAssembler.toResourceFromEntity(enterprise.get());
        return ResponseEntity.ok(enterpriseResource);
    }

    @PutMapping(value = "/{enterpriseId}")
    public ResponseEntity<EnterpriseResource> updateEnterprise(@PathVariable Long enterpriseId, @RequestBody UpdateEnterpriseResource resource) {
        var updateEnterpriseCommand = UpdateEnterpriseCommandFromResourceAssembler.toCommandFromResource(enterpriseId, resource);
        var updatedEnterprise = enterpriseCommandService.handle(updateEnterpriseCommand);
        if (updatedEnterprise.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var enterpriseResourceUpdated = EnterpriseResourceFromEntityAssembler.toResourceFromEntity(updatedEnterprise.get());
        return ResponseEntity.ok(enterpriseResourceUpdated);

    }


}
