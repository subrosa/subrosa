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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.springframework.context.MessageSource;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheBuilder;

/**
 * View resolver for mustache templates.
 */
public class MustacheViewResolver extends AbstractTemplateViewResolver implements ViewResolver, ResourceLoaderAware {

    private ResourceLoader resourceLoader;
    private MessageSource messageSource;

    /**
     * Creates a new mustache view resolver.
     */
    public MustacheViewResolver() {
        setViewClass(MustacheView.class);
    }

    @Override
    protected Class<?> requiredViewClass() {
        return MustacheView.class;
    }

    @Override
    protected MustacheView buildView(String viewName) throws Exception {
        //@TODO determine view class from name
        this.setViewClass(GameMustacheView.class);

        MustacheView view = (MustacheView) super.buildView(viewName);
        view.setMessageSource(messageSource);
        Resource resource = resourceLoader.getResource(view.getUrl());
        MustacheBuilder compiler = new MustacheBuilder(resource.getFile().getParentFile());
        compiler.setSuperclass(MustacheTemplate.class.getName());

        if (resource.exists()) {
            String templateName = resource.getFile().getParentFile().getPath();
            Mustache template = compiler.build(new BufferedReader(new InputStreamReader(resource.getInputStream())), templateName);
            template.setRoot(resource.getFile().getParentFile());
            view.setTemplate(template);
        } else {
            throw new FileNotFoundException(viewName);
        }

        return view;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}