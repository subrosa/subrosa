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

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.samskivert.mustache.Mustache.TemplateLoader;

public class MustacheTemplateLoader implements TemplateLoader, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    public Reader getTemplate(String filename) throws Exception {
        Resource resource = resourceLoader.getResource(filename);
        if (resource.exists()) {
            return new InputStreamReader(resource.getInputStream());
        }
        throw new FileNotFoundException(filename);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

}
