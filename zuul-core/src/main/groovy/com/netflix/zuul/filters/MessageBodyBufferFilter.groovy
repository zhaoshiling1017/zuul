package com.netflix.zuul.filters

import com.netflix.config.DynamicIntProperty
import com.netflix.config.DynamicPropertyFactory
import com.netflix.zuul.bytebuf.ByteBufUtils
import com.netflix.zuul.context.ZuulMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rx.Observable

/**
 * User: michaels@netflix.com
 * Date: 5/28/15
 * Time: 10:54 AM
 */
public abstract class MessageBodyBufferFilter extends BaseFilter<ZuulMessage, ZuulMessage>
{
    private static final Logger LOG = LoggerFactory.getLogger(MessageBodyBufferFilter.class);

    private static final DynamicIntProperty MAX_BODY_SIZE_PROP = DynamicPropertyFactory.getInstance().getIntProperty(
            "zuul.MessageBodyBufferFilter.max.size", 25 * 1000 * 1024);

    @Override
    Observable<ZuulMessage> applyAsync(ZuulMessage msg)
    {
        final int maxBodySize = MAX_BODY_SIZE_PROP.get();

        return ByteBufUtils.aggregate(msg.getBodyStream(), maxBodySize)
                .map({bb ->
                    // Set the body on Response object.
                    byte[] body = ByteBufUtils.toBytes(bb);
                    msg.setBody(body);
                    return msg;
                });
    }

    @Override
    boolean shouldFilter(ZuulMessage msg) {
        return true
    }

    @Override
    int filterOrder() {
        return -10
    }
}