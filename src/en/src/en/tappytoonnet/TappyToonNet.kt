package eu.kanade.tachiyomi.extension.en.tappytoonnet

import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class TappyToonNet : ParsedHttpSource() {

    override val name = "TappyToon (Free only)"
    override val baseUrl = "https://www.tappytoon.net"
    override val lang = "en"
    override val supportsLatest = true

    // Popular manga list (basic placeholder)
    override fun popularMangaRequest(page: Int): Request =
        GET("$baseUrl/en/comics?page=$page")

    override fun popularMangaSelector() = "div.comic-item"
    override fun popularMangaFromElement(element: Element): SManga {
        val manga = SManga.create()
        manga.title = element.select("h3").text()
        manga.setUrlWithoutDomain(element.select("a").attr("href"))
        manga.thumbnail_url = element.select("img").attr("src")
        return manga
    }
    override fun popularMangaNextPageSelector() = "a.next"

    // Chapter list
    override fun chapterListSelector() = "li.chapter-item"

    override fun chapterFromElement(element: Element): SChapter {
        val chapter = SChapter.create()
        chapter.name = element.select("span.title").text()
        chapter.url = element.select("a").attr("href")

        if (element.hasClass("locked")) {
            chapter.scanlator = "🔒 Locked – available only on web"
        }

        return chapter
    }

    override fun chapterListRequest(manga: SManga): Request {
        return GET("$baseUrl${manga.url}")
    }

    // Page list (images in
