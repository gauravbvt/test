
package com.analystdb;

import java.util.List;
import com.analystdb.data.Approach;
import com.analystdb.data.Flow;
import com.analystdb.data.Issue;
import com.analystdb.data.IssueComment;
import com.analystdb.data.output.ApproachIssueCountRtnType;
import com.analystdb.data.output.ApproachIssuesRtnType;
import com.analystdb.data.output.DocumentCategoryIssueCountsRtnType;
import com.analystdb.data.output.DocumentIssueCountRtnType;
import com.analystdb.data.output.DocumentIssuesRtnType;
import com.analystdb.data.output.InterviewIssuesRtnType;
import com.analystdb.data.output.IssueCategoryCountsRtnType;
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
 *  01/25/2013 16:28:55
 * 
 */
@SuppressWarnings("unchecked")
public class AnalystDB
    implements DataServiceManagerAccess, LiveDataService
{

    private DataServiceManager dsMgr;
    private TaskManager taskMgr;

    public List<ResourceValuesRtnType> resourceValues(Long project, String keyword, PagingOptions pagingOptions) {
        return ((List<ResourceValuesRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.resourceValuesQueryName), project, keyword, pagingOptions));
    }

    public List<ProjectPlansRtnType> projectPlans(Long project, PagingOptions pagingOptions) {
        return ((List<ProjectPlansRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.projectPlansQueryName), project, pagingOptions));
    }

    public List<IssueCategoryCountsRtnType> issueCategoryCounts(Long project, PagingOptions pagingOptions) {
        return ((List<IssueCategoryCountsRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.issueCategoryCountsQueryName), project, pagingOptions));
    }

    public List<DocumentIssueCountRtnType> documentIssueCount(Long project, Integer category, PagingOptions pagingOptions) {
        return ((List<DocumentIssueCountRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.documentIssueCountQueryName), project, category, pagingOptions));
    }

    public List<IssueComment> otherFlowIssues(Long flow, Long project, PagingOptions pagingOptions) {
        return ((List<IssueComment> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.otherFlowIssuesQueryName), flow, project, pagingOptions));
    }

    public List<Approach> availableApproaches(Long issue, Long project, PagingOptions pagingOptions) {
        return ((List<Approach> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.availableApproachesQueryName), issue, project, pagingOptions));
    }

    public List<DocumentCategoryIssueCountsRtnType> documentCategoryIssueCounts(Long project, PagingOptions pagingOptions) {
        return ((List<DocumentCategoryIssueCountsRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.documentCategoryIssueCountsQueryName), project, pagingOptions));
    }

    public List<InterviewIssuesRtnType> interviewIssues(Long resource, PagingOptions pagingOptions) {
        return ((List<InterviewIssuesRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.interviewIssuesQueryName), resource, pagingOptions));
    }

    public List<ApproachIssueCountRtnType> approachIssueCount(Long project, PagingOptions pagingOptions) {
        return ((List<ApproachIssueCountRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.approachIssueCountQueryName), project, pagingOptions));
    }

    public List<Issue> otherApproachIssues(Long approach, Long project, PagingOptions pagingOptions) {
        return ((List<Issue> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.otherApproachIssuesQueryName), approach, project, pagingOptions));
    }

    public List<RecentInterviewsRtnType> recentInterviews(Long project, PagingOptions pagingOptions) {
        return ((List<RecentInterviewsRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.recentInterviewsQueryName), project, pagingOptions));
    }

    public List<ApproachIssuesRtnType> approachIssues(Long project, Long approach, PagingOptions pagingOptions) {
        return ((List<ApproachIssuesRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.approachIssuesQueryName), project, approach, pagingOptions));
    }

    public List<DocumentIssuesRtnType> documentIssues(Long project, Integer document, PagingOptions pagingOptions) {
        return ((List<DocumentIssuesRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.documentIssuesQueryName), project, document, pagingOptions));
    }

    public List<UpcomingInterviewsRtnType> upcomingInterviews(Long project, PagingOptions pagingOptions) {
        return ((List<UpcomingInterviewsRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.upcomingInterviewsQueryName), project, pagingOptions));
    }

    public List<ResourceKeywordsRtnType> resourceKeywords(Long project, PagingOptions pagingOptions) {
        return ((List<ResourceKeywordsRtnType> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.resourceKeywordsQueryName), project, pagingOptions));
    }

    public List<Flow> flowsByProject(Long project, PagingOptions pagingOptions) {
        return ((List<Flow> ) dsMgr.invoke(taskMgr.getQueryTask(), (AnalystDBConstants.flowsByProjectQueryName), project, pagingOptions));
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
