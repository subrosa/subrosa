package com.subrosa.web.view.mustache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

public class MustacheView extends org.springframework.web.servlet.view.mustache.MustacheView {

    private static final String CSS_RESOURCE_PATH = "subrosa/static/css/";
    private static final String JS_RESOURCE_PATH = "subrosa/static/js/";

    private ArrayList<String> cssRequirements = new ArrayList<String>();
    private ArrayList<String> jsRequirements = new  ArrayList<String>();

    public MustacheView() {
        //@TODO: update these with actual "base" CSS/JSS requirements.
        this.cssRequirements.add("style.css");
        this.jsRequirements.add("jquery.min.js");
    }

    protected MustacheView(ArrayList<String> cssRequirements, ArrayList<String> jsRequirements) {
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

    protected String buildRequirementString(ArrayList<String> requirementArray) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<requirementArray.size(); i++) {
            String requirement = requirementArray.get(i);
            if (i != requirementArray.size()-1) {
                requirement = FilenameUtils.removeExtension(requirement);
            }
            if (i != 0) {
                sb.append(",");
            }
            sb.append(requirement);
        }
        return sb.toString();
    }

    public String getCssRequirementsString() {
        return CSS_RESOURCE_PATH + buildRequirementString(getCssRequirements());
    }

    public ArrayList getCssRequirements() {
        return cssRequirements;
    }

    public void addCssRequirement(String requirement) {
        this.cssRequirements.add(requirement);
    }

    public void addJsRequirement(String requirement) {
        this.jsRequirements.add(requirement);
    }

    public void setCssRequirements(ArrayList<String> cssRequirements) {
        this.cssRequirements = cssRequirements;
    }

    public String getJsRequirementsString() {
        return JS_RESOURCE_PATH + buildRequirementString(getJsRequirements());
    }

    public ArrayList getJsRequirements() {
        return jsRequirements;
    }

    protected void setJsRequirements(ArrayList<String> jsRequirements) {
        this.jsRequirements = jsRequirements;
    }
}
