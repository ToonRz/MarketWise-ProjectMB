package com.example.marketwiseproject.data.repository

import com.example.marketwiseproject.data.api.BinanceWebSocket
import com.example.marketwiseproject.data.api.CoinGeckoApi
import com.example.marketwiseproject.data.api.CoinGeckoMarket
import com.example.marketwiseproject.data.api.MarketChartResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CryptoRepositoryTest {

    private lateinit var mockCoinGeckoApi: CoinGeckoApi
    private lateinit var mockBinanceWebSocket: BinanceWebSocket
    private lateinit var cryptoRepository: CryptoRepository

    @Before
    fun setUp() {
        mockCoinGeckoApi = mockk()
        mockBinanceWebSocket = mockk()
        cryptoRepository = CryptoRepository(mockCoinGeckoApi, mockBinanceWebSocket)
    }

    // Test Case 11
    @Test
    fun `getTopCryptos returns mapped list when api succeeds`() = runTest {
        val mockMarkets = listOf(
            CoinGeckoMarket("bitcoin", "btc", "Bitcoin", 50000.0, 100.0, 0.2, 51000.0, 49000.0, 1000000.0, 1000000000.0)
        )
        coEvery { mockCoinGeckoApi.getMarkets(perPage = 50, currency = "usd", order = "market_cap_desc", page = 1) } returns mockMarkets

        val result = cryptoRepository.getTopCryptos()
        
        assertEquals(1, result.size)
        assertEquals("bitcoin", result[0].id)
        assertEquals("BTC", result[0].symbol) // Validating uppercase conversion
        assertEquals(50000.0, result[0].price, 0.0)
    }

    // Test Case 12
    @Test
    fun `getTopCryptos returns empty list when api throws exception`() = runTest {
        coEvery { mockCoinGeckoApi.getMarkets(any(), any(), any(), any()) } throws RuntimeException("API Error")

        val result = cryptoRepository.getTopCryptos()
        
        assertTrue(result.isEmpty())
    }

    // Test Case 13
    @Test
    fun `getTopCryptos handles multiple coins correctly`() = runTest {
        val mockMarkets = listOf(
            CoinGeckoMarket("bitcoin", "btc", "Bitcoin", 50000.0, 100.0, 0.2, 51000.0, 49000.0, 1000000.0, 1000000000.0),
            CoinGeckoMarket("ethereum", "eth", "Ethereum", 3000.0, 50.0, 1.5, 3100.0, 2900.0, 500000.0, 500000000.0)
        )
        coEvery { mockCoinGeckoApi.getMarkets(any(), any(), any(), any()) } returns mockMarkets

        val result = cryptoRepository.getTopCryptos()
        
        assertEquals(2, result.size)
        assertEquals("BTC", result[0].symbol)
        assertEquals("ETH", result[1].symbol)
    }

    // Test Case 14
    @Test
    fun `getTopCryptos maps all fields correctly`() = runTest {
        val mockMarkets = listOf(
            CoinGeckoMarket("bitcoin", "btc", "Bitcoin", 50000.0, 100.0, 0.2, 51000.0, 49000.0, 1000000.0, 1000000000.0)
        )
        coEvery { mockCoinGeckoApi.getMarkets(any(), any(), any(), any()) } returns mockMarkets

        val item = cryptoRepository.getTopCryptos()[0]
        
        assertEquals(100.0, item.change24h, 0.0)
        assertEquals(0.2, item.changePercent24h, 0.0)
        assertEquals(51000.0, item.high24h, 0.0)
        assertEquals(49000.0, item.low24h, 0.0)
        assertEquals(1000000.0, item.volume24h, 0.0)
        assertEquals(1000000000.0, item.marketCap, 0.0)
    }

    // Test Case 15
    @Test
    fun `getTopCryptos returns empty list when api returns empty list`() = runTest {
        coEvery { mockCoinGeckoApi.getMarkets(any(), any(), any(), any()) } returns emptyList()

        val result = cryptoRepository.getTopCryptos()
        
        assertTrue(result.isEmpty())
    }

    // Test Case 16
    @Test
    fun `getHistoricalPrices returns prices when api succeeds`() = runTest {
        val mockPrices = listOf(
            listOf(1620000000.0, 50000.0),
            listOf(1620086400.0, 51000.0)
        )
        val mockResponse = MarketChartResponse(mockPrices)
        coEvery { mockCoinGeckoApi.getMarketChart("bitcoin", days = 30, currency = "usd") } returns mockResponse

        val result = cryptoRepository.getHistoricalPrices("bitcoin")
        
        assertEquals(2, result.size)
        assertEquals(1620000000.0, result[0][0], 0.0)
        assertEquals(50000.0, result[0][1], 0.0)
    }

    // Test Case 17
    @Test
    fun `getHistoricalPrices returns empty list when api throws exception`() = runTest {
        coEvery { mockCoinGeckoApi.getMarketChart(any(), any(), any()) } throws RuntimeException("Network Error")

        val result = cryptoRepository.getHistoricalPrices("bitcoin")
        
        assertTrue(result.isEmpty())
    }

    // Test Case 18
    @Test
    fun `getHistoricalPrices correctly passes days parameter`() = runTest {
        val mockResponse = MarketChartResponse(emptyList())
        coEvery { mockCoinGeckoApi.getMarketChart("ethereum", days = 7, currency = "usd") } returns mockResponse

        val result = cryptoRepository.getHistoricalPrices("ethereum", 7)
        
        assertTrue(result.isEmpty())
    }

    // Test Case 19
    @Test
    fun `getRealTimePrice calls connectPriceStream from WebSocket`() {
        val symbol = "BTCUSDT"
        val mockFlow = flowOf(50000.0)
        every { mockBinanceWebSocket.connectPriceStream(symbol) } returns mockFlow

        val resultFlow = cryptoRepository.getRealTimePrice(symbol)

        assertEquals(mockFlow, resultFlow)
        verify { mockBinanceWebSocket.connectPriceStream(symbol) }
    }

    // Test Case 20
    @Test
    fun `disconnect calls disconnect from WebSocket`() {
        every { mockBinanceWebSocket.disconnect() } returns Unit

        cryptoRepository.disconnect()

        verify { mockBinanceWebSocket.disconnect() }
    }
}
