package com.example.f1live.news

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Path
import org.simpleframework.xml.Attribute

// 1. The Root <rss>
// RSS Data Classes
@Root(name = "rss", strict = false)
data class RssFeed(
    @field:Element(name = "channel", required = false)
    var channel: RssChannel? = null
)

@Root(name = "channel", strict = false)
data class RssChannel(
    @field:Element(name = "title", required = false)
    var title: String = "",

    @field:Element(name = "description", required = false)
    var description: String = "",

    // Use ElementList to handle multiple link elements
    @field:ElementList(entry = "link", inline = true, required = false)
    var links: List<String>? = null,

    @field:ElementList(entry = "item", inline = true, required = false)
    var articles: List<NewsArticle>? = null
)

@Root(name = "item", strict = false)
data class NewsArticle(
    @field:Element(name = "title", required = false)
    var title: String = "",

    @field:Element(name = "description", required = false)
    var description: String = "",

    @field:Element(name = "link", required = false)
    var link: String = "",

    @field:Element(name = "guid", required = false)
    var guid: String = "",

    @field:Element(name = "pubDate", required = false)
    var pubDate: String = "",

    // F1's feed uses enclosure for images
    @field:Element(name = "enclosure", required = false)
    var enclosure: Enclosure? = null
)

@Root(name = "enclosure", strict = false)
data class Enclosure(
    @field:Attribute(name = "url", required = false)
    var url: String = "",

    @field:Attribute(name = "type", required = false)
    var type: String = "",

    @field:Attribute(name = "length", required = false)
    var length: String = ""
)