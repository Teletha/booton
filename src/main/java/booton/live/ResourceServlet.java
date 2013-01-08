/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package booton.live;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kiss.I;
import kiss.XML;
import booton.translator.Javascript;
import booton.util.HTMLWriter;

/**
 * @version 2013/01/05 0:16:22
 */
@SuppressWarnings("serial")
public class ResourceServlet extends HttpServlet {

    /** The current root path. */
    private final Path root;

    /**
     * @param root
     */
    public ResourceServlet(Path root) {
        this.root = root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String path = request.getPathInfo();

        if (path == null) {
            path = "/index.html";
        }

        Path file = root.resolve(path.substring(1));

        if (path.endsWith(".html")) {
            rebuild(file).to(new HTMLWriter(new OutputStreamWriter(response.getOutputStream(), I.$encoding)));
        } else {
            I.copy(Files.newInputStream(file), response.getOutputStream(), true);
        }
    }

    /**
     * <p>
     * Rebuild html file.
     * </p>
     * 
     * @param file
     */
    private XML rebuild(Path file) {
        long now = new Date().getTime();
        XML html = I.xml(file);

        // ignore cache
        for (XML link : html.find("link[rel=stylesheet]")) {
            String href = link.attr("href");

            if (href.length() != 0 && !href.startsWith("http://") && !href.startsWith("htttps://")) {
                link.attr("href", href + "?" + now);
            }
        }

        for (XML link : html.find("script[src]")) {
            String src = link.attr("src");

            if (src.length() != 0 && !src.startsWith("http://") && !src.startsWith("htttps://")) {
                link.attr("src", src + "?" + now);
            }
        }

        // append live coding script
        html.find("body").child("script").attr("type", "text/javascript").attr("src", "live.js?" + now);
        Javascript.getScript(LiveCoding.class).writeTo(root.resolve("live.js"));

        return html;
    }
}
