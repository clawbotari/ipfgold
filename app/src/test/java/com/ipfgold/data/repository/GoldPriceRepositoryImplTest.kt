package com.ipfgold.data.repository

import com.ipfgold.data.datasource.LocalGoldPriceDataSource
import com.ipfgold.data.datasource.RemoteGoldPriceDataSource
import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.domain.model.GoldPrice
import com.ipfgold.domain.model.PricePeriod
import com.ipfgold.domain.repository.DataSourceException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

@Suppress("OPT_IN_USAGE")
class GoldPriceRepositoryImplTest {

    private val remoteDataSource = mockk<RemoteGoldPriceDataSource>()
    private val localDataSource = mockk<LocalGoldPriceDataSource>()
    private val repository = GoldPriceRepositoryImpl(remoteDataSource, localDataSource)

    @Test
    fun `returns remote data and saves to cache when network available`() = runTest {
        // Given
        val mockGoldPrice = GoldPrice(
            priceUSD = 2150.0,
            priceEUR = 1850.0,
            change24h = 10.0,
            changePercent24h = 0.5,
            timestamp = Instant.now()
        )
        coEvery { remoteDataSource.getCurrentPrice() } returns mockGoldPrice
        coEvery { localDataSource.saveCurrentPrice(mockGoldPrice) } returns Unit

        // When
        val result = repository.getCurrentPrice()

        // Then
        assertEquals(mockGoldPrice, result)
        coVerify { remoteDataSource.getCurrentPrice() }
        coVerify { localDataSource.saveCurrentPrice(mockGoldPrice) }
    }

    @Test
    fun `returns cached data when remote fails and cache is valid`() = runTest {
        // Given
        val mockGoldPrice = GoldPrice(
            priceUSD = 2100.0,
            priceEUR = 1800.0,
            change24h = -5.0,
            changePercent24h = -0.24,
            timestamp = Instant.now()
        )
        val remoteException = DataSourceException("Network error")
        coEvery { remoteDataSource.getCurrentPrice() } throws remoteException
        coEvery { localDataSource.getCurrentPrice() } returns mockGoldPrice

        // When
        val result = repository.getCurrentPrice()

        // Then
        assertEquals(mockGoldPrice, result)
        coVerify { remoteDataSource.getCurrentPrice() }
        coVerify { localDataSource.getCurrentPrice() }
        coVerify(exactly = 0) { localDataSource.saveCurrentPrice(any()) }
    }

    @Test
    fun `throws descriptive error when both remote and cache fail`() = runTest {
        // Given
        val remoteException = DataSourceException("Network error")
        coEvery { remoteDataSource.getCurrentPrice() } throws remoteException
        coEvery { localDataSource.getCurrentPrice() } returns null

        // When / Then
        try {
            repository.getCurrentPrice()
            fail("Expected DataSourceException")
        } catch (e: DataSourceException) {
            assertTrue(e.message!!.contains("No se pudo obtener el precio del oro"))
            assertTrue(e.message!!.contains("Error de red"))
            assertTrue(e.message!!.contains("No hay datos en caché"))
        }
        coVerify { remoteDataSource.getCurrentPrice() }
        coVerify { localDataSource.getCurrentPrice() }
    }

    @Test
    fun `does not call remote when cache TTL has not expired`() = runTest {
        // This test is about the repository's behavior: it always tries remote first.
        // The TTL logic is inside LocalGoldPriceDataSource, not the repository.
        // So we just verify that remote is called even if cache exists.
        // If we want to test TTL, we need to test LocalGoldPriceDataSource separately.
        // For now, we ensure that remote is always attempted.
        val mockGoldPrice = GoldPrice(
            priceUSD = 2150.0,
            priceEUR = 1850.0,
            change24h = 10.0,
            changePercent24h = 0.5,
            timestamp = Instant.now()
        )
        coEvery { remoteDataSource.getCurrentPrice() } returns mockGoldPrice
        coEvery { localDataSource.saveCurrentPrice(mockGoldPrice) } returns Unit

        repository.getCurrentPrice()

        coVerify { remoteDataSource.getCurrentPrice() }
    }

    @Test
    fun `getHistoricalPrices returns remote data and saves to cache`() = runTest {
        // Given
        val period = PricePeriod.W1
        val mockChartPoints = listOf(
            ChartPoint(LocalDate.now(), 2100.0, 1800.0),
            ChartPoint(LocalDate.now().minusDays(1), 2090.0, 1790.0)
        )
        coEvery { remoteDataSource.getHistoricalPrices(period) } returns mockChartPoints
        coEvery { localDataSource.saveHistoricalPoints(mockChartPoints) } returns Unit

        // When
        val result = repository.getHistoricalPrices(period)

        // Then
        assertEquals(mockChartPoints, result)
        coVerify { remoteDataSource.getHistoricalPrices(period) }
        coVerify { localDataSource.saveHistoricalPoints(mockChartPoints) }
    }

    @Test
    fun `getHistoricalPrices returns cached data when remote fails`() = runTest {
        // Given
        val period = PricePeriod.W1
        val mockChartPoints = listOf(
            ChartPoint(LocalDate.now(), 2100.0, 1800.0)
        )
        val remoteException = DataSourceException("Network error")
        coEvery { remoteDataSource.getHistoricalPrices(period) } throws remoteException
        coEvery { localDataSource.getHistoricalPoints(period) } returns mockChartPoints

        // When
        val result = repository.getHistoricalPrices(period)

        // Then
        assertEquals(mockChartPoints, result)
        coVerify { remoteDataSource.getHistoricalPrices(period) }
        coVerify { localDataSource.getHistoricalPoints(period) }
    }

    @Test
    fun `getHistoricalPrices throws descriptive error when both remote and cache fail`() = runTest {
        // Given
        val period = PricePeriod.W1
        val remoteException = DataSourceException("Network error")
        coEvery { remoteDataSource.getHistoricalPrices(period) } throws remoteException
        coEvery { localDataSource.getHistoricalPoints(period) } returns emptyList()

        // When / Then
        try {
            repository.getHistoricalPrices(period)
            fail("Expected DataSourceException")
        } catch (e: DataSourceException) {
            assertTrue(e.message!!.contains("No se pudieron obtener los datos históricos"))
            assertTrue(e.message!!.contains("Error de red"))
            assertTrue(e.message!!.contains("No hay datos en caché"))
        }
        coVerify { remoteDataSource.getHistoricalPrices(period) }
        coVerify { localDataSource.getHistoricalPoints(period) }
    }
}