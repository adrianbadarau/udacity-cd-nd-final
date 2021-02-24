package com.adrianbadarau.klist.front_end.gateway

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.springframework.stereotype.Component

@Component
class TokenRelayFilter : ZuulFilter() {

    override fun run(): Any? {
        val ctx = RequestContext.getCurrentContext()
        @Suppress("UNCHECKED_CAST")
        val headers = ctx.get("ignoredHeaders") as MutableSet<String>
        // JWT tokens should be relayed to the resource servers
        headers.remove("authorization")
        return null
    }

    override fun shouldFilter() = true

    override fun filterType() = "pre"

    override fun filterOrder() = 10000
}
