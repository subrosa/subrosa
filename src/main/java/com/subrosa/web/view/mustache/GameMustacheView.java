package com.subrosa.web.view.mustache;

/**
 * Used for Game based mustache views.
 */
public class GameMustacheView extends MustacheView {

    public GameMustacheView() {
        //@TODO: update this with actual requirements
        addCssRequirement("960-grid.css");
        addJsRequirement("ga.js");
    }
}
