package com.subrosa.web.view.mustache;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.MustacheTrace;

/**
 * Template that overrides partial lookup.
 */
public class MustacheTemplate extends Mustache {

    /**
     * Compiles a partial include a Mustache.
     * 
     * @param name the name/path of the partial to compile
     * @return a compiled template based on the partial name
     * @throws MustacheException if there is a problem compiling the partial
     */
    @Override
    protected Mustache compilePartial(String name) throws MustacheException {
        MustacheTrace.Event event = null;
        if (trace) {
            event = MustacheTrace.addEvent("compile partial: " + name, getRoot() == null ? "classpath" : getRoot().getName());
        }
        Mustache mustache = mj.parseFile(name);
        mustache.setMustacheJava(mj);
        mustache.setRoot(getRoot());
        if (trace) {
            event.end();
        }
        return mustache;
    }
}
