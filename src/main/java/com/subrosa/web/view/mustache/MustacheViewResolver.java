/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.subrosa.web.view.mustache;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.mustache.MustacheTemplateLoader;

public class MustacheViewResolver extends AbstractTemplateViewResolver implements ViewResolver, InitializingBean {

    private MustacheTemplateLoader templateLoader;
    private Compiler compiler;

    private boolean standardsMode = false;
    private boolean escapeHTML = true;

    public MustacheViewResolver() {
        setViewClass(org.springframework.web.servlet.view.mustache.MustacheView.class);
    }

    @Override
    protected Class<?> requiredViewClass() {
        return org.springframework.web.servlet.view.mustache.MustacheView.class;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {

        final org.springframework.web.servlet.view.mustache.MustacheView view = (org.springframework.web.servlet.view.mustache.MustacheView) super.buildView(viewName);

        Template template = compiler.compile(templateLoader.getTemplate(view.getUrl()));
        view.setTemplate(template);

        return view;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        compiler = Mustache.compiler()
                .escapeHTML(escapeHTML)
                .standardsMode(standardsMode)
                .withLoader(templateLoader);
    }

    @Required
    public void setTemplateLoader(MustacheTemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    /**
     * Whether or not standards mode is enabled.
     * <p/>
     * disabled by default.
     */
    public void setStandardsMode(boolean standardsMode) {
        this.standardsMode = standardsMode;
    }

    /**
     * Whether or not HTML entities are escaped by default.
     * <p/>
     * default is true.
     */
    public void setEscapeHTML(boolean escapeHTML) {
        this.escapeHTML = escapeHTML;
    }

}
