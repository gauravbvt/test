
package com.analystdb;

import java.util.List;
import com.analystdb.data.IssueCategory;
import com.analystdb.data.IssueComment;
import com.analystdb.data.output.AllDocsWithIssuesRtnType;
import com.analystdb.data.output.AllIssuesRtnType;
import com.analystdb.data.output.ApproachIssueCountRtnType;
import com.analystdb.data.output.ApproachIssuesRtnType;
import com.analystdb.data.output.DocumentCategoryIssueCountsRtnType;
import com.analystdb.data.output.DocumentIssueCountRtnType;
import com.analystdb.data.output.DocumentIssuesRtnType;
import com.analystdb.data.output.InterviewIssuesRtnType;
import com.analystdb.data.output.IssueApproachesRtnType;
import com.analystdb.data.output.IssueCategoryCountsRtnType;
import com.analystdb.data.output.IssueFlowsRtnType;
import com.analystdb.data.output.IssuesByCategoryRtnType;
import com.analystdb.data.output.ProjectPlansRtnType;
import com.analystdb.data.output.RecentInterviewsRtnType;
import com.analystdb.data.output.ResourceKeywordsRtnType;
import com.analystdb.data.output.ResourceValuesRtnType;
import com.analystdb.data.output.UpcomingInterviewsRtnType;
import com.wavemaker.json.type.TypeDefinition;
import com.wavemaker.runtime.data.DataServiceManager;
import com.wavemaker.runtime.data.DataServiceManagerAccess;
import com.wavemaker.runtime.data.TaskManager;
import com.wavemaker.runtime.service.LiveDataService;
import com.wavemaker.runtime.service.PagingOptions;
import com.wavemaker.runtime.service.PropertyOptions;
import com.wavemaker.runtime.service.TypedServiceReturn;


/**
 *  Operations for service "analystDB"
 *  02/11/2013 20:47:54
 * 
 */
