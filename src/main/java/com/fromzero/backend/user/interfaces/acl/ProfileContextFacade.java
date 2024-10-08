package com.fromzero.backend.user.interfaces.acl;

import com.fromzero.backend.user.domain.model.aggregates.Developer;
import com.fromzero.backend.user.domain.model.aggregates.Enterprise;
import com.fromzero.backend.user.domain.model.commands.UpdateDeveloperCompletedProjectsCommand;
import com.fromzero.backend.user.domain.model.queries.GetDeveloperByIdQuery;
import com.fromzero.backend.user.domain.model.queries.GetDeveloperByUserIdAsyncQuery;
import com.fromzero.backend.user.domain.model.queries.GetEnterpriseByUserIdAsyncQuery;
import com.fromzero.backend.user.domain.services.DeveloperCommandService;
import com.fromzero.backend.user.domain.services.DeveloperQueryService;
import com.fromzero.backend.user.domain.services.EnterpriseQueryService;
import org.springframework.stereotype.Service;

@Service
public class ProfileContextFacade {
    private final DeveloperQueryService developerQueryService;
    private final DeveloperCommandService developerCommandService;
    private final EnterpriseQueryService enterpriseQueryService;

    public ProfileContextFacade(DeveloperQueryService developerQueryService, DeveloperCommandService developerCommandService, EnterpriseQueryService enterpriseQueryService) {
        this.developerQueryService = developerQueryService;
        this.developerCommandService = developerCommandService;
        this.enterpriseQueryService = enterpriseQueryService;
    }
    public Developer getDeveloperByUserId(Long id){
        var getDeveloperByUserIdQuery = new GetDeveloperByUserIdAsyncQuery(id);
        var developer = this.developerQueryService.handle(getDeveloperByUserIdQuery);
        return developer.orElse(null);
    }

    public Enterprise getEnterpriseByUserId(Long id){
        var getEnterpriseByUserIdQuery = new GetEnterpriseByUserIdAsyncQuery(id);
        var enterprise = this.enterpriseQueryService.handle(getEnterpriseByUserIdQuery);
        return enterprise.orElse(null);
    }

    public void updateDeveloperCompletedProjects(Long developerId){
        var developer = developerQueryService.handle(new GetDeveloperByIdQuery(developerId));
        if (developer.isEmpty())return;

        var count = developer.get().getCompletedProjects();
        var command = new UpdateDeveloperCompletedProjectsCommand(developer.get(),count+1);
        var updatedDeveloper = this.developerCommandService.handle(command);

    }
}
