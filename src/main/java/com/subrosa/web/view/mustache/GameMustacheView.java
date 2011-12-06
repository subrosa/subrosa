package com.subrosa.web.view.mustache;

/**
 * Used for Game based mustache views.
 */
public class GameMustacheView extends MustacheView {

    public GameMustacheView() {
        //@TODO: update this with actual requirements
        this.addCssRequirement("960-grid.css");
        this.addJsRequirement("ga.js");
    }
}
