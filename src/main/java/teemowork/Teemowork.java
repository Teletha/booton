/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package teemowork;

import static booton.translator.web.WebSupport.*;

import java.util.Collection;

import js.ui.Application;
import js.ui.ImageGrid;
import teemowork.model.Champion;
import booton.View;
import booton.css.CSS;
import booton.css.FontFamily;
import booton.css.Value;
import booton.translator.web.jQuery;

/**
 * @version 2012/12/11 14:23:57
 */
public class Teemowork extends Application {

    /** The champion viewer. */
    private ImageGrid champions = new ImageGrid<Champion>() {

        @Override
        protected Collection<Champion> sources() {
            return Champion.getAll();
        }

        @Override
        protected String name(Champion source) {
            return source.name;
        }

        @Override
        protected String uri(Champion source) {
            return "src/main/resources/teemowork/icon/" + source.getSystemName() + ".png";
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    protected void jsmain() {
        // create navigation
        // body.append("<div class='navbar'><div class='navbar-inner'><a class='brand' href='#'>Title</a> <ul class='nav'><li class='active'><a href='#''>Home</a></li><li><a href='#''>Link</a></li> <li><a href='#''>Link</a></li> </ul></div></div>");

        $("body").css("padding", "150px");

        jQuery root = $("body");

        for (Champion champion : Champion.getAll()) {
            String uri = "src/main/resources/teemowork/icon/" + champion.getSystemName() + ".png";

            root.child(MyCSS.class).css("background-image", "url(" + uri + ")").child("span").text(champion.name);
        }
    }

    /**
     * @version 2012/12/14 10:43:46
     */
    private static class HTML extends View {

        /**
         * {@inheritDoc}
         */
        @Override
        protected void buildView(jQuery root) {

            for (Champion champion : Champion.getAll()) {
                String uri = "src/main/resources/teemowork/icon/" + champion.getSystemName() + ".png";

                root.child("span").css("background-image", "url(" + uri + ")");
                // jQuery item = $("<span><icon><span>" + champion.name + "</span></icon></span>");
                // item.appendTo("body");
                // item.children("icon").css("background-image", "url(" + uri + ")");
            }
        }
    }

    /**
     * @version 2012/12/10 16:59:19
     */
    private static class MyCSS extends CSS {

        private FontFamily Yanone = new FontFamily("Yanone Kaffeesatz", "http://fonts.googleapis.com/css?family=Yanone+Kaffeesatz");

        {
            display.inlineBlock();
            box.size(60, px);
            outline.none();
            background.transparent().noRepeat().top().left().contain();
            position.relative();

            cover();

            while (rule("span")) {
                font.family(Yanone).weight.bold().size(18, px);
                lineHeight.size(20, px);
                padding.size(5, px);
                textShadow.add(1, px, 1, px, 1, px, rgba(0, 0, 0, 0.1));
                textAlign.center();
                background.rgba(255, 255, 255, 0.6);
                borderRadius.size(5, px);
                pointerEvents.none();
                position.bottom(100, px);
                boxShadow.offset(1, px, 1, px).blurRadius(2, px).rgba(0, 0, 0, 0.1);
                opacity.alpha(0);
                transition.property.all().duration(0.2, s).timing.easeInOut().delay(0.15, s);

                bubble(100, 4, 10);
            }

            while (rule(":hover span")) {
                opacity.alpha(0.9);
                position.bottom(70, px);
            }
        }

        /**
         * <p>
         * Apply bubble border box style.
         * </p>
         * 
         * @param boxWidth
         * @param borderWidth
         * @param bubbleWidth
         */
        private void bubble(double boxWidth, double borderWidth, double bubbleWidth) {
            position.absolute().left(50, percent);
            box.width(boxWidth, px);
            margin.left(-boxWidth / 2, px);
            border.width(borderWidth, px).solid().rgb(5, 5, 5);

            while (rule(("::before"))) {
                display.block();
                box.width(0, px).height(0, px);
                content.value("");
                position.absolute()
                        .bottom(-bubbleWidth - borderWidth - bubbleWidth, px)
                        .left(boxWidth / 2 - borderWidth - bubbleWidth, px);
                border.width(bubbleWidth, px).solid().transparent();
                borderTop.black().width(bubbleWidth, px).solid();
            }
        }

        /**
         * Apply screen cover.
         */
        private void cover() {
            Value width = box.width();
            Value height = box.height();

            while (rule("::after")) {
                content.value("");
                display.block();
                position.absolute();
                box.width(width.size, width.unit).height(height.size, height.unit);
                background.white();
                opacity.alpha(0.15);
            }

            while (rule(":hover::after")) {
                opacity.alpha(0);
            }
        }
    }
}
