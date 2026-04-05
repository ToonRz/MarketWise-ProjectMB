package com.example.marketwiseproject.data.repository

import com.example.marketwiseproject.data.api.FinnhubApi
import com.example.marketwiseproject.data.api.FinnhubBasicFinancials
import com.example.marketwiseproject.data.api.FinnhubCandles
import com.example.marketwiseproject.data.api.FinnhubProfile
import com.example.marketwiseproject.data.api.FinnhubQuote
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StockRepositoryTest {

    private lateinit var mockFinnhubApi: FinnhubApi
    private lateinit var stockRepository: StockRepository

    @Before
    fun setUp() {
        mockFinnhubApi = mockk()
        stockRepository = StockRepository(mockFinnhubApi)
    }

    // Test Case 1
    @Test
    fun `getStockDetails returns success when API calls are successful`() = runTest {
        val symbol = "AAPL"
        
        val mockQuote = FinnhubQuote(150.0, 2.5, 1.6, 151.0, 148.0, 149.0, 147.5)
        val mockProfile = FinnhubProfile("Apple Inc.", "AAPL", "logoUrl", 2500000.0, 16000.0)
        val mockCandles = FinnhubCandles(listOf(150.0), listOf(151.0), listOf(148.0), listOf(149.0), listOf(100L), listOf(1620L), "ok")
        val mockFinancials = FinnhubBasicFinancials(mapOf("peNormalizedAnnual" to 28.5))

        coEvery { mockFinnhubApi.getQuote(symbol, any()) } returns mockQuote
        coEvery { mockFinnhubApi.getProfile(symbol, any()) } returns mockProfile
        coEvery { mockFinnhubApi.getStockCandles(symbol, "D", any(), any(), any()) } returns mockCandles
        coEvery { mockFinnhubApi.getBasicFinancials(symbol, "all", any()) } returns mockFinancials

        val result = stockRepository.getStockDetails(symbol)
        
        assertTrue(result.isSuccess)
        val details = result.getOrNull()!!
        assertEquals("AAPL", details.symbol)
        assertEquals("Apple Inc.", details.name)
        assertEquals(150.0, details.currentPrice, 0.0)
    }

    // Test Case 2
    @Test
    fun `getStockDetails maps fallback symbol when profile ticker is null`() = runTest {
        val symbol = "UNKNOWN"
        val mockQuote = FinnhubQuote(10.0, null, null, null, null, null, null)
        val mockProfile = FinnhubProfile("Unknown Corp", null, null, null, null)
        val mockCandles = FinnhubCandles(null, null, null, null, null, null, "ok")
        val mockFinancials = FinnhubBasicFinancials(null)

        coEvery { mockFinnhubApi.getQuote(any(), any()) } returns mockQuote
        coEvery { mockFinnhubApi.getProfile(any(), any()) } returns mockProfile
        coEvery { mockFinnhubApi.getStockCandles(any(), any(), any(), any(), any()) } returns mockCandles
        coEvery { mockFinnhubApi.getBasicFinancials(any(), any(), any()) } returns mockFinancials

        val details = stockRepository.getStockDetails(symbol).getOrNull()!!
        assertEquals("UNKNOWN", details.symbol) 
    }

    // Test Case 3
    @Test
    fun `getStockDetails maps default name when profile name is null`() = runTest {
        val symbol = "TEST"
        coEvery { mockFinnhubApi.getQuote(any(), any()) } returns FinnhubQuote(10.0, null, null, null, null, null, null)
        coEvery { mockFinnhubApi.getProfile(any(), any()) } returns FinnhubProfile(null, "TEST", null, null, null)
        coEvery { mockFinnhubApi.getStockCandles(any(), any(), any(), any(), any()) } returns FinnhubCandles(null, null, null, null, null, null, "ok")
        coEvery { mockFinnhubApi.getBasicFinancials(any(), any(), any()) } returns FinnhubBasicFinancials(null)

        val details = stockRepository.getStockDetails(symbol).getOrNull()!!
        assertEquals("N/A", details.name)
    }

    // Test Case 4
    @Test
    fun `getStockDetails returns failure when getQuote throws exception`() = runTest {
        val symbol = "AAPL"
        val exception = RuntimeException("Network error")
        coEvery { mockFinnhubApi.getQuote(any(), any()) } throws exception

        val result = stockRepository.getStockDetails(symbol)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // Test Case 5
    @Test
    fun `getStockDetails returns failure when getProfile throws exception`() = runTest {
        val symbol = "AAPL"
        coEvery { mockFinnhubApi.getQuote(any(), any()) } returns FinnhubQuote(100.0, 1.0, 1.0, 101.0, 99.0, 100.0, 99.0)
        coEvery { mockFinnhubApi.getProfile(any(), any()) } throws RuntimeException("Profile Error")

        val result = stockRepository.getStockDetails(symbol)
        assertTrue(result.isFailure)
        assertEquals("Profile Error", result.exceptionOrNull()?.message)
    }

    // Test Case 6
    @Test
    fun `getStockDetails extracts peRatio correctly`() = runTest {
        val symbol = "TSLA"
        coEvery { mockFinnhubApi.getQuote(any(), any()) } returns FinnhubQuote(10.0, null, null, null, null, null, null)
        coEvery { mockFinnhubApi.getProfile(any(), any()) } returns FinnhubProfile(null, null, null, null, null)
        coEvery { mockFinnhubApi.getStockCandles(any(), any(), any(), any(), any()) } returns FinnhubCandles(null, null, null, null, null, null, "ok")
        coEvery { mockFinnhubApi.getBasicFinancials(any(), any(), any()) } returns FinnhubBasicFinancials(mapOf("peNormalizedAnnual" to 55.5))

        val details = stockRepository.getStockDetails(symbol).getOrNull()!!
        assertEquals(55.5, details.peRatio)
    }

    // Test Case 7
    @Test
    fun `getStockDetails falls back to 0 when quote fields are null`() = runTest {
        val symbol = "TSLA"
        coEvery { mockFinnhubApi.getQuote(any(), any()) } returns FinnhubQuote(null, null, null, null, null, null, null)
        coEvery { mockFinnhubApi.getProfile(any(), any()) } returns FinnhubProfile(null, null, null, null, null)
        coEvery { mockFinnhubApi.getStockCandles(any(), any(), any(), any(), any()) } returns FinnhubCandles(null, null, null, null, null, null, "ok")
        coEvery { mockFinnhubApi.getBasicFinancials(any(), any(), any()) } returns FinnhubBasicFinancials(null)

        val details = stockRepository.getStockDetails(symbol).getOrNull()!!
        assertEquals(0.0, details.currentPrice, 0.0)
        assertEquals(0.0, details.change, 0.0)
        assertEquals(0.0, details.percentChange, 0.0)
    }

    // Test Case 8
    @Test
    fun `getStockDetails uses last volume from candles`() = runTest {
        val symbol = "AAPL"
        coEvery { mockFinnhubApi.getQuote(any(), any()) } returns FinnhubQuote(10.0, null, null, null, null, null, null)
        coEvery { mockFinnhubApi.getProfile(any(), any()) } returns FinnhubProfile(null, null, null, null, null)
        
        val volumes = listOf(100L, 200L, 300L)
        coEvery { mockFinnhubApi.getStockCandles(any(), any(), any(), any(), any()) } returns FinnhubCandles(null, null, null, null, volumes, null, "ok")
        coEvery { mockFinnhubApi.getBasicFinancials(any(), any(), any()) } returns FinnhubBasicFinancials(null)

        val details = stockRepository.getStockDetails(symbol).getOrNull()!!
        assertEquals(300L, details.volume)
    }

    // Test Case 9
    @Test
    fun `getStockDetails volume is null when candles volumes is empty`() = runTest {
        val symbol = "AAPL"
        coEvery { mockFinnhubApi.getQuote(any(), any()) } returns FinnhubQuote(10.0, null, null, null, null, null, null)
        coEvery { mockFinnhubApi.getProfile(any(), any()) } returns FinnhubProfile(null, null, null, null, null)
        coEvery { mockFinnhubApi.getStockCandles(any(), any(), any(), any(), any()) } returns FinnhubCandles(null, null, null, null, emptyList(), null, "ok")
        coEvery { mockFinnhubApi.getBasicFinancials(any(), any(), any()) } returns FinnhubBasicFinancials(null)

        val details = stockRepository.getStockDetails(symbol).getOrNull()!!
        assertNull(details.volume)
    }

    // Test Case 10
    @Test
    fun `getStockDetails peRatio is null when financials metric is null`() = runTest {
        val symbol = "AAPL"
        coEvery { mockFinnhubApi.getQuote(any(), any()) } returns FinnhubQuote(10.0, null, null, null, null, null, null)
        coEvery { mockFinnhubApi.getProfile(any(), any()) } returns FinnhubProfile(null, null, null, null, null)
        coEvery { mockFinnhubApi.getStockCandles(any(), any(), any(), any(), any()) } returns FinnhubCandles(null, null, null, null, null, null, "ok")
        coEvery { mockFinnhubApi.getBasicFinancials(any(), any(), any()) } returns FinnhubBasicFinancials(null)

        val details = stockRepository.getStockDetails(symbol).getOrNull()!!
        assertNull(details.peRatio)
    }
}
