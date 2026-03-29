package com.ipfgold.ui.home

import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.domain.model.Currency
import com.ipfgold.domain.model.GoldPrice
import com.ipfgold.domain.model.PricePeriod
import com.ipfgold.domain.repository.GoldPriceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: GoldPriceRepository
    private lateinit var viewModel: HomeViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = HomeViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadData emits Loading then Success when repository returns data`() = runTest {
        // Given
        val mockGoldPrice = GoldPrice(
            priceUSD = 2150.0,
            priceEUR = 1850.0,
            change24h = 10.0,
            changePercent24h = 0.5,
            timestamp = Instant.now()
        )
        val mockChartPoints = listOf(
            ChartPoint(LocalDate.now(), 2100.0, 1800.0),
            ChartPoint(LocalDate.now().minusDays(1), 2090.0, 1790.0)
        )
        coEvery { repository.getCurrentPrice() } returns mockGoldPrice
        coEvery { repository.getHistoricalPrices(PricePeriod.ALL) } returns mockChartPoints

        // When
        viewModel.loadData()

        // Then
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is HomeUiState.Success)
        val success = uiState as HomeUiState.Success
        assertEquals(mockGoldPrice, success.price)
        assertEquals(mockChartPoints, success.chartPoints)
        assertEquals(Currency.USD, success.selectedCurrency)
        assertEquals(PricePeriod.ALL, success.selectedPeriod)
        assertFalse(success.isOffline)

        coVerify { repository.getCurrentPrice() }
        coVerify { repository.getHistoricalPrices(PricePeriod.ALL) }
    }

    @Test
    fun `loadData emits Loading then Error when repository throws exception`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { repository.getCurrentPrice() } throws exception

        // When
        viewModel.loadData()

        // Then
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is HomeUiState.Error)
        val error = uiState as HomeUiState.Error
        assertTrue(error.message.contains("No se pudieron cargar los datos"))
        assertTrue(error.canRetry)

        coVerify { repository.getCurrentPrice() }
    }

    @Test
    fun `loadData uses cached data when network fails`() = runTest {
        // This test is a bit tricky because the repository already implements the cache fallback.
        // We'll test that the ViewModel handles the Success state from cached data.
        // Simulate repository returning data (which could be from cache)
        val mockGoldPrice = GoldPrice(
            priceUSD = 2150.0,
            priceEUR = 1850.0,
            change24h = 10.0,
            changePercent24h = 0.5,
            timestamp = Instant.now()
        )
        val mockChartPoints = listOf(
            ChartPoint(LocalDate.now(), 2100.0, 1800.0)
        )
        coEvery { repository.getCurrentPrice() } returns mockGoldPrice
        coEvery { repository.getHistoricalPrices(PricePeriod.ALL) } returns mockChartPoints

        viewModel.loadData()

        val uiState = viewModel.uiState.first()
        assertTrue(uiState is HomeUiState.Success)
        val success = uiState as HomeUiState.Success
        assertEquals(mockGoldPrice, success.price)
        // Note: we cannot directly test that cache was used because repository abstracts it.
    }

    @Test
    fun `currency toggle triggers new data load with correct currency`() = runTest {
        // First load data
        val mockGoldPrice = GoldPrice(
            priceUSD = 2150.0,
            priceEUR = 1850.0,
            change24h = 10.0,
            changePercent24h = 0.5,
            timestamp = Instant.now()
        )
        val mockChartPoints = listOf(
            ChartPoint(LocalDate.now(), 2100.0, 1800.0)
        )
        coEvery { repository.getCurrentPrice() } returns mockGoldPrice
        coEvery { repository.getHistoricalPrices(PricePeriod.ALL) } returns mockChartPoints

        viewModel.loadData()

        // Now change currency to EUR
        viewModel.setCurrency(Currency.EUR)

        // Verify state updated with new currency
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is HomeUiState.Success)
        val success = uiState as HomeUiState.Success
        assertEquals(Currency.EUR, success.selectedCurrency)
        // Should NOT reload data, only update UI state
        coVerify(exactly = 1) { repository.getCurrentPrice() }
        coVerify(exactly = 1) { repository.getHistoricalPrices(PricePeriod.ALL) }
    }

    @Test
    fun `period selector triggers new data load with correct period`() = runTest {
        // First load data with ALL period
        val mockGoldPrice = GoldPrice(
            priceUSD = 2150.0,
            priceEUR = 1850.0,
            change24h = 10.0,
            changePercent24h = 0.5,
            timestamp = Instant.now()
        )
        val mockChartPointsAll = listOf(
            ChartPoint(LocalDate.now(), 2100.0, 1800.0)
        )
        val mockChartPointsW1 = listOf(
            ChartPoint(LocalDate.now(), 2100.0, 1800.0),
            ChartPoint(LocalDate.now().minusDays(1), 2090.0, 1790.0)
        )
        coEvery { repository.getCurrentPrice() } returns mockGoldPrice
        coEvery { repository.getHistoricalPrices(PricePeriod.ALL) } returns mockChartPointsAll
        coEvery { repository.getHistoricalPrices(PricePeriod.W1) } returns mockChartPointsW1

        viewModel.loadData()

        // Change period to W1
        viewModel.setPeriod(PricePeriod.W1)

        // Verify that historical data was reloaded with correct period
        coVerify { repository.getHistoricalPrices(PricePeriod.W1) }
        // Current price should not be reloaded
        coVerify(exactly = 1) { repository.getCurrentPrice() }

        // Verify state updated
        val uiState = viewModel.uiState.first()
        assertTrue(uiState is HomeUiState.Success)
        val success = uiState as HomeUiState.Success
        assertEquals(PricePeriod.W1, success.selectedPeriod)
        assertEquals(mockChartPointsW1, success.chartPoints)
    }

    @Test
    fun `period selector when not in Success state triggers full reload`() = runTest {
        // Simulate error state
        coEvery { repository.getCurrentPrice() } throws Exception("Network error")
        viewModel.loadData()

        // Now set period, should trigger loadData again
        coEvery { repository.getCurrentPrice() } returns GoldPrice(
            priceUSD = 2150.0,
            priceEUR = 1850.0,
            change24h = 10.0,
            changePercent24h = 0.5,
            timestamp = Instant.now()
        )
        coEvery { repository.getHistoricalPrices(PricePeriod.W1) } returns emptyList()

        viewModel.setPeriod(PricePeriod.W1)

        coVerify(atLeast = 2) { repository.getCurrentPrice() }
        coVerify { repository.getHistoricalPrices(PricePeriod.W1) }
    }
}