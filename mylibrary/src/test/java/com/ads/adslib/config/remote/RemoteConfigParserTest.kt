package com.ads.adslib.config.remote

import com.ads.adslib.core.model.AdNetwork
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [RemoteConfigParser] — pure JSON → [AdsRemoteConfig] mapping.
 * Runs on the JVM using the real org.json from the test classpath.
 */
class RemoteConfigParserTest {

    private val validJson = """
        {
          "ads": {
            "enabled": true,
            "provider_priority": ["admob", "meta", "unity"],
            "providers": {
              "admob": {
                "enabled": true,
                "app_open": { "enabled": true, "ad_unit_id": "ao-1" },
                "banner": {
                  "home":   { "enabled": true,  "ad_unit_id": "b-home" },
                  "history":{ "enabled": false, "ad_unit_id": "b-hist" }
                },
                "interstitial": {
                  "enabled": true,
                  "frequency": 3,
                  "search": { "ad_unit_id": "i-search" }
                },
                "rewarded": {
                  "enabled": true,
                  "search": { "ad_unit_id": "r-search" }
                },
                "native": {
                  "detail": { "enabled": true, "ad_unit_id": "n-detail" }
                }
              },
              "unity": {
                "enabled": false,
                "game_id": "12345"
              }
            }
          }
        }
    """.trimIndent()

    @Test
    fun `valid json parses enabled and provider priority`() {
        val cfg = RemoteConfigParser.parse(validJson)
        assertTrue(cfg.enabled)
        assertEquals(listOf(AdNetwork.ADMOB, AdNetwork.META, AdNetwork.UNITY), cfg.providerPriority)
    }

    @Test
    fun `admob provider parses all formats`() {
        val admob = RemoteConfigParser.parse(validJson).providers[AdNetwork.ADMOB]!!
        assertTrue(admob.enabled)
        assertEquals("ao-1", admob.appOpen?.adId)
        assertEquals("b-home", admob.banner["home"]?.adId)
        assertTrue(admob.banner["home"]?.enabled == true)
        // per-screen disabled flag honoured
        assertFalse(admob.banner["history"]?.enabled == true)
        assertTrue(admob.interstitialEnabled)
        assertEquals(3, admob.interstitialFrequency)
        assertEquals("i-search", admob.interstitial["search"]?.adId)
        assertTrue(admob.rewardedEnabled)
        assertEquals("n-detail", admob.native["detail"]?.adId)
    }

    @Test
    fun `interstitial entries inherit the format level enabled flag`() {
        val admob = RemoteConfigParser.parse(validJson).providers[AdNetwork.ADMOB]!!
        // interstitial entries have no per-screen "enabled" → inherit format flag (true)
        assertTrue(admob.interstitial["search"]?.enabled == true)
    }

    @Test
    fun `unity game id parsed and unknown formats are empty`() {
        val unity = RemoteConfigParser.parse(validJson).providers[AdNetwork.UNITY]!!
        assertFalse(unity.enabled)
        assertEquals("12345", unity.gameId)
        assertTrue(unity.native.isEmpty())
        assertNull(unity.appOpen)
    }

    @Test
    fun `malformed json returns EMPTY`() {
        val cfg = RemoteConfigParser.parse("{ this is not valid json ")
        assertSame(AdsRemoteConfig.EMPTY, cfg)
    }

    @Test
    fun `missing ads section returns EMPTY`() {
        val cfg = RemoteConfigParser.parse("""{ "other": { "x": 1 } }""")
        assertSame(AdsRemoteConfig.EMPTY, cfg)
    }

    @Test
    fun `unknown provider names are dropped from priority`() {
        val json = """
            { "ads": { "enabled": true,
              "provider_priority": ["admob", "ironsource", "meta"],
              "providers": {} } }
        """.trimIndent()
        val cfg = RemoteConfigParser.parse(json)
        assertEquals(listOf(AdNetwork.ADMOB, AdNetwork.META), cfg.providerPriority)
    }

    @Test
    fun `interstitial frequency is coerced to at least one`() {
        val json = """
            { "ads": { "enabled": true, "provider_priority": ["admob"],
              "providers": { "admob": { "enabled": true,
                "interstitial": { "enabled": true, "frequency": 0,
                  "search": { "ad_unit_id": "i-1" } } } } } }
        """.trimIndent()
        val admob = RemoteConfigParser.parse(json).providers[AdNetwork.ADMOB]!!
        assertEquals(1, admob.interstitialFrequency)
    }

    @Test
    fun `meta placement_id is read when ad_unit_id absent`() {
        val json = """
            { "ads": { "enabled": true, "provider_priority": ["meta"],
              "providers": { "meta": { "enabled": true,
                "banner": { "home": { "enabled": true, "placement_id": "META_HOME" } } } } } }
        """.trimIndent()
        val meta = RemoteConfigParser.parse(json).providers[AdNetwork.META]!!
        assertEquals("META_HOME", meta.banner["home"]?.adId)
    }
}