@SuppressWarnings("unchecked")
public class AnalystDB
    implements DataServiceManagerAccess, LiveDataService
{

    private DataServiceManager dsMgr;
    private TaskManager taskMgr;

    public List<AllIssuesRtnType> allIssues(Long project, PagingOptions pagingOptions) {
        return ((List<AllIssuesRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.allIssuesQueryName), project, pagingOptions));
    }

    public com.analystdb.data.FlowAttribute getFlowAttributeById(Long id, PagingOptions pagingOptions) {
        List<com.analystdb.data.FlowAttribute> rtn = ((List<com.analystdb.data.FlowAttribute> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.getFlowAttributeByIdQueryName), id, pagingOptions));
        if (rtn.isEmpty()) {
            return null;
        } else {
            return rtn.get(0);
        }
    }

    public List<ResourceValuesRtnType> resourceValues(Long project, String keyword, PagingOptions pagingOptions) {
        return ((List<ResourceValuesRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.resourceValuesQueryName), project, keyword, pagingOptions));
    }

    public List<IssueComment> otherFlowIssues(Long flow, Long project, PagingOptions pagingOptions) {
        return ((List<IssueComment> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.otherFlowIssuesQueryName), flow, project, pagingOptions));
    }

    public com.analystdb.data.output.MaxIssueSequenceRtnType maxIssueSequence(Long project, PagingOptions pagingOptions) {
        List<com.analystdb.data.output.MaxIssueSequenceRtnType> rtn = ((List<com.analystdb.data.output.MaxIssueSequenceRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.maxIssueSequenceQueryName), project, pagingOptions));
        if (rtn.isEmpty()) {
            return null;
        } else {
            return rtn.get(0);
        }
    }

    public List<IssueApproachesRtnType> issueApproaches(Long issue, PagingOptions pagingOptions) {
        return ((List<IssueApproachesRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.issueApproachesQueryName), issue, pagingOptions));
    }

    public List<IssuesByCategoryRtnType> issuesByCategory(Long project, Long category, PagingOptions pagingOptions) {
        return ((List<IssuesByCategoryRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.issuesByCategoryQueryName), project, category, pagingOptions));
    }

    public List<ApproachIssueCountRtnType> approachIssueCount(Long project, PagingOptions pagingOptions) {
        return ((List<ApproachIssueCountRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.approachIssueCountQueryName), project, pagingOptions));
    }

    public List<IssueCategory> otherCategories(Long project, Long issue, PagingOptions pagingOptions) {
        return ((List<IssueCategory> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.otherCategoriesQueryName), project, issue, pagingOptions));
    }

    public List<ApproachIssuesRtnType> approachIssues(Long project, Long approach, PagingOptions pagingOptions) {
        return ((List<ApproachIssuesRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.approachIssuesQueryName), project, approach, pagingOptions));
    }

    public List<UpcomingInterviewsRtnType> upcomingInterviews(Long project, PagingOptions pagingOptions) {
        return ((List<UpcomingInterviewsRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.upcomingInterviewsQueryName), project, pagingOptions));
    }

    public List<ResourceKeywordsRtnType> resourceKeywords(Long project, PagingOptions pagingOptions) {
        return ((List<ResourceKeywordsRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.resourceKeywordsQueryName), project, pagingOptions));
    }

    public List<IssueCategoryCountsRtnType> issueCategoryCounts(Long project, PagingOptions pagingOptions) {
        return ((List<IssueCategoryCountsRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.issueCategoryCountsQueryName), project, pagingOptions));
    }

    public List<ProjectPlansRtnType> projectPlans(Long project, PagingOptions pagingOptions) {
        return ((List<ProjectPlansRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.projectPlansQueryName), project, pagingOptions));
    }

    public List<com.analystdb.data.Approach> otherIssueApproaches(Long project, Long issue, PagingOptions pagingOptions) {
        return ((List<com.analystdb.data.Approach> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.otherIssueApproachesQueryName), project, issue, pagingOptions));
    }

    public List<DocumentIssueCountRtnType> documentIssueCount(Long project, Integer category, PagingOptions pagingOptions) {
        return ((List<DocumentIssueCountRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.documentIssueCountQueryName), project, category, pagingOptions));
    }

    public List<IssueFlowsRtnType> issueFlows(Long issue, PagingOptions pagingOptions) {
        return ((List<IssueFlowsRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.issueFlowsQueryName), issue, pagingOptions));
    }

    public List<com.analystdb.data.Approach> availableApproaches(Long issue, Long project, PagingOptions pagingOptions) {
        return ((List<com.analystdb.data.Approach> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.availableApproachesQueryName), issue, project, pagingOptions));
    }

    public List<com.analystdb.data.Flow> otherIssueFlows(Long project, Long issueComment, PagingOptions pagingOptions) {
        return ((List<com.analystdb.data.Flow> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.otherIssueFlowsQueryName), project, issueComment, pagingOptions));
    }

    public List<DocumentCategoryIssueCountsRtnType> documentCategoryIssueCounts(Long project, PagingOptions pagingOptions) {
        return ((List<DocumentCategoryIssueCountsRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.documentCategoryIssueCountsQueryName), project, pagingOptions));
    }

    public List<InterviewIssuesRtnType> interviewIssues(Long resource, PagingOptions pagingOptions) {
        return ((List<InterviewIssuesRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.interviewIssuesQueryName), resource, pagingOptions));
    }

    public List<com.analystdb.data.Issue> addressedIssues(Long project, PagingOptions pagingOptions) {
        return ((List<com.analystdb.data.Issue> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.addressedIssuesQueryName), project, pagingOptions));
    }

    public List<com.analystdb.data.Issue> otherApproachIssues(Long approach, Long project, PagingOptions pagingOptions) {
        return ((List<com.analystdb.data.Issue> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.otherApproachIssuesQueryName), approach, project, pagingOptions));
    }

    public List<RecentInterviewsRtnType> recentInterviews(Long project, PagingOptions pagingOptions) {
        return ((List<RecentInterviewsRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.recentInterviewsQueryName), project, pagingOptions));
    }

    public List<DocumentIssuesRtnType> documentIssues(Long project, Integer document, Integer phase, PagingOptions pagingOptions) {
        return ((List<DocumentIssuesRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.documentIssuesQueryName), project, document, phase, pagingOptions));
    }

    public List<AllDocsWithIssuesRtnType> allDocsWithIssues(Long project, PagingOptions pagingOptions) {
        return ((List<AllDocsWithIssuesRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.allDocsWithIssuesQueryName), project, pagingOptions));
    }

    public List<com.analystdb.data.Flow> flowsByProject(Long project, PagingOptions pagingOptions) {
        return ((List<com.analystdb.data.Flow> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.flowsByProjectQueryName), project, pagingOptions));
    }

    public Object insert(Object o) {
        return dsMgr.invoke(taskMgr.getInsertTask(), o);
    }

    public TypedServiceReturn read(TypeDefinition rootType, Object o, PropertyOptions propertyOptions, PagingOptions pagingOptions) {
        return ((TypedServiceReturn) dsMgr.invoke(taskMgr.getReadTask(), rootType, o, propertyOptions, pagingOptions));
    }

    public Object update(Object o) {
        return dsMgr.invoke(taskMgr.getUpdateTask(), o);
    }

    public void delete(Object o) {
        dsMgr.invoke(taskMgr.getDeleteTask(), o);
    }

    public void begin() {
        dsMgr.begin();
    }

    public void commit() {
        dsMgr.commit();
    }

    public void rollback() {
        dsMgr.rollback();
    }

    public DataServiceManager getDataServiceManager() {
        return dsMgr;
    }

    public void setDataServiceManager(DataServiceManager dsMgr) {
        this.dsMgr = dsMgr;
    }

    public TaskManager getTaskManager() {
        return taskMgr;
    }

    public void setTaskManager(TaskManager taskMgr) {
        this.taskMgr = taskMgr;
    }

}
