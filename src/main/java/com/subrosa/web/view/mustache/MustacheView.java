package com.subrosa.web.view.mustache;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.sampullara.mustache.Mustache;
import com.sampullara.util.TemplateFunction;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.view.AbstractTemplateView;

import com.subrosa.web.view.il8n.LocaleDetectionFilter;

public class MustacheView extends AbstractTemplateView {
    private static final String CSS_RESOURCE_PATH = "static/css/";
    private static final String JS_RESOURCE_PATH = "static/js/";
    private static final String MUSTACHE_PATH = "";

    private Mustache template;
    private MessageSource messageSource;

    private String title = "Sub Rosa Games";
    private Set<String> cssRequirements = new HashSet<String>();
    private Set<String> jsRequirements = new HashSet<String>();
    private Set<Map<String, String>> clientTemplates = new HashSet<Map<String, String>>();

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, final HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {

        TemplateFunction translate = new TemplateFunction() {
            @Override
            public String apply(String input) {
                Locale locale = (Locale) request.getAttribute(LocaleDetectionFilter.REQUEST_LOCALE_ATTRIBUTE);
                return messageSource.getMessage(input, null, locale);
            }
        };

        TemplateFunction setTitle = new TemplateFunction() {
            @Override
            public String apply(String input) {
                setTitle(input);
                return null;
            }
        };

        TemplateFunction addCss = new TemplateFunction() {
            @Override
            public String apply(String input) {
                String requirements[] = input.split(",");
                for (String requirement : requirements) {
                    addCssRequirement(requirement.trim());
                }
                return null;
            }
        };

        TemplateFunction addJs = new TemplateFunction() {
            @Override
            public String apply(String input) {
                String requirements[] = input.split(",");
                for (String requirement : requirements) {
                    addJsRequirement(requirement.trim());
                }
                return null;
            }
        };

        TemplateFunction addClientTemplate = new TemplateFunction() {
            @Override
            public String apply(String input) {
                String requirements[] = input.split(",");
                addClientTemplate(requirements[0], requirements[1].trim());
                return null;
            }
        };
        //@TODO: figure out problem with css/js getters (for some reason this works with clientTemplates)
        model.put("i18n", translate);
        model.put("setTitle", setTitle);
        model.put("title", getTitle());
        model.put("addCss", addCss);
        model.put("css", getCssRequirements());
        model.put("addJs", addJs);
        model.put("js", getJsRequirements());
        model.put("addClientTemplate", addClientTemplate);
        model.put("clientTemplates", getClientTemplates());

        response.setContentType(getContentType());
        template.execute(response.getWriter(), model);
    }

    protected String buildRequirementString(Set<String> requirementSet) {
        StringBuilder sb = new StringBuilder();
        Iterator iterator = requirementSet.iterator();
        String extension = FilenameUtils.getExtension(iterator.next().toString());
        sb.append(StringUtils.join(Collections2.transform(requirementSet, new Function<String, String>() {
            @Override
            public String apply(String filename) {
                return FilenameUtils.removeExtension(filename);
            }
        }), ","));
        sb.append('.');
        sb.append(extension);
        return sb.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addCssRequirement(String requirement) {
        cssRequirements.add(requirement);
    }

    public String getCssRequirements() {
        String requirements = null;
        if (cssRequirements.size() > 0) {
            requirements = CSS_RESOURCE_PATH + buildRequirementString(cssRequirements);
        }
        return requirements;
    }

    public void addJsRequirement(String requirement) {
        jsRequirements.add(requirement);
    }

    public String getJsRequirements() {
        String requirements = null;
        if (jsRequirements.size() > 0) {
            return JS_RESOURCE_PATH + buildRequirementString(jsRequirements);
        }
        return requirements;
    }

    public void addClientTemplate(String id, String url) {
        Map<String, String> template = new HashMap<String, String>();
        template.put("id", id);
        template.put("url", url);
        clientTemplates.add(template);
    }

    public Set<Map<String, String>> getClientTemplates() {
        return clientTemplates;
    }

    public void setTemplate(Mustache template) {
        this.template = template;
    }

    public Mustache getTemplate() {
        return template;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
