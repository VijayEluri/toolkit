package com.trailmagic.image.ui;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.http.HttpServletRequest;

import com.trailmagic.user.*;
import com.trailmagic.image.*;

public class ImageFrameLinkTag extends TagSupport {
    private ImageFrame m_frame;
    

    private static final String USER_ATTR = "user";

    public int doStartTag() throws JspException {
        StringBuffer html = new StringBuffer();

        try {
            html.append("<a href=\"");

            //XXX: yeek?
            WebApplicationContext ctx =
                WebApplicationContextUtils
                .getRequiredWebApplicationContext(pageContext
                                                  .getServletContext());
            LinkHelper helper =
                (LinkHelper)ctx.getBean("linkHelper");
            // XXX: check for null bean
            // XXX: evil cast?
            helper.setRequest((HttpServletRequest)pageContext.getRequest());
                //                new LinkHelper((HttpServletRequest)pageContext.getRequest());

            html.append(helper.getImageGroupFrameUrl(m_frame));
            html.append("\">");
            pageContext.getOut().write(html.toString());
            return EVAL_BODY_INCLUDE;
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().write("</a>");
            return EVAL_PAGE;
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    public void setFrame(ImageFrame frame) {
        m_frame = frame;
    }

    public ImageFrame getFrame() {
        return m_frame;
    }
}
