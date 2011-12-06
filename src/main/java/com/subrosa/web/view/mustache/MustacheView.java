package com.subrosa.web.view.mustache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

public class MustacheView extends org.springframework.web.servlet.view.mustache.MustacheView {
    private static final String CSS_RESOURCE_PATH = "subrosa/static/css/";
    private static final String JS_RESOURCE_PATH = "subrosa/static/js/";

    //@TODO: add header, footer, and other partials to instance variables?
    private List<String> cssRequirements = new ArrayList<String>();
    private List<String> jsRequirements = new  ArrayList<String>();

    public MustacheView() {
        //@TODO: update these with actual "base" CSS/JSS requirements.
        this.cssRequirements.add("style.css");
        this.jsRequirements.add("jquery.min.js");
    }

    protected MustacheView(List<String> cssRequirements, List<String> jsRequirements) {
        this.cssRequirements = cssRequirements;
        this.jsRequirements = jsRequirements;
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {

        model.put("js", this.getJsRequirementsString());
        model.put("css", this.getCssRequirementsString());
        super.renderMergedTemplateModel(model, request, response);
    }

    protected String buildRequirementString(List<String> requirementArray) {
        StringBuilder sb = new StringBuilder();
        int end = requirementArray.size() - 1;
        sb.append(StringUtils.join(Collections2.transform(requirementArray.subList(0, end), new Function<String, String>() {
            @Override
            public String apply(String from) {
                return FilenameUtils.removeExtension(from);
            }
        }), ","));
        sb.append(",");
        sb.append(requirementArray.get(end));
        return sb.toString();
    }

    public void addCssRequirement(String requirement) {
        this.cssRequirements.add(requirement);
    }

    public void addJsRequirement(String requirement) {
        this.jsRequirements.add(requirement);
    }

    public String getCssRequirementsString() {
        return CSS_RESOURCE_PATH + buildRequirementString(getCssRequirements());
    }

    protected List<String> getCssRequirements() {
        return cssRequirements;
    }

    public void setCssRequirements(List<String> cssRequirements) {
        this.cssRequirements = cssRequirements;
    }

    public String getJsRequirementsString() {
        return JS_RESOURCE_PATH + buildRequirementString(getJsRequirements());
    }

    protected List<String> getJsRequirements() {
        return jsRequirements;
    }

    public void setJsRequirements(List<String> jsRequirements) {
        this.jsRequirements = jsRequirements;
    }
}
