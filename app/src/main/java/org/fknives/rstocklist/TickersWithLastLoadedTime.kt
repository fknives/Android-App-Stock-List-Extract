package org.fknives.rstocklist

data class TickersWithLastLoadedTime(val tickers: List<String>, val lastLoadedAt: Long)