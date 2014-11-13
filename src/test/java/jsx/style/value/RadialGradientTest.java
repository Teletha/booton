/*
 * Copyright (C) 2014 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package jsx.style.value;

import static booton.css.Unit.*;
import jsx.style.Style;
import jsx.style.StyleDeclarationTestBase;
import jsx.style.StyleDescriptor;
import jsx.style.property.Background.BackgroundImage;

import org.junit.Test;

/**
 * @version 2014/11/13 10:51:41
 */
public class RadialGradientTest extends StyleDeclarationTestBase {

    private static final Color black = Color.Black;

    private static final Color white = Color.White;

    private static final Numeric one = new Numeric(10, px);

    private static final Numeric two = new Numeric(20, em);

    private static final Position pos = new Position(one, two);

    @Test
    public void base() throws Exception {
        ValidatableStyleRule parsed = parse(MyStyle.base).rule();
        assert parsed.property("background-image", "radial-gradient(black,white)");
        assert parsed.property("background-image", "-webkit-radial-gradient(black,white)");
    }

    @Test
    public void colors() throws Exception {
        ValidatableStyleRule parsed = parse(MyStyle.colors).rule();

        assert parsed.property("background-image", "radial-gradient(black,white,black)");
        assert parsed.property("background-image", "-webkit-radial-gradient(black,white,black)");
    }

    @Test
    public void percentage() throws Exception {
        ValidatableStyleRule parsed = parse(MyStyle.percentage).rule();

        assert parsed.property("background-image", "radial-gradient(black 10%,white 90%)");
        assert parsed.property("background-image", "-webkit-radial-gradient(black 10%,white 90%)");
    }

    @Test
    public void length() throws Exception {
        ValidatableStyleRule parsed = parse(MyStyle.length).rule();

        assert parsed.property("background-image", "radial-gradient(black 10px,white 20em)");
        assert parsed.property("background-image", "-webkit-radial-gradient(black 10px,white 20em)");
    }

    @Test
    public void repeat() throws Exception {
        ValidatableStyleRule parsed = parse(MyStyle.repeat).rule();

        assert parsed.property("background-image", "repeating-radial-gradient(black,white)");
        assert parsed.property("background-image", "-webkit-repeating-radial-gradient(black,white)");
    }

    @Test
    public void position() throws Exception {
        ValidatableStyleRule parsed = parse(MyStyle.position).rule();

        assert parsed.property("background-image", "radial-gradient(at 10px 20em,black,white)");
        assert parsed.property("background-image", "-webkit-radial-gradient(10px 20em,black,white)");
    }

    /**
     * @version 2014/11/13 9:53:57
     */
    private static class MyStyle extends StyleDescriptor {

        private static Style base = () -> {
            background.image(BackgroundImage.of(new RadialGradient().color(black, white)));
        };

        private static Style colors = () -> {
            background.image(BackgroundImage.of(new RadialGradient().color(black, white, black)));
        };

        private static Style percentage = () -> {
            background.image(BackgroundImage.of(new RadialGradient().color(black, 10).color(white, 90)));
        };

        private static Style length = () -> {
            background.image(BackgroundImage.of(new RadialGradient().color(black, one).color(white, two)));
        };

        private static Style repeat = () -> {
            background.image(BackgroundImage.of(new RadialGradient().repeat().color(black, white)));
        };

        private static Style position = () -> {
            background.image(BackgroundImage.of(new RadialGradient().position(pos).color(black, white)));
        };
    }
}
