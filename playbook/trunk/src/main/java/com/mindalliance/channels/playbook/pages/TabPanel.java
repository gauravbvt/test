package com.mindalliance.channels.playbook.pages;

import com.mindalliance.channels.playbook.ifm.Tab;
import com.mindalliance.channels.playbook.pages.filters.Filter;
import com.mindalliance.channels.playbook.pages.graphs.InfoFlowPanel;
import com.mindalliance.channels.playbook.pages.graphs.TimelinePanel;
import com.mindalliance.channels.playbook.pages.reports.DirectoryReportPage;
import com.mindalliance.channels.playbook.pages.reports.RSSTab;
import com.mindalliance.channels.playbook.pages.reports.ReportPage;
import com.mindalliance.channels.playbook.pages.reports.TBDReportPage;
import com.mindalliance.channels.playbook.ref.Ref;
import com.mindalliance.channels.playbook.support.models.ContainerSummary;
import com.mindalliance.channels.playbook.support.models.RefPropertyModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ...
 */
public class TabPanel extends Panel implements SelectionManager {

    private Ref selected;
    private List<ContentView> views;
    private int selectedView;
    private FormPanel form;

    public TabPanel(String id, IModel tabModel) {
        super(id, tabModel);
        setRenderBodyOnly(true);

        form = new FormPanel("content-form", new PropertyModel(this, "selected"));

        add(new FilterPanel("filter", new RefPropertyModel(tabModel, "filter")) {
            public void onFilterApplied(Filter f) {
                final Ref tabRef = getTabRef();

                tabRef.begin();

                Tab tab = (Tab) tabRef.deref();
                f.setContainer(tab.getBase());
                tab.setFilter(f);
                tab.changed("filter");
                tabRef.commit();

                // Force a recompute of the contents on
                // the subsequent refresh
//                TabPanel.this.detach();
                TabPanel.this.addOrReplace(createTabPanel());
            }

            public void onFilterSave(Filter filter) {
                TabPanel.this.onFilterSave(getTab(), filter);
            }
        });

        addLinks();
        add(createTabPanel());
        add(form);
    }

    private void addLinks() {
        Object[][] links = {
                {"Directory", "address_book2.png", DirectoryReportPage.class, "Resource directory for this tab"},
                {"Playbook", "branch_element.png", TBDReportPage.class, "Playbook report for this tab"},
                {"Issues", "flag_red.png", TBDReportPage.class, "Issues report for this tab"},
                {"RSS", "feed-icon-14x14.png", RSSTab.class, "RSS feed for changes to this tab"}
        };
        add(new DataView("links", new ListDataProvider(Arrays.asList(links))) {
            protected void populateItem(Item item) {
                Object[] details = (Object[]) item.getModelObject();
                PageParameters params = new PageParameters();
                params.put(ReportPage.REPORT_TAB_PARAM, getTab().getId());
                params.put(ReportPage.REPORT_MIMETYPE_PARAM, "xml");
                BookmarkablePageLink link = new BookmarkablePageLink("link", (Class<?>) details[2], new PageParameters(params));
                WebMarkupContainer image = new WebMarkupContainer("link-text");
                image.add(new AttributeModifier("src", new Model((String) details[1])));
                image.add(new AttributeModifier("alt", new Model((String) details[0])));
                image.add(new AttributeModifier("title", new Model((String) details[3])));
                link.add(image);
                item.add(link);
            }
        });
    }

    private TabbedPanel createTabPanel() {
        views = new ArrayList<ContentView>();
        TabbedPanel viewTabs = new TabbedPanel("content-views", createViewTabs()) {
            protected WebMarkupContainer newLink(String linkId, final int index) {
                return new Link(linkId) {
                    public void onClick() {
                        setSelectedView(index);
                        setSelectedTab(index);
                    }
                };
            }
        };
        viewTabs.setRenderBodyOnly(true);
        return viewTabs;
    }

    private List<AbstractTab> createViewTabs() {
        List<AbstractTab> result = new ArrayList<AbstractTab>();
        final IModel tabModel = new PropertyModel(this, "tab");

        result.add(new AbstractTab(new Model("Table")) {
            private TableView panel;

            public Panel getPanel(String panelId) {
                if (panel == null) {
                    panel = new TableView(panelId, tabModel, TabPanel.this);
                    panel.setSelected(getSelected());
                    add(panel);
                    views.add(panel);
                }
                return panel;
            }
        });

        ContainerSummary summary = getTab().getSummary();
        if (summary.isTimelineable()) {
            result.add(new AbstractTab(new Model("Timeline")) {
                private TimelinePanel panel;

                public Panel getPanel(String panelId) {
                    if (panel == null) {
                        panel = new TimelinePanel(panelId, tabModel, TabPanel.this);
                        views.add(panel);
                        add(panel);
                        assert (panel.getPage() != null);
                        panel.setSelected(getSelected());
                    }
                    return panel;
                }
            });
        }

        if (summary.isMappable()) {
            result.add(new AbstractTab(new Model("Map")) {
                private ContentView panel;

                public Panel getPanel(String panelId) {
                    // TODO hook this up
                    if (panel == null) {
                        panel = new ContentView(panelId, tabModel, TabPanel.this);
                        views.add(panel);
                        add(panel);
                        panel.setSelected(getSelected());
                    }
                    return panel;
                }
            });
        }

        if (summary.isFlowable()) {
            result.add(new AbstractTab(new Model("Flow")) {
                private InfoFlowPanel panel;

                public Panel getPanel(String panelId) {
                    // TODO filter to Agent.class or Event.class
                    if (panel == null) {
                        panel = new InfoFlowPanel(panelId, tabModel, TabPanel.this);
                        views.add(panel);
                        add(panel);
                        panel.setSelected(getSelected());
                    }
                    return panel;
                }
            });
        }

        return result;
    }

    protected void onFilterSave(Tab tab, Filter filter) {
    }

    public Ref getTabRef() {
        return (Ref) getModelObject();
    }

    public Tab getTab() {
        return (Tab) getTabRef().deref();
    }

    public void setTabRef(Ref ref) {
        setModelObject(ref);
    }

    public void doAjaxSelection(Ref ref, AjaxRequestTarget target) {
        if (this.selected != ref
                && (this.selected == null || !this.selected.equals(ref))) {

            setSelected(ref);
            target.addComponent(views.get(getSelectedView()));
            target.addComponent(form);
        }
    }

    public Ref getSelected() {
        return selected;
    }

    public void setSelected(Ref selected) {
        if (this.selected != selected
                && (this.selected == null || !this.selected.equals(selected))) {

            this.selected = selected;
            for (ContentView view : views)
//                if ( view.isVisible() )
                view.setSelected(selected);
            form.modelChanged();
        }
    }

    public int getSelectedView() {
        return selectedView;
    }

    public void setSelectedView(int selectedView) {
        this.selectedView = selectedView;
    }

    public void detachModels() {
        super.detachModels();
        for (ContentView v : views)
            v.detach();
    }
}
